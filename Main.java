package StudentManagementSystem;


import java.util.Scanner;

// =============================================================================
//                                 MAIN.JAVA
// =============================================================================


/**
 * Entry point of the application
 * Contains only the menu loop and delegates all work to StudentManager
 * This is called "Separation of Concerns" - best practice!
 */
public class Main {
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        Scanner scanner = manager.getScanner();

        // Load previous data
        manager.loadFromFile();

        // Beautiful welcome message
        System.out.println("\n" + "💔".repeat(80));
        System.out.println("      WELCOME TO STUDENT MANAGEMENT SYSTEM");
        System.out.println("           Created with passion and patience");
        System.out.println("                 Now 100% Professional Grade");
        System.out.println("♥".repeat(80));

        // Main program loop
        while (true) {
            displayMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> { manager.addStudent();         manager.saveToFile(); }
                case "2" -> manager.viewAllStudents();
                case "3" -> manager.searchStudent();
                case "4" -> { manager.updateStudent();      manager.saveToFile(); }
                case "5" -> { manager.deleteStudent();      manager.saveToFile(); }
                case "6" -> {
                    manager.saveToFile();
                    System.out.println("\nThank you for using our system!");
                    System.out.println("All data saved safely. Goodbye! See you soon ♥");
                    return;
                }
                default -> System.out.println("Invalid option! Please enter 1-6");
            }
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("               MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. Add New Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Exit");
        System.out.print("Choose option (1-6): ");
    }


    
    
}
