package com.foxstudio.martianlauncher.ui.screens.content.download

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import com.foxstudio.martianlauncher.R
import com.foxstudio.martianlauncher.community.DownloadState
import com.foxstudio.martianlauncher.community.KeymapHubApi
import com.foxstudio.martianlauncher.community.KeymapHubViewModel
import com.foxstudio.martianlauncher.community.KeymapListState
import com.foxstudio.martianlauncher.community.model.KeymapItem
import com.foxstudio.martianlauncher.ui.base.BaseScreen
import com.foxstudio.martianlauncher.ui.components.BackgroundCard
import com.foxstudio.martianlauncher.ui.components.ScalingLabel
import com.foxstudio.martianlauncher.ui.screens.NestedNavKey
import com.foxstudio.martianlauncher.ui.screens.TitledNavKey
@Composable
fun KeymapCommunityScreen(
    key: NestedNavKey.DownloadKeymapCommunity,
    mainScreenKey: TitledNavKey?,
    downloadScreenKey: TitledNavKey?
) {
    val vm: KeymapHubViewModel = viewModel(key = "KeymapCommunity")
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var showUploadDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vm.downloadState) {
        when (val state = vm.downloadState) {
            is DownloadState.Done -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.community_download_success, state.file.nameWithoutExtension),
                    Toast.LENGTH_SHORT
                ).show()
                vm.resetDownloadState()
            }
            is DownloadState.Failed -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.community_download_failed, state.message),
                    Toast.LENGTH_LONG
                ).show()
                vm.resetDownloadState()
            }
            else -> {}
        }
    }

    BaseScreen(
        levels1 = listOf(
            Pair(NestedNavKey.Download::class.java, mainScreenKey)
        ),
        Triple(key, downloadScreenKey, false)
    ) { isVisible ->
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = vm.searchQuery,
                    onValueChange = vm::onSearchChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.community_search_hint)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        focusManager.clearFocus()
                        vm.load(1)
                    })
                )

                IconButton(onClick = { vm.load(vm.currentPage) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_restart_alt),
                        contentDescription = stringResource(R.string.generic_refresh)
                    )
                }

                IconButton(onClick = { showUploadDialog = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = "Upload"
                    )
                }
            }

            when (val state = vm.listState) {
                is KeymapListState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is KeymapListState.Empty -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        ScalingLabel(text = stringResource(R.string.community_empty))
                    }
                }
                is KeymapListState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ScalingLabel(text = state.message)
                            FilledTonalButton(onClick = { vm.load(vm.currentPage) }) {
                                Text(stringResource(R.string.generic_refresh))
                            }
                        }
                    }
                }
                is KeymapListState.Success -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            KeymapCard(
                                item = item,
                                isDownloading = vm.downloadState is DownloadState.Downloading &&
                                    (vm.downloadState as DownloadState.Downloading).id == item.id,
                                onDownload = { vm.download(item) }
                            )
                        }
                    }

                    val pg = state.pagination
                    if (pg.pages > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FilledTonalButton(
                                onClick = { vm.load(pg.page - 1) },
                                enabled = pg.page > 1
                            ) { Text("‹") }

                            Text(
                                text = stringResource(R.string.community_page, pg.page, pg.pages),
                                style = MaterialTheme.typography.labelMedium
                            )

                            FilledTonalButton(
                                onClick = { vm.load(pg.page + 1) },
                                enabled = pg.page < pg.pages
                            ) { Text("›") }
                        }
                    }
                }
            }
        }
    }

    if (showUploadDialog) {
        KeymapUploadDialog(onDismiss = { showUploadDialog = false })
    }
}

@Composable
private fun KeymapCard(
    item: KeymapItem,
    isDownloading: Boolean,
    onDownload: () -> Unit
) {
    BackgroundCard(modifier = Modifier.fillMaxWidth().height(200.dp)) {
        Column {
            AsyncImage(
                model = KeymapHubApi.imageUrl(item),
                contentDescription = item.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Name: ${item.name}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!item.version.isNullOrBlank()) {
                    Text(
                        text = "Version: ${item.version}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (!item.author.isNullOrBlank()) {
                    Text(
                        text = "Author: ${item.author}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isDownloading) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Button(
                        onClick = onDownload,
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.community_download),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}
