package com.example;

public abstract class Employee {
    
    private String name;
    private int id;
    private int skillLevel;
    private boolean working;

    public Employee(String name, int id, int skillLevel, boolean working){
        this.name = name;
        this.id = id;
        this.skillLevel = skillLevel;
        this.working = working;
    }
    public String getName(){
        return name;
    }
   
    public int getId(){
        return id;
    }
    
    public int getSkillLevel(){
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel){
        this.skillLevel = skillLevel;
    }

    public boolean isWorking(){
        return working;
    }

    public void setWorking(boolean working){
        this.working = working;
    }

    public abstract boolean isAssignedTask();
}
