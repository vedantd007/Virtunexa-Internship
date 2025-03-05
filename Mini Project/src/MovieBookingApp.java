import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MovieBookingApp {
    public static void main(String[] args) {
        System.out.println("Starting Movie Booking App...");

        // Step 1: Connect to the Database
        Database.connect();  // Line 93 (Potentially causing the error)

        // Step 2: Create necessary tables if not already created
        Database.createTables();

        // Step 3: Launch the GUI using Swing (Runs on Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            new MainFrame(); // This is your main UI window
        });
    }
}

// Main UI Frame for the application
class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Movie Ticket Booking System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JLabel welcomeLabel = new JLabel("Welcome to Movie Booking System", SwingConstants.CENTER);
        panel.add(welcomeLabel);

        JButton bookButton = new JButton("Book Tickets");
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new BookingFrame(); // Opens booking window
            }
        });

        panel.add(bookButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton);

        add(panel);
        setVisible(true);
    }
}

// Booking Window
class BookingFrame extends JFrame {
    public BookingFrame() {
        setTitle("Book Tickets");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Booking Functionality Here...", SwingConstants.CENTER);
        add(label);

        setVisible(true);
    }
}
