module org.lenden {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari;

    opens org.lenden to javafx.fxml;
    opens org.lenden.model to javafx.base;

    exports org.lenden;
}
