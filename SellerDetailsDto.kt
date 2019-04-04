package com.si.sevencup.seller.data.dto

import com.si.sevencup.seller.domain.entity.SellerDetails

data class SellerDetailsDto(val name: String = "", val email: String = "") {
    fun toEntity(uid: String) = SellerDetails(uid, name, email)
}