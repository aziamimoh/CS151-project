package com.example;

public class EmployeeImp extends Employee{

    private EmployeeTaskManager taskManager;

    public EmployeeImp(String name, int id, int skillLevel, boolean working, boolean hasTask){
        super(name, id, skillLevel, working, hasTask);
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
