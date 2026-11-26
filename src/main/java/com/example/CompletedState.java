package com.example;

public class CompletedState implements TaskState {
    @Override
    public void assignEmployee(TaskImp task, Employee employee) {
        // a completed task cannot be assigned again
    }

    @Override
    public void doTask(TaskImp task, Employee employee) {
        // a completed task cannot be started again
    }

    @Override
    public void completeTask(TaskImp task) {
        // the task is already completed
    }

    @Override
    public void unassignTask(TaskImp task) {
        // a completed task cannot be unassigned
    }

    @Override
    public String getStateName() {
        return "COMPLETED";
    }
}

