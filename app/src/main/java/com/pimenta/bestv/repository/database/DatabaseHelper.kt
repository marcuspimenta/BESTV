/*
 * Copyright (C) 2018 Marcus Pimenta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.pimenta.bestv.repository.database

import android.app.Application
import android.database.sqlite.SQLiteDatabase
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper
import com.j256.ormlite.support.ConnectionSource
import com.j256.ormlite.table.TableUtils
import com.pimenta.bestv.repository.entity.Movie
import com.pimenta.bestv.repository.entity.TvShow
import timber.log.Timber
import java.sql.SQLException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by marcus on 05-03-2018.
 */
@Singleton class DatabaseHelper @Inject constructor(
        private val application: Application
) : OrmLiteSqliteOpenHelper(application, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(database: SQLiteDatabase, connectionSource: ConnectionSource) {
        try {
            TableUtils.createTable(connectionSource, Movie::class.java)
            TableUtils.createTable(connectionSource, TvShow::class.java)
        } catch (exception: SQLException) {
            Timber.e(exception, "Can't create database")
            throw RuntimeException(exception)
        }
    }

    override fun onUpgrade(database: SQLiteDatabase, connectionSource: ConnectionSource, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        private const val DATABASE_NAME = "bestv.db"
        private const val DATABASE_VERSION = 1
    }
}