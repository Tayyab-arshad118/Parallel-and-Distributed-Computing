import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Manages storage and retrieval of complaints
 * Uses thread-safe mechanisms to handle concurrent access
 * Implements READ-WRITE lock pattern for optimal performance
 */
public class ComplaintManager {
    
    // Data structure to store complaints
    private ArrayList<String> complaints;
    
    // Thread-safe lock for concurrent access
    private ReentrantReadWriteLock lock;
    
    // Counter for unique complaint IDs
    private int complaintIdCounter;
    
    // To track thread information
    private int totalRequestsProcessed;
    
    // DateTimeFormatter for timestamps
    private DateTimeFormatter dateFormatter;
    
    /**
     * Constructor initializes the complaint manager
     */
    public ComplaintManager() {
        this.complaints = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.complaintIdCounter = 1000;
        this.totalRequestsProcessed = 0;
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * Add a complaint to the system (WRITE operation)
     * @param userId ID of the user
     * @param complaintText The complaint message
     * @return Confirmation with complaint ID
     */
    public String addComplaint(String userId, String complaintText) {
        lock.writeLock().lock();
        try {
            Thread.sleep(50); // Simulate processing time
            
            complaintIdCounter++;
            String timestamp = LocalDateTime.now().format(dateFormatter);
            String complaintEntry = String.format(
                "ID: %d | User: %s | Time: %s | Complaint: %s | Thread: %s",
                complaintIdCounter,
                userId,
                timestamp,
                complaintText,
                Thread.currentThread().getName()
            );
            
            complaints.add(complaintEntry);
            totalRequestsProcessed++;
            
            return String.format("✓ Complaint #%d submitted successfully by %s",
                    complaintIdCounter, userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Complaint processing interrupted";
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Retrieve all complaints (READ operation)
     * @return List of all complaints
     */
    public List<String> getAllComplaints() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(complaints); // Return copy for safety
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get total count of complaints
     * @return Number of complaints
     */
    public int getTotalComplaints() {
        lock.readLock().lock();
        try {
            return complaints.size();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    /**
     * Get total requests processed
     * @return Number of requests
     */
    public int getTotalRequests() {
        lock.readLock().lock();
        try {
            return totalRequestsProcessed;
        } finally {
            lock.readLock().unlock();
        }
    }
}
