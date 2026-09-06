package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
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
import com.example.data.model.StudentFeeSummary
import com.example.data.repository.AcademyRepository
import com.example.data.sample.SampleDataLoader
import com.example.domain.gseb.GSEBQuestionBank
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppRole {
    ADMIN_FACULTY,
    PARENT_VMS
}

class AcademyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AcademyRepository

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayDateString: String = dateFormat.format(Date())

    // Role state
    private val _currentRole = MutableStateFlow(AppRole.ADMIN_FACULTY)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    // Parent Login state (Student profile)
    private val _loggedInStudent = MutableStateFlow<Student?>(null)
    val loggedInStudent: StateFlow<Student?> = _loggedInStudent.asStateFlow()

    // Date & Batch selection for Attendance
    private val _selectedDate = MutableStateFlow(todayDateString)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedBatchId = MutableStateFlow(0L)
    val selectedBatchId: StateFlow<Long> = _selectedBatchId.asStateFlow()

    // In-memory attendance map for the current active editing session
    private val _activeAttendanceMap = MutableStateFlow<Map<Long, String>>(emptyMap())
    val activeAttendanceMap: StateFlow<Map<Long, String>> = _activeAttendanceMap.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Selected paper for viewing
    private val _viewingPaper = MutableStateFlow<GSEBGeneratedPaper?>(null)
    val viewingPaper: StateFlow<GSEBGeneratedPaper?> = _viewingPaper.asStateFlow()

    // Selected fee for receipt viewing / paying
    private val _selectedFeeForReceipt = MutableStateFlow<FeeRecord?>(null)
    val selectedFeeForReceipt: StateFlow<FeeRecord?> = _selectedFeeForReceipt.asStateFlow()

    // Selected transaction for receipt viewing
    private val _selectedTransactionForReceipt = MutableStateFlow<FeePaymentTransaction?>(null)
    val selectedTransactionForReceipt: StateFlow<FeePaymentTransaction?> = _selectedTransactionForReceipt.asStateFlow()

    // Selected student for detailed fee tracking & history view
    private val _selectedStudentForFeeDetail = MutableStateFlow<Student?>(null)
    val selectedStudentForFeeDetail: StateFlow<Student?> = _selectedStudentForFeeDetail.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AcademyRepository(database.academyDao())

        // Populate sample data on first run
        viewModelScope.launch {
            SampleDataLoader.populateInitialDataIfEmpty(database.academyDao())
            // Set default logged in student for immediate convenient parent preview
            val students = repository.allStudents.firstOrNull() ?: emptyList()
            if (_loggedInStudent.value == null && students.isNotEmpty()) {
                _loggedInStudent.value = students.first()
            }
        }
    }

    val allStudents: StateFlow<List<Student>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBatches: StateFlow<List<Batch>> = repository.allBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSchedules: StateFlow<List<ScheduleSlot>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFees: StateFlow<List<FeeRecord>> = repository.allFees
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTransactions: StateFlow<List<FeePaymentTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotices: StateFlow<List<ClassNotice>> = repository.allNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPapers: StateFlow<List<GSEBGeneratedPaper>> = repository.allPapers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Fee & Balance Summaries (Real-time computed ledger per student)
    val studentFeeSummaries: StateFlow<List<StudentFeeSummary>> = combine(
        allStudents,
        allFees,
        allTransactions
    ) { students, fees, transactions ->
        students.map { student ->
            val studentFees = fees.filter { it.studentId == student.id }
            val studentTxs = transactions.filter { it.studentId == student.id }
            val totalAllocated = studentFees.sumOf { it.totalAmount }
            val totalPaid = studentFees.sumOf { it.paidAmount }
            val outstanding = (totalAllocated - totalPaid).coerceAtLeast(0.0)
            val overdueCount = studentFees.count { it.status == "OVERDUE" }
            val hasPartial = studentFees.any { it.status == "PARTIAL" }
            val status = when {
                overdueCount > 0 -> "OVERDUE"
                outstanding == 0.0 && totalAllocated > 0 -> "CLEARED"
                hasPartial -> "PARTIAL"
                outstanding > 0 -> "PENDING"
                else -> "CLEARED"
            }
            val lastPayment = studentTxs.maxByOrNull { it.id }?.paymentDate
                ?: studentFees.mapNotNull { it.lastPaymentDate }.maxOrNull()

            StudentFeeSummary(
                student = student,
                feeRecords = studentFees,
                totalAllocated = totalAllocated,
                totalPaid = totalPaid,
                outstandingBalance = outstanding,
                status = status,
                overdueCount = overdueCount,
                lastPaymentDate = lastPayment,
                transactionCount = studentTxs.size
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Date-specific attendance records
    val dateAttendance: StateFlow<List<AttendanceRecord>> = _selectedDate
        .flatMapLatest { date -> repository.getAttendanceForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Parent VMS Dynamic flows
    val parentAttendance: StateFlow<List<AttendanceRecord>> = _loggedInStudent
        .flatMapLatest { student ->
            if (student == null) flowOf(emptyList()) else repository.getAttendanceForStudent(student.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parentFees: StateFlow<List<FeeRecord>> = _loggedInStudent
        .flatMapLatest { student ->
            if (student == null) flowOf(emptyList()) else repository.getFeesForStudent(student.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parentTransactions: StateFlow<List<FeePaymentTransaction>> = _loggedInStudent
        .flatMapLatest { student ->
            if (student == null) flowOf(emptyList()) else repository.getTransactionsForStudent(student.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parentResults: StateFlow<List<ExamResult>> = _loggedInStudent
        .flatMapLatest { student ->
            if (student == null) flowOf(emptyList()) else repository.getResultsForStudent(student.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val parentLeaveRequests: StateFlow<List<ParentLeaveRequest>> = _loggedInStudent
        .flatMapLatest { student ->
            if (student == null) flowOf(emptyList()) else repository.getLeaveRequestsForStudent(student.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Sync active attendance map whenever date or batch changes or DB updates
    init {
        viewModelScope.launch {
            combine(allStudents, dateAttendance) { students, records ->
                val map = mutableMapOf<Long, String>()
                students.forEach { s ->
                    val existing = records.find { it.studentId == s.id }
                    map[s.id] = existing?.status ?: "PRESENT"
                }
                map
            }.collect { map ->
                if (_activeAttendanceMap.value.isEmpty() || _activeAttendanceMap.value.size != map.size) {
                    _activeAttendanceMap.value = map
                }
            }
        }
    }

    // Role switcher
    fun setRole(role: AppRole) {
        _currentRole.value = role
    }

    // Parent login
    fun loginParent(identifier: String): Boolean {
        val clean = identifier.trim()
        val found = allStudents.value.find { s ->
            s.rollNumber.equals(clean, ignoreCase = true) ||
            s.parentPhone == clean ||
            s.name.contains(clean, ignoreCase = true)
        }
        return if (found != null) {
            _loggedInStudent.value = found
            _currentRole.value = AppRole.PARENT_VMS
            _userMessage.value = "Welcome ${found.parentName}! Accessing ${found.name}'s VMS portal."
            true
        } else {
            _userMessage.value = "No student found with Roll/Phone: '$identifier'"
            false
        }
    }

    fun selectStudentForParent(student: Student) {
        _loggedInStudent.value = student
        _currentRole.value = AppRole.PARENT_VMS
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
        // Refresh active attendance map for this date
        viewModelScope.launch {
            val records = repository.getAttendanceForDate(date).firstOrNull() ?: emptyList()
            val map = mutableMapOf<Long, String>()
            allStudents.value.forEach { s ->
                val existing = records.find { it.studentId == s.id }
                map[s.id] = existing?.status ?: "PRESENT"
            }
            _activeAttendanceMap.value = map
        }
    }

    fun setSelectedBatch(batchId: Long) {
        _selectedBatchId.value = batchId
    }

    fun setStudentAttendanceStatus(studentId: Long, status: String) {
        val current = _activeAttendanceMap.value.toMutableMap()
        current[studentId] = status
        _activeAttendanceMap.value = current
    }

    fun markAllPresent(studentList: List<Student>) {
        val current = _activeAttendanceMap.value.toMutableMap()
        studentList.forEach { s ->
            current[s.id] = "PRESENT"
        }
        _activeAttendanceMap.value = current
        _userMessage.value = "Marked ${studentList.size} students Present"
    }

    fun saveAttendance() {
        viewModelScope.launch {
            val date = _selectedDate.value
            val map = _activeAttendanceMap.value
            val recordsToSave = allStudents.value.map { s ->
                val status = map[s.id] ?: "PRESENT"
                AttendanceRecord(
                    studentId = s.id,
                    batchId = s.batchId,
                    date = date,
                    status = status,
                    remarks = if (status == "LATE") "Marked late" else if (status == "ABSENT") "Not reported" else "Regular",
                    markedAt = System.currentTimeMillis()
                )
            }
            repository.saveAttendance(recordsToSave, triggerAbsentAlerts = true)
            _userMessage.value = "Attendance for $date saved & parents notified!"
        }
    }

    fun addSchedule(
        batchId: Long,
        standard: String,
        subject: String,
        teacher: String,
        day: String,
        start: String,
        end: String,
        room: String
    ) {
        viewModelScope.launch {
            repository.addScheduleSlot(
                ScheduleSlot(
                    batchId = batchId,
                    standard = standard,
                    subject = subject,
                    teacherName = teacher,
                    dayOfWeek = day,
                    startTime = start,
                    endTime = end,
                    roomNo = room
                )
            )
            _userMessage.value = "Class scheduled for $standard ($day)!"
        }
    }

    fun deleteSchedule(slot: ScheduleSlot) {
        viewModelScope.launch {
            repository.deleteScheduleSlot(slot)
            _userMessage.value = "Schedule slot removed."
        }
    }

    fun recordFeePayment(
        feeId: Long,
        amount: Double,
        mode: String,
        ref: String,
        remarks: String = ""
    ) {
        viewModelScope.launch {
            repository.recordFeePayment(feeId, amount, mode, ref, remarks)
            _userMessage.value = "Payment of ₹${amount.toInt()} recorded & logged to payment history!"
        }
    }

    fun setSelectedTransactionForReceipt(tx: FeePaymentTransaction?) {
        _selectedTransactionForReceipt.value = tx
    }

    fun setSelectedStudentForFeeDetail(student: Student?) {
        _selectedStudentForFeeDetail.value = student
    }

    fun sendFeeReminderNotice(student: Student, balance: Double) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())
            repository.postNotice(
                ClassNotice(
                    title = "Fee Balance Reminder: ${student.name}",
                    message = "Dear ${student.parentName}, outstanding tuition balance of ₹${balance.toInt()} is pending for ${student.name} (${student.rollNumber} • ${student.standard}). Kindly clear dues via online UPI or academy desk.",
                    category = "FEES",
                    priority = "HIGH",
                    targetStandard = student.standard,
                    datePosted = todayStr
                )
            )
            _userMessage.value = "Fee reminder dispatched to ${student.parentName} (${student.parentPhone})!"
        }
    }

    fun generateGSEBPaper(
        standard: String,
        subject: String,
        medium: String,
        totalMarks: Int,
        durationMinutes: Int
    ) {
        viewModelScope.launch {
            val (sectionsJson, answerKey) = GSEBQuestionBank.generatePaper(
                standard = standard,
                subject = subject,
                medium = medium,
                totalMarks = totalMarks,
                durationMinutes = durationMinutes
            )

            val isGuj = medium.equals("Gujarati", ignoreCase = true)
            val title = if (isGuj) {
                "Advance Academy GSEB $standard $subject પ્રશ્નપત્ર ($medium માધ્યમ)"
            } else {
                "Advance Academy GSEB $standard $subject Board Blueprint Test ($medium)"
            }

            val generalInstructions = if (isGuj) {
                "૧. બધા વિભાગોના પ્રશ્નો સૂચના મુજબ ફરજિયાત છે.\n૨. પ્રશ્નની સામે દર્શાવેલ ગુણ તે પ્રશ્નના મહત્તમ ગુણ છે.\n૩. સ્વચ્છ અને આકર્ષક અક્ષરે ઉત્તરો લખવા.\n૪. જરૂરી હોય ત્યાં નામનિર્દેશિત આકૃતિ દોરો."
            } else {
                "1. All questions are compulsory as per official GSEB Blueprint.\n2. Figures to the right indicate full marks.\n3. Write answers in neat handwriting with proper steps.\n4. Draw labeled diagrams wherever necessary."
            }

            val newPaper = GSEBGeneratedPaper(
                title = title,
                standard = standard,
                subject = subject,
                medium = medium,
                totalMarks = totalMarks,
                durationMinutes = durationMinutes,
                createdDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                generalInstructions = generalInstructions,
                sectionsJson = sectionsJson,
                answerKey = answerKey
            )

            val paperId = repository.savePaper(newPaper)
            val created = newPaper.copy(id = paperId)
            _viewingPaper.value = created
            _userMessage.value = "GSEB $subject Paper Generated (${totalMarks}M)!"
        }
    }

    fun viewPaper(paper: GSEBGeneratedPaper?) {
        _viewingPaper.value = paper
    }

    fun deletePaper(paper: GSEBGeneratedPaper) {
        viewModelScope.launch {
            repository.deletePaper(paper)
            if (_viewingPaper.value?.id == paper.id) {
                _viewingPaper.value = null
            }
            _userMessage.value = "Question paper deleted."
        }
    }

    fun postNotice(title: String, message: String, category: String, priority: String, standard: String) {
        viewModelScope.launch {
            repository.postNotice(
                ClassNotice(
                    title = title,
                    message = message,
                    category = category,
                    priority = priority,
                    targetStandard = standard,
                    datePosted = "Just now"
                )
            )
            _userMessage.value = "Notification dispatched to parents!"
        }
    }

    fun submitLeaveRequest(startDate: String, endDate: String, reason: String) {
        val student = _loggedInStudent.value ?: return
        viewModelScope.launch {
            repository.submitLeaveRequest(
                ParentLeaveRequest(
                    studentId = student.id,
                    startDate = startDate,
                    endDate = endDate,
                    reason = reason,
                    requestDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                )
            )
            _userMessage.value = "Leave request submitted to Advance Academy administration."
        }
    }

    fun markNoticeRead(noticeId: Long) {
        viewModelScope.launch {
            repository.markNoticeAsRead(noticeId)
        }
    }

    fun setFeeForReceipt(fee: FeeRecord?) {
        _selectedFeeForReceipt.value = fee
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }
}
