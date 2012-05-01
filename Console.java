import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.*;

public class Console extends UnicastRemoteObject implements ConsoleInterface {
     private int polo = 0;

     public Console() throws RemoteException
     {
          super();
     }

     public String getNom(int piece) throws RemoteException
     {
          polo++;

          String chaine = "youpi ";
          chaine += String.valueOf(polo);
          // données à envoyer : chaîne de caractères
          byte[] data = chaine.getBytes();

          this.envoyer(data);

          return "test";
     }
     

     public int getTemperature(int piece) throws RemoteException
     {
          return polo;
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

     protected void envoyer(byte[] data)
     {
          try
          {
               InetAddress adr;
               DatagramPacket packet;
               DatagramSocket socket;

               // adr contient l'@IP de la partie serveur
               adr = InetAddress.getByName("localhost");

               // création du paquet avec les données et en précisant l'adresse du serveur
               packet = new DatagramPacket(data, data.length, adr, 13000);

               // création d'une socket, sans la lier à un port particulier
               socket = new DatagramSocket();

               // envoi du paquet via la socket
               socket.send(packet);

               socket.close();
          } catch (Exception e) {
               System.err.println(e);
          }
     }
}
