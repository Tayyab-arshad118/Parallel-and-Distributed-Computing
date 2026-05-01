import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Server - Starts the RMI Registry and binds the Complaint Service
 * Listens on port 1099 for remote client connections
 * This is the main server component of the distributed system
 */
public class Server {
    
    public static void main(String[] args) {
        try {
            // Print server startup information
            System.out.println("================================================");
            System.out.println("  DISTRIBUTED COMPLAINT MANAGEMENT SYSTEM");
            System.out.println("  Server Component");
            System.out.println("================================================");
            System.out.println();
            
            // Create the complaint service implementation
            ComplaintServiceImpl service = new ComplaintServiceImpl();
            System.out.println("[✓] Complaint Service created");
            
            // Create or locate the RMI registry on port 1099
            try {
                Registry registry = LocateRegistry.createRegistry(1099);
                System.out.println("[✓] RMI Registry created on port 1099");
            } catch (RemoteException e) {
                // Registry might already exist
                System.out.println("[✓] RMI Registry already exists on port 1099");
            }
            
            // Get the registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            // Bind the service to the registry
            registry.rebind("ComplaintService", service);
            System.out.println("[✓] Complaint Service bound to RMI Registry");
            System.out.println();
            
            // Print server details
            System.out.println("================================================");
            System.out.println("  SERVER DETAILS");
            System.out.println("================================================");
            System.out.println("Service Name: ComplaintService");
            System.out.println("Host: localhost");
            System.out.println("Port: 1099");
            System.out.println("Status: RUNNING");
            System.out.println();
            System.out.println("[✓] Server is ready to accept client connections");
            System.out.println("    Press Ctrl+C to stop the server");
            System.out.println("================================================");
            System.out.println();
            
            // Keep the server running
            while (true) {
                Thread.sleep(1000);
            }
            
        } catch (Exception e) {
            System.err.println("[✗] Server Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
