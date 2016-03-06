package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.StringConverter;
import sample.Settings;

/**
 * Start dialog class.
 */
public class StartDialog {
    static private String lastXCellsInput = "10";
    static private String lastYCellsInput = "10";
    static private String lastMinesCountInput = "20";

    Dialog<Settings> dialog = new Dialog<>();
    Settings settings;

    private boolean forceClose = false;

    private boolean validate(String xCellsInput, String yCellsInput, String minesCountInput) {
        try {
            int xCells = Integer.parseInt(xCellsInput);
            int yCells = Integer.parseInt(yCellsInput);
            int minesCount = Integer.parseInt(minesCountInput);

            if (Settings.validate(xCells, yCells, minesCount)) {
                lastXCellsInput = xCellsInput;
                lastYCellsInput = yCellsInput;
                lastMinesCountInput = minesCountInput;

                settings = new Settings(xCells, yCells, minesCount);
                return true;
            }

            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    StartDialog(String text, boolean showCancel) {
        // Initialize dialog window settings.
        dialog.setTitle("New game");
        dialog.setHeaderText(text);
        dialog.setResizable(false);
        dialog.setX(0);

        // Initialize dialog controls.
        Label xCellsLabel = new Label("X: ");
        Label yCellsLabel = new Label("Y: ");
        Label minesCountLabel = new Label("Mines count: ");
        Label errorLabel = new Label();
        errorLabel.setWrapText(true);
        errorLabel.setTextFill(Color.RED);
        errorLabel.setMinHeight(50);
        TextField xCellsField = new TextField(lastXCellsInput);
        TextField yCellsField = new TextField(lastYCellsInput);
        TextField minesCountField = new TextField(lastMinesCountInput);

        // Initialize dialog grid.
        GridPane gridPane = new GridPane();
        GridPane fieldsGrid = new GridPane();
        fieldsGrid.setVgap(10);
        fieldsGrid.setPrefWidth(400);
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(40);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(70);
        fieldsGrid.getColumnConstraints().addAll(column1, column2);

        ColumnConstraints column = new ColumnConstraints();
        column.setPercentWidth(100);
        gridPane.getColumnConstraints().addAll(column);

        fieldsGrid.add(xCellsLabel, 0, 0);
        fieldsGrid.add(xCellsField, 1, 0);
        fieldsGrid.add(yCellsLabel, 0, 1);
        fieldsGrid.add(yCellsField, 1, 1);
        fieldsGrid.add(minesCountLabel, 0, 2);
        fieldsGrid.add(minesCountField, 1, 2);
        gridPane.add(fieldsGrid, 0, 0);
        gridPane.add(errorLabel, 0, 1);
        dialog.getDialogPane().setContent(gridPane);

        // Initialize OK button.
        ButtonType buttonTypeOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        // OK button event.
        dialog.setOnCloseRequest((event) -> {
            if (!forceClose)
                event.consume();
            else
                forceClose = false;
        });
        dialog.getDialogPane().lookupButton(buttonTypeOk).addEventHandler(ActionEvent.ACTION, (event) -> {
            if (validate(xCellsField.getText(), yCellsField.getText(), minesCountField.getText())) {
                forceClose = true;
                dialog.close();
            }
            else {
                errorLabel.setText("Each field must contain positive integer.\nMines count must be equal or lower than cells number.");
            }
        });

        // Initialize Cancel button.
        if (showCancel) {
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
            dialog.getDialogPane().lookupButton(buttonTypeCancel).addEventHandler(ActionEvent.ACTION, (event) -> {
                forceClose = true;
                dialog.close();
            });
        }

    }

    public Settings getSettings() {
        dialog.showAndWait();
        return settings;
    }
}
