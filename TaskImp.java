package com.example;

import java.util.ArrayList;
import java.util.List;

public class TaskImp implements Task {
	// private attributes
	private List<Employee> employees;
	private String name;
	private int length;
	private int difficulty;
	private boolean assigned;
	private boolean done;
	
	// class constructor
	public TaskImp(String name, int length, int difficulty, boolean assigned, boolean done) {
		this.employees = new ArrayList<>();
		this.name = name;
		this.length = length;
		this.difficulty = difficulty;
		this.assigned = assigned;
		this.done = done;
	}
	
	// getters and setters
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override 
	public int getLength() {
		return length;
	} 
	
	@Override
	public void setLength(int length) {
		this.length = length;
	}
	
	@Override
	public int getDifficulty() {
		return difficulty;
	}
	
	@Override 
	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	
	@Override
	public boolean isAssigned() {
		return assigned;
	}
	
	@Override
	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}
	
	@Override
	public boolean isDone() {
		return done;
	}
	
	@Override
	public void setDone(boolean done) {
		this.done = done;
		
		if (done) {
			// removes task based upon employee that is assigned to it
			for (Employee employee : new ArrayList<>(employees)) {
				// updating employee attributes based on task that is done 
				employee.removeTask(this);
				employee.setTaskStatus(false);
				employee.setWorking(false);
			}
		
			// task has no employees on it
			employees.clear();
			assigned = false;
		}
	}
	
	@Override
	public void assignEmployee(Employee employee) {
		if (employee == null || done) {
			return;
		}
		
		if (employees.contains(employee)) {
			return;
		}
		
		// check for all proper attributes to be false in order to assign employee to a task
		if (!employee.isWorking() && !employee.isAssignedTask()) {
			// add Employee employee onto list that is working on task
			employees.add(employee);
			employee.addTask(this);
			employee.setTaskStatus(true);
				
			assigned = true;
		}
	}
	
	// do task that it is assigned to
	@Override
	public void doTask(Employee employee) {
		if (employees.contains(employee)) {
			employee.setWorking(true);
		}
	}
	
	// all assigned employees method implementation from interface
	// so it's easy to tell what employees are already assigned 
	// assign all employees not on list to a task 
	@Override
	public List<Employee> getEmployees() {
		return new ArrayList<>(employees);
	}		
	
}

