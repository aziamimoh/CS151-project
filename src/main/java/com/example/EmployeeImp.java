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

    public EmployeeTaskManager getTaskManager()
    {
        return taskManager;
    }



    
}
