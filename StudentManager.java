package StudentManagementSystem;


// =============================================================================
//                           STUDENTMANAGER.JAVA
// =============================================================================
//package studentmanager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main business logic class - handles all operations on students
 * Follows Single Responsibility Principle:
 *   - Manages in-memory list
 *   - Loads from / saves to file
 *   - Provides CRUD operations (Create, Read, Update, Delete)
 */
public class StudentManager {
    private final List<Student> students = new ArrayList<>();  // In-memory storage
    private static final String FILE_PATH = "students.txt";     // Persistent storage file
    private int nextId = 1000;                                  // Auto-increment ID generator
    private final Scanner scanner = new Scanner(System.in);     // Single scanner instance (best practice)

    // =========================================================================
    //                          FILE OPERATIONS
    // =========================================================================

    /**
     * Loads all students from file when program starts
     * Called once in main() - ensures data persistence across runs
     */
    public void loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("No previous data found. Starting with empty database.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int loadedCount = 0;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;  // Skip blank lines

                String[] parts = line.split("\\|", -1);  // -1 to include empty fields
                if (parts.length != 6) {
                    System.out.println("Skipping corrupted line: " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(parts[0].trim());
                    Student student = new Student(
                            id,
                            parts[1], parts[2], parts[3], parts[4],
                            Integer.parseInt(parts[5].trim())
                    );
                    students.add(student);
                    if (id >= nextId) nextId = id + 1;  // Ensure IDs continue from highest
                    loadedCount++;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format in line: " + line);
                }
            }
            System.out.println("Successfully loaded " + loadedCount + " student(s) from file.\n");
        } catch (IOException e) {
            System.err.println("Critical error reading file: " + e.getMessage());
        }
    }

    /**
     * Saves ALL current students to file
     * Called after every add/update/delete to prevent data loss
     */
    public void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Student student : students) {
                writer.println(student.toFileFormat());
            }
            // No need to print message every time - only on important events
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }

    // =========================================================================
    //                          CORE OPERATIONS
    // =========================================================================

    /**
     * Adds a new student with full input validation
     */
    public void addStudent() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           ADD NEW STUDENT");
        System.out.println("=".repeat(50));

        String name = getNonEmptyInput("Enter Name");
        String regNo = getNonEmptyInput("Enter Registration Number");

        // Prevent duplicate registration numbers
        if (findStudentByRegNo(regNo) != null) {
            System.out.println("Error: Student with Registration Number '" + regNo + "' already exists!");
            return;
        }

        String grade = getNonEmptyInput("Enter Grade");
        String gender = getValidGender();
        int age = getValidAge();

        Student newStudent = new Student(nextId++, name, regNo, grade, gender, age);
        students.add(newStudent);

        System.out.println("SUCCESS! Student added with ID: " + newStudent.getStudentId());
        System.out.println("Student: " + newStudent.getName() + " | Age: " + age + " | Grade: " + grade);
    }

    /**
     * Displays all students in a beautiful table format
     */
    public void viewAllStudents() {
        System.out.println("\n" + "=".repeat(100));
        System.out.println("                           ALL STUDENTS (" + students.size() + " total)");
        System.out.println("=".repeat(100));

        if (students.isEmpty()) {
            System.out.println("           No students found. Add your first student!");
            System.out.println("=".repeat(100));
            return;
        }

        // Header
        System.out.printf("%-8s %-25s %-18s %-12s %-8s %s%n",
                "ID", "Name", "Registration No.", "Grade", "Gender", "Age");
        System.out.println("-".repeat(100));

        // Data rows
        for (Student s : students) {
            System.out.println(s);
        }
        System.out.println("-".repeat(100));
    }

    /**
     * Searches student by Registration Number (most reliable unique key)
     */
    public void searchStudent() {
        System.out.print("Enter Registration Number to search: ");
        String regNo = scanner.nextLine().trim();

        Student found = findStudentByRegNo(regNo);
        if (found != null) {
            System.out.println("STUDENT FOUND!");
            System.out.println("-".repeat(80));
            System.out.printf("%-8s %-25s %-18s %-12s %-8s %s%n",
                    "ID", "Name", "Reg No.", "Grade", "Gender", "Age");
            System.out.println(found);
            System.out.println("-".repeat(80));
        } else {
            System.out.println("No student found with Registration Number: " + regNo);
        }
    }

    /**
     * Updates existing student - safe and user-friendly
     */
    public void updateStudent() {
        System.out.print("Enter Registration Number to update: ");
        String regNo = scanner.nextLine().trim();
        Student student = findStudentByRegNo(regNo);

        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.println("Current student: " + student);
        System.out.println("Leave field empty and press Enter to keep current value.\n");

        String input = getInputWithDefault("New Name", student.getName());
        if (!input.isEmpty()) student.setName(input);

        input = getInputWithDefault("New Grade", student.getGrade());
        if (!input.isEmpty()) student.setGrade(input);

        input = getInputWithDefault("New Gender (MALE/FEMALE/OTHERS)", student.getGender());
        if (!input.isEmpty()) {
            String upper = input.toUpperCase();
            if (upper.equals("MALE") || upper.equals("FEMALE") || upper.equals("OTHERS")) {
                student.setGender(upper);
            } else {
                System.out.println("Invalid gender! Keeping old value.");
            }
        }

        input = getInputWithDefault("New Age", String.valueOf(student.getAge()));
        if (!input.isEmpty()) {
            try {
                int newAge = Integer.parseInt(input);
                if (newAge > 0) student.setAge(newAge);
                else System.out.println("Age must be positive! Keeping old value.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid age format! Keeping old value.");
            }
        }

        System.out.println("Student updated successfully!");
    }

    /**
     * Deletes a student permanently
     */
    public void deleteStudent() {
        System.out.print("Enter Registration Number to DELETE: ");
        String regNo = scanner.nextLine().trim();
        Student student = findStudentByRegNo(regNo);

        if (student == null) {
            System.out.println("Student not found!");
            return;
        }

        System.out.println("Found: " + student.getName() + " (ID: " + student.getStudentId() + ")");
        System.out.print("Type 'DELETE' to confirm permanent deletion: ");
        if (scanner.nextLine().trim().equalsIgnoreCase("DELETE")) {
            students.remove(student);
            System.out.println("Student permanently deleted.");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    // =========================================================================
    //                          HELPER METHODS
    // =========================================================================

    private Student findStudentByRegNo(String regNo) {
        for (Student s : students) {
            if (s.getRegistrationNumber().equalsIgnoreCase(regNo.trim())) {
                return s;
            }
        }
        return null;
    }

    private String getValidGender() {
        while (true) {
            System.out.print("Enter Gender (MALE / FEMALE / OTHERS): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("MALE") || input.equals("FEMALE") || input.equals("OTHERS")) {
                return input;
            }
            System.out.println("Invalid! Please choose MALE, FEMALE, or OTHERS");
        }
    }

    private int getValidAge() {
        while (true) {
            System.out.print("Enter Age (years): ");
            try {
                int age = Integer.parseInt(scanner.nextLine().trim());
                if (age >= 5 && age <= 100) return age;  // Reasonable age range
                System.out.println("Please enter realistic age (5-100)");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    private String getNonEmptyInput(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("This field cannot be empty!");
        }
    }

    private String getInputWithDefault(String prompt, String current) {
        System.out.print(prompt + " [" + current + "]: ");
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? current : input;
    }

    // Used by Main class to access scanner (avoid creating multiple scanners)
    public Scanner getScanner() {
        return scanner;
    }
}
