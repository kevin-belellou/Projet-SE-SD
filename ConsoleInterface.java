import java.rmi.*;

public interface ConsoleInterface extends Remote {
     public String getNom(int piece) throws RemoteException;

     public int getTemperature(int piece) throws RemoteException;

     public int getTemperatureVoulue(int piece) throws RemoteException;

     public int getNivChauffage(int piece) throws RemoteException;

     public int getNivChauffageVoulu(int piece) throws RemoteException;

     public void setTemperatureVoulue(int temp) throws RemoteException;

     public void setNivChauffageVoulu(int niv) throws RemoteException;
}
