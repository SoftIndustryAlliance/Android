package com.si.sevencup.seller.presentation.sellers

import com.si.lib.mvvm.BaseViewModel2
import com.si.lib.mvvm.StateReducer
import com.si.sevencup.common.domain.entity.SellerCup
import com.si.sevencup.seller.domain.cases.LoadHistory
import com.si.sevencup.seller.domain.cases.LoadSellers
import com.si.sevencup.seller.domain.entity.SellerDetails
import io.reactivex.Observable
import io.reactivex.Scheduler

class SellersViewModel(
        private val loadSellers: LoadSellers,
        private val loadHistory: LoadHistory,
        scheduler: Scheduler
) : BaseViewModel2<SellersAction, SellersState>(scheduler) {

    override fun route(action: SellersAction): Observable<StateReducer<SellersState>> {
        return when(action) {
            is SellersAction.LoadSellers ->
                loadSellers.execute()
                    .map<StateReducer<SellersState>> { SellersReducer.ShowSellers(it) }
                    .toObservable()
                    .startWith(SellersReducer.ShowLoading)
                    .onErrorReturn { SellersReducer.ShowError(it) }

            is SellersAction.LoadCups -> loadCups(action.sellerUid)

        }
    }

    override fun createInitialState() = SellersState.Idle

    private fun loadCups(seller : SellerDetails) : Observable<StateReducer<SellersState>> {
        return if (seller.cups.isEmpty())
            loadHistory.execute(seller.uid, Int.MAX_VALUE)
                    .map<StateReducer<SellersState>> {
                        seller.cups.addAll(it)
                        SellersReducer.ShowCalendar(seller)
                    }
                    .toObservable()
                    .startWith(SellersReducer.ShowLoading)
                    .onErrorReturn { SellersReducer.ShowError(it) }
        else
            Observable.just(SellersReducer.ShowCalendar(seller))
    }
}