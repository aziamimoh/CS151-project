package com.example;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.util.Set;
import java.util.TreeSet;

public class InventoryView {

    private final Inventory inventory;
    private final FilteredList<InventoryItem> filteredItems;

    // Top KPI labels
    private final Label totalDistinctLabel = new Label("0");
    private final Label lowStockLabel = new Label("0");
    private final Label outOfStockLabel = new Label("0");
    private final Label itemCountSubLabel = new Label("0 items");

    // Filter controls
    private final TextField searchField = new TextField();
    private final ComboBox<String> categoryBox = new ComboBox<>();
    private final TextField thresholdField = new TextField("10");
    private final ToggleButton lowStockToggle = new ToggleButton("OFF");

    private final TableView<InventoryItem> table = new TableView<>();

    public InventoryView(Inventory inventory) {
        if (inventory == null) {
            throw new IllegalArgumentException("Inventory cannot be null");
        }

        this.inventory = inventory;
        this.filteredItems =
                new FilteredList<>(inventory.getObservableItems(), item -> true);
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Inventory Dashboard");
        stage.setScene(new Scene(createView(), 1200, 730));
        stage.show();
    }
    
    public Parent createView() {
        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle(
                "-fx-background-color: #f4f6f9;" +
                "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
        );

        // Header
        Label title = new Label("INVENTORY");
        title.setStyle(
                "-fx-font-size: 26px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1a202c;"
        );

        // Dashboard sections
        HBox kpiRow = createKpiSection();
        HBox filterCard = createFilterSection();
        VBox tableCard = createTableSection();

        VBox.setVgrow(tableCard, Priority.ALWAYS);

        root.getChildren().addAll(
                title,
                kpiRow,
                filterCard,
                tableCard
        );

        setupListeners();
        updateCategories();
        refreshAll();

        return root;
    }

    // ---------------------------------------------------------
    // KPI SECTION
    // ---------------------------------------------------------

    private HBox createKpiSection() {

        VBox totalCard = createKpiCard(
                "TOTAL DISTINCT ITEMS",
                totalDistinctLabel,
                "#dbeafe",
                "#1e40af"
        );

        VBox lowCard = createKpiCard(
                "LOW STOCK ALERTS",
                lowStockLabel,
                "#fed7aa",
                "#b45309"
        );

        VBox outCard = createKpiCard(
                "OUT OF STOCK ITEMS",
                outOfStockLabel,
                "#fecaca",
                "#b91c1c"
        );

        HBox row = new HBox(16, totalCard, lowCard, outCard);

        HBox.setHgrow(totalCard, Priority.ALWAYS);
        HBox.setHgrow(lowCard, Priority.ALWAYS);
        HBox.setHgrow(outCard, Priority.ALWAYS);

        return row;
    }

    private VBox createKpiCard(
            String heading,
            Label valueLabel,
            String bgHex,
            String accentHex) {

        Label header = new Label(heading);

        header.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #334155;"
        );

        valueLabel.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: " + accentHex + ";"
        );

        VBox card = new VBox(6, header, valueLabel);

        card.setPadding(new Insets(14, 18, 14, 18));
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle(
                "-fx-background-color: " + bgHex + ";" +
                "-fx-border-color: " + accentHex + "55;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        return card;
    }

    // ---------------------------------------------------------
    // FILTER SECTION
    // ---------------------------------------------------------

    private HBox createFilterSection() {

        // Search
        Label searchLabel = new Label("Search Item Name");
        searchLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        searchField.setPromptText("e.g., Ground Beef, Cheese...");
        searchField.setPrefWidth(240);

        VBox searchBox = new VBox(
                6,
                searchLabel,
                searchField
        );

        // Category / Type
        Label categoryLabel = new Label("Category / Type");
        categoryLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        categoryBox.setPrefWidth(180);

        VBox categoryContainer = new VBox(
                6,
                categoryLabel,
                categoryBox
        );

        // Threshold
        Label thresholdLabel = new Label("Threshold");
        thresholdLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        thresholdField.setPrefWidth(70);
        thresholdField.setAlignment(Pos.CENTER);

        VBox thresholdBox = new VBox(
                6,
                thresholdLabel,
                thresholdField
        );

        // Low Stock Toggle
        Label toggleLabel = new Label("Show Low Stock");
        toggleLabel.setStyle(
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1e293b;"
        );

        lowStockToggle.setPrefWidth(70);
        updateToggleStyle();

        VBox toggleBox = new VBox(
                6,
                toggleLabel,
                lowStockToggle
        );

        HBox filterCard = new HBox(
                20,
                searchBox,
                categoryContainer,
                thresholdBox,
                toggleBox
        );

        filterCard.setAlignment(Pos.CENTER_LEFT);
        filterCard.setPadding(new Insets(14, 18, 14, 18));

        filterCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        return filterCard;
    }

    // ---------------------------------------------------------
    // TABLE SECTION
    // ---------------------------------------------------------

    @SuppressWarnings("unchecked")
    private VBox createTableSection() {

        Label tableTitle = new Label("INVENTORY ITEMS");

        tableTitle.setStyle(
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #0f172a;"
        );

        itemCountSubLabel.setStyle(
                "-fx-text-fill: #64748b;"
        );

        HBox leftHeader = new HBox(
                10,
                tableTitle,
                itemCountSubLabel
        );

        leftHeader.setAlignment(Pos.CENTER_LEFT);
        // Add Item button
         Button addItemButton = new Button("+ Add Item");

         addItemButton.setStyle(
        "-fx-background-color: #16a34a;" +
        "-fx-text-fill: white;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 6;"
        );

        addItemButton.setOnAction(event -> showAddItemDialog());
        // Status legend
        Label inStockLegend = createBadgeLabel(
                "● In Stock",
                "#dcfce7",
                "#166534"
        );

        Label lowStockLegend = createBadgeLabel(
                "● Low Stock",
                "#fef3c7",
                "#92400e"
        );

        Label outStockLegend = createBadgeLabel(
                "● Out of Stock",
                "#fee2e2",
                "#991b1b"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox legendBar = new HBox(
                8,
                leftHeader,
                spacer,
                addItemButton,
                inStockLegend,
                lowStockLegend,
                outStockLegend
        );

        legendBar.setAlignment(Pos.CENTER_LEFT);
        legendBar.setPadding(new Insets(0, 0, 10, 0));

        // Table setup
        table.setItems(filteredItems);
        table.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
    );

        // Item Name
        TableColumn<InventoryItem, String> nameCol =
                new TableColumn<>("ITEM NAME");

        nameCol.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );

        // Type
        TableColumn<InventoryItem, String> typeCol =
                new TableColumn<>("CATEGORY / TYPE");

        typeCol.setCellValueFactory(
                new PropertyValueFactory<>("type")
        );

        // Current Stock
        TableColumn<InventoryItem, Integer> stockCol =
                new TableColumn<>("CURRENT STOCK");

        stockCol.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        // Status
        TableColumn<InventoryItem, String> statusCol =
                new TableColumn<>("STATUS");

        statusCol.setCellValueFactory(cell -> {

            int quantity = cell.getValue().getQuantity();
            int threshold = getThresholdValue();

            if (quantity == 0) {
                return new SimpleStringProperty("Out of Stock");
            }

            if (quantity <= threshold) {
                return new SimpleStringProperty("Low Stock");
            }

            return new SimpleStringProperty("In Stock");
        });

        statusCol.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(String status, boolean empty) {

                super.updateItem(status, empty);

                if (empty || status == null) {
                    setGraphic(null);
                    return;
                }

                Label badge;

                switch (status) {

                    case "Out of Stock":
                        badge = createBadgeLabel(
                                "● Out of Stock",
                                "#fee2e2",
                                "#991b1b"
                        );
                        break;

                    case "Low Stock":
                        badge = createBadgeLabel(
                                "● Low Stock",
                                "#fef3c7",
                                "#92400e"
                        );
                        break;

                    default:
                        badge = createBadgeLabel(
                                "● In Stock",
                                "#dcfce7",
                                "#166534"
                        );
                        break;
                }

                setGraphic(badge);
            }
        });

        // Quick Actions
        TableColumn<InventoryItem, Void> actionCol =
                new TableColumn<>("QUICK ACTIONS");

        actionCol.setCellFactory(col -> new TableCell<>() {

            private final Button minusButton = new Button("-1");
            private final Button plusButton = new Button("+5");
            private final Button setButton = new Button("Set Qty");

            private final HBox actionBox =
                    new HBox(
                            6,
                            minusButton,
                            plusButton,
                            setButton
                    );

            {
                minusButton.setStyle(
                        "-fx-background-color: #fee2e2;" +
                        "-fx-text-fill: #991b1b;" +
                        "-fx-font-weight: bold;"
                );

                plusButton.setStyle(
                        "-fx-background-color: #dcfce7;" +
                        "-fx-text-fill: #166534;" +
                        "-fx-font-weight: bold;"
                );

                setButton.setStyle(
                        "-fx-background-color: #dbeafe;" +
                        "-fx-text-fill: #1e40af;" +
                        "-fx-font-weight: bold;"
                );

                actionBox.setAlignment(Pos.CENTER_LEFT);

                // Decrease stock by 1
                minusButton.setOnAction(event -> {

                    InventoryItem entry =
                            getTableView()
                                    .getItems()
                                    .get(getIndex());

                    boolean changed =
                            inventory.adjustStock(
                                    entry.getName(),
                                    -1
                            );

                    if (!changed) {
                        showError(
                                "Stock cannot be reduced below zero."
                        );
                    }

                    refreshAll();
                });

                // Increase stock by 5
                plusButton.setOnAction(event -> {

                    InventoryItem entry =
                            getTableView()
                                    .getItems()
                                    .get(getIndex());

                    inventory.adjustStock(
                            entry.getName(),
                            5
                    );

                    refreshAll();
                });

                // Set exact quantity
                setButton.setOnAction(event -> {

                    InventoryItem entry =
                            getTableView()
                                    .getItems()
                                    .get(getIndex());

                    TextInputDialog dialog =
                            new TextInputDialog(
                                    String.valueOf(
                                            entry.getQuantity()
                                    )
                            );

                    dialog.setTitle("Set Stock Quantity");
                    dialog.setHeaderText(
                            "Update quantity for "
                                    + entry.getName()
                    );
                    dialog.setContentText(
                            "New quantity:"
                    );

                    dialog.showAndWait()
                            .ifPresent(input -> {

                                try {

                                    int newQuantity =
                                            Integer.parseInt(
                                                    input.trim()
                                            );

                                    inventory.changeQuantity(
                                            entry.getItem(),
                                            newQuantity
                                    );

                                    refreshAll();

                                } catch (IllegalArgumentException ex) {

                                    showError(
                                            "Please enter a valid non-negative integer."
                                    );
                                }
                            });
                });
            }

            @Override
            protected void updateItem(
                    Void item,
                    boolean empty) {

                super.updateItem(item, empty);

                setGraphic(
                        empty ? null : actionBox
                );
            }
        });

        table.getColumns().addAll(
                nameCol,
                typeCol,
                stockCol,
                statusCol,
                actionCol
        );

        VBox tableCard =
                new VBox(
                        8,
                        legendBar,
                        table
                );

        tableCard.setPadding(
                new Insets(14, 18, 14, 18)
        );

        tableCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: #cbd5e1;" +
                "-fx-border-radius: 8;" +
                "-fx-background-radius: 8;"
        );

        VBox.setVgrow(
                table,
                Priority.ALWAYS
        );

        return tableCard;
    }

    // ---------------------------------------------------------
    // LISTENERS
    // ---------------------------------------------------------

    private void setupListeners() {

        searchField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilters()
                );

        categoryBox.valueProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                applyFilters()
                );

        /*
         * Only allow non-negative whole numbers
         * in the threshold field.
         */
        thresholdField.setTextFormatter(
                new TextFormatter<String>(
                        change -> {

                            String newText =
                                    change.getControlNewText();

                            if (newText.matches("\\d*")) {
                                return change;
                            }

                            return null;
                        }
                )
        );

        thresholdField.textProperty()
                .addListener(
                        (obs, oldValue, newValue) ->
                                refreshAll()
                );

        lowStockToggle.setOnAction(event -> {
            updateToggleStyle();
            applyFilters();
        });
    }

    // ---------------------------------------------------------
    // FILTERING
    // ---------------------------------------------------------

    private void applyFilters() {

        String searchText =
                searchField.getText() == null
                        ? ""
                        : searchField
                                .getText()
                                .trim()
                                .toLowerCase();

        String selectedCategory =
                categoryBox.getValue();

        boolean onlyLowStock =
                lowStockToggle.isSelected();

        int threshold =
                getThresholdValue();

        filteredItems.setPredicate(entry -> {

            boolean matchesName =
                    searchText.isEmpty()
                            || entry
                            .getName()
                            .toLowerCase()
                            .contains(searchText);

            boolean matchesCategory =
                    selectedCategory == null
                            || selectedCategory.equals(
                                    "All Categories"
                            )
                            || entry
                            .getType()
                            .equalsIgnoreCase(
                                    selectedCategory
                            );

            /*
             * Low Stock means:
             *
             * quantity > 0 AND quantity <= threshold
             *
             * Quantity 0 is kept separate as Out of Stock.
             */
            boolean matchesLowStock =
                    !onlyLowStock
                            || (
                            entry.getQuantity() > 0
                                    && entry.getQuantity()
                                    <= threshold
                    );

            return matchesName
                    && matchesCategory
                    && matchesLowStock;
        });

        updateItemCount();
    }

    // ---------------------------------------------------------
    // DASHBOARD REFRESH
    // ---------------------------------------------------------

    private void refreshAll() {

        updateCategories();
        applyFilters();
        refreshMetrics();
        table.refresh();
    }

    private void refreshMetrics() {

        int threshold =
                getThresholdValue();

        int lowStockCount = 0;
        int outOfStockCount = 0;

        InventoryIterator iterator = inventory.createIterator();

        if (!iterator.isDone()) {

            InventoryItem entry = iterator.first();
        
            while (!iterator.isDone()) {
        
                int quantity = entry.getQuantity();
        
                if (quantity == 0) {
        
                    outOfStockCount++;
        
                } else if (quantity <= threshold) {
        
                    lowStockCount++;
                }
        
                entry = iterator.next();
            }
        }

        totalDistinctLabel.setText(
                String.valueOf(
                        inventory.size()
                )
        );

        lowStockLabel.setText(
                String.valueOf(
                        lowStockCount
                )
        );

        outOfStockLabel.setText(
                String.valueOf(
                        outOfStockCount
                )
        );

        updateItemCount();
    }

    private void updateItemCount() {

        int count =
                filteredItems.size();

        itemCountSubLabel.setText(
                count
                        + (count == 1
                        ? " item"
                        : " items")
        );
    }

    // ---------------------------------------------------------
    // CATEGORY / TYPE FILTER
    // ---------------------------------------------------------

    private void updateCategories() {

        String previousSelection =
                categoryBox.getValue();

        Set<String> categories =
                new TreeSet<>(
                        String.CASE_INSENSITIVE_ORDER
                );

                InventoryIterator iterator = inventory.createIterator();

                if (!iterator.isDone()) {
                
                    InventoryItem entry = iterator.first();
                
                    while (!iterator.isDone()) {
                
                        String type = entry.getType();
                
                        if (type != null && !type.isBlank()) {
                            categories.add(type.trim());
                        }
                
                        entry = iterator.next();
                    }
                }

        categoryBox.getItems().clear();
        categoryBox.getItems().add(
                "All Categories"
        );
        categoryBox.getItems().addAll(
                categories
        );

        if (previousSelection != null
                && categoryBox
                .getItems()
                .contains(previousSelection)) {

            categoryBox.setValue(
                    previousSelection
            );

        } else {

            categoryBox.setValue(
                    "All Categories"
            );
        }
    }

    // ---------------------------------------------------------
    // THRESHOLD
    // ---------------------------------------------------------

    private int getThresholdValue() {

        String text =
                thresholdField
                        .getText()
                        .trim();

        if (text.isEmpty()) {
            return 10;
        }

        try {

            return Integer.parseInt(text);

        } catch (NumberFormatException e) {

            return 10;
        }
    }

    // ---------------------------------------------------------
    // UI HELPERS
    // ---------------------------------------------------------

    private void updateToggleStyle() {

        if (lowStockToggle.isSelected()) {

            lowStockToggle.setText("ON");

            lowStockToggle.setStyle(
                    "-fx-background-color: #f59e0b;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-weight: bold;"
            );

        } else {

            lowStockToggle.setText("OFF");

            lowStockToggle.setStyle(
                    "-fx-background-color: #e2e8f0;" +
                    "-fx-text-fill: #334155;" +
                    "-fx-font-weight: bold;"
            );
        }
    }

    private Label createBadgeLabel(
            String text,
            String bgHex,
            String textHex) {

        Label badge =
                new Label(text);

        badge.setPadding(
                new Insets(
                        3,
                        10,
                        3,
                        10
                )
        );

        badge.setStyle(
                "-fx-background-color: "
                        + bgHex
                        + ";" +
                "-fx-text-fill: "
                        + textHex
                        + ";" +
                "-fx-font-size: 11px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;"
        );

        return badge;
    }
    private void showAddItemDialog() {

        Dialog<ButtonType> dialog = new Dialog<>();
    
        dialog.setTitle("Add Inventory Item");
        dialog.setHeaderText("Enter the new item's information");
    
        ButtonType addButtonType =
                new ButtonType(
                        "Add Item",
                        ButtonBar.ButtonData.OK_DONE
                );
    
        dialog.getDialogPane()
                .getButtonTypes()
                .addAll(
                        addButtonType,
                        ButtonType.CANCEL
                );
    
        // Input fields
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Ground Beef");
    
        TextField typeField = new TextField();
        typeField.setPromptText("e.g., Meat");
    
        TextField quantityField = new TextField();
        quantityField.setPromptText("e.g., 10");
    
        // Only allow whole numbers for quantity
        quantityField.setTextFormatter(
                new TextFormatter<String>(
                        change -> {
                            String newText =
                                    change.getControlNewText();
    
                            if (newText.matches("\\d*")) {
                                return change;
                            }
    
                            return null;
                        }
                )
        );
    
        GridPane form = new GridPane();
    
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));
    
        form.add(new Label("Item Name:"), 0, 0);
        form.add(nameField, 1, 0);
    
        form.add(new Label("Category / Type:"), 0, 1);
        form.add(typeField, 1, 1);
    
        form.add(new Label("Starting Quantity:"), 0, 2);
        form.add(quantityField, 1, 2);
    
        dialog.getDialogPane().setContent(form);
    
        dialog.showAndWait().ifPresent(result -> {
    
            if (result != addButtonType) {
                return;
            }
    
            String name = nameField.getText().trim();
            String type = typeField.getText().trim();
            String quantityText =
                    quantityField.getText().trim();
    
            // Validate the name
            if (name.isEmpty()) {
                showError("Item name cannot be blank.");
                return;
            }
    
            // Validate quantity
            if (quantityText.isEmpty()) {
                showError("Please enter a starting quantity.");
                return;
            }
    
            try {
    
                int quantity =
                        Integer.parseInt(quantityText);
    
                Item item =
                        new Item(
                                name,
                                type,
                                quantity > 0
                        );
    
                inventory.addToInventory(
                        item,
                        quantity
                );
    
                refreshAll();
    
            } catch (NumberFormatException ex) {
    
                showError(
                        "Please enter a valid non-negative whole number."
                );
    
            } catch (IllegalArgumentException ex) {
    
                showError(ex.getMessage());
            }
        });
    }
    private void showError(
            String message) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR,
                        message
                );

        alert.setHeaderText(null);
        alert.showAndWait();
    }
}