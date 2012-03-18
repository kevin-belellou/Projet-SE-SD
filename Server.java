import java.net.*;
import java.io.*;

public class Server {
     public static void main(String argv[]) {
          try {
               // serveur positionne sa socket d'écoute sur le port local 7777
               ServerSocket serverSocket = new ServerSocket(12000);

               // se met en attente de connexion de la part d'un client distant
               Socket socket = serverSocket.accept();

               // connexion acceptée : récupère les flux objets pour communiquer
               // avec le client qui vient de se connecter
//               ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
               ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

               // attente les données venant du client
               while (true) {
                    Integer temp = (Integer)input.readObject();
                    System.out.println(" recu : "+temp);
               }

               // affiche les coordonnées du client qui vient de se connecter
//               System.out.println(" ca vient de : "+socket.getInetAddress()+":"+socket.getPort());
               // envoi d'une réponse au client
//               output.writeObject(new String("bien recu"));
          } catch (Exception e) {
               System.err.println(e);
          }
     }
}
