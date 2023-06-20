import java.awt.*;
import java.rmi.RemoteException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Map;

public class VoteSystemGUI extends JFrame {
    private RemoteService remoteService;

    private JFrame frame;
    private JLabel UserName;
    private JComboBox<String> candidateComboBox;
    private JButton voteButton;
    private JLabel resultLabel;
    private JTextArea resultArea;
    private String username;
    private Map<String, UserSession> activeSessions;
    private JButton logoutButton;



    public VoteSystemGUI(RemoteService remoteService, String username, Map<String, UserSession> activeSessions) throws RemoteException {
        this.remoteService = remoteService;
        this.username=username;
        this.activeSessions = activeSessions;

        frame = new JFrame("Vote System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBackground(Color.decode("#2ED9C3"));

        frame.getContentPane().add(panel);
        JLabel UserName = new JLabel("user name connected : "+remoteService.GetUserById(remoteService.retrieveUserId(username)));
        panel.add(UserName);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel candidateLabel = new JLabel("Select a candidate:");
        panel.add(candidateLabel);

        candidateComboBox = new JComboBox<>();
        panel.add(candidateComboBox);

        voteButton = new JButton("Vote");
        voteButton.setBackground(Color.decode("#FF8473"));
        voteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    vote(username);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.decode("#FF8473"));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                    logout(username);


            }
        });
        panel.add(voteButton);
        panel.add(logoutButton);

        resultLabel = new JLabel("List Vote:");
        panel.add(resultLabel);

        // Result area
        resultArea = new JTextArea(10, 20);
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);
        panel.add(scrollPane);

        // Retrieve candidate names from the remote service
        try {
            String[] candidates = remoteService.getCandidates();

            candidateComboBox.setModel(new DefaultComboBoxModel<>(candidates));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        frame.pack();
        frame.setVisible(true);
    }
    private void logout(String username)  {
            activeSessions.remove(username);
                   JOptionPane.showMessageDialog(frame, "Logged out successfully!");
            frame.dispose();
            SwingUtilities.invokeLater(() -> {
                LoginClientGUI loginClientGUI = new LoginClientGUI(remoteService);
                loginClientGUI.setLocationRelativeTo(null); // Center the window
            });
    }
    private synchronized void vote(String username) throws RemoteException {
        String selectedCandidate = (String) candidateComboBox.getSelectedItem();

        if (selectedCandidate != null) {
            int userId = remoteService.retrieveUserId(username);
            remoteService.voteIncrement(selectedCandidate);
            try {
                JOptionPane.showMessageDialog(VoteSystemGUI.this,
                        "Vote casted for candidate: " + selectedCandidate,
                        "Vote Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
                updateVoteCounts();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(VoteSystemGUI.this,
                        "Error casting vote: " + ex.getMessage(),
                        "Vote Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(VoteSystemGUI.this,
                    "Select a candidate",
                    "Vote Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVoteCounts() {
        resultArea.setText("");
        try {
            resultArea.append("Vote Counts:\n");
            resultArea.append("--------------\n");
            String[] candidates = remoteService.getCandidates();
            for (String candidate : candidates) {
                resultArea.append(candidate + ": " + remoteService.getVoteCount(candidate) + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(VoteSystemGUI.this,
                    "Error retrieving vote counts: " + e.getMessage(),
                    "Vote Count Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public void main(String[] args) {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 2000);
            RemoteService remoteService = (RemoteService) registry.lookup("RemoteService");
            SwingUtilities.invokeLater(() -> {
                VoteSystemGUI voteSystemGUI = null;
                try {
                    voteSystemGUI = new VoteSystemGUI(remoteService, username, activeSessions);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                voteSystemGUI.setLocationRelativeTo(null); // Center the window
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
