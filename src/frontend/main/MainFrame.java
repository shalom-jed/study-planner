package frontend.main;

import backend.service.StudyPlannerService;
import frontend.panels.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainFrame extends JFrame {
    private StudyPlannerService service;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private DashboardPanel dashboardPanel;
    private GraphPanel graphPanel;
    private TreePanel treePanel;
    private PlannerPanel plannerPanel;
    
    public MainFrame() {
        service = new StudyPlannerService();
        service.loadSampleData();
        
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Smart Study Planner - PDSA Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Set modern look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setLayout(new BorderLayout());
        
        // Create Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Create Main Content with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(245, 246, 250));
        
        // Initialize panels
        dashboardPanel = new DashboardPanel(service);
        graphPanel = new GraphPanel(service);
        treePanel = new TreePanel(service);
        plannerPanel = new PlannerPanel(service);
        
        mainPanel.add(dashboardPanel, "DASHBOARD");
        mainPanel.add(graphPanel, "GRAPH");
        mainPanel.add(treePanel, "TREE");
        mainPanel.add(plannerPanel, "PLANNER");
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Status bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(52, 73, 94));
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel(" Ready | Graph: " + service.getGraph().getSubjects().size() + " subjects loaded");
        statusLabel.setForeground(Color.WHITE);
        statusBar.add(statusLabel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        
        JLabel title = new JLabel("Study Planner");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        sidebar.add(title);
        
        // Nav buttons
        String[] items = {"Dashboard", "Subject Graph", "Syllabus Tree", "Study Planner"};
        String[] cards = {"DASHBOARD", "GRAPH", "TREE", "PLANNER"};
        
        for (int i = 0; i < items.length; i++) {
            JButton btn = createNavButton(items[i]);
            final String card = cards[i];
            btn.addActionListener(e -> cardLayout.show(mainPanel, card));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        // Quick actions
        JPanel quickPanel = new JPanel();
        quickPanel.setBackground(new Color(44, 62, 80));
        quickPanel.setLayout(new GridLayout(2, 1, 5, 5));
        quickPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        JButton refreshBtn = new JButton("Refresh Data");
        refreshBtn.setBackground(new Color(46, 204, 113));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> {
            dashboardPanel.refreshStats();
            treePanel.refreshTree();
            plannerPanel.updateStudyPath();
            plannerPanel.updateWeaknessList();
        });
        
        quickPanel.add(refreshBtn);
        sidebar.add(quickPanel);
        
        return sidebar;
    }
    
    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(67, 97, 123));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });
        
        return btn;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}