package com.example.todoapp

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.transition.TransitionInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todoapp.databinding.ActivityAddEditBinding

class AddEditActivity : AppCompatActivity() {

    private lateinit var id:String
    private lateinit var title:String
    private lateinit var desp:String
    private val taskViewModel: TaskViewModel by viewModels()
    private lateinit var task: Task


    private lateinit var binding:ActivityAddEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding=ActivityAddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.white)))

        //title
        val actionBarTitle = TextView(this).apply {
            text = "Add Update"
            setTextColor(ContextCompat.getColor(this@AddEditActivity, R.color.black))
            textSize = 20f
        }
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.customView = actionBarTitle

// Set back arrow color
        val upArrow = AppCompatResources.getDrawable(this, R.drawable.baseline_arrow_back_24)
        upArrow?.setTint(ContextCompat.getColor(this, R.color.black)) // your arrow color
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        supportActionBar?.title = "Add/Update" // Optional


        val intentdata = intent
        id= intentdata.getStringExtra("ID").toString()
        title= intentdata.getStringExtra("TITLE").toString()
        desp= intentdata.getStringExtra("DESCRIPTION").toString()
        desp= intentdata.getStringExtra("DESCRIPTION").toString()
        task = (intent.getSerializableExtra("LIST") as? Task?: return)

        println("atatatatat :"+id)
        println("atatatatat :"+title)
        println("atatatatat :"+desp)

        window.sharedElementEnterTransition = TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move).apply {
                duration=200
            }



        if(title.isNotEmpty() && !title.equals("null")){

            binding.inputTitle.setText(title)
            binding.inputDesc.setText(desp)

        }

    }

    override fun onDestroy() {
        super.onDestroy()

        var updatetitle=binding.inputTitle.text.toString()
        var updateDesc=binding.inputDesc.text.toString()

        println("atatattatat :"+binding.inputTitle.toString())
        if(updatetitle.equals("null")||updatetitle.equals(""))
        {

        }else
        {

            if(title.isNotEmpty() && !title.equals("null"))
            {
                //update


                val updatedTask = task.copy(title = updatetitle, description = updateDesc)
                taskViewModel.updateTask(updatedTask)

            }else
            {

                //add
                val task = Task(title = updatetitle, description = updateDesc)
                taskViewModel.addTask(task)
            }
        }

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed() // For modern Android
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}