
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.toedter.calendar.JCalendar;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;

public class MainFrame extends JFrame {
    private JCalendar calendar;
    private JList<String> diaryList;
    private DefaultListModel<String> listModel;
    private JPanel mainContent;
    private HashMap<String, String> diaryEntries;

    public MainFrame() {
        setTitle("나의 일기장");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 데이터 초기화
        diaryEntries = new HashMap<>();

        // 컴포넌트 초기화
        initializeComponents();

        // 리스트 및 강조 표시
        refreshDiaryView();

        setVisible(true);
    }

    private void initializeComponents() {
        // 상단 버튼 패널
        JPanel topButtonPanel = new JPanel();
        JButton writeButton = new JButton("일기쓰기");
        JButton drawButton = new JButton("그림일기");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        // 버튼 리스너 설정
        writeButton.addActionListener(e -> showWritingDialog(false));
        drawButton.addActionListener(e -> showWritingDialog(true));
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> showDeleteDialog());

        topButtonPanel.add(writeButton);
        topButtonPanel.add(drawButton);
        topButtonPanel.add(editButton);
        topButtonPanel.add(deleteButton);

        // 달력 설정
        calendar = new JCalendar();

        // 달력 날짜 변경 시 이벤트 처리
        calendar.getDayChooser().addPropertyChangeListener("day", evt -> refreshDiaryView());

        // 달력 월 변경 시 이벤트 처리
        calendar.getMonthChooser().addPropertyChangeListener("month", evt -> highlightDiaryDates());

        // 오른쪽 사이드바 (일기 목록)
        listModel = new DefaultListModel<>();
        diaryList = new JList<>(listModel);
        diaryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane sideBarScroll = new JScrollPane(diaryList);
        sideBarScroll.setBorder(BorderFactory.createTitledBorder("작성된 일기 목록"));
        sideBarScroll.setPreferredSize(new Dimension(200, 0));

        // 중앙 메인 패널에 달력 추가
        mainContent = new JPanel(new BorderLayout());
        mainContent.add(calendar, BorderLayout.CENTER);

        // 레이아웃 구성
        add(topButtonPanel, BorderLayout.NORTH);
        add(mainContent, BorderLayout.CENTER);
        add(sideBarScroll, BorderLayout.EAST);
    }

    private void refreshDiaryView() {
        highlightDiaryDates(); // 강조 상태 갱신
        updateDiaryList(); // 선택된 날짜의 제목 갱신
    }

    private void highlightDiaryDates() {
    try {
        DB_Dates dbDates = new DB_Dates();
        diaryEntries.clear();

        // DB에서 작성된 모든 날짜 가져오기
        List<String> savedDates = dbDates.getAllSavedDates();
        for (String date : savedDates) {
            diaryEntries.put(date, ""); // 일기 날짜 저장
        }

        // 선택된 날짜 가져오기
        java.util.Date selectedDate = calendar.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(selectedDate);
        
        // 달력의 날짜 버튼 가져오기
        JPanel dayPanel = calendar.getDayChooser().getDayPanel();
        Component[] dayButtons = dayPanel.getComponents();

        for (Component dayButton : dayButtons) {
            if (dayButton instanceof JButton) {
                JButton button = (JButton) dayButton;
                String dayText = button.getText();

                if (!dayText.isEmpty() && dayText.matches("\\d+")) {
                    int year = calendar.getYearChooser().getYear();
                    int month = calendar.getMonthChooser().getMonth() + 1;
                    String formattedDate = String.format("%04d-%02d-%02d", year, month, Integer.parseInt(dayText));

                    // 강조 상태 업데이트
                    if (diaryEntries.containsKey(formattedDate)) {
                        button.setBackground(Color.YELLOW); // 작성된 일기 강조
                        button.setOpaque(true);
                    } else {
                        button.setBackground(null); // 강조 제거
                        button.setOpaque(false);
                    }
                    
                    // 선택된 날짜 테두리 색
                    if (formattedDate.equals(dateStr)) {
                    	button.setBorder(BorderFactory.createLineBorder(Color.RED, 3)); // 테두리 색과 두께 설정
                        button.setOpaque(true);
                    }
                    else {
                        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)); // 테두리 초기화
                    }
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private void updateDiaryList() {
        listModel.clear();

        // 선택된 날짜 가져오기
        java.util.Date selectedDate = calendar.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(selectedDate);

        try {
            DB_Contents dbContents = new DB_Contents();
            DB_Drawings dbDrawings = new DB_Drawings();

            // 텍스트 일기 제목 가져오기
            List<String> textTitles = dbContents.getTitlesByDate(dateStr);
            for (String textTitle : textTitles) {
                listModel.addElement("- [텍스트] " + textTitle);
            }

            // 그림 일기 제목 가져오기
            List<String> drawingTitles = dbDrawings.getDrawingsByDate(dateStr);
            for (String drawingTitle : drawingTitles) {
                listModel.addElement("- [그림] " + drawingTitle);
            }

            // 일기가 없을 경우 메시지 표시
            if (listModel.isEmpty()) {
                listModel.addElement("작성된 일기가 없습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            listModel.addElement("데이터를 불러오는 중 오류가 발생했습니다.");
        }
    }

    private void showWritingDialog(boolean isDrawing) {
        JDialog dialog = new JDialog(this, isDrawing ? "그림 일기 쓰기" : "일기 쓰기", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());

        // 현재 선택된 날짜를 문자열로 포맷
        Date selectedDate = calendar.getDate();
        LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = localDate.format(formatter);

        // 상단 패널: 날짜 및 제목 입력 필드
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel dateLabel = new JLabel("작성 날짜: " + currentDate);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("제목: ");
        JTextField titleField = new JTextField(20);
        titlePanel.add(titleLabel);
        titlePanel.add(titleField);
        topPanel.add(dateLabel, BorderLayout.NORTH);
        topPanel.add(titlePanel, BorderLayout.SOUTH);

        // 본문 패널: 텍스트 일기 또는 그림 일기
        JPanel mainPanel = new JPanel(new BorderLayout());
        ImageInsertPanel imageInsertPanel = new ImageInsertPanel();
        if (isDrawing) {
            DrawingPanel drawingPanel = new DrawingPanel();
            JPanel colorSelectionPanel = drawingPanel.createColorSelectionPanel();
            mainPanel.add(drawingPanel, BorderLayout.CENTER);
            mainPanel.add(colorSelectionPanel, BorderLayout.SOUTH);
        } else {
            WriteDiary writeDiaryPanel = new WriteDiary();
            mainPanel.add(writeDiaryPanel, BorderLayout.CENTER);
            mainPanel.add(imageInsertPanel, BorderLayout.SOUTH);
        }

        // 하단 패널: 사진 삽입, 저장 및 취소 버튼 정렬
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton attachButton = new JButton("사진 삽입");
        JButton saveButton = new JButton("저장");
        JButton cancelButton = new JButton("취소");

        buttonPanel.add(attachButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // 그림일기일 경우 사진 삽입 버튼 제거
        if (isDrawing) {
            buttonPanel.remove(attachButton);  // 그림일기일 경우, 사진 삽입 버튼 제거
        } else {
            attachButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(dialog);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imageInsertPanel.displayImage(selectedFile);
                }
            });
        }

        saveButton.addActionListener(e -> {
            try {
                String title = titleField.getText();
                if (title.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "제목을 입력해주세요.");
                    return; // 저장 중단
                }

                DB_Dates dbDates = new DB_Dates();
                int entryId = dbDates.saveDate(currentDate);
                if (entryId == -1) {
                    JOptionPane.showMessageDialog(dialog, "날짜 저장 중 오류가 발생했습니다.");
                    return;
                }

                if (!isDrawing) {
                    // 텍스트 일기 저장
                    String content = ((WriteDiary) mainPanel.getComponent(0)).getTextArea().getText();
                    if (content.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "내용을 입력해주세요.");
                        return; // 저장 중단
                    }

                    File selectedImage = imageInsertPanel.getSelectedFile();
                    String photoPath = (selectedImage != null) ? selectedImage.getAbsolutePath() : null;

                    DB_Contents dbContents = new DB_Contents();
                    dbContents.saveContent(entryId, title, content, photoPath);

                    JOptionPane.showMessageDialog(dialog, "텍스트 일기가 성공적으로 저장되었습니다.");
                    dialog.dispose();
                } else {
                    // 그림 일기 저장
                    DrawingPanel drawingPanel = (DrawingPanel) mainPanel.getComponent(0);
                    if (drawingPanel == null) {
                        JOptionPane.showMessageDialog(dialog, "그림 일기 패널을 찾을 수 없습니다.");
                        return; // 저장 중단
                    }

                    if (drawingPanel.isDrawingEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "그림을 추가해주세요.");
                        return; // 저장 중단
                    }

                    BufferedImage drawing = drawingPanel.getBufferedImage();
                    DB_Drawings dbDrawings = new DB_Drawings();
                    dbDrawings.saveDrawing(entryId, drawing, title);

                    JOptionPane.showMessageDialog(dialog, "그림 일기가 성공적으로 저장되었습니다.");
                    dialog.dispose();
                }

                refreshDiaryView();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "DB 저장 중 오류 발생: " + ex.getMessage());
                ex.printStackTrace();
            }
        });



        cancelButton.addActionListener(e -> {
            dialog.dispose();
            refreshDiaryView();
        });

        dialog.add(topPanel, BorderLayout.NORTH);
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
    int selectedIndex = diaryList.getSelectedIndex();
    if (selectedIndex == -1 || listModel.get(selectedIndex).equals("작성된 일기가 없습니다.")) {
        JOptionPane.showMessageDialog(this, "수정할 일기를 선택해주세요.");
        return;
    }

    // 선택된 제목 가져오기
    String selectedTitle = diaryList.getSelectedValue();

    // 태그 제거
    if (selectedTitle.startsWith("- [텍스트] ")) {
        selectedTitle = selectedTitle.replace("- [텍스트] ", "");
    } else if (selectedTitle.startsWith("- [그림] ")) {
        selectedTitle = selectedTitle.replace("- [그림] ", "");
    }

    // 선택된 날짜 가져오기
    java.util.Date selectedDate = calendar.getDate();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = sdf.format(selectedDate);

    try {
        DB_Contents dbContents = new DB_Contents();
        DB_Drawings dbDrawings = new DB_Drawings();

        // 텍스트 일기 확인
        Map<String, String> contentData = dbContents.getContentByTitleAndDate(selectedTitle, dateStr);
        if (contentData != null) {
            String content = contentData.get("content");
            String photoPath = contentData.get("photo_path");
            showTextDiaryEditDialog(selectedTitle, content, photoPath, dbContents, dateStr);
            return;
        }

        // 그림 일기 확인
        BufferedImage drawing = dbDrawings.getDrawingByTitleAndDate(selectedTitle, dateStr);
        if (drawing != null) {
            showDrawingDiaryEditDialog(selectedTitle, drawing, dbDrawings, dateStr);
            return;
        }

        JOptionPane.showMessageDialog(this, "선택한 일기를 찾을 수 없습니다.");
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "일기 데이터를 불러오는 중 오류가 발생했습니다.");
    }
}

    private void showTextDiaryEditDialog(String title, String content, String photoPath, DB_Contents dbContents, String dateStr) {
    JDialog dialog = new JDialog(this, "텍스트 일기 수정", true);
    dialog.setSize(800, 600);
    dialog.setLayout(new BorderLayout());

    // 상단 패널: 제목 필드
    JPanel topPanel = new JPanel(new BorderLayout());
    JLabel titleLabel = new JLabel("제목: ");
    JTextField titleField = new JTextField(title, 20); // 기존 제목 로드
    topPanel.add(titleLabel, BorderLayout.WEST);
    topPanel.add(titleField, BorderLayout.CENTER);

    // 중앙 패널: 내용 필드 및 이미지
    JPanel mainPanel = new JPanel(new BorderLayout());
    JTextArea contentArea = new JTextArea(content); // 기존 내용 로드
    JScrollPane contentScroll = new JScrollPane(contentArea);
    mainPanel.add(contentScroll, BorderLayout.CENTER);

    // 이미지 표시 패널
    JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JLabel imageLabel = new JLabel(); // 이미지 표시 라벨
    if (photoPath != null && !photoPath.isEmpty()) {
        File imageFile = new File(photoPath);
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(photoPath);

            // 이미지 크기 조정
            Image scaledImage = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            imageLabel.setText("이미지를 찾을 수 없습니다.");
        }
    }
    imagePanel.add(imageLabel);
    mainPanel.add(imagePanel, BorderLayout.SOUTH);

    // 하단 패널: 저장 및 취소 버튼
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton attachButton = new JButton("사진 삽입");
    JButton saveButton = new JButton("저장");
    JButton cancelButton = new JButton("취소");

    // 사진 수정
    final String[] selectedPhotoPath = {photoPath}; // 선택된 사진 경로 저장
    attachButton.addActionListener(e -> {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(dialog);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedPhotoPath[0] = selectedFile.getAbsolutePath(); // 경로 업데이트

            // 선택된 사진 표시
            ImageIcon icon = new ImageIcon(selectedPhotoPath[0]);
            Image scaledImage = icon.getImage().getScaledInstance(200, 150, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImage));
        }
    });
    
    saveButton.addActionListener(e -> {
        try {
            String newTitle = titleField.getText();
            String newContent = contentArea.getText();
            String newPhotoPath = selectedPhotoPath[0];
            
            if (newTitle.isEmpty() || newContent.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "제목과 내용을 모두 입력해주세요.");
                return;
            }
            
            dbContents.updateContent(title, dateStr, newTitle, newContent, newPhotoPath);
            
            JOptionPane.showMessageDialog(dialog, "텍스트 일기가 성공적으로 수정되었습니다.");
            refreshDiaryView();
            dialog.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "일기 수정 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    cancelButton.addActionListener(e -> dialog.dispose());
    bottomPanel.add(attachButton);
    bottomPanel.add(saveButton);
    bottomPanel.add(cancelButton);

    dialog.add(topPanel, BorderLayout.NORTH);
    dialog.add(mainPanel, BorderLayout.CENTER);
    dialog.add(bottomPanel, BorderLayout.SOUTH);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}

    private void showDrawingDiaryEditDialog(String title, BufferedImage drawing, DB_Drawings dbDrawings, String dateStr) {
    // 다이얼로그 생성
    JDialog dialog = new JDialog(this, "그림 일기 수정", true);
    dialog.setSize(800, 700);
    dialog.setLayout(new BorderLayout());

    // 상단 패널: 제목 필드
    JPanel topPanel = new JPanel(new BorderLayout());
    JLabel titleLabel = new JLabel("제목: ");
    JTextField titleField = new JTextField(title, 20); // 기존 제목 로드
    topPanel.add(titleLabel, BorderLayout.WEST);
    topPanel.add(titleField, BorderLayout.CENTER);

    // 중앙 패널: DrawingPanel(그림 편집 캔버스)
    DrawingPanel drawingPanel = new DrawingPanel();
    drawingPanel.setPreferredSize(new Dimension(600, 400));
    if (drawing != null) {
        drawingPanel.setBufferedImage(drawing); // 기존 그림 로드
    }

    // 색상 선택 패널 (DrawingPanel과 연결)
    JPanel centerPanel = new JPanel(new BorderLayout());
    centerPanel.add(drawingPanel, BorderLayout.CENTER); // 캔버스
    centerPanel.add(drawingPanel.createColorSelectionPanel(), BorderLayout.SOUTH); // 색상 선택 패널

    // 하단 패널: 저장 및 취소 버튼
    JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton saveButton = new JButton("저장");
    JButton cancelButton = new JButton("취소");

    saveButton.addActionListener(e -> {
        try {
            String newTitle = titleField.getText();
            if (newTitle.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "제목을 입력해주세요.");
                return;
            }

            // 수정된 그림 가져오기
            BufferedImage updatedDrawing = drawingPanel.getBufferedImage();

            // DB 업데이트
            dbDrawings.updateDrawing(title, dateStr, newTitle, updatedDrawing);

            JOptionPane.showMessageDialog(dialog, "그림 일기가 성공적으로 수정되었습니다.");
            refreshDiaryView(); // UI 갱신
            dialog.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "그림 일기 수정 중 오류 발생: " + ex.getMessage());
            ex.printStackTrace();
        }
    });

    cancelButton.addActionListener(e -> dialog.dispose());

    bottomPanel.add(saveButton);
    bottomPanel.add(cancelButton);

    // 다이얼로그에 컴포넌트 추가
    dialog.add(topPanel, BorderLayout.NORTH); // 제목 필드
    dialog.add(centerPanel, BorderLayout.CENTER); // 그림 캔버스 및 색상 선택 패널
    dialog.add(bottomPanel, BorderLayout.SOUTH); // 버튼
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}

    private void showDeleteDialog() {
	int selectedIndex = diaryList.getSelectedIndex();
    if (selectedIndex == -1 || listModel.get(selectedIndex).equals("작성된 일기가 없습니다.")) {
        JOptionPane.showMessageDialog(this, "삭제할 일기를 선택해주세요.");
        return;
    }

    // 선택된 제목 가져오기
    String selectedTitle = diaryList.getSelectedValue();

    // 태그 제거
    if (selectedTitle.startsWith("- [텍스트] ")) {
        selectedTitle = selectedTitle.replace("- [텍스트] ", "");
    } else if (selectedTitle.startsWith("- [그림] ")) {
        selectedTitle = selectedTitle.replace("- [그림] ", "");
    }

    // 선택된 날짜 가져오기
    java.util.Date selectedDate = calendar.getDate();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String dateStr = sdf.format(selectedDate);

    // 삭제 확인 대화상자
    int confirm = JOptionPane.showConfirmDialog(
        this,
        "선택한 일기를 정말 삭제하시겠습니까?",
        "삭제 확인",
        JOptionPane.YES_NO_OPTION
    );

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            DB_Contents dbContents = new DB_Contents();
            DB_Drawings dbDrawings = new DB_Drawings();

            // 텍스트 일기 삭제
            boolean isTextDeleted = dbContents.deleteContentByTitleAndDate(selectedTitle, dateStr);

            // 그림 일기 삭제
            boolean isDrawingDeleted = dbDrawings.deleteDrawingByTitleAndDate(selectedTitle, dateStr);

            if (isTextDeleted || isDrawingDeleted) {
                JOptionPane.showMessageDialog(this, "일기가 성공적으로 삭제되었습니다.");
                refreshDiaryView(); // UI 갱신
            } else {
                JOptionPane.showMessageDialog(this, "선택한 일기를 찾을 수 없거나 삭제에 실패했습니다.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "일기 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}
