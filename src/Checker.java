public abstract class Checker
{
    protected boolean white;
    protected Chessboard.Cell currentCell;
    public abstract boolean move(Chessboard.Cell toCell);
}
