package com.lemonsquad.musichome.core.data.media

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@OptIn(kotlinx.coroutines.FlowPreview::class)
class MediaStoreObserver(
    private val context: Context,
    private val onSyncRequested: suspend () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val syncTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    // L.I.S.A. Architecture Note: Intentional adoption of FlowPreview.
    // The debounce operator is essential here to prevent the ContentObserver from 
    // flooding the system with sync requests during bulk media operations.
    // There is no stable alternative in kotlinx.coroutines, so we explicitly opt in.
    init {
        scope.launch {
            syncTrigger
                .debounce(3000) // 3 second debounce
                .collect {
                    onSyncRequested()
                }
        }
    }

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        syncTrigger.tryEmit(Unit)
    }

    fun register() {
        context.contentResolver.registerContentObserver(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            true,
            this
        )
    }

    fun unregister() {
        context.contentResolver.unregisterContentObserver(this)
    }
}
