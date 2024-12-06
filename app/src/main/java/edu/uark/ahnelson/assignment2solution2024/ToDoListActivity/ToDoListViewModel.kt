package edu.uark.ahnelson.assignment2solution2024.ToDoListActivity

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoItem
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoListRepository
import kotlinx.coroutines.launch


class ToDoListViewModel(private val repository: ToDoListRepository): ViewModel() {

    init {
        repository.setCollection("todoItems")
    }
    fun updateChecked(itemId: Int, checked: Boolean) {
        viewModelScope.launch {
            repository.updateCompleted(itemId, checked)
        }
    }

    fun setCollection(collection:String){
        repository.setCollection(collection)
    }

    fun start() {
        repository.start()
    }

    val allToDoItems: LiveData<Map<Int, ToDoItem>> = repository.allToDoItems.asLiveData()

    class ToDoListViewModelFactory(private val repository: ToDoListRepository) : ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if(modelClass.isAssignableFrom(ToDoListViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return ToDoListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }


}