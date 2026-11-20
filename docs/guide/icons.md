# Icon System

COUI has two sets of icons:

- **Basic icons** (`COUIIcons.Basic.*`, in `coui-ui`): the few icons COUI's own components use, such as arrows and the check mark.
- **Extended icons** (`COUIIcons.<Name>`, in `coui-icons`): the icons ColorOS 17 draws, taken from the ColorOS system apps.

Extended icons come in a single weight. Their default size is 24 dp. They are drawn in one color, so set the color with `Icon`'s `tint`.

## Installation

The basic `coui-ui` library includes only a small set of essential icons. To access the full collection of COUI icons, you need to add the `coui-icons` dependency to your project.

```kotlin
// build.gradle.kts
commonMain.dependencies {
    implementation("io.github.suqi8.coui.kmp:coui-ui:<version>")
    // Add the extended icon library
    implementation("io.github.suqi8.coui.kmp:coui-icons:<version>")
}
```

## Usage

Each extended icon is an extension property on `COUIIcons`, imported from `io.github.suqi8.coui.kmp.icon.extended`:

```kotlin
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.suqi8.coui.kmp.basic.Icon
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Ok
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.theme.COUITheme
```

Then use them with `Icon`:

```kotlin
// 24 dp, tinted with the default content color
Icon(
    imageVector = COUIIcons.Ok,
    contentDescription = "Ok",
)

// Another size and color
Icon(
    imageVector = COUIIcons.Settings,
    contentDescription = "Settings",
    tint = COUITheme.colorScheme.primary,
    modifier = Modifier.size(32.dp),
)
```

## Available Icons

### Basic Icons

Basic icons include commonly used basic UI elements such as arrows and checkmarks. These icons are also used in COUI's own components. Below is the complete list:

| Icon Name | Icon |
|---|---|
| `ArrowRight` | <img src="/icons/basic/ArrowRight.svg" width="24" height="24" /> |
| `ArrowUpDown` | <img src="/icons/basic/ArrowUpDown.svg" width="24" height="24" /> |
| `Check` | <img src="/icons/basic/Check.svg" width="24" height="24" /> |
| `Close` | <img src="/icons/basic/Close.svg" width="24" height="24" /> |
| `More` | <img src="/icons/basic/More.svg" width="24" height="24" /> |
| `Reset` | <img src="/icons/basic/Reset.svg" width="24" height="24" /> |
| `Search` | <img src="/icons/basic/Search.svg" width="24" height="24" /> |
| `SearchCleanup` | <img src="/icons/basic/SearchCleanup.svg" width="24" height="24" /> |
| `Sidebar` | <img src="/icons/basic/Sidebar.svg" width="24" height="24" /> |

### Extended Icons

Extended icons include a wide variety of icons for different use cases. Below is the complete list:

| Icon Name | Icon |
|---|---|
| `Add` | <img src="/icons/extended/Add.svg" width="24" height="24" /> |
| `AddCircle` | <img src="/icons/extended/AddCircle.svg" width="24" height="24" /> |
| `AddFolder` | <img src="/icons/extended/AddFolder.svg" width="24" height="24" /> |
| `Alarm` | <img src="/icons/extended/Alarm.svg" width="24" height="24" /> |
| `Album` | <img src="/icons/extended/Album.svg" width="24" height="24" /> |
| `All` | <img src="/icons/extended/All.svg" width="24" height="24" /> |
| `Answer` | <img src="/icons/extended/Answer.svg" width="24" height="24" /> |
| `AppRecording` | <img src="/icons/extended/AppRecording.svg" width="24" height="24" /> |
| `Back` | <img src="/icons/extended/Back.svg" width="24" height="24" /> |
| `Background` | <img src="/icons/extended/Background.svg" width="24" height="24" /> |
| `Backup` | <img src="/icons/extended/Backup.svg" width="24" height="24" /> |
| `BankCards` | <img src="/icons/extended/BankCards.svg" width="24" height="24" /> |
| `Blocklist` | <img src="/icons/extended/Blocklist.svg" width="24" height="24" /> |
| `CallRecording` | <img src="/icons/extended/CallRecording.svg" width="24" height="24" /> |
| `Carrier` | <img src="/icons/extended/Carrier.svg" width="24" height="24" /> |
| `ChevronBackward` | <img src="/icons/extended/ChevronBackward.svg" width="24" height="24" /> |
| `ChevronForward` | <img src="/icons/extended/ChevronForward.svg" width="24" height="24" /> |
| `Clear` | <img src="/icons/extended/Clear.svg" width="24" height="24" /> |
| `Close` | <img src="/icons/extended/Close.svg" width="24" height="24" /> |
| `Close2` | <img src="/icons/extended/Close2.svg" width="24" height="24" /> |
| `CloudFill` | <img src="/icons/extended/CloudFill.svg" width="24" height="24" /> |
| `Community` | <img src="/icons/extended/Community.svg" width="24" height="24" /> |
| `Contacts` | <img src="/icons/extended/Contacts.svg" width="24" height="24" /> |
| `ContactsBook` | <img src="/icons/extended/ContactsBook.svg" width="24" height="24" /> |
| `ContactsCircle` | <img src="/icons/extended/ContactsCircle.svg" width="24" height="24" /> |
| `ConvertFile` | <img src="/icons/extended/ConvertFile.svg" width="24" height="24" /> |
| `Copy` | <img src="/icons/extended/Copy.svg" width="24" height="24" /> |
| `Create` | <img src="/icons/extended/Create.svg" width="24" height="24" /> |
| `Cut` | <img src="/icons/extended/Cut.svg" width="24" height="24" /> |
| `Delete` | <img src="/icons/extended/Delete.svg" width="24" height="24" /> |
| `Download` | <img src="/icons/extended/Download.svg" width="24" height="24" /> |
| `Edit` | <img src="/icons/extended/Edit.svg" width="24" height="24" /> |
| `Email` | <img src="/icons/extended/Email.svg" width="24" height="24" /> |
| `ExpandLess` | <img src="/icons/extended/ExpandLess.svg" width="24" height="24" /> |
| `ExpandMore` | <img src="/icons/extended/ExpandMore.svg" width="24" height="24" /> |
| `Favorites` | <img src="/icons/extended/Favorites.svg" width="24" height="24" /> |
| `FavoritesFill` | <img src="/icons/extended/FavoritesFill.svg" width="24" height="24" /> |
| `File` | <img src="/icons/extended/File.svg" width="24" height="24" /> |
| `FileDownloads` | <img src="/icons/extended/FileDownloads.svg" width="24" height="24" /> |
| `Filter` | <img src="/icons/extended/Filter.svg" width="24" height="24" /> |
| `Folder` | <img src="/icons/extended/Folder.svg" width="24" height="24" /> |
| `FolderFill` | <img src="/icons/extended/FolderFill.svg" width="24" height="24" /> |
| `Forward` | <img src="/icons/extended/Forward.svg" width="24" height="24" /> |
| `GridView` | <img src="/icons/extended/GridView.svg" width="24" height="24" /> |
| `Help` | <img src="/icons/extended/Help.svg" width="24" height="24" /> |
| `Hide` | <img src="/icons/extended/Hide.svg" width="24" height="24" /> |
| `Home` | <img src="/icons/extended/Home.svg" width="24" height="24" /> |
| `HorizontalSplit` | <img src="/icons/extended/HorizontalSplit.svg" width="24" height="24" /> |
| `Image` | <img src="/icons/extended/Image.svg" width="24" height="24" /> |
| `Import` | <img src="/icons/extended/Import.svg" width="24" height="24" /> |
| `Info` | <img src="/icons/extended/Info.svg" width="24" height="24" /> |
| `Layers` | <img src="/icons/extended/Layers.svg" width="24" height="24" /> |
| `Link` | <img src="/icons/extended/Link.svg" width="24" height="24" /> |
| `ListView` | <img src="/icons/extended/ListView.svg" width="24" height="24" /> |
| `Location` | <img src="/icons/extended/Location.svg" width="24" height="24" /> |
| `Lock` | <img src="/icons/extended/Lock.svg" width="24" height="24" /> |
| `MapAlbum` | <img src="/icons/extended/MapAlbum.svg" width="24" height="24" /> |
| `Merge` | <img src="/icons/extended/Merge.svg" width="24" height="24" /> |
| `Messages` | <img src="/icons/extended/Messages.svg" width="24" height="24" /> |
| `Mic` | <img src="/icons/extended/Mic.svg" width="24" height="24" /> |
| `MicSlash` | <img src="/icons/extended/MicSlash.svg" width="24" height="24" /> |
| `MindMap` | <img src="/icons/extended/MindMap.svg" width="24" height="24" /> |
| `Months` | <img src="/icons/extended/Months.svg" width="24" height="24" /> |
| `More` | <img src="/icons/extended/More.svg" width="24" height="24" /> |
| `MoreCircle` | <img src="/icons/extended/MoreCircle.svg" width="24" height="24" /> |
| `MoveFile` | <img src="/icons/extended/MoveFile.svg" width="24" height="24" /> |
| `Music` | <img src="/icons/extended/Music.svg" width="24" height="24" /> |
| `Notes` | <img src="/icons/extended/Notes.svg" width="24" height="24" /> |
| `NotesFill` | <img src="/icons/extended/NotesFill.svg" width="24" height="24" /> |
| `Ok` | <img src="/icons/extended/Ok.svg" width="24" height="24" /> |
| `Paste` | <img src="/icons/extended/Paste.svg" width="24" height="24" /> |
| `Pause` | <img src="/icons/extended/Pause.svg" width="24" height="24" /> |
| `Phone` | <img src="/icons/extended/Phone.svg" width="24" height="24" /> |
| `Photos` | <img src="/icons/extended/Photos.svg" width="24" height="24" /> |
| `Pin` | <img src="/icons/extended/Pin.svg" width="24" height="24" /> |
| `Play` | <img src="/icons/extended/Play.svg" width="24" height="24" /> |
| `Playlist` | <img src="/icons/extended/Playlist.svg" width="24" height="24" /> |
| `Promotions` | <img src="/icons/extended/Promotions.svg" width="24" height="24" /> |
| `Recent` | <img src="/icons/extended/Recent.svg" width="24" height="24" /> |
| `Recording` | <img src="/icons/extended/Recording.svg" width="24" height="24" /> |
| `RecordingTape` | <img src="/icons/extended/RecordingTape.svg" width="24" height="24" /> |
| `Redo` | <img src="/icons/extended/Redo.svg" width="24" height="24" /> |
| `Refresh` | <img src="/icons/extended/Refresh.svg" width="24" height="24" /> |
| `Remove` | <img src="/icons/extended/Remove.svg" width="24" height="24" /> |
| `RemoveContact` | <img src="/icons/extended/RemoveContact.svg" width="24" height="24" /> |
| `Rename` | <img src="/icons/extended/Rename.svg" width="24" height="24" /> |
| `Replace` | <img src="/icons/extended/Replace.svg" width="24" height="24" /> |
| `Reply` | <img src="/icons/extended/Reply.svg" width="24" height="24" /> |
| `ReplyAll` | <img src="/icons/extended/ReplyAll.svg" width="24" height="24" /> |
| `Report` | <img src="/icons/extended/Report.svg" width="24" height="24" /> |
| `Reset` | <img src="/icons/extended/Reset.svg" width="24" height="24" /> |
| `RotateLeft` | <img src="/icons/extended/RotateLeft.svg" width="24" height="24" /> |
| `Scan` | <img src="/icons/extended/Scan.svg" width="24" height="24" /> |
| `ScreenCapture` | <img src="/icons/extended/ScreenCapture.svg" width="24" height="24" /> |
| `ScreenMirroring` | <img src="/icons/extended/ScreenMirroring.svg" width="24" height="24" /> |
| `Search` | <img src="/icons/extended/Search.svg" width="24" height="24" /> |
| `SearchDevice` | <img src="/icons/extended/SearchDevice.svg" width="24" height="24" /> |
| `SelectAll` | <img src="/icons/extended/SelectAll.svg" width="24" height="24" /> |
| `Send` | <img src="/icons/extended/Send.svg" width="24" height="24" /> |
| `Settings` | <img src="/icons/extended/Settings.svg" width="24" height="24" /> |
| `Share` | <img src="/icons/extended/Share.svg" width="24" height="24" /> |
| `Show` | <img src="/icons/extended/Show.svg" width="24" height="24" /> |
| `Sidebar` | <img src="/icons/extended/Sidebar.svg" width="24" height="24" /> |
| `Sort` | <img src="/icons/extended/Sort.svg" width="24" height="24" /> |
| `Stopwatch` | <img src="/icons/extended/Stopwatch.svg" width="24" height="24" /> |
| `Store` | <img src="/icons/extended/Store.svg" width="24" height="24" /> |
| `Tasks` | <img src="/icons/extended/Tasks.svg" width="24" height="24" /> |
| `Th1` | <img src="/icons/extended/Th1.svg" width="24" height="24" /> |
| `Th10` | <img src="/icons/extended/Th10.svg" width="24" height="24" /> |
| `Th11` | <img src="/icons/extended/Th11.svg" width="24" height="24" /> |
| `Th12` | <img src="/icons/extended/Th12.svg" width="24" height="24" /> |
| `Th13` | <img src="/icons/extended/Th13.svg" width="24" height="24" /> |
| `Th14` | <img src="/icons/extended/Th14.svg" width="24" height="24" /> |
| `Th15` | <img src="/icons/extended/Th15.svg" width="24" height="24" /> |
| `Th16` | <img src="/icons/extended/Th16.svg" width="24" height="24" /> |
| `Th17` | <img src="/icons/extended/Th17.svg" width="24" height="24" /> |
| `Th18` | <img src="/icons/extended/Th18.svg" width="24" height="24" /> |
| `Th19` | <img src="/icons/extended/Th19.svg" width="24" height="24" /> |
| `Th2` | <img src="/icons/extended/Th2.svg" width="24" height="24" /> |
| `Th20` | <img src="/icons/extended/Th20.svg" width="24" height="24" /> |
| `Th21` | <img src="/icons/extended/Th21.svg" width="24" height="24" /> |
| `Th22` | <img src="/icons/extended/Th22.svg" width="24" height="24" /> |
| `Th23` | <img src="/icons/extended/Th23.svg" width="24" height="24" /> |
| `Th24` | <img src="/icons/extended/Th24.svg" width="24" height="24" /> |
| `Th25` | <img src="/icons/extended/Th25.svg" width="24" height="24" /> |
| `Th26` | <img src="/icons/extended/Th26.svg" width="24" height="24" /> |
| `Th27` | <img src="/icons/extended/Th27.svg" width="24" height="24" /> |
| `Th28` | <img src="/icons/extended/Th28.svg" width="24" height="24" /> |
| `Th29` | <img src="/icons/extended/Th29.svg" width="24" height="24" /> |
| `Th3` | <img src="/icons/extended/Th3.svg" width="24" height="24" /> |
| `Th30` | <img src="/icons/extended/Th30.svg" width="24" height="24" /> |
| `Th31` | <img src="/icons/extended/Th31.svg" width="24" height="24" /> |
| `Th4` | <img src="/icons/extended/Th4.svg" width="24" height="24" /> |
| `Th5` | <img src="/icons/extended/Th5.svg" width="24" height="24" /> |
| `Th6` | <img src="/icons/extended/Th6.svg" width="24" height="24" /> |
| `Th7` | <img src="/icons/extended/Th7.svg" width="24" height="24" /> |
| `Th8` | <img src="/icons/extended/Th8.svg" width="24" height="24" /> |
| `Th9` | <img src="/icons/extended/Th9.svg" width="24" height="24" /> |
| `Theme` | <img src="/icons/extended/Theme.svg" width="24" height="24" /> |
| `Timer` | <img src="/icons/extended/Timer.svg" width="24" height="24" /> |
| `TopDownloads` | <img src="/icons/extended/TopDownloads.svg" width="24" height="24" /> |
| `Translate` | <img src="/icons/extended/Translate.svg" width="24" height="24" /> |
| `Trim` | <img src="/icons/extended/Trim.svg" width="24" height="24" /> |
| `Tune` | <img src="/icons/extended/Tune.svg" width="24" height="24" /> |
| `Undo` | <img src="/icons/extended/Undo.svg" width="24" height="24" /> |
| `Unlock` | <img src="/icons/extended/Unlock.svg" width="24" height="24" /> |
| `Unpin` | <img src="/icons/extended/Unpin.svg" width="24" height="24" /> |
| `Update` | <img src="/icons/extended/Update.svg" width="24" height="24" /> |
| `UploadCloud` | <img src="/icons/extended/UploadCloud.svg" width="24" height="24" /> |
| `VerticalSplit` | <img src="/icons/extended/VerticalSplit.svg" width="24" height="24" /> |
| `VolumeOff` | <img src="/icons/extended/VolumeOff.svg" width="24" height="24" /> |
| `VolumeUp` | <img src="/icons/extended/VolumeUp.svg" width="24" height="24" /> |
| `Weeks` | <img src="/icons/extended/Weeks.svg" width="24" height="24" /> |
| `WorldClock` | <img src="/icons/extended/WorldClock.svg" width="24" height="24" /> |
| `Years` | <img src="/icons/extended/Years.svg" width="24" height="24" /> |
| `ZoomOut` | <img src="/icons/extended/ZoomOut.svg" width="24" height="24" /> |

