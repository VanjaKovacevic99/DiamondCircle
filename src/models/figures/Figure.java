package models.figures;

import app.DiamondCircle;
import controllers.DiamondCircleFormController;
import elements.Diamond;
import javafx.util.Pair;
import models.Game;
import models.map.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;



public abstract class Figure extends Thread {
    private Color color;
    private int typeOfMoving;

    private String figureName;
    private boolean isFigureRun = false;
    private boolean isFigureEnd = false;
    private boolean isFigureFallen=false;
    private int firstFieldValue=0;
    private int numberOfDiamonds=0;
    private ArrayList<Field> path;
    private Field fieldBeforeMove;
    private static Field setField;
    private long timeOfMoving=0;
    private String currentTime="";

    private final String mark = "F";

    private final int FIGURE_SLEEP=1_000;


    private ArrayList<Field> currentPath = new ArrayList<>();


    public final Object LOCK = new Object();
    public static final Object LOCK_PATH=new Object();

    private int lastField = 0;

    public void setFigureName(String figureName) {
        this.figureName = figureName;
    }

    public boolean isFigureFallen() {
        return isFigureFallen;
    }

    public void setFigureFallen(boolean figureFallen) {
        isFigureFallen = figureFallen;
    }
    public void setFigureEnd(boolean isFigureEnd){
        this.isFigureEnd=isFigureEnd;
    }

    public int getLastField() {
        return lastField;
    }

    public String getMark() {
        return mark;
    }

    public ArrayList<Field> getCurrentPath() {
        return currentPath;
    }

    public String getFigureName() {
        return figureName;
    }

    public ArrayList<Field> getPath() {
        return path;
    }

    public boolean isFigureRun() {
        return isFigureRun;
    }

    public boolean isFigureEnd() {
        return isFigureEnd;
    }

    public Field getFieldBeforeMove() {
        return fieldBeforeMove;
    }

    public Figure(Color color, int typeOfMoving) {
        this.color = color;
        this.typeOfMoving = typeOfMoving;
    }

    public Figure() {
        super();
    }

    public Figure(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setTypeOfMoving(int typeOfMoving) {
        this.typeOfMoving = typeOfMoving;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    @Override
    public void run() {
        path = DiamondCircleFormController.getPath();
        isFigureRun = true;
        synchronized (LOCK) {
            while (currentPath.size() != path.size()) {

                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);

                }
                long begin=System.currentTimeMillis();

                //polje prije pomijeranja
                if (currentPath.size() == 0) {
                    fieldBeforeMove = null;
                } else {
                    fieldBeforeMove = currentPath.get(currentPath.size() - 1);
                    firstFieldValue=fieldBeforeMove.getValue();
                }


                if (fieldBeforeMove != null) {

                    Game.removeFigure.add(fieldBeforeMove);//gui
                    Game.game[fieldBeforeMove.getX()][fieldBeforeMove.getY()].setObjectOfField(null);
                }

                //polja koja prelazi u jednom koraku
                ArrayList<Field> pathOneMove = Game.move(fieldBeforeMove, typeOfMoving + numberOfDiamonds, path);



                numberOfDiamonds=0;

                if (Game.game[pathOneMove.get(pathOneMove.size() - 1).getX()][pathOneMove.get(pathOneMove.size() - 1).getY()].getObjectOfField() instanceof Figure) {
                    while (Game.game[pathOneMove.get(pathOneMove.size() - 1).getX()][pathOneMove.get(pathOneMove.size() - 1).getY()].getObjectOfField() instanceof Figure) {
                        int i = getIndexNextField(pathOneMove.get(pathOneMove.size() - 1), path);
                        if (pathOneMove.size() + currentPath.size() == path.size()) {
                            this.isFigureEnd=true;
                            break;
                        }

                    else {

                        pathOneMove.add(path.get(i));

                    }

                }


                }
                //poruka
                Game.currentMessage=oneMoveMessage(pathOneMove.size(),firstFieldValue,pathOneMove.get(pathOneMove.size()-1).getValue());


                ArrayList<Field> temp = new ArrayList<>(pathOneMove);
                lastField = pathOneMove.size() + currentPath.size();//duzina niza na kraju putanje


                long startPause=0;
                long timePause=0;
                for (Field f : pathOneMove) {

                    synchronized (Game.PAUSE_LOCK) {
                        startPause=System.currentTimeMillis();
                        try {
                            if (Game.isPause)
                                Game.PAUSE_LOCK.wait();

                        } catch (InterruptedException e) {
                            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                        }
                        timePause=System.currentTimeMillis()-startPause;
                    }

                        currentPath.add(temp.remove(0));



                    Field field=Game.getFieldAt(f.getX(),f.getY());

                    if (field.getObjectOfField() instanceof Diamond){
                        numberOfDiamonds++;
                        Game.pickUpDiamond(f.getX(),f.getY());
                        Game.removeDiamond.add(field);
                    }


                    if (!(Game.game[f.getX()][f.getY()].getObjectOfField() instanceof Figure)) {
                        Game.putFigure.add( new Pair<>(f, this));//gui

                        Game.game[f.getX()][f.getY()].setObjectOfField(this);
                    }


                        try {
                            Thread.sleep(FIGURE_SLEEP); // move every 1 second
                        } catch (InterruptedException e) {
                            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                        }

                        if ( (currentPath.size() != lastField && this.equals(Game.game[f.getX()][f.getY()].getObjectOfField()) ) || (currentPath.size()==path.size()) ) {
                            Game.removeFigure.add(f);//gui
                            Game.game[f.getX()][f.getY()].setObjectOfField(null);


                        }
                        if(currentPath.size()==path.size()){
                            try {

                                Thread.sleep(FIGURE_SLEEP);
                            } catch (InterruptedException e) {
                                Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                            }
                        }



                }
                long end=System.currentTimeMillis();
                timeOfMoving+=(end-begin)-timePause;
                currentTime=Game.timeToString(timeOfMoving);
                LOCK.notify();
            }

            }

            isFigureEnd = true;


    }

    public static int getIndexNextField(Field currentField,ArrayList<Field> path){
        int element=0;
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).getValue() == currentField.getValue()) {
                element = i + 1;
                break;
            }
        }
        return element;
    }

    //poruka jedan potez
    public String oneMoveMessage(int numberOfSteps, int firstField, int lastField){
        return "Na potezu je igrac " + Game.currentPlayer.getPlayerName() + ", figura " + this.getFigureName() + " prelazi " + numberOfSteps +", sa polja " + firstField + " na polje " + lastField + ".";


    }



}







