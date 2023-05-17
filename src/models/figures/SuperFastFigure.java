package models.figures;

import models.figures.Figure;

public class SuperFastFigure extends Figure {
    private final String mark="S";

    public SuperFastFigure(Color color, int typeOfMoving){
        super(color,typeOfMoving);
    }

    public SuperFastFigure(Color color){
        super(color);
    }

    @Override
    public void setTypeOfMoving(int typeOfMoving){
        super.setTypeOfMoving(2*typeOfMoving);
    }

    @Override
    public String getMark() {
        return mark;
    }
}
