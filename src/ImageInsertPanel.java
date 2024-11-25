package Project;
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
    private File selectedFile;
    
    public ImageInsertPanel() {
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER); // 이미지 중앙 정렬
        setLayout(new BorderLayout());
        add(imageLabel, BorderLayout.CENTER); // 이미지만 표시
    }
    
    public File getSelectedFile() {
    	System.out.println("선택된 파일: " + (selectedFile != null ? selectedFile.getAbsolutePath() : "없음"));
        return selectedFile;
    }

    // 선택한 이미지를 표시하는 메서드
    public void displayImage(File selectedFile) {
    	this.selectedFile = selectedFile; // 선택된 파일 저장
        try {
            BufferedImage bufferedImage = ImageIO.read(selectedFile);
            ImageIcon imageIcon = new ImageIcon(bufferedImage);
            Image image = imageIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH); // 이미지 크기 조정
            imageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "이미지를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
}