import java.rmi.*;

public interface ConsoleInterface extends Remote {
     public String[] getInfos() throws RemoteException;

     public String[] getNoms() throws RemoteException;

     public boolean setTemperatureVoulue(int temp, String piece) throws RemoteException;

     public boolean setNivChauffageVoulu(int niv, String piece) throws RemoteException;
}
