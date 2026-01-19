package com.example.ayeyarricemill;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static javafx.scene.layout.AnchorPane.setLeftAnchor;
import static javafx.scene.layout.AnchorPane.setTopAnchor;

public class GoodPriceController {
    @FXML private AnchorPane MPRoot;
    @FXML private AnchorPane MPModal;
    @FXML private ComboBox<String> typeCombo;
    @FXML private TableView<GoodPriceModel> priceTable;
    @FXML private TableColumn<GoodPriceModel, String> colNo;
    @FXML private TableColumn<GoodPriceModel, String> colType;
    @FXML private TableColumn<GoodPriceModel, Double> colPrice;
    @FXML private TableColumn<GoodPriceModel, String> colCreated;
    @FXML private TableColumn<GoodPriceModel, String> colCreatedBy;
    @FXML private TableColumn<GoodPriceModel, String> colUpdated;
    @FXML private TableColumn<GoodPriceModel, String> colUpdatedBy;
    @FXML private TableColumn<GoodPriceModel, Void> colAction;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final ObservableList<GoodPriceModel> priceList = FXCollections.observableArrayList();

    public static String loggedInUsername = "Admin";

    private String currentUrl = "http://localhost:9090/api/rice_price";
    private String currentCategory = "Rice";

    @FXML
    public void initialize() {
        if(MPRoot != null && MPModal != null){
            MPRoot.widthProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newleftAnchor = (newNumber.doubleValue() - MPModal.getPrefWidth())/ 2.0;
                    setLeftAnchor(MPModal,newleftAnchor);
                }
            });

            MPRoot.heightProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldNumber, Number newNumber) {
                    double newTopAnchor = (newNumber.doubleValue() - MPModal.getPrefHeight()) / 2.0;
                    setTopAnchor(MPModal, newTopAnchor);
                }
            });

            setLeftAnchor(MPModal, (MPRoot.getWidth() - MPModal.getPrefWidth() ) / 2.0);
            setTopAnchor(MPModal, (MPRoot.getHeight() - MPModal.getPrefHeight()) /2.0);

        }

        setupComboBox();
        setupTable();
        loadDataFromBackend();

        ObservableList<String> options = FXCollections.observableArrayList(
                "Rice",
                "Broken Rice",
                "Others"
        );
        typeCombo.setItems(options);
        typeCombo.setValue("Rice"); // default value

    }

    private void setupComboBox() {
        ObservableList<String> options = FXCollections.observableArrayList("Rice", "Broken Rice", "Others");
        typeCombo.setItems(options);
        typeCombo.setValue("Rice"); // Default Value

        // ComboBox ပြောင်းလဲပါက URL ပြောင်းပြီး Data ပြန်ဆွဲမည်
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                currentCategory = newVal;
                updateUrlAndColumns(newVal);
                loadDataFromBackend();
            }
        });
    }

    private void updateUrlAndColumns(String category) {
        switch (category) {
            case "Rice":
                currentUrl = "http://localhost:9090/api/rice_price";
                colType.setText("Rice Type");
                break;
            case "Broken Rice":
                currentUrl = "http://localhost:9090/api/brokenRice_price";
                colType.setText("Rice Type");
                break;
            case "Others":
                currentUrl = "http://localhost:9090/api/other_price";
                colType.setText("Item Type");
                break;
        }
    }

    private void setupTable() {
        // No Column (1, 2, 3...)
        colNo.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : String.valueOf(getIndex() + 1));
            }
        });

        colType.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTypeName()));
        colPrice.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPrice()));
        colCreatedBy.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getCreatedBy()));
        colCreated.setCellValueFactory(cell -> formatTimestamp(cell.getValue().getCreatedDate()));
        colUpdatedBy.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getUpdatedBy()));
        colUpdated.setCellValueFactory(cell -> formatTimestamp(cell.getValue().getUpdatedDate()));

        setupActionButtons();
        priceTable.setItems(priceList);
    }

    private SimpleStringProperty formatTimestamp(String timestamp){
        // ပြင်ဆင်ထားသော Logic: timestamp သည် null ဖြစ်မှသာ "-" ပြမည်
        if(timestamp == null || timestamp.isEmpty() || timestamp.equals("null")) {
            return new SimpleStringProperty("-");
        }
        try {
            if (timestamp.contains("T")) {
                return new SimpleStringProperty(timestamp.split("T")[0]);
            }
            return new SimpleStringProperty(timestamp);
        } catch (Exception e) {
            return new SimpleStringProperty("-");
        }
    }

    private void setupActionButtons() {
        colAction.setCellFactory(column -> new TableCell<>() {
            private final Button editBtn = new Button("Update");
            private final Button deleteBtn = new Button("Delete");
            private final HBox container = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand;");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");

                editBtn.setOnAction(event -> {
                    GoodPriceModel data = getTableView().getItems().get(getIndex());
                    if (data != null) showDialog(data);
                });

                deleteBtn.setOnAction(event -> {
                    GoodPriceModel data = getTableView().getItems().get(getIndex());
                    if (data != null) handleDelete(data);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void loadDataFromBackend() {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(currentUrl)).GET().build();
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            priceList.clear();
                            // Backend မှ Data ပုံစံမတူသော်လည်း (riceType vs type) Manual Map လုပ်၍ Table ထဲထည့်မည်
                            JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
                            for (JsonElement element : jsonArray) {
                                priceList.add(mapJsonToModel(element.getAsJsonObject()));
                            }
                        });
                    }
                });
    }

//    private GoodPriceModel mapJsonToModel(JsonObject json) {
//        GoodPriceModel model = new GoodPriceModel();
//        model.setId(json.has("id") ? json.get("id").getAsString() : null);
//
//        // Rice/Broken Rice တွင် "riceType", Others တွင် "type" ဟုပါရှိသည်
//        if (json.has("riceType")) {
//            model.setTypeName(json.get("riceType").getAsString());
//        } else if (json.has("type") || json.has("Type")) { // OtherPrice အတွက်
//            model.setTypeName(json.has("type") ? json.get("type").getAsString() : json.get("Type").getAsString());
//        }
//
//        model.setPrice(json.has("price") ? json.get("price").getAsDouble() : 0.0);
//        if (json.has("createdBy")) model.setCreatedBy(json.get("createdBy").getAsString());
//        if (json.has("createdDate")) model.setCreatedDate(json.get("createdDate").getAsString());
//        if (json.has("updatedBy")) model.setUpdatedBy(json.get("updatedBy").getAsString());
//        if (json.has("updatedDate")) model.setUpdatedDate(json.get("updatedDate").getAsString());
//
//        return model;
//    }

    private GoodPriceModel mapJsonToModel(JsonObject json) {
        GoodPriceModel model = new GoodPriceModel();
        model.setId(safeGetString(json, "id"));

        // Rice/Broken Rice တွင် "riceType", Others တွင် "type" ဟုပါရှိသည်
        if (json.has("riceType") && !json.get("riceType").isJsonNull()) {
            model.setTypeName(json.get("riceType").getAsString());
        } else if (json.has("type") && !json.get("type").isJsonNull()) {
            model.setTypeName(json.get("type").getAsString());
        } else if (json.has("Type") && !json.get("Type").isJsonNull()) {
            model.setTypeName(json.get("Type").getAsString());
        }

        model.setPrice(json.has("price") && !json.get("price").isJsonNull() ? json.get("price").getAsDouble() : 0.0);

        model.setCreatedBy(safeGetString(json, "createdBy"));
        model.setCreatedDate(safeGetString(json, "createdDate"));
        model.setUpdatedBy(safeGetString(json, "updatedBy"));
        model.setUpdatedDate(safeGetString(json, "updatedDate"));

        return model;
    }

    // Helper method to handle JsonNull safely
    private String safeGetString(JsonObject json, String key) {
        if (json.has(key) && !json.get(key).isJsonNull()) {
            return json.get(key).getAsString();
        }
        return "";
    }


    @FXML
    private void handleAddNew() {
        showDialog(null);
    }

    private void handleDelete(GoodPriceModel selected) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure want to delete?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(currentUrl + "/" + selected.getId()))
                        .DELETE()
                        .build();
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(r -> { if (r.statusCode() == 200) loadDataFromBackend(); });
            }
        });
    }

    private void showDialog(GoodPriceModel existingData) {
        Dialog<GoodPriceModel> dialog = new Dialog<>();
        dialog.setTitle(existingData == null ? "New Entry (" + currentCategory + ")" : "Edit Entry");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField typeField = new TextField();
        TextField priceField = new TextField();

        if (existingData != null) {
            typeField.setText(existingData.getTypeName());
            priceField.setText(String.valueOf(existingData.getPrice()));
        }

        grid.add(new Label(currentCategory.equals("Others") ? "Item Name:" : "Rice Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Price:"), 0, 1);
        grid.add(priceField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                GoodPriceModel model = existingData == null ? new GoodPriceModel() : existingData;
                model.setTypeName(typeField.getText());
                try { model.setPrice(Double.parseDouble(priceField.getText())); } catch (Exception e) { model.setPrice(0.0); }

                if (existingData == null) model.setCreatedBy(loggedInUsername);
                else model.setUpdatedBy(loggedInUsername);

                return model;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(data -> {
            String method = existingData == null ? "POST" : "PUT";
            String url = existingData == null ? currentUrl : currentUrl + "/" + data.getId();
            saveToBackend(data, method, url);
        });
    }

    private void saveToBackend(GoodPriceModel data, String method, String url) {
        JsonObject json = new JsonObject();

        // Backend လိုအပ်ချက်အရ Field name ပြောင်းလဲပေးပို့ခြင်း
        if (currentCategory.equals("Others")) {
            json.addProperty("type", data.getTypeName()); // OtherPrice အတွက်
        } else {
            json.addProperty("riceType", data.getTypeName()); // Rice & Broken Rice အတွက်
        }

        json.addProperty("price", data.getPrice());

        if (method.equals("POST")) json.addProperty("createdBy", data.getCreatedBy());
        else json.addProperty("updatedBy", data.getUpdatedBy());

        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json");
        HttpRequest request = method.equals("POST")
                ? builder.POST(HttpRequest.BodyPublishers.ofString(json.toString())).build()
                : builder.PUT(HttpRequest.BodyPublishers.ofString(json.toString())).build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    if (res.statusCode() == 200 || res.statusCode() == 201) {
                        loadDataFromBackend();
                    } else {
                        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Error: " + res.statusCode()).show());
                    }
                });
    }

    public static class GoodPriceModel {
        private String id;
        private String typeName; // Can correspond to 'riceType' or 'type'
        private Double price;
        private String createdBy;
        private String createdDate;
        private String updatedBy;
        private String updatedDate;

        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTypeName() { return typeName; }
        public void setTypeName(String typeName) { this.typeName = typeName; }
        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
        public String getCreatedDate() { return createdDate; }
        public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
        public String getUpdatedBy() { return updatedBy; }
        public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
        public String getUpdatedDate() { return updatedDate; }
        public void setUpdatedDate(String updatedDate) { this.updatedDate = updatedDate; }
    }

}

