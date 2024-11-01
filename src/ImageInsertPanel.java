import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

//+
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class ImageInsertPanel extends JPanel {
    private JLabel imageLabel;
    
    //+
    private File selectedFile; // 선택된 파일을 저장할 변수
    private BufferedImage bufferedImage; // 선택된 이미지를 저장할 BufferedImage
    
    public ImageInsertPanel() {
        imageLabel = new JLabel();
        JButton insertButton = new JButton("사진 삽입");

        
        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                	selectedFile = fileChooser.getSelectedFile(); // 선택된 파일을 저장
                    try {
                        bufferedImage = ImageIO.read(selectedFile); // BufferedImage로 읽기
                        ImageIcon imageIcon = new ImageIcon(bufferedImage); // BufferedImage를 ImageIcon으로 변환
                        Image image = imageIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                        imageLabel.setIcon(new ImageIcon(image));
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(ImageInsertPanel.this, "이미지를 읽는 도중 오류가 발생했습니다: " + ioException.getMessage());
                    }
                }
            }
        });
        
        setLayout(new BorderLayout());
        add(insertButton, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);
        
    }
}