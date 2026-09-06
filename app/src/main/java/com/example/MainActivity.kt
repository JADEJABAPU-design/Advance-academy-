package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AcademyTopBar
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.FeesScreen
import com.example.ui.screens.GSEBPaperGeneratorScreen
import com.example.ui.screens.NoticesScreen
import com.example.ui.screens.ParentVMSScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyEmerald
import com.example.ui.theme.AcademyGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AcademyViewModel
import com.example.ui.viewmodel.AppRole

data class NavTabItem(
    val label: String,
    val icon: ImageVector,
    val testTag: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AdvanceAcademyApp()
            }
        }
    }
}

@Composable
fun AdvanceAcademyApp(
    viewModel: AcademyViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val allNotices by viewModel.allNotices.collectAsStateWithLifecycle()
    val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

    var selectedStaffTab by remember { mutableIntStateOf(0) }
    var selectedParentTab by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    val staffNavItems = listOf(
        NavTabItem("Attendance", Icons.Default.CheckCircle, "nav_attendance"),
        NavTabItem("Schedule", Icons.Default.CalendarToday, "nav_schedule"),
        NavTabItem("Fees", Icons.Default.AccountBalanceWallet, "nav_fees"),
        NavTabItem("GSEB Papers", Icons.Default.MenuBook, "nav_gseb_papers"),
        NavTabItem("Updates", Icons.Default.Notifications, "nav_updates")
    )

    val parentNavItems = listOf(
        NavTabItem("VMS Portal", Icons.Default.FamilyRestroom, "nav_parent_vms"),
        NavTabItem("Timetable", Icons.Default.CalendarToday, "nav_parent_schedule"),
        NavTabItem("Fees", Icons.Default.Receipt, "nav_parent_fees"),
        NavTabItem("GSEB Papers", Icons.Default.MenuBook, "nav_parent_gseb"),
        NavTabItem("Updates", Icons.Default.Notifications, "nav_parent_notices")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AcademyTopBar(
                currentRole = currentRole,
                onToggleRole = {
                    viewModel.setRole(
                        if (currentRole == AppRole.ADMIN_FACULTY) AppRole.PARENT_VMS else AppRole.ADMIN_FACULTY
                    )
                },
                unreadNoticesCount = allNotices.count { !it.isReadByParent },
                onNotificationsClick = {
                    if (currentRole == AppRole.ADMIN_FACULTY) {
                        selectedStaffTab = 4
                    } else {
                        selectedParentTab = 4
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = AcademyBlue,
                tonalElevation = 8.dp
            ) {
                if (currentRole == AppRole.ADMIN_FACULTY) {
                    staffNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedStaffTab == index,
                            onClick = { selectedStaffTab = index },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedStaffTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AcademyBlue,
                                selectedTextColor = AcademyBlue,
                                indicatorColor = AcademyGold.copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                } else {
                    parentNavItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedParentTab == index,
                            onClick = { selectedParentTab = index },
                            icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedParentTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AcademyBlue,
                                selectedTextColor = AcademyBlue,
                                indicatorColor = AcademyEmerald.copy(alpha = 0.25f)
                            ),
                            modifier = Modifier.testTag(item.testTag)
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (currentRole == AppRole.ADMIN_FACULTY) {
                when (selectedStaffTab) {
                    0 -> AttendanceScreen(viewModel = viewModel)
                    1 -> ScheduleScreen(viewModel = viewModel)
                    2 -> FeesScreen(viewModel = viewModel)
                    3 -> GSEBPaperGeneratorScreen(viewModel = viewModel)
                    4 -> NoticesScreen(viewModel = viewModel)
                }
            } else {
                when (selectedParentTab) {
                    0 -> ParentVMSScreen(viewModel = viewModel)
                    1 -> ScheduleScreen(viewModel = viewModel)
                    2 -> FeesScreen(viewModel = viewModel)
                    3 -> GSEBPaperGeneratorScreen(viewModel = viewModel)
                    4 -> NoticesScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
