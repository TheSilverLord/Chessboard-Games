import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class Corners
{
    private Chessboard chessboard;
    private Timer timer;
    private boolean clicked = false;
    private boolean jumping = false;
    private boolean whiteTurn;
    private boolean whiteVictory;
    private boolean blackVictory;
    int blx, bly; // координаты подсвеченной клетки
    private Chessboard.Cell chosenCell = null;
    private Checker checker = null;
    private String destination = null;

    String host = "localhost";
    int port = 48655;
    Socket socket;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    Vector<String> clients;
    private String opponent = null;
    private String name;
    private boolean white;

    Corners(JPanel field, JPanel controlPanel)
    {
        // Начальная расстановка шашек
        chessboard = new Chessboard();
        for(char a = 'a'; a <= 'c'; a++)
        {
            for(int i = 8; i >= 6; i--) chessboard.addChecker(new Checker(false, chessboard.getCell(a, i)));
        }
        for (char a = 'f'; a <= 'h'; a++)
        {
            for (int i = 3; i >= 1; i--) chessboard.addChecker(new Checker(true, chessboard.getCell(a, i)));
        }

        // Таймер отрисовки поля и шашек
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
        }, 250, 250);

        // Соединение клиента с сервером и получение списка клиентов
        try
        {
            socket = new Socket(host, port);
            Thread.sleep(2000);
            oos = new ObjectOutputStream(socket.getOutputStream());
            ois = new ObjectInputStream(socket.getInputStream());
            Request r = (Request) ois.readObject();
            clients = new Vector<>((Vector<String>) r.data);
            name = r.receiver;
            for (String c : clients)
            {
                JTextArea clientsTA = (JTextArea)(controlPanel.getComponent(3));
                clientsTA.append(c + '\n');
            }
        }
        catch (IOException | ClassNotFoundException | InterruptedException e)
        {
            e.printStackTrace();
        }

        Thread getRequest = new Thread(() -> {
            while (true)
            {
                try
                {
                    Request request = (Request) ois.readObject();
                    if (request.operationID == 1)
                    {
                        clients.add((String)request.data);
                        JTextArea clientsTA = (JTextArea)(controlPanel.getComponent(3));
                        clientsTA.append(clients.lastElement() + '\n');
                    }
                    else if (request.operationID == 2)
                    {
                        clients.remove(request.data);
                        for (String c : clients)
                        {
                            JTextArea clientsTA = (JTextArea)(controlPanel.getComponent(3));
                            clientsTA.append(c + '\n');
                        }
                    }
                    else if (request.operationID == 3)
                    {
                        DataPack dataPack = (DataPack) request.data;
                        chosenCell = chessboard.getCells().get(dataPack.source);
                        Chessboard.Cell destination = chessboard.getCells().get(dataPack.destination);
                        for (Checker c : chessboard.getCheckers())
                        {
                            if (c.getCurrentCell().equals(chosenCell))
                            {
                                c.move(destination);
                                whiteTurn = !whiteTurn;
                                if (whiteTurn) ((JTextArea)(controlPanel.getComponent(0))).setText("Ход белых");
                                else ((JTextArea)(controlPanel.getComponent(0))).setText("Ход чёрных");
                                victoryCheck(field, (JTextArea)(controlPanel.getComponent(0)));
                                break;
                            }
                        }
                    }
                    else if (request.operationID == 4)
                    {
                        opponent = (String) request.data;
                        white = false;
                        controlPanel.getComponent(4).setEnabled(false);
                        controlPanel.getComponent(4).setFocusable(false);
                    }
                }
                catch (IOException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }
            }
        });

        whiteTurn = true;

        field.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                if (white == whiteTurn)
                {
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
                                            break;
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }
                    else
                    {
                        destination = null;
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

                        if (!jumping) // если не происходит повторного прыжка
                        {
                            // движение на соседнюю клетку
                            if ((chosenCell.getRow() == dest.getRow() && (chosenCell.getCol() + 1 == dest.getCol() || chosenCell.getCol() - 1 == dest.getCol())) ||
                                    (chosenCell.getCol() == dest.getCol() && (chosenCell.getRow() + 1 == dest.getRow() || chosenCell.getRow() - 1 == dest.getRow())))
                            {
                                if (checker.move(dest))
                                {
                                    try {
                                        oos.writeObject(new Request(3, new DataPack(String.valueOf(chosenCell.getCol()) + chosenCell.getRow(), destination), opponent));
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                    whiteTurn = !whiteTurn;
                                }
                            }
                        }

                        // движение через клетку (прыжок), если соседняя клетка занята
                        if ((chosenCell.getRow() == dest.getRow() && ((chosenCell.getCol() + 2 == dest.getCol() && chessboard.getCell((char) (chosenCell.getCol() + 1), chosenCell.getRow()).isOccupied()) ||
                                (chosenCell.getCol() - 2 == dest.getCol() && chessboard.getCell((char) (chosenCell.getCol() - 1), chosenCell.getRow()).isOccupied()))) ||
                                (chosenCell.getCol() == dest.getCol() && ((chosenCell.getRow() + 2 == dest.getRow() && chessboard.getCell(chosenCell.getCol(), chosenCell.getRow() + 1).isOccupied()) ||
                                        (chosenCell.getRow() - 2 == dest.getRow() && chessboard.getCell(chosenCell.getCol(), chosenCell.getRow() - 1).isOccupied()))))
                        {
                            checker.move(dest);

                            if (!((chessboard.getCell((char) (dest.getCol() + 1), dest.getRow()).isOccupied() && !(dest.getCol() + 2 == chosenCell.getCol()) && !(chessboard.getCell((char) (dest.getCol() + 2), dest.getRow()).isOccupied())) ||
                                    (chessboard.getCell((char) (dest.getCol() - 1), dest.getRow()).isOccupied() && !(dest.getCol() - 2 == chosenCell.getCol()) && !(chessboard.getCell((char) (dest.getCol() - 2), dest.getRow()).isOccupied())) ||
                                    (chessboard.getCell(dest.getCol(), dest.getRow() + 1).isOccupied() && !(dest.getRow() + 2 == chosenCell.getRow()) && !(chessboard.getCell(dest.getCol(), dest.getRow() + 2).isOccupied())) ||
                                    (chessboard.getCell(dest.getCol(), dest.getRow() - 1).isOccupied() && !(dest.getRow() - 2 == chosenCell.getRow()) && !(chessboard.getCell(dest.getCol(), dest.getRow() - 2).isOccupied()))))
                            {
                                jumping = false;
                                try {
                                    oos.writeObject(new Request(3, new DataPack(String.valueOf(chosenCell.getCol()) + chosenCell.getRow(), destination), opponent));
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                                whiteTurn = !whiteTurn;
                                controlPanel.getComponent(1).setEnabled(false);
                            }
                            else
                            {
                                jumping = true;
                                chosenCell = dest;
                                int[] m = chessboard.getCellCoord().get(String.valueOf(chosenCell.getCol()) + chosenCell.getRow());
                                blx = m[0];
                                bly = m[1];
                                controlPanel.getComponent(1).setEnabled(true);
                            }
                        }

                        if (!jumping) clicked = false;

                        if (whiteTurn) ((JTextArea)(controlPanel.getComponent(0))).setText("Ход белых");
                        else ((JTextArea)(controlPanel.getComponent(0))).setText("Ход чёрных");

                        victoryCheck(field, (JTextArea)(controlPanel.getComponent(0)));
                    }
                }
            }
        });

        getRequest.start();
    }

    public synchronized boolean endTurn()
    {
        whiteTurn = !whiteTurn;
        try {
            oos.writeObject(new Request(3, new DataPack(String.valueOf(chosenCell.getCol()) + chosenCell.getRow(), destination), opponent));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        jumping = false;
        return whiteTurn;
    }

    public synchronized void victoryCheck(JPanel field, JTextArea turn)
    {
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

        if (whiteVictory)
        {
            turn.setText("Победа белых");
            timer.cancel();
            chessboard.paint(field.getGraphics(), field.getMinimumSize().width, field.getMinimumSize().height);
        }
        else if (blackVictory)
        {
            turn.setText("Победа чёрных");
            timer.cancel();
            chessboard.paint(field.getGraphics(), field.getMinimumSize().width, field.getMinimumSize().height);
        }
    }

    public void setOpponent(String opponent) { this.opponent = opponent; }
    public String getOpponent() { return opponent; }

    public String getName() { return this.name; }

    public void setWhite(boolean white) { this.white = white; }
}
