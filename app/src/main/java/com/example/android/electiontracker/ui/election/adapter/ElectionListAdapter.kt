package com.example.android.electiontracker.ui.election.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.electiontracker.databinding.ItemElectionListBinding
import com.example.android.electiontracker.network.models.Election

class ElectionListAdapter(private val clickListener: ElectionListener) :
    ListAdapter<Election, ElectionListAdapter.ElectionViewHolder>(ElectionDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElectionViewHolder {
        return ElectionViewHolder(ItemElectionListBinding
            .inflate(LayoutInflater.from(parent.context),parent, false))
    }

    override fun onBindViewHolder(holder: ElectionViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    class ElectionViewHolder(private var binding: ItemElectionListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(election: Election, clickListener: ElectionListener) {
            binding.election = election
            binding.clickListener = clickListener
        }
    }

    class ElectionListener(val clickListener: (election: Election) -> Unit) {
        fun onClick(election: Election) = clickListener(election)
    }

    companion object ElectionDiffCallback : DiffUtil.ItemCallback<Election>() {
        override fun areItemsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Election, newItem: Election): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.name == newItem.name
                    && oldItem.division == newItem.division
                    && oldItem.electionDay == newItem.electionDay
        }
    }
}