package kr.esens.mvi_architecture_example.intent

sealed class MainIntent {
    object FetchUser : MainIntent()
}