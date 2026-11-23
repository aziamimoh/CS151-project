package com.example;

import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainDashboard extends Application {
    private static final int LOW_STOCK_LIMIT = 10;
    private static final int MAX_ROWS = 3;

    private Inventory inventory = new Inventory();
    private TaskManager taskManager = new TaskManager();
    private final Label itemsValue = new Label();
    private final Label lowStockValue = new Label();
    private final Label openTasksValue = new Label();
    private final Label unassignedValue = new Label();
    private final VBox lowStockRows = new VBox();
    private final VBox taskRows = new VBox();

    @Override
    public void start(Stage stage) {
        Label title = new Label("DASHBOARD");
        title.setStyle("-fx-font-size: 34px; -fx-font-weight: bold; -fx-text-fill: #121820;");

        HBox cards = new HBox(18,
            summaryCard("TOTAL DISTINCT ITEMS", itemsValue, "#d7e7fb", "#174e8c"),
            summaryCard("LOW STOCK ALERTS", lowStockValue, "#fbe6c8", "#8a4a00"),
            summaryCard("OPEN TASKS", openTasksValue, "#d7f5e5", "#147348"),
            summaryCard("UNASSIGNED TASKS", unassignedValue, "#e6dcf7", "#4b2a83")
        );
        for (Node node : cards.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        Label goTo = new Label("GO TO");
        goTo.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");

        Button inventoryButton = navButton("Open Inventory Dashboard", "#22476e");
        inventoryButton.setOnAction(event -> comingSoon("Inventory Dashboard"));
        Button employeeButton = navButton("Open Employee Dashboard", "#5a1d22");
        employeeButton.setOnAction(event -> new EmployeeDashboard().start(new Stage()));
        Button taskButton = navButton("Open Task Manager", "#04600d");
        taskButton.setOnAction(event -> comingSoon("Task Manager"));

        HBox nav = new HBox(18,
            navCard("Inventory", inventoryButton),
            navCard("Employees", employeeButton),
            navCard("Tasks", taskButton)
        );
        for (Node node : nav.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);

        HBox lists = new HBox(18,
            listPanel("LOW STOCK", lowStockRows, "Inventory Dashboard"),
            listPanel("OPEN TASKS", taskRows, "Task Manager")
        );
        for (Node node : lists.getChildren()) HBox.setHgrow(node, Priority.ALWAYS);
        VBox.setVgrow(lists, Priority.ALWAYS);

        VBox root = new VBox(18, title, cards, goTo, nav, lists);
        root.setPadding(new Insets(24));
        root.setStyle("-fx-background-color: #f5f8fc; -fx-font-family: 'Arial';");
        refreshCounts();
        refreshLists();

        stage.setTitle("Restaurant Simulator");
        stage.setScene(new Scene(root, 1200, 730));
        stage.show();
    }

    private static VBox summaryCard(String caption, Label value, String bg, String foreground) {
        Label heading = new Label(caption);
        heading.setStyle("-fx-font-size: 16px; -fx-text-fill: #202630;");
        value.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: " + foreground + ";");
        VBox box = new VBox(9, heading, value);
        box.setPadding(new Insets(17));
        box.setPrefWidth(270);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 9;"
            + " -fx-border-color: #abb8c7; -fx-border-radius: 9;");
        return box;
    }

    private static Button navButton(String text, String bg) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(42);
        button.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 7; -fx-cursor: hand;"
            + " -fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: bold;");
        return button;
    }

    private static VBox navCard(String caption, Button button) {
        Label heading = new Label(caption);
        heading.setStyle("-fx-font-size: 17px; -fx-font-weight: bold;");
        VBox box = new VBox(18, heading, button);
        box.setPadding(new Insets(18));
        box.setPrefWidth(370);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setStyle(panelStyle());
        return box;
    }

    private VBox listPanel(String caption, VBox rows, String screen) {
        Label sectionTitle = new Label(caption);
        sectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 17px;");
        Hyperlink viewAll = new Hyperlink("View all");
        viewAll.setStyle("-fx-font-size: 15px; -fx-text-fill: #174e8c; -fx-underline: true;");
        viewAll.setOnAction(event -> comingSoon(screen));
        HBox sectionHeader = new HBox(15, sectionTitle, spacer(), viewAll);
        sectionHeader.setAlignment(Pos.CENTER_LEFT);
        sectionHeader.setPadding(new Insets(10, 18, 8, 18));
        VBox panel = new VBox(sectionHeader, rows);
        panel.setPrefWidth(560);
        panel.setMaxWidth(Double.MAX_VALUE);
        panel.setStyle(panelStyle());
        return panel;
    }

    private static HBox lowStockRow(String name, int quantity) {
        Label quantityLabel = new Label(Integer.toString(quantity));
        quantityLabel.setStyle("-fx-text-fill: #5a6570; -fx-font-size: 15px;");
        return listRow(nameLabel(name), quantityLabel, badge("Low Stock", "#fbe6c8", "#8a4a00"));
    }

    private static HBox taskRow(Task task) {
        Label details = new Label("Difficulty " + task.getDifficulty() + " · Length " + task.getLength());
        details.setStyle("-fx-text-fill: #5a6570; -fx-font-size: 13px;");
        VBox info = new VBox(2, nameLabel(task.getName()), details);
        return listRow(info, task.isAssigned()
            ? badge("Assigned", "#d7e7fb", "#174e8c")
            : badge("Unassigned", "#e6dcf7", "#4b2a83"));
    }

    private static HBox listRow(Node left, Node... right) {
        HBox row = new HBox(12, left, spacer());
        row.getChildren().addAll(right);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(9, 18, 9, 18));
        row.setStyle("-fx-border-color: #dde3ea transparent transparent transparent;");
        return row;
    }

    private static Label nameLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px;");
        return label;
    }

    private static Label badge(String text, String bg, String foreground) {
        Label label = new Label(text);
        label.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 9; -fx-padding: 3 10;"
            + " -fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + foreground + ";");
        return label;
    }

    private static Region spacer() {
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        return region;
    }

    private static String panelStyle() {
        return "-fx-background-color: white; -fx-background-radius: 9;"
            + " -fx-border-color: #bcc5ce; -fx-border-radius: 9;";
    }

    /** Provide the Inventory and TaskManager this dashboard should display. */
    public void setData(Inventory newInventory, TaskManager newTaskManager) {
        inventory = newInventory;
        taskManager = newTaskManager;
        refreshCounts();
        refreshLists();
    }

    private void refreshCounts() {
        itemsValue.setText(Integer.toString(inventory.size()));
        lowStockValue.setText(Integer.toString(inventory.getLowStockItems(LOW_STOCK_LIMIT).size()));
        openTasksValue.setText(Integer.toString(taskManager.searchByDone(false).size()));
        unassignedValue.setText(Integer.toString(taskManager.getAvailableTasks().size()));
    }

    private void refreshLists() {
        lowStockRows.getChildren().clear();
        List<Item> lowStock = inventory.getLowStockItems(LOW_STOCK_LIMIT);
        for (int i = 0; i < lowStock.size() && i < MAX_ROWS; i++) {
            Item item = lowStock.get(i);
            lowStockRows.getChildren().add(lowStockRow(item.getName(), inventory.getQuantity(item)));
        }

        taskRows.getChildren().clear();
        List<Task> openTasks = taskManager.searchByDone(false);
        for (int i = 0; i < openTasks.size() && i < MAX_ROWS; i++) {
            taskRows.getChildren().add(taskRow(openTasks.get(i)));
        }
    }

    private void comingSoon(String screen) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setHeaderText(screen);
        info.setContentText("This screen is coming soon.");
        info.showAndWait();
    }
}
