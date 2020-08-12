import javax.swing.*;
import java.awt.*;

public class Main
{
    public static void main(String[] args)
    {
        int width = 1600;
        int height = 900;
        JFrame frame = new JFrame("Corners");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(width, height));
        JPanel field = new JPanel();
        JPanel controlPanel = new JPanel();
        Container container = frame.getContentPane();

        Corners game = new Corners();

        container.add(controlPanel, BorderLayout.WEST);
        container.add(field, BorderLayout.CENTER);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
