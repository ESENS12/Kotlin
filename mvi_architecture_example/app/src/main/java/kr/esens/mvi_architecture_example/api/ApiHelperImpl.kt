package kr.esens.mvi_architecture_example.api

import kr.esens.mvi_architecture_example.model.User

class ApiHelperImpl(private val apiService: ApiService) : ApiHelper{
    override suspend fun getUsers(): List<User> {
        return apiService.getUsers()
    }
}