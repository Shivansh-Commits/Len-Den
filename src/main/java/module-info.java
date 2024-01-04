module org.lenden {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari;
    requires java.desktop;

    opens org.lenden.model to javafx.base;

    exports org.lenden;
    opens org.lenden to javafx.base, javafx.fxml;
}
