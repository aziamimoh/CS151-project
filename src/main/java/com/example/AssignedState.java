package com.example;

public class AssignedState implements TaskState {

    @Override
    public void assignEmployee(TaskImp task, Employee employee) {
        // this task already has an employee assigned
    }

    @Override
    public void doTask(TaskImp task, Employee employee) {
        // verify that this employee is assigned and available to work
        if (employee != null
                && task.hasAssignedEmployee(employee)
                && !employee.isWorking()) {

            // employee begins working
            employee.setWorking(true);

            // change task state
            task.changeState(task.getInProgressState());
        }
    }

    @Override
    public void completeTask(TaskImp task) {
        // a task must be in progress before it can be completed
    }

    @Override
    public void unassignTask(TaskImp task) {
        // release employee and return task to unassigned
        task.releaseAssignedEmployees();

        task.changeState(task.getUnassignedState());
    }

    @Override
    public String getStateName() {
        return "ASSIGNED";
    }
}

