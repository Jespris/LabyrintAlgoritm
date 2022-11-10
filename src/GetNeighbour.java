package src;

public class GetNeighbour {
    private final int wallIndex;
    private final int cellIndex;

    public GetNeighbour(final int wallIndex, final int cellIndex){
        assert wallIndex < 4;
        this.cellIndex = cellIndex;
        this.wallIndex = wallIndex;
    }

    public int getCellIndex() {
        return cellIndex;
    }

    public int getWallIndex() {
        return wallIndex;
    }
}
