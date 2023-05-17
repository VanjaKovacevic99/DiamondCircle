package elements;

import javafx.scene.image.Image;

public class Diamond {
    private static final String DIAMOND_PATH="/image/diamond.jpg";

    public static Image readImage(){
        return new Image(Diamond.class.getResourceAsStream(DIAMOND_PATH));
    }
}
