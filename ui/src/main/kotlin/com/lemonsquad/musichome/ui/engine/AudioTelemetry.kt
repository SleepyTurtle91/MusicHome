package com.lemonsquad.musichome.ui.engine

import com.lemonsquad.musichome.ui.models.OutputState
import com.lemonsquad.musichome.ui.models.VerificationStatus
import kotlinx.coroutines.flow.Flow

interface AudioTelemetry {
    val sampleRate: Flow<Int?>
    val bitDepth: Flow<Int?>
    val format: Flow<String?>
    val outputDevice: Flow<OutputState>
    val verificationStatus: Flow<VerificationStatus>
}
