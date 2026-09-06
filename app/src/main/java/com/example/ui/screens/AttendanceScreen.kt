package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.Batch
import com.example.data.model.Student
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun AttendanceScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val batches by viewModel.allBatches.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedBatchId by viewModel.selectedBatchId.collectAsStateWithLifecycle()
    val activeAttendanceMap by viewModel.activeAttendanceMap.collectAsStateWithLifecycle()

    // Filter students by selected batch
    val filteredStudents = if (selectedBatchId == 0L) {
        students
    } else {
        students.filter { it.batchId == selectedBatchId }
    }

    // Attendance stats for filtered set
    val totalCount = filteredStudents.size
    val presentCount = filteredStudents.count { (activeAttendanceMap[it.id] ?: "PRESENT") == "PRESENT" }
    val absentCount = filteredStudents.count { (activeAttendanceMap[it.id] ?: "PRESENT") == "ABSENT" }
    val lateCount = filteredStudents.count { (activeAttendanceMap[it.id] ?: "PRESENT") == "LATE" }
    val attendanceRate = if (totalCount > 0) ((presentCount + lateCount) * 100) / totalCount else 0

    // Date navigation helper
    val cal = Calendar.getInstance()
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displaySdf = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())
    val displayDate = try {
        val parsed = sdf.parse(selectedDate)
        if (parsed != null) displaySdf.format(parsed) else selectedDate
    } catch (e: Exception) {
        selectedDate
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Date Picker & Navigation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("attendance_date_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        try {
                            val d = sdf.parse(selectedDate) ?: Calendar.getInstance().time
                            val c = Calendar.getInstance().apply { time = d; add(Calendar.DAY_OF_YEAR, -1) }
                            viewModel.setSelectedDate(sdf.format(c.time))
                        } catch (e: Exception) {}
                    }
                ) {
                    Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = "Previous Day", tint = AcademyBlue)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        viewModel.setSelectedDate(viewModel.todayDateString)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        tint = AcademyBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = displayDate,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1E293B)
                        )
                        if (selectedDate == viewModel.todayDateString) {
                            Text(
                                text = "Today's Session",
                                fontSize = 10.sp,
                                color = AcademyEmerald,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                IconButton(
                    onClick = {
                        try {
                            val d = sdf.parse(selectedDate) ?: Calendar.getInstance().time
                            val c = Calendar.getInstance().apply { time = d; add(Calendar.DAY_OF_YEAR, 1) }
                            viewModel.setSelectedDate(sdf.format(c.time))
                        } catch (e: Exception) {}
                    }
                ) {
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Next Day", tint = AcademyBlue)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Batches Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedBatchId == 0L,
                onClick = { viewModel.setSelectedBatch(0L) },
                label = { Text("All Batches (${students.size})") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AcademyBlue,
                    selectedLabelColor = Color.White
                )
            )
            batches.forEach { batch ->
                FilterChip(
                    selected = selectedBatchId == batch.id,
                    onClick = { viewModel.setSelectedBatch(batch.id) },
                    label = { Text(batch.name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AcademyBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Summary KPI Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AttendanceKpi("Present", "$presentCount", AcademyEmerald)
                AttendanceKpi("Absent", "$absentCount", AcademyCrimson)
                AttendanceKpi("Late", "$lateCount", AcademyGold)
                AttendanceKpi("Rate", "$attendanceRate%", AcademyBlue)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Action Bar: "Mark All Present" & "Save & Notify Parents"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.markAllPresent(filteredStudents) },
                modifier = Modifier.testTag("mark_all_present_button")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = AcademyEmerald, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mark All Present", fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.saveAttendance() },
                modifier = Modifier.testTag("save_attendance_button"),
                colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save & Notify Parents", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Student Attendance List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredStudents, key = { it.id }) { student ->
                val currentStatus = activeAttendanceMap[student.id] ?: "PRESENT"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_attendance_item_${student.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Student Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (student.avatarColorSeed % 3) {
                                            0 -> AcademyBlue
                                            1 -> AcademyEmerald
                                            else -> AcademyGold
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = student.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "${student.rollNumber} • ${student.standard}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Status Selector Buttons (Present / Late / Absent)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StatusOptionChip(
                                label = "P",
                                isSelected = currentStatus == "PRESENT",
                                selectedColor = AcademyEmerald,
                                onClick = { viewModel.setStudentAttendanceStatus(student.id, "PRESENT") }
                            )
                            StatusOptionChip(
                                label = "L",
                                isSelected = currentStatus == "LATE",
                                selectedColor = AcademyGold,
                                onClick = { viewModel.setStudentAttendanceStatus(student.id, "LATE") }
                            )
                            StatusOptionChip(
                                label = "A",
                                isSelected = currentStatus == "ABSENT",
                                selectedColor = AcademyCrimson,
                                onClick = { viewModel.setStudentAttendanceStatus(student.id, "ABSENT") }
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun AttendanceKpi(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Black, fontSize = 16.sp, color = color)
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun StatusOptionChip(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .clickable { onClick() },
        shape = CircleShape,
        color = if (isSelected) selectedColor else Color(0xFFF1F5F9),
        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFCBD5E1)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF64748B),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
