package models;

import app.DiamondCircle;
import controllers.DiamondCircleFormController;
import javafx.scene.image.Image;
import javafx.util.Pair;
import models.cards.Card;
import models.cards.OrdinaryCard;
import models.cards.SpecialCard;
import models.figures.*;
import models.figures.Color;
import models.map.Field;


import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {

    public static String diamondPath="src/path/path.txt";
    public static int NUMBER_OF_ORDINARY_CARDS=10;
    public static int NUMBER_OF_SPECIAL_CARDS=12;
    public static int MIN_NUMBERS_OF_HOLE=2;
    public static int MAX_NUMBERS_OF_HOLE=6;

    public static String NAME="name";
    public static String FIGURE_NAME="Figure ";
    public static String RESULTS_PATH="src/results";

    public static int SLEEP_TIME=1_000;
    public static final Object PAUSE_LOCK=new Object();
    public static final Object REFRESH_LOCK=new Object();



    public static Field[][] game;


    public static boolean isGameOver=false;
    public static boolean isPause = true;


    public static Player currentPlayer=null;
    public static Card currentCard=null;
    public static int currentValue=0;
    public static int matrixDimension;
    public static Image currentImage;
    public static ConcurrentLinkedQueue<Field> removeFigure=new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Field> removeDiamond=new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Pair<Field,Figure>> putFigure = new ConcurrentLinkedQueue<Pair<Field,Figure>>();
    public static ConcurrentLinkedQueue<Field> putHole=new ConcurrentLinkedQueue<>();
    public static  ConcurrentLinkedQueue<Field> removeHole=new ConcurrentLinkedQueue<>();
    public static String currentMessage= "";

    //public static ArrayList<Field>
    public static ArrayList<Player> players=new ArrayList<>();
    public static ArrayList<Field> holes=new ArrayList<>();

    public Game(){
        super();
    }

    public Game(int matrixDimension,int numberOfPlayers){
        this.matrixDimension=matrixDimension;
        players=createPlayers(numberOfPlayers);
        game=matrixFieldMatrix(matrixDimension);

    }

    public static int getMatrixDimension() {
        return matrixDimension;
    }

    public static Field getFieldAt(int x, int y){
        return game[x][y];
    }


    //random broj iz opsega
    public static int randomNumber(int max, int min){
        Random random=new Random();
        return random.nextInt(max - min) + min;
    }

    //putanja
    public static ArrayList<String> loadPath(int matrixDimension){
        String [] array=null;
        ArrayList<String> stringArrayList=new ArrayList<>();
        try {
            File file = new File(diamondPath);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()){
                String line= sc.nextLine();
                int dimension=Integer.parseInt(line.split("#")[0]);
                if(matrixDimension == dimension){
                    String stringPath;
                    stringPath=line.split("#")[1];
                    array=stringPath.split(",");
                }
            }
        }
        catch (FileNotFoundException e){
            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);
        }
        Collections.addAll(stringArrayList,array);

        return stringArrayList;
    }

    //kreiranje matrice i popunjavanje vrijednosti
    public static ArrayList<Field> matrixField(int matrixDimension) {
        ArrayList<Field> arrayList=new ArrayList<>();
            int k=1;
            for (int i = 0; i < matrixDimension; i++)
                for (int j = 0; j < matrixDimension; j++) {
                    arrayList.add(new Field(j,i,k,null));
                    k++;
                }
        return arrayList;
    }

    public static Field[][] matrixFieldMatrix(int matrixDimension) {
        Field [][] matrix=new Field[matrixDimension][matrixDimension];
        int k=1;
        for (int i = 0; i < matrixDimension; i++)
            for (int j = 0; j < matrixDimension; j++) {
                matrix[i][j]=new Field(j,i,k,null);

                k++;
            }
        return matrix;
    }

    //putanja, svaka vrijednost uz koordinate
    public static ArrayList<Field> pathField(ArrayList<String> stringPath, ArrayList<Field> matrix){
        ArrayList<Field> arrayList=new ArrayList<>();
        for(int i=0; i<stringPath.size(); i++){
            for(int j=0;j<matrix.size();j++){
                if(Integer.parseInt(stringPath.get(i))== matrix.get(j).getValue()){
                    arrayList.add(new Field(matrix.get(j).getX(),matrix.get(j).getY(),matrix.get(j).getValue(),null));
                }
            }
        }
        return arrayList;
    }


    //pomijeranje za odredejni broj koraka, lista koraka izmedju startne i zavrsne pozicjije
    public static ArrayList<Field> move(Field startPosition,int numberOfStep, ArrayList<Field> path) {
        ArrayList<Field> endPosition = new ArrayList<>();
        int element = 0;
        if (startPosition == null) {
            element = 0;
        }
        else {
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getValue() == startPosition.getValue()) {
                element = i + 1;
                break;
            }
        }
        }

        if (element + numberOfStep>path.size()){
            endPosition=new ArrayList<>(path.subList(element,path.size()));
        }
        else
        {
            for (int j = 0; j < numberOfStep; j++) {

                endPosition.add(new Field(path.get(element + j).getX(), path.get(element + j).getY(), path.get(element + j).getValue(), null));
            }
        }

        return endPosition;
    }


    //kreiranje spila
    public static ArrayList<Card> creatDeckOfCards(){
        ArrayList<Card> deck=new ArrayList<>();
        for (int i=0; i<NUMBER_OF_SPECIAL_CARDS;i++){
            deck.add(new SpecialCard());
        }
        for (int j=0; j< NUMBER_OF_ORDINARY_CARDS; j++){
            deck.add(new OrdinaryCard(1));
            deck.add(new OrdinaryCard(2));
            deck.add(new OrdinaryCard(3));
            deck.add(new OrdinaryCard(4));
        }
        Collections.shuffle(deck);
        return deck;
    }
    //kreiranje igraca, i odredjujemo koji je igrac koje boje
    public static ArrayList<Player> createPlayers(int numberOfPlayers){
        ArrayList<Player> playerArrayList=new ArrayList<>();
        Color [] colors=Color.values();
        List<Color> listColors = Arrays.asList(colors);
        Collections.shuffle(listColors);
        Collections.shuffle(playerArrayList);
        for(int i=0;i<numberOfPlayers;i++){
            playerArrayList.add(new Player(NAME + (i+1),listColors.get(i)));
        }
        return playerArrayList;
    }

    //broj fajlova sa rezultatima
    public static int currentNumberOfPlayedGame(){
        File directory=new File(RESULTS_PATH);
        return directory.list().length;
    }

    public static boolean isGameOver() {
        return  isGameOver;
    }

    //dodjeljivanje imena figurama
    public static void setFiguresName(int numberOfPlayers){
        int k=1;
        for (int i=0;i<numberOfPlayers; i++){
            for (int j=0;j<Player.NUMBER_OF_FIGURES; j++){
            players.get(i).getFigures().get(j).setFigureName(FIGURE_NAME + k);
            k++;
            }
        }

    }

   // da li je diamant na polju
    public static void pickUpDiamond(int x, int y){
          game[x][y].setObjectOfField(null);
    }



    public static void setPause() {
        synchronized (PAUSE_LOCK) {
            if (!isPause)
                PAUSE_LOCK.notifyAll();
        }

    }
    public static String pathToSting(ArrayList<Field> path){
        StringBuilder stringPath= new StringBuilder();
        for (Field f:path){
            stringPath.append(f.getValue());
            stringPath.append(", ");
        }
        return stringPath.toString();
    }

    public static void writeResults(int numberOfPlayers){
     try{synchronized (REFRESH_LOCK){
            String path= RESULTS_PATH + "/IGRA_" + LocalDateTime.now().getHour() + "." +
                    LocalDateTime.now().getMinute() + "." +
                    LocalDateTime.now().getSecond()+".txt";

            PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(path)));
        for(int i=0;i<numberOfPlayers;i++){
            printWriter.println("Igrac " + (i+1) + " - " + players.get(i).getPlayerName());
            for (int j=0; j<Player.NUMBER_OF_FIGURES;j++){
                Figure figure=players.get(i).getFigures().get(j);
                String path1=pathToSting(figure.getCurrentPath());
                printWriter.println("   " + figure.getFigureName()+ "(" + figure.getMark() + "," + figure.getColor() + ")" + " - predjeni put  (" + path1.substring(0,path1.length()-2) + ") - Stigla do cillja" + (figure.getCurrentPath().size()==figure.getPath().size()? " Da":" Ne"));
            }

       }
        printWriter.println("Ukupan broj trajanja igre: " + timeToString(DiamondCircleFormController.timeSimulation));
        printWriter.close();
     REFRESH_LOCK.notify();}
      }
        catch (IOException e){
            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
        }
    }

    public static String timeToString(long millis){
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

    }


    public static void startGame(int numberOfPlayers)  {
        ArrayList<Card> cards=creatDeckOfCards();

        while (!isGameOver()){
            currentCard=cards.get(0);
            Collections.rotate(cards,-1);
            if(currentCard instanceof OrdinaryCard) {
                currentImage=((OrdinaryCard) currentCard).readImage();
                currentValue=((OrdinaryCard) currentCard).getValue();
                    currentPlayer = players.get(0);
                    Iterator<Player> iterator = players.iterator();
                    if (currentPlayer.isEnd()) {
                        while (iterator.hasNext()) {
                            currentPlayer =  iterator.next();

                            if (!currentPlayer.isEnd()){
                                break;
                            }

                        }
                    }
                if (!currentPlayer.isEnd()) {

                    if (!currentPlayer.isStarted() && !isGameOver()) {
                        currentPlayer.setCurrentValue(((OrdinaryCard) currentCard).getValue());
                        currentPlayer.start();

                        try {
                            Thread.sleep(SLEEP_TIME/2);
                        } catch (InterruptedException e) {
                            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                        }
                    }


                synchronized (currentPlayer.LOCK) {
                    currentPlayer.LOCK.notify();
                    try {
                       currentPlayer.LOCK.wait();
                        if (!currentPlayer.isEnd())
                            Collections.rotate(players, -1);

                    } catch (InterruptedException e) {
                        Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                    }

                }
                }
                else {
                    isGameOver=true;
                    break;
                }

            }

            else {

                SpecialCard currentCard1 = (SpecialCard) currentCard;
                currentImage=currentCard1.readImage();
                currentCard1.setHoleOnMatrix();
            }

        }
        writeResults(numberOfPlayers);


        System.out.println("kraj simualcija");

    }
}