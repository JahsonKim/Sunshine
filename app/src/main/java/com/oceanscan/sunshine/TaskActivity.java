package com.oceanscan.sunshine;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.oceanscan.sunshine.data.AppDatabase;
import com.oceanscan.sunshine.models.TaskDao;
import com.oceanscan.sunshine.models.TaskEntry;

import java.util.Date;
import java.util.List;

public class TaskActivity extends AppCompatActivity {

    AppDatabase appDatabase;
    private static String TAG = TaskActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        appDatabase=AppDatabase.getInstance(getApplicationContext());

        //add items in the database
        Date date=new Date();
        TaskEntry taskEntry=new TaskEntry("Task 1",1,date);
        TaskEntry taskEntry1=new TaskEntry("Task 2",0,date);
        TaskEntry taskEntry2=new TaskEntry("Task 3",2,date);
        appDatabase.taskDao().insertTask(taskEntry);
        appDatabase.taskDao().insertTask(taskEntry1);
        appDatabase.taskDao().insertTask(taskEntry2);
        List<TaskEntry> allTask=appDatabase.taskDao().getAllTasks();
        for(int i=0;i<allTask.size();i++){
            Log.i(TAG,"Tasks "+allTask.get(i).getDescription());
        }
    }
}
