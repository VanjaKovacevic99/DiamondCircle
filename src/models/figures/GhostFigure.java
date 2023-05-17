package models.figures;

import app.DiamondCircle;
import elements.Diamond;
import models.Game;
import models.map.Field;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GhostFigure extends Thread{
    public ArrayList<Field> diamondsPosition;
    public static int MIN_NUMBER_OF_DIAMONDS=2;

    public static int TIME_OF_SLEEPING_THREAD=5_000;

    private int matrixDimension;
   private ArrayList<Field> path;

    public GhostFigure(int matrixDimension){
        this.matrixDimension=matrixDimension;
        putDiamonds();
    }

    //postavljanje dijamanata
    public void putDiamonds() {
        diamondsPosition=new ArrayList<>();
        ArrayList<Field> positions=new ArrayList<>();
        path = Game.pathField(Game.loadPath(matrixDimension), Game.matrixField(matrixDimension));
        int numberOfDiamonds = Game.randomNumber(matrixDimension,MIN_NUMBER_OF_DIAMONDS);
        ArrayList<Integer> as=new ArrayList<>();
        while (numberOfDiamonds > 0) {
            int position = Game.randomNumber(path.size(),0);
                if(!as.contains(position)){
                    as.add(position);
                positions.add(path.get(position));
                numberOfDiamonds--;
                }
            }

        this.diamondsPosition=positions;
    }

    @Override
    public void run(){
        while (!Game.isGameOver()){

            for(int i=0;i<matrixDimension-1;i++)
                for (int j=0;j<matrixDimension-1;j++){
                    if(Game.game[i][j].getObjectOfField() instanceof Diamond)
                        Game.game[i][j].setObjectOfField(null);

                    }
            if (Game.isGameOver())
                break;


            for (Field f:diamondsPosition){
                synchronized (Game.PAUSE_LOCK) {
                    try {
                        if (Game.isPause)
                            Game.PAUSE_LOCK.wait();
                    } catch (InterruptedException e) {
                        Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING, e.fillInStackTrace().toString(), e);
                    }
                }
                if(Game.game[f.getX()][f.getY()].getObjectOfField()!=null){
                    while (Game.game[f.getX()][f.getY()].getObjectOfField()!=null) {
                        if(f.getValue()==path.get(path.size()-1).getValue()) {
                            break;
                        }
                        else {
                            int i = Figure.getIndexNextField(f, path);
                            f=path.get(i);
                        }
                    }
                }

                if(Game.game[f.getX()][f.getY()].getObjectOfField()==null){
                    Game.game[f.getX()][f.getY()].setObjectOfField(new Diamond());
               }




                }

            try {
                    Thread.sleep(TIME_OF_SLEEPING_THREAD);
                } catch (InterruptedException e) {
                    Logger.getLogger(DiamondCircle.LOGGER_NAME).log(Level.WARNING,e.fillInStackTrace().toString(),e);
                }

            putDiamonds();
        }

    }




}
