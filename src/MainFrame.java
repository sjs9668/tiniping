package Project;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.toedter.calendar.JCalendar;

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
        
        // 일기 강조 표시
        highlightDiaryDates();

        setVisible(true);
    }

    private void initializeComponents() {
        // 상단 버튼 패널
        JPanel topButtonPanel = new JPanel();
        JButton writeButton = new JButton("일기쓰기");
        JButton drawButton = new JButton("그림일기");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");

        // 버튼 리스너 설정 (창은 뜨지만 동작은 없음)
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

        // 달력 날짜 변경 시 강조 업데이트
        calendar.getDayChooser().addPropertyChangeListener("day", evt -> highlightDiaryDates());

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

    private void highlightDiaryDates() {
        JPanel dayPanel = calendar.getDayChooser().getDayPanel();
        Component[] dayButtons = dayPanel.getComponents();

        for (Component dayButton : dayButtons) {
            if (dayButton instanceof JButton) {
                JButton button = (JButton) dayButton;
                String dayText = button.getText();

                // dayText가 비어있지 않고 숫자인지 확인
                if (!dayText.isEmpty() && dayText.matches("\\d+")) {
                    // 현재 달력의 연도와 월 정보 가져오기
                    int year = calendar.getYearChooser().getYear();
                    int month = calendar.getMonthChooser().getMonth() + 1; // 0부터 시작하므로 1을 더함
                    String formattedDate = String.format("%04d-%02d-%02d", year, month, Integer.parseInt(dayText));

                    // 일기 데이터에 존재하면 강조 표시
                    if (diaryEntries.containsKey(formattedDate)) {
                        button.setBackground(Color.YELLOW); // 강조 색상 설정
                    } else {
                        button.setBackground(null); // 기본 색상으로 복원
                    }
                } else {
                    button.setBackground(null); // 기본 색상으로 복원
                }
            }
        }
    }

    // 일기 작성 다이얼로그를 표시하는 메서드
    private void showWritingDialog(boolean isDrawing) {
        JDialog dialog = new JDialog(this, isDrawing ? "그림 일기 쓰기" : "일기 쓰기", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());

        // 현재 선택된 날짜를 LocalDate로 변환 후 포맷팅
        Date selectedDate = calendar.getDate();
        LocalDate localDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String currentDate = localDate.format(formatter);

        JLabel dateLabel = new JLabel("작성 날짜: " + currentDate);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // 글쓰기와 그림일기 공통의 사진 첨부 버튼
        JButton attachButton = new JButton("사진 첨부");
        buttonPanel.add(attachButton);

        // 텍스트 입력 영역 비활성화
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false); // 텍스트 입력 비활성화
        JScrollPane scrollPane = new JScrollPane(textArea);
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JButton saveButton = new JButton("저장");
        JButton cancelButton = new JButton("취소");
        
        // 저장 버튼의 기능 비활성화
        saveButton.addActionListener(e -> {
            // 저장 기능 비활성화
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(dateLabel, BorderLayout.NORTH);
        dialog.add(bottomPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 수정 확인 창 띄우기 코드
    private void showEditDialog() {
        JDialog dialog = new JDialog(this, "수정", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // 삭제 확인 창 띄우기 코드
    private void showDeleteDialog() {
        JDialog dialog = new JDialog(this, "삭제", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame();
        });
    }
}
