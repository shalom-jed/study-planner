package frontend.panels;

import backend.datastructure.WeaknessHeap;
import backend.model.Subject;
import backend.service.StudyPlannerService;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import java.awt.*;

public class PlannerPanel extends JPanel {
    private StudyPlannerService service;
    private DefaultListModel<String> pathModel;
    private DefaultListModel<String> weakModel;
    
    public PlannerPanel(StudyPlannerService service) {
        this.service = service;
        setLayout(new GridLayout(1, 2, 15, 0));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 246, 250));
        
        // Left: Study Path (Topological)
        JPanel pathPanel = createListPanel("Study Path (Topological Sort)", true);
        pathModel = new DefaultListModel<>();
        JList<String> pathList = new JList<>(pathModel);
        pathPanel.add(new JScrollPane(pathList), BorderLayout.CENTER);
        
        JButton refreshPath = new JButton("Refresh Path");
        refreshPath.addActionListener(e -> updateStudyPath());
        pathPanel.add(refreshPath, BorderLayout.SOUTH);
        
        // Right: Weakness Heap
        JPanel weakPanel = createListPanel("Priority Queue (Max-Heap)", false);
        weakModel = new DefaultListModel<>();
        JList<String> weakList = new JList<>(weakModel);
        weakList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                if (!isSelected) {
                    label.setBackground(new Color(255, 200 - (index * 10), 200 - (index * 10)));
                }
                return label;
            }
        });
        weakPanel.add(new JScrollPane(weakList), BorderLayout.CENTER);
        
        JButton studyNext = new JButton("Study Weakest Topic");
        studyNext.setBackground(new Color(231, 76, 60));
        studyNext.setForeground(Color.WHITE);
        studyNext.addActionListener(e -> studyNext());
        weakPanel.add(studyNext, BorderLayout.SOUTH);
        
        add(pathPanel);
        add(weakPanel);
        
        updateStudyPath();
        updateWeaknessList();
    }
    
    private JPanel createListPanel(String title, boolean path) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 14)
        ));
        return panel;
    }
    
    public void updateStudyPath() {
        pathModel.clear();
        java.util.List<Subject> path = service.getStudyPath();
        int i = 1;
        for (Subject s : path) {
            pathModel.addElement(i++ + ". " + s.getName() + " [Score: " + String.format("%.0f", s.getScore()) + "%]");
        }
    }
    
    public void updateWeaknessList() {
        weakModel.clear();
        java.util.List<WeaknessHeap.HeapNode> nodes = service.getAllWeaknesses();
        int rank = 1;
        for (WeaknessHeap.HeapNode node : nodes) {
            weakModel.addElement(String.format("#%d %s (Weakness: %.1f)", 
                rank++, node.getTopicName(), node.getWeaknessScore()));
        }
    }
    
    private void studyNext() {
        WeaknessHeap.HeapNode next = service.getNextWeakTopic();
        if (next != null) {
            JOptionPane.showMessageDialog(this, 
                "Next topic to study:\n" + next.getTopicName() + 
                "\nSubject: " + next.getSubjectId() +
                "\nPriority: " + String.format("%.1f", next.getWeaknessScore()),
                "Study Recommendation", JOptionPane.INFORMATION_MESSAGE);
            updateWeaknessList();
        }
    }
}