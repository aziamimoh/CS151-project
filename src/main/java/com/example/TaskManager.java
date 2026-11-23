package com.example;

import java.util.ArrayList;
import java.util.List;

// manage all tasks the restaurant has to do
public class TaskManager {
	// all tasks
	private List<Task> tasks;
	
	// can create multiple task managers in order to have different sets of tasks
	// or for more restaurants which will also have different tasks
	public TaskManager() {
		tasks = new ArrayList<>();
	}
	
	// same methods as in EmployeeTaskManager, but for different collections
	// EmployeeTaskManager --> an employee's tasks
	// TaskManager --> all tasks in the restaurant
	public void removeTask(Task task) {
		tasks.remove(task);
	}
	
	public List<Task> getTasks() {
		return new ArrayList<>(tasks);
	}
	
	// all relevant search methods
	// with similar logic but for different attributes
	// & utilizes getter methods from TaskImp but codes to interface
	public List<Task> searchByName(String name) {
		// for task objects with specified name
		List<Task> results = new ArrayList<>();
		
		for (Task task : tasks) {
			String taskName = task.getName();
			
			if (taskName.equalsIgnoreCase(name)) {
				results.add(task);
			}
		}
		
		return results;
	}
	
	public List<Task> searchByLength(int length) {
		// for task objects with specified length
		List<Task> results = new ArrayList<>();
		
		for (Task task : tasks) {
			int taskLength = task.getLength();
			
			if (taskLength == length) {
				results.add(task);
			}
		}
		
		return results;
	}
	
	public List<Task> searchByDifficulty(int difficulty) {
		// for task objects with specified difficulty
		List<Task> results = new ArrayList<>();
		
		for (Task task : tasks) {
			int taskDifficulty = task.getDifficulty();
			
			if (taskDifficulty == difficulty) {
				results.add(task);
			}
		}
		
		return results;
	}
	
	public List<Task> searchByAssigned(boolean assigned) {
		// for task objects with specified assigned status
		List<Task> results = new ArrayList<>();
		
		for (Task task : tasks) {
			boolean taskAssigned = task.isAssigned();
			
			if (taskAssigned = assigned) {
				results.add(task);
			}
		}	
		
		return results;
	}
			
	public List<Task> searchByDone(boolean done) {
		// for task objects with specified done status		
		List<Task> results = new ArrayList<>();
		
		for (Task task : tasks) {
			boolean taskDone = task.isDone();
			
			if (taskDone = done) {
				results.add(task);
			}
		}
		
		return results;
	}
	
	// getter method for all tasks with assigned = false & done = false
	public List<Task> getAvailableTasks() {
		// for task objects with availability
		List<Task> results = new ArrayList<>();
		
		for (Task task : tasks) {
			boolean taskAssigned = task.isAssigned();
			boolean taskDone = task.isDone();
			
			// adds non assigned and non done tasks to a list of available tasks
			if (!taskAssigned && !taskDone) {
				results.add(task);
			}
		}
			
		return results;
	}
	
}

