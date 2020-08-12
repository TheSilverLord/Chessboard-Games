import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Checker
{
    protected boolean white;
    private Chessboard.Cell currentCell;
    private BufferedImage image;

    Checker(boolean white, Chessboard.Cell currentCell)
    {
        this.white = white;
        this.currentCell = currentCell;
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

    public boolean move(Chessboard.Cell toCell)
    {
        return false;
    }
}
