package com.si.sevencup.seller.domain.cases

import com.si.sevencup.seller.domain.repository.AdminRepository

class LoadSellers(private val adminRepository: AdminRepository) {

    fun execute() = adminRepository.getSellers()

}