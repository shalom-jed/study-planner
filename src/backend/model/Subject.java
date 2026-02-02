package backend.model;

/**
 * Model class representing a Subject/Node in the Graph
 */
public class Subject {
    private String id;
    private String name;
    private double score; // 0-100
    private int x, y; // Coordinates for visualization
    
    public Subject(String id, String name, double score) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.x = (int)(Math.random() * 400) + 50;
        this.y = (int)(Math.random() * 300) + 50;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public double getWeaknessScore() {
        return 100.0 - score;
    }
    
    @Override
    public String toString() {
        return name + " (" + String.format("%.0f%%", score) + ")";
    }
}