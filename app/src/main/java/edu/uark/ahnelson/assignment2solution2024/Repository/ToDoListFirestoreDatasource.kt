package edu.uark.ahnelson.assignment2solution2024.Repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ToDoListFirestoreDatasource(val repository: ToDoListRepository) {
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    lateinit var collectionReference:CollectionReference

    fun start () {
        auth = Firebase.auth

        val currUser = auth.currentUser

        if (currUser != null) {
            val userCollection = db.collection("users")
            val todoItemsCollection = userCollection.document(currUser.uid).collection("todoItems")

        }

    }

    fun setCollection(collection:String){
        auth = Firebase.auth
        Log.d("FirestoreDatasource", "USERID: ${auth.currentUser?.uid}")
        collectionReference = db.collection("users").document(auth.currentUser?.uid.toString()).collection(collection)
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

    fun insert(toDoItem: ToDoItem){
        collectionReference.document(toDoItem.id.toString()).set(toDoItem)
            .addOnSuccessListener { Log.d("FirestoreDatasource", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("FirestoreDatasource", "Error writing document", e) }
    }

    fun update(toDoItem: ToDoItem){
        collectionReference.document(toDoItem.id.toString()).set(toDoItem)
            .addOnSuccessListener { Log.d("FirestoreDatasource", "DocumentSnapshot successfully updated!") }
            .addOnFailureListener { e -> Log.w("FirestoreDatasource", "Error updating document", e) }
    }



    fun delete(id: Int){
        collectionReference.document(id.toString()).delete()
            .addOnSuccessListener { Log.d("FirestoreDatasource", "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w("FirestoreDatasource", "Error deleting document", e) }
    }
}