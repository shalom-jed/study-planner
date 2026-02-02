package backend.datastructure;

import backend.model.Subject;
import java.util.*;

/**
 * Graph implementation using Adjacency List
 * Handles prerequisite relationships and topological sorting
 */
public class SubjectGraph {
    private Map<String, Subject> subjects;
    private Map<String, List<String>> adjacencyList; 
    
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
     * LOGIC UPGRADE: Check if adding a dependency creates a cycle.
     * Returns true if safe to add, false if it creates a loop.
     */
    public boolean canAddDependency(String subjectId, String prerequisiteId) {
        // If they are the same, it's a self-loop
        if (subjectId.equals(prerequisiteId)) return false;

        // Temporarily add edge
        List<String> deps = adjacencyList.get(prerequisiteId);
        if (deps == null) deps = new ArrayList<>();
        deps.add(subjectId);
        adjacencyList.put(prerequisiteId, deps);

        // Check for cycle
        boolean hasCycle = hasCycle();

        // Remove edge (backtrack)
        deps.remove(subjectId);

        return !hasCycle;
    }
    
    /**
     * Kahn's Algorithm for Topological Sorting
     */
    public List<Subject> getTopologicalOrder() {
        Map<String, Integer> inDegree = new HashMap<>();
        
        for (String id : subjects.keySet()) inDegree.put(id, 0);
        
        for (List<String> edges : adjacencyList.values()) {
            for (String dest : edges) {
                inDegree.put(dest, inDegree.getOrDefault(dest, 0) + 1);
            }
        }
        
        Queue<Subject> queue = new LinkedList<>();
        for (String id : subjects.keySet()) {
            if (inDegree.get(id) == 0) queue.add(subjects.get(id));
        }
        
        List<Subject> result = new ArrayList<>();
        int count = 0;

        while (!queue.isEmpty()) {
            Subject current = queue.poll();
            result.add(current);
            count++;
            
            if (adjacencyList.containsKey(current.getId())) {
                for (String neighbor : adjacencyList.get(current.getId())) {
                    inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                    if (inDegree.get(neighbor) == 0) {
                        queue.add(subjects.get(neighbor));
                    }
                }
            }
        }
        
        // If count != subjects.size(), there is a cycle (or disconnected parts in cycle)
        return count == subjects.size() ? result : new ArrayList<>(); // Return empty if cycle detected
    }
    
    public boolean hasCycle() {
        // If topological sort returns fewer nodes than exist, we have a cycle
        return getTopologicalOrder().size() != subjects.size();
    }
    
    public Map<String, Subject> getSubjects() { return subjects; }
    public Map<String, List<String>> getAdjacencyList() { return adjacencyList; }
}