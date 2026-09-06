package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.StudentFeeSummary
import com.example.ui.components.FeeReceiptDialog
import com.example.ui.components.StudentFeeLedgerDialog
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold
import com.example.ui.viewmodel.AcademyViewModel

@Composable
fun FeesScreen(
    viewModel: AcademyViewModel,
    modifier: Modifier = Modifier
) {
    val fees by viewModel.allFees.collectAsStateWithLifecycle()
    val students by viewModel.allStudents.collectAsStateWithLifecycle()
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val studentSummaries by viewModel.studentFeeSummaries.collectAsStateWithLifecycle()

    val viewingReceiptFee by viewModel.selectedFeeForReceipt.collectAsStateWithLifecycle()
    val viewingReceiptTx by viewModel.selectedTransactionForReceipt.collectAsStateWithLifecycle()
    val viewingStudentDetail by viewModel.selectedStudentForFeeDetail.collectAsStateWithLifecycle()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Student Balances, 1: Payment History, 2: Fee Invoices
    var paymentDialogFee by remember { mutableStateOf<FeeRecord?>(null) }

    // Overall Financial Summary
    val totalBilled = fees.sumOf { it.totalAmount }
    val totalCollected = fees.sumOf { it.paidAmount }
    val totalOutstanding = (totalBilled - totalCollected).coerceAtLeast(0.0)
    val totalStudentsWithDues = studentSummaries.count { it.outstandingBalance > 0 }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Financial Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("fees_overview_card"),
            shape = RoundedCornerShape(16.dp),
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AcademyBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = AcademyBlue
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Fee Tracking & Balances",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Advance Academy Financial Ledger",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    if (totalStudentsWithDues > 0) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AcademyCrimson.copy(alpha = 0.12f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = AcademyCrimson,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "$totalStudentsWithDues with Dues",
                                    color = AcademyCrimson,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FeeSummaryColumn("Total Billed", "₹${totalBilled.toInt()}", Color(0xFF1E293B))
                    FeeSummaryColumn("Total Collected", "₹${totalCollected.toInt()}", AcademyEmerald)
                    FeeSummaryColumn(
                        "Outstanding Dues",
                        "₹${totalOutstanding.toInt()}",
                        if (totalOutstanding > 0) AcademyCrimson else AcademyEmerald
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main 3-Tab Selector
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = AcademyBlue,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .testTag("fees_main_tabs")
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.People, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Student Balances",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Payment History (${transactions.size})",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Fee Invoices (${fees.size})",
                            fontWeight = if (activeTab == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tab Content
        when (activeTab) {
            0 -> StudentBalancesView(
                studentSummaries = studentSummaries,
                fees = fees,
                onSelectStudentLedger = { student -> viewModel.setSelectedStudentForFeeDetail(student) },
                onRecordPaymentForFee = { fee -> paymentDialogFee = fee },
                onSendReminder = { student, bal -> viewModel.sendFeeReminderNotice(student, bal) }
            )
            1 -> PaymentHistoryView(
                transactions = transactions,
                students = students,
                onViewReceipt = { tx -> viewModel.setSelectedTransactionForReceipt(tx) }
            )
            2 -> FeeInvoicesView(
                fees = fees,
                students = students,
                onViewReceipt = { fee -> viewModel.setFeeForReceipt(fee) },
                onPayFee = { fee -> paymentDialogFee = fee }
            )
        }
    }

    // Payment Dialog
    if (paymentDialogFee != null) {
        val fee = paymentDialogFee!!
        val student = students.find { it.id == fee.studentId }
        val balance = (fee.totalAmount - fee.paidAmount).coerceAtLeast(0.0)

        RecordPaymentDialog(
            fee = fee,
            student = student,
            balanceDue = balance,
            onDismiss = { paymentDialogFee = null },
            onConfirm = { amount, mode, ref, remarks ->
                viewModel.recordFeePayment(fee.id, amount, mode, ref, remarks)
                paymentDialogFee = null
            }
        )
    }

    // Student Fee Ledger Dialog
    if (viewingStudentDetail != null) {
        val student = viewingStudentDetail!!
        val summary = studentSummaries.find { it.student.id == student.id }
        val studentFees = fees.filter { it.studentId == student.id }
        val studentTxs = transactions.filter { it.studentId == student.id }

        StudentFeeLedgerDialog(
            student = student,
            summary = summary,
            feeRecords = studentFees,
            transactions = studentTxs,
            onDismiss = { viewModel.setSelectedStudentForFeeDetail(null) },
            onRecordPaymentForFee = { fee ->
                viewModel.setSelectedStudentForFeeDetail(null)
                paymentDialogFee = fee
            },
            onViewReceiptForRecord = { fee ->
                viewModel.setFeeForReceipt(fee)
            },
            onViewReceiptForTransaction = { tx ->
                viewModel.setSelectedTransactionForReceipt(tx)
            },
            onSendReminder = { st, bal ->
                viewModel.sendFeeReminderNotice(st, bal)
            }
        )
    }

    // View Receipt for FeeRecord
    if (viewingReceiptFee != null) {
        val fee = viewingReceiptFee!!
        val student = students.find { it.id == fee.studentId }
        FeeReceiptDialog(
            fee = fee,
            student = student,
            onDismiss = { viewModel.setFeeForReceipt(null) },
            onPayRemaining = { _ ->
                paymentDialogFee = fee
                viewModel.setFeeForReceipt(null)
            }
        )
    }

    // View Receipt for specific FeePaymentTransaction
    if (viewingReceiptTx != null) {
        val tx = viewingReceiptTx!!
        val student = students.find { it.id == tx.studentId }
        FeeReceiptDialog(
            transaction = tx,
            student = student,
            onDismiss = { viewModel.setSelectedTransactionForReceipt(null) },
            onPayRemaining = { _ ->
                val fee = fees.find { it.id == tx.feeRecordId }
                if (fee != null) {
                    paymentDialogFee = fee
                }
                viewModel.setSelectedTransactionForReceipt(null)
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 0: Student Balances (Outstanding Balances per Student)
// -------------------------------------------------------------
@Composable
fun StudentBalancesView(
    studentSummaries: List<StudentFeeSummary>,
    fees: List<FeeRecord>,
    onSelectStudentLedger: (Student) -> Unit,
    onRecordPaymentForFee: (FeeRecord) -> Unit,
    onSendReminder: (Student, Double) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableIntStateOf(0) } // 0: All Students, 1: Outstanding Dues, 2: Cleared

    val filteredList = studentSummaries.filter { summary ->
        val matchesSearch = searchQuery.isBlank() ||
            summary.student.name.contains(searchQuery, ignoreCase = true) ||
            summary.student.rollNumber.contains(searchQuery, ignoreCase = true) ||
            summary.student.standard.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (filterMode) {
            1 -> summary.outstandingBalance > 0
            2 -> summary.outstandingBalance == 0.0 && summary.totalAllocated > 0
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by student name, roll number, or standard...", fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_student_balance_field"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Filter chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterMode == 0,
                onClick = { filterMode = 0 },
                label = { Text("All Students (${studentSummaries.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AcademyBlue,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = filterMode == 1,
                onClick = { filterMode = 1 },
                label = { Text("Dues Pending (${studentSummaries.count { it.outstandingBalance > 0 }})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AcademyCrimson,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = filterMode == 2,
                onClick = { filterMode = 2 },
                label = { Text("Fully Cleared (${studentSummaries.count { it.outstandingBalance == 0.0 && it.totalAllocated > 0 }})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AcademyEmerald,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No student balance records match your filter.", color = Color(0xFF64748B), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.student.id }) { summary ->
                    val student = summary.student
                    val progress = if (summary.totalAllocated > 0) (summary.totalPaid / summary.totalAllocated).toFloat() else 1f
                    val hasDues = summary.outstandingBalance > 0

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectStudentLedger(student) }
                            .testTag("student_balance_card_${student.id}"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = CardDefaults.outlinedCardBorder()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Top Row: Student info & Status
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val initials = student.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(if (hasDues) AcademyBlue else AcademyEmerald),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = student.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "${student.rollNumber} • ${student.standard}",
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (summary.status) {
                                        "CLEARED" -> AcademyEmerald.copy(alpha = 0.15f)
                                        "PARTIAL" -> AcademyGold.copy(alpha = 0.2f)
                                        else -> AcademyCrimson.copy(alpha = 0.15f)
                                    }
                                ) {
                                    Text(
                                        text = when (summary.status) {
                                            "CLEARED" -> "ALL CLEARED"
                                            "OVERDUE" -> "OVERDUE"
                                            "PARTIAL" -> "PARTIAL"
                                            else -> "PENDING"
                                        },
                                        color = when (summary.status) {
                                            "CLEARED" -> AcademyEmerald
                                            "PARTIAL" -> Color(0xFFB45309)
                                            else -> AcademyCrimson
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Outstanding Balance Highlight Box
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (hasDues) Color(0xFFFEF2F2) else Color(0xFFF0FDF4),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Current Outstanding Balance",
                                        fontSize = 11.sp,
                                        color = if (hasDues) AcademyCrimson else AcademyEmerald,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (hasDues) "₹${summary.outstandingBalance.toInt()}" else "₹0 (Fully Paid)",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (hasDues) AcademyCrimson else AcademyEmerald
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "Paid to Date", fontSize = 11.sp, color = Color(0xFF64748B))
                                    Text(
                                        text = "₹${summary.totalPaid.toInt()} / ₹${summary.totalAllocated.toInt()}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Progress bar
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(5.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (progress >= 1f) AcademyEmerald else AcademyBlue,
                                trackColor = Color(0xFFE2E8F0),
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Footer info & actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (summary.lastPaymentDate != null) "Last Paid: ${summary.lastPaymentDate} (${summary.transactionCount} txns)" else "No payments recorded",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (hasDues) {
                                        OutlinedButton(
                                            onClick = { onSendReminder(student, summary.outstandingBalance) },
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text("Remind", fontSize = 11.sp)
                                        }

                                        val pendingFee = fees.firstOrNull { it.studentId == student.id && (it.totalAmount - it.paidAmount) > 0 }
                                        if (pendingFee != null) {
                                            Button(
                                                onClick = { onRecordPaymentForFee(pendingFee) },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = AcademyEmerald)
                                            ) {
                                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Pay", fontSize = 11.sp)
                                            }
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onSelectStudentLedger(student) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("Ledger", fontSize = 11.sp)
                                    }
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

// -------------------------------------------------------------
// TAB 1: Payment History (Chronological Transaction History)
// -------------------------------------------------------------
@Composable
fun PaymentHistoryView(
    transactions: List<FeePaymentTransaction>,
    students: List<Student>,
    onViewReceipt: (FeePaymentTransaction) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("All") }

    val filteredTxs = transactions.filter { tx ->
        val student = students.find { it.id == tx.studentId }
        val matchesSearch = searchQuery.isBlank() ||
            tx.receiptNumber.contains(searchQuery, ignoreCase = true) ||
            tx.transactionRef.contains(searchQuery, ignoreCase = true) ||
            tx.feeTitle.contains(searchQuery, ignoreCase = true) ||
            (student != null && (student.name.contains(searchQuery, ignoreCase = true) || student.rollNumber.contains(searchQuery, ignoreCase = true)))

        val matchesMode = selectedMode == "All" || tx.paymentMode.equals(selectedMode, ignoreCase = true)

        matchesSearch && matchesMode
    }

    val totalCollected = filteredTxs.sumOf { it.amountPaid }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by receipt #, student name, roll number, or txn ref...", fontSize = 13.sp) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_payment_history_field"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mode Filters
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "UPI", "Cash", "NetBanking", "Cheque").forEach { mode ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { selectedMode = mode },
                    label = { Text(mode, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AcademyBlue,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Transaction Summary Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredTxs.size} Transactions Logged",
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Sum: ₹${totalCollected.toInt()}",
                fontSize = 13.sp,
                color = AcademyEmerald,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredTxs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No payment transactions found matching criteria.", color = Color(0xFF64748B), fontSize = 13.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredTxs, key = { it.id }) { tx ->
                    val student = students.find { it.id == tx.studentId }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tx_item_${tx.id}"),
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
                                        text = student?.name ?: "Student",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "${student?.rollNumber ?: ""} • ${student?.standard ?: ""}",
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
                            Text(
                                text = tx.feeTitle,
                                fontSize = 12.sp,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )

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
                                        text = tx.receiptNumber,
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFF475569),
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onViewReceipt(tx) },
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Receipt", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${tx.paymentDate} • ${tx.paymentTime}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "Balance after txn: ₹${tx.balanceAfterPayment.toInt()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (tx.balanceAfterPayment > 0) AcademyCrimson else AcademyEmerald
                                )
                            }

                            if (tx.remarks.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Note: ${tx.remarks}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
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
}

// -------------------------------------------------------------
// TAB 2: Fee Invoices (Course/Term Fees List)
// -------------------------------------------------------------
@Composable
fun FeeInvoicesView(
    fees: List<FeeRecord>,
    students: List<Student>,
    onViewReceipt: (FeeRecord) -> Unit,
    onPayFee: (FeeRecord) -> Unit
) {
    var selectedFilter by remember { mutableIntStateOf(0) } // 0: All, 1: Pending/Partial, 2: Paid

    val filteredFees = when (selectedFilter) {
        1 -> fees.filter { it.status != "PAID" }
        2 -> fees.filter { it.status == "PAID" }
        else -> fees
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == 0,
                onClick = { selectedFilter = 0 },
                label = { Text("All Invoices (${fees.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AcademyBlue,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedFilter == 1,
                onClick = { selectedFilter = 1 },
                label = { Text("Pending / Dues", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AcademyBlue,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selectedFilter == 2,
                onClick = { selectedFilter = 2 },
                label = { Text("Fully Paid", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AcademyBlue,
                    selectedLabelColor = Color.White
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredFees, key = { it.id }) { fee ->
                val student = students.find { it.id == fee.studentId }
                val balance = (fee.totalAmount - fee.paidAmount).coerceAtLeast(0.0)
                val progress = if (fee.totalAmount > 0) (fee.paidAmount / fee.totalAmount).toFloat() else 1f

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fee_card_${fee.id}"),
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
                                    text = student?.name ?: "Student",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${student?.rollNumber ?: ""} • ${student?.standard ?: ""}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = when (fee.status) {
                                    "PAID" -> AcademyEmerald.copy(alpha = 0.15f)
                                    "PARTIAL" -> AcademyGold.copy(alpha = 0.2f)
                                    else -> AcademyCrimson.copy(alpha = 0.15f)
                                }
                            ) {
                                Text(
                                    text = fee.status,
                                    color = when (fee.status) {
                                        "PAID" -> AcademyEmerald
                                        "PARTIAL" -> Color(0xFFB45309)
                                        else -> AcademyCrimson
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = fee.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "Due Date: ${fee.dueDate}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress >= 1f) AcademyEmerald else AcademyBlue,
                            trackColor = Color(0xFFE2E8F0),
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Paid: ₹${fee.paidAmount.toInt()} / ₹${fee.totalAmount.toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                            if (balance > 0) {
                                Text(
                                    text = "Outstanding Due: ₹${balance.toInt()}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademyCrimson
                                )
                            } else {
                                Text(
                                    text = "Cleared",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AcademyEmerald
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onViewReceipt(fee) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Receipt", fontSize = 12.sp)
                            }

                            if (balance > 0) {
                                Button(
                                    onClick = { onPayFee(fee) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = AcademyEmerald)
                                ) {
                                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Record Pay", fontSize = 12.sp)
                                }
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

@Composable
fun FeeSummaryColumn(label: String, value: String, valueColor: Color) {
    Column {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = valueColor)
    }
}

@Composable
fun RecordPaymentDialog(
    fee: FeeRecord,
    student: Student?,
    balanceDue: Double,
    onDismiss: () -> Unit,
    onConfirm: (amount: Double, mode: String, ref: String, remarks: String) -> Unit
) {
    var amountText by remember { mutableStateOf(balanceDue.toInt().toString()) }
    var selectedMode by remember { mutableStateOf("UPI") }
    var referenceText by remember { mutableStateOf("UPI/ADV-${(1000..9999).random()}") }
    var remarksText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Fee Payment", fontWeight = FontWeight.Bold, color = AcademyBlue) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Student: ${student?.name ?: ""} (${student?.rollNumber ?: ""})",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Outstanding Balance: ₹${balanceDue.toInt()}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademyCrimson
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount to Pay (₹)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Mode Selector
                Text(text = "Payment Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("UPI", "Cash", "NetBanking", "Cheque").forEach { mode ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode },
                            label = { Text(mode, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AcademyBlue,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = referenceText,
                    onValueChange = { referenceText = it },
                    label = { Text("Transaction Reference") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = remarksText,
                    onValueChange = { remarksText = it },
                    label = { Text("Payment Note / Remarks (Optional)") },
                    placeholder = { Text("e.g. Cleared via Google Pay") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount, selectedMode, referenceText, remarksText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AcademyEmerald)
            ) {
                Text("Confirm Payment")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
