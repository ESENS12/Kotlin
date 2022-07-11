package kr.esens.mvi_architecture_example.api

import kr.esens.mvi_architecture_example.model.User
import retrofit2.http.GET


interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<User>

}