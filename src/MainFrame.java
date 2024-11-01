package Project;
import java.text.SimpleDateFormat;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;  // 추가
import javax.swing.event.ListSelectionListener;  // 추가
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
        calendar.getDayChooser().addPropertyChangeListener("day", evt -> {
            // 선택된 날짜의 일기 제목 업데이트 (실제 데이터는 DB에서 가져올 예정)
            updateDiaryList();
        });

        // 오른쪽 사이드바 (일기 목록)
        listModel = new DefaultListModel<>();
        diaryList = new JList<>(listModel);
        diaryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // 리스트 선택 이벤트 처리
        diaryList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedTitle = diaryList.getSelectedValue();
                    if (selectedTitle != null) {
                        // 여기에서 선택된 제목에 대한 처리를 할 수 있습니다
                        System.out.println("선택된 일기 제목: " + selectedTitle);
                    }
                }
            }
        });

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

    // 선택된 날짜의 일기 목록 업데이트
    private void updateDiaryList() {
        listModel.clear();  // 기존 목록 초기화
        
        // 선택된 날짜 가져오기
        java.util.Date selectedDate = calendar.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(selectedDate);
        
        // 선택된 날짜에 저장된 일기가 있는 경우만 제목 표시
        if (diaryEntries.containsKey(dateStr)) {
            String diaryContent = diaryEntries.get(dateStr);
            String title = diaryContent.split("\n")[0]; // 제목 추출 (첫 줄)
            listModel.addElement("제목: " + title);
        } else {
            listModel.addElement("작성된 일기가 없습니다.");
        }
    }


    private void highlightDiaryDates() {
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

                    if (diaryEntries.containsKey(formattedDate)) {
                        button.setBackground(Color.YELLOW);
                    } else {
                        button.setBackground(null);
                    }

                    // 날짜 클릭 이벤트 추가
                    button.addActionListener(e -> updateSidebarWithDiaryTitle(formattedDate));
                }
            }
        }
    }

    // 사이드바에 선택된 날짜의 일기 제목을 표시하는 메서드
    private void updateSidebarWithDiaryTitle(String date) {
    	 listModel.clear();  // 기존 목록 초기화
    	    
    	    // 선택된 날짜 가져오기
    	    java.util.Date selectedDate = calendar.getDate();
    	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	    String dateStr = sdf.format(selectedDate);
    	    
    	    // 해당 날짜에 저장된 일기가 있는지 확인하고 제목만 사이드바에 표시
    	    if (diaryEntries.containsKey(dateStr)) {
    	        String diaryContent = diaryEntries.get(dateStr);
    	        String title = diaryContent.split("\n")[0]; // 제목 추출 (첫 줄)
    	        listModel.addElement("제목: " + title);
    	    } else {
    	        listModel.addElement("작성된 일기가 없습니다.");
    	    }
    }
    //updateSidebarWithDiaryTitle는 특정 날짜를 클릭했을 때 해당 날짜의 일기 제목을 업데이트하기 위해 사용.
    //updateDiaryList는 선택된 날짜가 변경될 때 현재 선택된 날짜의 일기 목록을 업데이트하기 위해 사용.

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

        // 상단 패널 (날짜와 제목 입력란 포함)
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel dateLabel = new JLabel("작성 날짜: " + currentDate);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // 제목 입력란
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("제목: ");
        JTextField titleField = new JTextField(20); // 입력 칸의 크기를 조정

        titlePanel.add(titleLabel);
        titlePanel.add(titleField);

        // 날짜와 제목을 포함한 패널을 topPanel에 추가
        topPanel.add(dateLabel, BorderLayout.NORTH);
        topPanel.add(titlePanel, BorderLayout.SOUTH);

        // 본문 입력 영역
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton attachButton = new JButton("사진 첨부");
        buttonPanel.add(attachButton);

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

        // 다이얼로그에 컴포넌트 추가
        dialog.add(topPanel, BorderLayout.NORTH);
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
