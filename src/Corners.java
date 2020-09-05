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
    private boolean whiteTurn;
    private boolean whiteVictory;
    private boolean blackVictory;
    int blx, bly; // координаты подсвеченной клетки
    private Chessboard.Cell chosenCell = null;
    private Checker checker = null;

    Corners(JPanel field, JPanel controlPanel)
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
                chessboard.paint(field.getGraphics(), field.getMinimumSize().width, field.getMinimumSize().height);
                if (clicked)
                {
                    field.getGraphics().drawImage(Chessboard.Cell.getBacklight(), blx, bly, null);
                }
            }
        }, 1000, 1000);

        whiteTurn = true;

        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                // поиск клетки, на которую кликнули, по координатам
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
                            chosenCell = chessboard.getCells().get(entry.getKey());
                            if (chosenCell.isOccupied())
                            {
                                for (Checker c : chessboard.getCheckers())
                                {
                                    if (c.getCurrentCell().equals(chosenCell) && c.isWhite() == whiteTurn)
                                    {
                                        checker = c;
                                        clicked = true;
                                    }
                                }
                            }
                            break;
                        }
                    }
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
                    Chessboard.Cell dest = chessboard.getCells().get(destination);

                    // движение на соседнюю клетку
                    if ((chosenCell.getRow() == dest.getRow() && (chosenCell.getCol() + 1 == dest.getCol() || chosenCell.getCol() - 1 == dest.getCol())) ||
                            (chosenCell.getCol() == dest.getCol() && (chosenCell.getRow() + 1 == dest.getRow() || chosenCell.getRow() - 1 == dest.getRow())))
                    {
                        if (checker.move(dest))
                            whiteTurn = !whiteTurn;
                    }

                    // движение через клетку (прыжок), если соседняя клетка занята
                    if ((chosenCell.getRow() == dest.getRow() && ((chosenCell.getCol() + 2 == dest.getCol() && chessboard.getCell((char) (chosenCell.getCol() + 1), chosenCell.getRow()).isOccupied()) ||
                            (chosenCell.getCol() - 2 == dest.getCol() && chessboard.getCell((char) (chosenCell.getCol() - 1), chosenCell.getRow()).isOccupied()))) ||
                            (chosenCell.getCol() == dest.getCol() && ((chosenCell.getRow() + 2 == dest.getRow() && chessboard.getCell(chosenCell.getCol(), chosenCell.getRow() + 1).isOccupied()) ||
                            (chosenCell.getRow() - 2 == dest.getRow() && chessboard.getCell(chosenCell.getCol(), chosenCell.getRow() - 1).isOccupied()))))
                    {
                        checker.move(dest);

                        if (!((chessboard.getCell((char) (dest.getCol() + 1), dest.getRow()).isOccupied() && !(dest.getCol() + 2 == chosenCell.getCol())) ||
                                (chessboard.getCell((char) (dest.getCol() - 1), dest.getRow()).isOccupied() && !(dest.getCol() - 2 == chosenCell.getCol())) ||
                                (chessboard.getCell(dest.getCol(), dest.getRow() + 1).isOccupied() && !(dest.getRow() + 2 == chosenCell.getRow())) ||
                                (chessboard.getCell(dest.getCol(), dest.getRow() - 1).isOccupied() && !(dest.getRow() - 2 == chosenCell.getRow()))))
                        {
                            whiteTurn = !whiteTurn;
                            controlPanel.getComponent(1).setEnabled(false);
                        }
                        else controlPanel.getComponent(1).setEnabled(true);
                    }
                    clicked = false;

                    //проверка на победу
                    if (whiteTurn)
                    {
                        int i = 0;
                        for (Checker c : chessboard.getCheckers())
                        {
                            if (!c.isWhite())
                            {
                                if (c.getCurrentCell().getCol() < 'f') break;
                                else if (c.getCurrentCell().getRow() > 3) break;
                                else i++;
                            }
                        }
                        if (i == 9) blackVictory = true;
                    }
                    else
                    {
                        int i = 0;
                        for (Checker c : chessboard.getCheckers())
                        {
                            if (c.isWhite())
                            {
                                if (c.getCurrentCell().getCol() > 'c') break;
                                else if (c.getCurrentCell().getRow() < 6) break;
                                else i++;
                            }
                        }
                        if (i == 9) whiteVictory = true;
                    }

                    if (whiteTurn) ((JTextArea)(controlPanel.getComponent(0))).setText("Ход белых");
                    else ((JTextArea)(controlPanel.getComponent(0))).setText("Ход чёрных");
                    if (whiteVictory)
                    {
                        ((JTextArea)(controlPanel.getComponent(0))).setText("Победа белых");
                        timer.cancel();
                    }
                    else if (blackVictory)
                    {
                        ((JTextArea)(controlPanel.getComponent(0))).setText("Победа чёрных");
                        timer.cancel();
                    }
                }
            }
        });
    }

    public boolean endTurn()
    {
        whiteTurn = !whiteTurn;
        return whiteTurn;
    }
}
