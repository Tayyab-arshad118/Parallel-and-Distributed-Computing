import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

/**
 * Client - Command-line client for the Complaint Management System
 * Connects to the remote server via RMI
 * Allows users to submit and view complaints
 */
public class Client {
    
    private ComplaintService service;
    private Scanner scanner;
    private String userId;
    
    /**
     * Constructor initializes the client
     */
    public Client(String userId) {
        this.userId = userId;
        this.scanner = new Scanner(System.in);
        this.service = null;
    }
    
    /**
     * Connect to the remote server
     */
    public boolean connect() {
        try {
            // Locate the RMI registry
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            
            // Look up the service
            service = (ComplaintService) registry.lookup("ComplaintService");
            System.out.println("[✓] Connected to Complaint Service");
            return true;
        } catch (Exception e) {
            System.err.println("[✗] Connection Error: " + e.getMessage());
            System.err.println("    Make sure the server is running on localhost:1099");
            return false;
        }
    }
    
    /**
     * Display main menu
     */
    private void displayMenu() {
        System.out.println("\n=== Complaint Management System ===");
        System.out.println("User: " + userId);
        System.out.println("1. Submit Complaint");
        System.out.println("2. View All Complaints");
        System.out.println("3. Check Complaint Count");
        System.out.println("4. Server Status");
        System.out.println("5. Exit");
        System.out.print("Enter choice: ");
    }
    
    /**
     * Submit a complaint
     */
    private void submitComplaint() {
        try {
            System.out.print("\nEnter complaint: ");
            String complaint = scanner.nextLine();
            
            if (complaint.trim().isEmpty()) {
                System.out.println("Error: Complaint cannot be empty");
                return;
            }
            
            String result = service.submitComplaint(userId, complaint);
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Error submitting complaint: " + e.getMessage());
        }
    }
    
    /**
     * View all complaints
     */
    private void viewComplaints() {
        try {
            List<String> complaints = service.viewAllComplaints();
            
            if (complaints.isEmpty()) {
                System.out.println("\nNo complaints in the system yet.");
            } else {
                System.out.println("\n=== All Complaints ===");
                for (int i = 0; i < complaints.size(); i++) {
                    System.out.println((i + 1) + ". " + complaints.get(i));
                }
            }
        } catch (Exception e) {
            System.err.println("Error viewing complaints: " + e.getMessage());
        }
    }
    
    /**
     * Check complaint count
     */
    private void checkCount() {
        try {
            int count = service.getComplaintCount();
            System.out.println("\nTotal complaints in system: " + count);
        } catch (Exception e) {
            System.err.println("Error getting complaint count: " + e.getMessage());
        }
    }
    
    /**
     * Get server status
     */
    private void getStatus() {
        try {
            String status = service.getServerStatus();
            System.out.println("\n" + status);
        } catch (Exception e) {
            System.err.println("Error getting server status: " + e.getMessage());
        }
    }
    
    /**
     * Run the client
     */
    public void run() {
        while (true) {
            displayMenu();
            String choice = scanner.nextLine();
            
            switch (choice.trim()) {
                case "1":
                    submitComplaint();
                    break;
                case "2":
                    viewComplaints();
                    break;
                case "3":
                    checkCount();
                    break;
                case "4":
                    getStatus();
                    break;
                case "5":
                    System.out.println("\nGoodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("  DISTRIBUTED COMPLAINT MANAGEMENT SYSTEM");
        System.out.println("  Command-Line Client");
        System.out.println("================================================");
        
        String userId;
        if (args.length > 0) {
            userId = args[0];
        } else {
            System.out.print("Enter your User ID: ");
            Scanner tempScanner = new Scanner(System.in);
            userId = tempScanner.nextLine();
        }
        
        Client client = new Client(userId);
        
        if (client.connect()) {
            client.run();
        }
    }
}
