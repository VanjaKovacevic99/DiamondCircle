package controllers;

import app.DiamondCircle;
import exceptions.IllegalValueException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;



public class FirstFormController {
    private static final String DIAMOND_CIRCLE_FORM= "/resources/diamondCircleForm.fxml";
    private static final int MIN_NUMBERS_OF_PLAYERS=2;
    private static final int MAX_NUMBERS_OF_PLAYERS=4;
    private static final int MIN_MATRIX_DIMENSION=7;
    private static final int MAX_MATRIX_DIMENSION=10;


    @FXML
    private TextField numberOfPlayersTextField;

    @FXML
    private TextField matrixDimensionsTextField;

    @FXML
    private Button playButton;

    @FXML
    private Label wrongValueLabel;


    @FXML
    void playGameButton(MouseEvent event) {
        try{
        if((Integer.parseInt(numberOfPlayersTextField.getText()) > MAX_NUMBERS_OF_PLAYERS || Integer.parseInt(numberOfPlayersTextField.getText()) < MIN_NUMBERS_OF_PLAYERS ) &&( Integer.parseInt(matrixDimensionsTextField.getText()) < MIN_MATRIX_DIMENSION || Integer.parseInt(matrixDimensionsTextField.getText()) > MAX_MATRIX_DIMENSION) ) {
            throw new IllegalValueException("Broj igraca i dimenzija matrice nisu iz opsega!");
        }
        if((Integer.parseInt(numberOfPlayersTextField.getText()) > MAX_NUMBERS_OF_PLAYERS || Integer.parseInt(numberOfPlayersTextField.getText()) < MIN_NUMBERS_OF_PLAYERS)){
            throw  new IllegalValueException("Broj igraca nije iz opsega!");
        }
        if ( Integer.parseInt(matrixDimensionsTextField.getText()) < MIN_MATRIX_DIMENSION || Integer.parseInt(matrixDimensionsTextField.getText()) > MAX_MATRIX_DIMENSION){
            throw new IllegalValueException("Dimenzije matrice nisu iz opsega!");
            }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(DIAMOND_CIRCLE_FORM));
        DiamondCircleFormController diamondCircleController=new DiamondCircleFormController(stage,Integer.parseInt(numberOfPlayersTextField.getText()), Integer.parseInt(matrixDimensionsTextField.getText()));
        loader.setController(diamondCircleController);
        Parent root = loader.load();
        stage.setTitle(DiamondCircle.APPLICATION_TITLE);
        stage.setScene(new Scene(root));
        stage.show();}
        catch (IllegalValueException e) {
            wrongValueLabel.setText(e.getMessage());
            wrongValueLabel.setStyle("-fx-text-fill: red;");
            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);
        }
        catch (IOException e){
            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);

        }

    }

    Stage stage;

    public FirstFormController(Stage stage){
        this.stage=stage;
    }
}
