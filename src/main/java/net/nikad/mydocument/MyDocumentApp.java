package net.nikad.mydocument;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import net.nikad.mydocument.controller.MainController;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

        List<String> args = getParameters().getRaw();
        if (!args.isEmpty()) {
            File file = new File(args.get(0));
            if (file.exists() && file.isFile()) {
                MainController controller = loader.getController();
                controller.openFile(file);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
