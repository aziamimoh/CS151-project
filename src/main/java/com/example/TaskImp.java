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
	private TaskState state = TaskState.UNASSIGNED;
	
	// class constructor
	public TaskImp(String name, int length, int difficulty, boolean assigned, boolean done) {
		this.employees = new ArrayList<>();
		this.name = name;
		this.length = length;
		this.difficulty = difficulty;
		this.assigned = assigned;
		this.done = done;
	}
	
	// state pattern implementation
	private enum TaskState {
		UNASSIGNED {
			@Override
			boolean assign(TaskImp task, Employee employee) {
				return task.assignFromUnassigned(employee);
			}
		},

		ASSIGNED {
			@Override
			boolean start(TaskImp task, Employee employee) {
				return task.startFromAssigned(employee);
			}
		},

		IN_PROGRESS {
			@Override
			boolean start(TaskImp task, Employee employee) {
				return task.startFromAssigned(employee);
			}

			@Override
			boolean complete(TaskImp task) {
				return task.completeFromInProgress();
			}
		},

		COMPLETED;

			boolean assign(TaskImp task, Employee employee) {
				return false;
			}

			boolean start(TaskImp task, Employee employee) {
				return false;
			}

			boolean complete(TaskImp task) {
				return false;
			}
			
	}
	
	// helper methods for state pattern behavior
    // assign employee while task is NOT assigned
    private boolean assignFromUnassigned(Employee employee) {
        if (employee == null || employee.isWorking() || employee.hasTasks()) {
            return false;
        }

        employees.add(employee);
        employee.addTask(this);

        state = TaskState.ASSIGNED;

        return true;
    }


    // Start task while task is ASSIGNED
    private boolean startFromAssigned(Employee employee) {
        if (employee == null || !employees.contains(employee) || employee.isWorking()) {
            return false;
        }

        employee.setWorking(true);
        state = TaskState.IN_PROGRESS;

        return true;
    }


    // Complete task while task is IN_PROGRESS
    private boolean completeFromInProgress() {
        if (employees.isEmpty()) {
            return false;
        }

        for (Employee employee : new ArrayList<>(employees)) {
            employee.removeTask(this);
            employee.setWorking(false);
        }

        employees.clear();
		
		assigned = false;
        done = true;
        state = TaskState.COMPLETED;

        return true;
    }


    // Unassign task while task is ASSIGNED
    private boolean unassignFromAssigned() {
        for (Employee employee : new ArrayList<>(employees)) {
            employee.removeTask(this);
            employee.setWorking(false);
        }

        employees.clear();
        state = TaskState.UNASSIGNED;

        return true;
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

    if (state.assign(this, employee)) {
        assigned = true;
    }
}
	
	@Override
	public void completeTask() {
		state.complete(this);
	}
	
	// do task that it is assigned to
	@Override
    public void doTask(Employee employee) {
    state.start(this, employee);
}
	
	// all assigned employees method implementation from interface
	// so it's easy to tell what employees are already assigned 
	// assign all employees not on list to a task 
	@Override
	public List<Employee> getAssignedEmployees() {
		return new ArrayList<>(employees);
	}		
	
}

