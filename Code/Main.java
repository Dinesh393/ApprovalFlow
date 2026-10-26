

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import model.ApprovalHistory;
import model.ApprovalStep;
import model.Request;
import model.User;
import service.ApprovalService;
import service.RequestService;
import service.UserService;

public class Main {

    private final Scanner scanner = new Scanner(System.in);

    private final UserService userService = new UserService();
    private final RequestService requestService = new RequestService();
    private final ApprovalService approvalService = new ApprovalService();

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    // Application entry loop
    public void start() {

        boolean running = true;

        while (running) {
            System.out.println("\n==================================");
            System.out.println("       APPROVAFLOW SYSTEM");
            System.out.println("==================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");

            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    User user = login();

                    if (user != null) {
                        showRoleMenu(user);
                    }
                    break;

                case 2:
                    registerUser();
                    break;

                case 0:
                    running = false;
                    System.out.println("Thank you for using ApprovaFlow!");
                    break;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        scanner.close();
    }

    // Register a new employee
    private void registerUser() {

        System.out.println("\n========== EMPLOYEE REGISTRATION ==========");

        String fullName = readLine("Full name: ");
        String username = readLine("Username: ");
        String email = readLine("Email: ");
        String password = readLine("Password: ");

        User user = new User();
        user.setFullName(fullName);
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setRole("EMPLOYEE");

        try {
            boolean success = userService.registerUser(user);

            if (success) {
                System.out.println("Registration successful. You can log in now.");
            } else {
                System.out.println("Registration failed. Check your details.");
            }

        } catch (SQLException e) {
            System.out.println(
                "Registration failed. Username or email may already exist."
            );
        }
    }

    // Login
    private User login() {

        String username = readLine("Username: ");
        String password = readLine("Password: ");

        try {
            User user = userService.loginUser(username, password);

            if (user == null) {
                System.out.println("Invalid username or password.");
                return null;
            }

            System.out.println("\nWelcome, " + user.getFullName() + "!");
            System.out.println("Role: " + user.getRole());

            return user;

        } catch (SQLException e) {
            System.out.println("Unable to log in due to a database error.");
            return null;
        }
    }

    // Route the user according to their role
    private void showRoleMenu(User user) {

        switch (user.getRole()) {
            case "EMPLOYEE":
                employeeMenu(user);
                break;

            case "MANAGER":
            case "FINANCE_MANAGER":
                approvalMenu(user);
                break;

            case "ADMIN":
                adminMenu(user);
                break;

            default:
                System.out.println("Unknown role. Access denied.");
        }
    }

    // Employee menu
    private void employeeMenu(User user) {

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========== EMPLOYEE MENU ==========");
            System.out.println("1. Submit request");
            System.out.println("2. View my requests");
            System.out.println("3. View my request by ID");
            System.out.println("0. Logout");

            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        submitRequest(user);
                        break;

                    case 2:
                        displayRequests(
                            requestService.getRequestsByUserId(user.getUserId())
                        );
                        break;

                    case 3:
                        viewMyRequestById(user);
                        break;

                    case 0:
                        loggedIn = false;
                        System.out.println("Logged out.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Database operation failed.");
            }
        }
    }

    // Submit request
    private void submitRequest(User user) throws SQLException {

        String title = readLine("Request title: ");
        String description = readLine("Description: ");

        BigDecimal amount;

        try {
            amount = new BigDecimal(readLine("Amount: "));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return;
        }

        Request request = new Request();
        request.setUserId(user.getUserId());
        request.setTitle(title);
        request.setDescription(description);
        request.setAmount(amount);

        if (requestService.submitRequest(request)) {
            System.out.println("Request submitted successfully.");
            System.out.println("Request ID: " + request.getRequestId());
            System.out.println("Status: " + request.getStatus());
        } else {
            System.out.println("Request submission failed.");
        }
    }

    // Employees may view only their own requests
    private void viewMyRequestById(User user) throws SQLException {

        int requestId = readInt("Enter request ID: ");
        Request request = requestService.getRequestById(requestId);

        if (request == null || request.getUserId() != user.getUserId()) {
            System.out.println("Request not found or access denied.");
            return;
        }

        displayRequest(request);
    }

    // Manager and finance manager menu
    private void approvalMenu(User user) {

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========== APPROVAL MENU ==========");
            System.out.println("1. View my pending tasks");
            System.out.println("2. Approve a step");
            System.out.println("3. Reject a step");
            System.out.println("4. View request approval history");
            System.out.println("0. Logout");

            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        displayPendingSteps(user.getUserId());
                        break;

                    case 2:
                        processApproval(user, "APPROVED");
                        break;

                    case 3:
                        processApproval(user, "REJECTED");
                        break;

                    case 4:
                        viewHistoryByRequestId();
                        break;

                    case 0:
                        loggedIn = false;
                        System.out.println("Logged out.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Approval operation failed.");
            }
        }
    }

    // Display pending tasks assigned to the current approver
    private void displayPendingSteps(int approverId) throws SQLException {

        List<ApprovalStep> steps =
            approvalService.getPendingStepsByApproverId(approverId);

        if (steps.isEmpty()) {
            System.out.println("No pending approval tasks.");
            return;
        }

        for (ApprovalStep step : steps) {
            System.out.println("----------------------------------");
            System.out.println("Step ID: " + step.getStepId());
            System.out.println("Request ID: " + step.getRequestId());
            System.out.println("Status: " + step.getStatus());

            Request request =
                requestService.getRequestById(step.getRequestId());

            if (request != null) {
                System.out.println("Title: " + request.getTitle());
                System.out.println("Amount: " + request.getAmount());
            }
        }
    }

    // Approve or reject a step
    private void processApproval(User user, String action)
            throws SQLException {

        int stepId = readInt("Enter approval step ID: ");
        String comments = readLine("Comments (optional): ");

        boolean success = approvalService.processApproval(
            stepId,
            user.getUserId(),
            action,
            comments
        );

        if (success) {
            System.out.println("Approval action completed: " + action);
        } else {
            System.out.println("Action failed. Check the step and assignment.");
        }
    }

    // Admin menu
    private void adminMenu(User user) {

        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. View all users");
            System.out.println("2. View all requests");
            System.out.println("3. Assign approver");
            System.out.println("4. View approval history");
            System.out.println("0. Logout");

            int choice = readInt("Enter choice: ");

            try {
                switch (choice) {
                    case 1:
                        displayUsers(userService.getAllUsers());
                        break;

                    case 2:
                        displayRequests(requestService.getAllRequests());
                        break;

                    case 3:
                        assignApprover();
                        break;

                    case 4:
                        viewHistoryByRequestId();
                        break;

                    case 0:
                        loggedIn = false;
                        System.out.println("Logged out.");
                        break;

                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (SQLException e) {
                System.out.println("Admin operation failed.");
            }
        }
    }

    // Assign an approver
    private void assignApprover() throws SQLException {

        int requestId = readInt("Request ID: ");
        int approverId = readInt("Approver user ID: ");

        ApprovalStep step = new ApprovalStep();
        step.setRequestId(requestId);
        step.setApproverId(approverId);

        if (approvalService.assignApprover(step)) {
            System.out.println("Approver assigned successfully.");
            System.out.println("Step ID: " + step.getStepId());
        } else {
            System.out.println("Approver assignment failed.");
        }
    }

    // Display approval history
    private void viewHistoryByRequestId() throws SQLException {

        int requestId = readInt("Request ID: ");

        List<ApprovalHistory> history =
            approvalService.getHistoryByRequestId(requestId);

        if (history.isEmpty()) {
            System.out.println("No approval history found.");
            return;
        }

        for (ApprovalHistory item : history) {
            System.out.println("----------------------------------");
            System.out.println("History ID: " + item.getHistoryId());
            System.out.println("Step ID: " + item.getStepId());
            System.out.println("Action: " + item.getAction());
            System.out.println("Comments: " + item.getComments());
        }
    }

    // Display users
    private void displayUsers(List<User> users) {

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return;
        }

        for (User user : users) {
            System.out.println("----------------------------------");
            System.out.println("User ID: " + user.getUserId());
            System.out.println("Name: " + user.getFullName());
            System.out.println("Username: " + user.getUsername());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getRole());
        }
    }

    // Display request list
    private void displayRequests(List<Request> requests) {

        if (requests.isEmpty()) {
            System.out.println("No requests found.");
            return;
        }

        for (Request request : requests) {
            displayRequest(request);
        }
    }

    // Display a single request
    private void displayRequest(Request request) {

        System.out.println("----------------------------------");
        System.out.println("Request ID: " + request.getRequestId());
        System.out.println("Employee ID: " + request.getUserId());
        System.out.println("Title: " + request.getTitle());
        System.out.println("Description: " + request.getDescription());
        System.out.println("Amount: " + request.getAmount());
        System.out.println("Status: " + request.getStatus());
    }

    // Safely read an integer
    private int readInt(String prompt) {

        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    // Read a line of console input
    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
