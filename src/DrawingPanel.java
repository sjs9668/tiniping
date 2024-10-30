import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class DrawingPanel extends JPanel {
    private int prevX, prevY;

    public DrawingPanel() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Graphics g = getGraphics();
                g.setColor(Color.BLACK);  // 기본 색상
                g.drawLine(prevX, prevY, x, y);

                prevX = x;
                prevY = y;
                g.dispose();
            }
        });
    }
}