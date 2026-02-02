package frontend.renders;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Custom renderer for JTree showing completion status
 */
public class ColoredTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        
        JLabel label = (JLabel) super.getTreeCellRendererComponent(
            tree, value, selected, expanded, leaf, row, hasFocus);
        
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        String text = node.toString();
        
        // Check if completed (user object stores completion status)
        if (node.getUserObject() instanceof Boolean && (Boolean) node.getUserObject()) {
            label.setForeground(new Color(46, 204, 113)); // Green
            label.setText("✓ " + text);
        } else {
            label.setForeground(Color.BLACK);
            label.setText("○ " + text);
        }
        
        return label;
    }
}