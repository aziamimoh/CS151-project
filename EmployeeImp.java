package com.example;

public class EmployeeImp implements Employee{

    private String name;
    private int id;
    private int skillLevel;
    private boolean working;

    private EmployeeTaskManager taskManager;

    public EmployeeImp(String name, int id, int skillLevel, boolean working){
        this.name = name;
        this.id = id;
        this.skillLevel = skillLevel;
        this.working = working;
        this.taskManager = new EmployeeTaskManager();
    }

    @Override 
    public String getName(){
        return name;
    }

    @Override 
    public int getId(){
        return id;
    }

    @Override 
    public int getSkillLevel(){
        return skillLevel;
    }

    @Override 
    public void setSkillLevel(int skillLevel){
        this.skillLevel = skillLevel;
    }

    @Override 
    public boolean isWorking(){
        return working;
    }

    @Override
    public void setWorking(boolean working)
    {
        this.working = working;
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
