// Copyright 2025, compose-miuix-ui contributors
// SPDX-License-Identifier: Apache-2.0

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suqi8.coui.kmp.basic.BasicComponent
import com.suqi8.coui.kmp.basic.InputField
import com.suqi8.coui.kmp.basic.ScrollBehavior
import com.suqi8.coui.kmp.basic.SearchBar
import com.suqi8.coui.kmp.basic.SmallTitle
import com.suqi8.coui.kmp.basic.Text
import com.suqi8.coui.kmp.icon.MiuixIcons
import com.suqi8.coui.kmp.icon.icons.useful.AddSecret
import com.suqi8.coui.kmp.icon.icons.useful.Back
import com.suqi8.coui.kmp.icon.icons.useful.Blocklist
import com.suqi8.coui.kmp.icon.icons.useful.Cancel
import com.suqi8.coui.kmp.icon.icons.useful.Confirm
import com.suqi8.coui.kmp.icon.icons.useful.Copy
import com.suqi8.coui.kmp.icon.icons.useful.Cut
import com.suqi8.coui.kmp.icon.icons.useful.Delete
import com.suqi8.coui.kmp.icon.icons.useful.Edit
import com.suqi8.coui.kmp.icon.icons.useful.ImmersionMore
import com.suqi8.coui.kmp.icon.icons.useful.Info
import com.suqi8.coui.kmp.icon.icons.useful.Like
import com.suqi8.coui.kmp.icon.icons.useful.More
import com.suqi8.coui.kmp.icon.icons.useful.Move
import com.suqi8.coui.kmp.icon.icons.useful.NavigatorSwitch
import com.suqi8.coui.kmp.icon.icons.useful.New
import com.suqi8.coui.kmp.icon.icons.useful.Order
import com.suqi8.coui.kmp.icon.icons.useful.Paste
import com.suqi8.coui.kmp.icon.icons.useful.Pause
import com.suqi8.coui.kmp.icon.icons.useful.Personal
import com.suqi8.coui.kmp.icon.icons.useful.Play
import com.suqi8.coui.kmp.icon.icons.useful.Reboot
import com.suqi8.coui.kmp.icon.icons.useful.Redo
import com.suqi8.coui.kmp.icon.icons.useful.Refresh
import com.suqi8.coui.kmp.icon.icons.useful.Remove
import com.suqi8.coui.kmp.icon.icons.useful.RemoveBlocklist
import com.suqi8.coui.kmp.icon.icons.useful.RemoveSecret
import com.suqi8.coui.kmp.icon.icons.useful.Rename
import com.suqi8.coui.kmp.icon.icons.useful.Restore
import com.suqi8.coui.kmp.icon.icons.useful.Save
import com.suqi8.coui.kmp.icon.icons.useful.Scan
import com.suqi8.coui.kmp.icon.icons.useful.Search
import com.suqi8.coui.kmp.icon.icons.useful.SelectAll
import com.suqi8.coui.kmp.icon.icons.useful.Settings
import com.suqi8.coui.kmp.icon.icons.useful.Share
import com.suqi8.coui.kmp.icon.icons.useful.Stick
import com.suqi8.coui.kmp.icon.icons.useful.Undo
import com.suqi8.coui.kmp.icon.icons.useful.Unlike
import com.suqi8.coui.kmp.icon.icons.useful.Unstick
import com.suqi8.coui.kmp.icon.icons.useful.Update
import com.suqi8.coui.kmp.theme.COUITheme
import com.suqi8.coui.kmp.utils.getWindowSize
import com.suqi8.coui.kmp.utils.overScrollVertical
import com.suqi8.coui.kmp.utils.scrollEndHaptic
import component.TextComponent
import component.otherComponent

@Composable
fun MainPage(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    scrollEndHaptic: Boolean,
) {
    var searchValue by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val showDialog = remember { mutableStateOf(false) }
    val dialogTextFieldValue = remember { mutableStateOf("") }
    val showBottomSheet = remember { mutableStateOf(false) }
    val bottomSheetDropdownSelectedOption = remember { mutableStateOf(0) }
    val bottomSheetSuperSwitchState = remember { mutableStateOf(true) }
    val checkbox = remember { mutableStateOf(false) }
    val checkboxTrue = remember { mutableStateOf(true) }
    val switch = remember { mutableStateOf(false) }
    val switchTrue = remember { mutableStateOf(true) }
    val dropdownOptionSelected = remember { mutableStateOf(0) }
    val spinnerOptionSelected = remember { mutableStateOf(0) }
    val spinnerOptionSelectedDialog = remember { mutableStateOf(2) }
    val superCheckbox = remember { mutableStateOf("State: false") }
    val superCheckboxState = remember { mutableStateOf(false) }
    val superRightCheckbox = remember { mutableStateOf("false") }
    val superRightCheckboxState = remember { mutableStateOf(false) }
    val superSwitch = remember { mutableStateOf("false") }
    val superSwitchState = remember { mutableStateOf(false) }
    val superSwitchAnimState = remember { mutableStateOf(false) }

    val notExpanded by remember { derivedStateOf { !expanded } }

    val textComponent = @Composable {
        TextComponent(
            showDialog,
            dialogTextFieldValue,
            showBottomSheet = showBottomSheet,
            bottomSheetDropdownSelectedOption,
            bottomSheetSuperSwitchState,
            checkbox,
            checkboxTrue,
            switch,
            switchTrue,
            dropdownOptionSelected,
            spinnerOptionSelected,
            spinnerOptionSelectedDialog,
            superCheckbox,
            superCheckboxState,
            superRightCheckbox,
            superRightCheckboxState,
            superSwitch,
            superSwitchState,
            superSwitchAnimState
        )
    }

    val miuixIcons = remember {
        listOf(
            MiuixIcons.Useful.AddSecret,
            MiuixIcons.Useful.Back,
            MiuixIcons.Useful.Blocklist,
            MiuixIcons.Useful.Cancel,
            MiuixIcons.Useful.Confirm,
            MiuixIcons.Useful.Copy,
            MiuixIcons.Useful.Cut,
            MiuixIcons.Useful.Delete,
            MiuixIcons.Useful.Edit,
            MiuixIcons.Useful.ImmersionMore,
            MiuixIcons.Useful.Info,
            MiuixIcons.Useful.Like,
            MiuixIcons.Useful.More,
            MiuixIcons.Useful.Move,
            MiuixIcons.Useful.NavigatorSwitch,
            MiuixIcons.Useful.New,
            MiuixIcons.Useful.Order,
            MiuixIcons.Useful.Paste,
            MiuixIcons.Useful.Pause,
            MiuixIcons.Useful.Personal,
            MiuixIcons.Useful.Play,
            MiuixIcons.Useful.Reboot,
            MiuixIcons.Useful.Redo,
            MiuixIcons.Useful.Refresh,
            MiuixIcons.Useful.Remove,
            MiuixIcons.Useful.RemoveBlocklist,
            MiuixIcons.Useful.RemoveSecret,
            MiuixIcons.Useful.Rename,
            MiuixIcons.Useful.Restore,
            MiuixIcons.Useful.Save,
            MiuixIcons.Useful.Scan,
            MiuixIcons.Useful.Search,
            MiuixIcons.Useful.SelectAll,
            MiuixIcons.Useful.Settings,
            MiuixIcons.Useful.Share,
            MiuixIcons.Useful.Stick,
            MiuixIcons.Useful.Undo,
            MiuixIcons.Useful.Unlike,
            MiuixIcons.Useful.Unstick,
            MiuixIcons.Useful.Update
        )
    }

    val windowSize = getWindowSize()
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier
            .then(
                if (scrollEndHaptic) Modifier.scrollEndHaptic() else Modifier
            )
            .overScrollVertical()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
            .height(windowSize.height.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()),
        overscrollEffect = null,
    ) {
        item(key = "searchbar") {
            SmallTitle(text = "SearchBar")
            SearchBar(
                modifier = Modifier.padding(bottom = 12.dp),
                inputField = {
                    InputField(
                        query = searchValue,
                        onQueryChange = { searchValue = it },
                        onSearch = { expanded = false },
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        label = "Search"
                    )
                },
                outsideRightAction = {
                    Text(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                expanded = false
                                searchValue = ""
                            },
                        text = "Cancel",
                        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Bold),
                        color = COUITheme.colorScheme.primary
                    )
                },
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                Column {
                    repeat(4) { idx ->
                        val resultText = "Suggestion $idx"
                        BasicComponent(
                            title = resultText,
                            onClick = {
                                searchValue = resultText
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        if (notExpanded) {
            item(key = "textComponent") {
                textComponent()
            }
            otherComponent(miuixIcons, focusManager, padding)
        }
    }
}
