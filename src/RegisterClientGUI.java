import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import javax.swing.*;

public class RegisterClientGUI extends JFrame {
    private static final String RMI_URL = "rmi://localhost/RemoteService";
    private RemoteService remoteService;

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel loginLabel;

    public RegisterClientGUI(RemoteService remoteService) {
        this.remoteService = remoteService;
        frame = new JFrame("Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#2ED9C3"));

        frame.getContentPane().add(panel);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);

        usernameField = new JTextField(20);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        panel.add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.decode("#FF8473"));
        registerButton.addActionListener(e -> register());
        panel.add(registerButton);

        loginLabel = new JLabel("Already have an account? Click here to login:");
        panel.add(loginLabel);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.decode("#FF8473"));
        loginButton.addActionListener(e -> switchToLogin());
        panel.add(loginButton);

        frame.pack();
        frame.setVisible(true);
    }

    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            boolean registered = remoteService.register(username, password);

            if (registered) {
                JOptionPane.showMessageDialog(frame, "Registration successful!");
                frame.dispose(); // Close the registration window

                // Open the login window
                SwingUtilities.invokeLater(() -> {
                    LoginClientGUI loginClient = new LoginClientGUI(remoteService);
                    loginClient.setLocationRelativeTo(null); // Center the window
                });
            } else {
                JOptionPane.showMessageDialog(frame, "Username already exists.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void switchToLogin() {
        frame.dispose(); // Close the registration window

        // Open the login window
        SwingUtilities.invokeLater(() -> {
            LoginClientGUI loginClient = new LoginClientGUI(remoteService);
            loginClient.setLocationRelativeTo(null); // Center the window
        });
    }

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 2000);
            RemoteService remoteService = (RemoteService) registry.lookup("RemoteService");
            SwingUtilities.invokeLater(() -> {
                RegisterClientGUI client = new RegisterClientGUI(remoteService);
                client.setLocationRelativeTo(null); // Center the window
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
