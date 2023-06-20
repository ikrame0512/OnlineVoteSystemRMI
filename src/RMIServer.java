import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServer {
    public static void main(String[] args) {

        try {


            // Création de l'objet d'implémentation du système de vote
            RemoteServiceImpl votingSystemObj = new RemoteServiceImpl();

            // Création du registre RMI
            Registry registry = LocateRegistry.createRegistry(2000);

            // Lier l'objet d'implémentation du système de vote au registre RMI
            registry.bind("RemoteService", votingSystemObj);

            System.out.println("Server started. Waiting for client requests...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
