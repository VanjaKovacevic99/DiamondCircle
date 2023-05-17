package models.figures;

import models.figures.Figure;

public class OrdinaryFigure extends Figure {
    private final String mark="O";

    public OrdinaryFigure(Color color,int typeOfMoving){
        super(color,typeOfMoving);

    }

    public OrdinaryFigure(Color color){
        super(color);}

    @Override
    public String getMark() {
        return mark;
    }
}
