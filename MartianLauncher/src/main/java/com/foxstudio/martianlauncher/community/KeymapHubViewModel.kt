package com.foxstudio.martianlauncher.community

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.foxstudio.martianlauncher.community.model.KeymapItem
import com.foxstudio.martianlauncher.community.model.Pagination
import com.foxstudio.martianlauncher.game.control.zalith.ZalithControlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

sealed interface KeymapListState {
    data object Loading : KeymapListState
    data object Empty : KeymapListState
    data class Success(val items: List<KeymapItem>, val pagination: Pagination) : KeymapListState
    data class Error(val message: String) : KeymapListState
}

sealed interface DownloadState {
    data object Idle : DownloadState
    data class Downloading(val id: Int) : DownloadState
    data class Done(val id: Int, val file: File) : DownloadState
    data class Failed(val id: Int, val message: String) : DownloadState
}

class KeymapHubViewModel : ViewModel() {

    var listState by mutableStateOf<KeymapListState>(KeymapListState.Loading)
        private set

    var downloadState by mutableStateOf<DownloadState>(DownloadState.Idle)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var currentPage by mutableStateOf(1)
        private set

    private var searchJob: Job? = null

    init {
        load()
    }

    fun load(page: Int = 1) {
        currentPage = page
        listState = KeymapListState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                KeymapHubApi.list(page = page, search = searchQuery)
            }.onSuccess { resp ->
                withContext(Dispatchers.Main) {
                    listState = if (resp.data.isEmpty()) KeymapListState.Empty
                    else KeymapListState.Success(resp.data, resp.pagination)
                }
            }.onFailure { e ->
                withContext(Dispatchers.Main) {
                    listState = KeymapListState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun onSearchChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            load(page = 1)
        }
    }

    fun download(item: KeymapItem) {
        if (downloadState is DownloadState.Downloading) return
        downloadState = DownloadState.Downloading(item.id)
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val full = KeymapHubApi.getItem(item.id)
                val jsonContent: String = KeymapHubApi.fetchJsonContent(full)
                ZalithControlManager.import(jsonContent.byteInputStream(), name = item.name)
            }.onSuccess { file ->
                withContext(Dispatchers.Main) {
                    downloadState = DownloadState.Done(item.id, file)
                }
            }.onFailure { e ->
                withContext(Dispatchers.Main) {
                    downloadState = DownloadState.Failed(item.id, e.message ?: "Download failed")
                }
            }
        }
    }

    fun resetDownloadState() {
        downloadState = DownloadState.Idle
    }
}
