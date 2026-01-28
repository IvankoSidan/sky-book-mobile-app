package com.wheezy.myjetpackproject.UI.Components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.wheezy.myjetpackproject.Data.Model.Notification
import com.wheezy.myjetpackproject.Data.Model.User
import com.wheezy.myjetpackproject.R
import com.wheezy.myjetpackproject.Utils.format
import com.wheezy.myjetpackproject.ViewModel.TopBarViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.ColorFilter

@Composable
fun TopBar(
    user: User? = null,
    viewModel: TopBarViewModel,
    onOpenDrawer: () -> Unit = {}
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = viewModel.unreadCount
    val isLoading by viewModel.isLoading.collectAsState()
    var showDropdown by remember { mutableStateOf(false) }
    val profileSize = 60.dp
    val iconSize = 45.dp

    var bellPosition by remember { mutableStateOf(Offset(0f, 0f)) }

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    Box {
        ConstraintLayout(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val (world, name, profile, notification, title, menuButton) = createRefs()

            Image(
                painter = painterResource(id = R.drawable.world),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                modifier = Modifier
                    .clickable { }
                    .constrainAs(world) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            if (user?.profilePicture != null) {
                AsyncImage(
                    model = user.profilePicture,
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(profileSize)
                        .clip(CircleShape)
                        .constrainAs(profile) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        },
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Default avatar",
                    modifier = Modifier
                        .size(profileSize)
                        .clip(CircleShape)
                        .constrainAs(profile) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                )
            }

            IconButton(
                onClick = { onOpenDrawer() },
                modifier = Modifier
                    .size(iconSize)
                    .constrainAs(menuButton) {
                        end.linkTo(notification.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_menu),
                    contentDescription = "Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier
                    .size(iconSize)
                    .constrainAs(notification) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                    .clickable {
                        showDropdown = !showDropdown
                        if (showDropdown) viewModel.loadNotifications()
                    }
                    .onGloballyPositioned { coordinates ->
                        bellPosition = coordinates.positionInWindow()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bell_icon),
                    contentDescription = "Notifications",
                    modifier = Modifier.fillMaxSize()
                )

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(MaterialTheme.colorScheme.error, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = unreadCount.toString(),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onError,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isLoading && showDropdown) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            }

            Text(
                text = user?.name ?: "Guest",
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .constrainAs(name) {
                        top.linkTo(parent.top)
                        start.linkTo(profile.end)
                        bottom.linkTo(parent.bottom)
                    }
            )

            Text(
                text = stringResource(id = R.string.dashboard_title),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier.constrainAs(title) {
                    top.linkTo(profile.bottom)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
            )
        }

        AnimatedVisibility(
            visible = showDropdown,
            enter = fadeIn(animationSpec = tween(400, 50, LinearOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.9f,
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) +
                    slideInVertically(
                        initialOffsetY = { -50 },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ),
            exit = fadeOut(animationSpec = tween(250, easing = FastOutLinearInEasing)) +
                    scaleOut(
                        targetScale = 0.95f,
                        animationSpec = tween(250, easing = FastOutLinearInEasing)
                    ) +
                    slideOutVertically(
                        targetOffsetY = { -30 },
                        animationSpec = tween(250, easing = FastOutLinearInEasing)
                    ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 70.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 4.dp,
                shadowElevation = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize(tween(300))
            ) {
                NotificationsDropdown(
                    notifications = notifications,
                    onClose = { showDropdown = false },
                    onClearAll = { viewModel.deleteAllNotifications() }
                )
            }
        }
    }
}

@Composable
fun NotificationsDropdown(
    notifications: List<Notification>,
    onClose: () -> Unit,
    onClearAll: () -> Unit = {}
) {
    var showClearAllConfirmation by remember { mutableStateOf(false) }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Notifications",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = {
                            onClearAll()
                            showClearAllConfirmation = true
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(2000)
                                showClearAllConfirmation = false
                            }
                        }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Clear all notifications",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close notifications",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                notifications.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No new notifications",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                showClearAllConfirmation -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "All notifications cleared",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        notifications.forEach { notification ->
                            val isUnread = !notification.isRead
                            val backgroundColor =
                                if (isUnread) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                                else Color.Transparent

                            val (icon, iconTint) = when {
                                "report" in notification.message.lowercase() ->
                                    Icons.Default.Assessment to MaterialTheme.colorScheme.tertiary

                                "message" in notification.message.lowercase() ->
                                    Icons.Default.Email to MaterialTheme.colorScheme.secondary

                                else -> Icons.Default.Info to MaterialTheme.colorScheme.primary
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor, RoundedCornerShape(8.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = iconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        notification.message,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isUnread) FontWeight.SemiBold else FontWeight.Normal,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        notification.timestamp.format(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
