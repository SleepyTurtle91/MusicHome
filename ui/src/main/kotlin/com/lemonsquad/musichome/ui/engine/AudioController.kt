package com.lemonsquad.musichome.ui.engine

import com.lemonsquad.musichome.ui.models.GainStage

interface AudioController {
    fun play()
    fun pause()
    fun skipNext()
    fun skipPrevious()
    fun seekTo(position: Long)
    fun setVolume(volume: Int)
    fun setGain(stage: GainStage)
}
