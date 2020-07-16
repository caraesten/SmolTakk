package com.smolltakk.scripts.db

import com.smoltakk.db.setup.InitializeDb

class CreateDb {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // The entry point!
            InitializeDb.setup()
        }
    }
}