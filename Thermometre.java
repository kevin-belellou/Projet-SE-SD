import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.Socket;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
               byte data[] = new byte[100];
               InetAddress group = InetAddress.getByName(argv[0]);
               MulticastSocket socketMulticast = new MulticastSocket(new Integer(argv[1]));
               socketMulticast.joinGroup(group);
               DatagramPacket dp = new DatagramPacket(data, data.length);
               MessageTemperature msg;

               InetAddress systeme = InetAddress.getByName("127.0.0.1");
               Socket socketTCP = new Socket(systeme, 12000);
//               ObjectInputStream input = new ObjectInputStream(socketTCP.getInputStream());
               ObjectOutputStream output = new ObjectOutputStream(socketTCP.getOutputStream());

               while (true) {
                    socketMulticast.receive(dp);
                    msg = MessageTemperature.fromBytes(dp.getData(),dp.getLength());
                    if (msg.getType() == MessageTemperature.MESURE) {
                         System.out.println(msg.toString());
                         output.writeObject(msg.getPiece() + " | " + msg.getValeur());
                    }
               }
          } catch(Exception e) {
               System.err.println("[Erreur] Lecture socket : " + e);
          }
     }
}
