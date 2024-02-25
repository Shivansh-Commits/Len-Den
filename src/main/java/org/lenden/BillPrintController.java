package org.lenden;

import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import org.lenden.model.Bill;
import org.lenden.model.BillItems;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

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
            billText.append(String.format("%-25s %5d %10.2f %10.2f", item.getFoodItemName(), item.getFoodItemQuantity(), item.getFoodItemPrice(), (item.getFoodItemQuantity() * item.getFoodItemPrice()) )).append("\n");
        }

        // Add Sub-total price
        billText.append("-".repeat(50)).append("\n");
        billText.append(String.format("%30s %10.2f", "Sub-Total", bill.getSubTotal())).append("\n");

        // Add Discount
        billText.append(String.format("%30s %10.2f", "Discount (%)", bill.getDiscount() )).append("\n");

        // Add total price
        billText.append(String.format("%30s %10.2f", "Total", bill.getTotal() )).append("\n");

        // Add Tax Charges
        billText.append("-".repeat(50)).append("\n");
        billText.append(String.format("%30s %10.2f", "CGST (%)", bill.getCgst() )).append("\n");
        billText.append(String.format("%30s %10.2f", "SGST (%)", bill.getSgst() )).append("\n");
        billText.append(String.format("%30s %10.2f", "Service Charge (%)", bill.getServiceCharge() )).append("\n");

        // Add Grand total price
        billText.append("-".repeat(50)).append("\n");
        billText.append(String.format("%30s %10.2f", "Grand Total", bill.getGrandTotal())).append("\n");

        // Add footer
        billText.append("\n");
        billText.append(String.format("%35s", "Thank You!")).append("\n");

        //FOR THERMAL PRINTING
        // createBillContent();

        //FOR GENERATING PDF
        billLabel.setText((billText.toString()));

    }

    /**
     *
     * Contains actual printing logic.
     * Displays print window.
     * **/
    @FXML
    private void printBill()
    {
        // Get the scene associated with the print button
        Scene scene = billLabel.getScene();

        // Create a PrinterJob
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(scene.getWindow())) {
            boolean success = printerJob.printPage(billLabel);

            if (success) {
                printerJob.endJob();
            }
        }
    }

    @FXML
    private void closePreviewWindow(MouseEvent e) // NOT READY
    {
        Stage temp = (Stage) close.getScene().getWindow();
        temp.close();
    }


    //FOR THERMAL PRINTER
    private void createBillContent() {

        ArrayList<String> items = new ArrayList<>();
        items.add("Item 1: $10.00");
        items.add("Item 2: $15.50");
        items.add("Item 3: $5.25");
        double totalCost = 30.75;

        StringBuilder billBuilder = new StringBuilder();

        // Header
        billBuilder.append("************ Bill ************\n\n");

        // Items
        for (String item : items) {
            billBuilder.append(item).append("\n");
        }

        // Separator
        billBuilder.append("-----------------------------\n");

        // Total Cost
        billBuilder.append("Total Cost: $").append(totalCost).append("\n");

        printBill(billBuilder.toString());
    }

    private static void printBill(String billContent) {
        // Create a PrintRequestAttributeSet with desired print settings
        PrintRequestAttributeSet attributeSet = new HashPrintRequestAttributeSet();
        attributeSet.add(new Copies(1)); // Number of copies
        attributeSet.add(new MediaPrintableArea(0, 0, 80, 100, MediaPrintableArea.MM)); // Page size

        try {
            // Convert bill content to InputStream
            byte[] contentBytes = billContent.getBytes();
            InputStream inputStream = new ByteArrayInputStream(contentBytes);

            // Create a Doc
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
            Doc doc = new SimpleDoc(inputStream, flavor, null);

            // Get the default print service
            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();

            if (printService != null) {
                // Create a PrintJob
                DocPrintJob printJob = printService.createPrintJob();

                // Print the document
                printJob.print(doc, attributeSet);
            } else {
                System.out.println("No default print service found.");
            }
        } catch (PrintException e) {
            e.printStackTrace();
        }
    }



}
