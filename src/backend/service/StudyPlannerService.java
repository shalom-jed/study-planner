package backend.service;

import backend.datastructure.*;
import backend.model.*;
import java.util.*;

/**
 * Service layer coordinating between data structures
 */
public class StudyPlannerService {
    private SubjectGraph graph;
    private SyllabusTree syllabusTree;
    private WeaknessHeap weaknessHeap;
    
    public StudyPlannerService() {
        graph = new SubjectGraph();
        syllabusTree = new SyllabusTree("Curriculum Root");
        weaknessHeap = new WeaknessHeap();
    }
    
    // Graph Operations
    public void addSubject(String id, String name, double score) {
        Subject subj = new Subject(id, name, score);
        graph.addSubject(subj);
        
        // Auto-add to heap if weak
        if (score < 75) {
            addWeaknessNode(id, name + " (General)", 100 - score, id, subj);
        }
    }
    
    public void addPrerequisite(String subjectId, String prereqId) {
        graph.addPrerequisite(subjectId, prereqId);
    }
    
    public List<Subject> getStudyPath() {
        return graph.getTopologicalOrder();
    }
    
    // Tree Operations
    public void addSyllabusTopic(String parentId, String topicId, String title) {
        Topic topic = new Topic(topicId, title);
        syllabusTree.addTopic(parentId, topic);
    }
    
    public void toggleTopicCompletion(String topicId) {
        Topic topic = syllabusTree.findTopic(topicId);
        if (topic != null) {
            topic.setCompleted(!topic.isCompleted());
        }
    }
    
    // Heap Operations
    public void addWeaknessNode(String topicId, String topicName, double weakness, String subjectId, Subject subject) {
        weaknessHeap.insert(new WeaknessHeap.HeapNode(topicId, topicName, weakness, subjectId, subject));
    }
    
    public WeaknessHeap.HeapNode getNextWeakTopic() {
        return weaknessHeap.extractMax();
    }
    
    public List<WeaknessHeap.HeapNode> getAllWeaknesses() {
        return weaknessHeap.getSortedNodes();
    }
    
    // Statistics
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSubjects", graph.getSubjects().size());
        stats.put("studyPathLength", getStudyPath().size());
        stats.put("weakTopicsCount", weaknessHeap.size());
        stats.put("completionPercentage", syllabusTree.getOverallProgress());
        return stats;
    }
    
    // Getters
    public SubjectGraph getGraph() { return graph; }
    public SyllabusTree getSyllabusTree() { return syllabusTree; }
    public WeaknessHeap getWeaknessHeap() { return weaknessHeap; }
    
    // Sample Data Initialization
    public void loadSampleData() {
        // Subjects
        addSubject("CS101", "Programming Basics", 85);
        addSubject("CS102", "Data Structures", 65);
        addSubject("CS201", "Algorithms", 45);
        addSubject("CS202", "Databases", 70);
        addSubject("CS301", "Web Development", 80);
        
        // Prerequisites
        addPrerequisite("CS102", "CS101");
        addPrerequisite("CS201", "CS102");
        addPrerequisite("CS202", "CS102");
        addPrerequisite("CS301", "CS101");
        addPrerequisite("CS301", "CS202");
        
        // Syllabus
        addSyllabusTopic("root", "mod1", "Programming Fundamentals");
        addSyllabusTopic("mod1", "t1", "Variables & Types");
        addSyllabusTopic("mod1", "t2", "Control Structures");
        
        addSyllabusTopic("root", "mod2", "Advanced Data Structures");
        addSyllabusTopic("mod2", "t3", "Trees");
        addSyllabusTopic("mod2", "t4", "Graphs");
    }
}