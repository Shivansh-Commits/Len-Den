package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.lenden.dao.DaoImpl;
import org.lenden.model.Bill;
import java.net.URL;
import java.util.ResourceBundle;

public class SalesController implements Initializable
{
    @FXML
    BorderPane mainBorderPane;
    @FXML
    TableView<Bill> billsTable;

    DaoImpl daoImpl = new DaoImpl();
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        ObservableList<Bill> bills = FXCollections.observableArrayList();
        try {
            //Getting Menu Items FOR MENU TABLE
            bills = daoImpl.fetchAllBills();
        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }
        // Create a cell value factory for the Bill No. column
        TableColumn<Bill, Integer> billNoCol = new TableColumn<>("Bill No.");
        billNoCol.setCellValueFactory(new PropertyValueFactory<>("billnumber"));
        billNoCol.setPrefWidth(200);
        billNoCol.setStyle("-fx-alignment: CENTER;");
        billNoCol.setSortType(TableColumn.SortType.DESCENDING);

        // Create a cell value factory for the Date column
        TableColumn<Bill, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(170);

        // Create a cell value factory for the Grand Total column
        TableColumn<Bill, Double> grandTotalCol = new TableColumn<>("Grand Total");
        grandTotalCol.setCellValueFactory(new PropertyValueFactory<>("grandTotal"));
        grandTotalCol.setPrefWidth(200);
        grandTotalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Date column
        TableColumn<Bill, String> subTotalCol = new TableColumn<>("Sub-Total");
        subTotalCol.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        subTotalCol.setPrefWidth(170);
        subTotalCol.setStyle("-fx-alignment: CENTER;");

        // Create a cell value factory for the Mode Of Payment column
        TableColumn<Bill, String> modeOfPaymentCol = new TableColumn<>("Mode of Payment");
        modeOfPaymentCol.setCellValueFactory(new PropertyValueFactory<>("modeOfpayment"));
        modeOfPaymentCol.setPrefWidth(170);

        // Create a cell value factory for the Status column
        TableColumn<Bill, String> statusCol = new TableColumn<>("Order Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(170);
        statusCol.setStyle("-fx-alignment: CENTER;");


        // Set the cell value factories for the table columns
        billsTable.getColumns().setAll(billNoCol, dateCol ,grandTotalCol, subTotalCol , modeOfPaymentCol ,statusCol );
        billsTable.setItems(bills);
        billsTable.getSortOrder().add(billNoCol);
        billsTable.sort();

    }
}
