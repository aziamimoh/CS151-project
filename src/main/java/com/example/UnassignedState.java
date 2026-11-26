package com.example;

public class UnassignedState implements TaskState {

    // assigns an available employee to the task
    @Override
    public void assignEmployee(TaskImp task, Employee employee) {
        // check whether the employee is available
        if (task.canAssignEmployee(employee)) {

            // add employee to the task
            task.addAssignedEmployee(employee);

            // add task to the employee's task manager
            employee.addTask(task);

            // change task state
            task.changeState(task.getAssignedState());
        }
    }

    @Override
    public void doTask(TaskImp task, Employee employee) {
        // a task must be assigned before it can start
    }

    @Override
    public void completeTask(TaskImp task) {
        // an unassigned task cannot be completed
    }

    @Override
    public void unassignTask(TaskImp task) {
        // the task is already unassigned
    }

    @Override
    public String getStateName() {
        return "UNASSIGNED";
    }
}

