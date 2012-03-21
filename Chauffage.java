import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.DatagramPacket;
import java.io.ByteArrayOutputStream;

/**
 * Programme qui recoit les demandes de chauffage d'une piece envoyee par le systeme central
 * via des sockets TCP, les affiche à l'ecran et les communique
 * a Air.java via le groupe multicast de cette piece.
 */
public class Chauffage {
     /**
      * Lance le programme Chauffage. Les parametres sont les suivants :
      * $ java Chauffage groupeMulticast portMulticast nivChauffage
      *   groupeMulticast: adresse IP du groupe multicast a utiliser pour la piece
      *   port : port du groupe multicast
      *   nivChauffage : parametre temporaire, niveau de chauffage demande
      */
     public static void main(String argv[])
     {
          // Verification des arguments
          if (argv.length != 3) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java Chauffage groupeMulticast portMulticast nivChauffage");
               System.exit(1);
          }

          try {
               // Variables pour le multicast
               byte data[] = new byte[100];
               InetAddress group = InetAddress.getByName(argv[0]);
               Integer port = new Integer(argv[1]);
               MulticastSocket socketMulticast = new MulticastSocket(port);
               MessageTemperature msg;

               while (true) {
                    // Envoi des donnees à Air.java
                    msg = new MessageTemperature(new Integer(argv[2]), MessageTemperature.CHAUFFER, "chambre");
                    data = msg.toBytes();
                    socketMulticast.send(new DatagramPacket(data, data.length, group, port));
               }
          } catch(Exception e) {
               System.err.println("[Erreur] Lecture socket : " + e);
          }
     }
}
