package com.example;

public abstract class Employee {
    
    private String name;
    private int id;
    private int skillLevel;
    private boolean working;
    private boolean hasTask;

    public Employee(String name, int id, int skillLevel, boolean working, boolean hasTask){
        this.name = name;
        this.id = id;
        this.skillLevel = skillLevel;
        this.working = working;
        this.hasTask = hasTask;
        this.taskManager = new EmployeeTaskManager();
    }

    public void setName(String name) {
		this.name = name;
	}
    
    public String getName(){
        return name;
    }

    public void setId(int id) {
		this.id = id;
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

    public boolean isAssignedTask() {
		return hasTask;
	}
    
    public void setTaskStatus(boolean hasTask) {
		this.hasTask = hasTask;
	}

    private EmployeeTaskManager taskManager;
    
    public void addTask(Task task) {
		taskManager.addTask(task);
	}

	public void removeTask(Task task) {
		taskManager.removeTask(task);
	}
}
