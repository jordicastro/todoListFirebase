package edu.uark.ahnelson.assignment2solution2024.ToDoListActivity

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import edu.uark.ahnelson.assignment2solution2024.AddEditToDoActivity.AddEditToDoActivity
import edu.uark.ahnelson.assignment2solution2024.AlarmReceiver
import edu.uark.ahnelson.assignment2solution2024.Repository.ToDoItem
import edu.uark.ahnelson.assignment2solution2024.ToDoListApplication
import edu.uark.ahnelson.assignment2solution2024.Util.NotificationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import edu.uark.ahnelson.assignment2solution2024.Authentication.LoginActivity
import edu.uark.ahnelson.assignment2solution2024.R
import java.util.*

class ToDoListActivity : AppCompatActivity() {

    val startAddEditToDoActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result: ActivityResult ->
        if(result.resultCode== Activity.RESULT_OK){
            Log.d("MainActivity","Completed")
        }
    }

    private val toDoListViewModel: ToDoListViewModel by viewModels {
        ToDoListViewModel.ToDoListViewModelFactory((application as ToDoListApplication).repository)
    }

    val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                NotificationUtils().createNotificationChannel(this)
            } else {
                Toast.makeText(this,
                    "Unable to schedule notification",
                    Toast.LENGTH_SHORT)
                    .show()
            }
        }

    private fun checkNotificationPrivilege(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationUtils().createNotificationChannel(this)
            return true
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return false
            }
            return true
        }
    }


    fun recyclerAdapterItemClicked(itemId:Int){
        startAddEditToDoActivity.launch(Intent(this, AddEditToDoActivity::class.java).putExtra(
            AddEditToDoActivity.EXTRA_ID,itemId))
    }
    fun recyclerAdapterItemCheckboxClicked(itemId:Int,isChecked:Boolean){
        toDoListViewModel.updateChecked(itemId,isChecked)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        toDoListViewModel.clearToDoItems()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)
        checkNotificationPrivilege()
        NotificationUtils().createNotificationChannel(applicationContext)

        val userId = intent.getStringExtra("USER_ID")
        Log.d("ToDoListActivity", "ToDoListActivity: User ID: $userId")
        // set the collection to the user's todoItems
        toDoListViewModel.setCollection("todoItems")

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            toDoListViewModel.clearToDoItems()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = ToDoListAdapter(this::recyclerAdapterItemClicked,this::recyclerAdapterItemCheckboxClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Add an observer on the LiveData returned by allToDoItems.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        toDoListViewModel.allToDoItems.observe(this) { todoitems ->
            // Update the cached copy of the words in the adapter.
            todoitems.let {
                scheduleNotifications(it)
                adapter.submitList(it.values.toList())
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            startAddEditToDoActivity.launch(Intent(this, AddEditToDoActivity::class.java))
        }
    }

    private fun scheduleNotifications(toDoItems: Map<Int, ToDoItem>?) {
        val alarmManager = this.applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        if (toDoItems != null) {
            for (item in toDoItems.values){
                val dueDate = item.dueDate
                if((dueDate != null) && (dueDate > Calendar.getInstance().timeInMillis) && item.id != null){
                    val alarmIntent = Intent(this.applicationContext, AlarmReceiver::class.java)
                    alarmIntent.putExtra(AddEditToDoActivity.EXTRA_ID,item.id)
                    val pendingAlarmIntent = PendingIntent.getBroadcast(this.applicationContext,
                        item.id!!,alarmIntent,FLAG_IMMUTABLE)
                    alarmManager?.set(AlarmManager.RTC_WAKEUP,dueDate,pendingAlarmIntent)
                }
            }
        }
    }
}