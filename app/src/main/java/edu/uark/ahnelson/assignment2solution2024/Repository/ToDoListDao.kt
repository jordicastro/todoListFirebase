package edu.uark.ahnelson.assignment2solution2024.Repository

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ToDoListDao {

    @MapInfo(keyColumn="id")
    @Query("SELECT * FROM todoitems_table order by id ASC")
    fun getToDoItems(): Flow<Map<Int, ToDoItem>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(toDoItem: ToDoItem): Long

    @Query("DELETE FROM todoitems_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM todoitems_table WHERE id = :id")
    suspend fun getItem(id:Int): ToDoItem

    @Query("UPDATE todoitems_table SET completed=:completed WHERE id=:toDoId")
    suspend fun updateCompleted(toDoId: Int, completed: Int)

    @Query("DELETE FROM todoitems_table WHERE id=:id")
    suspend fun deleteItem(id: Int)

    @Update
    suspend fun updateItem(toDoItem: ToDoItem)

}