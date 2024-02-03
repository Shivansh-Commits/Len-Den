module org.lenden {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari; //DB CONNECTION POO:
    requires java.desktop;
    requires com.machinezoo.sourceafis;
    requires com.fasterxml.jackson.databind; // FINGERPRINT LIBRARY

    opens org.lenden.model to javafx.base;

    exports org.lenden;
    opens org.lenden to javafx.base, javafx.fxml;
}
