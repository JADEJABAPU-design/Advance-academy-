package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.GSEBGeneratedPaper
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyGold

@Composable
fun PaperViewerDialog(
    paper: GSEBGeneratedPaper,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Question Paper, 1: Solutions
    val context = LocalContext.current

    // Parse sections from JSON or format nicely
    val parsedSections = remember(paper.sectionsJson) {
        parseSectionsFromJson(paper.sectionsJson)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 16.dp)
                .testTag("gseb_paper_viewer_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AcademyBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GSEB BOARD PAPER",
                                fontWeight = FontWeight.Bold,
                                color = AcademyBlue,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${paper.standard} • ${paper.subject} (${paper.medium})",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Copy to Clipboard
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("GSEB Paper", "${paper.title}\n\n${paper.generalInstructions}\n\n${paper.sectionsJson}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Paper copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = AcademyBlue)
                        }

                        // Delete
                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = AcademyCrimson)
                        }

                        // Close
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Tabs: Question Paper vs Answer Key
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = AcademyBlue,
                    modifier = Modifier.clip(RoundedCornerShape(8.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Question Paper", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Marking Scheme & Solutions", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Paper Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (selectedTab == 0) {
                        // Official GSEB Board Paper View
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                                .background(Color(0xFFFAFAFA))
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                // Formal Academy Header
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "ADVANCE ACADEMY",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        color = AcademyBlue,
                                        letterSpacing = 1.5.sp
                                    )
                                    Text(
                                        text = "GUJARAT SECONDARY & HIGHER SECONDARY EDUCATION BOARD (GSEB)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569)
                                    )
                                    Text(
                                        text = paper.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    // Details Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Std: ${paper.standard}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Subject: ${paper.subject}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Medium: ${paper.medium}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Time: ${paper.durationMinutes} Minutes", fontSize = 11.sp)
                                        Text(text = "Date: ${paper.createdDate}", fontSize = 11.sp)
                                        Text(text = "Max Marks: ${paper.totalMarks}", fontSize = 11.sp, fontWeight = FontWeight.Black, color = AcademyBlue)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Start
                                    ) {
                                        Text(text = "Student Name / Roll No: ____________________________________", fontSize = 11.sp, color = Color(0xFF64748B))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // General Instructions
                                Text(
                                    text = "General Instructions:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = paper.generalInstructions,
                                    fontSize = 11.sp,
                                    color = Color(0xFF475569),
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Sections
                                parsedSections.forEach { section ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = section.name,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = AcademyBlue
                                            )
                                            if (section.instructions.isNotEmpty()) {
                                                Text(
                                                    text = section.instructions,
                                                    fontSize = 10.sp,
                                                    color = Color(0xFF475569)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    section.questions.forEach { q ->
                                        Text(
                                            text = q,
                                            fontSize = 12.sp,
                                            color = Color(0xFF1E293B),
                                            lineHeight = 18.sp,
                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "— ALL THE BEST FOR GSEB BOARD EXAMS —",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                            }
                        }
                    } else {
                        // Answer Key / Marking Scheme View
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(10.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "OFFICIAL GSEB MARKING SCHEME & MODEL ANSWERS",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = AcademyBlue
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider()
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = paper.answerKey,
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B),
                                    lineHeight = 20.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

data class ParsedSection(
    val name: String,
    val instructions: String,
    val questions: List<String>
)

private fun parseSectionsFromJson(json: String): List<ParsedSection> {
    val result = mutableListOf<ParsedSection>()
    try {
        // Simple regex/substring based extraction of sections for reliability
        val sectionChunks = json.split("{\"sectionName\":").drop(1)
        for (chunk in sectionChunks) {
            val name = chunk.substringBefore("\",\"instructions\":").removePrefix("\"")
            val remainder = chunk.substringAfter("\",\"instructions\":")
            val instructions = remainder.substringBefore("\",\"questions\":").removePrefix("\"")
            val questionsPart = remainder.substringAfter("\",\"questions\":[").substringBefore("]}")
            val qList = questionsPart.split("\",\"").map { it.replace("\"", "").replace("\\\"", "\"").replace("\\n", "\n").trim() }
            result.add(ParsedSection(name, instructions, qList))
        }
    } catch (e: Exception) {
        // Fallback simple parsing
        result.add(
            ParsedSection(
                name = "GSEB Board Examination Paper",
                instructions = "Attempt all questions.",
                questions = listOf(json)
            )
        )
    }
    return if (result.isEmpty()) listOf(ParsedSection("Section A", "Answer all questions", listOf("1. GSEB Board Question Model"))) else result
}
