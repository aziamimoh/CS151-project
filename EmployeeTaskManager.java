package com.example;
import java.util.ArrayList;
import java.util.List;

public class EmployeeTaskManager {
    private List<Task> tasks;

    public EmployeeTaskManager(){
        tasks = new ArrayList<>();
    }

    public EmployeeTaskManager(){
        tasks = new ArrayList<>();
    }

    public void addTask(Task task){
        if(!tasks.contains(task)){
            tasks.add(task);
        }
    }

    public void removeTask(Task task){
        tasks.remove(task);
    }

    public List<Task> getTasks(){
        return new ArrayList<>(tasks);
    }
}
