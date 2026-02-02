package frontend.panels;

import backend.model.Subject;
import backend.service.StudyPlannerService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphPanel extends JPanel {
    private StudyPlannerService service;
    
    public GraphPanel(StudyPlannerService service) {
        this.service = service;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Toolbar
        JPanel toolbar = new JPanel();
        toolbar.setBackground(new Color(236, 240, 241));
        
        JButton addBtn = new JButton("Add Subject");
        JButton prereqBtn = new JButton("Add Prerequisite");
        JButton pathBtn = new JButton("Show Study Path");
        
        addBtn.addActionListener(e -> showAddSubjectDialog());
        prereqBtn.addActionListener(e -> showAddPrerequisiteDialog());
        
        toolbar.add(addBtn);
        toolbar.add(prereqBtn);
        toolbar.add(pathBtn);
        
        add(toolbar, BorderLayout.NORTH);
        
        // Canvas
        GraphCanvas canvas = new GraphCanvas();
        add(new JScrollPane(canvas), BorderLayout.CENTER);
    }
    
    private void showAddSubjectDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JSpinner scoreField = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
        
        Object[] message = {
            "Subject ID:", idField,
            "Name:", nameField,
            "Score:", scoreField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Subject", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            service.addSubject(idField.getText(), nameField.getText(), (Integer)scoreField.getValue());
            repaint();
        }
    }
    
    private void showAddPrerequisiteDialog() {
        java.util.List<Subject> subjects = new ArrayList<>(service.getGraph().getSubjects().values());
        if (subjects.size() < 2) return;
        
        JComboBox<Subject> subjBox = new JComboBox<>(subjects.toArray(new Subject[0]));
        JComboBox<Subject> prereqBox = new JComboBox<>(subjects.toArray(new Subject[0]));
        
        Object[] message = {
            "Subject:", subjBox,
            "Prerequisite:", prereqBox
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Prerequisite", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Subject s = (Subject) subjBox.getSelectedItem();
            Subject p = (Subject) prereqBox.getSelectedItem();
            if (s != null && p != null && !s.getId().equals(p.getId())) {
                service.addPrerequisite(s.getId(), p.getId());
                repaint();
            }
        }
    }
    
    class GraphCanvas extends JPanel {
        public GraphCanvas() {
            setPreferredSize(new Dimension(800, 600));
            
            MouseAdapter ma = new MouseAdapter() {
                private Subject selected = null;
                private Point offset = new Point();
                
                public void mousePressed(MouseEvent e) {
                    for (Subject s : service.getGraph().getSubjects().values()) {
                        if (distance(e.getX(), e.getY(), s.getX(), s.getY()) < 30) {
                            selected = s;
                            offset.x = e.getX() - s.getX();
                            offset.y = e.getY() - s.getY();
                            break;
                        }
                    }
                }
                
                public void mouseDragged(MouseEvent e) {
                    if (selected != null) {
                        selected.setX(e.getX() - offset.x);
                        selected.setY(e.getY() - offset.y);
                        repaint();
                    }
                }
                
                public void mouseReleased(MouseEvent e) {
                    selected = null;
                }
                
                private double distance(int x1, int y1, int x2, int y2) {
                    return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
                }
            };
            
            addMouseListener(ma);
            addMouseMotionListener(ma);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw edges
            g2d.setStroke(new BasicStroke(2));
            for (Map.Entry<String, List<String>> entry : service.getGraph().getAdjacencyList().entrySet()) {
                Subject source = service.getGraph().getSubjects().get(entry.getKey());
                if (source == null) continue;
                
                for (String destId : entry.getValue()) {
                    Subject dest = service.getGraph().getSubjects().get(destId);
                    if (dest != null) {
                        g2d.setColor(new Color(149, 165, 166));
                        drawArrow(g2d, source.getX(), source.getY(), dest.getX(), dest.getY());
                    }
                }
            }
            
            // Draw nodes
            for (Subject s : service.getGraph().getSubjects().values()) {
                int size = (int)(40 + (100 - s.getScore()) / 5);
                int green = (int)(s.getScore() * 2.55);
                int red = 255 - green;
                
                g2d.setColor(new Color(red, green, 100));
                g2d.fillOval(s.getX() - size/2, s.getY() - size/2, size, size);
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawOval(s.getX() - size/2, s.getY() - size/2, size, size);
                
                // Text
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(s.getName(), s.getX() - fm.stringWidth(s.getName())/2, s.getY() - size/2 - 5);
                
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                String scoreStr = String.format("%.0f%%", s.getScore());
                fm = g2d.getFontMetrics();
                g2d.drawString(scoreStr, s.getX() - fm.stringWidth(scoreStr)/2, s.getY() + 4);
            }
        }
        
        private void drawArrow(Graphics2D g2d, int x1, int y1, int x2, int y2) {
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int len = 15;
            int endX = x2 - (int)(25 * Math.cos(angle));
            int endY = y2 - (int)(25 * Math.sin(angle));
            
            g2d.drawLine(x1, y1, endX, endY);
            
            Polygon arrow = new Polygon();
            arrow.addPoint(endX, endY);
            arrow.addPoint((int)(endX - len * Math.cos(angle - Math.PI/6)), 
                          (int)(endY - len * Math.sin(angle - Math.PI/6)));
            arrow.addPoint((int)(endX - len * Math.cos(angle + Math.PI/6)), 
                          (int)(endY - len * Math.sin(angle + Math.PI/6)));
            g2d.fill(arrow);
        }
    }
}