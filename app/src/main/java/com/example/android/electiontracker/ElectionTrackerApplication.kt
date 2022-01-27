package com.example.android.electiontracker

import android.app.Application
import com.example.android.electiontracker.database.ElectionDatabase

class ElectionTrackerApplication : Application() {
    val database: ElectionDatabase by lazy { ElectionDatabase.getInstance(this) }
}