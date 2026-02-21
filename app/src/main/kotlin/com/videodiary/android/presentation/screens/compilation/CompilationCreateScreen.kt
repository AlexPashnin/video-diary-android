package com.videodiary.android.presentation.screens.compilation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.videodiary.android.domain.model.QualityOption
import com.videodiary.android.domain.model.WatermarkPosition
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompilationCreateScreen(
    onCompilationCreated: (compilationId: String) -> Unit,
    onBack: () -> Unit,
    viewModel: CompilationCreateViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.createdId) {
        state.createdId?.let { onCompilationCreated(it) }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Compilation") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            // Date range section
            SectionLabel("Date Range")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                DateButton(
                    label = "From",
                    date = state.startDate,
                    modifier = Modifier.weight(1f),
                    onClick = { showStartDatePicker = true },
                )
                DateButton(
                    label = "To",
                    date = state.endDate,
                    modifier = Modifier.weight(1f),
                    onClick = { showEndDatePicker = true },
                )
            }

            // Clip count summary
            ClipCountSummary(
                clipCount = state.clipIds.size,
                isLoading = state.isLoadingClips,
            )

            HorizontalDivider()

            // Quality section
            SectionLabel("Quality")
            QualitySelector(
                selected = state.quality,
                onSelect = viewModel::setQuality,
            )

            HorizontalDivider()

            // Watermark position section
            SectionLabel("Watermark Position")
            WatermarkPositionSelector(
                selected = state.watermarkPosition,
                onSelect = viewModel::setWatermarkPosition,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = viewModel::createCompilation,
                enabled = !state.isCreating && !state.isLoadingClips && state.clipIds.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isCreating) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Create Compilation")
            }
        }
    }

    if (showStartDatePicker) {
        DatePickerDialogWrapper(
            initial = state.startDate,
            onConfirm = {
                viewModel.setStartDate(it)
                showStartDatePicker = false
            },
            onDismiss = { showStartDatePicker = false },
        )
    }

    if (showEndDatePicker) {
        DatePickerDialogWrapper(
            initial = state.endDate,
            onConfirm = {
                viewModel.setEndDate(it)
                showEndDatePicker = false
            },
            onDismiss = { showEndDatePicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialogWrapper(
    initial: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis =
                initial
                    .atStartOfDay(ZoneId.of("UTC"))
                    .toInstant()
                    .toEpochMilli(),
        )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    onConfirm(Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate())
                }
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    ) { DatePicker(state = datePickerState) }
}

@Composable
private fun DateButton(
    label: String,
    date: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(onClick = onClick, modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ClipCountSummary(
    clipCount: Int,
    isLoading: Boolean,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        if (isLoading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Counting clips…", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Text(
                text =
                    if (clipCount == 0) {
                        "No clips in selected range"
                    } else {
                        "$clipCount clip${if (clipCount == 1) "" else "s"} found"
                    },
                style = MaterialTheme.typography.bodyMedium,
                color =
                    if (clipCount == 0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
            )
        }
    }
}

@Composable
private fun QualitySelector(
    selected: QualityOption,
    onSelect: (QualityOption) -> Unit,
) {
    val options =
        listOf(
            QualityOption.Q_480P to "480p",
            QualityOption.Q_720P to "720p",
            QualityOption.Q_1080P to "1080p",
            QualityOption.Q_4K to "4K",
        )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (option, label) ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(label) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WatermarkPositionSelector(
    selected: WatermarkPosition,
    onSelect: (WatermarkPosition) -> Unit,
) {
    // 3 rows × 2 cols grid matching the 6 positions
    val rows =
        listOf(
            listOf(WatermarkPosition.TOP_LEFT to "↖ Top Left", WatermarkPosition.TOP_RIGHT to "↗ Top Right"),
            listOf(
                WatermarkPosition.CENTER_TOP to "⬆ Center Top",
                WatermarkPosition.CENTER_BOTTOM to "⬇ Center Bottom",
            ),
            listOf(
                WatermarkPosition.BOTTOM_LEFT to "↙ Bottom Left",
                WatermarkPosition.BOTTOM_RIGHT to "↘ Bottom Right",
            ),
        )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { (position, label) ->
                    val isSelected = position == selected
                    Surface(
                        onClick = { onSelect(position) },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
                        color =
                            if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                        border =
                            BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color =
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outlineVariant
                                    },
                            ),
                    ) {
                        Text(
                            text = label,
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                            color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
