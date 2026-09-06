package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GSEBGeneratedPaper
import com.example.ui.components.PaperViewerDialog
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold
import com.example.ui.viewmodel.AcademyViewModel

@Composable
fun GSEBPaperGeneratorScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val papers by viewModel.allPapers.collectAsStateWithLifecycle()
    val viewingPaper by viewModel.viewingPaper.collectAsStateWithLifecycle()

    var selectedStandard by remember { mutableStateOf("Std 10 GSEB") }
    var selectedSubject by remember { mutableStateOf("Mathematics") }
    var selectedMedium by remember { mutableStateOf("English") }
    var selectedMarks by remember { mutableIntStateOf(80) }

    val durationMinutes = when (selectedMarks) {
        25 -> 60
        50 -> 120
        else -> 180
    }

    val availableSubjects = when (selectedStandard) {
        "Std 12 Science" -> listOf("Physics", "Chemistry", "Mathematics", "Biology")
        "Std 12 Commerce" -> listOf("Accountancy", "Statistics", "Economics", "English")
        else -> listOf("Mathematics", "Science", "Social Science", "English")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // GSEB Generator Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("gseb_generator_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header with Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF1E3A8A), Color(0xFF2563EB))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AcademyGold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GSEB Board Paper Generator",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = AcademyBlue
                            )
                            Text(
                                text = "Gujarat Board Official Blueprint & Format",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = AcademyEmerald.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "2025-26 Blueprint",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademyEmerald,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Standard Selector
                Text(text = "Standard / Stream", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Std 10 GSEB", "Std 12 Science", "Std 12 Commerce", "Std 9 GSEB").forEach { std ->
                        FilterChip(
                            selected = selectedStandard == std,
                            onClick = {
                                selectedStandard = std
                                selectedSubject = if (std == "Std 12 Science") "Physics" else if (std == "Std 12 Commerce") "Accountancy" else "Mathematics"
                            },
                            label = { Text(std, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AcademyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Subject Selector
                Text(text = "Subject", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableSubjects.forEach { subj ->
                        FilterChip(
                            selected = selectedSubject == subj,
                            onClick = { selectedSubject = subj },
                            label = { Text(subj, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AcademyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Medium & Marks Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Medium
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Medium", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = selectedMedium == "English",
                                onClick = { selectedMedium = "English" },
                                label = { Text("English", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AcademyBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                            FilterChip(
                                selected = selectedMedium == "Gujarati",
                                onClick = { selectedMedium = "Gujarati" },
                                label = { Text("ગુજરાતી", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AcademyBlue,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Marks
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Max Marks", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(25, 50, 80).forEach { marks ->
                                FilterChip(
                                    selected = selectedMarks == marks,
                                    onClick = { selectedMarks = marks },
                                    label = { Text("${marks}M", fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AcademyBlue,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Duration note
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Duration: $durationMinutes Minutes • Sections: A (Objective), B (Short), C (Analytical), D (Long)",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Generate Button
                Button(
                    onClick = {
                        viewModel.generateGSEBPaper(
                            standard = selectedStandard,
                            subject = selectedSubject,
                            medium = selectedMedium,
                            totalMarks = selectedMarks,
                            durationMinutes = durationMinutes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("generate_paper_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AcademyGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generate & Assemble GSEB Paper", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Generated Papers Archive Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Class Papers Archive (${papers.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Tap to open & print",
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Papers List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(papers, key = { it.id }) { paper ->
                Card(
                    onClick = { viewModel.viewPaper(paper) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("paper_card_${paper.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AcademyBlue.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = AcademyBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = paper.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF0F172A),
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "${paper.standard} • ${paper.subject}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(text = "•", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                    Text(
                                        text = "${paper.totalMarks} Marks (${paper.medium})",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AcademyBlue
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.deletePaper(paper) }) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFCBD5E1))
                            }
                            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Open", tint = Color(0xFF94A3B8))
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // View Paper Dialog
    if (viewingPaper != null) {
        PaperViewerDialog(
            paper = viewingPaper!!,
            onDismiss = { viewModel.viewPaper(null) },
            onDelete = {
                viewModel.deletePaper(viewingPaper!!)
            }
        )
    }
}
