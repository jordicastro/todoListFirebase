package edu.uark.ahnelson.assignment2solution2024

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoListRepository
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoListRoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ToDoListApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { ToDoListRoomDatabase.getDatabase(this,applicationScope)}
    val repository by lazy{ ToDoListRepository(database.toDoListDao())
    }
}