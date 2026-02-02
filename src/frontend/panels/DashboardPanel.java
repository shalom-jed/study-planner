package frontend.panels;

import backend.service.StudyPlannerService;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private StudyPlannerService service;
    private ModernCard subjectCard, pathCard, weakCard, completionCard;

    public DashboardPanel(StudyPlannerService service) {
        this.service = service;
        setLayout(new GridLayout(2, 2, 30, 30)); // Bigger gaps for modern look
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        setBackground(new Color(240, 242, 245)); // Light grey background

        // Initialize Cards with Gradients
        subjectCard = new ModernCard("Total Subjects", "0", new Color(62, 161, 219), new Color(44, 62, 80));
        pathCard = new ModernCard("Study Path Steps", "0", new Color(46, 204, 113), new Color(39, 174, 96));
        weakCard = new ModernCard("Weak Topics", "0", new Color(241, 196, 15), new Color(243, 156, 18));
        completionCard = new ModernCard("Completion", "0%", new Color(155, 89, 182), new Color(142, 68, 173));

        add(subjectCard);
        add(pathCard);
        add(weakCard);
        add(completionCard);

        // Refresh timer (every 1 second)
        Timer timer = new Timer(1000, e -> refreshStats());
        timer.start();
        refreshStats(); // Initial load
    }

    public void refreshStats() {
        Map<String, Object> stats = service.getDashboardStats();
        subjectCard.setValue(String.valueOf(stats.get("totalSubjects")));
        pathCard.setValue(String.valueOf(stats.get("studyPathLength")));
        weakCard.setValue(String.valueOf(stats.get("weakTopicsCount")));
        completionCard.setValue(String.format("%.1f%%", stats.get("completionPercentage")));
        repaint();
    }

    // Inner class for custom painted cards
    private class ModernCard extends JPanel {
        private String title;
        private String value;
        private Color color1, color2;

        public ModernCard(String title, String value, Color c1, Color c2) {
            this.title = title;
            this.value = value;
            this.color1 = c1;
            this.color2 = c2;
            setOpaque(false); // Enable transparency for rounded corners
        }

        public void setValue(String value) {
            this.value = value;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            // Draw Shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(5, 5, w - 10, h - 10, 25, 25);

            // Draw Gradient Background
            GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w - 10, h - 10, 25, 25);

            // Draw Text
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString(title, 25, 45);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(value);
            g2.drawString(value, (w - 10 - textW) / 2, (h - 10) / 2 + 15);
        }
    }
}