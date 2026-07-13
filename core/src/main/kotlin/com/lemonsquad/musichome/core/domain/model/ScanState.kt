package com.lemonsquad.musichome.core.domain.model

sealed class ScanState {
    object Idle : ScanState()
    object Scanning : ScanState()
    data class Finished(val count: Int) : ScanState()
    data class Error(val message: String) : ScanState()
}
