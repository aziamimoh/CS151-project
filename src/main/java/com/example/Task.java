package com.example;

import java.util.list;

public interface Task {
	String getName();
	void setName(String name);
	int getLength();
	void setLength(int length);
	int getDifficulty();
	void setDifficulty(int difficulty);
	boolean isAssigned();
	void setAssigned(boolean assigned);
	boolean isDone();
	void setDone(boolean done);
	
	// assigning employee to a task
	void assignEmployee(Employee employee);
	void doTask(Employee employee);
	
	// assigned employees for every task implement
	List<Employee> getAssignedEmployees;
}

