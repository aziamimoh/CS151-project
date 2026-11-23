package com.example;

public class EmployeeImp extends Employee{

    private EmployeeTaskManager taskManager;

    public EmployeeImp(String name, int id, int skillLevel, boolean working){
        super(name, id, skillLevel, working);
        this.taskManager = new EmployeeTaskManager();
    }
    @Override 
    public boolean isAssignedTask()
    {
        return !taskManager.getTasks().isEmpty();
    }

    @Override
    public void addTask(Task task)
    {
        taskManager.addTask(task);
    }

    @Override
    public void removeTask(Task task)
    {
        taskManager.removeTask(task);
    }

    public EmployeeTaskManager getTaskManager()
    {
        return taskManager;
    }



    
}
