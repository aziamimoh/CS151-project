package com.example;

public abstract class Employee {
    
    private String name;
    private int id;
    private int skillLevel;
    private boolean working;
    private boolean onSchedule;

    public Employee(String name, int id, int skillLevel, boolean onSchedule){
        this.name = name;
        this.id = id;
        this.skillLevel = skillLevel;
        this.onSchedule = onSchedule;
        this.working = false;
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
    
    public boolean isOnSchedule() {
		return onSchedule;
	}
	
	public void setOnSchedule(boolean onSchedule) {
		this.onSchedule = onSchedule;
	}

    private EmployeeTaskManager taskManager;
    
    public void addTask(Task task) {
		taskManager.addTask(task);
	}

	public void removeTask(Task task) {
		taskManager.removeTask(task);
	}
	
	public boolean hasTasks() {
		return !taskManager.getTasks().isEmpty();
	}
}
