package com.example.todoapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.core.util.Pair as UtilPair

class TaskAdapter(
    private val context: Context,
    private val onEditClick: (Task) -> Unit

) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>()
{
    private var taskList = mutableListOf<Task>()


    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.txtTitle)
        val desc: TextView = view.findViewById(R.id.txtDescription)
        val cardview: CardView = view.findViewById(R.id.cardView)
        private val editIcon = itemView.findViewById<ImageView>(R.id.imgEdit)



        fun bind(task: Task) {
            title.text = task.title
            desc.text = task.description

            editIcon.setOnClickListener {
                onEditClick(task)
            }


        }
    }

    fun updateList(newList: List<Task>) {
        taskList = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount() = taskList.size
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])

        holder.itemView.setOnClickListener {

//            var i=Intent(context, AddEditActivity::class.java)
//            i.putExtra("ID",taskList[position].id)
//            i.putExtra("TITLE",taskList[position].title)
//            i.putExtra("DESCRIPTION",taskList[position].description)
//            i.putExtra("LIST",taskList[position])
//            context.startActivity(i)


            val i = Intent(context, AddEditActivity::class.java).apply {
                putExtra("ID", taskList[position].id)
                putExtra("TITLE", taskList[position].title)
                putExtra("DESCRIPTION", taskList[position].description)
                putExtra("LIST", taskList[position])
            }

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                context as Activity, // make sure context is cast to Activity
                UtilPair.create(holder.cardview as View, "task_title"),
            )

            ContextCompat.startActivity(context, i, options.toBundle())

        }


    }

    fun getTaskAt(position: Int): Task = taskList[position]


}