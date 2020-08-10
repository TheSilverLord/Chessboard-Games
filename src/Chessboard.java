import java.util.HashMap;
import java.util.Vector;

public class Chessboard
{
    class Cell
    {
        private String col;
        private int row;

        Cell(String column, int row)
        {
            this.col = column;
            this.row = row;
        }

        String getCol(){ return col; }
        int getRow(){ return row; }
    }

    private HashMap<String, Cell> cells;
    private Vector<Checker> checkers;

    Chessboard()
    {
        cells = new HashMap<>();
        for(char a = 'a'; a != 'i'; a++)
        {
            for (int i = 1; i <= 8; i++) cells.put(String.valueOf(a) + i, new Cell(String.valueOf(a), i));
        }

        checkers = new Vector<>();
    }
}