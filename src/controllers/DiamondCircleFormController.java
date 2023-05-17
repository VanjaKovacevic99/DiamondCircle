package controllers;

import app.DiamondCircle;
import elements.Diamond;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import models.Game;
import models.Player;
import models.figures.*;
import models.map.Field;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;



public class DiamondCircleFormController {


    private static final int TIME_FOR_RELOAD =50 ;
    private Stage stage;
    private int numberOfPlayers;
    private int matrixDimensions;
    private boolean firstRunning=true;
    private long timeOfStart;
    private long timeOfStartingPause=0;
    private long timeOfEndPause=0;
    private long timeAllPause=0;
    public static long timeSimulation;

    @FXML
    private StackPane diamondStackPane;

    @FXML
    private TextField numberTextField;

    @FXML
    private ImageView cardImageView;

    @FXML
    private StackPane imageStackPane;

    @FXML
    private TextField name1TextField;

    @FXML
    private TextField name2TextField;

    @FXML
    private TextField name3TextField;

    @FXML
    private TextField name4TextField;

    @FXML
    private Button resultsButton;
    @FXML
    private StackPane figureNamesStackPane;

    @FXML
    private VBox figuresVBox;

    @FXML
    private ListView<String> figuresListView;

    @FXML
    private TextArea messageTextArea;
    @FXML
    private Button startSimulationButton;

    @FXML
    private TextField timeTextField;


    private GridPane diamondGridPane;


    private static final String RESULTS_FORM = "/resources/resultsForm.fxml";
    private static final String MOVEMENT_FIGURE_FORM = "/resources/movementFigureForm.fxml";
    private static final String RESULTS_TITLE = "Results";
    private static final String FIGURE_MOVEMENT = "Figure movement";
    private static ArrayList<Field> path;
    public DiamondCircleFormController(){}
    public DiamondCircleFormController(Stage stage, int numberOfPlayers, int matrixDimensions) {

        this.stage = stage;
        this.matrixDimensions = matrixDimensions;
        this.numberOfPlayers = numberOfPlayers;
        path = Game.pathField(Game.loadPath(matrixDimensions), Game.matrixField(matrixDimensions));

    }

    public static ArrayList<Field> getPath() {
        return path;
    }

    @FXML
    public void initialize() {

        Game game = new Game(matrixDimensions, numberOfPlayers);

        ArrayList<String> resultsString = new ArrayList<>();
        Game.setFiguresName(numberOfPlayers);
        for (Player p : Game.players) {
            for (Figure f : p.getFigures()) {
                resultsString.add(f.getFigureName());

            }
        }
        ObservableList<String> items = FXCollections.observableArrayList(resultsString);
        figuresListView.setItems(items);

        Thread movementFigure = new Thread(() -> {

                figuresListView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent mouseEvent) {

                        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

                            String figureName = figuresListView.getSelectionModel().getSelectedItem();
                            Pair<Figure,Player> pair=getPlayerByFigureName(figureName);
                          showMovementScene(pair.getKey().getCurrentPath(),pair.getKey().getColor(),pair.getKey().getMark(),pair.getKey().getCurrentTime() );
                        }

                    }
                });



       });
       movementFigure.start();


        //imena igraca
        name1TextField.setText(Game.players.get(0).getPlayerName());
        name1TextField.setStyle("-fx-text-inner-color: " + Game.players.get(0).getColorFigures() + ";");
        name2TextField.setText(Game.players.get(1).getPlayerName());
        name2TextField.setStyle("-fx-text-inner-color: " + Game.players.get(1).getColorFigures() + ";");
        if (Game.players.size() > 2) {
            name3TextField.setText(Game.players.get(2).getPlayerName());
            name3TextField.setStyle("-fx-text-inner-color: " + Game.players.get(2).getColorFigures() + ";");
        }
        if (Game.players.size() > 3) {
            name4TextField.setText(Game.players.get(3).getPlayerName());
            name4TextField.setStyle("-fx-text-inner-color: " + Game.players.get(3).getColorFigures() + ";");
        }

        //br odigranih igara
        numberTextField.setText(String.valueOf(Game.currentNumberOfPlayedGame()));


        showGridPane();

        diamondStackPane.getChildren().add(diamondGridPane);
        AtomicBoolean isRefreshed= new AtomicBoolean(false);
        Thread refresh = new Thread(() -> {
            while (!isRefreshed.get()){
                synchronized (Game.REFRESH_LOCK){
                    try {
                        Game.REFRESH_LOCK.wait();
                    }
                    catch (InterruptedException e){
                        Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                    }
                if (Game.isGameOver()){
            if (Game.isGameOver){
                refreshGui();
            }
            try {
                sleep(Game.SLEEP_TIME);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
              isRefreshed.set(true);  }}}
        });
        refresh.start();



    }

    @FXML
    void startSimulation(MouseEvent event) {

        if (Game.isPause) {
            if(!firstRunning){
                 timeOfEndPause = System.currentTimeMillis();
                 timeAllPause+=timeOfEndPause-timeOfStartingPause;
            }

            Game.isPause = false;
            startSimulationButton.setText("Zaustavi");
            Game.setPause();
            if (firstRunning){

            timeOfStart=System.currentTimeMillis();
            GhostFigure ghostFigure = new GhostFigure(matrixDimensions);
            ghostFigure.start();

            Runnable simulation = () -> {

                Game.startGame(numberOfPlayers);


            };
            Thread t1 = new Thread(simulation);
            t1.start();



            Runnable diamondsMap = () -> {
                while (!Game.isGameOver()) {
                    if (!Game.isPause) {

                        List list = (List) this.diamondGridPane.getChildren().stream().filter((node) -> node instanceof ImageView).toList();
                        Platform.runLater(() -> {
                            this.diamondGridPane.getChildren().removeAll(list);
                        });
                        for (int i = 0; i < matrixDimensions - 1; i++)
                            for (int j = 0; j < matrixDimensions - 1; j++) {
                                Field field = Game.getFieldAt(i, j);
                                final int x_coordinate = i;
                                final int y_coordinate = j;

                                if (field.getObjectOfField() instanceof Diamond) {
                                    ImageView img_view2 = new ImageView(Diamond.readImage());
                                    img_view2.setFitHeight((double) 350 / matrixDimensions / 2);
                                    img_view2.setFitWidth((double) 350 / matrixDimensions / 2);
                                    Platform.runLater(() -> {

                                        this.diamondGridPane.add(img_view2, x_coordinate, y_coordinate);
                                        this.diamondGridPane.setAlignment(Pos.CENTER);
                                    });
                                }

                            }

                        try {
                            sleep(GhostFigure.TIME_OF_SLEEPING_THREAD);
                        } catch (InterruptedException e) {
                            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                        }
                    }
                }
            };


            Runnable figureMoving = () -> {
                while (!Game.isGameOver) {

                    Platform.runLater(() -> {
                            if (!Game.isPause) {
                            cardImageView.setImage(Game.currentImage);}
                        });



                            Platform.runLater(() -> {
                                        if (!Game.isPause) {
                                            Field field = Game.removeDiamond.poll();
                                            if (field !=null){
                                            List list = (List) this.diamondGridPane.getChildren().stream().filter((node) -> node instanceof ImageView && GridPane.getRowIndex(node) == field.getX() && GridPane.getColumnIndex(node) == field.getY()).toList();
                                            Platform.runLater(() -> {
                                                this.diamondGridPane.getChildren().removeAll(list);
                                            });
                                        }
                                        }
                            });

                            if (!Game.isPause) {
                            Field field = Game.removeFigure.poll();
                            if(field!=null){
                            List list = (List) this.diamondGridPane.getChildren().stream().filter((node) -> node instanceof TextField && GridPane.getRowIndex(node) == field.getY() && GridPane.getColumnIndex(node) == field.getX()).toList();
                            Platform.runLater(() -> {
                                this.diamondGridPane.getChildren().removeAll(list);
                            });
                            }
                            }


                            Platform.runLater(() -> {
                                if (!Game.isPause) {

                                                Pair<Field, Figure> pair = Game.putFigure.poll();
                                                if (pair!=null){
                                                TextField textField = new TextField();
                                                textField.setPrefSize(((double) 350 / matrixDimensions) - 1, ((double) 350 / matrixDimensions) - 1);
                                                textField.setText(pair.getValue().getMark());
                                                textField.setAlignment(Pos.CENTER);
                                                textField.setStyle("-fx-background-color: " + pair.getValue().getColor());
                                                this.diamondGridPane.add(textField, pair.getKey().getX(), pair.getKey().getY());
                                                this.diamondGridPane.setAlignment(Pos.CENTER);
                                                }

                                            }


                                        });


                            Platform.runLater(() -> {
                            messageTextArea.setText(Game.currentMessage);
                        });

                            if (!Game.isPause) {
                            Platform.runLater(() -> {
                                Field field = Game.putHole.poll();
                                if(field !=null) {
                                Circle newCircle = new Circle((double) 350 / matrixDimensions / 4);
                                diamondGridPane.add(newCircle, field.getX(), field.getY());

                                }

                            });}


                            if (!Game.isPause) {
                                Platform.runLater(() -> {
                                    Field field = Game.removeHole.poll();
                                    if(field !=null) {
                                        List list = (List)this.diamondGridPane.getChildren().stream().filter((node) -> node instanceof Circle && GridPane.getRowIndex(node) == field.getY() && GridPane.getColumnIndex(node) == field.getX()).toList();
                                        Platform.runLater(() -> {
                                            this.diamondGridPane.getChildren().removeAll(list);
                                        });
                                    }
                                });

                            }

                    if (!Game.isPause) {
                        Platform.runLater(() -> {
                            long timeOnThisMoment = System.currentTimeMillis();
                            timeSimulation = timeOnThisMoment - timeOfStart -timeAllPause;
                            timeTextField.setText(Game.timeToString(timeSimulation));
                        });
                    }
                        try {
                            sleep(TIME_FOR_RELOAD);
                        } catch (InterruptedException e) {
                            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                        }

                    }

            };


            Thread t = new Thread(diamondsMap);
            t.start();
            Thread t2 = new Thread(figureMoving);
            t2.start();}
            firstRunning=false;
        }
        else {
            timeOfStartingPause=System.currentTimeMillis();
            Game.isPause = true;
            startSimulationButton.setText("Pokreni");
            Game.setPause();
        }
    }

    @FXML
    void showResultsForm(MouseEvent event) {
        try{
            Stage stage1=new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(RESULTS_FORM));
        ResultsFormController resultsFormController=new ResultsFormController();
        loader.setController(resultsFormController);
        Parent root = loader.load();
        stage1.setTitle(RESULTS_TITLE);
        stage1.setScene(new Scene(root));
        stage1.show();
        }
        catch (IOException e){
        Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);

         }
    }

    //prikaz predjenog puta figure
    public void showMovementScene(ArrayList<Field> currentPath, Color color, String mark,String time){
        try{
            Stage stage1=new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MOVEMENT_FIGURE_FORM));
            MovementFigureFormController movementFigureFormController=new MovementFigureFormController(matrixDimensions,currentPath,color,mark,time);
            loader.setController(movementFigureFormController);
            Parent root = loader.load();
            stage1.setTitle(FIGURE_MOVEMENT);
            stage1.setScene(new Scene(root));
            stage1.show();
        }
        catch (IOException e){
            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);

        }

    }
    public void showGridPane(){
        ArrayList<Field> arrayList=Game.matrixField(matrixDimensions);
        diamondGridPane =new GridPane();
        diamondGridPane.setGridLinesVisible(true);
        diamondGridPane.setAlignment(Pos.CENTER);

        //kreiraj grid sistem
        for (int i = 0; i < matrixDimensions; i++) {
            ColumnConstraints colConst = new ColumnConstraints((double) 350/matrixDimensions);
            diamondGridPane.getColumnConstraints().add(colConst);
            RowConstraints rowConst = new RowConstraints((double) 350/matrixDimensions);
            diamondGridPane.getRowConstraints().add(rowConst);
        }

        for (int i=0;i<arrayList.size();i++){
            Text text=new Text("" + arrayList.get(i).getValue());
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setValignment(text, VPos.CENTER);
            diamondGridPane.add(text,arrayList.get(i).getX(),arrayList.get(i).getY());
        }

    }

    public static Pair<Figure,Player> getPlayerByFigureName(String figureName){
        Pair<Figure,Player> playerPair = null;
        for (Player p:Game.players){
            for (Figure f: p.getFigures()){
                if (Objects.equals(f.getFigureName(), figureName)) {
                    playerPair=new Pair<>(f,p);

                    break;
                }

            }

        }

        return playerPair;

    }

    //refres gui-a nakon zavrsene simulacije
    public void refreshGui(){
                    List list = (List) this.diamondGridPane.getChildren().stream().filter((node) -> node instanceof ImageView).toList();
                    Platform.runLater(() -> {
                        this.diamondGridPane.getChildren().removeAll(list);
                    });
                    //br odigranih igara
                    numberTextField.setText(String.valueOf(Game.currentNumberOfPlayedGame()));

                    startSimulationButton.setDisable(true);
                    cardImageView.setVisible(false);
                    messageTextArea.setText("");


                }



}
