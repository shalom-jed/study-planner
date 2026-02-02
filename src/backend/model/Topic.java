package backend.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree node model for Syllabus hierarchy
 */
public class Topic {
    private String id;
    private String title;
    private boolean completed;
    private List<Topic> children;
    private Topic parent;
    
    public Topic(String id, String title) {
        this.id = id;
        this.title = title;
        this.children = new ArrayList<>();
        this.completed = false;
    }
    
    public void addChild(Topic child) {
        child.parent = this;
        children.add(child);
    }
    
    public double getCompletionPercentage() {
        if (children.isEmpty()) {
            return completed ? 100.0 : 0.0;
        }
        double sum = 0;
        for (Topic child : children) {
            sum += child.getCompletionPercentage();
        }
        return sum / children.size();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public List<Topic> getChildren() { return children; }
    public Topic getParent() { return parent; }
    
    public Topic findTopic(String searchId) {
        if (this.id.equals(searchId)) return this;
        for (Topic child : children) {
            Topic found = child.findTopic(searchId);
            if (found != null) return found;
        }
        return null;
    }
}