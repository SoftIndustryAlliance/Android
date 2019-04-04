package com.si.sevencup.seller.presentation.sellers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.si.sevencup.seller.R
import com.si.sevencup.seller.domain.entity.SellerDetails
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_seller.*

class SellersAdapter(
        private val itemSelected: (SellerDetails) -> Unit
) : ListAdapter<SellerDetails, SellersViewHolder>(Diff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SellersViewHolder {
        return LayoutInflater.from(parent.context)
                .inflate(R.layout.item_seller, parent, false)
                .let { SellersViewHolder(it, itemSelected) }
    }

    override fun onBindViewHolder(holder: SellersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SellersViewHolder(
        override val containerView: View,
        private val itemSelected: (SellerDetails) -> Unit
) : RecyclerView.ViewHolder(containerView), LayoutContainer
{
    private var current: SellerDetails? = null

    init {
        containerView.setOnClickListener { current?.let(itemSelected) }
    }
    fun bind(item: SellerDetails) {
        current = item
        text_seller_email.text = item.email
        text_seller_name.text = item.displayName
    }
}

class Diff : DiffUtil.ItemCallback<SellerDetails>() {
    override fun areItemsTheSame(old: SellerDetails, new: SellerDetails) = old.uid == new.uid

    override fun areContentsTheSame(old: SellerDetails, new: SellerDetails) = old == new
}