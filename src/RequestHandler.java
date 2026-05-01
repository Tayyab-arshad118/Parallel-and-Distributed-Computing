import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Handles individual client requests in a separate thread
 * Simulates request processing with logging
 * Demonstrates multithreading on the server side
 */
public class RequestHandler implements Runnable {
    
    private String clientId;
    private ComplaintManager manager;
    private String requestType; // "SUBMIT", "VIEW", "COUNT", "STATUS"
    private String complaintText;
    private DateTimeFormatter dateFormatter;
    
    /**
     * Constructor for RequestHandler
     * @param clientId ID of the client making the request
     * @param manager Reference to ComplaintManager
     * @param requestType Type of request
     * @param complaintText Complaint text (if applicable)
     */
    public RequestHandler(String clientId, ComplaintManager manager, 
                         String requestType, String complaintText) {
        this.clientId = clientId;
        this.manager = manager;
        this.requestType = requestType;
        this.complaintText = complaintText;
        this.dateFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    }
    
    /**
     * Run method - processes the request
     */
    @Override
    public void run() {
        String timestamp = LocalDateTime.now().format(dateFormatter);
        String threadName = Thread.currentThread().getName();
        
        try {
            switch (requestType) {
                case "SUBMIT":
                    handleSubmitRequest(timestamp, threadName);
                    break;
                case "VIEW":
                    handleViewRequest(timestamp, threadName);
                    break;
                case "COUNT":
                    handleCountRequest(timestamp, threadName);
                    break;
                default:
                    System.out.println("[" + timestamp + "] Unknown request type");
            }
        } catch (Exception e) {
            System.err.println("[Error in " + threadName + "]: " + e.getMessage());
        }
    }
    
    /**
     * Handle submit complaint request
     */
    private void handleSubmitRequest(String timestamp, String threadName) {
        System.out.println("[" + timestamp + "] [" + threadName + "] Processing SUBMIT request from " + clientId);
        try {
            Thread.sleep(100); // Simulate processing
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + timestamp + "] [" + threadName + "] SUBMIT request completed");
    }
    
    /**
     * Handle view complaints request
     */
    private void handleViewRequest(String timestamp, String threadName) {
        System.out.println("[" + timestamp + "] [" + threadName + "] Processing VIEW request from " + clientId);
        int count = manager.getTotalComplaints();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + timestamp + "] [" + threadName + "] VIEW request completed. Found " + count + " complaints");
    }
    
    /**
     * Handle count request
     */
    private void handleCountRequest(String timestamp, String threadName) {
        System.out.println("[" + timestamp + "] [" + threadName + "] Processing COUNT request from " + clientId);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[" + timestamp + "] [" + threadName + "] COUNT request completed");
    }
}
