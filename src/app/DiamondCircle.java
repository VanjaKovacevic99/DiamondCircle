package app;


import controllers.DiamondCircleFormController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import controllers.FirstFormController;
import models.Game;
import models.Player;
import models.map.Field;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;

public class DiamondCircle extends Application {

    public static String APPLICATION_TITLE="DiamondCircle";
    public static String FIRST_FORM = "/resources/firstForm.fxml";
    public static final String LOGGER_NAME = "Global logger";
    public static final String LOGGER_PATH = "src/Logs/diamond_circle.log";
    public static Handler handler;
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FIRST_FORM));
        FirstFormController firstFormController=new FirstFormController(stage);
        loader.setController(firstFormController);
        Parent root = loader.load();
        stage.setTitle(APPLICATION_TITLE);
        stage.setScene(new Scene(root));
        stage.show();
    }


    public static void main(String[] args) {
        try{


            handler = new FileHandler(LOGGER_PATH,true);
            Logger logger = Logger.getLogger(LOGGER_NAME);
            handler.setFormatter(new SimpleFormatter());
            logger.addHandler(handler);
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.ALL);

        }catch (IOException e){
            Logger.getLogger(LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);

        }
        launch(args);

    }


}