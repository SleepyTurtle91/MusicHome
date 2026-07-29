package com.lemonsquad.musichome.core.domain.model

sealed class ScanState {
    object Idle : ScanState()
    data class Indexing(val progress: Float) : ScanState()
    data class Enriching(val current: Int, val total: Int) : ScanState()
    data class Analyzing(val phase: String) : ScanState()
    data class Finished(val count: Int) : ScanState()
    data class Error(val message: String) : ScanState()
}
