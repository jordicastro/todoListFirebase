package edu.uark.ahnelson.assignment2solution2024.Repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the ToDoItem class
@Database(entities = arrayOf(ToDoItem::class), version = 1, exportSchema = false)
public abstract class ToDoListRoomDatabase : RoomDatabase() {

    abstract fun toDoListDao(): ToDoListDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ToDoListRoomDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ToDoListRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoListRoomDatabase::class.java,
                    "todolist_database"
                )
                    .addCallback(ToDoListDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

    private class ToDoListDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.toDoListDao())
                }
            }
        }

        suspend fun populateDatabase(toDoListDao: ToDoListDao) {
            // Delete all content here.
            toDoListDao.deleteAll()

            // Add sample words.
            val toDoItem = ToDoItem(null,"Assignment 2", "Complete Assignment 2", 0, 0)
            toDoListDao.insert(toDoItem)
        }
    }

}