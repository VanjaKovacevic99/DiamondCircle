package models.map;

import models.figures.HoveringFigure;

public class Field {
    private int x;
    private int y;
    private int value;

    private Object objectOfField;

    public Field(){
        super();
    }

    public Field(int x, int y, int value, Object objectOfField){
        this.x=x;
        this.y=y;
        this.value=value;
        this.objectOfField=objectOfField;
    }

    public int getValue() {
        return value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Object getObjectOfField(){
        return objectOfField;
    }

    public void setObjectOfField(Object objectOfField) {
        this.objectOfField = objectOfField;
    }
}
