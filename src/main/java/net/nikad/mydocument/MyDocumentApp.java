package net.nikad.mydocument;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MyDocumentApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                MyDocumentApp.class.getResource("main.fxml"));
        Scene scene = new Scene(loader.load(), 1024, 768);
        stage.getIcons().add(new Image(
                Objects.requireNonNull(MyDocumentApp.class.getResourceAsStream("icon.png"))));
        stage.setTitle("Untitled — MyDocument");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
