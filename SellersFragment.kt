package com.si.sevencup.seller.presentation.sellers

import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.RoundedCornerTreatment
import com.google.android.material.shape.ShapePathModel
import com.si.lib.fragments.navigation.NavOptions
import com.si.lib.mvvm.NavigationFragmentK
import com.si.lib.mvvm.plusAssign
import com.si.sevencup.common.data.repository.AuthorizationCheckImpl
import com.si.sevencup.common.data.repository.DataRepositoryImpl
import com.si.sevencup.seller.R
import com.si.sevencup.seller.data.repository.AdminRepositoryImpl
import com.si.sevencup.seller.domain.cases.LoadHistory
import com.si.sevencup.seller.domain.cases.LoadSellers
import com.si.sevencup.seller.domain.entity.SellerDetails
import com.si.sevencup.seller.presentation.dashboard.DashboardAction
import com.si.sevencup.seller.presentation.dashboard.DashboardFragment
import com.si.sevencup.seller.presentation.logout.LogoutDialog
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_sellers.*
import java.util.*
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class SellersFragment : NavigationFragmentK(), DatePickerDialog.OnDateSetListener {

    private lateinit var datePicker: DatePickerDialog
    private lateinit var currentSeller : SellerDetails
    private var showPickerOnResume : Boolean = false

    private val stateObserver = Observer<SellersState> { onStateChanged(it) }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        Log.e(TAG, "onModelCreate!")
        val adminRepository = AdminRepositoryImpl()
        val loadSellers = LoadSellers(adminRepository)
        return SellersViewModel(loadSellers,
                LoadHistory(AuthorizationCheckImpl(), DataRepositoryImpl()),
                AndroidSchedulers.mainThread()) as T
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sellers, container, false)
    }
    private val sellerAdapter = SellersAdapter { onSellerClicked(it) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val shape = ShapePathModel().apply {
            topLeftCorner = RoundedCornerTreatment(resources.getDimension(R.dimen.default_margin))
            topRightCorner = RoundedCornerTreatment(32f)
        }
        dataCard.background = MaterialShapeDrawable(shape).apply {
            setTint(ContextCompat.getColor(view.context, android.R.color.white))
            paintStyle = Paint.Style.FILL
        }

        list_sellers.adapter = sellerAdapter

        Log.e(TAG, "Current state${getViewModel().getState()}" )

        getViewModel().observeState(this, stateObserver)
        getViewModel().getState() ?.let {
            if (it is SellersState.Calendar) {
                openSellerCalendar(it.seller)
            }
        }
        image_exit.setOnClickListener { LogoutDialog().show(fragmentManager, SellersFragment.TAG) }


    }

    override fun onResume() {
        super.onResume()
//        if (showPickerOnResume && currentSeller !=null) {
//            openSellerCalendar(currentSeller)
//        }
//        showPickerOnResume = false
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val apply = Calendar.getInstance()
                .apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, monthOfYear)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                    set(Calendar.MILLISECOND, 999)

                    Log.e(TAG, "new filter date:${this.time}")
                }
                .timeInMillis
        //Show cups only of selected date
        val list = LoadHistory.filterByDate(currentSeller.cups, apply)
        Log.e(TAG, "Filtered cups:${list}")
        view?.dismiss()//OnPause(true)
        if (!list.isEmpty()) {
            //showPickerOnResume = true
            navigationManager.launchFragment(DashboardFragment.createInstance(list, currentSeller.email), NavOptions.DEFAULT//.add(NavOptions.ADD_FRAGMENT)
            )
        } else {
            showError()
        }
    }

    private fun onStateChanged(state: SellersState) {
        Log.e(TAG, "onStateChanged:${state}")
        when(state) {
            is SellersState.Idle -> getViewModel().handleAction(SellersAction.LoadSellers)
            is SellersState.Error ->  showError()
            is SellersState.Loading -> {}
            is SellersState.Loaded -> sellerAdapter.submitList(state.sellers)
            is SellersState.Calendar -> openSellerCalendar(state.seller)
        }
    }

    private fun showError() {
        Toast.makeText(requireContext(), R.string.err_something_went_wrong, Toast.LENGTH_LONG).show()
    }

    private fun getViewModel() = ViewModelProviders.of(this, this)[SellersViewModel::class.java]

    private fun onSellerClicked(it: SellerDetails) {

        getViewModel().handleAction(SellersAction.LoadCups(it))

    }

    private fun openSellerCalendar(seller: SellerDetails) {
        Log.e(TAG, "cups:${seller.cups}")
        currentSeller = seller
        datePicker = DashboardFragment.createDatePicker(this);
        datePicker.autoDismiss(true)
        //Set enabled days to datepicker
        datePicker.selectableDays = (seller.cups
                //.filter { !it.uid.contentEquals("0") }
                .map {
                    //Log.e(TAG, "cup:${it}")
                    Calendar.getInstance().apply { timeInMillis = it.sellDate.time }
                }.toTypedArray())
        try {
            fragmentManager?.let {
                val oldFragment = it.findFragmentByTag("Datepickerdialog")
                val transaction : FragmentTransaction = it.beginTransaction()
                if (oldFragment != null) {
                    Log.e(TAG, "show old fragment")
                    transaction.show(oldFragment)
                    transaction.commit()
                }
                else {
                    Log.e(TAG, "show new fragment")
                datePicker.show(transaction, "Datepickerdialog")
                }
            }


        } catch (ex : Throwable) {ex.printStackTrace()}

    }

    companion object {
        const val TAG = "SellersFragment"
    }
}