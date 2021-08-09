package de.fynn.randomSitzordnung;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.awt.*;

public class Main extends Application {

    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        primaryStage.setTitle("Random Sitzordnung");
        primaryStage.setScene(new Scene(root,
                Toolkit.getDefaultToolkit().getScreenSize().getWidth()*0.8,
                Toolkit.getDefaultToolkit().getScreenSize().getHeight()*0.8));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/hammer.png")));
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreen(false);
        primaryStage.show();
    }

    public static Stage getStage() {
        return stage;
    }

}
