package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface AcademyDao {
    // --- Students ---
    @Query("SELECT * FROM students ORDER BY rollNumber ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE batchId = :batchId ORDER BY rollNumber ASC")
    fun getStudentsByBatch(batchId: Long): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE standard = :standard ORDER BY rollNumber ASC")
    fun getStudentsByStandard(standard: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE id = :id")
    fun getStudentById(id: Long): Flow<Student?>

    @Query("SELECT * FROM students WHERE rollNumber = :rollNumber LIMIT 1")
    suspend fun getStudentByRollNumberDirect(rollNumber: String): Student?

    @Query("SELECT * FROM students WHERE parentPhone = :phone OR rollNumber = :query")
    fun findStudentsByPhoneOrRoll(phone: String, query: String): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    // --- Batches ---
    @Query("SELECT * FROM batches ORDER BY id ASC")
    fun getAllBatches(): Flow<List<Batch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: Batch): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatches(batches: List<Batch>)

    // --- Attendance ---
    @Query("SELECT * FROM attendance WHERE batchId = :batchId AND date = :date")
    fun getAttendanceForBatchAndDate(batchId: Long, date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE date = :date")
    fun getAttendanceForDate(date: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceList(records: List<AttendanceRecord>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(record: AttendanceRecord): Long

    // --- Schedules ---
    @Query("SELECT * FROM schedules ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllSchedules(): Flow<List<ScheduleSlot>>

    @Query("SELECT * FROM schedules WHERE dayOfWeek = :day ORDER BY startTime ASC")
    fun getSchedulesForDay(day: String): Flow<List<ScheduleSlot>>

    @Query("SELECT * FROM schedules WHERE batchId = :batchId ORDER BY dayOfWeek ASC, startTime ASC")
    fun getSchedulesForBatch(batchId: Long): Flow<List<ScheduleSlot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(slot: ScheduleSlot): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(slots: List<ScheduleSlot>)

    @Delete
    suspend fun deleteSchedule(slot: ScheduleSlot)

    // --- Fees ---
    @Query("SELECT * FROM fees ORDER BY dueDate ASC")
    fun getAllFees(): Flow<List<FeeRecord>>

    @Query("SELECT * FROM fees WHERE studentId = :studentId ORDER BY dueDate DESC")
    fun getFeesForStudent(studentId: Long): Flow<List<FeeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFee(fee: FeeRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFees(fees: List<FeeRecord>)

    @Update
    suspend fun updateFee(fee: FeeRecord)

    // --- Fee Transactions / Payment History ---
    @Query("SELECT * FROM fee_transactions ORDER BY id DESC")
    fun getAllTransactions(): Flow<List<FeePaymentTransaction>>

    @Query("SELECT * FROM fee_transactions WHERE studentId = :studentId ORDER BY id DESC")
    fun getTransactionsForStudent(studentId: Long): Flow<List<FeePaymentTransaction>>

    @Query("SELECT * FROM fee_transactions WHERE feeRecordId = :feeId ORDER BY id DESC")
    fun getTransactionsForFee(feeId: Long): Flow<List<FeePaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FeePaymentTransaction): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<FeePaymentTransaction>)

    // --- Exam Results ---
    @Query("SELECT * FROM exam_results WHERE studentId = :studentId ORDER BY testDate DESC")
    fun getResultsForStudent(studentId: Long): Flow<List<ExamResult>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResult(result: ExamResult): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamResults(results: List<ExamResult>)

    // --- Class Notices / Automated Notifications ---
    @Query("SELECT * FROM class_notices ORDER BY id DESC")
    fun getAllNotices(): Flow<List<ClassNotice>>

    @Query("SELECT * FROM class_notices WHERE targetStandard = 'All' OR targetStandard = :standard ORDER BY id DESC")
    fun getNoticesForStandard(standard: String): Flow<List<ClassNotice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotice(notice: ClassNotice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotices(notices: List<ClassNotice>)

    @Query("UPDATE class_notices SET isReadByParent = 1 WHERE id = :noticeId")
    suspend fun markNoticeAsRead(noticeId: Long)

    // --- GSEB Papers ---
    @Query("SELECT * FROM gseb_papers ORDER BY id DESC")
    fun getAllPapers(): Flow<List<GSEBGeneratedPaper>>

    @Query("SELECT * FROM gseb_papers WHERE id = :id")
    fun getPaperById(id: Long): Flow<GSEBGeneratedPaper?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPaper(paper: GSEBGeneratedPaper): Long

    @Delete
    suspend fun deletePaper(paper: GSEBGeneratedPaper)

    // --- Leave Requests ---
    @Query("SELECT * FROM leave_requests WHERE studentId = :studentId ORDER BY id DESC")
    fun getLeaveRequestsForStudent(studentId: Long): Flow<List<ParentLeaveRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaveRequest(request: ParentLeaveRequest): Long
}
