import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;

// Finansal hedefleri görüntüleme, ekleme, güncelleme ve silme ekranı
public class FinancialGoalsScreen {

    public static VBox getView(IUser currentUser) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: #f9f9f9;");

        Label title = new Label("🎯 Finansal Hedefler");

        // Hedef verilerini veritabanından al
        FinancialGoalDAO goalDAO = new FinancialGoalDAO();
        ObservableList<FinancialGoal> goals = FXCollections.observableArrayList(
                goalDAO.getAllByUserId(currentUser.getId())
        );

        // Giriş alanları
        TextField nameField = new TextField();
        TextField targetAmountField = new TextField();
        TextField currentAmountField = new TextField();
        DatePicker datePicker = new DatePicker(LocalDate.now().plusMonths(1));

        // İşlem butonları
        Button addButton = new Button("➕ Ekle");
        Button updateButton = new Button("✏️ Güncelle");
        Button deleteButton = new Button("🗑️ Sil");

        // Giriş alanlarını içeren üst panel
        HBox formInputs = new HBox(10, nameField, targetAmountField, currentAmountField, datePicker, addButton, updateButton, deleteButton);
        formInputs.setAlignment(Pos.CENTER);
        formInputs.setPadding(new Insets(10));

        // Hedef tablosu
        TableView<FinancialGoal> table = new TableView<>(goals);

        // Sütunlar
        TableColumn<FinancialGoal, String> nameCol = new TableColumn<>("Hedef");
        nameCol.setCellValueFactory(cell -> cell.getValue().goalNameProperty());

        TableColumn<FinancialGoal, Double> targetAmountCol = new TableColumn<>("Hedef Tutar");
        targetAmountCol.setCellValueFactory(cell -> cell.getValue().targetAmountProperty().asObject());

        TableColumn<FinancialGoal, Double> currentAmountCol = new TableColumn<>("Mevcut");
        currentAmountCol.setCellValueFactory(cell -> cell.getValue().currentAmountProperty().asObject());

        TableColumn<FinancialGoal, LocalDate> dateCol = new TableColumn<>("Hedef Tarihi");
        dateCol.setCellValueFactory(cell -> cell.getValue().targetDateProperty());

        TableColumn<FinancialGoal, Double> progressCol = new TableColumn<>("İlerleme (%)");
        progressCol.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getProgress() * 100));

        TableColumn<FinancialGoal, Boolean> completedCol = new TableColumn<>("Tamamlandı");
        completedCol.setCellValueFactory(cell -> cell.getValue().completedProperty());
        completedCol.setCellFactory(CheckBoxTableCell.forTableColumn(completedCol));

        table.getColumns().addAll(nameCol, targetAmountCol, currentAmountCol, dateCol, progressCol, completedCol);

        // Ekle butonu
        addButton.setOnAction(e -> {
            FinancialGoalForm.showForm((Stage) root.getScene().getWindow(), null, goal -> {
                if (goalDAO.add(goal, currentUser.getId())) {
                    goals.setAll(goalDAO.getAllByUserId(currentUser.getId()));
                    table.refresh();
                }
            });
        });

        // Güncelle butonu
        updateButton.setOnAction(e -> {
            FinancialGoal selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                FinancialGoalForm.showForm((Stage) root.getScene().getWindow(), selected, updated -> {
                    goalDAO.update(updated, currentUser.getId());
                    goals.setAll(goalDAO.getAllByUserId(currentUser.getId()));
                    table.refresh();
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setHeaderText("ℹ️ Güncelleme İşlemi");
                alert.setContentText("Güncellemek için bir hedef seçin.");
                alert.showAndWait();
            }
        });

        // Silme butonu
        deleteButton.setOnAction(e -> {
            FinancialGoal selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Onay");
                confirm.setHeaderText("❓ Hedef Silme İşlemi");
                confirm.setContentText("Bu hedef silinsin mi?");
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        goalDAO.delete(selected.getGoalName(), currentUser.getId());
                        goals.setAll(goalDAO.getAllByUserId(currentUser.getId()));
                        table.refresh();
                    }
                });
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setHeaderText("ℹ️ Silme İşlemi");
                alert.setContentText("Silmek için bir hedef seçin.");
                alert.showAndWait();
            }
        });

        // Bileşenleri yerleştir
        root.getChildren().addAll(title, formInputs, table);
        return root;
    }
}
