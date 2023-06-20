import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteService extends Remote {
    boolean login(String username, String password) throws RemoteException;
    boolean register(String username, String password) throws RemoteException;
    String[] getCandidates() throws RemoteException;
    int retrieveUserId(String username) throws RemoteException;
    void voteIncrement(String candidate) throws RemoteException;
    int getVoteCount(String candidate) throws RemoteException;
    String GetUserById(int id) throws RemoteException;


}
