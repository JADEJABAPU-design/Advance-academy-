package com.example.domain.gseb

data class GSEBQuestion(
    val text: String,
    val section: String, // "A", "B", "C", "D"
    val marks: Int,
    val subject: String,
    val standard: String,
    val medium: String, // "English" or "Gujarati"
    val chapter: String,
    val answerHint: String = ""
)

object GSEBQuestionBank {
    val questions = listOf(
        // === STD 10 MATHEMATICS - ENGLISH ===
        GSEBQuestion(
            text = "If HCF(306, 657) = 9, find LCM(306, 657).",
            section = "A", marks = 1, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Real Numbers", answerHint = "LCM = (306 × 657) / 9 = 22,338"
        ),
        GSEBQuestion(
            text = "The discriminant of quadratic equation 2x² - 4x + 3 = 0 is: (A) -8  (B) 10  (C) -16  (D) 8",
            section = "A", marks = 1, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Quadratic Equations", answerHint = "(A) D = b² - 4ac = 16 - 24 = -8 (No real roots)"
        ),
        GSEBQuestion(
            text = "Find the 10th term of the AP: 2, 7, 12, ...",
            section = "A", marks = 1, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Arithmetic Progressions", answerHint = "a10 = a + 9d = 2 + 9(5) = 47"
        ),
        GSEBQuestion(
            text = "Evaluate: 2 tan² 45° + cos² 30° - sin² 60°.",
            section = "A", marks = 1, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Trigonometry", answerHint = "2(1)² + (√3/2)² - (√3/2)² = 2"
        ),
        GSEBQuestion(
            text = "Prove that √5 is an irrational number.",
            section = "B", marks = 2, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Real Numbers", answerHint = "Assume √5 = a/b (co-prime), 5b² = a², 5 divides a², thus 5 divides a. Contradiction."
        ),
        GSEBQuestion(
            text = "Find a quadratic polynomial whose sum and product of zeroes are -3 and 2 respectively.",
            section = "B", marks = 2, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Polynomials", answerHint = "k[x² - (α+β)x + αβ] = x² + 3x + 2"
        ),
        GSEBQuestion(
            text = "Solve by cross-multiplication or elimination: 8x + 5y = 9 and 3x + 2y = 4.",
            section = "B", marks = 2, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Linear Equations", answerHint = "Multiply eq 2 by 2.5 or eliminate. x = -1, y = 5"
        ),
        GSEBQuestion(
            text = "Find the ratio in which y-axis divides the line segment joining points (5, -6) and (-1, -4).",
            section = "B", marks = 2, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Coordinate Geometry", answerHint = "Point on y-axis is (0, y). 0 = (-k + 5)/(k + 1) => k = 5. Ratio is 5:1."
        ),
        GSEBQuestion(
            text = "Prove that: (sin θ - 2 sin³ θ) / (2 cos³ θ - cos θ) = tan θ.",
            section = "C", marks = 3, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Trigonometry", answerHint = "Factor out sin θ and cos θ. (1 - 2 sin² θ) = (2 cos² θ - 1) = cos 2θ. Result = tan θ."
        ),
        GSEBQuestion(
            text = "The sum of the 4th and 8th terms of an AP is 24 and sum of 6th and 10th terms is 44. Find first three terms.",
            section = "C", marks = 3, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Arithmetic Progressions", answerHint = "2a + 10d = 24, 2a + 14d = 44 => 4d = 20 => d = 5, a = -13. Terms: -13, -8, -3."
        ),
        GSEBQuestion(
            text = "State and Prove Basic Proportionality Theorem (Thales' Theorem) with neat figure and given-to prove data.",
            section = "D", marks = 4, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Triangles", answerHint = "Statement: If a line is drawn parallel to one side of triangle... Proof using ratio of areas."
        ),
        GSEBQuestion(
            text = "A solid consisting of a right circular cone of height 120 cm and radius 60 cm standing on a hemisphere of radius 60 cm is placed upright in a right circular cylinder full of water such that it touches the bottom. Find volume of water left in cylinder if radius of cylinder is 60 cm and height is 180 cm.",
            section = "D", marks = 4, subject = "Mathematics", standard = "Std 10 GSEB", medium = "English",
            chapter = "Surface Areas and Volumes", answerHint = "Vol of cylinder - Vol of solid = πr²h - (1/3πr²h_cone + 2/3πr³) = 1.131 m³"
        ),

        // === STD 10 MATHEMATICS - GUJARATI ===
        GSEBQuestion(
            text = "જો ગુ.સા.અ. (306, 657) = 9 હોય, તો લ.સા.અ. (306, 657) શોધો.",
            section = "A", marks = 1, subject = "Mathematics", standard = "Std 10 GSEB", medium = "Gujarati",
            chapter = "વાસ્તવિક સંખ્યાઓ", answerHint = "લ.સા.અ. = (306 × 657) / 9 = 22,338"
        ),
        GSEBQuestion(
            text = "દ્વિઘાત સમીકરણ 2x² - 4x + 3 = 0 નો વિવેચક D શોધો: (A) -8  (B) 10  (C) -16  (D) 8",
            section = "A", marks = 1, subject = "Mathematics", standard = "Std 10 GSEB", medium = "Gujarati",
            chapter = "દ્વિઘાત સમીકરણ", answerHint = "(A) D = b² - 4ac = 16 - 24 = -8 (વાસ્તવિક બીજ નથી)"
        ),
        GSEBQuestion(
            text = "સાબિત કરો કે √3 એ અસંમેય સંખ્યા છે.",
            section = "B", marks = 2, subject = "Mathematics", standard = "Std 10 GSEB", medium = "Gujarati",
            chapter = "વાસ્તવિક સંખ્યાઓ", answerHint = "ધારો કે √3 = a/b પરસ્પર અવિભાજ્ય પૂર્ણાંક. વિરોધાભાસ રીતથી સાબિતી."
        ),
        GSEBQuestion(
            text = "સાબિત કરો: (sin θ - 2 sin³ θ) / (2 cos³ θ - cos θ) = tan θ.",
            section = "C", marks = 3, subject = "Mathematics", standard = "Std 10 GSEB", medium = "Gujarati",
            chapter = "ત્રિકોણમિતિ", answerHint = "અંશમાંથી sin θ અને છેદમાંથી cos θ સામાન્ય લેતાં (1 - 2 sin² θ) / (2 cos² θ - 1) = 1. પરિણામ tan θ."
        ),
        GSEBQuestion(
            text = "થેલ્સનું પ્રમેય (સમપ્રમાણતાનું મૂળભૂત પ્રમેય) લખો અને આકૃતિ સાથે સાબિત કરો.",
            section = "D", marks = 4, subject = "Mathematics", standard = "Std 10 GSEB", medium = "Gujarati",
            chapter = "ત્રિકોણ", answerHint = "જો ત્રિકોણની કોઈ એક બાજુને સમાંતર દોરેલી રેખા બાકીની બે બાજુઓને ભિન્ન બિંદુઓમાં છેદે..."
        ),

        // === STD 12 SCIENCE - PHYSICS ===
        GSEBQuestion(
            text = "What is the SI unit and dimensional formula of electric permittivity of free space (ε₀)?",
            section = "A", marks = 1, subject = "Physics", standard = "Std 12 Science", medium = "English",
            chapter = "Electrostatics", answerHint = "C²·N⁻¹·m⁻² or F·m⁻¹, [M⁻¹ L⁻³ T⁴ A²]"
        ),
        GSEBQuestion(
            text = "State Kirchhoff's junction rule and loop rule with sign conventions.",
            section = "B", marks = 2, subject = "Physics", standard = "Std 12 Science", medium = "English",
            chapter = "Current Electricity", answerHint = "ΣI = 0 (charge conservation), ΣΔV = 0 (energy conservation)."
        ),
        GSEBQuestion(
            text = "Derive an expression for the magnetic field at a point on the axis of a circular current loop using Biot-Savart Law.",
            section = "C", marks = 3, subject = "Physics", standard = "Std 12 Science", medium = "English",
            chapter = "Magnetic Effects", answerHint = "B = (μ₀·I·R²) / [2(R² + x²)^(3/2)] along axial direction."
        ),
        GSEBQuestion(
            text = "Obtain the lens maker's formula: 1/f = (n - 1) [1/R₁ - 1/R₂] for a double convex lens with proper ray diagram.",
            section = "D", marks = 4, subject = "Physics", standard = "Std 12 Science", medium = "English",
            chapter = "Ray Optics", answerHint = "Refraction at two spherical surfaces: n₂/v₁ - n₁/u = (n₂-n₁)/R₁ and n₁/v - n₂/v₁ = (n₁-n₂)/R₂."
        ),

        // === STD 12 COMMERCE - ACCOUNTANCY ===
        GSEBQuestion(
            text = "What is Revaluation Account? What is its nature: (A) Real (B) Personal (C) Nominal (D) Asset",
            section = "A", marks = 1, subject = "Accountancy", standard = "Std 12 Commerce", medium = "English",
            chapter = "Partnership Accounts", answerHint = "(C) Nominal Account (Profit & Loss Adjustment)"
        ),
        GSEBQuestion(
            text = "State four differences between Sacrificing Ratio and Gaining Ratio.",
            section = "B", marks = 2, subject = "Accountancy", standard = "Std 12 Commerce", medium = "English",
            chapter = "Admission & Retirement", answerHint = "Meaning, Time of calculation, Formula (Old - New vs New - Old), Purpose."
        ),
        GSEBQuestion(
            text = "Explain Weighted Average Profit method of Goodwill valuation with formula and suitable illustration.",
            section = "C", marks = 3, subject = "Accountancy", standard = "Std 12 Commerce", medium = "English",
            chapter = "Valuation of Goodwill", answerHint = "Weighted Profit = Σ(Profit × Weight) / ΣWeights. Goodwill = Weighted Avg Profit × No. of Years Purchase."
        ),
        GSEBQuestion(
            text = "Pass necessary journal entries for forfeiture and re-issue of 500 equity shares of ₹10 each issued at 10% premium.",
            section = "D", marks = 4, subject = "Accountancy", standard = "Std 12 Commerce", medium = "English",
            chapter = "Company Accounts", answerHint = "Share Capital A/c Dr, Securities Premium Dr (if unpaid), To Calls in Arrears, To Share Forfeiture."
        )
    )

    fun generatePaper(
        standard: String,
        subject: String,
        medium: String,
        totalMarks: Int,
        durationMinutes: Int
    ): Pair<String, String> {
        // Filter matching questions or fallback
        val pool = questions.filter {
            (it.standard == standard || standard.contains("Std 10") && it.standard.contains("Std 10")) &&
            (it.subject.equals(subject, ignoreCase = true) || subject.contains(it.subject))
        }.let { if (it.isEmpty()) questions else it }

        val secA = pool.filter { it.section == "A" }.shuffled().take(if (totalMarks <= 25) 5 else if (totalMarks <= 50) 10 else 16)
        val secB = pool.filter { it.section == "B" }.shuffled().take(if (totalMarks <= 25) 4 else if (totalMarks <= 50) 8 else 10)
        val secC = pool.filter { it.section == "C" }.shuffled().take(if (totalMarks <= 25) 2 else if (totalMarks <= 50) 5 else 8)
        val secD = pool.filter { it.section == "D" }.shuffled().take(if (totalMarks <= 25) 1 else if (totalMarks <= 50) 3 else 5)

        val secAQuestions = if (secA.isNotEmpty()) secA.mapIndexed { idx, q -> "${idx + 1}. ${q.text}" } else listOf("1. Solve the given objective question according to syllabus.")
        val secBQuestions = if (secB.isNotEmpty()) secB.mapIndexed { idx, q -> "${idx + 1 + secAQuestions.size}. ${q.text}" } else listOf("${secAQuestions.size + 1}. Answer in brief with two points.")
        val secCQuestions = if (secC.isNotEmpty()) secC.mapIndexed { idx, q -> "${idx + 1 + secAQuestions.size + secBQuestions.size}. ${q.text}" } else listOf("${secAQuestions.size + secBQuestions.size + 1}. Explain with formula and diagram.")
        val secDQuestions = if (secD.isNotEmpty()) secD.mapIndexed { idx, q -> "${idx + 1 + secAQuestions.size + secBQuestions.size + secCQuestions.size}. ${q.text}" } else listOf("${secAQuestions.size + secBQuestions.size + secCQuestions.size + 1}. State and prove the principal theorem.")

        val isGujarati = medium.equals("Gujarati", ignoreCase = true)
        val secAName = if (isGujarati) "વિભાગ A (હેતુલક્ષી પ્રશ્નો - ૧ ગુણ)" else "SECTION A (Objective & MCQs - 1 Mark each)"
        val secBName = if (isGujarati) "વિભાગ B (ટૂંકજવાબી પ્રશ્નો - ૨ ગુણ)" else "SECTION B (Short Questions - 2 Marks each)"
        val secCName = if (isGujarati) "વિભાગ C (મુદ્દાસર પ્રશ્નો - ૩ ગુણ)" else "SECTION C (Analytical Questions - 3 Marks each)"
        val secDName = if (isGujarati) "વિભાગ D (વિસ્તૃત પ્રશ્નો - ૪ ગુણ)" else "SECTION D (Long Essay & Theorems - 4 Marks each)"

        // Build clean JSON
        val escape = { str: String -> str.replace("\"", "\\\"").replace("\n", "\\n") }
        val jsonBuilder = StringBuilder("[")
        val sections = listOf(
            Triple(secAName, "Answer all questions.", secAQuestions),
            Triple(secBName, "Answer questions in 30-40 words.", secBQuestions),
            Triple(secCName, "Answer questions with calculations/diagrams.", secCQuestions),
            Triple(secDName, "Answer in detail with step-by-step working.", secDQuestions)
        )

        sections.forEachIndexed { sIdx, (title, instr, qList) ->
            if (sIdx > 0) jsonBuilder.append(",")
            jsonBuilder.append("""{"sectionName":"${escape(title)}","instructions":"${escape(instr)}","questions":[""")
            qList.forEachIndexed { qIdx, q ->
                if (qIdx > 0) jsonBuilder.append(",")
                jsonBuilder.append("\"${escape(q)}\"")
            }
            jsonBuilder.append("]}")
        }
        jsonBuilder.append("]")

        val answerKey = buildString {
            append("=== ADVANCE ACADEMY GSEB MARKING SCHEME ===\n\n")
            append("Section A Solutions:\n")
            secA.forEachIndexed { idx, q -> append("${idx + 1}. ${q.answerHint.ifEmpty { "Step 1 = 1 mark" }}\n") }
            append("\nSection B Solutions:\n")
            secB.forEachIndexed { idx, q -> append("${idx + 1}. ${q.answerHint.ifEmpty { "Formula (1 mark) + Calculation (1 mark)" }}\n") }
            append("\nSection C & D Guidelines:\n")
            append("• Given data and diagram: 1 mark\n• Main theorem / derivation / steps: 2-3 marks\n• Final statement with SI units / answer box: 1 mark.")
        }

        return Pair(jsonBuilder.toString(), answerKey)
    }
}
