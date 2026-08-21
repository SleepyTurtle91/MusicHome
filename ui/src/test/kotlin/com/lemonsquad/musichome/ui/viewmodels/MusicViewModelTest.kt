package com.lemonsquad.musichome.ui.viewmodels

import android.content.Context
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.core.domain.model.PlaybackQueue
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.fail
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.lang.reflect.Field

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MusicViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    
    private lateinit var repository: MusicRepository
    private lateinit var context: Context
    private lateinit var viewModel: MusicViewModel
    
    private val mockQueueState = MutableStateFlow<PlaybackQueue?>(null)
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        repository = mockk(relaxed = true)
        context = RuntimeEnvironment.getApplication()
        
        val componentName = android.content.ComponentName(context, "com.lemonsquad.musichome.media.player.MusicPlaybackService")
        val serviceInfo = android.content.pm.ServiceInfo().apply {
            name = componentName.className
            packageName = componentName.packageName
        }
        org.robolectric.Shadows.shadowOf(context.packageManager).addOrUpdateService(serviceInfo)
        
        val resolveInfo = android.content.pm.ResolveInfo().apply {
            this.serviceInfo = serviceInfo
            filter = android.content.IntentFilter("androidx.media3.session.MediaLibraryService")
        }
        val intent = android.content.Intent("androidx.media3.session.MediaLibraryService")
        intent.setPackage(context.packageName)
        org.robolectric.Shadows.shadowOf(context.packageManager).addResolveInfoForIntent(intent, resolveInfo)

        val sessionIntent = android.content.Intent("androidx.media3.session.MediaSessionService")
        sessionIntent.setPackage(context.packageName)
        org.robolectric.Shadows.shadowOf(context.packageManager).addResolveInfoForIntent(sessionIntent, resolveInfo)

        every { repository.currentQueue } returns mockQueueState
        
        viewModel = MusicViewModel(repository, context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setMockPlaybackState(currentSongId: String?) {
        val field: Field = MusicViewModel::class.java.getDeclaredField("_playbackStatus")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val stateFlow = field.get(viewModel) as MutableStateFlow<PlaybackStatus>
        stateFlow.value = PlaybackStatus(currentSongId = currentSongId)
    }
    
    @Test
    fun `removeQueueItem with valid index removes item`() = runTest {
        val song1 = mockk<Song> { every { id } returns 1L }
        val song2 = mockk<Song> { every { id } returns 2L }
        val song3 = mockk<Song> { every { id } returns 3L }
        
        mockQueueState.value = PlaybackQueue(
            songs = listOf(song1, song2, song3),
            currentIndex = 1
        )
        
        setMockPlaybackState("2")
        
        viewModel.removeQueueItem(0) 
        
        verify { repository.updateQueueOrder(listOf(song2, song3)) }
        verify { repository.updateQueueIndex(0) }
    }

    @Test
    fun `removeQueueItem with out of bounds index does not crash`() = runTest {
        val song1 = mockk<Song> { every { id } returns 1L }
        
        mockQueueState.value = PlaybackQueue(
            songs = listOf(song1),
            currentIndex = 0
        )
        setMockPlaybackState("1")
        
        try {
            viewModel.removeQueueItem(5) 
        } catch (e: Exception) {
            fail("Expected no exception, but got $e")
        }
    }

    @Test
    fun `removeQueueItem before current index with idle player updates index correctly`() = runTest {
        val song1 = mockk<Song> { every { id } returns 1L }
        val song2 = mockk<Song> { every { id } returns 2L }
        
        mockQueueState.value = PlaybackQueue(
            songs = listOf(song1, song2),
            currentIndex = 1
        )
        
        setMockPlaybackState(null)
        
        viewModel.removeQueueItem(0)
        
        verify { repository.updateQueueOrder(listOf(song2)) }
        verify { repository.updateQueueIndex(0) }
    }
}
