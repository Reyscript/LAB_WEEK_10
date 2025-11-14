package com.example.lab_week_10

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: TotalViewModel
    private lateinit var database: TotalDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        database = TotalDatabase.getDatabase(this)
        viewModel = ViewModelProvider(this)[TotalViewModel::class.java]

        initializeValueFromDatabase()
        prepareViewModel()
    }

    override fun onStart() {
        super.onStart()
        showLastUpdatedToast()
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { currentTotal ->
            updateText(currentTotal)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun initializeValueFromDatabase() {
        try {
            val total = database.totalDao().getTotal(1)
            if (total == null) {
                val currentDate = Date().toString()
                database.totalDao().insert(Total(id = 1, total = TotalObject(0, currentDate)))
                viewModel.setLastUpdated(currentDate)
            } else {
                viewModel.setTotal(total.total.value)
                viewModel.setLastUpdated(total.total.date)
            }
        } catch (e: Exception) {
            viewModel.setTotal(0)
            viewModel.setLastUpdated("Never")
        }
    }

    private fun showLastUpdatedToast() {
        val lastUpdated = viewModel.lastUpdated.value
        if (!lastUpdated.isNullOrEmpty() && lastUpdated != "Never") {
            Toast.makeText(this, "Last updated: $lastUpdated", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            val currentDate = Date().toString()
            val currentTotal = viewModel.total.value ?: 0
            database.totalDao().update(Total(id = 1, total = TotalObject(currentTotal, currentDate)))
            viewModel.setLastUpdated(currentDate)
        } catch (e: Exception) {
        }
    }
}