import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.DatagramPacket;
import java.io.ByteArrayOutputStream;

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
      *   portMulticast : port du groupe multicast
      *   adrSysteme : adresse du systeme central
      *   portSysteme : port du systeme central
      */
     public static void main(String argv[])
     {
          // Verification des arguments
          if (argv.length != 4) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java Thermometre groupeMulticast portMulticast adrSysteme portSysteme");
               System.exit(1);
          }

          try {
               // Variables pour le multicast
               byte data[] = new byte[100];
               InetAddress group = InetAddress.getByName(argv[0]);
               MulticastSocket socketMulticast = new MulticastSocket(new Integer(argv[1]));
               socketMulticast.joinGroup(group); // Connexion au groupe multicast
               DatagramPacket dp = new DatagramPacket(data, data.length);
               MessageTemperature msg;

               // Variables pour le TCP
               byte data2[] = new byte[100];
               InetAddress adrSysteme = InetAddress.getByName(argv[2]);
               Socket socketTCP = new Socket(adrSysteme, new Integer(argv[3]));
               ByteArrayOutputStream output = new ByteArrayOutputStream(100);

               while (true) {
                    // Reception des donnees depuis Air.java
                    socketMulticast.receive(dp);
                    msg = MessageTemperature.fromBytes(dp.getData(),dp.getLength());

                    if (msg.getType() == MessageTemperature.MESURE) {
                         // Affichage
                         System.out.println(msg.toString());

                         // Envoi des donnees au serveur central
                         data2 = msg.toBytes();
                         output.reset();
                         output.write(data2, 0, data2.length);
                         output.writeTo(socketTCP.getOutputStream());
                    }
               }
          } catch(Exception e) {
               System.err.println("[Erreur] Lecture socket : " + e);
          }
     }
}
