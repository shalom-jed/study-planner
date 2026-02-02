package backend.datastructure;

import backend.model.Subject;
import java.util.*;

/**
 * Graph implementation using Adjacency List
 * Handles prerequisite relationships and topological sorting
 */
public class SubjectGraph {
    private Map<String, Subject> subjects;
    private Map<String, List<String>> adjacencyList; // prerequisite -> list of subjects that need it
    
    public SubjectGraph() {
        subjects = new HashMap<>();
        adjacencyList = new HashMap<>();
    }
    
    public void addSubject(Subject subject) {
        subjects.put(subject.getId(), subject);
        if (!adjacencyList.containsKey(subject.getId())) {
            adjacencyList.put(subject.getId(), new ArrayList<>());
        }
    }
    
    public void addPrerequisite(String subjectId, String prerequisiteId) {
        if (subjects.containsKey(subjectId) && subjects.containsKey(prerequisiteId)) {
            adjacencyList.get(prerequisiteId).add(subjectId);
        }
    }
    
    /**
     * Kahn's Algorithm for Topological Sorting
     * Returns valid study order respecting prerequisites
     */
    public List<Subject> getTopologicalOrder() {
        Map<String, Integer> inDegree = new HashMap<>();
        
        // Initialize in-degrees
        for (String id : subjects.keySet()) {
            inDegree.put(id, 0);
        }
        
        // Calculate in-degrees
        for (List<String> edges : adjacencyList.values()) {
            for (String dest : edges) {
                inDegree.put(dest, inDegree.get(dest) + 1);
            }
        }
        
        // Queue for BFS
        Queue<Subject> queue = new LinkedList<>();
        for (String id : subjects.keySet()) {
            if (inDegree.get(id) == 0) {
                queue.add(subjects.get(id));
            }
        }
        
        List<Subject> result = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            Subject current = queue.poll();
            result.add(current);
            
            // Reduce in-degree for neighbors
            for (String neighbor : adjacencyList.get(current.getId())) {
                int newDegree = inDegree.get(neighbor) - 1;
                inDegree.put(neighbor, newDegree);
                if (newDegree == 0) {
                    queue.add(subjects.get(neighbor));
                }
            }
        }
        
        return result;
    }
    
    public List<Subject> getPrerequisites(String subjectId) {
        List<Subject> prereqs = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            if (entry.getValue().contains(subjectId)) {
                prereqs.add(subjects.get(entry.getKey()));
            }
        }
        return prereqs;
    }
    
    public boolean hasCycle() {
        return getTopologicalOrder().size() != subjects.size();
    }
    
    public Map<String, Subject> getSubjects() { return subjects; }
    public Map<String, List<String>> getAdjacencyList() { return adjacencyList; }
    
    public void removeSubject(String id) {
        subjects.remove(id);
        adjacencyList.remove(id);
        for (List<String> list : adjacencyList.values()) {
            list.remove(id);
        }
    }
}