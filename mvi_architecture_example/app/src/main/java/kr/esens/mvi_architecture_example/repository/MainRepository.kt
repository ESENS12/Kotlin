package kr.esens.mvi_architecture_example.repository

import kr.esens.mvi_architecture_example.api.ApiHelper

class MainRepository(private val apiHelper: ApiHelper) {
    suspend fun getUsers() = apiHelper.getUsers()
}