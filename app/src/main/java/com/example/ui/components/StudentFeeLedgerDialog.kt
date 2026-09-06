package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FeePaymentTransaction
import com.example.data.model.FeeRecord
import com.example.data.model.Student
import com.example.data.model.StudentFeeSummary
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold

@Composable
fun StudentFeeLedgerDialog(
    student: Student,
    summary: StudentFeeSummary?,
    feeRecords: List<FeeRecord>,
    transactions: List<FeePaymentTransaction>,
    onDismiss: () -> Unit,
    onRecordPaymentForFee: (FeeRecord) -> Unit,
    onViewReceiptForRecord: (FeeRecord) -> Unit,
    onViewReceiptForTransaction: (FeePaymentTransaction) -> Unit,
    onSendReminder: (Student, Double) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Payment History, 1: Fee Accounts

    val totalAllocated = summary?.totalAllocated ?: feeRecords.sumOf { it.totalAmount }
    val totalPaid = summary?.totalPaid ?: feeRecords.sumOf { it.paidAmount }
    val outstandingBalance = (totalAllocated - totalPaid).coerceAtLeast(0.0)
    val progress = if (totalAllocated > 0) (totalPaid / totalAllocated).toFloat() else 1f

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 20.dp)
                .testTag("student_fee_ledger_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
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
                            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = null, tint = AcademyBlue)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Student Fee Ledger",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF0F172A)
                            )
                            Text(
                                text = "Payment History & Dues",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Student Identity Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val initials = student.name.split(" ").take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AcademyBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = student.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                            Text(
                                text = "${student.rollNumber} • ${student.standard}",
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF64748B))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${student.parentName} (${student.parentPhone})",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Outstanding Balance Highlight Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (outstandingBalance > 0) Color(0xFFFEF2F2) else Color(0xFFECFDF5)
                    ),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "CURRENT OUTSTANDING BALANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (outstandingBalance > 0) AcademyCrimson else AcademyEmerald,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (outstandingBalance > 0) AcademyCrimson.copy(alpha = 0.15f) else AcademyEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (outstandingBalance > 0) "Dues Pending" else "Fully Cleared",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (outstandingBalance > 0) AcademyCrimson else AcademyEmerald,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "₹${outstandingBalance.toInt()}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = if (outstandingBalance > 0) AcademyCrimson else AcademyEmerald
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (progress >= 1f) AcademyEmerald else AcademyBlue,
                            trackColor = Color(0xFFCBD5E1)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Total Allocated", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(text = "₹${totalAllocated.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Total Paid to Date", fontSize = 11.sp, color = Color(0xFF64748B))
                                Text(text = "₹${totalPaid.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AcademyEmerald)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Bar for Quick Reminder / Payment
                if (outstandingBalance > 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onSendReminder(student, outstandingBalance) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("send_reminder_btn")
                        ) {
                            Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send Reminder", fontSize = 11.sp)
                        }

                        val pendingFee = feeRecords.firstOrNull { (it.totalAmount - it.paidAmount) > 0 }
                        if (pendingFee != null) {
                            Button(
                                onClick = { onRecordPaymentForFee(pendingFee) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("record_pay_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = AcademyEmerald)
                            ) {
                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Record Pay", fontSize = 11.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Tabs: Payment History vs Fee Accounts
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF1F5F9),
                    contentColor = AcademyBlue,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Payment History (${transactions.size})", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Fee Invoices (${feeRecords.size})", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium, fontSize = 12.sp)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab 0: Payment History
                if (selectedTab == 0) {
                    if (transactions.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No payment transactions recorded yet for this student.", color = Color(0xFF64748B), fontSize = 13.sp)
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            transactions.forEach { tx ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("tx_card_${tx.id}"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                    border = CardDefaults.outlinedCardBorder()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
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
                                                    color = Color(0xFF1E293B)
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
                                                fontSize = 15.sp,
                                                color = AcademyEmerald
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        HorizontalDivider(color = Color(0xFFE2E8F0))
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
                                                onClick = { onViewReceiptForTransaction(tx) },
                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Receipt", fontSize = 11.sp)
                                            }
                                        }

                                        if (tx.transactionRef.isNotEmpty() || tx.remarks.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = if (tx.remarks.isNotEmpty()) "${tx.remarks} • Ref: ${tx.transactionRef}" else "Ref: ${tx.transactionRef}",
                                                fontSize = 10.sp,
                                                color = Color(0xFF64748B)
                                            )
                                        }

                                        Text(
                                            text = "Balance after transaction: ₹${tx.balanceAfterPayment.toInt()}",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (tx.balanceAfterPayment > 0) AcademyCrimson else AcademyEmerald
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Tab 1: Fee Accounts / Invoices
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        feeRecords.forEach { fee ->
                            val balance = (fee.totalAmount - fee.paidAmount).coerceAtLeast(0.0)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("fee_detail_card_${fee.id}"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = CardDefaults.outlinedCardBorder()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = fee.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when (fee.status) {
                                                "PAID" -> AcademyEmerald.copy(alpha = 0.15f)
                                                "PARTIAL" -> AcademyGold.copy(alpha = 0.2f)
                                                else -> AcademyCrimson.copy(alpha = 0.15f)
                                            }
                                        ) {
                                            Text(
                                                text = fee.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (fee.status) {
                                                    "PAID" -> AcademyEmerald
                                                    "PARTIAL" -> Color(0xFFB45309)
                                                    else -> AcademyCrimson
                                                },
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Due Date: ${fee.dueDate}", fontSize = 11.sp, color = Color(0xFF64748B))

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = "Total: ₹${fee.totalAmount.toInt()}", fontSize = 12.sp)
                                        Text(text = "Paid: ₹${fee.paidAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AcademyEmerald)
                                        Text(
                                            text = "Due: ₹${balance.toInt()}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (balance > 0) AcademyCrimson else AcademyEmerald
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedButton(
                                            onClick = { onViewReceiptForRecord(fee) },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(imageVector = Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Invoice Receipt", fontSize = 11.sp)
                                        }

                                        if (balance > 0) {
                                            Button(
                                                onClick = { onRecordPaymentForFee(fee) },
                                                modifier = Modifier.weight(1f),
                                                colors = ButtonDefaults.buttonColors(containerColor = AcademyEmerald)
                                            ) {
                                                Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Pay ₹${balance.toInt()}", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Close
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close Ledger")
                }
            }
        }
    }
}
