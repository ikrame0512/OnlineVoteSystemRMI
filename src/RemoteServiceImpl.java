import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteServiceImpl extends UnicastRemoteObject implements RemoteService {
    private Map<String, Integer> voteCounts;

    public RemoteServiceImpl() throws RemoteException {
        super();
        voteCounts = new HashMap<>();
        String[] candidates = getCandidates();
        for (String candidate : candidates) {
            int initialVoteCount = 0;
            voteCounts.put(candidate, initialVoteCount);
        }


    }

    @Override
    public boolean login(String username, String password) throws RemoteException {
        try (Connection c= (Connection) Connector.getConnection();
             PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE username = ? AND passwd = ?")) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // If a matching row is found, login is successful
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error occurred.");
        }
    }

    @Override
    public boolean register(String username, String password) throws RemoteException {
        try (Connection c= (Connection) Connector.getConnection();
             PreparedStatement stmt = c.prepareStatement("INSERT INTO users (username, passwd) VALUES (?, ?)")) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            // If any row is affected, registration is successful
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error occurred.");
        }
    }
    @Override
    public String[] getCandidates() throws RemoteException {
        try (Connection conn= (Connection) Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT complet_name FROM candidat");
             ResultSet rs = stmt.executeQuery()) {

            List<String> candidates = new ArrayList<>();
            while (rs.next()) {
                candidates.add(rs.getString("complet_name"));
            }
            return candidates.toArray(new String[0]);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error occurred.");
        }
    }




    public int retrieveUserId(String username) throws RemoteException{
        // Code to query the database and retrieve the user ID based on the username
        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id_user FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_user");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error occurred.");

        }
        return 0;
    }
    public String GetUserById(int id) throws RemoteException{
        // Code to query the database and retrieve the user ID based on the username
        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users WHERE  id_user= ?")) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RemoteException("Database error occurred.");

        }

        return null;
    }



    public synchronized void voteIncrement(String candidate) throws RemoteException {
        if (voteCounts.containsKey(candidate)) {
            int count = voteCounts.get(candidate);
            voteCounts.put(candidate, count + 1);
        }
    }

    public synchronized int getVoteCount(String candidate) throws RemoteException {
        if (voteCounts.containsKey(candidate)) {
            return voteCounts.get(candidate);
        }
        return 0;
    }



}
