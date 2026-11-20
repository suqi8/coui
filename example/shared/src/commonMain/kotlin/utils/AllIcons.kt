// Copyright 2025, compose-coui-ui contributors
// SPDX-License-Identifier: Apache-2.0
package utils

import androidx.compose.ui.graphics.vector.ImageVector
import io.github.suqi8.coui.kmp.icon.COUIIcons
import io.github.suqi8.coui.kmp.icon.extended.Add
import io.github.suqi8.coui.kmp.icon.extended.AddCircle
import io.github.suqi8.coui.kmp.icon.extended.AddFolder
import io.github.suqi8.coui.kmp.icon.extended.Alarm
import io.github.suqi8.coui.kmp.icon.extended.Album
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
import io.github.suqi8.coui.kmp.icon.extended.Th1
import io.github.suqi8.coui.kmp.icon.extended.Th10
import io.github.suqi8.coui.kmp.icon.extended.Th11
import io.github.suqi8.coui.kmp.icon.extended.Th12
import io.github.suqi8.coui.kmp.icon.extended.Th13
import io.github.suqi8.coui.kmp.icon.extended.Th14
import io.github.suqi8.coui.kmp.icon.extended.Th15
import io.github.suqi8.coui.kmp.icon.extended.Th16
import io.github.suqi8.coui.kmp.icon.extended.Th17
import io.github.suqi8.coui.kmp.icon.extended.Th18
import io.github.suqi8.coui.kmp.icon.extended.Th19
import io.github.suqi8.coui.kmp.icon.extended.Th2
import io.github.suqi8.coui.kmp.icon.extended.Th20
import io.github.suqi8.coui.kmp.icon.extended.Th21
import io.github.suqi8.coui.kmp.icon.extended.Th22
import io.github.suqi8.coui.kmp.icon.extended.Th23
import io.github.suqi8.coui.kmp.icon.extended.Th24
import io.github.suqi8.coui.kmp.icon.extended.Th25
import io.github.suqi8.coui.kmp.icon.extended.Th26
import io.github.suqi8.coui.kmp.icon.extended.Th27
import io.github.suqi8.coui.kmp.icon.extended.Th28
import io.github.suqi8.coui.kmp.icon.extended.Th29
import io.github.suqi8.coui.kmp.icon.extended.Th3
import io.github.suqi8.coui.kmp.icon.extended.Th30
import io.github.suqi8.coui.kmp.icon.extended.Th31
import io.github.suqi8.coui.kmp.icon.extended.Th4
import io.github.suqi8.coui.kmp.icon.extended.Th5
import io.github.suqi8.coui.kmp.icon.extended.Th6
import io.github.suqi8.coui.kmp.icon.extended.Th7
import io.github.suqi8.coui.kmp.icon.extended.Th8
import io.github.suqi8.coui.kmp.icon.extended.Th9
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
import io.github.suqi8.coui.kmp.icon.extended.All as AllIcon

/** Every extended icon once, in alphabetical order. */
val COUIIcons.All: List<ImageVector>
    get() = listOf(
        COUIIcons.Add,
        COUIIcons.AddCircle,
        COUIIcons.AddFolder,
        COUIIcons.Alarm,
        COUIIcons.Album,
        COUIIcons.AllIcon,
        COUIIcons.Answer,
        COUIIcons.AppRecording,
        COUIIcons.Back,
        COUIIcons.Background,
        COUIIcons.Backup,
        COUIIcons.BankCards,
        COUIIcons.Blocklist,
        COUIIcons.CallRecording,
        COUIIcons.Carrier,
        COUIIcons.ChevronBackward,
        COUIIcons.ChevronForward,
        COUIIcons.Clear,
        COUIIcons.Close,
        COUIIcons.Close2,
        COUIIcons.CloudFill,
        COUIIcons.Community,
        COUIIcons.Contacts,
        COUIIcons.ContactsBook,
        COUIIcons.ContactsCircle,
        COUIIcons.ConvertFile,
        COUIIcons.Copy,
        COUIIcons.Create,
        COUIIcons.Cut,
        COUIIcons.Delete,
        COUIIcons.Download,
        COUIIcons.Edit,
        COUIIcons.Email,
        COUIIcons.ExpandLess,
        COUIIcons.ExpandMore,
        COUIIcons.Favorites,
        COUIIcons.FavoritesFill,
        COUIIcons.File,
        COUIIcons.FileDownloads,
        COUIIcons.Filter,
        COUIIcons.Folder,
        COUIIcons.FolderFill,
        COUIIcons.Forward,
        COUIIcons.GridView,
        COUIIcons.Help,
        COUIIcons.Hide,
        COUIIcons.Home,
        COUIIcons.HorizontalSplit,
        COUIIcons.Image,
        COUIIcons.Import,
        COUIIcons.Info,
        COUIIcons.Layers,
        COUIIcons.Link,
        COUIIcons.ListView,
        COUIIcons.Location,
        COUIIcons.Lock,
        COUIIcons.MapAlbum,
        COUIIcons.Merge,
        COUIIcons.Messages,
        COUIIcons.Mic,
        COUIIcons.MicSlash,
        COUIIcons.MindMap,
        COUIIcons.Months,
        COUIIcons.More,
        COUIIcons.MoreCircle,
        COUIIcons.MoveFile,
        COUIIcons.Music,
        COUIIcons.Notes,
        COUIIcons.NotesFill,
        COUIIcons.Ok,
        COUIIcons.Paste,
        COUIIcons.Pause,
        COUIIcons.Phone,
        COUIIcons.Photos,
        COUIIcons.Pin,
        COUIIcons.Play,
        COUIIcons.Playlist,
        COUIIcons.Promotions,
        COUIIcons.Recent,
        COUIIcons.Recording,
        COUIIcons.RecordingTape,
        COUIIcons.Redo,
        COUIIcons.Refresh,
        COUIIcons.Remove,
        COUIIcons.RemoveContact,
        COUIIcons.Rename,
        COUIIcons.Replace,
        COUIIcons.Reply,
        COUIIcons.ReplyAll,
        COUIIcons.Report,
        COUIIcons.Reset,
        COUIIcons.RotateLeft,
        COUIIcons.Scan,
        COUIIcons.ScreenCapture,
        COUIIcons.ScreenMirroring,
        COUIIcons.Search,
        COUIIcons.SearchDevice,
        COUIIcons.SelectAll,
        COUIIcons.Send,
        COUIIcons.Settings,
        COUIIcons.Share,
        COUIIcons.Show,
        COUIIcons.Sidebar,
        COUIIcons.Sort,
        COUIIcons.Stopwatch,
        COUIIcons.Store,
        COUIIcons.Tasks,
        COUIIcons.Th1,
        COUIIcons.Th10,
        COUIIcons.Th11,
        COUIIcons.Th12,
        COUIIcons.Th13,
        COUIIcons.Th14,
        COUIIcons.Th15,
        COUIIcons.Th16,
        COUIIcons.Th17,
        COUIIcons.Th18,
        COUIIcons.Th19,
        COUIIcons.Th2,
        COUIIcons.Th20,
        COUIIcons.Th21,
        COUIIcons.Th22,
        COUIIcons.Th23,
        COUIIcons.Th24,
        COUIIcons.Th25,
        COUIIcons.Th26,
        COUIIcons.Th27,
        COUIIcons.Th28,
        COUIIcons.Th29,
        COUIIcons.Th3,
        COUIIcons.Th30,
        COUIIcons.Th31,
        COUIIcons.Th4,
        COUIIcons.Th5,
        COUIIcons.Th6,
        COUIIcons.Th7,
        COUIIcons.Th8,
        COUIIcons.Th9,
        COUIIcons.Theme,
        COUIIcons.Timer,
        COUIIcons.TopDownloads,
        COUIIcons.Translate,
        COUIIcons.Trim,
        COUIIcons.Tune,
        COUIIcons.Undo,
        COUIIcons.Unlock,
        COUIIcons.Unpin,
        COUIIcons.Update,
        COUIIcons.UploadCloud,
        COUIIcons.VerticalSplit,
        COUIIcons.VolumeOff,
        COUIIcons.VolumeUp,
        COUIIcons.Weeks,
        COUIIcons.WorldClock,
        COUIIcons.Years,
        COUIIcons.ZoomOut,
    )
