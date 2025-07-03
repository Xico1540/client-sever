module pt.estg.sd.alertops {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javax.servlet.api;
    requires static lombok;
    requires jdk.httpserver;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.sql;

    opens pt.estg.sd.alertops to javafx.fxml;
    exports pt.estg.sd.alertops;
    exports pt.estg.sd.alertops.components;
    opens pt.estg.sd.alertops.components to javafx.fxml;
    exports pt.estg.sd.alertops.utils;
    opens pt.estg.sd.alertops.utils to javafx.fxml;
    exports pt.estg.sd.alertops.server;
    opens pt.estg.sd.alertops.server to javafx.fxml;
}