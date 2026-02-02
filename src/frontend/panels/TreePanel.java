package frontend.panels;

import backend.model.Topic;
import backend.service.StudyPlannerService;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;

public class TreePanel extends JPanel {
    private StudyPlannerService service;
    private JTree tree;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    
    public TreePanel(StudyPlannerService service) {
        this.service = service;
        setLayout(new BorderLayout());
        
        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolbar.setBackground(new Color(236, 240, 241));
        
        JButton addBtn = new JButton("Add Module");
        JButton completeBtn = new JButton("Toggle Complete");
        
        addBtn.addActionListener(e -> showAddTopicDialog());
        completeBtn.addActionListener(e -> toggleSelected());
        
        toolbar.add(addBtn);
        toolbar.add(completeBtn);
        add(toolbar, BorderLayout.NORTH);
        
        // Tree
        rootNode = new DefaultMutableTreeNode(service.getSyllabusTree().getRoot().getTitle());
        treeModel = new DefaultTreeModel(rootNode);
        tree = new JTree(treeModel);
        tree.setCellRenderer((TreeCellRenderer) new ColoredTreeCellRenderer());
        tree.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        refreshTree();
        
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }
    
    public void refreshTree() {
        rootNode.removeAllChildren();
        buildTree(rootNode, service.getSyllabusTree().getRoot());
        treeModel.reload();
        expandAll();
    }
    
    private void buildTree(DefaultMutableTreeNode parent, Topic topic) {
        for (Topic child : topic.getChildren()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(child.getTitle());
            node.setUserObject(child.isCompleted()); // Store completion status
            parent.add(node);
            buildTree(node, child);
        }
    }
    
    private void expandAll() {
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
    }
    
    private void showAddTopicDialog() {
        TreePath path = tree.getSelectionPath();
        String parentId = "root";
        if (path != null) {
            // In real app, would map tree node back to topic ID
        }
        
        String title = JOptionPane.showInputDialog("Topic Title:");
        if (title != null && !title.isEmpty()) {
            String id = "t_" + System.currentTimeMillis();
            service.addSyllabusTopic(parentId, id, title);
            refreshTree();
        }
    }
    
    private void toggleSelected() {
        TreePath path = tree.getSelectionPath();
        if (path != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            boolean current = (Boolean) node.getUserObject();
            node.setUserObject(!current);
            tree.repaint();
        }
    }
}