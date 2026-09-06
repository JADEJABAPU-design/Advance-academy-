package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ScheduleSlot
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold
import com.example.ui.viewmodel.AcademyViewModel

@Composable
fun ScheduleScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.allSchedules.collectAsStateWithLifecycle()
    val batches by viewModel.allBatches.collectAsStateWithLifecycle()

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    val currentDay = daysOfWeek[selectedDayIndex]

    var selectedStandardFilter by remember { mutableStateOf("All") }
    var showAddDialog by remember { mutableStateOf(false) }

    val daySchedules = schedules.filter { slot ->
        slot.dayOfWeek.equals(currentDay, ignoreCase = true) &&
        (selectedStandardFilter == "All" || slot.standard == selectedStandardFilter)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFFF8FAFC),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = AcademyBlue,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_schedule_fab")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Class Slot")
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

            // Day of Week Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedDayIndex,
                containerColor = Color.White,
                contentColor = AcademyBlue,
                edgePadding = 8.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("schedule_day_tabs")
            ) {
                daysOfWeek.forEachIndexed { index, day ->
                    Tab(
                        selected = selectedDayIndex == index,
                        onClick = { selectedDayIndex = index },
                        text = {
                            Text(
                                text = day.take(3).uppercase(),
                                fontWeight = if (selectedDayIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Standard Filter Chips
            val standards = listOf("All", "Std 10 GSEB", "Std 12 Science", "Std 12 Commerce")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                standards.forEach { std ->
                    FilterChip(
                        selected = selectedStandardFilter == std,
                        onClick = { selectedStandardFilter = std },
                        label = { Text(std) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AcademyBlue,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Slots Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currentDay Schedule (${daySchedules.size} Classes)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (daySchedules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Class, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No classes scheduled for $currentDay", color = Color(0xFF64748B), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap '+' below to add a class slot.", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(daySchedules, key = { it.id }) { slot ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("schedule_slot_${slot.id}"),
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
                                    // Subject & Standard Pill
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    when {
                                                        slot.subject.contains("Math", true) -> AcademyBlue
                                                        slot.subject.contains("Sci", true) || slot.subject.contains("Phys", true) -> AcademyEmerald
                                                        else -> AcademyGold
                                                    }
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(imageVector = Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column {
                                            Text(
                                                text = slot.subject,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = Color(0xFF0F172A)
                                            )
                                            Text(
                                                text = slot.standard,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = AcademyBlue
                                            )
                                        }
                                    }

                                    // Delete action
                                    IconButton(onClick = { viewModel.deleteSchedule(slot) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFCBD5E1))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Details chips: Time, Teacher, Room
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = "${slot.startTime} - ${slot.endTime}", fontSize = 12.sp, color = Color(0xFF334155), fontWeight = FontWeight.Medium)
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = slot.teacherName, fontSize = 12.sp, color = Color(0xFF334155))
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.MeetingRoom, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = slot.roomNo, fontSize = 12.sp, color = Color(0xFF64748B))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add Schedule Dialog
    if (showAddDialog) {
        AddScheduleDialog(
            batches = batches,
            initialDay = currentDay,
            onDismiss = { showAddDialog = false },
            onConfirm = { batchId, standard, subject, teacher, day, start, end, room ->
                viewModel.addSchedule(batchId, standard, subject, teacher, day, start, end, room)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddScheduleDialog(
    batches: List<com.example.data.model.Batch>,
    initialDay: String,
    onDismiss: () -> Unit,
    onConfirm: (Long, String, String, String, String, String, String, String) -> Unit
) {
    var subject by remember { mutableStateOf("Mathematics") }
    var teacher by remember { mutableStateOf("Er. V.P. Sharma") }
    var standard by remember { mutableStateOf("Std 10 GSEB") }
    var dayOfWeek by remember { mutableStateOf(initialDay) }
    var startTime by remember { mutableStateOf("04:30 PM") }
    var endTime by remember { mutableStateOf("05:45 PM") }
    var room by remember { mutableStateOf("Room 102") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Class Schedule", fontWeight = FontWeight.Bold, color = AcademyBlue) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject (e.g. Science, Maths)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = teacher,
                    onValueChange = { teacher = it },
                    label = { Text("Teacher Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = standard,
                    onValueChange = { standard = it },
                    label = { Text("Standard / Batch") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dayOfWeek,
                    onValueChange = { dayOfWeek = it },
                    label = { Text("Day of Week") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = room,
                    onValueChange = { room = it },
                    label = { Text("Classroom / Room No") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val batchId = batches.find { it.standard == standard }?.id ?: 1L
                    onConfirm(batchId, standard, subject, teacher, dayOfWeek, startTime, endTime, room)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
