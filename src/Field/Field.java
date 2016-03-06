package Field;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import sample.Settings;

public class Field {
    private FieldModel fieldModel;
    private FieldView fieldView;
    private Canvas canvas;
    private GraphicsContext graphicsContext;
    private boolean hasWon;
    private boolean hasLost;
    private int lastMouseX;
    private int lastMouseY;

    public Field(Canvas canvas, GraphicsContext graphicsContext, Settings settings) {
        fieldModel = new FieldModel();
        this.graphicsContext = graphicsContext;
        this.canvas = canvas;
        setNewField(settings);
    }

    public void setNewField(Settings settings) {
        fieldView = new PlayingFieldView(fieldModel, canvas, graphicsContext);
        hasWon = false;
        hasLost = false;
        fieldModel.setNewField(settings);
        fieldView.setNewField();
    }

    public int getCanvasWidth() {
        return fieldView.canvasWidth;
    }

    public int getCanvasHeight() {
        return fieldView.canvasHeight;
    }

    /**
     * Recursively processes cells.
     */
    private void processCell(int x, int y) {
        if (fieldModel.getCellStatus(x, y) == CellStatus.EMPTY) {
            if (fieldModel.getCellMinesCount(x, y) == 0) {
                fieldModel.setCellStatus(x, y, CellStatus.EMPTY_REVEALED);
                if (fieldModel.checkCell(x + 1, y)) processCell(x + 1, y);
                if (fieldModel.checkCell(x - 1, y)) processCell(x - 1, y);
                if (fieldModel.checkCell(x, y + 1)) processCell(x, y + 1);
                if (fieldModel.checkCell(x, y - 1)) processCell(x, y - 1);
                if (fieldModel.checkCell(x + 1, y + 1)) processCell(x + 1, y + 1);
                if (fieldModel.checkCell(x - 1, y + 1)) processCell(x - 1, y + 1);
                if (fieldModel.checkCell(x - 1, y - 1)) processCell(x - 1, y - 1);
                if (fieldModel.checkCell(x + 1, y - 1)) processCell(x + 1, y - 1);
            }
            else {
                fieldModel.setCellStatus(x, y, CellStatus.EMPTY_REVEALED);
            }
            fieldView.drawCell(x, y, CellStatus.EMPTY_REVEALED);
        }
    }

    /**
     * Reveals the cell(s) and
     */
    public void clickCell(int x, int y) {
        switch (fieldModel.getCellStatus(x, y)) {
            case EMPTY:
                processCell(x, y);
                break;

            case MINED:
                hasLost = true;
        }
    }

    /**
     * Checks if the player has won.
     */
    public boolean hasWon() {
        return hasWon;
    }

    /**
     * Checks if the player has lost.
     */
    public boolean hasLost() {
        return hasLost;
    }

    public void setLostFieldView() {
        if (hasLost) {
            fieldView = new LostFieldView(fieldModel, fieldView.canvas, fieldView.graphicsContext);
        }
    }

    public void leftButtonPressed(int mouseX, int mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        fieldView.savePressedCellInfo(mouseX, mouseY);
        switch (fieldModel.getCellStatus(fieldView.pressedCell)) {
            case EMPTY:
            case MINED:
                fieldView.fillSavedCell(fieldView.PRESSED_CELL_COLOR);
        }
    }

    public void leftButtonReleased(int mouseX, int mouseY) {
        switch (fieldModel.getCellStatus(fieldView.pressedCell)) {
            case EMPTY:
            case MINED:
                fieldView.fillSavedCell(fieldView.CELL_COLOR);
        }

        if (lastMouseX == mouseX && lastMouseY == mouseY) {
            clickCell(fieldView.pressedCell.getX(), fieldView.pressedCell.getY());
        }
    }

    public void rightButtonClicked(int mouseX, int mouseY) {
        FieldCell cell = fieldView.getCellByMouseCoords(mouseX, mouseY);
        CellStatus cellStatus = fieldModel.getCellStatus(cell);
        if (cellStatus != CellStatus.EMPTY_REVEALED) {
            hasWon = fieldModel.toggleCellFlag(cell.getX(), cell.getY());
            fieldView.drawCell(cell.getX(), cell.getY(), fieldModel.getCellStatus(cell));
        }
    }

}