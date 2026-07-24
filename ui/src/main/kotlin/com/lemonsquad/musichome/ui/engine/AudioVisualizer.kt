package com.lemonsquad.musichome.ui.engine

import kotlinx.coroutines.flow.Flow

interface AudioVisualizer {
    val spectrum: Flow<FloatArray>
    val vuLevel: Flow<Float>
}
