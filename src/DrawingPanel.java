import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class DrawingPanel extends JPanel {
    private int prevX, prevY;
    private Color currentColor = Color.BLACK; // 기본 색상
    private boolean isEraserMode = false; // 지우개 모드 플래그
    private int eraserSize = 20; // 지우개 크기
    private BufferedImage canvas; // 그리기 버퍼

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 400));
        canvas = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB); // 초기화
        clearCanvas(); // 캔버스 초기화

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                Graphics2D g2d = canvas.createGraphics();
                if (isEraserMode) {
                    g2d.setColor(getBackground()); // 지우개는 배경색으로 그림
                    g2d.fillOval(x - eraserSize / 2, y - eraserSize / 2, eraserSize, eraserSize);
                } else {
                    g2d.setColor(currentColor); // 선택된 색상으로 그림
                    g2d.setStroke(new BasicStroke(2)); // 선 두께 설정
                    g2d.drawLine(prevX, prevY, x, y);
                }
                g2d.dispose();

                prevX = x;
                prevY = y;
                repaint(); // 패널 다시 그리기
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null); // 캔버스를 패널에 그림

        if (isEraserMode) {
            Point mousePosition = getMousePosition();
            if (mousePosition != null) {
                g.setColor(Color.GREEN); // 지우개 표시
                g.drawOval(mousePosition.x - eraserSize / 2, mousePosition.y - eraserSize / 2, eraserSize, eraserSize);
            }
        }
    }

    public void clearCanvas() {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.dispose();
        repaint();
    }

    public BufferedImage getBufferedImage() {
        return canvas;
    }

    public void setBufferedImage(BufferedImage drawing) {
        if (drawing != null) {
            this.canvas = drawing;
            repaint();
        }
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        this.isEraserMode = false; // 색상 변경 시 지우개 모드 해제
    }

    public Color getCurrentColor() {
        return currentColor;
    }

    public void activateEraser() {
        this.isEraserMode = true;
    }

    public boolean isDrawingEmpty() {
        for (int x = 0; x < canvas.getWidth(); x++) {
            for (int y = 0; y < canvas.getHeight(); y++) {
                int pixel = canvas.getRGB(x, y) & 0xFFFFFF; // RGB 값만 확인 (투명도 제외)
                if (pixel != Color.WHITE.getRGB()) {
                    return false; // 흰색이 아닌 픽셀이 하나라도 있으면 비어 있지 않음
                }
            }
        }
        return true; // 모든 픽셀이 흰색이면 비어 있음
    }

    public JPanel createColorSelectionPanel() {
        JPanel colorPanel = new JPanel(new FlowLayout());

        JButton colorChooserButton = new JButton("색상 선택");
        colorChooserButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "색상 선택", currentColor);
            if (selectedColor != null) {
                setCurrentColor(selectedColor);
            }
        });

        JButton eraserButton = new JButton("지우개");
        eraserButton.addActionListener(e -> activateEraser());

        colorPanel.add(colorChooserButton);
        colorPanel.add(eraserButton);

        return colorPanel;
    }
}
