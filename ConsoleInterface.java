import java.rmi.*;

public interface ConsoleInterface extends Remote {
     public String[] getInfos() throws RemoteException;

     public String[] getNoms() throws RemoteException;

     public int setTemperatureVoulue(int temp, String piece) throws RemoteException;

     public int setNivChauffageVoulu(int niv, String piece) throws RemoteException;
}
