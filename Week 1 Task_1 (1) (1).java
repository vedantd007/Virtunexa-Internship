/*
 * IntegratedApp.java
 *
 * This single-file Java program demonstrates an integrated application that:
 *   - Offers a Math Quiz (both CLI and GUI versions)
 *   - Manages expenses with SQLite persistence (GUI version)
 *   - Generates Excel reports using Apache POI
 *   - Exports/Imports expense data in JSON format using Jackson
 *
 * Required Libraries:
 *   - SQLite JDBC (e.g., sqlite-jdbc-3.36.0.3.jar)
 *   - Apache POI (poi-5.2.3.jar and poi-ooxml-5.2.3.jar)
 *   - Jackson Databind (jackson-databind-2.15.0.jar, jackson-core-2.15.0.jar, jackson-annotations-2.15.0.jar)
 *
 * Download the following libraries and add them to the classpath when compiling/running:
 * SQLite JDBC (e.g., sqlite-jdbc-3.36.0.3.jar)
 * Apache POI (e.g., poi-5.2.3.jar and poi-ooxml-5.2.3.jar plus any required dependencies)
 * Jackson Databind (e.g., jackson-databind-2.15.0.jar, along with jackson-core-2.15.0.jar and jackson-annotations-2.15.0.jar)
 */

 

 import javax.swing.*;
 import javax.swing.table.DefaultTableModel;
 import java.awt.*;
 import java.awt.event.*;
 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.sql.*;
 import java.util.*;
 import org.apache.poi.ss.usermodel.*;
 import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.core.type.TypeReference;
 
 public class IntegratedApp {
 
     /* =================== MAIN METHOD =================== */
     public static void main(String[] args) {
         // Create the SQLite database and necessary table (if not exists)
         ExpenseManagerDB.initializeDatabase();
 
         // Uncomment one of the following to run CLI or GUI mode:
         // runCLI();
         runGUI();
     }
 
     /* =================== CLI MODE =================== */
     public static void runCLI() {
         Scanner scanner = new Scanner(System.in);
         while (true) {
             System.out.println("\n===== Integrated Application (CLI Mode) =====");
             System.out.println("1. Take a Math Quiz");
             System.out.println("2. Manage Expenses");
             System.out.println("3. Generate Expense Excel Report");
             System.out.println("4. Export Expenses to JSON");
             System.out.println("5. Import Expenses from JSON");
             System.out.println("6. Exit");
             System.out.print("Enter your choice: ");
 
             int choice = Integer.parseInt(scanner.nextLine());
             switch (choice) {
                 case 1:
                     MathQuizCLI.startQuiz(scanner);
                     break;
                 case 2:
                     ExpenseManagerCLI.manageExpenses(scanner);
                     break;
                 case 3:
                     System.out.print("Enter Excel file path to generate report: ");
                     String excelPath = scanner.nextLine();
                     ExcelReportGenerator.generateExpenseReport(excelPath);
                     break;
                 case 4:
                     System.out.print("Enter JSON file path to export expenses: ");
                     String jsonExportPath = scanner.nextLine();
                     JSONExportImport.exportExpensesToJSON(jsonExportPath);
                     break;
                 case 5:
                     System.out.print("Enter JSON file path to import expenses: ");
                     String jsonImportPath = scanner.nextLine();
                     List<Expense> imported = JSONExportImport.importExpensesFromJSON(jsonImportPath);
                     System.out.println("Imported " + imported.size() + " expenses:");
                     for (Expense exp : imported) {
                         System.out.println(exp);
                     }
                     break;
                 case 6:
                     System.out.println("Exiting application.");
                     scanner.close();
                     System.exit(0);
                 default:
                     System.out.println("Invalid option. Try again.");
             }
         }
     }
 
     /* =================== GUI MODE =================== */
     public static void runGUI() {
         SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
     }
 
     /* =================== MATH QUIZ (CLI) =================== */
     public static class MathQuizCLI {
         public static void startQuiz(Scanner scanner) {
             Random random = new Random();
             int score = 0;
             final int totalQuestions = 5;
 
             for (int i = 1; i <= totalQuestions; i++) {
                 int num1 = random.nextInt(10) + 1;
                 int num2 = random.nextInt(10) + 1;
                 char[] operations = {'+', '-', '*'};
                 char operation = operations[random.nextInt(operations.length)];
 
                 int correctAnswer = switch (operation) {
                     case '+' -> num1 + num2;
                     case '-' -> num1 - num2;
                     case '*' -> num1 * num2;
                     default -> 0;
                 };
 
                 System.out.printf("Question %d: %d %c %d = ?%n", i, num1, operation, num2);
                 int userAnswer = Integer.parseInt(scanner.nextLine());
                 if (userAnswer == correctAnswer) {
                     System.out.println("Correct!");
                     score++;
                 } else {
                     System.out.println("Wrong! The correct answer is " + correctAnswer);
                 }
             }
             System.out.println("Quiz Over! Your Score: " + score + "/" + totalQuestions);
         }
     }
 
     /* =================== EXPENSE MANAGER (CLI) =================== */
     public static class ExpenseManagerCLI {
         public static void manageExpenses(Scanner scanner) {
             while (true) {
                 System.out.println("\n===== Expense Manager (CLI) =====");
                 System.out.println("1. Add Expense");
                 System.out.println("2. View Expenses");
                 System.out.println("3. Delete Expense");
                 System.out.println("4. Go Back");
                 System.out.print("Enter your choice: ");
 
                 int choice = Integer.parseInt(scanner.nextLine());
                 switch (choice) {
                     case 1:
                         System.out.print("Enter category: ");
                         String category = scanner.nextLine();
                         System.out.print("Enter amount: ");
                         double amount = Double.parseDouble(scanner.nextLine());
                         System.out.print("Enter date (YYYY-MM-DD): ");
                         String date = scanner.nextLine();
                         ExpenseManagerDB.addExpense(category, amount, date);
                         break;
                     case 2:
                         ExpenseManagerDB.viewExpenses();
                         break;
                     case 3:
                         System.out.print("Enter expense ID to delete: ");
                         int id = Integer.parseInt(scanner.nextLine());
                         ExpenseManagerDB.deleteExpense(id);
                         break;
                     case 4:
                         return;
                     default:
                         System.out.println("Invalid choice.");
                 }
             }
         }
     }
 
     /* =================== DATABASE & EXPENSE MANAGER (Shared by CLI & GUI) =================== */
     public static class ExpenseManagerDB {
         private static final String DB_URL = "jdbc:sqlite:database.db";
 
         // Initialize the database and create table if not exists.
         public static void initializeDatabase() {
             try (Connection conn = DriverManager.getConnection(DB_URL);
                  Statement stmt = conn.createStatement()) {
                 String sql = "CREATE TABLE IF NOT EXISTS expenses (" +
                              "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                              "category TEXT, " +
                              "amount REAL, " +
                              "date TEXT)";
                 stmt.execute(sql);
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
 
         public static void addExpense(String category, double amount, String date) {
             String sql = "INSERT INTO expenses (category, amount, date) VALUES (?, ?, ?)";
             try (Connection conn = DriverManager.getConnection(DB_URL);
                  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 pstmt.setString(1, category);
                 pstmt.setDouble(2, amount);
                 pstmt.setString(3, date);
                 pstmt.executeUpdate();
                 System.out.println("Expense added successfully.");
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
 
         public static void viewExpenses() {
             String sql = "SELECT * FROM expenses";
             try (Connection conn = DriverManager.getConnection(DB_URL);
                  Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql)) {
                 System.out.println("\n===== Expense List =====");
                 while (rs.next()) {
                     System.out.printf("%d. %s | ₹%.2f | %s%n",
                                       rs.getInt("id"),
                                       rs.getString("category"),
                                       rs.getDouble("amount"),
                                       rs.getString("date"));
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
 
         public static void deleteExpense(int id) {
             String sql = "DELETE FROM expenses WHERE id = ?";
             try (Connection conn = DriverManager.getConnection(DB_URL);
                  PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 pstmt.setInt(1, id);
                 int rowsAffected = pstmt.executeUpdate();
                 if (rowsAffected > 0) {
                     System.out.println("Expense deleted successfully.");
                 } else {
                     System.out.println("Expense not found.");
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
 
         // Retrieve expenses as a list (used for JSON export/import)
         public static List<Expense> getExpenses() {
             List<Expense> list = new ArrayList<>();
             String sql = "SELECT * FROM expenses";
             try (Connection conn = DriverManager.getConnection(DB_URL);
                  Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql)) {
                 while (rs.next()) {
                     list.add(new Expense(
                         rs.getInt("id"),
                         rs.getString("category"),
                         rs.getDouble("amount"),
                         rs.getString("date")
                     ));
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             return list;
         }
     }
 
     /* =================== EXPENSE MODEL =================== */
     public static class Expense {
         private int id;
         private String category;
         private double amount;
         private String date;
 
         // Default constructor (needed for Jackson)
         public Expense() {}
 
         public Expense(int id, String category, double amount, String date) {
             this.id = id;
             this.category = category;
             this.amount = amount;
             this.date = date;
         }
 
         // Getters and setters
         public int getId() { return id; }
         public void setId(int id) { this.id = id; }
 
         public String getCategory() { return category; }
         public void setCategory(String category) { this.category = category; }
 
         public double getAmount() { return amount; }
         public void setAmount(double amount) { this.amount = amount; }
 
         public String getDate() { return date; }
         public void setDate(String date) { this.date = date; }
 
         @Override
         public String toString() {
             return id + ": " + category + " | ₹" + amount + " | " + date;
         }
     }
 
     /* =================== EXCEL REPORT GENERATOR (Apache POI) =================== */
     public static class ExcelReportGenerator {
         private static final String DB_URL = "jdbc:sqlite:database.db";
 
         public static void generateExpenseReport(String filePath) {
             String sql = "SELECT * FROM expenses";
 
             try (Connection conn = DriverManager.getConnection(DB_URL);
                  Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql);
                  Workbook workbook = new XSSFWorkbook()) {
 
                 Sheet sheet = workbook.createSheet("Expenses Report");
 
                 // Create header row
                 Row headerRow = sheet.createRow(0);
                 String[] headers = {"ID", "Category", "Amount", "Date"};
                 for (int i = 0; i < headers.length; i++) {
                     Cell cell = headerRow.createCell(i);
                     cell.setCellValue(headers[i]);
                 }
 
                 // Write data rows
                 int rowNum = 1;
                 while (rs.next()) {
                     Row row = sheet.createRow(rowNum++);
                     row.createCell(0).setCellValue(rs.getInt("id"));
                     row.createCell(1).setCellValue(rs.getString("category"));
                     row.createCell(2).setCellValue(rs.getDouble("amount"));
                     row.createCell(3).setCellValue(rs.getString("date"));
                 }
 
                 // Autosize columns for readability
                 for (int i = 0; i < headers.length; i++) {
                     sheet.autoSizeColumn(i);
                 }
 
                 try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                     workbook.write(fileOut);
                 }
 
                 System.out.println("Excel report generated successfully at: " + filePath);
 
             } catch (SQLException | IOException e) {
                 e.printStackTrace();
             }
         }
     }
 
     /* =================== JSON EXPORT/IMPORT (Jackson) =================== */
     public static class JSONExportImport {
         private static final ObjectMapper mapper = new ObjectMapper();
 
         public static void exportExpensesToJSON(String filePath) {
             List<Expense> expenses = ExpenseManagerDB.getExpenses();
             try {
                 mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), expenses);
                 System.out.println("Expenses exported to JSON file: " + filePath);
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
 
         public static List<Expense> importExpensesFromJSON(String filePath) {
             List<Expense> expenses = new ArrayList<>();
             try {
                 expenses = mapper.readValue(new File(filePath), new TypeReference<List<Expense>>() {});
                 System.out.println("Expenses imported from JSON file: " + filePath);
             } catch (IOException e) {
                 e.printStackTrace();
             }
             return expenses;
         }
     }
 
     /* =================== MATH QUIZ GUI (Swing) =================== */
     public static class MathQuizGUI extends JFrame {
         private JLabel questionLabel, scoreLabel;
         private JTextField answerField;
         private JButton submitButton;
         private int num1, num2, correctAnswer, score = 0, questionCount = 0;
         private char operation;
         private final int totalQuestions = 5;
         private Random random = new Random();
 
         public MathQuizGUI() {
             setTitle("Math Quiz");
             setSize(300, 200);
             setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
             setLayout(new GridLayout(4, 1));
 
             questionLabel = new JLabel("Question will appear here");
             scoreLabel = new JLabel("Score: 0");
             answerField = new JTextField();
             submitButton = new JButton("Submit");
 
             add(questionLabel);
             add(answerField);
             add(submitButton);
             add(scoreLabel);
 
             submitButton.addActionListener(e -> submitAnswer());
             generateQuestion();
         }
 
         private void generateQuestion() {
             num1 = random.nextInt(10) + 1;
             num2 = random.nextInt(10) + 1;
             char[] operations = {'+', '-', '*'};
             operation = operations[random.nextInt(operations.length)];
 
             correctAnswer = switch (operation) {
                 case '+' -> num1 + num2;
                 case '-' -> num1 - num2;
                 case '*' -> num1 * num2;
                 default -> 0;
             };
 
             questionLabel.setText("Solve: " + num1 + " " + operation + " " + num2);
             answerField.setText("");
         }
 
         private void submitAnswer() {
             try {
                 int userAnswer = Integer.parseInt(answerField.getText());
                 if (userAnswer == correctAnswer) {
                     score++;
                 }
                 questionCount++;
                 if (questionCount < totalQuestions) {
                     generateQuestion();
                 } else {
                     JOptionPane.showMessageDialog(this, "Quiz Over! Your Score: " + score);
                     // Reset quiz for next time:
                     score = 0;
                     questionCount = 0;
                     generateQuestion();
                 }
                 scoreLabel.setText("Score: " + score);
             } catch (NumberFormatException ex) {
                 JOptionPane.showMessageDialog(this, "Please enter a valid number!");
             }
         }
     }
 
     /* =================== EXPENSE MANAGER GUI (Swing) =================== */
     public static class ExpenseManagerGUI extends JFrame {
         private JTextField categoryField, amountField, dateField;
         private JButton addButton, deleteButton;
         private DefaultTableModel tableModel;
         private JTable expenseTable;
         private Connection conn;
         private final String DB_URL = "jdbc:sqlite:database.db";
 
         public ExpenseManagerGUI() {
             setTitle("Expense Manager");
             setSize(600, 400);
             setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
             setLayout(new BorderLayout());
 
             // Panel for input fields
             JPanel inputPanel = new JPanel(new GridLayout(3, 2));
             inputPanel.add(new JLabel("Category:"));
             categoryField = new JTextField();
             inputPanel.add(categoryField);
 
             inputPanel.add(new JLabel("Amount:"));
             amountField = new JTextField();
             inputPanel.add(amountField);
 
             inputPanel.add(new JLabel("Date (YYYY-MM-DD):"));
             dateField = new JTextField();
             inputPanel.add(dateField);
 
             // Panel for buttons
             JPanel buttonPanel = new JPanel();
             addButton = new JButton("Add Expense");
             deleteButton = new JButton("Delete Expense");
             // Additional buttons for JSON export/import and Excel report
             JButton exportJsonButton = new JButton("Export JSON");
             JButton importJsonButton = new JButton("Import JSON");
             JButton exportExcelButton = new JButton("Export Excel");
 
             buttonPanel.add(addButton);
             buttonPanel.add(deleteButton);
             buttonPanel.add(exportJsonButton);
             buttonPanel.add(importJsonButton);
             buttonPanel.add(exportExcelButton);
 
             // Table for displaying expenses
             String[] columns = {"ID", "Category", "Amount", "Date"};
             tableModel = new DefaultTableModel(columns, 0);
             expenseTable = new JTable(tableModel);
             JScrollPane scrollPane = new JScrollPane(expenseTable);
 
             add(inputPanel, BorderLayout.NORTH);
             add(buttonPanel, BorderLayout.CENTER);
             add(scrollPane, BorderLayout.SOUTH);
 
             // Database connection
             try {
                 conn = DriverManager.getConnection(DB_URL);
             } catch (SQLException e) {
                 e.printStackTrace();
             }
 
             // Button actions
             addButton.addActionListener(e -> addExpense());
             deleteButton.addActionListener(e -> deleteExpense());
             exportJsonButton.addActionListener(e -> {
                 JSONExportImport.exportExpensesToJSON("expenses_export.json");
                 JOptionPane.showMessageDialog(this, "Exported to expenses_export.json");
             });
             importJsonButton.addActionListener(e -> {
                 List<Expense> importedExpenses = JSONExportImport.importExpensesFromJSON("expenses_export.json");
                 JOptionPane.showMessageDialog(this, "Imported " + importedExpenses.size() + " expenses.");
                 loadExpenses();
             });
             exportExcelButton.addActionListener(e -> {
                 ExcelReportGenerator.generateExpenseReport("Expense_Report.xlsx");
                 JOptionPane.showMessageDialog(this, "Excel report generated: Expense_Report.xlsx");
             });
 
             loadExpenses();
         }
 
         private void addExpense() {
             String category = categoryField.getText();
             double amount = Double.parseDouble(amountField.getText());
             String date = dateField.getText();
 
             String sql = "INSERT INTO expenses (category, amount, date) VALUES (?, ?, ?)";
             try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 pstmt.setString(1, category);
                 pstmt.setDouble(2, amount);
                 pstmt.setString(3, date);
                 pstmt.executeUpdate();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             loadExpenses();
         }
 
         private void loadExpenses() {
             tableModel.setRowCount(0);
             String sql = "SELECT * FROM expenses";
             try (Statement stmt = conn.createStatement();
                  ResultSet rs = stmt.executeQuery(sql)) {
                 while (rs.next()) {
                     tableModel.addRow(new Object[]{
                         rs.getInt("id"),
                         rs.getString("category"),
                         rs.getDouble("amount"),
                         rs.getString("date")
                     });
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
 
         private void deleteExpense() {
             int selectedRow = expenseTable.getSelectedRow();
             if (selectedRow == -1) {
                 JOptionPane.showMessageDialog(this, "Select an expense to delete.");
                 return;
             }
             int id = (int) tableModel.getValueAt(selectedRow, 0);
             String sql = "DELETE FROM expenses WHERE id = ?";
             try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                 pstmt.setInt(1, id);
                 pstmt.executeUpdate();
             } catch (SQLException e) {
                 e.printStackTrace();
             }
             loadExpenses();
         }
     }
 
     /* =================== MAIN GUI MENU (Swing) =================== */
     public static class MainGUI extends JFrame {
         public MainGUI() {
             setTitle("Integrated Application - Main Menu");
             setSize(350, 250);
             setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
             setLayout(new GridLayout(4, 1, 10, 10));
 
             JButton quizButton = new JButton("Start Math Quiz (GUI)");
             JButton expenseButton = new JButton("Open Expense Manager (GUI)");
             JButton cliButton = new JButton("Run CLI Mode (Console)");
             JButton exitButton = new JButton("Exit");
 
             quizButton.addActionListener(e -> new MathQuizGUI().setVisible(true));
             expenseButton.addActionListener(e -> new ExpenseManagerGUI().setVisible(true));
             cliButton.addActionListener(e -> {
                 // Launch CLI mode in a separate thread; note that console output
                 // will appear in the terminal from which you launched the application.
                 new Thread(IntegratedApp::runCLI).start();
             });
             exitButton.addActionListener(e -> System.exit(0));
 
             add(quizButton);
             add(expenseButton);
             add(cliButton);
             add(exitButton);
         }
     }
 }
 