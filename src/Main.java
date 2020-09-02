import javax.swing.*;
import java.awt.*;

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
        Container container = frame.getContentPane();

        // Текстовая область для обозначения очередности хода
        JTextArea turn = new JTextArea();
        turn.setFont(new Font("TimesRoman", Font.ITALIC, 20));
        turn.setText("Ход белых");
        turn.setEditable(false);
        turn.setFocusable(false);
        // Кнопка окончания хода (активируется в "уголках" при "перепрыгивании", если игрок не собирается "прыгать" дальше)
        JButton endTurn = new JButton("Закончить ход");
        endTurn.setFocusable(false);
        endTurn.setEnabled(false);

        controlPanel.add(turn); // компонент используется под номером 0, не изменять!
        controlPanel.add(endTurn); // компонент используется под номером 1, не изменять!

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
    }
}
