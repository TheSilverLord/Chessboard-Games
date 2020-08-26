import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Corners
{
    private Chessboard chessboard;
    private Timer timer;
    private boolean clicked = false;
    int blx, bly; // координаты подсвеченной клетки
    private String chosenCell;

    Corners(JPanel field)
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
                chessboard.paint(field.getGraphics());
                if (clicked)
                {
                    field.getGraphics().drawImage(Chessboard.Cell.getBacklight(), blx, bly, null);
                }
            }
        }, 1000, 1000);

        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (!clicked)
                {
                    for (HashMap.Entry<String, int[]> entry : chessboard.getCellCoord().entrySet())
                    {
                        int x = entry.getValue()[0];
                        int y = entry.getValue()[1];
                        if (e.getX() >= x && e.getX() <= x + Chessboard.Cell.getSize() && e.getY() - Chessboard.Cell.getSize() <= y && e.getY() - Chessboard.Cell.getSize() >= y - Chessboard.Cell.getSize())
                        {
                            blx = x;
                            bly = y;
                            chosenCell = entry.getKey();
                            break;
                        }
                    }
                    clicked = true;
                }
                else
                {
                    String destination = null;
                    for (HashMap.Entry<String, int[]> entry : chessboard.getCellCoord().entrySet())
                    {
                        int x = entry.getValue()[0];
                        int y = entry.getValue()[1];
                        if (e.getX() >= x && e.getX() <= x + Chessboard.Cell.getSize() && e.getY() - Chessboard.Cell.getSize() <= y && e.getY() - Chessboard.Cell.getSize() >= y - Chessboard.Cell.getSize())
                        {
                            destination = entry.getKey();
                            break;
                        }
                    }
                    Chessboard.Cell cC = chessboard.getCells().get(chosenCell);
                    Chessboard.Cell dest = chessboard.getCells().get(destination);
                    if (cC.getRow() == dest.getRow() &&  (cC.getCol() + 1 == dest.getCol() || cC.getCol() - 1 == dest.getCol()))
                    {
                        for (Checker checker : chessboard.getCheckers())
                        {
                            if (checker.getCurrentCell().equals(cC))
                            {
                                checker.move(dest);
                                break;
                            }
                        }
                    }
                    clicked = false;
                }
            }
        });
    }
}
