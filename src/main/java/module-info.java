module es.ieslosmontecillos.appinformes {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires jasperreports;


    opens es.ieslosmontecillos.appinformes to javafx.fxml;
    exports es.ieslosmontecillos.appinformes;
}