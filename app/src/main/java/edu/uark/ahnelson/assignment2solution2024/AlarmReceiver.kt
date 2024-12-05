package edu.uark.ahnelson.assignment2solution2024

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import edu.uark.ahnelson.assignment2solution2024.AddEditToDoActivity.AddEditToDoActivity
import edu.uark.ahnelson.assignment2solution2024.Util.NotificationUtils
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        val id = intent.getIntExtra(AddEditToDoActivity.EXTRA_ID,0)
        Log.d("BroadcastReceiver", id.toString())
        (context.applicationContext as ToDoListApplication).applicationScope.launch {
            val toDoItem = (context.applicationContext as ToDoListApplication).repository.getToDoItem(id)
            if (toDoItem.content.length > 100)
                NotificationUtils().createNotification(context,"ToDoItem \"${toDoItem.title}\" is due!",toDoItem.content.substring(0,100)+"...",id)
            else
                NotificationUtils().createNotification(context,"ToDoItem \"${toDoItem.title}\" is due!",toDoItem.content,id)
        }
    }
}
