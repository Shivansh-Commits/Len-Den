module org.lenden {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari; //DB CONNECTION POOL
    requires java.desktop;
    requires com.fasterxml.jackson.databind; //For binding String into Json string (used in Daoimpl)
    requires com.google.gson; //Used to display Variant data (JSON format) in table
    //requires com.machinezoo.sourceafis; // FINGERPRINT LIBRARY

    opens org.lenden.model to javafx.base;
    opens org.lenden to javafx.base, javafx.fxml;

    exports org.lenden;
}
