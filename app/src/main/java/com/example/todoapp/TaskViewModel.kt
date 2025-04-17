package com.example.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = TaskDatabase.getDatabase(application).taskDao()
    val allTasks: LiveData<List<Task>> = dao.getAllTasks()

    fun addTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        dao.insert(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        dao.delete(task)
    }
     fun updateTask(task: Task)=viewModelScope.launch(Dispatchers.IO) {
        dao.update(task)
    }

}