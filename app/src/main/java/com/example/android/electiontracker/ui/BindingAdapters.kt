package com.example.android.electiontracker.ui

import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.example.android.electiontracker.R
import com.example.android.electiontracker.network.models.VoterInfoResponse

@BindingAdapter("electionInfoTitle")
fun bindElectionInfoTitleText(view: TextView, voterInfo: VoterInfoResponse?) {
    voterInfo?.run {
        view.text = view.resources.getString(R.string.election_info_text, state?.get(0)?.name)
    }
}

@BindingAdapter("imageUrl")
fun bindImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imgUri = imageUrl.toUri().buildUpon().scheme("https").build()

        imageView.load(imgUri) {
            crossfade(true)
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
            transformations(CircleCropTransformation())
        }
    }
}