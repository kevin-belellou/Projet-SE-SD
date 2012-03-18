import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

/**
 * Programme qui recoit la temperature courante d'une piece envoyee par Air.java
 * via le groupe multicast de cette piece, l'affiche à l'ecran et la communique
 * au module Communication Temperature du système central via des sockets TCP
 */
public class Thermometre {
     /**
      * Lance le programme Thermometre. Les parametres sont les suivants :
      * $ java Thermometre groupeMulticast portMulticast
      *   groupeMulticast: adresse IP du groupe multicast a utiliser pour la piece
      *   port : port du groupe multicast
      */
     public static void main(String argv[]) {
          if (argv.length != 2) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java Thermometre groupeMulticast portMulticast");
               System.exit(1);
          }

          try {
               byte tab[] = new byte[100];
               InetAddress group = InetAddress.getByName(argv[0]);
               MulticastSocket socket = new MulticastSocket(new Integer(argv[1]));
               socket.joinGroup(group);
               DatagramPacket dp = new DatagramPacket(tab, tab.length);
               MessageTemperature msg;

               while (true) {
                    socket.receive(dp);
                    msg = MessageTemperature.fromBytes(dp.getData(),dp.getLength());
                    if (msg.getType() == MessageTemperature.MESURE) {
                         System.out.println(msg.toString());
                    }
               }
          } catch(Exception e) {
               System.err.println("[Erreur] Lecture socket : "+e);
          }
     }
}
