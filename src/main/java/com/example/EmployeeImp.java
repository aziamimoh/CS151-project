package com.example;

public class EmployeeImp extends Employee{

    private EmployeeTaskManager taskManager;

    public EmployeeImp(String name, int id, int skillLevel, boolean working, boolean hasTask){
        super(name, id, skillLevel, working, hasTask);
    }
    
    public EmployeeImp(String name, int id, int skillLevel, boolean working) {
		this(name, id, skillLevel, working, false);
	}
    
	// change only the super attributes
	
    @Override 
    public String getName() {
        return super.getName();
    }

    @Override 
    public int getId() {
        return super.getId();
    }

    @Override 
    public int getSkillLevel() {
        return super.getSkillLevel();
    }

    @Override 
    public void setSkillLevel(int skillLevel){
        super.setSkillLevel(skillLevel);
    }

    @Override 
    public boolean isWorking(){
        return super.isWorking();
    }

    @Override
    public void setWorking(boolean working) {
        super.setWorking(working);
    }
    
    @Override 
    public boolean isAssignedTask() {
        return super.isAssignedTask();
    }

    public EmployeeTaskManager getTaskManager() {
        return taskManager;
    }



    
}
