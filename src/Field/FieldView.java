package Field;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

abstract class FieldView {
    protected final int CELL_SIZE = 15;
    protected final int BORDER_SIZE = 1;
    protected int FONT_SIZE = 12;
    protected Color BORDER_COLOR = Color.color(1, 1, 1);
    protected Color CELL_COLOR = Color.color(0.8, 0.8, 0.8);
    protected Color PRESSED_CELL_COLOR = Color.color(0.75, 0.75, 0.75);
    protected Color FAILED_CELL_COLOR = CELL_COLOR;
    protected Color REVEALED_CELL_COLOR = Color.color(0.95, 0.95, 0.95);
    protected Color CELL_FONT_COLOR = Color.color(0, 0, 0);
    protected Color FAILED_CELL_FONT_COLOR = CELL_FONT_COLOR;

    protected Canvas canvas;
    protected GraphicsContext graphicsContext;

    protected int canvasWidth;
    protected int canvasHeight;

    protected FieldModel fieldModel;
    protected FieldCell pressedCell;
    protected int pressedCellStartX;
    protected int pressedCellStartY;

    public FieldView(FieldModel fieldModel, Canvas canvas, GraphicsContext graphicsContext) {
        this.fieldModel = fieldModel;
        this.canvas = canvas;
        this.graphicsContext = graphicsContext;
    }

    public void setNewField() {
        canvasWidth = fieldModel.getXCells() * (CELL_SIZE + BORDER_SIZE) + BORDER_SIZE;
        canvasHeight = fieldModel.getYCells() * (CELL_SIZE + BORDER_SIZE) + BORDER_SIZE;
        canvas.setWidth(canvasWidth);
        canvas.setHeight(canvasHeight);
        drawField();
    }

    public void drawField() {
        graphicsContext.setFont(new Font(12));
        graphicsContext.setFill(CELL_COLOR);
        graphicsContext.fillRect(0, 0, canvasWidth, canvasHeight);
        graphicsContext.setStroke(BORDER_COLOR);
        for (int i = 0; i < fieldModel.getYCells(); ++i) {
            graphicsContext.moveTo(0, 0.5 + i * (BORDER_SIZE + CELL_SIZE));
            graphicsContext.lineTo(canvasWidth, 0.5 + i * (BORDER_SIZE + CELL_SIZE));
        }
        for (int i = 0; i < fieldModel.getXCells(); ++i) {
            graphicsContext.moveTo(0.5 + i * (BORDER_SIZE + CELL_SIZE), 0);
            graphicsContext.lineTo(0.5 + i * (BORDER_SIZE + CELL_SIZE), canvasHeight);
        }
        graphicsContext.stroke();
    }

    public int startX(int x) {
        return BORDER_SIZE + x * (BORDER_SIZE + CELL_SIZE);
    }

    public int startY(int y) {
        return BORDER_SIZE + y * (BORDER_SIZE + CELL_SIZE);
    }

    public void savePressedCellInfo(int x, int y) {
        pressedCell = getCellByMouseCoords(x, y);
        pressedCellStartX = startX(pressedCell.getX());
        pressedCellStartY = startY(pressedCell.getY());
    }

    public void drawCell(int x, int y, CellStatus status) {
        int startX = startX(x);
        int startY = startY(y);
        switch (status) {
            case EMPTY:
                drawEmptyCell(startX, startY);
                break;

            case EMPTY_REVEALED:
                drawRevealedCell(fieldModel.getCellMinesCount(x, y), startX, startY);
                break;

            case EMPTY_FLAGGED:
                drawEmptyFlaggedCell(startX, startY);
                break;

            case EMPTY_INQUIRED:
            case MINED_INQUIRED:
                drawInquiredCell(startX, startY);
                break;

            case MINED:
                drawMinedCell(startX, startY);
                break;

            case MINED_FLAGGED:
                drawMinedFlaggedCell(startX, startY);
                break;
        }
    }

    public void drawEmptyCell(int startX, int startY) {
        fillCell(CELL_COLOR, startX, startY);
    }

    private void drawEmptyFlaggedCell(int startX, int startY) {
        fillCell(FAILED_CELL_COLOR, startX, startY);
        printOnCell('!', FAILED_CELL_FONT_COLOR, startX, startY);
    }

    private void drawInquiredCell(int startX, int startY) {
        fillCell(FAILED_CELL_COLOR, startX, startY);
        printOnCell('?', FAILED_CELL_FONT_COLOR, startX, startY);
    }

    private void drawMinedCell(int startX, int startY) {
        fillCell(FAILED_CELL_COLOR, startX, startY);
    }

    private void drawMinedFlaggedCell(int startX, int startY) {
        fillCell(CELL_COLOR, startX, startY);
        printOnCell('!', CELL_FONT_COLOR, startX, startY);
    }

    private void drawRevealedCell(int minesCount, int startX, int startY) {
        fillCell(REVEALED_CELL_COLOR, startX, startY);
        if (minesCount == 0) return;
        Color color;
        switch (minesCount) {
            case 1:
                color = Color.CORNFLOWERBLUE;
                break;

            case 2:
                color = Color.CHARTREUSE;
                break;

            case 3:
                color = Color.CRIMSON;
                break;

            case 4:
                color = Color.DARKMAGENTA;
                break;

            case 5:
                color = Color.MAROON;
                break;

            case 6:
                color = Color.TURQUOISE;
                break;

            case 7:
                color = Color.BLACK;
                break;

            case 8:
                color = Color.DARKGRAY;
                break;

            default:
                color = Color.BLACK;
                break;
        }
        printOnCell(Character.forDigit(minesCount, 10), color, startX, startY);
    }

    public void fillSavedCell(Color color) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(
                pressedCellStartX,
                pressedCellStartY,
                CELL_SIZE,
                CELL_SIZE
        );
    }

    public void fillCell(Color color, int startX, int startY) {
        graphicsContext.setFill(color);
        graphicsContext.fillRect(
                startX,
                startY,
                CELL_SIZE,
                CELL_SIZE
        );
    }

    public void printOnCell(char s, Color color, int startX, int startY) {
        graphicsContext.setStroke(color);
        graphicsContext.strokeText(Character.toString(s), startX + CELL_SIZE / 2 - 14 / 2, startY + CELL_SIZE - 3);
    }

    /**
     * Gets cell coords by mouse coords.
     *
     * @param mouseX Mouse x coordinate
     * @param mouseY Mouse y coordinate
     * @return Returns coordinaties of the corresponding cell.<BR>
     * If mouse coords point on the border, FieldCell is supplied with -1, -1 coords.
     */
    public FieldCell getCellByMouseCoords(int mouseX, int mouseY) {
        if ((mouseX < 1 && mouseX > BORDER_SIZE && mouseX % (BORDER_SIZE + CELL_SIZE) < BORDER_SIZE)
                || (mouseY < 1 && mouseY > BORDER_SIZE && mouseY % (BORDER_SIZE + CELL_SIZE) < BORDER_SIZE)) {
            return new FieldCell(-1, -1);
        }
        return new FieldCell(mouseX / (BORDER_SIZE + CELL_SIZE), mouseY / (BORDER_SIZE + CELL_SIZE));
    }
}

class PlayingFieldView extends FieldView {
    public PlayingFieldView(FieldModel fieldModel, Canvas canvas, GraphicsContext graphicsContext) {
        super(fieldModel, canvas, graphicsContext);
    }
}

class LostFieldView extends FieldView {
    public LostFieldView(FieldModel fieldModel, Canvas canvas, GraphicsContext graphicsContext) {
        super(fieldModel, canvas, graphicsContext);
        FAILED_CELL_COLOR = Color.rgb(212, 144, 144);
        for (int i = 0, j = 0; i < fieldModel.getYCells();) {
            drawCell(j, i, fieldModel.getCellStatus(j, i));
            ++j;
            if (j == fieldModel.getXCells()) {
                j = 0;
                ++i;
            }
        }
    }
}
