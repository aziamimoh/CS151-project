package com.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskDashboard extends Application {

    private TaskManager taskManager;
    private List<Employee> employees;
    private final Map<Task, Integer> taskNumbers = new HashMap<>();

    private TableView<TaskRow> taskTable;
    private ObservableList<TaskRow> visibleRows;

    private TextField searchField;
    private ComboBox<String> lengthFilter;
    private ComboBox<String> difficultyFilter;
    private ComboBox<String> availabilityFilter;
    private ComboBox<String> statusFilter;

    private Label totalTasksValue;
    private Label assignedTasksValue;
    private Label unassignedTasksValue;

    @Override
    public void start(Stage stage) {

        taskManager = new TaskManager();
        employees = new ArrayList<>();
        visibleRows = FXCollections.observableArrayList();

        createSampleData();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F4F6FA;");

        VBox content = new VBox(16);

        content.getChildren().addAll(
                createHeader(),
                createSummaryCards(),
                createFilterPanel(),
                createTaskTable()
        );

        root.setCenter(content);

        refreshDashboard();

        Scene scene = new Scene(root, 1180, 720);

        stage.setTitle("Restaurant Rush - Task Manager");
        stage.setMinWidth(950);
        stage.setMinHeight(620);
        stage.setScene(scene);
        stage.show();
    }

    // Dashboard title

    private VBox createHeader() {

        Label title = new Label("TASK MANAGER");

        title.setStyle(
                "-fx-font-size: 25px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #202B3C;"
        );

        Label subtitle = new Label(
                "View, assign, and track restaurant tasks"
        );

        subtitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #687386;"
        );

        VBox header = new VBox(3, title, subtitle);

        return header;
    }

    // Summary cards

    private HBox createSummaryCards() {

        totalTasksValue = createMetricValue();
        assignedTasksValue = createMetricValue();
        unassignedTasksValue = createMetricValue();

        VBox totalCard = createSummaryCard(
                "TOTAL TASKS",
                totalTasksValue,
                "#E2EEFC",
                "#244A78"
        );

        VBox assignedCard = createSummaryCard(
                "ASSIGNED",
                assignedTasksValue,
                "#DFF1E7",
                "#246443"
        );

        VBox unassignedCard = createSummaryCard(
                "UNASSIGNED",
                unassignedTasksValue,
                "#FFF0D8",
                "#8A5A16"
        );

        HBox cards = new HBox(
                14,
                totalCard,
                assignedCard,
                unassignedCard
        );

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(assignedCard, Priority.ALWAYS);
        HBox.setHgrow(unassignedCard, Priority.ALWAYS);

        return cards;
    }

    private Label createMetricValue() {

        Label value = new Label("0");

        value.setStyle(
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;"
        );

        return value;
    }

    private VBox createSummaryCard(
            String heading,
            Label value,
            String background,
            String textColor) {

        Label label = new Label(heading);

        label.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + textColor + ";"
        );

        value.setStyle(
                "-fx-font-size: 27px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + textColor + ";"
        );

        VBox card = new VBox(7, label, value);

        card.setPadding(new Insets(14, 16, 14, 16));
        card.setMinHeight(88);

        card.setStyle(
                "-fx-background-color: " + background + ";" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #D9E0E9;" +
                "-fx-border-radius: 10px;"
        );

        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);

        return card;
    }

    // Search and filter section

    private VBox createFilterPanel() {

        Label panelTitle = new Label("SEARCH & FILTER TASKS");

        panelTitle.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #344054;"
        );

        searchField = new TextField();
        searchField.setPromptText("Search task name or number...");
        searchField.setPrefWidth(300);

        styleControl(searchField);

        lengthFilter = new ComboBox<>();

        lengthFilter.getItems().addAll(
                "All lengths",
                "5",
                "8",
                "10",
                "12",
                "15",
                "20",
                "25",
                "30"
        );

        lengthFilter.setValue("All lengths");
        styleControl(lengthFilter);

        difficultyFilter = new ComboBox<>();

        difficultyFilter.getItems().addAll(
                "All levels",
                "1",
                "2",
                "3",
                "4",
                "5"
        );

        difficultyFilter.setValue("All levels");
        styleControl(difficultyFilter);

        availabilityFilter = new ComboBox<>();

        availabilityFilter.getItems().addAll(
                "All",
                "Assigned",
                "Unassigned"
        );

        availabilityFilter.setValue("All");
        styleControl(availabilityFilter);

        statusFilter = new ComboBox<>();

        statusFilter.getItems().addAll(
                "All statuses",
                "Not Started",
                "In Progress",
                "Completed"
        );

        statusFilter.setValue("All statuses");
        styleControl(statusFilter);

        HBox firstRow = new HBox(
                12,
                createFilterField("TASK NAME / NUMBER", searchField),
                createFilterField("LENGTH (MINUTES)", lengthFilter),
                createFilterField("DIFFICULTY", difficultyFilter)
        );

        HBox secondRow = new HBox(
                12,
                createFilterField("AVAILABILITY", availabilityFilter),
                createFilterField("COMPLETION STATUS", statusFilter)
        );

        HBox.setHgrow(firstRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(firstRow.getChildren().get(1), Priority.ALWAYS);
        HBox.setHgrow(firstRow.getChildren().get(2), Priority.ALWAYS);

        HBox.setHgrow(secondRow.getChildren().get(0), Priority.ALWAYS);
        HBox.setHgrow(secondRow.getChildren().get(1), Priority.ALWAYS);

        Button clearButton = new Button("Clear Filters");

        clearButton.setStyle(
                "-fx-background-color: #E8EDF4;" +
                "-fx-text-fill: #344054;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 7px;" +
                "-fx-padding: 8px 14px;"
        );

        clearButton.setOnAction(event -> clearFilters());

        HBox bottomRow = new HBox(clearButton);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> refreshTable()
        );

        lengthFilter.setOnAction(event -> refreshTable());
        difficultyFilter.setOnAction(event -> refreshTable());
        availabilityFilter.setOnAction(event -> refreshTable());
        statusFilter.setOnAction(event -> refreshTable());

        VBox panel = new VBox(
                12,
                panelTitle,
                firstRow,
                secondRow,
                bottomRow
        );

        panel.setPadding(new Insets(14));

        panel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #DCE2EA;" +
                "-fx-border-radius: 10px;"
        );

        return panel;
    }

    private VBox createFilterField(
            String labelText,
            javafx.scene.control.Control control) {

        Label label = new Label(labelText);

        label.setStyle(
                "-fx-font-size: 10px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #667085;"
        );

        control.setMaxWidth(Double.MAX_VALUE);

        VBox field = new VBox(5, label, control);

        HBox.setHgrow(field, Priority.ALWAYS);

        return field;
    }

    private void styleControl(
            javafx.scene.control.Control control) {

        control.setStyle(
                "-fx-background-color: #FFFFFF;" +
                "-fx-border-color: #D0D7E2;" +
                "-fx-border-radius: 6px;" +
                "-fx-background-radius: 6px;" +
                "-fx-font-size: 12px;"
        );
    }

    // Task table

    private VBox createTaskTable() {

        Label tableTitle = new Label("TASKS");

        tableTitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #344054;"
        );

        Label tableHint = new Label(
                "Use the action button on each row to assign, start, or complete a task."
        );

        tableHint.setStyle(
                "-fx-font-size: 11px;" +
                "-fx-text-fill: #758195;"
        );

        HBox titleRow = new HBox(10, tableTitle, tableHint);

        titleRow.setAlignment(Pos.CENTER_LEFT);

        taskTable = new TableView<>();

        taskTable.setItems(visibleRows);

        taskTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        taskTable.setPlaceholder(
                new Label("No tasks match your filters.")
        );

        taskTable.setPrefHeight(390);

        taskTable.setStyle(
                "-fx-background-radius: 7px;" +
                "-fx-border-radius: 7px;"
        );

        // Task number column

        TableColumn<TaskRow, String> numberColumn =
                new TableColumn<>("#");

        numberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        String.valueOf(cell.getValue().number)
                )
        );

        numberColumn.setPrefWidth(45);

        // Task name column

        TableColumn<TaskRow, String> nameColumn =
                new TableColumn<>("TASK NAME");

        nameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().task.getName()
                )
        );

        nameColumn.setPrefWidth(220);

        // Length column

        TableColumn<TaskRow, String> lengthColumn =
                new TableColumn<>("LENGTH");

        lengthColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().task.getLength() + " min"
                )
        );

        lengthColumn.setPrefWidth(90);

        // Difficulty column

        TableColumn<TaskRow, String> difficultyColumn =
                new TableColumn<>("DIFFICULTY");

        difficultyColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().task.getDifficulty() + " / 5"
                )
        );

        difficultyColumn.setPrefWidth(95);

        // Availability column

        TableColumn<TaskRow, String> availabilityColumn =
                new TableColumn<>("AVAILABILITY");

        availabilityColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        getAvailability(cell.getValue().task)
                )
        );

        availabilityColumn.setPrefWidth(110);

        // Status column

        TableColumn<TaskRow, String> statusColumn =
                new TableColumn<>("STATUS");

        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        getStatus(cell.getValue().task)
                )
        );

        statusColumn.setPrefWidth(115);

        // Employee column

        TableColumn<TaskRow, String> employeeColumn =
                new TableColumn<>("EMPLOYEE");

        employeeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        getEmployeeNames(cell.getValue().task)
                )
        );

        employeeColumn.setPrefWidth(130);

        // Quick action column

        TableColumn<TaskRow, Void> actionColumn =
                new TableColumn<>("QUICK ACTION");

        actionColumn.setPrefWidth(115);

        actionColumn.setCellFactory(column ->
                new TableCell<TaskRow, Void>() {

            private final Button actionButton = new Button();

            {
                actionButton.setMaxWidth(Double.MAX_VALUE);

                actionButton.setStyle(
                        "-fx-background-color: #E4ECF8;" +
                        "-fx-text-fill: #284B75;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 6px;"
                );

                actionButton.setOnAction(event -> {

                    int rowIndex = getIndex();

                    if (rowIndex >= 0
                            && rowIndex < getTableView().getItems().size()) {

                        Task selectedTask =
                                getTableView().getItems()
                                .get(rowIndex).task;

                        handleTaskAction(selectedTask);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || getIndex() < 0
                        || getIndex() >= getTableView().getItems().size()) {

                    setGraphic(null);

                } else {

                    Task task = getTableView().getItems()
                            .get(getIndex()).task;

                    actionButton.setText(getActionText(task));
                    actionButton.setDisable(task.isDone());

                    setGraphic(actionButton);
                }
            }
        });

        taskTable.getColumns().addAll(
                numberColumn,
                nameColumn,
                lengthColumn,
                difficultyColumn,
                availabilityColumn,
                statusColumn,
                employeeColumn,
                actionColumn
        );

        VBox tablePanel = new VBox(10, titleRow, taskTable);

        tablePanel.setPadding(new Insets(14));

        tablePanel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #DCE2EA;" +
                "-fx-border-radius: 10px;"
        );

        return tablePanel;
    }

    // Sample employees and tasks for testing

    private void createSampleData() {

        Employee alice = new Employee("Alice", 101, 3, false, false) {};
		Employee tyler = new Employee("Tyler", 102, 2, false, false) {};
		Employee grace = new Employee("Grace", 103, 2, false, false) {};
		Employee olivia = new Employee("Olivia", 104, 4, false, false) {};
		Employee ethan = new Employee("Ethan", 105, 1, false, false) {};
		Employee mia = new Employee("Mia", 106, 3, false, false) {};

        employees.add(alice);
        employees.add(tyler);
        employees.add(grace);
        employees.add(olivia);
        employees.add(ethan);
        employees.add(mia);

        TaskImp takeOutTrash =
                addSampleTask("Take out trash", 15, 3);

        addSampleTask("Wash dishes", 30, 2);

        TaskImp wipeCountertops =
                addSampleTask("Wipe countertops", 10, 2);

        TaskImp countMoney =
                addSampleTask("Count money", 5, 1);

        TaskImp sweepDiningRoom =
                addSampleTask("Sweep dining room", 20, 2);

        TaskImp restockNapkins =
                addSampleTask("Restock napkins", 10, 1);

        addSampleTask("Clean windows", 25, 3);
        addSampleTask("Mop kitchen", 25, 4);

        TaskImp prepareVegetables =
                addSampleTask("Prepare vegetables", 20, 3);

        addSampleTask("Check refrigerator", 8, 1);

        // Six tasks are assigned; four remain unassigned.

        takeOutTrash.assignEmployee(alice);

        wipeCountertops.assignEmployee(tyler);
        wipeCountertops.doTask(tyler);

        countMoney.assignEmployee(grace);
        countMoney.doTask(grace);

        sweepDiningRoom.assignEmployee(olivia);

        restockNapkins.assignEmployee(ethan);

        prepareVegetables.assignEmployee(mia);
    }

    private TaskImp addSampleTask(
            String name,
            int length,
            int difficulty) {

        TaskImp task = new TaskImp(
                name,
                length,
                difficulty,
                false,
                false
        );

        taskManager.addTask(task);

        taskNumbers.put(task, taskNumbers.size() + 1);

        return task;
    }

    // Search for a task by name

    private Task findTaskByName(String name) {

        for (Task task : taskManager.getTasks()) {

            if (task.getName().equalsIgnoreCase(name)) {
                return task;
            }
        }

        return null;
    }

    // Clear all filters

    private void clearFilters() {

        searchField.clear();

        lengthFilter.setValue("All lengths");
        difficultyFilter.setValue("All levels");
        availabilityFilter.setValue("All");
        statusFilter.setValue("All statuses");

        refreshTable();
    }

    // Refresh dashboard information

    private void refreshDashboard() {

        refreshSummaryCards();
        refreshTable();
    }

    // Update summary card numbers

    private void refreshSummaryCards() {

        int total = 0;
        int assigned = 0;
        int unassigned = 0;

        for (Task task : taskManager.getTasks()) {

            total++;

            if (!task.isDone() && task.isAssigned()) {
                assigned++;
            }
            else if (!task.isDone() && !task.isAssigned()) {
                unassigned++;
            }
        }

        totalTasksValue.setText(String.valueOf(total));
        assignedTasksValue.setText(String.valueOf(assigned));
        unassignedTasksValue.setText(String.valueOf(unassigned));
    }

    // Update visible table rows

    private void refreshTable() {

        if (taskTable == null) {
            return;
        }

        visibleRows.clear();

        String searchText = searchField.getText() == null
                ? "" : searchField.getText().trim().toLowerCase();

        for (Task task : taskManager.getTasks()) {

            if (matchesFilters(task, searchText)) {

                Integer number = taskNumbers.get(task);

                visibleRows.add(
                        new TaskRow(task, number == null ? 0 : number)
                );
            }
        }

        taskTable.refresh();

        refreshSummaryCards();
    }

    // Check whether a task matches the selected filters

    private boolean matchesFilters(Task task, String searchText) {

        if (!searchText.isEmpty()) {

            Integer number = taskNumbers.get(task);

            boolean nameMatches =
                    task.getName().toLowerCase().contains(searchText);

            boolean numberMatches =
                    number != null
                    && String.valueOf(number).contains(searchText);

            if (!nameMatches && !numberMatches) {
                return false;
            }
        }

        if (!lengthFilter.getValue().equals("All lengths")
                && task.getLength()
                != Integer.parseInt(lengthFilter.getValue())) {

            return false;
        }

        if (!difficultyFilter.getValue().equals("All levels")
                && task.getDifficulty()
                != Integer.parseInt(difficultyFilter.getValue())) {

            return false;
        }

        if (availabilityFilter.getValue().equals("Assigned")
                && !task.isAssigned()) {

            return false;
        }

        if (availabilityFilter.getValue().equals("Unassigned")
                && task.isAssigned()) {

            return false;
        }

        String selectedStatus = statusFilter.getValue();

        if (!selectedStatus.equals("All statuses")
                && !selectedStatus.equals(getStatus(task))) {

            return false;
        }

        return true;
    }

    // Determine task availability

    private String getAvailability(Task task) {

        return task.isAssigned()
                ? "Assigned"
                : "Unassigned";
    }

    // Determine task completion status

    private String getStatus(Task task) {

        if (task.isDone()) {
            return "Completed";
        }

        List<Employee> assignedEmployees =
                task.getAssignedEmployees();

        for (Employee employee : assignedEmployees) {

            if (employee.isWorking()) {
                return "In Progress";
            }
        }

        return "Not Started";
    }

    // Get employee names assigned to a task

    private String getEmployeeNames(Task task) {

        List<Employee> assignedEmployees =
                task.getAssignedEmployees();

        if (assignedEmployees.isEmpty()) {
            return "-";
        }

        StringBuilder names = new StringBuilder();

        for (Employee employee : assignedEmployees) {

            if (names.length() > 0) {
                names.append(", ");
            }

            names.append(employee.getName());
        }

        return names.toString();
    }

    // Determine which quick action the task needs

    private String getActionText(Task task) {

        if (task.isDone()) {
            return "Completed";
        }

        if (!task.isAssigned()) {
            return "Assign";
        }

        if (getStatus(task).equals("In Progress")) {
            return "Complete";
        }

        return "Start";
    }

    // Handle Assign, Start, and Complete button actions

    private void handleTaskAction(Task task) {

        if (task.isDone()) {
            return;
        }

        if (!task.isAssigned()) {

            Employee availableEmployee = findAvailableEmployee();

            if (availableEmployee == null) {

                showMessage(
                        "No available employees",
                        "All employees currently have a task assigned."
                );

                return;
            }

            task.assignEmployee(availableEmployee);

        } else if (getStatus(task).equals("In Progress")) {

            task.completeTask();

        } else {

            List<Employee> assignedEmployees =
                    task.getAssignedEmployees();

            if (assignedEmployees.isEmpty()) {

                showMessage(
                        "Employee not found",
                        "This task is marked as assigned, but no employee is linked to it."
                );

                return;
            }

            task.doTask(assignedEmployees.get(0));
        }

        refreshDashboard();
    }

    // Find an employee who is not already assigned or working

    private Employee findAvailableEmployee() {

        for (Employee employee : employees) {

            if (!employee.isWorking()
                    && !employee.isAssignedTask()) {

                return employee;
            }
        }

        return null;
    }

    // Display information messages

    private void showMessage(String title, String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // Stores a task and its displayed task number

    private static class TaskRow {

        private final Task task;
        private final int number;

        private TaskRow(Task task, int number) {

            this.task = task;
            this.number = number;
        }
    }

    public static void main(String[] args) {

        launch(args);
    }
}

