package edu.uark.ahnelson.assignment2solution2024.AddEditToDoActivity

import androidx.lifecycle.*
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoItem
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoListRepository
import kotlinx.coroutines.launch

class AddEditToDoViewModel(private val repository: ToDoListRepository): ViewModel() {

    val _toDoItem = MutableLiveData<ToDoItem>().apply { value=null }
    val toDoItem:LiveData<ToDoItem>
        get() = _toDoItem

    fun start(itemId:Int){
        viewModelScope.launch {
            repository.allToDoItems.collect{
                _toDoItem.value = it[itemId]
            }
        }
    }

    fun insert(toDoItem: ToDoItem) {
        viewModelScope.launch {
            repository.insert(toDoItem)
        }
    }

    fun deleteToDoItem() {
        viewModelScope.launch {
            toDoItem.value?.id?.let { repository.deleteToDoItem(it) }
        }
    }

    fun updateItem(toDoItem: ToDoItem) {
        viewModelScope.launch {
            repository.updateItem(toDoItem)
        }
    }

    class AddEditToDoViewModelFactory(private val repository: ToDoListRepository) : ViewModelProvider.Factory{
        override fun <T: ViewModel> create(modelClass: Class<T>): T{
            if(modelClass.isAssignableFrom(AddEditToDoViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return AddEditToDoViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}