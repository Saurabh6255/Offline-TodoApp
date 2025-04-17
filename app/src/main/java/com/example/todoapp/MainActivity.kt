package com.example.todoapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.todoapp.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var adapter: TaskAdapter
    private var originalTaskList: List<Task> = emptyList()
    private lateinit var binding:ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.fabAddTask.setOnClickListener {
//            showAddTaskDialog()

            var i=Intent(this,AddEditActivity::class.java)
            startActivity(i)
        }

        adapter = TaskAdapter(
            this,
            onEditClick = { task -> showEditTaskDialog(task) }
        )


        binding.taskRecycler.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        binding.taskRecycler.adapter = adapter

        taskViewModel.allTasks.observe(this, Observer { list ->
            originalTaskList = list

            list?.let { adapter.updateList(it) }
        })




        binding.etSearch.addTextChangedListener {
            val query = it.toString().trim()
            val filtered = originalTaskList.filter { task ->
                task.title.contains(query, ignoreCase = true) ||
                        task.description.contains(query, ignoreCase = true)
            }
            adapter.updateList(filtered)
        }


        binding.swipeRefreshLayout.setOnRefreshListener {
            // Trigger your refresh logic here
//            taskViewModel.refreshTasks()
            taskViewModel.allTasks.observe(this, Observer { list ->
                list?.let { adapter.updateList(it) }
            })

            // Optionally delay hiding the refresh spinner
            Handler(Looper.getMainLooper()).postDelayed({
                binding.swipeRefreshLayout.isRefreshing = false
            }, 1000) // 1 second delay for smooth UX
        }


        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Delete Note")
                    .setMessage("Do you want to delete this note?")
                    .setPositiveButton("Yes") { _, _ ->
                        val task = adapter.getTaskAt(position)
                        taskViewModel.deleteTask(task)
                        Toast.makeText(this@MainActivity, "Note deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("No") { _, _ ->
                        // Reset the item if "No" is clicked
                        adapter.notifyItemChanged(position)  // Refreshes the item at the position
                    }
                    .create()
                    .show()

            }
        }

        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.taskRecycler)


    }


    private fun showAddTaskDialog() {
        val dialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)

        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set width to 90% of screen
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.90).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val titleInput = dialogView.findViewById<EditText>(R.id.inputTitle)
        val descInput = dialogView.findViewById<EditText>(R.id.inputDesc)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)


        btnAdd.setOnClickListener {

                        val title = titleInput.text.toString().trim()
                        val desc = descInput.text.toString().trim()
                        if (title.isNotEmpty()) {
                            val task = Task(title = title, description = desc)
                            taskViewModel.addTask(task)
                            dialog.dismiss()
                        } else
                        {
                            titleInput.error = "Title is required"
                        }
        }

        dialog.show()
    }


    private fun showEditTaskDialog(task: Task) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null)
        val titleEdit = dialogView.findViewById<EditText>(R.id.inputTitle)
        val descEdit = dialogView.findViewById<EditText>(R.id.inputDesc)
        val btnAdd = dialogView.findViewById<Button>(R.id.btnAdd)




        titleEdit.setText(task.title)
        descEdit.setText(task.description)

        val alertDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        btnAdd.setOnClickListener {


            val newTitle = titleEdit.text.toString()
            val newDesc = descEdit.text.toString()

            if (newTitle.isNotEmpty()) {
                val updatedTask = task.copy(title = newTitle, description = newDesc)
                taskViewModel.updateTask(updatedTask)
                alertDialog.dismiss()
            } else {
                titleEdit.error = "Title cannot be empty"
            }
        }



        alertDialog.show()
    }


    private fun showDeleteConfirmationDialog() {

    }


    override fun onResume() {
        super.onResume()
        taskViewModel.allTasks.observe(this, Observer { list ->
            list?.let { adapter.updateList(it) }
        })
    }

}