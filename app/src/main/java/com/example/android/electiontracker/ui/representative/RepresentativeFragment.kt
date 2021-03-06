package com.example.android.electiontracker.ui.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.android.electiontracker.BuildConfig
import com.example.android.electiontracker.R
import com.example.android.electiontracker.databinding.FragmentRepresentativeBinding
import com.example.android.electiontracker.network.models.Address
import com.example.android.electiontracker.ui.election.ElectionsViewModel
import com.example.android.electiontracker.ui.representative.RepresentativeViewModel.Companion.MOTION_LAYOUT_STATE_KEY
import com.example.android.politicalpreparedness.ui.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.snackbar.Snackbar
import java.util.*

class RepresentativeFragment : Fragment() {

    companion object {
        const val TAG = "RepresentativeFragment"
        private const val REQUEST_LOCATION_PERMISSION = 99
        private const val LOCATION_REQUEST_CODE = 98
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var listAdapter: RepresentativeListAdapter
    private var _binding: FragmentRepresentativeBinding? = null
    val binding get() = _binding!!

    val viewModel: RepresentativeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.setStates(resources.getStringArray(R.array.states).toList())

        listAdapter = RepresentativeListAdapter()
        binding.representativesRecyclerView.adapter = listAdapter

        setUpClickListeners()
        setUpObservers()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        savedInstanceState?.let {
            it.getInt(MOTION_LAYOUT_STATE_KEY)?.let { stateId ->
                binding.motionLayout.transitionToState(stateId)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val layoutState = binding.motionLayout.currentState
        outState.putInt(MOTION_LAYOUT_STATE_KEY, layoutState)
    }

    private fun setUpObservers() {
        viewModel.representatives.observe(viewLifecycleOwner, {
            it?.let { listAdapter.submitList(it) }
        })

        viewModel.showSnackbarMessage.observe(viewLifecycleOwner, {
            if (it != ElectionsViewModel.EMPTY_SNACKBAR_INT) {
                Snackbar.make(requireView(), getText(it), Snackbar.LENGTH_SHORT).show()
                viewModel.clearSnackbarMessage()
            }
        })
    }

    private fun setUpClickListeners() {
        binding.locationButton.setOnClickListener {
            hideKeyboard()
            checkDeviceLocationSettingsAndGetLocation()
        }
        binding.searchButton.setOnClickListener {
            hideKeyboard()
            performSearchClick()
        }
    }

    private fun performSearchClick() {
        if (binding.state.selectedItemPosition == 0) {
            YoYo.with(Techniques.Pulse).duration(500).repeat(3).playOn(binding.state)
            Snackbar.make(binding.root, R.string.error_state_required, Snackbar.LENGTH_SHORT).show()
        } else {
            val hasConnection = checkHasInternetConnection()
            if (hasConnection == null || !hasConnection) {
                Snackbar.make(
                    binding.root,
                    R.string.error_no_internet_detected,
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                viewModel.getRepresentatives()
            }
        }
    }

    private fun checkHasInternetConnection(): Boolean? {
        val connectivityManager =
            getSystemService(requireActivity(), ConnectivityManager::class.java)

        val connected = connectivityManager?.run {
            val currentNetwork = this.activeNetwork
            val caps = this.getNetworkCapabilities(currentNetwork)
            caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

        return connected
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            true
        } else {
            requestRequiredPermissions()
            false
        }
    }


    private fun requestRequiredPermissions() {
        if (isPermissionGranted())
            return

        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION
        )
    }

    private fun isPermissionGranted(): Boolean {
        val fineLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))

        return fineLocationApproved
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            checkDeviceLocationSettingsAndGetLocation(false)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            } else {
                Snackbar.make(
                    binding.root,
                    getText(R.string.error_location_permission_required),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.settings) {
                        startActivity(Intent().apply {
                            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }.show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (!checkLocationPermissions())
            return

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                location?.let {
                    Log.d(TAG, location.toString())

                    try {
                        val address = geoCodeLocation(location)
                        viewModel.setAddress(address)
                    } catch (e: Throwable) {
                        Snackbar.make(
                            binding.root,
                            getText(R.string.error_location_parse_geocode),
                            Snackbar.LENGTH_LONG
                        ).show() //
                    }
                }
            }
            .addOnFailureListener {
                Log.e(TAG, it.localizedMessage)
                it.printStackTrace()
                Snackbar.make(
                    binding.root,
                    getText(R.string.error_location_not_found),
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }

    private fun checkDeviceLocationSettingsAndGetLocation(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())

        val locationSettingsResponseTask = settingsClient.checkLocationSettings(builder.build())
        locationSettingsResponseTask.addOnFailureListener { exception ->
            handleLocationResponseException(exception, resolve)
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful) {
                getLocation()
            }
        }
    }

    private fun handleLocationResponseException(
        exception: Exception,
        resolve: Boolean
    ) {
        if (exception is ResolvableApiException && resolve) {
            try {
                startIntentSenderForResult(
                    exception.resolution.intentSender,
                    LOCATION_REQUEST_CODE,
                    null, 0, 0, 0, null
                )
            } catch (sendEx: IntentSender.SendIntentException) {
                Log.d(TAG, "Error getting location settings resolution: " + sendEx.message)
            }
        } else {
            Snackbar.make(
                binding.root,
                R.string.error_location_disabled, Snackbar.LENGTH_LONG
            ).setAction(android.R.string.ok) {
                checkDeviceLocationSettingsAndGetLocation()
            }.show()
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}