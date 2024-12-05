package edu.uark.ahnelson.assignment2solution2024.Repository

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ToDoListFirestoreDatasource(val repository: ToDoListRepository) {
    val db = Firebase.firestore
    lateinit var collectionReference:CollectionReference

    fun setCollection(collection:String){
        collectionReference = db.collection(collection)
        collectionReference.addSnapshotListener{ snapshot, e ->
            if (e != null) {
                Log.w("FirestoreDatasource", "Listen failed.", e)
                return@addSnapshotListener
            }else{
                if (snapshot != null && !snapshot.isEmpty()) {
                    Log.d(
                        "FirestoreDatasource",
                        "Current number of documents: ${snapshot.documents.size}"
                    )
                }


            }
        }
    }

    suspend fun insert(toDoItem: ToDoItem){
        collectionReference.document(toDoItem.id.toString()).set(toDoItem)
            .addOnSuccessListener { Log.d("FirestoreDatasource", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("FirestoreDatasource", "Error writing document", e) }
    }

    suspend fun update(toDoItem: ToDoItem){
        collectionReference.document(toDoItem.id.toString()).set(toDoItem)
            .addOnSuccessListener { Log.d("FirestoreDatasource", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("FirestoreDatasource", "Error updating document", e) }
    }



    suspend fun delete(id: Int){
        collectionReference.document(id.toString()).delete()
            .addOnSuccessListener { Log.d("FirestoreDatasource", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("FirestoreDatasource", "Error deleting document", e) }
    }
}