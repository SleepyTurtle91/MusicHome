package com.lemonsquad.musichome.core

import com.lemonsquad.musichome.core.domain.model.PlaybackQueue
import com.lemonsquad.musichome.core.domain.model.RepeatMode
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackSessionTest {

    @Test
    fun `test queue revision increments`() {
        val initial = PlaybackQueue(songs = emptyList(), revision = 0)
        val mutated = initial.copy(revision = initial.revision + 1)
        
        assertEquals(0, initial.revision)
        assertEquals(1, mutated.revision)
    }

    @Test
    fun `test repeat mode ordinal mapping`() {
        assertEquals(0, RepeatMode.NONE.ordinal)
        assertEquals(1, RepeatMode.ONE.ordinal)
        assertEquals(2, RepeatMode.ALL.ordinal)
    }
}
