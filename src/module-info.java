module Vanja_Kovacevic_1114_18_java_2022{
    requires javafx.controls;
    requires javafx.graphics;

    requires java.logging;
    requires javafx.fxml;
    requires java.desktop;
    requires jdk.xml.dom;

    opens app to javafx.fxml;
    exports app;
    opens controllers to javafx.fxml;
    exports controllers;



}