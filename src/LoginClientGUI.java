import java.awt.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

public class LoginClientGUI extends JFrame{
    private Map<String, UserSession> activeSessions;

    private RemoteService remoteService;

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel registerLabel;


    public LoginClientGUI(RemoteService remoteService) {
        activeSessions = new HashMap<>();
        this.remoteService = remoteService;
        frame = new JFrame("Login");
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

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(Color.decode("#FF8473"));

        loginButton.addActionListener(e -> login());
        panel.add(loginButton);
        registerLabel = new JLabel("Don't have an account? Click here to register:");
        panel.add(registerLabel);

        JButton registerButton = new JButton("Register");
        registerButton.setBackground(Color.decode("#FF8473"));

        registerButton.addActionListener(e -> switchToRegister());
        panel.add(registerButton);

        frame.pack();
        frame.setVisible(true);
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            boolean loggedIn = remoteService.login(username, password);

            if (loggedIn) {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                int userId = remoteService.retrieveUserId(username); // Replace with your code to retrieve the user ID
                UserSession session = new UserSession(userId);
                activeSessions.put(username, session);
                frame.dispose();
                SwingUtilities.invokeLater(() -> {
                    VoteSystemGUI voteSystemGUI = null;
                    try {
                        voteSystemGUI = new VoteSystemGUI(remoteService,username,activeSessions);
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                    voteSystemGUI.setLocationRelativeTo(null); // Center the window
                });
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect username or password.");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private void switchToRegister() {
        frame.dispose(); // Close the login window

        // Open the registration window
        SwingUtilities.invokeLater(() -> {
            RegisterClientGUI registerClient = new RegisterClientGUI(remoteService);
            registerClient.setLocationRelativeTo(null); // Center the window
        });
    }
    public int getLoggedInUserId(String username) throws RemoteException {
        UserSession session = activeSessions.get(username);
        if (session != null) {
            return session.getUserId();
        } else {
            return 0; // or any default value you prefer
        }
    }


    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 2000);
            RemoteService remoteService = (RemoteService) registry.lookup("RemoteService");
            SwingUtilities.invokeLater(() -> {
                LoginClientGUI client = new LoginClientGUI(remoteService);
                client.setLocationRelativeTo(null); // Center the window
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
