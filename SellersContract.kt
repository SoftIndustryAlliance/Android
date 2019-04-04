package com.si.sevencup.seller.presentation.sellers

import com.si.lib.mvvm.StateReducer
import com.si.sevencup.common.utils.Action
import com.si.sevencup.common.utils.HandlingBehaviour
import com.si.sevencup.seller.domain.entity.SellerDetails

sealed class SellersState {
    abstract val sellers: List<SellerDetails>?

    object Idle : SellersState() {
        override val sellers: List<SellerDetails>? = null
    }
    data class Loading(
            override val sellers: List<SellerDetails>?
    ) : SellersState()
    data class Loaded(
            override val sellers: List<SellerDetails>?
    ) : SellersState()
    data class Calendar(
            override val sellers: List<SellerDetails>?,
            val seller : SellerDetails
    ) : SellersState()
    data class Error(
            override val sellers: List<SellerDetails>?,
            val error: Throwable
    ) : SellersState()
}

sealed class SellersReducer : StateReducer<SellersState> {
    data class ShowSellers(val sellers: List<SellerDetails>) : SellersReducer(){
        override fun reduce(old: SellersState) = SellersState.Loaded(sellers)
    }

    data class ShowCalendar(val seller: SellerDetails) : SellersReducer(){
        override fun reduce(old: SellersState) = SellersState.Calendar(old.sellers, seller)
    }

    data class ShowError(val error: Throwable) : SellersReducer() {
        override fun reduce(old: SellersState) = SellersState.Error(old.sellers, error)
    }

    object ShowLoading : SellersReducer() {
        override fun reduce(old: SellersState) = SellersState.Loading(old.sellers)
    }
}


sealed class SellersAction : Action {
    object LoadSellers : SellersAction() {
        override val handlingBehaviour = HandlingBehaviour.Replaceable
    }
    data class LoadCups(val sellerUid: SellerDetails) : SellersAction() {
        override val handlingBehaviour = HandlingBehaviour.Replaceable
    }
}