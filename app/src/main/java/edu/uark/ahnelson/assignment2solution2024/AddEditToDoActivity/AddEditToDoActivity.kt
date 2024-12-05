package edu.uark.ahnelson.assignment2solution2024.AddEditToDoActivity

import android.icu.text.DateFormat.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.viewModels
import edu.uark.ahnelson.assignment2solution2024.R
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoItem
import edu.uark.ahnelson.assignment2solution2024.ToDoListApplication
import edu.uark.ahnelson.assignment2solution2024.Util.DatePickerFragment
import edu.uark.ahnelson.assignment2solution2024.Util.TimePickerFragment


import java.util.*

class AddEditToDoActivity : AppCompatActivity() {

    private lateinit var toDoItem: ToDoItem
    private lateinit var etTitle:EditText
    private lateinit var etContent:EditText
    private lateinit var etDate: Button
    private lateinit var checkBox: CheckBox

    private val addEditToDoViewModel: AddEditToDoViewModel by viewModels {
        AddEditToDoViewModel.AddEditToDoViewModelFactory((application as ToDoListApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_to_do)
        etTitle = findViewById(R.id.etToDoTitle)
        etContent = findViewById(R.id.etEditContent)
        etDate = findViewById(R.id.editTextDate)
        checkBox = findViewById(R.id.cbMarkComplete)
        val id = intent.getIntExtra(EXTRA_ID,-1)
        if (id == -1){
            populateNewToDoItem()
        }else{
            populateExistingToDoItem(id)
        }
    }

    fun populateNewToDoItem(){
        toDoItem = ToDoItem(null,"","",0,0)
        updateViewUI()
    }

    fun populateExistingToDoItem(id:Int){
        addEditToDoViewModel.start(id)
        addEditToDoViewModel.toDoItem.observe(this){
            if(it != null) {
                toDoItem = it
                updateViewUI()
            }
        }
    }

    fun updateViewUI(){

        etTitle.setText(toDoItem.title)
        etContent.setText(toDoItem.content)
        if(toDoItem.dueDate != null) {
            val cal: Calendar = Calendar.getInstance()
            cal.timeInMillis = toDoItem.dueDate!!
            etDate.setText(java.text.DateFormat.getDateTimeInstance(DEFAULT,SHORT).format(cal.timeInMillis))
        }else{
            etDate.setText("")
        }
        checkBox.isChecked = toDoItem.completed != 0
    }

    fun deleteClicked(view:View){
        Log.d("AddEditDoDoActivity","Delete Clicked")
        if(toDoItem.id==0){
            setResult(RESULT_CANCELED)
            finish()
        }else{
            addEditToDoViewModel.deleteToDoItem()
            setResult(RESULT_OK)
            finish()
        }
    }
    fun saveClicked(view:View){
        Log.d("AddEditToDoActivity","Save Clicked")
        if(toDoItem.id==null){
            getFieldsIntoItem()
            addEditToDoViewModel.insert(toDoItem)
            setResult(RESULT_OK)
            finish()
        }else{
            getFieldsIntoItem()
            addEditToDoViewModel.updateItem(toDoItem)
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun getFieldsIntoItem(){
        toDoItem.title = etTitle.text.toString()
        toDoItem.content = etContent.text.toString()
        toDoItem.dueDate = java.text.DateFormat.getDateTimeInstance(DEFAULT,SHORT).parse(etDate.text.toString())?.time
        if(checkBox.isChecked){
            toDoItem.completed = 1
        }else{
            toDoItem.completed = 0
        }
    }

    fun dateSet(calendar: Calendar){
        TimePickerFragment(calendar,this::timeSet).show(supportFragmentManager, "timePicker")
    }
    fun timeSet(calendar: Calendar){
        etDate.setText(java.text.DateFormat.getDateTimeInstance(DEFAULT,SHORT).format(calendar.timeInMillis))
    }
    fun dateClicked(view:View){
        DatePickerFragment(this::dateSet).show(supportFragmentManager, "datePicker")
    }

    companion object{
        val EXTRA_ID = "com.example.assignment2solution.addedittodoactivity.id"
    }
}