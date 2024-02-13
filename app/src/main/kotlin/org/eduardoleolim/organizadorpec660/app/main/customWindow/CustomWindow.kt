package org.eduardoleolim.organizadorpec660.app.main.customWindow

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import org.eduardoleolim.organizadorpec660.app.main.customWindow.utils.CustomWindowDecorationAccessing

@Composable
private fun FrameWindowScope.CustomWindowFrame(
    onRequestMinimize: (() -> Unit)?,
    onRequestClose: () -> Unit,
    onRequestToggleMaximize: (() -> Unit)?,
    title: String,
    windowIcon: ImageVector? = null,
    center: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides MaterialTheme.colorScheme.onBackground,
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            SnapDraggableToolbar(
                title = title,
                windowIcon = windowIcon,
                center = center,
                onRequestMinimize = onRequestMinimize,
                onRequestClose = onRequestClose,
                onRequestToggleMaximize = onRequestToggleMaximize
            )
            content()
        }
    }
}

@Composable
fun isWindowFocused(): Boolean {
    return LocalWindowInfo.current.isWindowFocused
}

@Composable
fun isWindowMaximized(): Boolean {
    return LocalWindowState.current.placement == WindowPlacement.Maximized
}

@Composable
fun isWindowFloating(): Boolean {
    return LocalWindowState.current.placement == WindowPlacement.Floating
}

@Composable
fun FrameWindowScope.SnapDraggableToolbar(
    title: String,
    windowIcon: ImageVector? = null,
    center: @Composable () -> Unit,
    onRequestMinimize: (() -> Unit)?,
    onRequestToggleMaximize: (() -> Unit)?,
    onRequestClose: () -> Unit,
) {
    ProvideWindowSpotContainer {
        if (CustomWindowDecorationAccessing.isSupported) {
            FrameContent(title, windowIcon, center, onRequestMinimize, onRequestToggleMaximize, onRequestClose)
        } else {
            WindowDraggableArea {
                FrameContent(title, windowIcon, center, onRequestMinimize, onRequestToggleMaximize, onRequestClose)
            }
        }
    }
}

@Composable
private fun FrameWindowScope.FrameContent(
    title: String,
    windowIcon: ImageVector? = null,
    center: @Composable () -> Unit,
    onRequestMinimize: (() -> Unit)?,
    onRequestToggleMaximize: (() -> Unit)?,
    onRequestClose: () -> Unit,
) {
    Row(
        Modifier.fillMaxWidth().height(40.dp).background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(24.dp))
        windowIcon?.let {
            Icon(it, null)
            Spacer(Modifier.width(8.dp))
        }

        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onBackground.copy(0.75f)
        ) {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                modifier = Modifier.windowFrameItem("title", HitSpots.DRAGGABLE_AREA),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Box(Modifier.weight(1f)) {
            center()
        }
        WindowsActionButtons(
            onRequestClose,
            onRequestMinimize,
            onRequestToggleMaximize,
        )
    }
}

@Composable
fun CustomWindow(
    state: WindowState,
    onCloseRequest: () -> Unit,
    onRequestMinimize: (() -> Unit)? = {
        state.isMinimized = true
    },
    onRequestToggleMaximize: (() -> Unit)? = {
        if (state.placement == WindowPlacement.Maximized) {
            state.placement = WindowPlacement.Floating
        } else {
            state.placement = WindowPlacement.Maximized
        }
    },
    defaultTitle: String = "Untitled",
    defaultIcon: Painter? = null,
    content: @Composable FrameWindowScope.() -> Unit,
) {
    //two-way binding
    val windowController = remember { WindowController() }
    val center = windowController.center ?: {}

    val transparent: Boolean
    val undecorated: Boolean
    val isAeroSnapSupported = CustomWindowDecorationAccessing.isSupported

    if (isAeroSnapSupported) {
        transparent = false
        undecorated = false
    } else {
        transparent = true
        undecorated = true
    }

    Window(
        state = state,
        transparent = transparent,
        undecorated = undecorated,
        icon = defaultIcon,
        onCloseRequest = onCloseRequest,
    ) {
        val title = windowController.title ?: defaultTitle
        LaunchedEffect(title) {
            window.title = title
        }
        CompositionLocalProvider(
            LocalWindowController provides windowController,
            LocalWindowState provides state,
        ) {
            val icon by rememberUpdatedState(windowController.icon)
            // val onIconClick by rememberUpdatedState(windowController.onIconClick)

            CustomWindowFrame(
                onRequestMinimize = onRequestMinimize,
                onRequestClose = onCloseRequest,
                onRequestToggleMaximize = onRequestToggleMaximize,
                title = title,
                windowIcon = icon,
                center = { center() }
            ) {
                content()
            }
        }
    }
}


class WindowController {
    var title by mutableStateOf(null as String?)
    var icon by mutableStateOf(null as ImageVector?)
    var onIconClick by mutableStateOf(null as (() -> Unit)?)
    var center: (@Composable () -> Unit)? by mutableStateOf(null)
}

private val LocalWindowController = compositionLocalOf<WindowController> { error("window controller not provided") }
private val LocalWindowState = compositionLocalOf<WindowState> { error("window controller not provided") }

@Composable
fun WindowCenter(content: @Composable () -> Unit) {
    val c = LocalWindowController.current

    DisposableEffect(Unit) {
        c.center = content
        onDispose {
            c.center = null
        }
    }
}

@Composable
fun WindowTitle(title: String) {
    val c = LocalWindowController.current
    LaunchedEffect(title) {
        c.title = title
    }
    DisposableEffect(Unit) {
        onDispose {
            c.title = null
        }
    }
}

@Composable
fun WindowIcon(icon: ImageVector, onClick: () -> Unit) {
    val current = LocalWindowController.current
    DisposableEffect(icon) {
        current.let {
            it.icon = icon
            it.onIconClick = onClick

            onDispose {
                it.icon = null
                it.onIconClick = null
            }
        }
    }
}
