package com.bible.alive.ui.read

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.LightbulbOutline
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bible.alive.R
import com.bible.alive.domain.model.Verse

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChapterReadScreen(
    bookId: Int,
    chapter: Int,
    onBack: () -> Unit,
    onNextChapter: (bookId: Int, chapter: Int) -> Unit,
    viewModel: ReadViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFontSizeSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    LaunchedEffect(bookId, chapter) {
        viewModel.loadChapter(bookId, chapter)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = uiState.currentChapter?.bookName ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Capítulo $chapter",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showFontSizeSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "Tamaño de fuente"
                        )
                    }
                    IconButton(onClick = { viewModel.toggleSpeaking() }) {
                        Icon(
                            imageVector = if (uiState.isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                            contentDescription = if (uiState.isSpeaking) "Detener" else "Leer en voz alta"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val book = uiState.selectedBook
                    if (book != null && chapter < book.chapters) {
                        onNextChapter(bookId, chapter + 1)
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Siguiente capítulo",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(20.dp)
            ) {
                uiState.currentChapter?.verses?.let { verses ->
                    items(verses) { verse ->
                        VerseItem(
                            verse = verse,
                            fontSize = uiState.fontSize,
                            isHighlighted = verse.verse in uiState.highlightedVerses,
                            hasNote = verse.verse in uiState.notedVerses,
                            onLongPress = { viewModel.onVerseSelected(verse) }
                        )
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
    
    if (uiState.showVerseOptions) {
        VerseOptionsBottomSheet(
            verse = uiState.selectedVerse,
            onDismiss = { viewModel.dismissVerseOptions() },
            onAction = { viewModel.onVerseAction(it) }
        )
    }
    
    if (showFontSizeSheet) {
        FontSizeBottomSheet(
            currentSize = uiState.fontSize,
            onSizeChange = { viewModel.setFontSize(it) },
            onDismiss = { showFontSizeSheet = false }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VerseItem(
    verse: Verse,
    fontSize: Int,
    isHighlighted: Boolean,
    hasNote: Boolean,
    onLongPress: () -> Unit
) {
    val backgroundColor = if (isHighlighted) {
        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.background
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress
            )
            .background(backgroundColor)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = (fontSize - 2).sp
                    )
                ) {
                    append("${verse.verse} ")
                }
                append(verse.text)
            },
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = fontSize.sp,
                lineHeight = (fontSize + 8).sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        
        if (hasNote) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Note,
                contentDescription = "Tiene nota",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerseOptionsBottomSheet(
    verse: Verse?,
    onDismiss: () -> Unit,
    onAction: (VerseAction) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = verse?.reference ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "\"${verse?.text ?: ""}\"",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                VerseOptionItem(
                    icon = Icons.Default.RecordVoiceOver,
                    label = stringResource(R.string.read_aloud),
                    onClick = { onAction(VerseAction.ReadAloud) }
                )
                VerseOptionItem(
                    icon = Icons.Default.Summarize,
                    label = stringResource(R.string.summary),
                    onClick = { onAction(VerseAction.Summary) }
                )
                VerseOptionItem(
                    icon = Icons.Default.LightbulbOutline,
                    label = stringResource(R.string.meaning),
                    onClick = { onAction(VerseAction.Meaning) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                VerseOptionItem(
                    icon = Icons.Default.Share,
                    label = stringResource(R.string.share),
                    onClick = { onAction(VerseAction.Share) }
                )
                VerseOptionItem(
                    icon = Icons.Default.Bookmark,
                    label = stringResource(R.string.highlight),
                    onClick = { onAction(VerseAction.Highlight) }
                )
                VerseOptionItem(
                    icon = Icons.Default.Edit,
                    label = stringResource(R.string.add_note),
                    onClick = { onAction(VerseAction.AddNote) }
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun VerseOptionItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun FontSizeBottomSheet(
    currentSize: Int,
    onSizeChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.font_size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "A",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Slider(
                    value = currentSize.toFloat(),
                    onValueChange = { onSizeChange(it.toInt()) },
                    valueRange = 12f..24f,
                    steps = 5,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                )
                
                Text(
                    text = "A",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Tamaño actual: ${currentSize}sp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
