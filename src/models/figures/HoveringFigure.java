package models.figures;

import models.figures.Figure;

public class HoveringFigure  extends Figure {
    private final String mark="H";

    public HoveringFigure(Color color,int typeOfMoving){
        super(color,typeOfMoving);
    }

    public HoveringFigure(Color color){
        super(color);
    }

    @Override
    public String getMark() {
        return mark;
    }
}
