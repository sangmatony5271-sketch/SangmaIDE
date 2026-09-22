package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_files")
data class ProjectFile(
    @PrimaryKey val path: String,
    val name: String,
    val content: String,
    val language: String, // "kotlin", "xml", "json", "gradle", "markdown"
    val isFolder: Boolean = false,
    val parentPath: String = "",
    val lastModified: Long = System.currentTimeMillis(),
    val isUnsaved: Boolean = false
)

@Entity(tableName = "git_commits")
data class GitCommit(
    @PrimaryKey val hash: String,
    val message: String,
    val author: String,
    val timestamp: Long = System.currentTimeMillis(),
    val branch: String = "main",
    val filesChangedCount: Int = 1
)

@Entity(tableName = "backups")
data class BackupSnapshot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val label: String,
    val encryptedPayload: String,
    val fileCount: Int
)

enum class IdeTheme(val displayName: String, val isDark: Boolean) {
    MONOKAI_PRO("Monokai Pro", true),
    DRACULA("Dracula Dark", true),
    ONE_DARK("One Dark Pro", true),
    SOLARIZED_DARK("Solarized Dark", true),
    NORD("Nord Minimal", true),
    LIGHT_MODERN("Light Modern", false)
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    BENGALI("bn", "বাংলা (Bengali)")
}

data class Breakpoint(
    val filePath: String,
    val lineNumber: Int,
    val isEnabled: Boolean = true
)

enum class LogLevel { INFO, WARNING, ERROR, SUCCESS }

data class ConsoleLog(
    val id: Long = System.currentTimeMillis(),
    val tag: String,
    val message: String,
    val level: LogLevel = LogLevel.INFO,
    val timestamp: Long = System.currentTimeMillis()
)

enum class SuggestionType { KEYWORD, FUNCTION, CLASS, AI_NOTRACK }

data class AutoCompleteSuggestion(
    val label: String,
    val insertText: String,
    val detail: String,
    val type: SuggestionType
)

data class Collaborator(
    val id: String,
    val name: String,
    val colorHex: String,
    val activeFile: String,
    val isOnline: Boolean = true
)

enum class NotificationType { INFO, BUILD, GIT, SECURITY, COLLAB }

data class PushNotificationItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val type: NotificationType = NotificationType.INFO
)
