package com.example.data.sample

import com.example.data.dao.AcademyDao
import com.example.data.model.AttendanceRecord
import com.example.data.model.Batch
import com.example.data.model.ClassNotice
import com.example.data.model.ExamResult
import com.example.data.model.FeePaymentTransaction
import com.example.data.model.FeeRecord
import com.example.data.model.GSEBGeneratedPaper
import com.example.data.model.ScheduleSlot
import com.example.data.model.Student
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SampleDataLoader {
    suspend fun populateInitialDataIfEmpty(dao: AcademyDao) = withContext(Dispatchers.IO) {
        // Check if database has students
        val existingStudents = dao.getStudentByRollNumberDirect("AA-101")
        if (existingStudents != null) return@withContext

        // 1. Batches
        val batches = listOf(
            Batch(1, "Std 10 Champions (GSEB Guj)", "Std 10 GSEB", "Gujarati", "04:30 PM - 07:30 PM", "Room 101"),
            Batch(2, "Std 10 Super 30 (GSEB Eng)", "Std 10 GSEB", "English", "04:30 PM - 07:30 PM", "Room 102"),
            Batch(3, "Std 12 Science Group-A (Maths)", "Std 12 Science", "English", "03:00 PM - 06:30 PM", "Room 201 (Science Wing)"),
            Batch(4, "Std 12 Science Group-B (Bio)", "Std 12 Science", "Gujarati", "03:00 PM - 06:30 PM", "Room 202 (Bio Lab)"),
            Batch(5, "Std 12 Commerce Elite", "Std 12 Commerce", "English", "05:00 PM - 08:00 PM", "Room 301"),
            Batch(6, "Std 9 Foundation", "Std 9 GSEB", "English", "03:30 PM - 06:00 PM", "Room 103")
        )
        dao.insertBatches(batches)

        // 2. Students
        val students = listOf(
            Student(
                id = 1,
                rollNumber = "AA-101",
                name = "Hardiksinh Jadeja",
                standard = "Std 10 GSEB",
                batchId = 2,
                parentName = "Jayendrasinh Jadeja",
                parentPhone = "9825101001",
                parentEmail = "jadejahardiksinh994@gmail.com",
                enrollmentDate = "2025-06-10",
                bloodGroup = "O+",
                avatarColorSeed = 1
            ),
            Student(
                id = 2,
                rollNumber = "AA-102",
                name = "Aayushi Patel",
                standard = "Std 10 GSEB",
                batchId = 2,
                parentName = "Bhaveshbhai Patel",
                parentPhone = "9879202002",
                parentEmail = "bhavesh.patel@example.com",
                enrollmentDate = "2025-06-12",
                bloodGroup = "B+",
                avatarColorSeed = 2
            ),
            Student(
                id = 3,
                rollNumber = "AA-103",
                name = "Devansh Shah",
                standard = "Std 10 GSEB",
                batchId = 1,
                parentName = "Nileshbhai Shah",
                parentPhone = "9426303003",
                parentEmail = "nilesh.shah@example.com",
                enrollmentDate = "2025-06-15",
                bloodGroup = "A+",
                avatarColorSeed = 3
            ),
            Student(
                id = 4,
                rollNumber = "AA-201",
                name = "Priyanshu Dave",
                standard = "Std 12 Science",
                batchId = 3,
                parentName = "Kaushikbhai Dave",
                parentPhone = "9712404004",
                parentEmail = "kaushik.dave@example.com",
                enrollmentDate = "2024-06-10",
                bloodGroup = "AB+",
                avatarColorSeed = 4
            ),
            Student(
                id = 5,
                rollNumber = "AA-202",
                name = "Riddhi Mehta",
                standard = "Std 12 Science",
                batchId = 4,
                parentName = "Pareshbhai Mehta",
                parentPhone = "9909505005",
                parentEmail = "paresh.mehta@example.com",
                enrollmentDate = "2024-06-11",
                bloodGroup = "B+",
                avatarColorSeed = 5
            ),
            Student(
                id = 6,
                rollNumber = "AA-301",
                name = "Parth Joshi",
                standard = "Std 12 Commerce",
                batchId = 5,
                parentName = "Sanjaybhai Joshi",
                parentPhone = "9824606006",
                parentEmail = "sanjay.joshi@example.com",
                enrollmentDate = "2024-06-20",
                bloodGroup = "O+",
                avatarColorSeed = 6
            ),
            Student(
                id = 7,
                rollNumber = "AA-302",
                name = "Ananya Trivedi",
                standard = "Std 12 Commerce",
                batchId = 5,
                parentName = "Dharmeshbhai Trivedi",
                parentPhone = "9898707007",
                parentEmail = "dharmesh.t@example.com",
                enrollmentDate = "2024-06-22",
                bloodGroup = "A+",
                avatarColorSeed = 7
            )
        )
        dao.insertStudents(students)

        // 3. Attendance history for past 7 days
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val attendanceList = mutableListOf<AttendanceRecord>()

        for (dayOffset in 0..6) {
            val dateCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -dayOffset) }
            val dateStr = dateFormat.format(dateCal.time)
            val isSunday = dateCal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
            if (isSunday) continue

            students.forEach { s ->
                val status = when {
                    s.id == 1L && dayOffset == 2 -> "LATE"
                    s.id == 3L && dayOffset == 3 -> "ABSENT"
                    s.id == 6L && dayOffset == 1 -> "LATE"
                    else -> "PRESENT"
                }
                val remarks = if (status == "LATE") "Late by 15 mins (Traffic)" else if (status == "ABSENT") "Family occasion" else "On time"
                attendanceList.add(
                    AttendanceRecord(
                        studentId = s.id,
                        batchId = s.batchId,
                        date = dateStr,
                        status = status,
                        remarks = remarks,
                        markedAt = dateCal.timeInMillis
                    )
                )
            }
        }
        dao.insertAttendanceList(attendanceList)

        // 4. Schedules
        val schedules = listOf(
            // Std 10 Super 30
            ScheduleSlot(1, 2, "Std 10 GSEB", "Mathematics", "Er. V.P. Sharma", "Monday", "04:30 PM", "05:45 PM", "Room 102"),
            ScheduleSlot(2, 2, "Std 10 GSEB", "Science (Physics/Chem)", "Dr. R.K. Vyas", "Monday", "06:00 PM", "07:15 PM", "Room 102"),
            ScheduleSlot(3, 2, "Std 10 GSEB", "Social Science", "Prof. M.N. Solanki", "Tuesday", "04:30 PM", "05:45 PM", "Room 102"),
            ScheduleSlot(4, 2, "Std 10 GSEB", "English Language", "Mrs. P.H. Desai", "Tuesday", "06:00 PM", "07:15 PM", "Room 102"),
            ScheduleSlot(5, 2, "Std 10 GSEB", "Mathematics (Doubt Clinic)", "Er. V.P. Sharma", "Wednesday", "04:30 PM", "05:45 PM", "Room 102"),
            ScheduleSlot(6, 2, "Std 10 GSEB", "GSEB Board Paper Practice", "Academy Faculty", "Thursday", "05:00 PM", "07:00 PM", "Exam Hall A"),
            ScheduleSlot(7, 2, "Std 10 GSEB", "Science Practical & Theory", "Dr. R.K. Vyas", "Friday", "04:30 PM", "06:30 PM", "Room 102"),
            ScheduleSlot(8, 2, "Std 10 GSEB", "Weekly GSEB Grand Test", "Examiner Board", "Saturday", "04:00 PM", "06:00 PM", "Exam Hall A"),

            // Std 12 Science
            ScheduleSlot(9, 3, "Std 12 Science", "Physics (Mechanics & Electrodynamics)", "Prof. K.N. Trivedi", "Monday", "03:00 PM", "04:30 PM", "Room 201"),
            ScheduleSlot(10, 3, "Std 12 Science", "Chemistry (Organic & Physical)", "Prof. S.M. Mehta", "Monday", "04:45 PM", "06:15 PM", "Room 201"),
            ScheduleSlot(11, 3, "Std 12 Science", "Mathematics (Calculus & Vectors)", "Prof. A.R. Patel", "Tuesday", "03:00 PM", "04:30 PM", "Room 201"),
            ScheduleSlot(12, 3, "Std 12 Science", "Physics Lab & Numerical Practice", "Prof. K.N. Trivedi", "Wednesday", "03:00 PM", "05:00 PM", "Physics Lab"),
            ScheduleSlot(13, 3, "Std 12 Science", "Chemistry Numerical Clinic", "Prof. S.M. Mehta", "Thursday", "03:30 PM", "05:30 PM", "Room 201"),
            ScheduleSlot(14, 3, "Std 12 Science", "GSEB HSC Mock Board Exam", "Faculty Panel", "Saturday", "02:30 PM", "05:30 PM", "Room 201"),

            // Std 12 Commerce
            ScheduleSlot(15, 5, "Std 12 Commerce", "Elements of Accounts (Part I & II)", "CA H.M. Raval", "Monday", "05:00 PM", "06:30 PM", "Room 301"),
            ScheduleSlot(16, 5, "Std 12 Commerce", "Statistics (Probability & Index)", "Prof. J.D. Shukla", "Monday", "06:45 PM", "08:00 PM", "Room 301"),
            ScheduleSlot(17, 5, "Std 12 Commerce", "Economics (GSEB Syllabus)", "Mrs. S.K. Pandya", "Tuesday", "05:00 PM", "06:30 PM", "Room 301"),
            ScheduleSlot(18, 5, "Std 12 Commerce", "Accounts Problem Solving Workshop", "CA H.M. Raval", "Wednesday", "05:00 PM", "07:00 PM", "Room 301"),
            ScheduleSlot(19, 5, "Std 12 Commerce", "Statistics Practical Calculations", "Prof. J.D. Shukla", "Thursday", "05:00 PM", "06:30 PM", "Room 301"),
            ScheduleSlot(20, 5, "Std 12 Commerce", "Commerce Weekly Test Series", "Faculty Board", "Saturday", "05:00 PM", "07:00 PM", "Exam Hall B")
        )
        dao.insertSchedules(schedules)

        // 5. Fees
        val fees = listOf(
            FeeRecord(1, 1, "Term 1 Academic Tuition Fee", 18000.0, 18000.0, "2025-07-10", "2025-07-05", "UPI", "UPI/ADV9825101/01", "REC-AA-2025-101", "PAID"),
            FeeRecord(2, 1, "Term 2 Board Test Series & Notes", 12000.0, 6000.0, "2025-10-15", "2025-09-01", "UPI", "UPI/ADV9825101/02", "REC-AA-2025-102", "PARTIAL"),
            FeeRecord(3, 2, "Term 1 Academic Tuition Fee", 18000.0, 18000.0, "2025-07-10", "2025-07-08", "Cash", "CASH/REC-092", "REC-AA-2025-103", "PAID"),
            FeeRecord(4, 2, "Term 2 Board Test Series & Notes", 12000.0, 12000.0, "2025-10-15", "2025-08-20", "NetBanking", "HDFC/NET/4412", "REC-AA-2025-104", "PAID"),
            FeeRecord(5, 3, "Term 1 Academic Tuition Fee", 16000.0, 10000.0, "2025-07-10", "2025-07-12", "Cheque", "CHQ-SBIN-8891", "REC-AA-2025-105", "PARTIAL"),
            FeeRecord(6, 4, "Std 12 Science Annual Course Fee", 32000.0, 20000.0, "2025-08-30", "2025-07-15", "UPI", "UPI/ADV7712/91", "REC-AA-2025-201", "PARTIAL"),
            FeeRecord(7, 5, "Std 12 Science Annual Course Fee", 32000.0, 32000.0, "2025-08-30", "2025-08-10", "NetBanking", "ICICI/TXN/8912", "REC-AA-2025-202", "PAID"),
            FeeRecord(8, 6, "Std 12 Commerce Annual Course Fee", 22000.0, 0.0, "2025-08-15", null, "UPI", "", "", "OVERDUE"),
            FeeRecord(9, 7, "Std 12 Commerce Annual Course Fee", 22000.0, 22000.0, "2025-08-15", "2025-08-01", "Cash", "CASH/REC-115", "REC-AA-2025-302", "PAID")
        )
        dao.insertFees(fees)

        // 5b. Initial Fee Payment Transactions History
        val transactions = listOf(
            FeePaymentTransaction(
                id = 1,
                feeRecordId = 1,
                studentId = 1,
                feeTitle = "Term 1 Academic Tuition Fee",
                amountPaid = 18000.0,
                paymentDate = "2025-07-05",
                paymentTime = "11:15 AM",
                paymentMode = "UPI",
                transactionRef = "UPI/ADV9825101/01",
                receiptNumber = "REC-AA-2025-101",
                balanceAfterPayment = 0.0,
                remarks = "Term 1 Full Payment via Google Pay",
                recordedBy = "Admin Accounts Desk"
            ),
            FeePaymentTransaction(
                id = 2,
                feeRecordId = 2,
                studentId = 1,
                feeTitle = "Term 2 Board Test Series & Notes",
                amountPaid = 6000.0,
                paymentDate = "2025-09-01",
                paymentTime = "04:30 PM",
                paymentMode = "UPI",
                transactionRef = "UPI/ADV9825101/02",
                receiptNumber = "REC-AA-2025-102",
                balanceAfterPayment = 6000.0,
                remarks = "First installment paid for Board Test Series",
                recordedBy = "Accounts Desk"
            ),
            FeePaymentTransaction(
                id = 3,
                feeRecordId = 3,
                studentId = 2,
                feeTitle = "Term 1 Academic Tuition Fee",
                amountPaid = 18000.0,
                paymentDate = "2025-07-08",
                paymentTime = "10:00 AM",
                paymentMode = "Cash",
                transactionRef = "CASH/REC-092",
                receiptNumber = "REC-AA-2025-103",
                balanceAfterPayment = 0.0,
                remarks = "Cash received at academy front office",
                recordedBy = "Admin Cashier"
            ),
            FeePaymentTransaction(
                id = 4,
                feeRecordId = 4,
                studentId = 2,
                feeTitle = "Term 2 Board Test Series & Notes",
                amountPaid = 12000.0,
                paymentDate = "2025-08-20",
                paymentTime = "02:45 PM",
                paymentMode = "NetBanking",
                transactionRef = "HDFC/NET/4412",
                receiptNumber = "REC-AA-2025-104",
                balanceAfterPayment = 0.0,
                remarks = "HDFC NetBanking IMPS transfer verified",
                recordedBy = "Accounts Desk"
            ),
            FeePaymentTransaction(
                id = 5,
                feeRecordId = 5,
                studentId = 3,
                feeTitle = "Term 1 Academic Tuition Fee",
                amountPaid = 10000.0,
                paymentDate = "2025-07-12",
                paymentTime = "12:10 PM",
                paymentMode = "Cheque",
                transactionRef = "CHQ-SBIN-8891",
                receiptNumber = "REC-AA-2025-105",
                balanceAfterPayment = 6000.0,
                remarks = "SBI Cheque No. 49102 cleared",
                recordedBy = "Admin Accounts Desk"
            ),
            FeePaymentTransaction(
                id = 6,
                feeRecordId = 6,
                studentId = 4,
                feeTitle = "Std 12 Science Annual Course Fee",
                amountPaid = 20000.0,
                paymentDate = "2025-07-15",
                paymentTime = "05:20 PM",
                paymentMode = "UPI",
                transactionRef = "UPI/ADV7712/91",
                receiptNumber = "REC-AA-2025-201",
                balanceAfterPayment = 12000.0,
                remarks = "Part payment received via PhonePe",
                recordedBy = "Accounts Desk"
            ),
            FeePaymentTransaction(
                id = 7,
                feeRecordId = 7,
                studentId = 5,
                feeTitle = "Std 12 Science Annual Course Fee",
                amountPaid = 32000.0,
                paymentDate = "2025-08-10",
                paymentTime = "01:15 PM",
                paymentMode = "NetBanking",
                transactionRef = "ICICI/TXN/8912",
                receiptNumber = "REC-AA-2025-202",
                balanceAfterPayment = 0.0,
                remarks = "Full annual fee payment NEFT",
                recordedBy = "Admin Cashier"
            ),
            FeePaymentTransaction(
                id = 8,
                feeRecordId = 9,
                studentId = 7,
                feeTitle = "Std 12 Commerce Annual Course Fee",
                amountPaid = 22000.0,
                paymentDate = "2025-08-01",
                paymentTime = "11:40 AM",
                paymentMode = "Cash",
                transactionRef = "CASH/REC-115",
                receiptNumber = "REC-AA-2025-302",
                balanceAfterPayment = 0.0,
                remarks = "Cash payment for Commerce batch cleared",
                recordedBy = "Admin Accounts Desk"
            )
        )
        dao.insertTransactions(transactions)

        // 6. Exam Results
        val results = listOf(
            ExamResult(1, 1, "GSEB Unit Test 1 (Maths)", "Mathematics", 48, 50, "2025-07-28", 1, "Outstanding analytical approach in Quadratic equations."),
            ExamResult(2, 1, "GSEB Mid-Term Prelim (Science)", "Science", 74, 80, "2025-08-20", 2, "Very good diagrams in Biology and accurate Physics derivations."),
            ExamResult(3, 1, "Weekly Rapid MCQ Test", "All Subjects", 24, 25, "2025-09-02", 1, "Flawless performance in Section A objective questions."),
            ExamResult(4, 2, "GSEB Unit Test 1 (Maths)", "Mathematics", 46, 50, "2025-07-28", 2, "Solid step-by-step presentation."),
            ExamResult(5, 4, "Physics HSC Board Test 1", "Physics", 45, 50, "2025-08-15", 1, "Excellent numerical solving in Electrostatics."),
            ExamResult(6, 6, "Accounts Partnership Test", "Accountancy", 42, 50, "2025-08-18", 3, "Good grasp of Revaluation account entries.")
        )
        dao.insertExamResults(results)

        // 7. Class Notices / Automated Notifications
        val notices = listOf(
            ClassNotice(
                id = 1,
                title = "GSEB Board Paper Generator Updated",
                message = "The latest GSEB Blueprint (2025-26) with 30% Objective and 70% Descriptive weightage is active for Std 10 and 12.",
                category = "EXAM",
                priority = "HIGH",
                targetStandard = "All",
                datePosted = "Today, 09:30 AM"
            ),
            ClassNotice(
                id = 2,
                title = "Attendance Status Logged",
                message = "Student attendance for Advance Academy has been verified. Daily attendance report is accessible in the Parent VMS portal.",
                category = "ATTENDANCE",
                priority = "NORMAL",
                targetStandard = "All",
                datePosted = "Today, 08:00 AM"
            ),
            ClassNotice(
                id = 3,
                title = "Advance Academy Sunday Masterclass",
                message = "Special 3-Hour GSEB Board Problem Solving and Doubt Clearing workshop this Sunday 9:00 AM to 12:00 PM.",
                category = "SCHEDULE",
                priority = "HIGH",
                targetStandard = "Std 10 GSEB",
                datePosted = "Yesterday"
            ),
            ClassNotice(
                id = 4,
                title = "Fee Balance Reminder (Term 2)",
                message = "Parents are requested to clear remaining term fees before the 15th to ensure uninterrupted test series access. Instant UPI receipts available.",
                category = "FEES",
                priority = "NORMAL",
                targetStandard = "All",
                datePosted = "04 Sep 2025"
            ),
            ClassNotice(
                id = 5,
                title = "GSEB Board Exam Form Registration Notice",
                message = "All Std 10 & 12 students must submit verified passport photos and Aadhaar copy at the academy desk by Friday.",
                category = "GENERAL",
                priority = "URGENT",
                targetStandard = "All",
                datePosted = "01 Sep 2025"
            )
        )
        dao.insertNotices(notices)

        // 8. Sample Pre-generated GSEB Board Question Papers
        val gsebMathsPaper = GSEBGeneratedPaper(
            id = 1,
            title = "Advance Academy GSEB Std 10 Mathematics Board Blueprint Test",
            standard = "Std 10 GSEB",
            subject = "Mathematics",
            medium = "English",
            totalMarks = 80,
            durationMinutes = 180,
            createdDate = "05 Sep 2025",
            generalInstructions = "1. All questions are compulsory according to GSEB Blueprint.\n2. Write answers neatly with proper steps.\n3. Section A has 16 MCQs/Objectives (1 mark each).\n4. Section B has 10 Short Questions (2 marks each).\n5. Section C has 8 Medium Questions with internal options (3 marks each).\n6. Section D has 5 Long Essay/Theorem questions (4 marks each).",
            sectionsJson = """
[
  {
    "sectionName": "SECTION A (Objective & MCQs - 16 Marks)",
    "instructions": "Answer all 16 questions. Each question carries 1 mark.",
    "questions": [
      "1. Find the HCF of 96 and 404 by prime factorisation method.",
      "2. The decimal expansion of 14587/1250 will terminate after how many decimal places? (A) 1  (B) 2  (C) 3  (D) 4",
      "3. If one zero of quadratic polynomial x² + 3x + k is 2, then find the value of k.",
      "4. The pair of equations x + 2y + 5 = 0 and -3x - 6y + 1 = 0 has: (A) unique solution (B) exactly two solutions (C) infinitely many (D) no solution",
      "5. State True or False: Every real number is either rational or irrational.",
      "6. In an AP, if d = -4, n = 7, an = 4, then find first term a.",
      "7. Find the distance between points P(2, 3) and Q(4, 1).",
      "8. If tan A = 4/3, find the value of sin A · cos A.",
      "9. Fill in the blank: A line intersecting a circle in two points is called a _______.",
      "10. The probability of an event that cannot happen is _______.",
      "11. If the perimeter and area of a circle are numerically equal, then radius of the circle is: (A) 2 units (B) π units (C) 4 units (D) 7 units",
      "12. Write the formula for finding the mode of grouped data.",
      "13. If P(E) = 0.05, what is the probability of 'not E'?",
      "14. State whether the following equation is quadratic: (x - 2)² + 1 = 2x - 3.",
      "15. The sum of the first n positive integers is given by the formula _______.",
      "16. Evaluate: sin 30° + cos 60° - tan 45°."
    ]
  },
  {
    "sectionName": "SECTION B (Short Answer Questions - 20 Marks)",
    "instructions": "Answer any 10 out of 12 questions. Each question carries 2 marks.",
    "questions": [
      "17. Prove that 3 + 2√5 is irrational.",
      "18. Find the zeroes of the quadratic polynomial 6x² - 3 - 7x and verify the relationship between zeroes and coefficients.",
      "19. Solve the following pair of linear equations by elimination: 3x + 4y = 10 and 2x - 2y = 2.",
      "20. Find the roots of quadratic equation 2x² - 5x + 3 = 0 using quadratic formula.",
      "21. Which term of the AP: 3, 8, 13, 18, ... is 78?",
      "22. Find the coordinates of the point which divides the join of (-1, 7) and (4, -3) in the ratio 2:3.",
      "23. If sin(A - B) = 1/2 and cos(A + B) = 1/2, where 0° < A + B ≤ 90° and A > B, find A and B.",
      "24. A tangent PQ at a point P of a circle of radius 5 cm meets a line through center O at point Q so that OQ = 12 cm. Find length of PQ.",
      "25. A bag contains 3 red balls and 5 black balls. A ball is drawn at random. What is the probability that the ball drawn is: (i) red? (ii) not red?",
      "26. Find the mean of the following distribution: Class (0-10, 10-20, 20-30, 30-40, 40-50), Frequency (5, 8, 12, 11, 4)."
    ]
  },
  {
    "sectionName": "SECTION C (Medium / Analytical Questions - 24 Marks)",
    "instructions": "Answer any 8 questions. Each question carries 3 marks.",
    "questions": [
      "27. If the sum of first 14 terms of an AP is 1050 and its first term is 10, find the 20th term.",
      "28. Prove the identity: (sin A + cosec A)² + (cos A + sec A)² = 7 + tan² A + cot² A.",
      "29. From a point on the ground, the angles of elevation of the bottom and the top of a transmission tower fixed at the top of a 20 m high building are 45° and 60° respectively. Find the height of the tower.",
      "30. Prove that the lengths of tangents drawn from an external point to a circle are equal.",
      "31. A solid toy is in the form of a hemisphere surmounted by a right circular cone. The height of the cone is 2 cm and diameter of base is 4 cm. Determine volume of the toy. (Take π = 3.14)",
      "32. If the median of the distribution given below is 28.5, find values of x and y (Total N = 60).",
      "33. Solve graphically: 2x + y = 6 and 2x - y + 2 = 0. Find area of the triangle formed with x-axis.",
      "34. A motor boat whose speed is 18 km/h in still water takes 1 hour more to go 24 km upstream than to return downstream to the same spot. Find speed of the stream."
    ]
  },
  {
    "sectionName": "SECTION D (Long Essay / Theorem Questions - 20 Marks)",
    "instructions": "Answer any 5 questions. Each question carries 4 marks.",
    "questions": [
      "35. State and Prove Basic Proportionality Theorem (Thales Theorem).",
      "36. Draw a triangle ABC with side BC = 6 cm, AB = 5 cm and ∠ABC = 60°. Then construct a triangle whose sides are 3/4 of the corresponding sides of triangle ABC. Write steps of construction.",
      "37. State and prove Pythagoras Theorem: In a right triangle, the square of hypotenuse is equal to sum of squares of the other two sides.",
      "38. As observed from the top of a 75 m high lighthouse from sea-level, the angles of depression of two ships are 30° and 45°. If one ship is exactly behind the other on the same side of lighthouse, find distance between two ships.",
      "39. A tent is in the shape of a cylinder surmounted by a conical top. If the height and diameter of cylindrical part are 2.1 m and 4 m, and slant height of the top is 2.8 m, find area of canvas used and cost at ₹500 per m²."
    ]
  }
]
            """.trimIndent(),
            answerKey = "GSEB Marking Scheme:\nSection A: 1. 4, 2. (D) 4 places, 3. k = -10, 4. (D) no solution, 5. True, 6. a = 28, 7. 2√2 units, 8. 12/25, 9. Secant, 10. 0, 11. (A) 2 units, 12. l + ((f1-f0)/(2f1-f0-f2))*h, 13. 0.95, 14. Yes, 15. n(n+1)/2, 16. 0.\nFull solutions and step-by-step points for Sections B, C, D included in Teacher's Companion Sheet."
        )
        dao.insertPaper(gsebMathsPaper)

        val gsebSciGujPaper = GSEBGeneratedPaper(
            id = 2,
            title = "Advance Academy GSEB Std 10 વિજ્ઞાન બોર્ડ મોડેલ પ્રશ્નપત્ર (ગુજરાતી માધ્યમ)",
            standard = "Std 10 GSEB",
            subject = "Science",
            medium = "Gujarati",
            totalMarks = 80,
            durationMinutes = 180,
            createdDate = "04 Sep 2025",
            generalInstructions = "૧. બધા પ્રશ્નો ફરજિયાત છે. ગુજરાત બોર્ડ (GSEB) ની નવીનતમ બ્લુપ્રિન્ટ અનુસાર.\n૨. સ્પષ્ટ, સ્વચ્છ અને નામનિર્દેશનવાળી આકૃતિઓ દોરો.\n૩. વિભાગ A: હેતુલક્ષી પ્રશ્નો (૧૬ ગુણ).\n૪. વિભાગ B: ટૂંકજવાબી પ્રશ્નો (૨૦ ગુણ).\n૫. વિભાગ C: મુદ્દાસર પ્રશ્નો (૨૪ ગુણ).\n૬. વિભાગ D: વિસ્તૃત પ્રશ્નો (૨૦ ગુણ).",
            sectionsJson = """
[
  {
    "sectionName": "વિભાગ A (હેતુલક્ષી પ્રશ્નો - ૧૬ ગુણ)",
    "instructions": "દરેક પ્રશ્નનો ૧ ગુણ છે. બધા પ્રશ્નો ઉત્તરવહીમાં લખો.",
    "questions": [
      "૧. Fe₂O₃ + 2Al -> Al₂O₃ + 2Fe - આપેલ પ્રક્રિયા કયા પ્રકારની પ્રક્રિયા છે? (A) સંયોગીકરણ (B) વિઘટન (C) વિસ્થાપન (D) દ્વિવિસ્થાપન",
      "૨. પિત્તરસ ક્યાં ઉત્પન્ન થાય છે? (A) જઠર (B) યકૃત (C) સ્વાદુપિંડ (D) નાનું આંતરડું",
      "૩. ખરાં કે ખોટાં જણાવો: અંતર્ગોળ અરીસાની કેન્દ્રલંબાઈ ઋણ હોય છે.",
      "૪. વિદ્યુતભારનો SI એકમ જણાવો.",
      "૫. ખાલી જગ્યા પૂરો: શુદ્ધ સોનું _______ કેરેટનું હોય છે.",
      "૬. સામાન્ય દ્રષ્ટિ ધરાવતી પુખ્ત વ્યક્તિ માટે સ્પષ્ટ દ્રષ્ટિનું લઘુતમ અંતર કેટલું છે?",
      "૭. ઓઝોન વાયુનું અણુસૂત્ર લખો.",
      "૮. પ્લાસ્ટર ઓફ પેરિસનું રાસાયણિક સૂત્ર આપો.",
      "૯. માનવમાં લિંગ નિશ્ચયન માટે કયું રંગસૂત્ર જવાબદાર છે?",
      "૧૦. વિદ્યુતપ્રવાહ માપવા માટે વપરાતા સાધનનું નામ જણાવો.",
      "૧૧. મેન્ડેલના પ્રયોગમાં F2 પેઢીમાં પ્રભાવી અને પ્રચ્છન્ન લક્ષણોનું સ્વરૂપ પ્રકાર પ્રમાણ જણાવો.",
      "૧૨. આંખના કયા ભાગ પર વસ્તુનું પ્રતિબિંબ રચાય છે? (A) કીકી (B) કનીનિકા (C) નેત્રપટલ (રેટિના) (D) સિલિયરી સ્નાયુ",
      "૧૩. બાયોગેસનો મુખ્ય ઘટક કયો વાયુ છે?",
      "૧૪. ધાતુઓને ટીપીને પાતળા પતરા બનાવવાની ક્ષમતાને _______ કહે છે.",
      "૧૫. લાલ કીડીના ડંખમાં કયો એસિડ હોય છે?",
      "૧૬. સમતલ અરીસાની મોટવણી હંમેશા કેટલી હોય છે?"
    ]
  },
  {
    "sectionName": "વિભાગ B (ટૂંકજવાબી પ્રશ્નો - ૨૦ ગુણ)",
    "instructions": "કોઈપણ ૧૦ પ્રશ્નોના ઉત્તર આપો (દરેકના ૨ ગુણ).",
    "questions": [
      "૧૭. વિઘટન પ્રક્રિયા એટલે શું? એક રાસાયણિક સમીકરણ આપી સમજાવો.",
      "૧૮. દૈનિક જીવનમાં pH નું મહત્વ સમજાવો (કોઈપણ બે મુદ્દા).",
      "૧૯. મિશ્રધાતુ એટલે શું? કાંસું (Bronze) અને પિત્તળ (Brass) ના ઘટકો લખો.",
      "૨૦. સ્વયંપોષી પોષણ અને વિષમપોષી પોષણ વચ્ચેના બે તફાવત આપો.",
      "૨૧. મનુષ્યના શ્વસનતંત્રમાં વાયુકોષ્ઠોની ભૂમિકા સમજાવો.",
      "૨૨. સ્નેલનો વક્રીભવનનો નિયમ લખો અને સૂત્ર તારવો.",
      "૨૩. ૧૨ V વિદ્યુત સ્થિતિમાનનો તફાવત ધરાવતા બે બિંદુઓ વચ્ચે ૨ C વિદ્યુતભારને લઈ જવા માટે કેટલું કાર્ય કરવું પડે?",
      "૨૪. ઓહ્મનો નિયમ લખી તેનું ગાણિતિક સૂત્ર આપો.",
      "૨૫. ફ્લેમિંગનો ડાબા હાથનો નિયમ લખો.",
      "૨૬. જૈવ-વિઘટનીય અને જૈવ-અવિઘટનીય કચરા વચ્ચેનો તફાવત આપો."
    ]
  },
  {
    "sectionName": "વિભાગ C (મુદ્દાસર પ્રશ્નો - ૨૪ ગુણ)",
    "instructions": "કોઈપણ ૮ પ્રશ્નોના ઉત્તર મુદ્દાસર આપો (દરેકના ૩ ગુણ).",
    "questions": [
      "૨૭. ધાતુના એસિડ સાથેની પ્રક્રિયા દર્શાવતો ઝિંક અને મંદ સલ્ફ્યુરિક એસિડનો પ્રયોગ આકૃતિ સાથે સમજાવો.",
      "૨૮. લોખંડનું ક્ષારણ અટકાવવાના ત્રણ ઉપાયો જણાવો.",
      "૨૯. મનુષ્યના હૃદયની આંતરિક રચના દર્શાવતી નામનિર્દેશનવાળી નામવાળી રેખાકૃતિ દોરો.",
      "૩૦. જારક શ્વસન અને અજારક શ્વસન વચ્ચેના ત્રણ મુખ્ય તફાવતો આપો.",
      "૩૧. લઘુદ્રષ્ટિની ખામી (માયોપિયા) એટલે શું? તેનું નિવારણ આકૃતિ દોરી સમજાવો.",
      "૩૨. અવરોધોના સમાંતર જોડાણ માટે સમતુલ્ય અવરોધનું સૂત્ર તારવો.",
      "૩૩. વિદ્યુત મોટરનો સિદ્ધાંત અને રચના નામનિર્દેશિત આકૃતિ સાથે સમજાવો.",
      "૩૪. પોષક સ્તરો એટલે શું? એક આહાર શૃંખલાનું ઉદાહરણ આપી પોષક સ્તરો સમજાવો."
    ]
  },
  {
    "sectionName": "વિભાગ D (વિસ્તૃત પ્રશ્નો - ૨૦ ગુણ)",
    "instructions": "કોઈપણ ૫ પ્રશ્નોના સવિસ્તાર ઉત્તર આપો (દરેકના ૪ ગુણ).",
    "questions": [
      "૩૫. મનુષ્યના ઉત્સર્જન તંત્રની સ્વચ્છ નામનિર્દેશનવાળી આકૃતિ દોરી મૂત્રપિંડ નલિકા (નેફ્રોન) ની રચના સમજાવો.",
      "૩૬. સાબુની સફાઈ પ્રક્રિયાની ક્રિયાવિધિ મિસેલ રચનાની આકૃતિ સાથે સમજાવો.",
      "૩૭. માનવ પાચનતંત્રની નામનિર્દેશિત આકૃતિ દોરી જઠર અને નાના આંતરડામાં થતી પાચનક્રિયા સમજાવો.",
      "૩૮. અંતર્ગોળ અરીસા સામે વસ્તુને (i) C થી દૂર અને (ii) C અને F વચ્ચે મૂકતા રચાતા પ્રતિબિંબની કિરણાવલિ દોરી સ્થાન અને પ્રકાર જણાવો.",
      "૩૯. ક્લોર-આલ્કલી પ્રક્રિયા સમીકરણ સાથે સમજાવો અને તેમાં ઉત્પન્ન થતી નીપજોના ઉપયોગો જણાવો."
    ]
  }
]
            """.trimIndent(),
            answerKey = "GSEB ઉત્તરવહી સૂચકાંક:\nવિભાગ A: ૧. (C) વિસ્થાપન, ૨. (B) યકૃત, ૩. ખરું, ૪. કુલંબ (C), ૫. ૨૪ કેરેટ, ૬. ૨૫ સેમી, ૭. O₃, ૮. CaSO₄·½H₂O, ૯. Y રંગસૂત્ર, ૧૦. એમીટર, ૧૧. ૩:૧, ૧૨. (C) નેત્રપટલ, ૧૩. મિથેન (CH₄), ૧૪. ટીપાવપણું, ૧૫. મિથેનોઇક (ફોર્મિક) એસિડ, ૧૬. +૧."
        )
        dao.insertPaper(gsebSciGujPaper)
    }
}
