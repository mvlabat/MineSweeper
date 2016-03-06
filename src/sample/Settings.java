package sample;

public class Settings {
    private int xCells;
    private int yCells;
    private int minesCount;

    public Settings(int xCells, int yCells, int minesCount) {
        this.xCells = xCells;
        this.yCells = yCells;
        this.minesCount = minesCount;
    }

    public int getXCells() {
        return xCells;
    }

    public int getYCells() {
        return yCells;
    }

    public int getMinesCount() {
        return minesCount;
    }

    static public boolean validate(int xCells, int yCells, int minesCount) {
        return (xCells > 0 && yCells > 1 && minesCount > 0 && minesCount <= xCells * yCells);
    }
}