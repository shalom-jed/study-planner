package frontend.panels;

import backend.model.Subject;
import backend.service.StudyPlannerService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphPanel extends JPanel {
    private StudyPlannerService service;
    private GraphCanvas canvas;
    
    public GraphPanel(StudyPlannerService service) {
        this.service = service;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Toolbar
        JPanel toolbar = new JPanel();
        toolbar.setBackground(new Color(236, 240, 241));
        
        JButton addBtn = new JButton("Add Subject");
        JButton prereqBtn = new JButton("Add Prerequisite");
        JButton centerBtn = new JButton("Reset View");
        
        addBtn.addActionListener(e -> showAddSubjectDialog());
        prereqBtn.addActionListener(e -> showAddPrerequisiteDialog());
        centerBtn.addActionListener(e -> canvas.resetView());
        
        toolbar.add(addBtn);
        toolbar.add(prereqBtn);
        toolbar.add(centerBtn);
        
        add(toolbar, BorderLayout.NORTH);
        
        // Canvas
        canvas = new GraphCanvas();
        add(canvas, BorderLayout.CENTER);
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
            if (!idField.getText().isEmpty()) {
                service.addSubject(idField.getText(), nameField.getText(), (Integer)scoreField.getValue());
                repaint();
            }
        }
    }
    
    private void showAddPrerequisiteDialog() {
        java.util.List<Subject> subjects = new ArrayList<>(service.getGraph().getSubjects().values());
        if (subjects.size() < 2) return;
        
        JComboBox<Subject> subjBox = new JComboBox<>(subjects.toArray(new Subject[0]));
        JComboBox<Subject> prereqBox = new JComboBox<>(subjects.toArray(new Subject[0]));
        
        Object[] message = {
            "Subject (Depends on):", subjBox,
            "Prerequisite (Required):", prereqBox
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add Prerequisite", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Subject s = (Subject) subjBox.getSelectedItem();
            Subject p = (Subject) prereqBox.getSelectedItem();
            
            if (s != null && p != null) {
                // LOGIC UPGRADE: Check for cycles before adding
                if (service.getGraph().canAddDependency(s.getId(), p.getId())) {
                    service.addPrerequisite(s.getId(), p.getId());
                    repaint();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Cannot add this prerequisite!\nIt would create a circular logic loop.", 
                        "Logic Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    class GraphCanvas extends JPanel {
        private double zoomFactor = 1.0;
        private double translateX = 0;
        private double translateY = 0;
        private Point lastMousePt;
        
        public GraphCanvas() {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            
            MouseAdapter ma = new MouseAdapter() {
                private Subject selected = null;
                
                public void mousePressed(MouseEvent e) {
                    lastMousePt = e.getPoint();
                    // Transform click to model coordinates
                    double modelX = (e.getX() - translateX) / zoomFactor;
                    double modelY = (e.getY() - translateY) / zoomFactor;

                    for (Subject s : service.getGraph().getSubjects().values()) {
                        if (distance(modelX, modelY, s.getX(), s.getY()) < 30) {
                            selected = s;
                            break;
                        }
                    }
                }
                
                public void mouseDragged(MouseEvent e) {
                    double dx = e.getX() - lastMousePt.getX();
                    double dy = e.getY() - lastMousePt.getY();

                    if (selected != null) {
                        selected.setX((int)(selected.getX() + dx / zoomFactor));
                        selected.setY((int)(selected.getY() + dy / zoomFactor));
                    } else {
                        // Pan the canvas
                        translateX += dx;
                        translateY += dy;
                    }
                    lastMousePt = e.getPoint();
                    repaint();
                }
                
                public void mouseReleased(MouseEvent e) {
                    selected = null;
                }
                
                // INTERACTION UPGRADE: Zoom with mouse wheel
                public void mouseWheelMoved(MouseWheelEvent e) {
                    if (e.getWheelRotation() < 0) {
                        zoomFactor *= 1.1;
                    } else {
                        zoomFactor *= 0.9;
                    }
                    repaint();
                }
            };
            
            addMouseListener(ma);
            addMouseMotionListener(ma);
            addMouseWheelListener(ma); // Add the wheel listener
        }
        
        public void resetView() {
            zoomFactor = 1.0;
            translateX = 0;
            translateY = 0;
            repaint();
        }

        private double distance(double x1, double y1, double x2, double y2) {
            return Math.sqrt(Math.pow(x2-x1, 2) + Math.pow(y2-y1, 2));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Apply Zoom and Pan
            AffineTransform oldTransform = g2d.getTransform();
            g2d.translate(translateX, translateY);
            g2d.scale(zoomFactor, zoomFactor);

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
                int green = Math.max(0, Math.min(255, (int)(s.getScore() * 2.55)));
                int red = Math.max(0, Math.min(255, 255 - green));
                
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
            
            // Reset transform for other components
            g2d.setTransform(oldTransform);
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