package Field;

import sample.Settings;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

class FieldModel {
    private int xCells;
    private int yCells;
    private int minesCount;
    private CellStatus[][] field;
    private byte[][] surroundingMinesCount;

    FieldModel() { }

    /**
     * Initializes a new field.
     */
    public void setNewField(Settings settings) {
        xCells = settings.getXCells();
        yCells = settings.getYCells();
        this.minesCount = settings.getMinesCount();
        generateMines();
    }

    /**
     * Gets a status of the specified cell.
     */
    public CellStatus getCellStatus(int x, int y) {
        return field[y][x];
    }

    /**
     * Gets a status of the specified cell.
     */
    public CellStatus getCellStatus(FieldCell cell) {
        return field[cell.getY()][cell.getX()];
    }

    /**
     * Sets a new status for the specified cell.
     */
    public void setCellStatus(FieldCell cell, CellStatus status) {
        field[cell.getY()][cell.getX()] = status;
    }

    /**
     * Sets a new status for the specified cell.
     */
    public void setCellStatus(int x, int y, CellStatus status) {
        field[y][x] = status;
    }

    /**
     * Increments surrounding mines count for the specified cell.
     */
    private void IncrementCellMinesCount(int x, int y) {
        surroundingMinesCount[y][x]++;
    }

    /**
     * Increments surrounding mines count for neighbour cells when a mine has been generated.
     */
    private void IncrementSurroundingMinesCount(int x, int y) {
        if (checkCell(x - 1, y - 1)) IncrementCellMinesCount(x - 1, y - 1);
        if (checkCell(x, y - 1)) IncrementCellMinesCount(x, y - 1);
        if (checkCell(x + 1, y - 1)) IncrementCellMinesCount(x + 1, y - 1);
        if (checkCell(x + 1, y)) IncrementCellMinesCount(x + 1, y);
        if (checkCell(x + 1, y + 1)) IncrementCellMinesCount(x + 1, y + 1);
        if (checkCell(x, y + 1)) IncrementCellMinesCount(x, y + 1);
        if (checkCell(x - 1, y + 1)) IncrementCellMinesCount(x - 1, y + 1);
        if (checkCell(x - 1, y)) IncrementCellMinesCount(x - 1, y);
    }

    /**
     * Fills the field with mines.
     */
    private void generateMines() {
        // Initialize members and helper objects;
        Random randomizer = new Random();
        List<FieldCell> valuesList = new LinkedList<>();
        field = new CellStatus[yCells][xCells];
        surroundingMinesCount = new byte[yCells][xCells];

        // Generate list of possible values (we have to generate mines with unique coordinates).
        for (int i = 0, j = 0; i < xCells;) {
            valuesList.add(new FieldCell(i, j));
            setCellStatus(i, j++, CellStatus.EMPTY);
            if (j == yCells) {
                j = 0;
                ++i;
            }
        }

        // Place the mines.
        FieldCell tempCell;
        for (int i = 0, x1 = 0, y1 = 0, x2, y2, x, y; i < minesCount; ++i) {
            tempCell = valuesList.remove(randomizer.nextInt(xCells * yCells - i));
            setCellStatus(tempCell.getX(), tempCell.getY(), CellStatus.MINED);
            IncrementSurroundingMinesCount(tempCell.getX(), tempCell.getY());
        }
    }

    /**
     * Toggles a cell flag when the right mouse button is pressed.
     *
     * @return Returns a boolean whether a player has won or not.
     */
    public boolean toggleCellFlag(int x, int y) {
        switch (getCellStatus(x, y)) {
            case EMPTY:
                setCellStatus(x, y, CellStatus.EMPTY_FLAGGED);
                break;

            case EMPTY_FLAGGED:
                setCellStatus(x, y, CellStatus.EMPTY_INQUIRED);
                break;

            case EMPTY_INQUIRED:
                setCellStatus(x, y, CellStatus.EMPTY);
                break;

            case MINED:
                setCellStatus(x, y, CellStatus.MINED_FLAGGED);
                break;

            case MINED_FLAGGED:
                setCellStatus(x, y, CellStatus.MINED_INQUIRED);
                break;

            case MINED_INQUIRED:
                setCellStatus(x, y, CellStatus.MINED);
                break;
        }
        return checkForWin();
    }

    /**
     * Checks for win combination.
     */
    public boolean checkForWin() {
        for (int i = 0, j = 0; i < yCells;) {
            switch (getCellStatus(j, i)) {
                case EMPTY:
                case EMPTY_FLAGGED:
                case EMPTY_INQUIRED:
                case MINED:
                case MINED_INQUIRED:
                    return false;
            }

            ++j;
            if (j == xCells) {
                j = 0;
                ++i;
            }
        }

        return true;
    }

    /**
     * Returns surrounding mines count of the specified cell.
     */
    public int getCellMinesCount(int x, int y) {
        return surroundingMinesCount[y][x];
    }

    public int getXCells() {
        return xCells;
    }

    public int getYCells() {
        return yCells;
    }

    /**
     * Checks if the coordinates are in the field range.
     */
    public boolean checkCell(int x, int y) {
        return (x >= 0 && y >= 0 && x < xCells && y < yCells);
    }
}
