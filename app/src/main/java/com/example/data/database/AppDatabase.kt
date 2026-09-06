package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
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

@Database(
    entities = [
        Student::class,
        Batch::class,
        AttendanceRecord::class,
        ScheduleSlot::class,
        FeeRecord::class,
        FeePaymentTransaction::class,
        ExamResult::class,
        ClassNotice::class,
        GSEBGeneratedPaper::class,
        ParentLeaveRequest::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun academyDao(): AcademyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "advance_academy_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
