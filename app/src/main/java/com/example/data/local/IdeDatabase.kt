package com.example.data.local

import android.content.Context
import androidx.room.*
import com.example.data.model.BackupSnapshot
import com.example.data.model.GitCommit
import com.example.data.model.ProjectFile
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM project_files ORDER BY isFolder DESC, name ASC")
    fun getAllFiles(): Flow<List<ProjectFile>>

    @Query("SELECT * FROM project_files WHERE path = :path LIMIT 1")
    suspend fun getFileByPath(path: String): ProjectFile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateFile(file: ProjectFile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFiles(files: List<ProjectFile>)

    @Query("DELETE FROM project_files WHERE path = :path")
    suspend fun deleteFile(path: String)
}

@Dao
interface GitDao {
    @Query("SELECT * FROM git_commits ORDER BY timestamp DESC")
    fun getAllCommits(): Flow<List<GitCommit>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommit(commit: GitCommit)
}

@Dao
interface BackupDao {
    @Query("SELECT * FROM backups ORDER BY timestamp DESC")
    fun getAllBackups(): Flow<List<BackupSnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBackup(backup: BackupSnapshot)

    @Query("DELETE FROM backups WHERE id = :id")
    suspend fun deleteBackup(id: Int)
}

@Database(
    entities = [ProjectFile::class, GitCommit::class, BackupSnapshot::class],
    version = 1,
    exportSchema = false
)
abstract class IdeDatabase : RoomDatabase() {
    abstract fun fileDao(): FileDao
    abstract fun gitDao(): GitDao
    abstract fun backupDao(): BackupDao

    companion object {
        @Volatile
        private var INSTANCE: IdeDatabase? = null

        fun getInstance(context: Context): IdeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IdeDatabase::class.java,
                    "notrack_ide.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
