package kr.esens.mvi_architecture_example.api

import kr.esens.mvi_architecture_example.model.User

interface ApiHelper {
    suspend fun getUsers(): List<User>
}