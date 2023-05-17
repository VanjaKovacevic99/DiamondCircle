package controllers;

import app.DiamondCircle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.Game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ResultsFormController {

    @FXML
    private ListView<String> resultsListView;

    public void initialize(){
        ArrayList<String> resultsString=getFileName();
        ObservableList<String> items = FXCollections.observableArrayList(resultsString);
        resultsListView.setItems(items);
        resultsListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        String item = resultsListView.getSelectionModel()
                                .getSelectedItem();
                        String fileText=readFile(Game.RESULTS_PATH + "/" + item);
                        if (item != null) {
                            StackPane pane = new StackPane();
                            Scene scene = new Scene(pane);
                            Stage stage = new Stage();
                            stage.setScene(scene);

                            pane.getChildren().add(
                                    new TextArea(fileText));

                            stage.show();
                        }

                    }
                }
            }
        });

    }
    public ArrayList<String> getFileName(){

    ArrayList<String> results = new ArrayList<String>();
    File[] files = new File(Game.RESULTS_PATH).listFiles();
        for (File file : files) {
        if (file.isFile()) {
            results.add(file.getName());
        }
    }return results;
    }

    private static String readFile(String filePath)
    {

        StringBuilder builder = new StringBuilder();
        try (BufferedReader buffer = new BufferedReader(
                new FileReader(filePath))) {

            String str;

            while ((str = buffer.readLine()) != null) {

                builder.append(str).append("\n");
            }
        }

        catch (IOException e) {

            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);

        }
        return builder.toString();
    }

}
