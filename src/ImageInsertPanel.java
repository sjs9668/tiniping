import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImageInsertPanel extends JPanel {
    private JLabel imageLabel;

    public ImageInsertPanel() {
        imageLabel = new JLabel();
        JButton insertButton = new JButton("사진 삽입");

        insertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    ImageIcon imageIcon = new ImageIcon(selectedFile.getPath());
                    Image image = imageIcon.getImage().getScaledInstance(300, 200, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(image));
                }
            }
        });

        setLayout(new BorderLayout());
        add(insertButton, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);
    }
}