import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Remote Interface for Complaint Service
 * Defines the methods available to remote clients
 * This interface extends Remote, making it suitable for Java RMI
 */
public interface ComplaintService extends Remote {
    
    /**
     * Submit a complaint to the server
     * @param userId ID of the user submitting the complaint
     * @param complaintText The complaint message
     * @return Confirmation message with complaint ID
     * @throws RemoteException if remote communication fails
     */
    String submitComplaint(String userId, String complaintText) throws RemoteException;
    
    /**
     * Retrieve all complaints from the server
     * @return List of all complaints in the system
     * @throws RemoteException if remote communication fails
     */
    List<String> viewAllComplaints() throws RemoteException;
    
    /**
     * Get the total count of complaints in the system
     * @return Number of complaints
     * @throws RemoteException if remote communication fails
     */
    int getComplaintCount() throws RemoteException;
    
    /**
     * Get server status with thread information
     * @return Status message
     * @throws RemoteException if remote communication fails
     */
    String getServerStatus() throws RemoteException;
}
