// Copyright 2026, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package canvas.ui

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.AddCircle
import io.github.suqi8.coui.kmp.icon.extended.AddFolder
import io.github.suqi8.coui.kmp.icon.extended.Alarm
import io.github.suqi8.coui.kmp.icon.extended.Album
import io.github.suqi8.coui.kmp.icon.extended.All
import io.github.suqi8.coui.kmp.icon.extended.Answer
import io.github.suqi8.coui.kmp.icon.extended.AppRecording
import io.github.suqi8.coui.kmp.icon.extended.Back
import io.github.suqi8.coui.kmp.icon.extended.Background
import io.github.suqi8.coui.kmp.icon.extended.Backup
import io.github.suqi8.coui.kmp.icon.extended.BankCards
import io.github.suqi8.coui.kmp.icon.extended.Blocklist
import io.github.suqi8.coui.kmp.icon.extended.CallRecording
import io.github.suqi8.coui.kmp.icon.extended.Carrier
import io.github.suqi8.coui.kmp.icon.extended.ChevronBackward
import io.github.suqi8.coui.kmp.icon.extended.ChevronForward
import io.github.suqi8.coui.kmp.icon.extended.Clear
import io.github.suqi8.coui.kmp.icon.extended.Close
import io.github.suqi8.coui.kmp.icon.extended.Close2
import io.github.suqi8.coui.kmp.icon.extended.CloudFill
import io.github.suqi8.coui.kmp.icon.extended.Community
import io.github.suqi8.coui.kmp.icon.extended.Contacts
import io.github.suqi8.coui.kmp.icon.extended.ContactsBook
import io.github.suqi8.coui.kmp.icon.extended.ContactsCircle
import io.github.suqi8.coui.kmp.icon.extended.ConvertFile
import io.github.suqi8.coui.kmp.icon.extended.Copy
import io.github.suqi8.coui.kmp.icon.extended.Create
import io.github.suqi8.coui.kmp.icon.extended.Cut
import io.github.suqi8.coui.kmp.icon.extended.Delete
import io.github.suqi8.coui.kmp.icon.extended.Download
import io.github.suqi8.coui.kmp.icon.extended.Edit
import io.github.suqi8.coui.kmp.icon.extended.Email
import io.github.suqi8.coui.kmp.icon.extended.ExpandLess
import io.github.suqi8.coui.kmp.icon.extended.ExpandMore
import io.github.suqi8.coui.kmp.icon.extended.Favorites
import io.github.suqi8.coui.kmp.icon.extended.FavoritesFill
import io.github.suqi8.coui.kmp.icon.extended.File
import io.github.suqi8.coui.kmp.icon.extended.FileDownloads
import io.github.suqi8.coui.kmp.icon.extended.Filter
import io.github.suqi8.coui.kmp.icon.extended.Folder
import io.github.suqi8.coui.kmp.icon.extended.FolderFill
import io.github.suqi8.coui.kmp.icon.extended.Forward
import io.github.suqi8.coui.kmp.icon.extended.GridView
import io.github.suqi8.coui.kmp.icon.extended.Help
import io.github.suqi8.coui.kmp.icon.extended.Hide
import io.github.suqi8.coui.kmp.icon.extended.Home
import io.github.suqi8.coui.kmp.icon.extended.HorizontalSplit
import io.github.suqi8.coui.kmp.icon.extended.Image
import io.github.suqi8.coui.kmp.icon.extended.Import
import io.github.suqi8.coui.kmp.icon.extended.Info
import io.github.suqi8.coui.kmp.icon.extended.Layers
import io.github.suqi8.coui.kmp.icon.extended.Link
import io.github.suqi8.coui.kmp.icon.extended.ListView
import io.github.suqi8.coui.kmp.icon.extended.Location
import io.github.suqi8.coui.kmp.icon.extended.Lock
import io.github.suqi8.coui.kmp.icon.extended.MapAlbum
import io.github.suqi8.coui.kmp.icon.extended.Merge
import io.github.suqi8.coui.kmp.icon.extended.Messages
import io.github.suqi8.coui.kmp.icon.extended.Mic
import io.github.suqi8.coui.kmp.icon.extended.MicSlash
import io.github.suqi8.coui.kmp.icon.extended.MindMap
import io.github.suqi8.coui.kmp.icon.extended.Months
import io.github.suqi8.coui.kmp.icon.extended.More
import io.github.suqi8.coui.kmp.icon.extended.MoreCircle
import io.github.suqi8.coui.kmp.icon.extended.MoveFile
import io.github.suqi8.coui.kmp.icon.extended.Music
import io.github.suqi8.coui.kmp.icon.extended.Notes
import io.github.suqi8.coui.kmp.icon.extended.NotesFill
import io.github.suqi8.coui.kmp.icon.extended.Ok
import io.github.suqi8.coui.kmp.icon.extended.Paste
import io.github.suqi8.coui.kmp.icon.extended.Pause
import io.github.suqi8.coui.kmp.icon.extended.Phone
import io.github.suqi8.coui.kmp.icon.extended.Photos
import io.github.suqi8.coui.kmp.icon.extended.Pin
import io.github.suqi8.coui.kmp.icon.extended.Play
import io.github.suqi8.coui.kmp.icon.extended.Playlist
import io.github.suqi8.coui.kmp.icon.extended.Promotions
import io.github.suqi8.coui.kmp.icon.extended.Recent
import io.github.suqi8.coui.kmp.icon.extended.Recording
import io.github.suqi8.coui.kmp.icon.extended.RecordingTape
import io.github.suqi8.coui.kmp.icon.extended.Redo
import io.github.suqi8.coui.kmp.icon.extended.Refresh
import io.github.suqi8.coui.kmp.icon.extended.Remove
import io.github.suqi8.coui.kmp.icon.extended.RemoveContact
import io.github.suqi8.coui.kmp.icon.extended.Rename
import io.github.suqi8.coui.kmp.icon.extended.Replace
import io.github.suqi8.coui.kmp.icon.extended.Reply
import io.github.suqi8.coui.kmp.icon.extended.ReplyAll
import io.github.suqi8.coui.kmp.icon.extended.Report
import io.github.suqi8.coui.kmp.icon.extended.Reset
import io.github.suqi8.coui.kmp.icon.extended.RotateLeft
import io.github.suqi8.coui.kmp.icon.extended.Scan
import io.github.suqi8.coui.kmp.icon.extended.ScreenCapture
import io.github.suqi8.coui.kmp.icon.extended.ScreenMirroring
import io.github.suqi8.coui.kmp.icon.extended.Search
import io.github.suqi8.coui.kmp.icon.extended.SearchDevice
import io.github.suqi8.coui.kmp.icon.extended.SelectAll
import io.github.suqi8.coui.kmp.icon.extended.Send
import io.github.suqi8.coui.kmp.icon.extended.Settings
import io.github.suqi8.coui.kmp.icon.extended.Share
import io.github.suqi8.coui.kmp.icon.extended.Show
import io.github.suqi8.coui.kmp.icon.extended.Sidebar
import io.github.suqi8.coui.kmp.icon.extended.Sort
import io.github.suqi8.coui.kmp.icon.extended.Stopwatch
import io.github.suqi8.coui.kmp.icon.extended.Store
import io.github.suqi8.coui.kmp.icon.extended.Tasks
import io.github.suqi8.coui.kmp.icon.extended.Theme
import io.github.suqi8.coui.kmp.icon.extended.Timer
import io.github.suqi8.coui.kmp.icon.extended.TopDownloads
import io.github.suqi8.coui.kmp.icon.extended.Translate
import io.github.suqi8.coui.kmp.icon.extended.Trim
import io.github.suqi8.coui.kmp.icon.extended.Tune
import io.github.suqi8.coui.kmp.icon.extended.Undo
import io.github.suqi8.coui.kmp.icon.extended.Unlock
import io.github.suqi8.coui.kmp.icon.extended.Unpin
import io.github.suqi8.coui.kmp.icon.extended.Update
import io.github.suqi8.coui.kmp.icon.extended.UploadCloud
import io.github.suqi8.coui.kmp.icon.extended.VerticalSplit
import io.github.suqi8.coui.kmp.icon.extended.VolumeOff
import io.github.suqi8.coui.kmp.icon.extended.VolumeUp
import io.github.suqi8.coui.kmp.icon.extended.Weeks
import io.github.suqi8.coui.kmp.icon.extended.WorldClock
import io.github.suqi8.coui.kmp.icon.extended.Years
import io.github.suqi8.coui.kmp.icon.extended.ZoomOut

/** The vector for an icon name of [canvas.model.IconNames], or null for an unknown name. */
fun iconFor(name: String?): ImageVector? = when (name) {
    "Add" -> COUIIcons.Add
    "AddCircle" -> COUIIcons.AddCircle
    "AddFolder" -> COUIIcons.AddFolder
    "Alarm" -> COUIIcons.Alarm
    "Album" -> COUIIcons.Album
    "All" -> COUIIcons.All
    "Answer" -> COUIIcons.Answer
    "AppRecording" -> COUIIcons.AppRecording
    "Back" -> COUIIcons.Back
    "Background" -> COUIIcons.Background
    "Backup" -> COUIIcons.Backup
    "BankCards" -> COUIIcons.BankCards
    "Blocklist" -> COUIIcons.Blocklist
    "CallRecording" -> COUIIcons.CallRecording
    "Carrier" -> COUIIcons.Carrier
    "ChevronBackward" -> COUIIcons.ChevronBackward
    "ChevronForward" -> COUIIcons.ChevronForward
    "Clear" -> COUIIcons.Clear
    "Close" -> COUIIcons.Close
    "Close2" -> COUIIcons.Close2
    "CloudFill" -> COUIIcons.CloudFill
    "Community" -> COUIIcons.Community
    "Contacts" -> COUIIcons.Contacts
    "ContactsBook" -> COUIIcons.ContactsBook
    "ContactsCircle" -> COUIIcons.ContactsCircle
    "ConvertFile" -> COUIIcons.ConvertFile
    "Copy" -> COUIIcons.Copy
    "Create" -> COUIIcons.Create
    "Cut" -> COUIIcons.Cut
    "Delete" -> COUIIcons.Delete
    "Download" -> COUIIcons.Download
    "Edit" -> COUIIcons.Edit
    "Email" -> COUIIcons.Email
    "ExpandLess" -> COUIIcons.ExpandLess
    "ExpandMore" -> COUIIcons.ExpandMore
    "Favorites" -> COUIIcons.Favorites
    "FavoritesFill" -> COUIIcons.FavoritesFill
    "File" -> COUIIcons.File
    "FileDownloads" -> COUIIcons.FileDownloads
    "Filter" -> COUIIcons.Filter
    "Folder" -> COUIIcons.Folder
    "FolderFill" -> COUIIcons.FolderFill
    "Forward" -> COUIIcons.Forward
    "GridView" -> COUIIcons.GridView
    "Help" -> COUIIcons.Help
    "Hide" -> COUIIcons.Hide
    "Home" -> COUIIcons.Home
    "HorizontalSplit" -> COUIIcons.HorizontalSplit
    "Image" -> COUIIcons.Image
    "Import" -> COUIIcons.Import
    "Info" -> COUIIcons.Info
    "Layers" -> COUIIcons.Layers
    "Link" -> COUIIcons.Link
    "ListView" -> COUIIcons.ListView
    "Location" -> COUIIcons.Location
    "Lock" -> COUIIcons.Lock
    "MapAlbum" -> COUIIcons.MapAlbum
    "Merge" -> COUIIcons.Merge
    "Messages" -> COUIIcons.Messages
    "Mic" -> COUIIcons.Mic
    "MicSlash" -> COUIIcons.MicSlash
    "MindMap" -> COUIIcons.MindMap
    "Months" -> COUIIcons.Months
    "More" -> COUIIcons.More
    "MoreCircle" -> COUIIcons.MoreCircle
    "MoveFile" -> COUIIcons.MoveFile
    "Music" -> COUIIcons.Music
    "Notes" -> COUIIcons.Notes
    "NotesFill" -> COUIIcons.NotesFill
    "Ok" -> COUIIcons.Ok
    "Paste" -> COUIIcons.Paste
    "Pause" -> COUIIcons.Pause
    "Phone" -> COUIIcons.Phone
    "Photos" -> COUIIcons.Photos
    "Pin" -> COUIIcons.Pin
    "Play" -> COUIIcons.Play
    "Playlist" -> COUIIcons.Playlist
    "Promotions" -> COUIIcons.Promotions
    "Recent" -> COUIIcons.Recent
    "Recording" -> COUIIcons.Recording
    "RecordingTape" -> COUIIcons.RecordingTape
    "Redo" -> COUIIcons.Redo
    "Refresh" -> COUIIcons.Refresh
    "Remove" -> COUIIcons.Remove
    "RemoveContact" -> COUIIcons.RemoveContact
    "Rename" -> COUIIcons.Rename
    "Replace" -> COUIIcons.Replace
    "Reply" -> COUIIcons.Reply
    "ReplyAll" -> COUIIcons.ReplyAll
    "Report" -> COUIIcons.Report
    "Reset" -> COUIIcons.Reset
    "RotateLeft" -> COUIIcons.RotateLeft
    "Scan" -> COUIIcons.Scan
    "ScreenCapture" -> COUIIcons.ScreenCapture
    "ScreenMirroring" -> COUIIcons.ScreenMirroring
    "Search" -> COUIIcons.Search
    "SearchDevice" -> COUIIcons.SearchDevice
    "SelectAll" -> COUIIcons.SelectAll
    "Send" -> COUIIcons.Send
    "Settings" -> COUIIcons.Settings
    "Share" -> COUIIcons.Share
    "Show" -> COUIIcons.Show
    "Sidebar" -> COUIIcons.Sidebar
    "Sort" -> COUIIcons.Sort
    "Stopwatch" -> COUIIcons.Stopwatch
    "Store" -> COUIIcons.Store
    "Tasks" -> COUIIcons.Tasks
    "Theme" -> COUIIcons.Theme
    "Timer" -> COUIIcons.Timer
    "TopDownloads" -> COUIIcons.TopDownloads
    "Translate" -> COUIIcons.Translate
    "Trim" -> COUIIcons.Trim
    "Tune" -> COUIIcons.Tune
    "Undo" -> COUIIcons.Undo
    "Unlock" -> COUIIcons.Unlock
    "Unpin" -> COUIIcons.Unpin
    "Update" -> COUIIcons.Update
    "UploadCloud" -> COUIIcons.UploadCloud
    "VerticalSplit" -> COUIIcons.VerticalSplit
    "VolumeOff" -> COUIIcons.VolumeOff
    "VolumeUp" -> COUIIcons.VolumeUp
    "Weeks" -> COUIIcons.Weeks
    "WorldClock" -> COUIIcons.WorldClock
    "Years" -> COUIIcons.Years
    "ZoomOut" -> COUIIcons.ZoomOut
    else -> null
}
