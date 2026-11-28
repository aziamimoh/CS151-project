package com.example;

import java.util.ArrayList;
import java.util.List;

public class TaskImp implements Task {
	// private attributes
	private List<Employee> employees;
	private String name;
	private int length;
	private int difficulty;
	
	// state pattern objects
    private final TaskState unassignedState;
    private final TaskState assignedState;
    private final TaskState inProgressState;
    private final TaskState completedState;

    // current task state
    private TaskState currentState;

    // constructor
    public TaskImp(String name, int length, int difficulty, boolean assigned, boolean done) {
        employees = new ArrayList<>();

        this.name = name;
        this.length = length;
        this.difficulty = difficulty;

        // create the four state objects
        unassignedState = new UnassignedState();
        assignedState = new AssignedState();
        inProgressState = new InProgressState();
        completedState = new CompletedState();

        // setting up the initial state
        if (done) {
            currentState = completedState;
        } else if (assigned) {
            currentState = assignedState;
        } else {
            currentState = unassignedState;
        }
    }

    // constructor for a new unassigned task
    public TaskImp(String name, int length, int difficulty) {
        this(name, length, difficulty, false, false);
    }

    // methods used by the state classes
    
    public boolean canAssignEmployee(Employee employee) {
		return employee != null && employee.isOnSchedule() && !employee.isWorking() 
		&& !employee.hasTasks() && employee.getSkillLevel() >= difficulty;
	}

    public void addAssignedEmployee(Employee employee) {
        if (employee != null && !employees.contains(employee)) {
            employees.add(employee);
        }
    }

    public boolean hasAssignedEmployee(Employee employee) {
        return employee != null && employees.contains(employee);
    }

    public boolean hasAssignedEmployees() {
        return !employees.isEmpty();
    }

    public void releaseAssignedEmployees() {
        for (Employee employee : new ArrayList<>(employees)) {
            employee.removeTask(this);
            employee.setWorking(false);
        }
        
        employees.clear();
    }

    // change the current state
    public void changeState(TaskState newState) {
        if (newState != null) {
            currentState = newState;
        }
    }

    // state pattern getters
    
    public TaskState getUnassignedState() {
        return unassignedState;
    }

    public TaskState getAssignedState() {
        return assignedState;
    }

    public TaskState getInProgressState() {
        return inProgressState;
    }

    public TaskState getCompletedState() {
        return completedState;
    }


    // task information getters and setters
    
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

    // checking whether task is assigned

    @Override
    public boolean isAssigned() {
        return currentState == assignedState || currentState == inProgressState;
    }

    @Override
    public void setAssigned(boolean assigned) {
        if (!assigned) {
            currentState.unassignTask(this);
        }
    }

    // checking whether task is completed

    @Override
    public boolean isDone() {
        return currentState == completedState;
    }

    @Override
    public void setDone(boolean done) {
        if (done) {
            currentState.completeTask(this);
        }
    }

    // public task actions
    // delegating proper behavior to the current state

    @Override
    public void assignEmployee(Employee employee) {
        currentState.assignEmployee(this, employee);
    }

    @Override
    public void doTask(Employee employee) {
        currentState.doTask(this, employee);
    }

    @Override
    public void completeTask() {
        currentState.completeTask(this);
    }

    // getting list of total assigned employees
    @Override
    public List<Employee> getAssignedEmployees() {
        return new ArrayList<>(employees);
    }

    // getting readable current state name
    public String getStateName() {
        return currentState.getStateName();
    }
}
