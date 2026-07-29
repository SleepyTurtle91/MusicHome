package com.lemonsquad.musichome.media.player

import androidx.media3.common.Player

interface TransitionStrategy {
    fun prepare(player: Player)
    fun begin(player: Player)
    fun finish(player: Player)
}

class InstantTransition : TransitionStrategy {
    override fun prepare(player: Player) {}
    override fun begin(player: Player) {}
    override fun finish(player: Player) {}
}
