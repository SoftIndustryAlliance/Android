package com.si.sevencup.seller.domain.entity

import com.si.sevencup.common.domain.entity.SellerCup

data class SellerDetails(val uid: String, val displayName: String,
                         val email: String, var cups : MutableList<SellerCup> = ArrayList<SellerCup>())