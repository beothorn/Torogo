package beothorn.github.com.toroidalgo.go.impl.logic;

public class BoardPosition {

    private int column;
    private int line;

    public BoardPosition(int column, int line) {
        this.column = column;
        this.line = line;
    }

    public int getColumn() {
        return column;
    }

    public int getLine() {
        return line;
    }
}
