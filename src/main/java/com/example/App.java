package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        /* 
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.show();
        */
               Inventory inventory = new Inventory();
        inventory.addToInventory(new Item("Veggies", "Produce", true), 0);
        inventory.addToInventory(new Item("Lettuce", "Produce", true), 25);
        inventory.addToInventory(new Item("Chicken", "Meat", true), 30);
    
        // Shared employees
        List<Employee> employees = new ArrayList<>();

        Employee alice = new EmployeeImp("Alice", 101, 3, false);
        Employee tyler = new EmployeeImp("Tyler", 102, 2, false);
        Employee grace = new EmployeeImp("Grace", 103, 2, false);
        Employee olivia = new EmployeeImp("Olivia", 104, 4, false);
        Employee ethan = new EmployeeImp("Ethan", 105, 1, false);
        Employee mia = new EmployeeImp("Mia", 106, 3, false);

        employees.add(alice);
        employees.add(tyler);
        employees.add(grace);
        employees.add(olivia);
        employees.add(ethan);
        employees.add(mia);

        // Shared task manager
        TaskManager taskManager = new TaskManager();

        TaskImp takeOutTrash = new TaskImp("Take out trash", 15, 3, false, false);
        TaskImp washDishes = new TaskImp("Wash dishes", 30, 2, false, false);
        TaskImp wipeCountertops = new TaskImp("Wipe countertops", 10, 2, false, false);
        TaskImp countMoney = new TaskImp("Count money", 5, 1, false, false);
        TaskImp sweepDiningRoom = new TaskImp("Sweep dining room", 20, 2, false, false);
        TaskImp restockNapkins = new TaskImp("Restock napkins", 10, 1, false, false);
        TaskImp cleanWindows = new TaskImp("Clean windows", 25, 3, false, false);
        TaskImp mopKitchen = new TaskImp("Mop kitchen", 25, 4, false, false);
        TaskImp prepareVegetables = new TaskImp("Prepare vegetables", 20, 3, false, false);
        TaskImp checkRefrigerator = new TaskImp("Check refrigerator", 8, 1, false, false);

        taskManager.addTask(takeOutTrash);
        taskManager.addTask(washDishes);
        taskManager.addTask(wipeCountertops);
        taskManager.addTask(countMoney);
        taskManager.addTask(sweepDiningRoom);
        taskManager.addTask(restockNapkins);
        taskManager.addTask(cleanWindows);
        taskManager.addTask(mopKitchen);
        taskManager.addTask(prepareVegetables);
        taskManager.addTask(checkRefrigerator);

        // Initial task assignments
        takeOutTrash.assignEmployee(alice);

        wipeCountertops.assignEmployee(tyler);
        wipeCountertops.doTask(tyler);

        countMoney.assignEmployee(grace);
        countMoney.doTask(grace);

        sweepDiningRoom.assignEmployee(olivia);
        restockNapkins.assignEmployee(ethan);
        prepareVegetables.assignEmployee(mia);
        

        MainDashboard dashboard = new MainDashboard();
        dashboard.setData(inventory, taskManager, employees);
        dashboard.start(stage);
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}