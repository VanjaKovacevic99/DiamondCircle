package models;

import app.DiamondCircle;
import models.cards.OrdinaryCard;
import models.figures.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import static models.Game.*;


public class Player extends Thread {

    public final Object LOCK = new Object();
    private String playerName;

    public final ArrayList<Figure> figures = new ArrayList<>();
    private Figure currentFigure;
    private boolean isStarted=false;
    private boolean isEnd=false;


    private int currentNumberOfFigures;
    public static final int NUMBER_OF_FIGURES = 4;

    private int currentValue=0;

    public Player(){
        super();
    }
    public boolean isStarted(){
        return isStarted;
    }

    public boolean isEnd(){
        return isEnd;
    }

    private int numberOfDiamonds=0;

    public static ArrayList<Player> playersWithoutFigure = new ArrayList<>();


    public Player(String playerName, Color color) {
        this.playerName = playerName;
        generateFigure(color);
    }



    public ArrayList<Figure> getFigures() {
        return figures;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getColorFigures() {
        return figures.get(0).getColor().toString();
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setCurrentValue(int currentValue){
        this.currentValue=currentValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public Figure getCurrentFigure() {
        return currentFigure;
    }

    public void generateFigure(Color color) {


        int numberOfFigures = NUMBER_OF_FIGURES;

        while (numberOfFigures > 0) {
            int randNumber = Game.randomNumber(4, 1);
            if (randNumber == 1) {
                figures.add(new HoveringFigure(color));


            } else if (randNumber == 2) {
                figures.add(new OrdinaryFigure(color));


            } else if (randNumber==3){
                figures.add(new SuperFastFigure(color));
            }
            numberOfFigures--;
        }



    }


    @Override
    public void run() {
        isStarted = true;
        synchronized (LOCK) {
            while (currentNumberOfFigures != NUMBER_OF_FIGURES) {

               try {
                    LOCK.wait(); // wait for move

                } catch (InterruptedException e) {
                    Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                }

                currentFigure = figures.get(0);

                Iterator<Figure> iterator=figures.iterator();
               if (currentFigure.isFigureEnd()){
                   while (iterator.hasNext()){
                       currentFigure= iterator.next();
                       if (!currentFigure.isFigureEnd())
                       break;

                   }
                   }

               if (!currentFigure.isFigureEnd()) {


                   currentFigure.setTypeOfMoving(Game.currentValue);
                   if (!currentFigure.isFigureRun()) {

                       currentFigure.start();
                       try {
                           Thread.sleep(SLEEP_TIME/2);
                       } catch (InterruptedException e) {
                           Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                       }
                   }
                   synchronized (currentFigure.LOCK) {
                       currentFigure.LOCK.notify();
                       try {

                           currentFigure.LOCK.wait();
                       } catch (InterruptedException e) {
                           Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                       }
                   }


               }
               else {
                   currentNumberOfFigures=4;
               }

                LOCK.notify();
            }
        }

        isEnd = true;
    }


}

