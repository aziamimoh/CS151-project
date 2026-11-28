package com.example;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.layout.GridPane;

public class EmployeeDashboard extends Application {
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private final FilteredList<Employee> filtered = new FilteredList<>(employees, e -> true);
    private final Label totalValue = new Label();
    private final Label scheduledValue = new Label();
    private final Label unscheduledValue = new Label();
    private final Label rowCount = new Label();
    private List<Employee> sourceEmployees;
	private TableView<Employee> table;

    public EmployeeDashboard(List<Employee> employees) {
		if (employees == null) {
			throw new IllegalArgumentException("Employees cannot be null");
		}
    
		sourceEmployees = employees;
		this.employees.setAll(employees);
    }

    @Override
    public void start(Stage stage) {

        Label title = new Label("EMPLOYEES");
      
		HBox titleRow = new HBox(title);
		titleRow.setAlignment(Pos.CENTER_LEFT);
        title.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #121820;");

        HBox cards = new HBox(18,
            summaryCard("TOTAL EMPLOYEES", totalValue, "#d7e7fb", "#174e8c"),
            summaryCard("ON THE SCHEDULE", scheduledValue, "#d7f5e5", "#147348"),
            summaryCard("NOT SCHEDULED", unscheduledValue, "#f8dad9", "#a32c35")
        );
        for (javafx.scene.Node node : cards.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        TextField search = new TextField();
        search.setPromptText("Search employee name or ID...");
        search.setPrefHeight(39);
        ComboBox<String> skillFilter = new ComboBox<>(FXCollections.observableArrayList(
            "All Skill Levels", "Level 1", "Level 2", "Level 3", "Level 4", "Level 5"));
        skillFilter.setValue("All Skill Levels");
        skillFilter.setMaxWidth(Double.MAX_VALUE);
        skillFilter.setPrefHeight(39);
        ComboBox<String> scheduleFilter = new ComboBox<>(FXCollections.observableArrayList(
            "All Employees", "Scheduled", "Not Scheduled"));
        scheduleFilter.setValue("All Employees");
        scheduleFilter.setMaxWidth(Double.MAX_VALUE);
        scheduleFilter.setPrefHeight(39);

        Runnable refreshFilter = () -> {
            String query = search.getText().trim().toLowerCase();
            String selectedSkill = skillFilter.getValue();
            String selectedSchedule = scheduleFilter.getValue();
            filtered.setPredicate(e ->
                (query.isEmpty() || e.getName().toLowerCase().contains(query)
                    || Integer.toString(e.getId()).contains(query))
                && (selectedSkill.equals("All Skill Levels")
                    || selectedSkill.equals("Level " + e.getSkillLevel()))
                && (selectedSchedule.equals("All Employees")
                    || (selectedSchedule.equals("Scheduled") == e.isOnSchedule()))
            );
            rowCount.setText(filtered.size() + " employees");
        };
        search.textProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());
        skillFilter.valueProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());
        scheduleFilter.valueProperty().addListener((obs, oldValue, newValue) -> refreshFilter.run());

        VBox searchGroup = labeledControl("Search Employee", search);
        VBox skillGroup = labeledControl("Skill Level", skillFilter);
        VBox scheduleGroup = labeledControl("Schedule Status", scheduleFilter);
        HBox.setHgrow(searchGroup, Priority.ALWAYS);
        HBox.setHgrow(skillGroup, Priority.ALWAYS);
        HBox.setHgrow(scheduleGroup, Priority.ALWAYS);
        searchGroup.setMaxWidth(Double.MAX_VALUE);
        skillGroup.setMaxWidth(Double.MAX_VALUE);
        scheduleGroup.setMaxWidth(Double.MAX_VALUE);
        HBox filters = new HBox(18, searchGroup, skillGroup, scheduleGroup);
        filters.setPadding(new Insets(18));
        filters.setStyle(panelStyle());

        table = new TableView<>(filtered);
        table.setEditable(true);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPlaceholder(new Label("No employees match the current filters."));
        table.setFixedCellSize(52);

        TableColumn<Employee, String> nameCol = new TableColumn<>("NAME");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.setPrefWidth(250);
        TableColumn<Employee, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getId()));
        idCol.setPrefWidth(100);
        TableColumn<Employee, Number> skillCol = new TableColumn<>("SKILL LEVEL");
        skillCol.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getSkillLevel()));
        skillCol.setPrefWidth(150);
        TableColumn<Employee, Boolean> scheduleCol = new TableColumn<>("ON THE SCHEDULE");
        scheduleCol.setCellValueFactory(data -> {
            Employee employee = data.getValue();
            BooleanProperty scheduled = new SimpleBooleanProperty(employee.isOnSchedule());
            scheduled.addListener((obs, oldValue, newValue) -> {
                employee.setOnSchedule(newValue);
                refreshCounts();
                filtered.setPredicate(filtered.getPredicate());
                rowCount.setText(filtered.size() + " employees");
            });
            return scheduled;
        });
        scheduleCol.setCellFactory(CheckBoxTableCell.forTableColumn(scheduleCol));
        scheduleCol.setPrefWidth(190);
        TableColumn<Employee, Void> actionCol = new TableColumn<>("QUICK ACTIONS");
		actionCol.setPrefWidth(190);

		actionCol.setCellFactory(column -> new TableCell<Employee, Void>() {
			
			private final Button editButton = new Button("Edit");
			private final Button removeButton = new Button("Remove");
			private final HBox buttons = new HBox(7, editButton, removeButton);
		{
				editButton.setOnAction(event -> {
					Employee employee = getTableView().getItems().get(getIndex());
					editEmployee(employee);
				});

				removeButton.setOnAction(event -> {
					Employee employee = getTableView().getItems().get(getIndex());
					removeEmployee(employee);
				});

				buttons.setAlignment(Pos.CENTER);
			}

			@Override
			protected void updateItem(Void item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(empty ? null : buttons);
				setAlignment(Pos.CENTER);
			}
		});
       
        table.getColumns().addAll(nameCol, idCol, skillCol, scheduleCol, actionCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        Label sectionTitle = new Label("EMPLOYEE LIST");
		sectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");

		rowCount.setStyle("-fx-text-fill: #5a6570; -fx-font-size: 15px;");

		Button addEmployeeButton = new Button("+ Add Employee");

		addEmployeeButton.setStyle(
				"-fx-background-color: #16a34a;" +
				"-fx-text-fill: white;" +
				"-fx-font-weight: bold;" +
				"-fx-background-radius: 6px;" +
				"-fx-padding: 6 12 6 12;"
		);

		addEmployeeButton.setOnAction(event -> addEmployee());

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox sectionHeader = new HBox(
				15,
				sectionTitle,
				rowCount,
				spacer,
				addEmployeeButton
		);

		sectionHeader.setAlignment(Pos.CENTER_LEFT);
		sectionHeader.setPadding(new Insets(15, 18, 13, 18));
       
        VBox listPanel = new VBox(sectionHeader, table);
        listPanel.setStyle(panelStyle());
        VBox.setVgrow(listPanel, Priority.ALWAYS);

        VBox root = new VBox(18, titleRow, cards, filters, listPanel);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f5f8fc; -fx-font-family: 'Arial';");
        refreshCounts();
        refreshFilter.run();

        stage.setTitle("Employees");
        stage.setScene(new Scene(root, 1200, 730));
        stage.show();
    }

    private static VBox summaryCard(String caption, Label value, String bg, String foreground) {
        Label heading = new Label(caption);
        heading.setStyle("-fx-font-size: 16px; -fx-text-fill: #202630;");
        value.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: " + foreground + ";");
        VBox box = new VBox(9, heading, value);
        box.setPadding(new Insets(17));
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 9;"
            + " -fx-border-color: #abb8c7; -fx-border-radius: 9;");
        return box;
    }

    private static VBox labeledControl(String title, Control control) {
        Label label = new Label(title);
        label.setStyle("-fx-font-size: 15px;");
        control.setMaxWidth(Double.MAX_VALUE);
        return new VBox(8, label, control);
    }

    private static String panelStyle() {
        return "-fx-background-color: white; -fx-background-radius: 9;"
            + " -fx-border-color: #bcc5ce; -fx-border-radius: 9;";
    }

    public void setEmployees(java.util.Collection<? extends Employee> newEmployees) {
        employees.setAll(newEmployees);
        refreshCounts();
    }
    
    // add a new employee
	private void addEmployee() {
		EmployeeDetails details = showEmployeeForm(null, "Add Employee");

		if (details == null) {
			return;
		}
	
		if (employeeIdExists(details.id, null)) {
			showMessage("Duplicate ID",
					"An employee with that ID already exists.");
			return;
		}

		Employee employee = new EmployeeImp(
				details.name,
				details.id,
				details.skillLevel,
				details.working
		);

		sourceEmployees.add(employee);
		employees.add(employee);

		refreshDashboard();
	}

	// edit an existing employee
	private void editEmployee(Employee employee) {
		EmployeeDetails details = showEmployeeForm(employee, "Edit Employee");

		if (details == null) {
			return;
		}

		if (employeeIdExists(details.id, employee)) {
			showMessage("Duplicate ID",
					"Another employee already uses that ID.");
			return;
		}

		employee.setName(details.name);
		employee.setId(details.id);
		employee.setSkillLevel(details.skillLevel);
		employee.setOnSchedule(details.working);

		refreshDashboard();
	}

	// display the employee form
	private EmployeeDetails showEmployeeForm(Employee employee, String title) {
		Dialog<EmployeeDetails> dialog = new Dialog<>();

		dialog.setTitle(title);
		dialog.setHeaderText("Enter the employee information.");

		ButtonType saveButton = new ButtonType(
				"Save", ButtonBar.ButtonData.OK_DONE);

		dialog.getDialogPane().getButtonTypes().addAll(
				saveButton, ButtonType.CANCEL);

		TextField nameField = new TextField(
				employee == null ? "" : employee.getName());

		TextField idField = new TextField(
				employee == null ? "" : String.valueOf(employee.getId()));

		TextField skillField = new TextField(
				employee == null ? "" : String.valueOf(employee.getSkillLevel()));

		CheckBox workingBox = new CheckBox("On the schedule");
		workingBox.setSelected(employee != null && employee.isOnSchedule());
	
		GridPane form = new GridPane();
		form.setHgap(10);
		form.setVgap(10);

		form.add(new Label("Name:"), 0, 0);
		form.add(nameField, 1, 0);

		form.add(new Label("ID:"), 0, 1);
		form.add(idField, 1, 1);

		form.add(new Label("Skill level (1-5):"), 0, 2);
		form.add(skillField, 1, 2);

		form.add(workingBox, 1, 3);

		dialog.getDialogPane().setContent(form);

		dialog.setResultConverter(button -> {
			if (button != saveButton) {
				return null;
			}

			String name = nameField.getText().trim();

			try {
				int id = Integer.parseInt(idField.getText().trim());
				int skillLevel = Integer.parseInt(skillField.getText().trim());

				if (name.isEmpty() || id < 0 || skillLevel < 1 || skillLevel > 5) {
					showMessage("Invalid employee",
							"Enter a name, a non-negative ID, and a skill level from 1 to 5.");
					return null;
				}

				return new EmployeeDetails(
						name, id, skillLevel, workingBox.isSelected());
	
			} catch (NumberFormatException exception) {
				showMessage("Invalid employee",
						"ID and skill level must be whole numbers.");
				return null;
			}
		});

		return dialog.showAndWait().orElse(null);
	}

	// check whether an employee ID is already being used
	private boolean employeeIdExists(int id, Employee employeeBeingEdited) {
		for (Employee currentEmployee : employees) {
			if (currentEmployee.getId() == id
					&& currentEmployee != employeeBeingEdited) {
				return true;
			}
		}

		return false;
	}

	// remove an employee
	private void removeEmployee(Employee employee) {
		if (employee.hasTasks()) {
			showMessage("Employee has assigned tasks",
					"Reassign or complete this employee's tasks before removing them.");
			return;
		}

		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
		confirmation.setTitle("Remove Employee");
		confirmation.setHeaderText("Remove " + employee.getName() + "?");
		confirmation.setContentText(
				"This employee will be removed from the employee list.");

		confirmation.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				sourceEmployees.remove(employee);
				employees.remove(employee);
				refreshDashboard();
			}
		});
	}


	// refresh the employee dashboard	
	private void refreshDashboard() {
		refreshCounts();

		if (table != null) {
			table.refresh();
			filtered.setPredicate(filtered.getPredicate());
			rowCount.setText(filtered.size() + " employees");
		}
	}
	
	private void showMessage(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);

		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.setContentText(message);

		alert.showAndWait();
	}
	
    private void refreshCounts() {
        long scheduled = employees.stream().filter(Employee::isOnSchedule).count();
        totalValue.setText(Integer.toString(employees.size()));
        scheduledValue.setText(Long.toString(scheduled));
        unscheduledValue.setText(Long.toString(employees.size() - scheduled));
    }
	
		private static class EmployeeDetails {
		private final String name;
		private final int id;
		private final int skillLevel;
		private final boolean working;

		private EmployeeDetails(String name, int id, int skillLevel, boolean working) {
			this.name = name;
			this.id = id;
			this.skillLevel = skillLevel;
			this.working = working;
		}
	}
}
