package com.example;

public interface TaskState {
	// all methods implemented in the state classes
	// to change the task behavior depending on the state its in
    void assignEmployee(TaskImp task, Employee employee);
    void doTask(TaskImp task, Employee employee);
    void completeTask(TaskImp task);
    void unassignTask(TaskImp task);

	// string to return the current state name 
    String getStateName();
}

