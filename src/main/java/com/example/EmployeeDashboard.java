package com.example;

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

public class EmployeeDashboard extends Application {
    private final ObservableList<Employee> employees = FXCollections.observableArrayList();
    private final FilteredList<Employee> filtered = new FilteredList<>(employees, e -> true);
    private final Label totalValue = new Label();
    private final Label scheduledValue = new Label();
    private final Label unscheduledValue = new Label();
    private final Label rowCount = new Label();

    @Override
    public void start(Stage stage) {
        // Demo records: replace with your existing EmployeeImp objects.
        setEmployees(FXCollections.observableArrayList(
            new EmployeeImp("Alex Morgan", 101, 3, true),
            new EmployeeImp("Jordan Lee", 102, 2, true),
            new EmployeeImp("Taylor Smith", 103, 1, false),
            new EmployeeImp("Casey Brown", 104, 4, true),
            new EmployeeImp("Sam Rivera", 105, 2, false),
            new EmployeeImp("Jamie Chen", 106, 3, true),
            new EmployeeImp("Riley Davis", 107, 1, false)
        ));

        Label title = new Label("EMPLOYEES");
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
            "All Skill Levels", "Level 1", "Level 2", "Level 3", "Level 4"));
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
                    || (selectedSchedule.equals("Scheduled") == e.isWorking()))
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

        TableView<Employee> table = new TableView<>(filtered);
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
            BooleanProperty working = new SimpleBooleanProperty(employee.isWorking());
            working.addListener((obs, oldValue, newValue) -> {
                employee.setWorking(newValue);
                refreshCounts();
                filtered.setPredicate(filtered.getPredicate());
                rowCount.setText(filtered.size() + " employees");
            });
            return working;
        });
        scheduleCol.setCellFactory(CheckBoxTableCell.forTableColumn(scheduleCol));
        scheduleCol.setPrefWidth(190);
        TableColumn<Employee, Void> actionCol = new TableColumn<>("QUICK ACTIONS");
        actionCol.setPrefWidth(130);
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button edit = new Button("Edit");
            {
                edit.setStyle("-fx-background-color: #d7e7fb; -fx-border-color: #9db8d6;"
                    + "-fx-background-radius: 7; -fx-border-radius: 7; -fx-cursor: hand;");
                edit.setOnAction(event -> editEmployee(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : edit);
                setAlignment(Pos.CENTER);
            }
        });
        table.getColumns().addAll(nameCol, idCol, skillCol, scheduleCol, actionCol);
        VBox.setVgrow(table, Priority.ALWAYS);

        Label sectionTitle = new Label("EMPLOYEE LIST");
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");
        rowCount.setStyle("-fx-text-fill: #5a6570; -fx-font-size: 15px;");
        HBox sectionHeader = new HBox(15, sectionTitle, rowCount);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        sectionHeader.setPadding(new Insets(15, 18, 13, 18));
        VBox listPanel = new VBox(sectionHeader, table);
        listPanel.setStyle(panelStyle());
        VBox.setVgrow(listPanel, Priority.ALWAYS);

        VBox root = new VBox(18, title, cards, filters, listPanel);
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

    /** Provide your existing EmployeeImp instances; no UI-specific model needed. */
    public void setEmployees(java.util.Collection<? extends Employee> newEmployees) {
        employees.setAll(newEmployees);
        refreshCounts();
    }

    private void refreshCounts() {
        long scheduled = employees.stream().filter(Employee::isWorking).count();
        totalValue.setText(Integer.toString(employees.size()));
        scheduledValue.setText(Long.toString(scheduled));
        unscheduledValue.setText(Long.toString(employees.size() - scheduled));
    }

    private void editEmployee(Employee employee) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText("Edit " + employee.getName());
        info.setContentText("Connect this action to your employee editing form.");
        info.showAndWait();
}
}