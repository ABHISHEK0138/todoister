package com.bawp.todoister.data;

import com.bawp.todoister.model.Task;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TaskDao {

    @Insert
    void insertTask(Task task);

    @Query("DELETE FROM task_table")
    void deleteAll();

    @Query("SELECT * FROM task_table where is_done = 0")
    LiveData<List<Task>> getTasks();

    @Query("SELECT * FROM task_table WHERE task_table.task_id == :id")
    LiveData<Task> get(long id);

    @Query("SELECT * FROM task_table WHERE task_table.task_id LIKE :task_name")
    LiveData<Task> get_t(String task_name);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);


}
