package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.FeePaymentTransaction
import com.example.data.model.FeeRecord
import com.example.data.model.Student
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyCrimson
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold

@Composable
fun FeeReceiptDialog(
    fee: FeeRecord? = null,
    transaction: FeePaymentTransaction? = null,
    student: Student?,
    onDismiss: () -> Unit,
    onPayRemaining: ((amount: Double) -> Unit)? = null
) {
    val totalAmount = fee?.totalAmount ?: ((transaction?.amountPaid ?: 0.0) + (transaction?.balanceAfterPayment ?: 0.0))
    val paidAmount = transaction?.amountPaid ?: fee?.paidAmount ?: 0.0
    val balanceDue = transaction?.balanceAfterPayment ?: fee?.let { (it.totalAmount - it.paidAmount).coerceAtLeast(0.0) } ?: 0.0
    val isFullyPaid = balanceDue == 0.0
    val receiptNumber = transaction?.receiptNumber?.ifEmpty { null } ?: fee?.receiptNumber?.ifEmpty { null } ?: "REC-AA-2025-GEN"
    val dateDisplay = if (transaction != null) "${transaction.paymentDate} (${transaction.paymentTime})" else (fee?.lastPaymentDate ?: fee?.dueDate ?: "Today")
    val titleDisplay = transaction?.feeTitle ?: fee?.title ?: "Tuition Fee"
    val modeDisplay = transaction?.paymentMode ?: fee?.paymentMode ?: "UPI"
    val refDisplay = transaction?.transactionRef ?: fee?.transactionRef ?: ""
    val remarksDisplay = transaction?.remarks ?: if (isFullyPaid) "Fees Cleared" else "Part Payment"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("fee_receipt_dialog"),
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
                // Top Action Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            tint = AcademyBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OFFICIAL FEE RECEIPT",
                            fontWeight = FontWeight.Bold,
                            color = AcademyBlue,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Academy Branding Header inside Receipt
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = AcademyBlue,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ADVANCE ACADEMY",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = AcademyBlue
                            )
                        }
                        Text(
                            text = "Coaching & Board Preparation Center",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Opp. Sardar Patel Park, GSEB Coaching Center",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFCBD5E1))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Receipt No: $receiptNumber",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF334155),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Date: $dateDisplay",
                                fontSize = 11.sp,
                                color = Color(0xFF334155)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Student Details
                Text(
                    text = "STUDENT PARTICULARS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        ReceiptRow("Student Name", student?.name ?: "Student")
                        ReceiptRow("Roll Number", student?.rollNumber ?: "AA-101")
                        ReceiptRow("Standard / Stream", student?.standard ?: "GSEB Board")
                        ReceiptRow("Parent / Guardian", student?.parentName ?: "Parent")
                        ReceiptRow("Parent Phone", student?.parentPhone ?: "")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fee Breakdown Table
                Text(
                    text = "FEE PARTICULARS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        ReceiptRow("Description", titleDisplay)
                        ReceiptRow("Total Fees Allocated", "₹${totalAmount.toInt()}")
                        ReceiptRow("Amount Paid", "₹${paidAmount.toInt()}", valueColor = AcademyEmerald, isBold = true)
                        ReceiptRow("Outstanding Balance", "₹${balanceDue.toInt()}", valueColor = if (balanceDue > 0) AcademyCrimson else AcademyEmerald, isBold = true)
                        if (modeDisplay.isNotEmpty()) {
                            ReceiptRow("Payment Mode", modeDisplay)
                        }
                        if (refDisplay.isNotEmpty()) {
                            ReceiptRow("Txn Reference", refDisplay)
                        }
                        if (remarksDisplay.isNotEmpty()) {
                            ReceiptRow("Status / Remarks", remarksDisplay)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Stamp
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = if (isFullyPaid) AcademyEmerald else if (fee?.status == "PARTIAL") AcademyGold else AcademyCrimson,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isFullyPaid) "★ FULLY PAID ★" else if (fee?.status == "PARTIAL") "★ PARTIAL PAYMENT ★" else "★ PENDING DUE ★",
                            color = if (isFullyPaid) AcademyEmerald else if (fee?.status == "PARTIAL") AcademyGold else AcademyCrimson,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }

                    if (!isFullyPaid && onPayRemaining != null) {
                        Button(
                            onClick = { onPayRemaining(balanceDue) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pay_fee_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = AcademyEmerald)
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pay ₹${balanceDue.toInt()}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = Color(0xFF1E293B),
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF64748B))
        Text(
            text = value,
            fontSize = 12.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}
