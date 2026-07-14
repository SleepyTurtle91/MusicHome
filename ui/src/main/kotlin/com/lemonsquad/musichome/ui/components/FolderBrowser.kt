package com.lemonsquad.musichome.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lemonsquad.musichome.ui.theme.MetallicGray
import com.lemonsquad.musichome.ui.theme.WalkmanOrange
import java.io.File

@Composable
fun FolderBrowser(
    rootPath: String,
    onFileClick: (File) -> Unit,
    onBack: () -> Unit
) {
    var currentDir by remember { mutableStateOf(File(rootPath)) }
    val files = remember(currentDir) { 
        currentDir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentDir.absolutePath == rootPath) {
                    onBack()
                } else {
                    currentDir = currentDir.parentFile ?: currentDir
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = WalkmanOrange)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = currentDir.name.uppercase(),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        LazyColumn {
            items(files) { file ->
                ListItem(
                    modifier = Modifier.clickable {
                        if (file.isDirectory) {
                            currentDir = file
                        } else {
                            onFileClick(file)
                        }
                    },
                    headlineContent = {
                        Text(file.name, color = if (file.isDirectory) Color.White else MetallicGray)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.Description,
                            contentDescription = null,
                            tint = if (file.isDirectory) WalkmanOrange else Color.DarkGray
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }
            
            if (files.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("EMPTY FOLDER", color = Color.DarkGray, letterSpacing = 2.sp)
                    }
                }
            }
        }
    }
}
