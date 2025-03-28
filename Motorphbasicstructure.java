package revisedms2;

// Import necessary libraries
import java.io.BufferedReader;       // File reading
import java.io.FileReader;            // File handling
import java.io.IOException;           // Input/output exceptions
import java.text.ParseException;      // Date parsing errors
import java.text.SimpleDateFormat;    // Date formatting
import java.util.*;                   // Collections and utilities

// Base class for all deduction types
abstract class Deduction {
    String name;  // Name of the deduction
    
    // Constructor to initialize deduction name
    public Deduction(String name) {
        this.name = name;  // Set deduction name
    }
    
    // Abstract method to calculate deduction amount
    public abstract double calculate(double amount);
    
    // Getter for deduction name
    public String getName() {
        return name;  // Return name of deduction
    }
}

// SSS deduction implementation
class SSSDeduction extends Deduction {
    // Constructor sets name to "SSS"
    public SSSDeduction() {
        super("SSS");  // Call parent constructor
    }

    // Calculate SSS contribution
    @Override
    public double calculate(double basicSalary) {
        if (basicSalary < 3250) return 135.0;         // Minimum bracket
        if (basicSalary >= 24750) return 1125.0;      // Maximum bracket
        double steps = Math.floor((basicSalary - 3250) / 500);  // Calculate steps
        return 135.0 + (steps + 1) * 22.50;          // Return calculated amount
    }
}

// PhilHealth deduction implementation
class PhilHealthDeduction extends Deduction {
    // Constructor sets name to "PhilHealth"
    public PhilHealthDeduction() {
        super("PhilHealth");  // Call parent constructor
    }

    // Calculate PhilHealth contribution
    @Override
    public double calculate(double basicSalary) {
        if (basicSalary <= 10000) return 150.0;       // Fixed rate
        if (basicSalary < 60000) return basicSalary * 0.015;  // 1.5% calculation
        return 900.0;                                 // Maximum contribution
    }
}

// Pag-IBIG deduction implementation
class PagIBIGDeduction extends Deduction {
    // Constructor sets name to "Pag-IBIG"
    public PagIBIGDeduction() {
        super("Pag-IBIG");  // Call parent constructor
    }

    // Calculate Pag-IBIG contribution
    @Override
    public double calculate(double basicSalary) {
        if (basicSalary >= 1000 && basicSalary <= 1500) 
            return basicSalary * 0.01;  // 1% for lower bracket
        if (basicSalary > 1500) 
            return basicSalary * 0.02;  // 2% for higher bracket
        return 0.0;                    // No contribution
    }
}

// Handles payroll calculations
class PayrollCalculator {
    List<Deduction> deductions;  // List of deductions
    
    // Initialize with standard deductions
    public PayrollCalculator() {
        deductions = new ArrayList<>();  // Create list
        deductions.add(new SSSDeduction());  // Add SSS
        deductions.add(new PhilHealthDeduction());  // Add PhilHealth
        deductions.add(new PagIBIGDeduction());  // Add Pag-IBIG
    }

    // Calculate total deductions
    public double calculateTotalDeductions(double basicSalary) {
        return deductions.stream()  // Create stream
            .mapToDouble(d -> d.calculate(basicSalary))  // Calculate each
            .sum();  // Sum all deductions
    }

    // Calculate withholding tax
    public static double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832) return 0.0;                          // Tax exempt
        if (taxableIncome <= 33333) return (taxableIncome - 20833) * 0.20; // 20% bracket
        if (taxableIncome <= 66667) return 2500 + (taxableIncome - 33333) * 0.25; // 25%
        if (taxableIncome <= 166667) return 10833 + (taxableIncome - 66667) * 0.30; // 30%
        if (taxableIncome <= 666667) return 40833.33 + (taxableIncome - 166667) * 0.32; // 32%
        return 200833.33 + (taxableIncome - 666667) * 0.35;               // 35% bracket
    }
}

// Represents an employee
class Employee {
    String employeeNumber;   // Employee ID
    String fullName;        // Full name
    String birthday;        // Date of birth
    double basicSalary;     // Monthly salary
    double hourlyRate;      // Hourly wage

    // Constructor to initialize employee
    public Employee(String employeeNumber, String fullName, String birthday, 
                   double basicSalary, double hourlyRate) {
        this.employeeNumber = employeeNumber;  // Set ID
        this.fullName = fullName;              // Set name
        this.birthday = birthday;             // Set birthday
        this.basicSalary = basicSalary;       // Set salary
        this.hourlyRate = hourlyRate;         // Set hourly rate
    }

    // Getter methods
    public String getEmployeeNumber() { return employeeNumber; }
    public String getFullName() { return fullName; }
    public String getBirthday() { return birthday; }
    public double getBasicSalary() { return basicSalary; }
    public double getHourlyRate() { return hourlyRate; }
}

// Manages attendance records
class AttendanceRecord {
    Map<String, Map<String, String[]>> attendanceData;  // Employee -> Date -> Times
    
    // Initialize data structure
    public AttendanceRecord() {
        attendanceData = new HashMap<>();  // Create empty map
    }

    // Add attendance record
    public void addAttendance(String empNumber, String date, String logIn, String logOut) {
        attendanceData.putIfAbsent(empNumber, new HashMap<>());  // Add employee if new
        attendanceData.get(empNumber).put(date, new String[]{logIn, logOut});  // Add times
    }

    // Get records within date range
    public Map<String, String[]> getAttendanceInRange(String empNumber, Date startDate, Date endDate) {
        Map<String, String[]> filteredRecords = new HashMap<>();  // Result storage
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");  // Date parser
        
        // Process each record
        attendanceData.getOrDefault(empNumber, new HashMap<>()).forEach((date, times) -> {
            try {
                Date currentDate = dateFormat.parse(date);  // Parse date
                // Check if within range
                if (!currentDate.before(startDate) && !currentDate.after(endDate)) {
                    filteredRecords.put(date, times);  // Add to results
                }
            } catch (ParseException e) {
                System.err.println("Error parsing date: " + date);  // Handle error
            }
        });
        return filteredRecords;  // Return filtered records
    }
}

// Handles data loading from files
class DataLoader {
    // Load employee data from CSV
    public static Map<String, Employee> loadEmployees(String filePath) throws IOException {
        Map<String, Employee> employees = new HashMap<>();  // Create storage
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();  // Skip header
            String line;
            while ((line = br.readLine()) != null) {  // Read each line
                String[] row = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);  // Split CSV
                if (row.length >= 19) {  // Validate columns
                    String empNumber = row[0].trim();  // Get ID
                    String fullName = row[2].trim() + " " + row[1].trim();  // Build name
                    String basicSalaryStr = row[13].trim().replaceAll("[,\"]", "");  // Clean salary
                    String hourlyRateStr = row[18].trim().replaceAll("[,\"]", "");  // Clean rate
                    
                    // Create and store employee
                    employees.put(empNumber, new Employee(
                        empNumber,
                        fullName,
                        row[3].trim(),
                        Double.parseDouble(basicSalaryStr),
                        Double.parseDouble(hourlyRateStr)
                    ));
                }
            }
        }
        return employees;  // Return populated map
    }

    // Load attendance data from CSV
    public static void loadAttendance(AttendanceRecord attendance, String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();  // Skip header
            String line;
            while ((line = br.readLine()) != null) {  // Read each line
                String[] data = line.split(",");  // Split CSV
                if (data.length == 6) {  // Validate columns
                    String empNumber = data[0].trim();  // Get ID
                    String date = data[3].trim();      // Get date
                    String logIn = data[4].trim();     // Get login time
                    String logOut = data[5].trim();    // Get logout time
                    attendance.addAttendance(empNumber, date, logIn, logOut);  // Add record
                }
            }
        }
    }
}

// Manages user interface
class MenuManager {
    Scanner scanner;                      // Input handler
    Map<String, Employee> employees;      // Employee data
    AttendanceRecord attendance;          // Attendance data
    PayrollCalculator payrollCalculator;  // Calculator

    // Initialize with data
    public MenuManager(Map<String, Employee> employees, AttendanceRecord attendance) {
        scanner = new Scanner(System.in);  // Create scanner
        this.employees = employees;        // Store employees
        this.attendance = attendance;      // Store attendance
        payrollCalculator = new PayrollCalculator();  // Create calculator
    }

    // Display main menu
    public void showMenu() {
        int choice;
        do {
            // Print menu options
            System.out.println("\nWelcome to MotorPH Menu:");
            System.out.print("\n");
            System.out.println("1. Display Employee Information");
            System.out.println("2. Compute Hours Worked");
            System.out.println("3. Compute Gross Salary");
            System.out.println("4. Compute Net Salary");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            choice = scanner.nextInt();  // Get user choice
            scanner.nextLine();          // Clear input buffer
            processChoice(choice);       // Handle selection
        } while (choice != 5);           // Loop until exit
    }

    // Route menu selection
    private void processChoice(int choice) {
        switch (choice) {
            case 1: displayEmployeeInfo();  // Show employee details
            case 2: computeHoursWorked();   // Calculate hours
            case 3: computeGrossSalary();   // Calculate gross pay
            case 4: computeNetSalary();     // Calculate net pay
            case 5: System.out.println("Exiting...");  // Exit message
            default: System.out.println("Thank you for using MotorPH Employee Hub!");
        }
    }

    // Option 1: Display employee information
    private void displayEmployeeInfo() {
        System.out.print("\n");
        System.out.print("Enter Employee Number: ");
        String empNumber = scanner.nextLine().trim();  // Get input
        Employee emp = employees.get(empNumber);       // Find employee
        
        if (emp != null) {  // If found
            System.out.println("\nEmployee Details:");
            System.out.println("Employee Number: " + emp.getEmployeeNumber());
            System.out.println("Full Name: " + emp.getFullName());
            System.out.println("Birthday: " + emp.getBirthday());
            System.out.printf("Basic Salary: PHP %.2f%n", emp.getBasicSalary());
            System.out.printf("Hourly Rate: PHP %.2f%n", emp.getHourlyRate());
        } else {
            System.out.println("Employee not found.");  // Not found message
        }
        showMenu();
    }

    // Option 2: Calculate hours worked
    private void computeHoursWorked() {
        System.out.print("\n");
        System.out.print("Enter employee number: ");
        String empNumber = scanner.nextLine();  // Get ID
        
        // Get date range
        System.out.print("Enter start date (MM/dd/yyyy): ");
        String startDateStr = scanner.nextLine();
        System.out.print("Enter end date (MM/dd/yyyy): ");
        String endDateStr = scanner.nextLine();

        try {
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            
            // Get filtered records
            Map<String, String[]> attendanceData = 
                attendance.getAttendanceInRange(empNumber, startDate, endDate);
            
            long totalMinutes = 0;
            // Process each record
            for (Map.Entry<String, String[]> entry : attendanceData.entrySet()) {
                long minutes = calculateTimeDifferenceMinutes(
                    entry.getValue()[0], entry.getValue()[1]);  // Calculate minutes
                totalMinutes += minutes;  // Accumulate total
                // Print daily hours
                System.out.printf("Date: %s, Hours: %s%n", 
                    entry.getKey(), formatTimeDifference(minutes));
            }
            
            // Print total hours
            System.out.printf("Total Hours: %s%n", formatTimeDifference(totalMinutes));
        } catch (ParseException e) {
            System.out.println("Invalid date format.");  // Handle parse error
        }
        showMenu();
    }

    // Option 3: Calculate gross salary
    private void computeGrossSalary() {
        System.out.print("\n");
        System.out.print("Enter employee number: ");
        String empNumber = scanner.nextLine();  // Get ID
        Employee emp = employees.get(empNumber);  // Find employee
        
        if (emp == null) {  // Check existence
            System.out.println("Employee not found.");
            return;
        }

        // Get date range
        System.out.print("Enter start date (MM/dd/yyyy): ");
        String startDateStr = scanner.nextLine();
        System.out.print("Enter end date (MM/dd/yyyy): ");
        String endDateStr = scanner.nextLine();

        try {
            // Parse dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date startDate = dateFormat.parse(startDateStr);
            Date endDate = dateFormat.parse(endDateStr);
            
            // Get attendance records
            Map<String, String[]> attendanceData = 
                attendance.getAttendanceInRange(empNumber, startDate, endDate);
            
            long totalMinutes = 0;
            // Sum all minutes
            for (Map.Entry<String, String[]> entry : attendanceData.entrySet()) {
                totalMinutes += calculateTimeDifferenceMinutes(
                    entry.getValue()[0], entry.getValue()[1]);
            }
            
            // Calculate and display
            double totalHours = totalMinutes / 60.0;
            double grossSalary = totalHours * emp.getHourlyRate();
            System.out.printf("Gross salary for %s: PHP %.2f%n", emp.getFullName(), grossSalary);
        } catch (ParseException e) {
            System.out.println("Invalid date format.");  // Handle error
        }
        showMenu();
    }

    // Option 4: Calculate net salary
    private void computeNetSalary() {
        System.out.print("\n");
        System.out.print("Enter employee number: ");
        String empNumber = scanner.nextLine();  // Get ID
        Employee emp = employees.get(empNumber);  // Find employee
        
        if (emp == null) {  // Check existence
            System.out.println("Employee not found.");
            return;
        }

        // Calculate components
        double basicSalary = emp.getBasicSalary();
        double totalDeductions = payrollCalculator.calculateTotalDeductions(basicSalary);
        double taxableIncome = basicSalary - totalDeductions;
        double withholdingTax = PayrollCalculator.calculateWithholdingTax(taxableIncome);
        double netSalary = taxableIncome - withholdingTax;

        // Display breakdown
        System.out.println("\nNet Salary Calculation:");
        System.out.printf("Basic Salary: PHP %.2f%n", basicSalary);
        System.out.printf("Total Deductions: PHP %.2f%n", totalDeductions);
        System.out.printf("Taxable Income: PHP %.2f%n", taxableIncome);
        System.out.printf("Withholding Tax: PHP %.2f%n", withholdingTax);
        System.out.printf("Net Salary: PHP %.2f%n", netSalary);
        showMenu();
    }

    // Calculate minutes between two times
    private long calculateTimeDifferenceMinutes(String logIn, String logOut) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");  // Time format
            Date timeIn = format.parse(logIn);  // Parse login time
            Date timeOut = format.parse(logOut); // Parse logout time
            return (timeOut.getTime() - timeIn.getTime()) / (60 * 1000);  // Difference in minutes
        } catch (ParseException e) {
            return -1;  // Error indicator
        }
    }

    // Format minutes to HH:mm
    private String formatTimeDifference(long minutes) {
        return (minutes < 0) ? "Invalid" :  // Handle errors
            String.format("%d:%02d", minutes / 60, minutes % 60);  // Format as hours:minutes
    }
}

// Main application class
public class RevisedMS2 {
    public static void main(String[] args) {
        try {
            // Load data
            Map<String, Employee> employees = DataLoader.loadEmployees("C:\\Users\\Sid\\Desktop\\Java\\Group7\\MO-IT101-Group-7\\filestoberead\\motorph_employee_data_complete.csv");
            AttendanceRecord attendance = new AttendanceRecord();
            DataLoader.loadAttendance(attendance, "C:\\Users\\Sid\\Desktop\\Java\\Group7\\MO-IT101-Group-7\\filestoberead\\attendance_record.csv");
            
            // Start application
            new MenuManager(employees, attendance).showMenu();
        } catch (IOException e) {
            System.err.println("Error loading data: " + e.getMessage());  // Handle file errors
        }
    }
}
