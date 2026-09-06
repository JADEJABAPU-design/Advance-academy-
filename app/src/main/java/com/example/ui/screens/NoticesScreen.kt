package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ClassNotice
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold
import com.example.ui.viewmodel.AcademyViewModel
import com.example.ui.viewmodel.AppRole

@Composable
fun NoticesScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val notices by viewModel.allNotices.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    var showBroadcastDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        floatingActionButton = {
            if (currentRole == AppRole.ADMIN_FACULTY) {
                FloatingActionButton(
                    onClick = { showBroadcastDialog = true },
                    containerColor = AcademyBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("broadcast_notice_fab")
                ) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = "Broadcast Notice")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Header Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notices_header_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AcademyBlue.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = AcademyBlue, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Automated Class Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Real-time automated SMS & VMS updates to parents",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Recent Notifications (${notices.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF334155)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Notifications Feed
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notices, key = { it.id }) { notice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notice_item_${notice.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Category Pill
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (notice.category) {
                                            "ATTENDANCE" -> AcademyEmerald.copy(alpha = 0.15f)
                                            "FEES" -> AcademyGold.copy(alpha = 0.2f)
                                            "EXAM" -> AcademyBlue.copy(alpha = 0.15f)
                                            else -> Color(0xFFE2E8F0)
                                        }
                                    ) {
                                        Text(
                                            text = notice.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (notice.category) {
                                                "ATTENDANCE" -> AcademyEmerald
                                                "FEES" -> Color(0xFFB45309)
                                                "EXAM" -> AcademyBlue
                                                else -> Color(0xFF475569)
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = "Target: ${notice.targetStandard}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Text(
                                    text = notice.datePosted,
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = notice.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = notice.message,
                                fontSize = 13.sp,
                                color = Color(0xFF475569),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    if (showBroadcastDialog) {
        BroadcastNoticeDialog(
            onDismiss = { showBroadcastDialog = false },
            onSend = { title, message, category, priority, standard ->
                viewModel.postNotice(title, message, category, priority, standard)
                showBroadcastDialog = false
            }
        )
    }
}

@Composable
fun BroadcastNoticeDialog(
    onDismiss: () -> Unit,
    onSend: (String, String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("SCHEDULE") }
    var selectedPriority by remember { mutableStateOf("HIGH") }
    var targetStandard by remember { mutableStateOf("All") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Broadcast Class Update", fontWeight = FontWeight.Bold, color = AcademyBlue) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Notice Title") },
                    placeholder = { Text("e.g. Tomorrow's Maths Test Postponed") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Message Body") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                // Category selection
                Text(text = "Category", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("SCHEDULE", "EXAM", "FEES", "GENERAL").forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AcademyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Target Standard
                Text(text = "Target Standard", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("All", "Std 10 GSEB", "Std 12 Science", "Std 12 Commerce").forEach { std ->
                        FilterChip(
                            selected = targetStandard == std,
                            onClick = { targetStandard = std },
                            label = { Text(std, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AcademyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && message.isNotBlank()) {
                        onSend(title, message, selectedCategory, selectedPriority, targetStandard)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
            ) {
                Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Broadcast Now")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
