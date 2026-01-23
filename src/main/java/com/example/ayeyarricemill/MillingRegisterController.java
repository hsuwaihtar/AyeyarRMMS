package com.example.ayeyarricemill;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class MillingRegisterController {

    // --- FXML UI Components ---
    @FXML private Pane step1Container;
    @FXML private Pane step2Container;
    @FXML private Pane step3Container;

    @FXML private ComboBox<PaddyPurchase> comboVoucherNo;
    @FXML private ComboBox<Warehouse> comboTargetWarehouse;
    @FXML private Button btnOkay, btnCalculate, btnConfirmProduction;
    @FXML private TextField txtHeadRice, txtBrokenRice, txtBrokenBran, txtBran;
    @FXML private Label lblSourceWarehouse, lblPaddyType, lblQtyMilled, lblStatus;
    @FXML private Label lblTotalOutputs, lblFinalYield, lblCurrentStock, lblMaxCapacity, lblSpaceStatus;

    // --- Variables ---
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final String BASE_URL = "http://localhost:9090/api";

    @FXML
    public void initialize() {
        setupStepVisibility();
        setupComboBoxes();
        loadInitialData();

        // --- Button Events ---
        btnOkay.setOnAction(e -> handleOkAction());
        btnCalculate.setOnAction(e -> performCalculation());
        btnConfirmProduction.setOnAction(e -> handleFinishMillingAction());
    }

    private void setupStepVisibility() {
        // Managed Property ကို Visible နဲ့ ချိတ်ထားမှ ပျောက်နေရင် နေရာမယူမှာ ဖြစ်ပါတယ်
        step2Container.managedProperty().bind(step2Container.visibleProperty());
        step3Container.managedProperty().bind(step3Container.visibleProperty());

        // စစချင်းမှာ Step 1 ပဲ ပြထားမယ်
        step1Container.setVisible(true);
        step2Container.setVisible(false);
        step3Container.setVisible(false);
    }

    private void handleOkAction() {
        if (comboVoucherNo.getValue() != null) {
            populateVoucherInfo();
            // OK နှိပ်ရင် အဆင့် ၂ (အလယ်ကွက်) ကို ဖော်မယ်
            step2Container.setVisible(true);
        } else {
            showError("Choose voucher");
        }
    }

    private void handleFinishMillingAction() {
        // Finish Milling နှိပ်ရင် ညာဘက်က Stock အကွက်ကို ဖော်မယ်
        step3Container.setVisible(true);

        // Data သိမ်းမယ့် Function ကို ဒီမှာ ခေါ်နိုင်ပါတယ်
        // saveMillingRecord();
    }

    private void setupComboBoxes() {
        comboVoucherNo.setConverter(new StringConverter<>() {
            @Override public String toString(PaddyPurchase object) { return object == null ? "" : object.getBatchNo(); }
            @Override public PaddyPurchase fromString(String string) { return null; }
        });
        comboTargetWarehouse.setConverter(new StringConverter<>() {
            @Override public String toString(Warehouse object) { return object == null ? "" : object.getName(); }
            @Override public Warehouse fromString(String string) { return null; }
        });
    }

    private void loadInitialData() {
        // Paddy Purchases ဆွဲယူခြင်း
        HttpRequest purchaseReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/paddy_purchases"))
                .GET().build();

        httpClient.sendAsync(purchaseReq, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        List<PaddyPurchase> all = gson.fromJson(response.body(), new TypeToken<List<PaddyPurchase>>(){}.getType());
                        List<PaddyPurchase> stockOnly = all.stream()
                                .filter(p -> "Stock".equalsIgnoreCase(p.getStatus()))
                                .collect(Collectors.toList());
                        Platform.runLater(() -> comboVoucherNo.setItems(FXCollections.observableArrayList(stockOnly)));
                    }
                });

        // Warehouse ဆွဲယူခြင်း
        HttpRequest warehouseReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/warehouses"))
                .GET().build();

        httpClient.sendAsync(warehouseReq, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        List<Warehouse> all = gson.fromJson(response.body(), new TypeToken<List<Warehouse>>(){}.getType());
                        List<Warehouse> goodOnly = all.stream()
                                .filter(w -> "Good".equalsIgnoreCase(w.getType()))
                                .collect(Collectors.toList());
                        Platform.runLater(() -> comboTargetWarehouse.setItems(FXCollections.observableArrayList(goodOnly)));
                    }
                });
    }

    private void loadData() {
        // Load Paddy Purchases (Stock ဖြစ်နေတဲ့ဟာတွေပဲ ယူမယ်)
        HttpRequest purchaseReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/paddy_purchases"))
                .GET().build();

        httpClient.sendAsync(purchaseReq, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        List<PaddyPurchase> all = gson.fromJson(response.body(), new TypeToken<List<PaddyPurchase>>(){}.getType());
                        List<PaddyPurchase> stockOnly = all.stream()
                                .filter(p -> "Stock".equalsIgnoreCase(p.getStatus()))
                                .collect(Collectors.toList());
                        Platform.runLater(() -> comboVoucherNo.setItems(FXCollections.observableArrayList(stockOnly)));
                    }
                });

        // Load Warehouses (Good - ဆန်ထွက်ကုန်ထည့်မယ့် ဂိုဒေါင်တွေပဲ ယူမယ်)
        HttpRequest warehouseReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/warehouses"))
                .GET().build();

        httpClient.sendAsync(warehouseReq, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        List<Warehouse> all = gson.fromJson(response.body(), new TypeToken<List<Warehouse>>(){}.getType());
                        List<Warehouse> goodOnly = all.stream()
                                .filter(w -> "Good".equalsIgnoreCase(w.getType()))
                                .collect(Collectors.toList());
                        Platform.runLater(() -> comboTargetWarehouse.setItems(FXCollections.observableArrayList(goodOnly)));
                    }
                });
    }

    private void populateVoucherInfo() {
        PaddyPurchase selected = comboVoucherNo.getValue();
        if (selected != null) {
            lblSourceWarehouse.setText(selected.getWarehouseName());
            lblPaddyType.setText(selected.getPaddyType());
            lblQtyMilled.setText(selected.getNetWeight() + " Tins");
            lblStatus.setText("Pending");
        }
    }

    private void performCalculation() {
        try {
            double hr = parse(txtHeadRice.getText());
            double br = parse(txtBrokenRice.getText());
            double bb = parse(txtBrokenBran.getText());
            double b = parse(txtBran.getText());
            double totalOutput = hr + br + bb + b;
            lblTotalOutputs.setText(String.format("%.2f အိတ်", totalOutput));

            double input = parse(lblQtyMilled.getText().replace(" တင်း", ""));
            if (input > 0) {
                double yield = (totalOutput / input) * 100;
                lblFinalYield.setText(String.format("%.2f%%", yield));
            }
        } catch (Exception e) {
            showError("တွက်ချက်မှု မှားယွင်းနေပါသည်။");
        }
    }

    private void saveMillingRecord() {
        PaddyPurchase batch = comboVoucherNo.getValue();
        Warehouse targetW = comboTargetWarehouse.getValue();
        if (batch == null || targetW == null) {
            showError("Voucher နှင့် ဂိုဒေါင်ကို အရင်ရွေးချယ်ပါ။");
            return;
        }

        // ၁။ Voucher Status ပြောင်းဖို့ Request (Simple Patch string format)
        HttpRequest updateStatusReq = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/paddy_purchases/" + batch.id + "/status?newStatus=Milled"))
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        httpClient.sendAsync(updateStatusReq, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    if (res.statusCode() == 200) {
                        // ၂။ Inventory Item အဖြစ် Head Rice ကို ထည့်ခြင်း (ဥပမာတစ်ခုသာ)
                        postInventory(batch, targetW);
                    }
                });
    }

    private void postInventory(PaddyPurchase batch, Warehouse targetW) {
        InventoryItem item = new InventoryItem();
        item.itemName = batch.getPaddyType() + " (ဆန်ချော)";
        item.quantity = (int) parse(txtHeadRice.getText());
        item.unit = "Bag";
        item.status = "Finished Good";

        String jsonBody = gson.toJson(item);
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/inventory-add/" + targetW.id))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    Platform.runLater(() -> {
                        showSuccess("အောင်မြင်စွာ သိမ်းဆည်းပြီးပါပြီ။");
                        clearForm();
                    });
                });
    }

    private void updateSpaceStatus(Warehouse w) {
        int current = w.currentStock != null ? w.currentStock : 0;
        if (w.capacity != null && current >= w.capacity) {
            lblSpaceStatus.setText("not available");
            lblSpaceStatus.setStyle("-fx-text-fill: red;");
        } else {
            lblSpaceStatus.setText("available");
            lblSpaceStatus.setStyle("-fx-text-fill: green;");
        }
    }

    private double parse(String s) {
        try { return Double.parseDouble(s.replaceAll("[^0-9.]", "")); } catch (Exception e) { return 0; }
    }

    private void clearForm() {
        txtHeadRice.clear(); txtBrokenRice.clear(); txtBrokenBran.clear(); txtBran.clear();
        loadData();
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, msg);
            a.show();
        });
    }

    private void showSuccess(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
            a.show();
        });
    }

    private void updateSpaceLabel(Warehouse w) {
        int capacity = w.capacity != null ? w.capacity : 0;
        int current = w.currentStock != null ? w.currentStock : 0;
        if (capacity > 0 && current >= capacity) {
            lblSpaceStatus.setText("ဂိုဒေါင်ပြည့်နေသည်");
            lblSpaceStatus.setStyle("-fx-text-fill: red;");
        } else {
            lblSpaceStatus.setText("နေရာလွတ်ရှိပါသည်");
            lblSpaceStatus.setStyle("-fx-text-fill: green;");
        }
    }



    // --- Static Inner Classes (Backend Models) ---

    public static class PaddyPurchase {
        private String id;
        private String batchNo;
        private String paddyType;
        private String warehouseName;
        private Double netWeight;
        private String status;
        private Double netPrice;

        public String getBatchNo() { return batchNo; }
        public String getPaddyType() { return paddyType; }
        public String getWarehouseName() { return warehouseName; }
        public Double getNetWeight() { return netWeight; }
        public String getStatus() { return status; }
    }

    public static class Warehouse {
        private String id;
        private String name;
        private String type;
        private Integer currentStock;
        private Integer capacity;

        public String getName() { return name; }
        public String getType() { return type; }
    }

    public static class InventoryItem {
        private String id;
        private String itemName;
        private Integer quantity;
        private String unit;
        private String status;
        private String remark;
    }
}