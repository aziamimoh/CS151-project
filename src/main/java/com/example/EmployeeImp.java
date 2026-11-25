package com.example;

public class EmployeeImp extends Employee{

    private EmployeeTaskManager taskManager;

    public EmployeeImp(String name, int id, int skillLevel, boolean working){
        super(name, id, skillLevel, working);
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
    

    public EmployeeTaskManager getTaskManager() {
        return taskManager;
    }



    
}
