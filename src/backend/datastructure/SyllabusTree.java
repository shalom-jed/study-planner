package backend.datastructure;

import java.util.ArrayList;
import java.util.List;

import backend.model.Topic;

/**
 * N-ary Tree implementation for Syllabus hierarchy
 */
public class SyllabusTree {
    private Topic root;
    
    public SyllabusTree(String rootTitle) {
        this.root = new Topic("root", rootTitle);
    }
    
    public void addTopic(String parentId, Topic newTopic) {
        Topic parent = findTopic(parentId);
        if (parent != null) {
            parent.addChild(newTopic);
        }
    }
    
    public Topic findTopic(String id) {
        return root.findTopic(id);
    }
    
    public void removeTopic(String id) {
        Topic target = findTopic(id);
        if (target != null && target.getParent() != null) {
            target.getParent().getChildren().remove(target);
        }
    }
    
    /**
     * Pre-order traversal for syllabus display
     */
    public List<Topic> getPreOrderTraversal() {
        List<Topic> result = new ArrayList<>();
        preOrder(root, result);
        return result;
    }
    
    private void preOrder(Topic node, List<Topic> list) {
        list.add(node);
        for (Topic child : node.getChildren()) {
            preOrder(child, list);
        }
    }
    
    public Topic getRoot() { return root; }
    
    public double getOverallProgress() {
        return root.getCompletionPercentage();
    }
}