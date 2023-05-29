package org.lenden;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.lenden.model.Bill;
import org.lenden.model.BillItems;

public class BillPrintController{

    @FXML
    Label billLabel;
    @FXML
    Button close;

    public void setPreviewBillValues(Bill bill)
    {
        StringBuilder billText = new StringBuilder();

        // Add header
        billText.append(String.format("%40s", bill.getOutletName())).append("\n");
        billText.append(String.format("%40s", bill.getOutletAddress())).append("\n");
        billText.append(String.format("%40s", bill.getPhone())).append("\n");
        billText.append(String.format("%35s %2s", "Bill No:" ,bill.getBillnumber())).append("\n");
        billText.append(String.format("%35s %2s", "Table No.:" ,bill.getTableNumber())).append("\n");
        billText.append("\n");

        // Add bill details
        billText.append(String.format("%-25s %5s %10s %10s", "Item", "Qty", "Price", "Total")).append("\n");
        billText.append("-".repeat(50)).append("\n");

        for (BillItems item : bill.getBillItems()) {
            billText.append(String.format("%-25s %5d %10d %10d", item.getFoodItemName(), item.getFoodItemQuantity(), item.getFoodItemPrice(), (item.getFoodItemQuantity() * item.getFoodItemPrice()) )).append("\n");
        }

        // Add Sub-total price
        billText.append("-".repeat(50)).append("\n");
        billText.append(String.format("%45s %10.2f", "Sub-Total", bill.getSubTotal())).append("\n");

        // Add Discount
        billText.append(String.format("%45s %10.2f", "Discount (%)", bill.getDiscount() )).append("\n");

        // Add total price
        billText.append(String.format("%45s %10.2f", "Total", bill.getTotal() )).append("\n");

        // Add Tax Charges
        billText.append("-".repeat(50)).append("\n");
        billText.append(String.format("%45s %10.2f", "CGST (%)", bill.getCgst() )).append("\n");
        billText.append(String.format("%45s %10.2f", "SGST (%)", bill.getSgst() )).append("\n");
        billText.append(String.format("%45s %10.2f", "Service Charge (%)", bill.getServiceCharge() )).append("\n");

        // Add Grand total price
        billText.append("-".repeat(50)).append("\n");
        billText.append(String.format("%45s %10.2f", "Grand Total", bill.getGrandTotal())).append("\n");

        // Add footer
        billText.append("\n");
        billText.append(String.format("%60s", "Thank You!")).append("\n");

        billLabel.setText((billText.toString()));
    }

    @FXML
    private void closePreviewWindow(MouseEvent e) // NOT READY
    {
        Stage temp = (Stage) close.getScene().getWindow();
        temp.close();
    }



}
