package models.cards;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.Objects;


public class OrdinaryCard extends Card{

    private int value;
    private static String[] IMAGE_NAME={"number1.png","number2.png","number3.png","number4.png"};

    public OrdinaryCard(int value) {
        this.value=value;
    }

    public int getValue() {
        return value;
    }

    public Image readImage(){
        Image image= null;
            if(value==1){
                image=  new Image(Objects.requireNonNull(OrdinaryCard.class.getResourceAsStream(IMAGE_PATH + IMAGE_NAME[0])));
            }
            else if (value==2){
                image= new Image(Objects.requireNonNull(OrdinaryCard.class.getResourceAsStream(IMAGE_PATH + IMAGE_NAME[1])));
            }
            else if (value==3){
                image= new Image(Objects.requireNonNull(OrdinaryCard.class.getResourceAsStream(IMAGE_PATH + IMAGE_NAME[2])));
            }
            else {
                image= new Image(Objects.requireNonNull(OrdinaryCard.class.getResourceAsStream(IMAGE_PATH + IMAGE_NAME[3])));
            }
            return image;

    }

    public static ImageView getImage() {
        ImageView imgView;
        String path = IMAGE_PATH + IMAGE_NAME[3];
        imgView = new ImageView(new File(path).toURI().toString());
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);
        return imgView;
    }


}
