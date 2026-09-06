package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rollNumber: String,
    val name: String,
    val standard: String, // e.g. "Std 10 GSEB", "Std 12 Science", "Std 11 Commerce", "Std 9 GSEB"
    val batchId: Long,
    val parentName: String,
    val parentPhone: String,
    val parentEmail: String = "",
    val enrollmentDate: String = "2025-06-15",
    val bloodGroup: String = "B+",
    val avatarColorSeed: Int = 0
)

@Entity(tableName = "batches")
data class Batch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val standard: String,
    val medium: String, // "Gujarati" or "English"
    val timingDescription: String,
    val classroom: String
)

@Entity(tableName = "attendance")
data class AttendanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val batchId: Long,
    val date: String, // "YYYY-MM-DD"
    val status: String, // "PRESENT", "ABSENT", "LATE"
    val remarks: String = "",
    val markedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "schedules")
data class ScheduleSlot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val batchId: Long,
    val standard: String,
    val subject: String,
    val teacherName: String,
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val startTime: String, // "04:30 PM"
    val endTime: String,   // "05:45 PM"
    val roomNo: String
)

@Entity(tableName = "fees")
data class FeeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val title: String, // e.g. "Term 1 Tuition Fee", "GSEB Board Test Series"
    val totalAmount: Double,
    val paidAmount: Double,
    val dueDate: String,
    val lastPaymentDate: String? = null,
    val paymentMode: String = "UPI", // "UPI", "Cash", "Cheque", "NetBanking"
    val transactionRef: String = "",
    val receiptNumber: String = "",
    val status: String // "PAID", "PARTIAL", "PENDING", "OVERDUE"
)

@Entity(tableName = "fee_transactions")
data class FeePaymentTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val feeRecordId: Long,
    val studentId: Long,
    val feeTitle: String,
    val amountPaid: Double,
    val paymentDate: String, // e.g. "2025-07-05"
    val paymentTime: String = "10:30 AM",
    val paymentMode: String = "UPI", // "UPI", "Cash", "Cheque", "NetBanking"
    val transactionRef: String = "",
    val receiptNumber: String = "",
    val balanceAfterPayment: Double = 0.0,
    val remarks: String = "",
    val recordedBy: String = "Admin Faculty"
)

data class StudentFeeSummary(
    val student: Student,
    val feeRecords: List<FeeRecord>,
    val totalAllocated: Double,
    val totalPaid: Double,
    val outstandingBalance: Double,
    val status: String, // "CLEARED", "PARTIAL", "PENDING", "OVERDUE"
    val overdueCount: Int,
    val lastPaymentDate: String?,
    val transactionCount: Int
)

@Entity(tableName = "exam_results")
data class ExamResult(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val testTitle: String,
    val subject: String,
    val marksObtained: Int,
    val totalMarks: Int,
    val testDate: String,
    val rankInBatch: Int = 1,
    val teacherRemarks: String = "Excellent effort"
)

@Entity(tableName = "class_notices")
data class ClassNotice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val category: String, // "ATTENDANCE", "FEES", "EXAM", "SCHEDULE", "HOLIDAY", "GENERAL"
    val priority: String = "NORMAL", // "NORMAL", "HIGH", "URGENT"
    val targetStandard: String = "All",
    val datePosted: String,
    val isReadByParent: Boolean = false
)

@Entity(tableName = "gseb_papers")
data class GSEBGeneratedPaper(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val standard: String,
    val subject: String,
    val medium: String, // "Gujarati" or "English"
    val totalMarks: Int,
    val durationMinutes: Int,
    val createdDate: String,
    val generalInstructions: String,
    val sectionsJson: String,
    val answerKey: String
)

@Entity(tableName = "leave_requests")
data class ParentLeaveRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val startDate: String,
    val endDate: String,
    val reason: String,
    val status: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val requestDate: String
)
