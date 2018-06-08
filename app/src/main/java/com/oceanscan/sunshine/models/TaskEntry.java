package com.oceanscan.sunshine.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;
@Entity(tableName="tasks")
public class TaskEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private int priority;
    @ColumnInfo(name="updatedAt")
    private Date updatedAt;

    public TaskEntry(int id,String description,int priority, Date updatedAt){
        this.setId(id);
        this.setDescription(description);
        this.setPriority(priority);
        this.setUpdatedAt(updatedAt);
    }
    @Ignore
    public TaskEntry(String description,int priority, Date updatedAt){

        this.setDescription(description);
        this.setPriority(priority);
        this.setUpdatedAt(updatedAt);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
