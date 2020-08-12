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
        private static int size = 72;

        Cell(String column, int row)
        {
            this.col = column;
            this.row = row;
        }

        String getCol(){ return col; }
        int getRow(){ return row; }
        static int getSize(){ return size; }
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

        int x = 38;
        for(char a = 'a'; a <= 'h'; a++)
        {
            int y = 514;
            for (int i = 1; i <= 8; i++)
            {
                cells.put(String.valueOf(a) + i, new Cell(String.valueOf(a), i));
                cellCoord.put(String.valueOf(a) + i, new int[]{x, y});
                y -= Cell.getSize();
            }
            x += Cell.getSize();
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
        for (Checker checker : checkers) {
            String coord = null;
            for (HashMap.Entry<String, Cell> entry : cells.entrySet()) {
                if (entry.getValue().equals(checker.getCurrentCell())) coord = entry.getKey();
            }
            g.drawImage(checker.getImage(), cellCoord.get(coord)[0], cellCoord.get(coord)[1], null);
        }
    }
}