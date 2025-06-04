module hr.javafx.fleetmanagementandmaintanancescheadule {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;


    opens hr.javafx.fleetmanagementandmaintanancescheadule to javafx.fxml;
    exports hr.javafx.fleetmanagementandmaintanancescheadule;
    exports hr.javafx.fleetmanagementandmaintanancescheadule.controller;
    opens hr.javafx.fleetmanagementandmaintanancescheadule.controller to javafx.fxml;
}