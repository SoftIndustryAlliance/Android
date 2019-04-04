package com.si.sevencup.seller.data.repository

import com.androidhuman.rxfirebase2.database.data
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.si.sevencup.seller.data.dto.SellerDetailsDto
import com.si.sevencup.seller.domain.entity.SellerDetails
import com.si.sevencup.seller.domain.repository.AdminRepository
import io.reactivex.Single

class AdminRepositoryImpl : AdminRepository {
    private val dbRootRef = FirebaseDatabase.getInstance().reference

    override fun isAdmin(uid: String): Single<Boolean> {
        return dbRootRef.child(ADMINS_ROOT)
                .child(uid)
                .data()
                .map(DataSnapshot::exists)
    }

    override fun getSellers(): Single<List<SellerDetails>> {
        return dbRootRef.child(SELLERS_ROOT)
                .data()
                .map { it.children.mapNotNull(::createSellerDetails) }

    }

    private fun createSellerDetails(details: DataSnapshot) =
            details.getValue(SellerDetailsDto::class.java)?.toEntity(details.key!!)

    companion object {
        const val SELLERS_ROOT = "Sellers"
        const val ADMINS_ROOT = "Admins"
    }
}