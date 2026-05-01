import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

/**
 * ClientGUI - Swing-based GUI client for the Complaint Management System
 * Provides a user-friendly interface for interacting with the remote service
 * Features: Submit complaints, view complaints, check count, monitor server status
 */
public class ClientGUI extends JFrame {
    
    private ComplaintService service;
    private String userId;
    
    // GUI Components
    private JTextField userIdField;
    private JTextField complaintInputField;
    private JTextArea outputArea;
    private JLabel statusLabel;
    private JButton submitButton;
    private JButton viewButton;
    private JButton countButton;
    private JButton statusButton;
    private JButton clearButton;
    
    /**
     * Constructor initializes the GUI
     */
    public ClientGUI() {
        setTitle("Distributed Complaint Management System - GUI Client");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel - Header and connection info
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Distributed Complaint Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel connectionPanel = new JPanel();
        connectionPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField(15);
        userIdField.setText("USER_" + System.currentTimeMillis() % 10000);
        connectionPanel.add(userIdField);
        
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> connectToServer());
        connectionPanel.add(connectButton);
        
        topPanel.add(connectionPanel, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Middle panel - Input and Output
        JPanel middlePanel = new JPanel(new BorderLayout(10, 10));
        
        // Input section
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Submit Complaint"));
        
        JPanel complaintPanel = new JPanel();
        complaintPanel.setLayout(new BorderLayout());
        complaintPanel.add(new JLabel("Complaint: "), BorderLayout.WEST);
        complaintInputField = new JTextField();
        complaintPanel.add(complaintInputField, BorderLayout.CENTER);
        inputPanel.add(complaintPanel);
        
        JPanel buttonPanel1 = new JPanel();
        submitButton = new JButton("Submit Complaint");
        submitButton.setEnabled(false);
        submitButton.addActionListener(e -> submitComplaint());
        buttonPanel1.add(submitButton);
        inputPanel.add(buttonPanel1);
        
        middlePanel.add(inputPanel, BorderLayout.NORTH);
        
        // Output section
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));
        outputArea = new JTextArea(15, 50);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);
        
        middlePanel.add(outputPanel, BorderLayout.CENTER);
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        
        // Bottom panel - Action buttons and status
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        
        JPanel actionPanel = new JPanel();
        viewButton = new JButton("View All Complaints");
        viewButton.setEnabled(false);
        viewButton.addActionListener(e -> viewComplaints());
        actionPanel.add(viewButton);
        
        countButton = new JButton("Check Count");
        countButton.setEnabled(false);
        countButton.addActionListener(e -> checkCount());
        actionPanel.add(countButton);
        
        statusButton = new JButton("Server Status");
        statusButton.setEnabled(false);
        statusButton.addActionListener(e -> getServerStatus());
        actionPanel.add(statusButton);
        
        clearButton = new JButton("Clear Output");
        clearButton.addActionListener(e -> outputArea.setText(""));
        actionPanel.add(clearButton);
        
        bottomPanel.add(actionPanel);
        
        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("Status: Disconnected");
        statusLabel.setForeground(Color.RED);
        statusPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(statusPanel);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
    }
    
    /**
     * Connect to the server
     */
    private void connectToServer() {
        userId = userIdField.getText().trim();
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a User ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            service = (ComplaintService) registry.lookup("ComplaintService");
            
            statusLabel.setText("Status: Connected (User: " + userId + ")");
            statusLabel.setForeground(Color.GREEN);
            
            // Enable all buttons
            submitButton.setEnabled(true);
            viewButton.setEnabled(true);
            countButton.setEnabled(true);
            statusButton.setEnabled(true);
            userIdField.setEnabled(false);
            
            appendOutput("[✓] Connected to Complaint Service\n");
        } catch (Exception e) {
            appendOutput("[✗] Connection Error: " + e.getMessage() + "\n");
            statusLabel.setText("Status: Connection Failed");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, 
                "Failed to connect to server.\nMake sure the server is running on localhost:1099",
                "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Submit a complaint
     */
    private void submitComplaint() {
        try {
            String complaintText = complaintInputField.getText().trim();
            if (complaintText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a complaint", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String result = service.submitComplaint(userId, complaintText);
            appendOutput(">>> SUBMIT COMPLAINT\n");
            appendOutput("User: " + userId + "\n");
            appendOutput("Complaint: " + complaintText + "\n");
            appendOutput("Result: " + result + "\n\n");
            
            complaintInputField.setText("");
        } catch (Exception e) {
            appendOutput("[✗] Error: " + e.getMessage() + "\n\n");
        }
    }
    
    /**
     * View all complaints
     */
    private void viewComplaints() {
        try {
            List<String> complaints = service.viewAllComplaints();
            appendOutput(">>> VIEW ALL COMPLAINTS\n");
            
            if (complaints.isEmpty()) {
                appendOutput("No complaints in the system yet.\n\n");
            } else {
                appendOutput("Total complaints: " + complaints.size() + "\n");
                appendOutput("----------------------------------------\n");
                for (int i = 0; i < complaints.size(); i++) {
                    appendOutput((i + 1) + ". " + complaints.get(i) + "\n");
                }
                appendOutput("----------------------------------------\n\n");
            }
        } catch (Exception e) {
            appendOutput("[✗] Error: " + e.getMessage() + "\n\n");
        }
    }
    
    /**
     * Check complaint count
     */
    private void checkCount() {
        try {
            int count = service.getComplaintCount();
            appendOutput(">>> CHECK COMPLAINT COUNT\n");
            appendOutput("Total complaints in system: " + count + "\n\n");
        } catch (Exception e) {
            appendOutput("[✗] Error: " + e.getMessage() + "\n\n");
        }
    }
    
    /**
     * Get server status
     */
    private void getServerStatus() {
        try {
            String status = service.getServerStatus();
            appendOutput(">>> SERVER STATUS\n");
            appendOutput(status + "\n\n");
        } catch (Exception e) {
            appendOutput("[✗] Error: " + e.getMessage() + "\n\n");
        }
    }
    
    /**
     * Append text to output area
     */
    private void appendOutput(String text) {
        outputArea.append(text);
        // Auto-scroll to bottom
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI());
    }
}
