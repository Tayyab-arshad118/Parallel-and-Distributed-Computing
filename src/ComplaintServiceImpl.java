import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Implementation of ComplaintService Remote Interface
 * Extends UnicastRemoteObject to make it a remote object
 * Implements all remote methods defined in ComplaintService interface
 */
public class ComplaintServiceImpl extends UnicastRemoteObject implements ComplaintService {
    
    private static final long serialVersionUID = 1L;
    
    // Reference to the complaint manager
    private ComplaintManager complaintManager;
    
    // Thread pool for handling client requests
    private ExecutorService threadPool;
    
    /**
     * Constructor initializes the service
     * @throws RemoteException if remote object initialization fails
     */
    public ComplaintServiceImpl() throws RemoteException {
        super();
        this.complaintManager = new ComplaintManager();
        // Create a thread pool with 5 threads to handle concurrent requests
        this.threadPool = Executors.newFixedThreadPool(5);
    }
    
    /**
     * Submit a complaint via RMI
     * @param userId ID of the user submitting the complaint
     * @param complaintText The complaint message
     * @return Confirmation message with complaint ID
     * @throws RemoteException if remote communication fails
     */
    @Override
    public String submitComplaint(String userId, String complaintText) throws RemoteException {
        try {
            // Validate input
            if (userId == null || userId.trim().isEmpty()) {
                return "Error: User ID cannot be empty";
            }
            if (complaintText == null || complaintText.trim().isEmpty()) {
                return "Error: Complaint text cannot be empty";
            }
            
            // Submit to complaint manager
            String result = complaintManager.addComplaint(userId, complaintText);
            
            // Submit request handler to thread pool for async processing
            RequestHandler handler = new RequestHandler(userId, complaintManager, "SUBMIT", complaintText);
            threadPool.execute(handler);
            
            return result;
        } catch (Exception e) {
            throw new RemoteException("Error submitting complaint: " + e.getMessage(), e);
        }
    }
    
    /**
     * Retrieve all complaints via RMI
     * @return List of all complaints in the system
     * @throws RemoteException if remote communication fails
     */
    @Override
    public List<String> viewAllComplaints() throws RemoteException {
        try {
            // Submit request handler to thread pool
            RequestHandler handler = new RequestHandler("all_users", complaintManager, "VIEW", "");
            threadPool.execute(handler);
            
            return complaintManager.getAllComplaints();
        } catch (Exception e) {
            throw new RemoteException("Error retrieving complaints: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get the total count of complaints via RMI
     * @return Number of complaints
     * @throws RemoteException if remote communication fails
     */
    @Override
    public int getComplaintCount() throws RemoteException {
        try {
            // Submit request handler to thread pool
            RequestHandler handler = new RequestHandler("all_users", complaintManager, "COUNT", "");
            threadPool.execute(handler);
            
            return complaintManager.getTotalComplaints();
        } catch (Exception e) {
            throw new RemoteException("Error getting complaint count: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get server status information
     * @return Status message with server details
     * @throws RemoteException if remote communication fails
     */
    @Override
    public String getServerStatus() throws RemoteException {
        try {
            int totalComplaints = complaintManager.getTotalComplaints();
            int totalRequests = complaintManager.getTotalRequests();
            int threadCount = Thread.activeCount();
            
            return String.format(
                "=== Server Status ===\n" +
                "Total Complaints: %d\n" +
                "Total Requests Processed: %d\n" +
                "Active Threads: %d\n" +
                "Status: RUNNING",
                totalComplaints, totalRequests, threadCount
            );
        } catch (Exception e) {
            throw new RemoteException("Error getting server status: " + e.getMessage(), e);
        }
    }
    
    /**
     * Shutdown the service (cleanup)
     */
    public void shutdown() {
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            System.out.println("Thread pool shut down gracefully");
        }
    }
}
