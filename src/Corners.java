import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Corners
{
    private Chessboard chessboard;
    private Timer timer;

    Corners(Graphics g)
    {
        chessboard = new Chessboard();
        for(char a = 'a'; a <= 'c'; a++)
        {
            for(int i = 8; i >= 6; i--) chessboard.addChecker(new Checker(false, chessboard.getCell(a, i)));
        }
        for (char a = 'f'; a <= 'h'; a++)
        {
            for (int i = 3; i >= 1; i--) chessboard.addChecker(new Checker(true, chessboard.getCell(a, i)));
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                chessboard.paint(g);
            }
        }, 1000, 1000);
    }
}
