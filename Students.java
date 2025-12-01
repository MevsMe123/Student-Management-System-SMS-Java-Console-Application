package StudentManagementSystem;


// =============================================================================
//                              STUDENT.JAVA
// =============================================================================


/**
 * Represents a single student with all required information.
 * This class is immutable for ID and mutable for updatable fields (name, grade, etc.)
 * Follows proper JavaBean conventions with private fields and public getters/setters.
 */
public class Student {
    private final int studentId;                    // Unique ID - never changes after creation
    private String name;                            // Student's full name
    private String registrationNumber;              // Unique registration number (like roll number)
    private String grade;                           // Current academic grade (e.g., A+, B, 10th)
    private String gender;                          // MALE / FEMALE / OTHERS
    private int age;                                // Student's age in years

    /**
     * Constructor to create a new Student object
     * @param studentId Unique system-generated ID
     * @param name Student's name
     * @param registrationNumber Unique registration number
     * @param grade Academic grade
     * @param gender Gender as string
     * @param age Age in years
     */
    public Student(int studentId, String name, String registrationNumber,
                   String grade, String gender, int age) {
        this.studentId = studentId;
        this.name = name.trim();
        this.registrationNumber = registrationNumber.trim();
        this.grade = grade.trim();
        this.gender = gender.toUpperCase().trim();
        this.age = age;
    }

    // ========================== GETTERS ==========================
    public int getStudentId()               { return studentId; }
    public String getName()                 { return name; }
    public String getRegistrationNumber()   { return registrationNumber; }
    public String getGrade()                { return grade; }
    public String getGender()               { return gender; }
    public int getAge()                     { return age; }

    // ========================== SETTERS (for updating) ==========================
    public void setName(String name)                    { this.name = name.trim(); }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber.trim(); }
    public void setGrade(String grade)                  { this.grade = grade.trim(); }
    public void setGender(String gender)                { this.gender = gender.toUpperCase().trim(); }
    public void setAge(int age)                         { this.age = age; }

    /**
     * Formats student data for beautiful console display
     */
    @Override
    public String toString() {
        return String.format("%-8d %-25s %-18s %-12s %-8s %3d",
                studentId, name, registrationNumber, grade, gender, age);
    }

    /**
     * Formats student data for saving to file (pipe-separated for easy parsing)
     * Using '|' delimiter because it's safe (rarely used in names)
     */
    public String toFileFormat() {
        return String.format("%d|%s|%s|%s|%s|%d",
                studentId, name, registrationNumber, grade, gender, age);
    }
}
