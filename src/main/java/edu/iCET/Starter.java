package edu.iCET;

import edu.iCET.controller.DashFormController;
import edu.iCET.controller.ViewHistoryController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Starter extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/dash_form.fxml"));

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(root, 420, 720);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        
        // Window dragging logic
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("TaskTrack");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo.png")));
        primaryStage.setResizable(false);
        primaryStage.show();
        
        ViewHistoryController.setStage(primaryStage);
        DashFormController.setStage(primaryStage);
    }
}
