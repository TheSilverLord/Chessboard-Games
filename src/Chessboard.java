import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

public class Chessboard
{
    static class Cell
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
    private HashMap<String, int[]> cellCoord;
    private Vector<Checker> checkers;
    private static BufferedImage image;

    Chessboard()
    {

        try
        {
            image = ImageIO.read(new File("./src/chessboard.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        cells = new HashMap<>();
        cellCoord = new HashMap<>();
        for(char a = 'a'; a != 'i'; a++)
        {
            for (int i = 1; i <= 8; i++) cells.put(String.valueOf(a) + i, new Cell(String.valueOf(a), i));
        }

        checkers = new Vector<>();
    }

    public Cell getCell(char col, int row)
    {
        return cells.get(String.valueOf(col) + row);
    }

    public synchronized void addChecker(Checker c){ checkers.add(c); }

    public synchronized void paint(Graphics g)
    {
        g.drawImage(image, 0, 0, null);
    }
}