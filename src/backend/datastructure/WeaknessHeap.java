package backend.datastructure;

import backend.model.Subject;
import java.util.ArrayList;
import java.util.List;

/**
 * Max-Heap implementation for prioritizing weak topics
 * Higher weakness score = Higher priority
 */
public class WeaknessHeap {
    private List<HeapNode> heap;
    
    public static class HeapNode {
        private String topicId;
        private String topicName;
        private double weaknessScore; // 100 - performance
        private String subjectId;
        private Subject subjectRef;
        
        public HeapNode(String topicId, String topicName, double weaknessScore, String subjectId, Subject subject) {
            this.topicId = topicId;
            this.topicName = topicName;
            this.weaknessScore = weaknessScore;
            this.subjectId = subjectId;
            this.subjectRef = subject;
        }
        
        // Getters
        public String getTopicId() { return topicId; }
        public String getTopicName() { return topicName; }
        public double getWeaknessScore() { return weaknessScore; }
        public String getSubjectId() { return subjectId; }
        public Subject getSubject() { return subjectRef; }
    }
    
    public WeaknessHeap() {
        heap = new ArrayList<>();
    }
    
    public void insert(HeapNode node) {
        heap.add(node);
        heapifyUp(heap.size() - 1);
    }
    
    public HeapNode extractMax() {
        if (heap.isEmpty()) return null;
        
        HeapNode max = heap.get(0);
        HeapNode last = heap.remove(heap.size() - 1);
        
        if (!heap.isEmpty()) {
            heap.set(0, last);
            heapifyDown(0);
        }
        
        return max;
    }
    
    public HeapNode peekMax() {
        return heap.isEmpty() ? null : heap.get(0);
    }
    
    public boolean isEmpty() {
        return heap.isEmpty();
    }
    
    public int size() {
        return heap.size();
    }
    
    /**
     * Get all nodes sorted by weakness (descending)
     */
    public List<HeapNode> getSortedNodes() {
        List<HeapNode> sorted = new ArrayList<>(heap);
        sorted.sort((a, b) -> Double.compare(b.getWeaknessScore(), a.getWeaknessScore()));
        return sorted;
    }
    
    private void heapifyUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / 2;
            if (heap.get(index).getWeaknessScore() > heap.get(parent).getWeaknessScore()) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }
    
    private void heapifyDown(int index) {
        int size = heap.size();
        
        while (true) {
            int largest = index;
            int left = 2 * index + 1;
            int right = 2 * index + 2;
            
            if (left < size && heap.get(left).getWeaknessScore() > heap.get(largest).getWeaknessScore()) {
                largest = left;
            }
            if (right < size && heap.get(right).getWeaknessScore() > heap.get(largest).getWeaknessScore()) {
                largest = right;
            }
            
            if (largest != index) {
                swap(index, largest);
                index = largest;
            } else {
                break;
            }
        }
    }
    
    private void swap(int i, int j) {
        HeapNode temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}