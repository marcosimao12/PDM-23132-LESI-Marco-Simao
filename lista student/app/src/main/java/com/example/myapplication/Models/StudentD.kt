package com.example.myapplication.Models

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentD{
    @Insert
    suspend fun  insert(example: student)

    @Query("SELECT * FROM student WHERE id = :id")
    fun getById(id: Int): Flow<student>

    @Delete
    suspend fun delete(example: student)
}

