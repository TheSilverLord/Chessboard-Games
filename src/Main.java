import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Main
{
    public static void main(String[] args)
    {
        int width = 875;
        int height = 660;
        JFrame frame = new JFrame("Corners");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(width, height));
        JPanel field = new JPanel();
        field.setSize(625, 625);
        field.setMaximumSize(new Dimension(625, 625));
        field.setMinimumSize(new Dimension( 625, 625));
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        Container container = frame.getContentPane();

        // Текстовая область для обозначения очередности хода
        JTextArea turn = new JTextArea();
        turn.setFont(new Font("TimesRoman", Font.ITALIC, 20));
        turn.setText("Ход белых");
        turn.setEditable(false);
        turn.setFocusable(false);
        turn.setMaximumSize(new Dimension(250, 20));
        // Кнопка окончания хода (активируется в "уголках" при "перепрыгивании", если игрок не собирается "прыгать" дальше)
        JButton endTurn = new JButton("Закончить ход");
        endTurn.setFocusable(false);
        endTurn.setEnabled(false);
        endTurn.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Текстовая область для отображения клиентов сервера с меткой
        JTextArea mark = new JTextArea();
        mark.setFont(new Font("TimesRoman", Font.ITALIC, 20));
        mark.setText("Список клиентов сервера");
        mark.setEditable(false);
        mark.setFocusable(false);
        mark.setMaximumSize(new Dimension(250, 20));
        JTextArea clients = new JTextArea();
        clients.setFont(new Font("TimesRoman", Font.ITALIC, 20));
        clients.setEditable(false);
        clients.setFocusable(false);
        // Область для ввода клиента, с которым будет проходить игра
        TextField clientInput = new TextField("", 10);

        controlPanel.add(turn); // компонент используется под номером 0, не изменять!
        controlPanel.add(endTurn); // компонент используется под номером 1, не изменять!
        controlPanel.add(mark);
        controlPanel.add(clients); // компонент используется под номером 3, не изменять!
        controlPanel.add(clientInput); // компонент используется под номером 4, не изменять!

        container.add(controlPanel, BorderLayout.EAST);
        container.add(field, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);

        Corners game = new Corners(field, controlPanel);

        endTurn.addActionListener(e -> {
            if (game.endTurn()) turn.setText("Ход белых");
            else turn.setText("Ход чёрных");
            endTurn.setEnabled(false);
        });

        clientInput.addActionListener(e -> {
            if (game.clients.contains(clientInput.getText()))
            {
                game.setOpponent(clientInput.getText());
                try
                {
                    game.oos.writeObject(new Request(4, game.getName(), game.getOpponent()));
                    game.setWhite(true);
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
                clientInput.setEditable(false);
                clientInput.setFocusable(false);
            }
            else clientInput.setText("Такого клиента нет на сервере");
        });

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try
                {
                    game.oos.writeObject(new Request(2));
                    game.ois.close();
                    game.oos.close();
                    game.socket.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        });
    }
}
