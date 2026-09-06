package com.example.data.repository

import com.example.data.dao.AcademyDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.Batch
import com.example.data.model.ClassNotice
import com.example.data.model.ExamResult
import com.example.data.model.FeePaymentTransaction
import com.example.data.model.FeeRecord
import com.example.data.model.GSEBGeneratedPaper
import com.example.data.model.ParentLeaveRequest
import com.example.data.model.ScheduleSlot
import com.example.data.model.Student
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AcademyRepository(private val dao: AcademyDao) {

    val allStudents: Flow<List<Student>> = dao.getAllStudents()
    val allBatches: Flow<List<Batch>> = dao.getAllBatches()
    val allSchedules: Flow<List<ScheduleSlot>> = dao.getAllSchedules()
    val allFees: Flow<List<FeeRecord>> = dao.getAllFees()
    val allTransactions: Flow<List<FeePaymentTransaction>> = dao.getAllTransactions()
    val allNotices: Flow<List<ClassNotice>> = dao.getAllNotices()
    val allPapers: Flow<List<GSEBGeneratedPaper>> = dao.getAllPapers()

    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForDate(date)

    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceRecord>> =
        dao.getAttendanceForStudent(studentId)

    fun getFeesForStudent(studentId: Long): Flow<List<FeeRecord>> =
        dao.getFeesForStudent(studentId)

    fun getTransactionsForStudent(studentId: Long): Flow<List<FeePaymentTransaction>> =
        dao.getTransactionsForStudent(studentId)

    fun getResultsForStudent(studentId: Long): Flow<List<ExamResult>> =
        dao.getResultsForStudent(studentId)

    fun getNoticesForStandard(standard: String): Flow<List<ClassNotice>> =
        dao.getNoticesForStandard(standard)

    fun getLeaveRequestsForStudent(studentId: Long): Flow<List<ParentLeaveRequest>> =
        dao.getLeaveRequestsForStudent(studentId)

    suspend fun saveAttendance(
        records: List<AttendanceRecord>,
        triggerAbsentAlerts: Boolean = true
    ) {
        dao.insertAttendanceList(records)

        // Trigger automated notification for absent students
        if (triggerAbsentAlerts) {
            val absentRecords = records.filter { it.status == "ABSENT" }
            if (absentRecords.isNotEmpty()) {
                val todayStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
                dao.insertNotice(
                    ClassNotice(
                        title = "Attendance Alert: Student Absent",
                        message = "${absentRecords.size} student(s) marked absent today. Automated SMS/VMS alert dispatched to parents.",
                        category = "ATTENDANCE",
                        priority = "HIGH",
                        targetStandard = "All",
                        datePosted = todayStr
                    )
                )
            }
        }
    }

    suspend fun addStudent(student: Student): Long = dao.insertStudent(student)

    suspend fun addScheduleSlot(slot: ScheduleSlot): Long {
        val id = dao.insertSchedule(slot)
        dao.insertNotice(
            ClassNotice(
                title = "Schedule Updated: ${slot.subject}",
                message = "New class scheduled for ${slot.standard} on ${slot.dayOfWeek} at ${slot.startTime} in ${slot.roomNo}.",
                category = "SCHEDULE",
                priority = "NORMAL",
                targetStandard = slot.standard,
                datePosted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
            )
        )
        return id
    }

    suspend fun deleteScheduleSlot(slot: ScheduleSlot) = dao.deleteSchedule(slot)

    suspend fun recordFeePayment(
        feeId: Long,
        amountPaying: Double,
        mode: String,
        transactionRef: String,
        remarks: String = ""
    ) {
        val fees = allFees.firstOrNull() ?: emptyList()
        val fee = fees.find { it.id == feeId } ?: return

        val newPaid = (fee.paidAmount + amountPaying).coerceAtMost(fee.totalAmount)
        val newStatus = if (newPaid >= fee.totalAmount) "PAID" else "PARTIAL"
        val remainingBalance = (fee.totalAmount - newPaid).coerceAtLeast(0.0)
        val txReceiptNumber = "REC-AA-2025-${(1000..9999).random()}"
        val finalReceiptNumber = if (fee.receiptNumber.isEmpty()) txReceiptNumber else fee.receiptNumber

        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        val ref = if (transactionRef.isNotBlank()) transactionRef else "${mode.uppercase()}/TXN${(10000..99999).random()}"

        val updatedFee = fee.copy(
            paidAmount = newPaid,
            status = newStatus,
            lastPaymentDate = dateStr,
            paymentMode = mode,
            transactionRef = ref,
            receiptNumber = finalReceiptNumber
        )
        dao.updateFee(updatedFee)

        // Record transaction in ledger history
        dao.insertTransaction(
            FeePaymentTransaction(
                feeRecordId = fee.id,
                studentId = fee.studentId,
                feeTitle = fee.title,
                amountPaid = amountPaying,
                paymentDate = dateStr,
                paymentTime = timeStr,
                paymentMode = mode,
                transactionRef = ref,
                receiptNumber = txReceiptNumber,
                balanceAfterPayment = remainingBalance,
                remarks = if (remarks.isNotBlank()) remarks else if (newStatus == "PAID") "Full balance cleared" else "Partial installment payment",
                recordedBy = "Accounts Desk"
            )
        )

        // Automated notification
        val todayStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
        dao.insertNotice(
            ClassNotice(
                title = "Fee Payment Confirmed: ₹${amountPaying.toInt()}",
                message = "Official Advance Academy receipt $txReceiptNumber generated. Mode: $mode. Current balance: ₹${remainingBalance.toInt()}.",
                category = "FEES",
                priority = "HIGH",
                targetStandard = "All",
                datePosted = todayStr
            )
        )
    }

    suspend fun postNotice(notice: ClassNotice): Long = dao.insertNotice(notice)

    suspend fun savePaper(paper: GSEBGeneratedPaper): Long {
        val id = dao.insertPaper(paper)
        val todayStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
        dao.insertNotice(
            ClassNotice(
                title = "New Paper Generated: ${paper.subject}",
                message = "A new GSEB Blueprint exam paper '${paper.title}' (${paper.totalMarks} Marks) has been created for ${paper.standard}.",
                category = "EXAM",
                priority = "HIGH",
                targetStandard = paper.standard,
                datePosted = todayStr
            )
        )
        return id
    }

    suspend fun deletePaper(paper: GSEBGeneratedPaper) = dao.deletePaper(paper)

    suspend fun submitLeaveRequest(request: ParentLeaveRequest): Long {
        val id = dao.insertLeaveRequest(request)
        val todayStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
        dao.insertNotice(
            ClassNotice(
                title = "Leave Request Submitted (VMS)",
                message = "Parent submitted leave request from ${request.startDate} to ${request.endDate}. Reason: ${request.reason}.",
                category = "ATTENDANCE",
                priority = "NORMAL",
                targetStandard = "All",
                datePosted = todayStr
            )
        )
        return id
    }

    suspend fun markNoticeAsRead(noticeId: Long) = dao.markNoticeAsRead(noticeId)
}
