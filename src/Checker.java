import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Checker
{
    private boolean white;
    private Chessboard.Cell currentCell;
    private BufferedImage image;

    Checker(boolean white, Chessboard.Cell currentCell)
    {
        this.white = white;
        this.currentCell = currentCell;
        this.currentCell.setOccupied(true);
        try
        {
            if (white)
                image = ImageIO.read(new File("./src/white.png"));
            else
                image = ImageIO.read(new File("./src/black.png"));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public Chessboard.Cell getCurrentCell() { return currentCell; }

    public BufferedImage getImage(){ return image; }

    public boolean isWhite() { return white; }

    public boolean move(Chessboard.Cell toCell)
    {
        if (!toCell.isOccupied())
        {
            currentCell.setOccupied(false);
            currentCell = toCell;
            currentCell.setOccupied(true);
            return true;
        }
        return false;
    }
}
