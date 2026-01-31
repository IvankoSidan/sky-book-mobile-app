package com.wheezy.myjetpackproject.core.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.wheezy.myjetpackproject.core.model.Notification
import com.wheezy.myjetpackproject.core.model.User
import com.wheezy.myjetpackproject.core.ui.R
import com.wheezy.myjetpackproject.core.common.utils.format

@Composable
fun TopBar(
    user: User? = null,
    notifications: List<Notification>,
    unreadCount: Int,
    isLoading: Boolean,
    onOpenDrawer: () -> Unit = {},
    onRefreshNotifications: () -> Unit = {},
    onDeleteAllNotifications: () -> Unit = {}
) {
    var showDropdown by remember { mutableStateOf(false) }
    val profileSize = 60.dp
    val iconSize = 45.dp

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
                    .constrainAs(world) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )

            if (user?.profilePicture != null) {
                AsyncImage(
                    model = user.profilePicture,
                    contentDescription = null,
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
                    contentDescription = null,
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
                    contentDescription = null,
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
                        if (showDropdown) onRefreshNotifications()
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bell_icon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .offset(x = 4.dp, y = (-4).dp)
                            .size(18.dp)
                            .background(Color.Red, CircleShape)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                            fontSize = 10.sp,
                            lineHeight = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            style = LocalTextStyle.current.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            modifier = Modifier.wrapContentSize()
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
            enter = fadeIn(animationSpec = tween(400)) + scaleIn() + slideInVertically(),
            exit = fadeOut(animationSpec = tween(250)) + scaleOut() + slideOutVertically(),
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
                    .animateContentSize()
            ) {
                NotificationsDropdown(
                    notifications = notifications,
                    onClose = { showDropdown = false },
                    onClearAll = onDeleteAllNotifications
                )
            }
        }
    }
}

@Composable
fun NotificationsDropdown(
    notifications: List<Notification>,
    onClose: () -> Unit,
    onClearAll: () -> Unit
) {
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
                Row {
                    if (notifications.isNotEmpty()) {
                        IconButton(onClick = onClearAll) {
                            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.outline)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (notifications.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No new notifications", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    notifications.forEach { notification ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (notification.isRead) Color.Transparent else MaterialTheme.colorScheme.primary.copy(0.1f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(notification.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                                Text(notification.timestamp.format(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    }
                }
            }
        }
    }
}