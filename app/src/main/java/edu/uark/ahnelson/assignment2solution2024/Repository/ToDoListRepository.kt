package edu.uark.ahnelson.assignment2solution2024.Repository

import android.util.Log
import androidx.annotation.WorkerThread
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoItem
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoListDao
import kotlinx.coroutines.flow.Flow

class ToDoListRepository(private val toDoListDao: ToDoListDao) {
    val allToDoItems: Flow<Map<Int, ToDoItem>> = toDoListDao.getToDoItems()
    var firestoreDatasource:ToDoListFirestoreDatasource = ToDoListFirestoreDatasource(this)

    fun setCollection(collection:String){
        firestoreDatasource.setCollection(collection)
    }
    @Suppress("RedudndantSuspendModifier")
    @WorkerThread
    suspend fun insert(toDoItem: ToDoItem){
        val id: Long = toDoListDao.insert(toDoItem) // insert into local database
        toDoItem.id = id.toInt()
        firestoreDatasource.insert(toDoItem) // insert into firestore
    }

    @Suppress("RedudndantSuspendModifier")
    @WorkerThread
    suspend fun getToDoItem(toDoId: Int): ToDoItem {
        return toDoListDao.getItem(toDoId)
    }

    @Suppress("RedudndantSuspendModifier")
    @WorkerThread
    suspend fun updateCompleted(toDoId: Int, completed:Boolean) {
        if (completed)
            toDoListDao.updateCompleted(toDoId,1)
        else
            toDoListDao.updateCompleted(toDoId,0)
    }

    @Suppress("RedudndantSuspendModifier")
    @WorkerThread
    suspend fun deleteToDoItem(id: Int) {
        toDoListDao.deleteItem(id)
        Log.d("ToDoListRepository", "Deleted item from local database with id: $id")
        firestoreDatasource.delete(id)
    }

    @Suppress("RedudndantSuspendModifier")
    @WorkerThread
    suspend fun updateItem(toDoItem: ToDoItem) {
        toDoListDao.updateItem(toDoItem)
        firestoreDatasource.update(toDoItem)
    }

}