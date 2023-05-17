package models.cards;

import app.DiamondCircle;

import javafx.scene.image.Image;
import models.Game;
import models.figures.Figure;
import models.figures.HoveringFigure;
import models.map.Field;


import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static models.Game.*;

public class SpecialCard extends Card {

    public static String SPECIAL_CARD_PATH = Card.IMAGE_PATH + "SpecialCard.png";
    int numberOfHole;

    public Image readImage() {
        return new Image(Objects.requireNonNull(SpecialCard.class.getResourceAsStream(SPECIAL_CARD_PATH)));
    }

    //odredjivanje pozicija rupa
    public ArrayList<Field> putHolePosition(int matrixDimension) {
        ArrayList<Field> path = pathField(loadPath(matrixDimension), matrixField(matrixDimension));
        ArrayList<Field> holePosition = new ArrayList<>();
        numberOfHole = Game.randomNumber(MAX_NUMBERS_OF_HOLE, MIN_NUMBERS_OF_HOLE);
        Game.currentMessage="Postavljeni broj rupa je " + numberOfHole + ".";
        ArrayList<Integer> as=new ArrayList<>();
        while (numberOfHole > 0) {
            int position = randomNumber(path.size(), 0);
            if(!as.contains(position)){
                as.add(position);
                holePosition.add(path.get(position));
                numberOfHole--;
            }

        }
        return holePosition;
    }

    //pozicioniranje rupa u matricu
    public void setHoleOnMatrix()  {

        ArrayList<Field> holesPosition=putHolePosition(matrixDimension);
        putHole.addAll(holesPosition);//gui
        ArrayList<Field> figuresForeRemove=new ArrayList<>();

        for (Field f:holesPosition){
            if(Game.isGameOver())
                break;
            else {

                if (game[f.getX()][f.getY()].getObjectOfField() instanceof Figure && !(game[f.getX()][f.getY()].getObjectOfField() instanceof HoveringFigure)) {

                    Figure figure = (Figure) game[f.getX()][f.getY()].getObjectOfField();
                    figure.setFigureEnd(true);
                    game[f.getX()][f.getY()].setObjectOfField(null);
                    figuresForeRemove.add(f);


                }
            }
        }

        try {
            Thread.sleep(SLEEP_TIME);
        }
        catch (InterruptedException e){
            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);
        }
        removeFigure.addAll(figuresForeRemove);//gui
        removeHole.addAll(holesPosition);//gui

        try {
            Thread.sleep(SLEEP_TIME);
        }
        catch (InterruptedException e){
            Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);
        }


    }


}
