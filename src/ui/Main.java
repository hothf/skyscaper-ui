package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static final int WIDTH_PX = 600;
    private static final int HEIGHT_PX = 500;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Resource Management Tool");
        primaryStage.setScene(new Scene(root, WIDTH_PX, HEIGHT_PX));
        primaryStage.setMinWidth(WIDTH_PX);
        primaryStage.setMinHeight(HEIGHT_PX);
        primaryStage.setResizable(true);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
