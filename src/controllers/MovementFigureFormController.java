package controllers;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;
import javafx.util.Pair;
import models.Game;
import models.Player;
import models.figures.Color;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import models.figures.Figure;
import models.map.Field;

import java.util.ArrayList;

public class MovementFigureFormController {
    private int matrixDimension;


    @FXML
    private StackPane movementStackPane;

    @FXML
    private TextField timeTextField;

    private GridPane gridPane;
    private Color color;
    private ArrayList<Field> currentPath;
    private String mark;
    private String time;

    public MovementFigureFormController(int matrixDimension,ArrayList<Field>currentPath,Color color,String mark,String time){
        this.matrixDimension=matrixDimension;
     this.currentPath=currentPath;
     this.color=color;
     this.mark=mark;
     this.time=time;

    }
    public MovementFigureFormController(String mark){
        this.mark=mark;
    }

    public void initialize() {
        showGridPane();
        movementStackPane.getChildren().add(gridPane);


                   timeTextField.setText(time);

                for (Field f :currentPath){
                Platform.runLater(() -> {
                    TextField textField = new TextField();
                    textField.setPrefSize((double) 350 / matrixDimension, (double) 350 / matrixDimension);
                    textField.setText(mark);
                    textField.setAlignment(Pos.CENTER);
                    textField.setStyle("-fx-background-color: " + color.toString());
                    this.gridPane.add(textField, f.getX(), f.getY());
                    this.gridPane.setAlignment(Pos.CENTER);


                });
            }
            }








        public void showGridPane(){
        ArrayList<Field> arrayList=Game.matrixField(matrixDimension);
        gridPane =new GridPane();
        gridPane.setGridLinesVisible(true);
        gridPane.setAlignment(Pos.CENTER);

        //kreiraj grid sistem
        for (int i = 0; i < matrixDimension; i++) {
            ColumnConstraints colConst = new ColumnConstraints((double) 350/matrixDimension);
            gridPane.getColumnConstraints().add(colConst);
            RowConstraints rowConst = new RowConstraints((double) 350/matrixDimension);
            gridPane.getRowConstraints().add(rowConst);
        }

        for (int i=0;i<arrayList.size();i++){
            Text text=new Text("" + arrayList.get(i).getValue());
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setValignment(text, VPos.CENTER);
            gridPane.add(text,arrayList.get(i).getX(),arrayList.get(i).getY());
        }

    }
}
