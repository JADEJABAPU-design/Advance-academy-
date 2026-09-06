package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FeePaymentTransaction
import com.example.data.model.FeeRecord
import com.example.data.model.Student
import com.example.ui.components.FeeReceiptDialog
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold
import com.example.ui.viewmodel.AcademyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ParentVMSScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val currentStudent by viewModel.loggedInStudent.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val parentAttendance by viewModel.parentAttendance.collectAsStateWithLifecycle()
    val parentFees by viewModel.parentFees.collectAsStateWithLifecycle()
    val parentTransactions by viewModel.parentTransactions.collectAsStateWithLifecycle()
    val parentResults by viewModel.parentResults.collectAsStateWithLifecycle()
    val allSchedules by viewModel.allSchedules.collectAsStateWithLifecycle()
    val allNotices by viewModel.allNotices.collectAsStateWithLifecycle()
    val leaveRequests by viewModel.parentLeaveRequests.collectAsStateWithLifecycle()
    val viewingReceiptFee by viewModel.selectedFeeForReceipt.collectAsStateWithLifecycle()
    val viewingReceiptTx by viewModel.selectedTransactionForReceipt.collectAsStateWithLifecycle()

    var showLeaveDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Attendance, 1: Academic Progress, 2: Fees, 3: Timetable, 4: Leave

    if (currentStudent == null) {
        // Parent Login Screen
        ParentLoginView(
            allStudents = allStudents,
            onLogin = { query -> viewModel.loginParent(query) },
            onQuickSelect = { student -> viewModel.selectStudentForParent(student) }
        )
    } else {
        val student = currentStudent!!
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayAttendance = parentAttendance.find { it.date == todayStr }
        val totalDays = parentAttendance.size
        val presentDays = parentAttendance.count { it.status == "PRESENT" || it.status == "LATE" }
        val attendancePercent = if (totalDays > 0) (presentDays * 100) / totalDays else 100

        // Filter today's timetable for student
        val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(Date())
        val todaySchedule = allSchedules.filter {
            it.dayOfWeek.equals(dayName, ignoreCase = true) &&
            (it.batchId == student.batchId || it.standard == student.standard)
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Student Hero Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_vms_hero_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(AcademyBlue, Color(0xFF2563EB))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(1),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = student.name,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "Roll: ${student.rollNumber} • ${student.standard}",
                                    fontSize = 12.sp,
                                    color = AcademyBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Parent: ${student.parentName}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }

                        // Child switcher button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    val nextStudent = allStudents.firstOrNull { it.id != student.id }
                                    if (nextStudent != null) viewModel.selectStudentForParent(nextStudent)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.SwapHoriz, contentDescription = "Switch Child", modifier = Modifier.size(14.dp), tint = AcademyBlue)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Switch", fontSize = 11.sp, color = AcademyBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick VMS Status Indicators: Today's Status & Overall Rate
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Today's Status
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (todayAttendance?.status == "ABSENT") Icons.Default.Error else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = if (todayAttendance?.status == "ABSENT") AcademyCrimson else AcademyEmerald,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(text = "Today's Attendance", fontSize = 10.sp, color = Color(0xFF64748B))
                                Text(
                                    text = todayAttendance?.status ?: "PRESENT (On Time)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (todayAttendance?.status == "ABSENT") AcademyCrimson else AcademyEmerald
                                )
                            }
                        }

                        // Overall Rate
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Overall Attendance", fontSize = 10.sp, color = Color(0xFF64748B))
                                Text(
                                    text = "$attendancePercent%",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = AcademyBlue
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs inside Parent VMS
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = AcademyBlue,
                edgePadding = 8.dp,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("parent_vms_tabs")
            ) {
                listOf("Attendance", "Exam Progress", "Fees & Receipts", "Timetable", "Leave Request").forEachIndexed { idx, title ->
                    Tab(
                        selected = selectedTab == idx,
                        onClick = { selectedTab = idx },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == idx) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content according to selected tab
            when (selectedTab) {
                0 -> AttendanceMonitoringTab(parentAttendance)
                1 -> ExamProgressTab(parentResults)
                2 -> FeesTab(
                    fees = parentFees,
                    transactions = parentTransactions,
                    student = student,
                    onSelectReceiptFee = { viewModel.setFeeForReceipt(it) },
                    onSelectReceiptTx = { viewModel.setSelectedTransactionForReceipt(it) },
                    onPayBalance = { fee, amount ->
                        viewModel.recordFeePayment(fee.id, amount, "UPI", "UPI/ADV-VMS-${(1000..9999).random()}", "Cleared via Parent VMS")
                    }
                )
                3 -> TimetableTab(todaySchedule, dayName)
                4 -> LeaveRequestTab(
                    leaveRequests = leaveRequests,
                    onOpenDialog = { showLeaveDialog = true }
                )
            }
        }
    }

    // Leave Application Dialog
    if (showLeaveDialog) {
        LeaveApplicationDialog(
            onDismiss = { showLeaveDialog = false },
            onSubmit = { start, end, reason ->
                viewModel.submitLeaveRequest(start, end, reason)
                showLeaveDialog = false
            }
        )
    }

    // View Receipt Dialog for Fee Record inside Parent VMS
    if (viewingReceiptFee != null) {
        FeeReceiptDialog(
            fee = viewingReceiptFee!!,
            student = currentStudent,
            onDismiss = { viewModel.setFeeForReceipt(null) },
            onPayRemaining = { amount ->
                viewModel.recordFeePayment(viewingReceiptFee!!.id, amount, "UPI", "UPI/VMS-${(1000..9999).random()}", "Cleared via Parent VMS")
                viewModel.setFeeForReceipt(null)
            }
        )
    }

    // View Receipt Dialog for Transaction inside Parent VMS
    if (viewingReceiptTx != null) {
        FeeReceiptDialog(
            transaction = viewingReceiptTx!!,
            student = currentStudent,
            onDismiss = { viewModel.setSelectedTransactionForReceipt(null) },
            onPayRemaining = { amount ->
                val fee = parentFees.find { it.id == viewingReceiptTx!!.feeRecordId }
                if (fee != null) {
                    viewModel.recordFeePayment(fee.id, amount, "UPI", "UPI/VMS-${(1000..9999).random()}", "Cleared via Parent VMS")
                }
                viewModel.setSelectedTransactionForReceipt(null)
            }
        )
    }
}

@Composable
fun ParentLoginView(
    allStudents: List<Student>,
    onLogin: (String) -> Unit,
    onQuickSelect: (Student) -> Unit
) {
    var query by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(AcademyBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FamilyRestroom,
                contentDescription = null,
                tint = AcademyGold,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "PARENT VMS PORTAL",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = AcademyBlue,
            letterSpacing = 1.sp
        )
        Text(
            text = "Advance Academy Virtual Monitoring System",
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Student Roll Number or Mobile",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("e.g. AA-101 or 9825101001") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = { onLogin(query) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
                ) {
                    Icon(imageVector = Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Access VMS Portal", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Switch for Demo
        Text(
            text = "One-Tap Demo Parent Profiles",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B)
        )
        Spacer(modifier = Modifier.height(8.dp))

        allStudents.take(4).forEach { student ->
            Card(
                onClick = { onQuickSelect(student) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
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
                    Column {
                        Text(text = student.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Roll: ${student.rollNumber} • ${student.standard}", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                    Text(text = "Parent: ${student.parentName}", fontSize = 11.sp, color = AcademyBlue, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun AttendanceMonitoringTab(attendanceList: List<com.example.data.model.AttendanceRecord>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(attendanceList) { record ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when (record.status) {
                                        "PRESENT" -> AcademyEmerald
                                        "LATE" -> AcademyGold
                                        else -> AcademyCrimson
                                    }
                                )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = record.date, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = record.remarks, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (record.status) {
                            "PRESENT" -> AcademyEmerald.copy(alpha = 0.15f)
                            "LATE" -> AcademyGold.copy(alpha = 0.2f)
                            else -> AcademyCrimson.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = record.status,
                            color = when (record.status) {
                                "PRESENT" -> AcademyEmerald
                                "LATE" -> Color(0xFFB45309)
                                else -> AcademyCrimson
                            },
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ExamProgressTab(results: List<com.example.data.model.ExamResult>) {
    if (results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No test results recorded yet.", color = Color(0xFF64748B))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(results) { res ->
                val percentage = (res.marksObtained * 100) / res.totalMarks

                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Column {
                                Text(text = res.testTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${res.subject} • ${res.testDate}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }

                            // Rank badge
                            if (res.rankInBatch > 0) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = AcademyGold.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "Rank #${res.rankInBatch}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFB45309),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Score progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Score: ${res.marksObtained} / ${res.totalMarks}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "$percentage%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AcademyBlue)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        LinearProgressIndicator(
                            progress = { percentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (percentage >= 80) AcademyEmerald else AcademyBlue,
                            trackColor = Color(0xFFE2E8F0),
                            strokeCap = StrokeCap.Round
                        )

                        if (res.teacherRemarks.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Faculty Remarks: ${res.teacherRemarks}",
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
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

@Composable
fun FeesTab(
    fees: List<FeeRecord>,
    transactions: List<FeePaymentTransaction>,
    student: Student,
    onSelectReceiptFee: (FeeRecord) -> Unit,
    onSelectReceiptTx: (FeePaymentTransaction) -> Unit,
    onPayBalance: (FeeRecord, Double) -> Unit
) {
    var feeTabMode by remember { mutableIntStateOf(0) } // 0: Payment History, 1: Fee Invoices

    val totalAllocated = fees.sumOf { it.totalAmount }
    val totalPaid = fees.sumOf { it.paidAmount }
    val outstandingBalance = (totalAllocated - totalPaid).coerceAtLeast(0.0)
    val progress = if (totalAllocated > 0) (totalPaid / totalAllocated).toFloat() else 1f

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Child Outstanding Balance Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("parent_fee_overview_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (outstandingBalance > 0) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)
                ),
                border = CardDefaults.outlinedCardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    .background(if (outstandingBalance > 0) AcademyCrimson.copy(alpha = 0.15f) else AcademyEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = if (outstandingBalance > 0) AcademyCrimson else AcademyEmerald
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Current Outstanding Balance",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (outstandingBalance > 0) AcademyCrimson else AcademyEmerald
                                )
                                Text(
                                    text = if (outstandingBalance > 0) "₹${outstandingBalance.toInt()} Pending" else "All Dues Cleared",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (outstandingBalance > 0) AcademyCrimson else AcademyEmerald
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (outstandingBalance > 0) AcademyCrimson.copy(alpha = 0.15f) else AcademyEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (outstandingBalance > 0) "Dues Pending" else "Cleared",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (outstandingBalance > 0) AcademyCrimson else AcademyEmerald,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (progress >= 1f) AcademyEmerald else AcademyBlue,
                        trackColor = Color(0xFFCBD5E1),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Total Course Fee", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(text = "₹${totalAllocated.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Total Paid to Date", fontSize = 11.sp, color = Color(0xFF64748B))
                            Text(text = "₹${totalPaid.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AcademyEmerald)
                        }
                    }

                    if (outstandingBalance > 0) {
                        val pendingFee = fees.firstOrNull { (it.totalAmount - it.paidAmount) > 0 }
                        if (pendingFee != null) {
                            val feeBal = (pendingFee.totalAmount - pendingFee.paidAmount).coerceAtLeast(0.0)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onPayBalance(pendingFee, feeBal) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("parent_pay_balance_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = AcademyEmerald)
                            ) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Pay Dues via Online UPI (₹${feeBal.toInt()})", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Sub-tabs: Payment History vs Invoices
        item {
            TabRow(
                selectedTabIndex = feeTabMode,
                containerColor = Color.White,
                contentColor = AcademyBlue,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = feeTabMode == 0,
                    onClick = { feeTabMode = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Payment History (${transactions.size})", fontWeight = if (feeTabMode == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp)
                        }
                    }
                )
                Tab(
                    selected = feeTabMode == 1,
                    onClick = { feeTabMode = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fee Invoices (${fees.size})", fontWeight = if (feeTabMode == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp)
                        }
                    }
                )
            }
        }

        if (feeTabMode == 0) {
            // Payment History
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No payment transactions recorded yet.", color = Color(0xFF64748B), fontSize = 13.sp)
                    }
                }
            } else {
                items(transactions, key = { it.id }) { tx ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("parent_tx_${tx.id}"),
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
                                Column {
                                    Text(
                                        text = tx.feeTitle,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "${tx.paymentDate} • ${tx.paymentTime}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Text(
                                    text = "+₹${tx.amountPaid.toInt()}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = AcademyEmerald
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = when (tx.paymentMode) {
                                            "UPI" -> AcademyBlue.copy(alpha = 0.12f)
                                            "Cash" -> AcademyEmerald.copy(alpha = 0.12f)
                                            "Cheque" -> AcademyGold.copy(alpha = 0.2f)
                                            else -> Color(0xFF64748B).copy(alpha = 0.12f)
                                        }
                                    ) {
                                        Text(
                                            text = tx.paymentMode,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (tx.paymentMode) {
                                                "UPI" -> AcademyBlue
                                                "Cash" -> AcademyEmerald
                                                "Cheque" -> Color(0xFFB45309)
                                                else -> Color(0xFF334155)
                                            },
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "Rec: ${tx.receiptNumber}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF475569)
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onSelectReceiptTx(tx) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("Official Receipt", fontSize = 11.sp)
                                }
                            }

                            if (tx.remarks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tx.remarks,
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // Fee Invoices
            items(fees, key = { it.id }) { fee ->
                val balance = (fee.totalAmount - fee.paidAmount).coerceAtLeast(0.0)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = fee.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(
                                text = fee.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (fee.status == "PAID") AcademyEmerald else AcademyCrimson
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Due Date: ${fee.dueDate}", fontSize = 11.sp, color = Color(0xFF64748B))

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Total: ₹${fee.totalAmount.toInt()}", fontSize = 12.sp)
                            Text(text = "Paid: ₹${fee.paidAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AcademyEmerald)
                            Text(text = "Balance: ₹${balance.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (balance > 0) AcademyCrimson else AcademyEmerald)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { onSelectReceiptFee(fee) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View Official Receipt & Pay Balance")
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

@Composable
fun TimetableTab(scheduleList: List<com.example.data.model.ScheduleSlot>, dayName: String) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Today's Schedule ($dayName)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (scheduleList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No scheduled classes for today.", color = Color(0xFF64748B))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(scheduleList) { slot ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = slot.subject, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "Teacher: ${slot.teacherName} • Room: ${slot.roomNo}", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                            Text(text = "${slot.startTime} - ${slot.endTime}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AcademyBlue)
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

@Composable
fun LeaveRequestTab(
    leaveRequests: List<com.example.data.model.ParentLeaveRequest>,
    onOpenDialog: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onOpenDialog,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
        ) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Apply for Leave via VMS")
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Past Leave Requests (${leaveRequests.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(leaveRequests) { req ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${req.startDate} to ${req.endDate}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = req.status, fontSize = 11.sp, color = AcademyEmerald, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Reason: ${req.reason}", fontSize = 12.sp, color = Color(0xFF475569))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun LeaveApplicationDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit
) {
    var startDate by remember { mutableStateOf("2025-09-10") }
    var endDate by remember { mutableStateOf("2025-09-11") }
    var reason by remember { mutableStateOf("Medical appointment / family function") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Parent Leave Application", fontWeight = FontWeight.Bold, color = AcademyBlue) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("From Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("To Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Reason for Absence") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(startDate, endDate, reason) },
                colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue)
            ) {
                Text("Submit Application")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
