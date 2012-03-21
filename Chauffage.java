import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.io.ObjectInputStream;

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
     public static void main(String argv[]) {
          if (argv.length != 3) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java Chauffage groupeMulticast portMulticast nivChauffage");
               System.exit(1);
          }

          try {
               byte data[] = new byte[100];
               InetAddress group = InetAddress.getByName(argv[0]);
               MulticastSocket socketMulticast = new MulticastSocket(new Integer(argv[1]));
               MessageTemperature msg;

//               InetAddress systeme = InetAddress.getByName("127.0.0.1");
//               Socket socketTCP = new Socket(systeme, 12000);
//               ObjectInputStream input = new ObjectInputStream(socketTCP.getInputStream());

               while (true) {
                    msg = new MessageTemperature(new Integer(argv[2]), MessageTemperature.CHAUFFER, "chambre");
                    data = msg.toBytes();
                    socketMulticast.send(new DatagramPacket(data, data.length, group, new Integer(argv[1])));
               }
          } catch(Exception e) {
               System.err.println("[Erreur] Lecture socket : " + e);
          }
     }
}
