import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class BinaryToDecimalConverter {
    private JFrame frame;
    private JTextField binaryInput;
    private JLabel resultLabel;
    private Connection connection;

    public BinaryToDecimalConverter() {
        initializeUI();
        initializeDatabase();
    }

    private void initializeUI() {
        frame = new JFrame("Binary to Decimal Converter");
        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Enter Binary Number:");
        binaryInput = new JTextField(20);
        JButton convertButton = new JButton("Convert");
        resultLabel = new JLabel("Result: ");

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertBinaryToDecimal();
            }
        });

        frame.add(label);
        frame.add(binaryInput);
        frame.add(convertButton);
        frame.add(resultLabel);
        frame.setVisible(true);
    }

    private void convertBinaryToDecimal() {
        String binaryString = binaryInput.getText();
        if (!binaryString.matches("[01]+")) {
            JOptionPane.showMessageDialog(frame, "Invalid binary number! Enter only 0s and 1s.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int decimalValue = Integer.parseInt(binaryString, 2);
        resultLabel.setText("Result: " + decimalValue);
        saveConversion(binaryString, decimalValue);
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:conversions.db");
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS conversions (id INTEGER PRIMARY KEY, binary TEXT, decimal INTEGER)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveConversion(String binary, int decimal) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO conversions (binary, decimal) VALUES (?, ?)");
            pstmt.setString(1, binary);
            pstmt.setInt(2, decimal);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BinaryToDecimalConverter::new);
    }
}