package frontend.panels;

import backend.service.StudyPlannerService;
import javax.swing.*;
import javax.swing.border.MatteBorder;

import java.awt.*;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private StudyPlannerService service;
    private JLabel subjectsLabel, pathLabel, weakLabel, completionLabel;
    
    public DashboardPanel(StudyPlannerService service) {
        this.service = service;
        setLayout(new GridLayout(2, 2, 20, 20));
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        setBackground(new Color(245, 246, 250));
        
        add(createCard("Total Subjects", "0", new Color(231, 76, 60)));
        add(createCard("Study Path Steps", "0", new Color(46, 204, 113)));
        add(createCard("Weak Topics", "0", new Color(241, 196, 15)));
        add(createCard("Overall Completion", "0%", new Color(52, 152, 219)));
        
        // Refresh timer
        Timer timer = new Timer(1000, e -> refreshStats());
        timer.start();
    }
    
    private JPanel createCard(String title, String initialValue, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 5, 0, 0, accentColor),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(127, 140, 141));
        
        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        
        // Store references for updates
        switch(title) {
            case "Total Subjects": subjectsLabel = valueLabel; break;
            case "Study Path Steps": pathLabel = valueLabel; break;
            case "Weak Topics": weakLabel = valueLabel; break;
            case "Overall Completion": completionLabel = valueLabel; break;
        }
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    public void refreshStats() {
        Map<String, Object> stats = service.getDashboardStats();
        subjectsLabel.setText(String.valueOf(stats.get("totalSubjects")));
        pathLabel.setText(String.valueOf(stats.get("studyPathLength")));
        weakLabel.setText(String.valueOf(stats.get("weakTopicsCount")));
        completionLabel.setText(String.format("%.1f%%", stats.get("completionPercentage")));
    }
}