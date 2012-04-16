import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.*;

public class Console extends UnicastRemoteObject implements ConsoleInterface {
     public Console() throws RemoteException
     {
          super();
     }

     public String getNom(int piece) throws RemoteException
     {
          return "test";
     }
     

     public int getTemperature(int piece) throws RemoteException
     {
          return 0;
     }

     public int getTemperatureVoulue(int piece) throws RemoteException
     {
          return 0;
     }

     public int getNivChauffage(int piece) throws RemoteException
     {
          return 0;
     }

     public int getNivChauffageVoulu(int piece) throws RemoteException
     {
          return 0;
     }

     public void setTemperatureVoulue(int temp) throws RemoteException
     {

     }

     public void setNivChauffageVoulu(int niv) throws RemoteException
     {

     }

     public static void main(String argv[])
     {
          try {
               System.setSecurityManager(new RMISecurityManager());

               LocateRegistry.createRegistry(1099);

               Console c = new Console();
               Naming.bind("Console", c);

               System.out.println("Ready");
          } catch(Exception e) {
               e.printStackTrace();
          }
     }
}
