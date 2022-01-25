package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.database.ElectionDatabase

class ElectionTrackerApplication : Application() {
    val database: ElectionDatabase by lazy { ElectionDatabase.getInstance(this) }
}