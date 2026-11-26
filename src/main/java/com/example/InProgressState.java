package com.example;

public class InProgressState implements TaskState {

    @Override
    public void assignEmployee(TaskImp task, Employee employee) {
        // an in-progress task cannot be assigned to another employee
    }

    @Override
    public void doTask(TaskImp task, Employee employee) {
        // the task is already in progress
    }

    @Override
    public void completeTask(TaskImp task) {
        // verify that the task has an assigned employee
        if (task.hasAssignedEmployees()) {

            // release assigned employees
            task.releaseAssignedEmployees();

            // change task state
            task.changeState(task.getCompletedState());
        }
    }

    @Override
    public void unassignTask(TaskImp task) {
        // an in-progress task cannot be unassigned
    }

    @Override
    public String getStateName() {
        return "IN_PROGRESS";
    }
}
