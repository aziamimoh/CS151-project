package com.example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

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

    public TaskDashboard(TaskManager taskManager, List<Employee> employees) {
        if (taskManager == null) {
            throw new IllegalArgumentException("TaskManager cannot be null");
        }

        if (employees == null) {
            throw new IllegalArgumentException("Employees cannot be null");
        }

        this.taskManager = taskManager;
        this.employees = employees;
    }

    @Override
    public void start(Stage stage) {
        visibleRows = FXCollections.observableArrayList();
        initializeTaskNumbers();

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

    // dashboard title

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

    // summary cards

    private HBox createSummaryCards() {
        totalTasksValue = createMetricValue();
        assignedTasksValue = createMetricValue();
        unassignedTasksValue = createMetricValue();

        VBox totalCard = createSummaryCard(
                "TOTAL TASKS", totalTasksValue, "#DFF1E7", "#246443");

        VBox assignedCard = createSummaryCard(
                "ASSIGNED", assignedTasksValue, "#E2EEFC", "#244A78");

        VBox unassignedCard = createSummaryCard(
                "UNASSIGNED", unassignedTasksValue, "#EEE5FA", "#5B3A84");

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
    
    // search and filter section

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

		updateLengthFilter();

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
    
    // task table

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
        
        Button addTaskButton = new Button("+ Add Task");
		Button removeTaskButton = new Button("Remove Selected");

		addTaskButton.setStyle(
				"-fx-background-color: #16a34a;" +
				"-fx-text-fill: white;" +
				"-fx-font-weight: bold;" +
				"-fx-background-radius: 6px;" +
				"-fx-padding: 6 12 6 12;"
		);

		removeTaskButton.setOnAction(event -> removeSelectedTask());
		addTaskButton.setOnAction(event -> addTask());

		HBox titleText = new HBox(10, tableTitle, tableHint);
		titleText.setAlignment(Pos.CENTER_LEFT);

		HBox titleRow = new HBox(10, titleText, addTaskButton, removeTaskButton);
		titleRow.setAlignment(Pos.CENTER_LEFT);

		HBox.setHgrow(titleText, Priority.ALWAYS);

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

        // task number column

        TableColumn<TaskRow, String> numberColumn =
                new TableColumn<>("#");

        numberColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        String.valueOf(cell.getValue().number)
                )
        );

        numberColumn.setPrefWidth(45);

        // task name column

        TableColumn<TaskRow, String> nameColumn =
                new TableColumn<>("TASK NAME");

        nameColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().task.getName()
                )
        );

        nameColumn.setPrefWidth(220);

        // length column

        TableColumn<TaskRow, String> lengthColumn =
                new TableColumn<>("LENGTH");

        lengthColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().task.getLength() + " min"
                )
        );

        lengthColumn.setPrefWidth(90);

        // difficulty column

        TableColumn<TaskRow, String> difficultyColumn =
                new TableColumn<>("DIFFICULTY");

        difficultyColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        cell.getValue().task.getDifficulty() + " / 5"
                )
        );

        difficultyColumn.setPrefWidth(95);

        // availability column

        TableColumn<TaskRow, String> availabilityColumn =
                new TableColumn<>("AVAILABILITY");

        availabilityColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        getAvailability(cell.getValue().task)
                )
        );

        availabilityColumn.setPrefWidth(110);

        // status column

        TableColumn<TaskRow, String> statusColumn =
                new TableColumn<>("STATUS");

        statusColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        getStatus(cell.getValue().task)
                )
        );

        statusColumn.setPrefWidth(115);

        // employee column

        TableColumn<TaskRow, String> employeeColumn =
                new TableColumn<>("EMPLOYEE");

        employeeColumn.setCellValueFactory(cell ->
                new ReadOnlyStringWrapper(
                        getEmployeeNames(cell.getValue().task)
                )
        );

        employeeColumn.setPrefWidth(130);

        // quick action column

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

        VBox panel = new VBox(10, titleRow, taskTable);

        panel.setPadding(new Insets(14));

        panel.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10px;" +
                "-fx-border-color: #DCE2EA;" +
                "-fx-border-radius: 10px;"
        );

        return panel;
    }
    
    // clear all filters
    private void clearFilters() {
        searchField.clear();

        lengthFilter.setValue("All lengths");
        difficultyFilter.setValue("All levels");
        availabilityFilter.setValue("All");
        statusFilter.setValue("All statuses");

        refreshTable();
    }
    
    // update the length dropdown using the lengths currently in TaskManager

	private void updateLengthFilter() {
		if (lengthFilter == null) {
			return;
		}

		String selectedLength = lengthFilter.getValue();
		Set<Integer> lengths = new TreeSet<>();
	
		for (Task task : taskManager.getTasks()) {
			lengths.add(task.getLength());
		}

		lengthFilter.getItems().clear();
		lengthFilter.getItems().add("All lengths");
	
		for (Integer length : lengths) {
			lengthFilter.getItems().add(String.valueOf(length));
		}

		if (selectedLength != null && lengthFilter.getItems().contains(selectedLength)) {
			lengthFilter.setValue(selectedLength);
		} else {
			lengthFilter.setValue("All lengths");
		}
	}


	// Open a form to create a new task
	private void addTask() {
		Dialog<Task> dialog = new Dialog<>();
		dialog.setTitle("Add Task");
		dialog.setHeaderText("Enter the new task information.");

		ButtonType addButtonType = new ButtonType("Add Task",
				javafx.scene.control.ButtonBar.ButtonData.OK_DONE);

		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		TextField nameField = new TextField();
		nameField.setPromptText("Task name");

		TextField lengthField = new TextField();
		lengthField.setPromptText("Length in minutes");

		TextField difficultyField = new TextField();
		difficultyField.setPromptText("Difficulty (1-5)");

		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);

		form.add(new Label("Task name:"), 0, 0);
		form.add(nameField, 1, 0);

		form.add(new Label("Length:"), 0, 1);
		form.add(lengthField, 1, 1);

		form.add(new Label("Difficulty:"), 0, 2);
		form.add(difficultyField, 1, 2);

		dialog.getDialogPane().setContent(form);

		dialog.setResultConverter(button -> {
			if (button == addButtonType) {
				String name = nameField.getText().trim();

				try {
					int length = Integer.parseInt(lengthField.getText().trim());
					int difficulty = Integer.parseInt(difficultyField.getText().trim());

					if (name.isEmpty() || length <= 0 || difficulty < 1 || difficulty > 5) {
						showMessage("Invalid task",
								"Enter a name, a positive length, and a difficulty from 1 to 5.");
						return null;
					}

					return new TaskImp(name, length, difficulty);
	
				} catch (NumberFormatException exception) {
					showMessage("Invalid task",
							"Length and difficulty must be whole numbers.");
				}
			}

			return null;
		});

		dialog.showAndWait().ifPresent(task -> {
			taskManager.addTask(task);
			refreshDashboard();
		});
	}


	// Remove the selected task if it is not assigned
	private void removeSelectedTask() {
		TaskRow selectedRow = taskTable.getSelectionModel().getSelectedItem();

		if (selectedRow == null) {
			showMessage("No task selected",
					"Select a task in the table before removing it.");
			return;
		}

		Task selectedTask = selectedRow.task;

		if (selectedTask.isAssigned()) {
			showMessage("Task is assigned",
					"Unassign or complete this task before removing it.");
			return;
		}

		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Remove Task");
		confirmation.setHeaderText("Remove " + selectedTask.getName() + "?");
		confirmation.setContentText(
				"This task will be removed from the restaurant task list.");

		confirmation.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				taskManager.removeTask(selectedTask);
				refreshDashboard();
			}
		});
	}

    private void initializeTaskNumbers() {
        taskNumbers.clear();

        int number = 1;

        for (Task task : taskManager.getTasks()) {
            taskNumbers.put(task, number);
            number++;
        }
    }

    // refresh dashboard information
    private void refreshDashboard() {
		initializeTaskNumbers();
		updateLengthFilter();
        refreshSummaryCards();
        refreshTable();
    }

    // update summary card numbers
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

    // update visible table rows

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

    // check whether a task matches the selected filters

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
    
    // determine task availability

    private String getAvailability(Task task) {
        return task.isAssigned()
                ? "Assigned"
                : "Unassigned";
    }

    // determine task completion status

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

    // get employee names assigned to a task

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

    // determine which quick action the task needs

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
    
    // handle assign, start, and complete button actions

    private void handleTaskAction(Task task) {
        if (task.isDone()) {
            return;
        }

        if (!task.isAssigned()) {

            Employee availableEmployee = findAvailableEmployee(task);

            if (availableEmployee == null) {

                showMessage(
                        "No available employees",
                        "No available employee has the required skill level for this task."
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

    // finds  an available employee who is on the schedule
		private Employee findAvailableEmployee(Task task) {
			for (Employee employee : employees) {

				if (employee.isOnSchedule()
						&& !employee.isWorking()
						&& !employee.hasTasks()
						&& employee.getSkillLevel() >= task.getDifficulty()) {

					return employee;
				}
			}

		return null;
	}

    // display information messages
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);

        alert.showAndWait();
    }

    // stores a task and its displayed task number
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
