package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    

        // name, length, difficulty, assigned, done
        TaskManager taskManager = new TaskManager();
        taskManager.addTask(new TaskImp("Task 1", 30, 1, false, false));
        taskManager.addTask(new TaskImp("Task 2", 60, 4, false, false));
        taskManager.addTask(new TaskImp("Task 3", 20, 1, true, false));
        

        MainDashboard dashboard = new MainDashboard();
        dashboard.setData(inventory, taskManager);
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