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
      * Taille maximale des messages
      */
     protected static final int TAILLEBUFF = 100;

     /**
      * Lance le programme Chauffage. Les parametres sont les suivants :
      * $ java Chauffage groupeMulticast portMulticast piece adrSysteme portSysteme
      *   groupeMulticast: adresse IP du groupe multicast a utiliser pour la piece
      *   portMulticast : port du groupe multicast
      *   piece : nom de la piece
      *   adrSysteme : adresse du systeme central
      *   portSysteme : port du systeme central
      */
     public static void main(String argv[])
     {
          // Verification des arguments
          if (argv.length != 5) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java Chauffage groupeMulticast portMulticast piece adrSysteme portSysteme");
               System.exit(1);
          }

          try {
               // Variables pour le multicast
               byte data[] = new byte[TAILLEBUFF];
               InetAddress group = InetAddress.getByName(argv[0]);
               Integer port = new Integer(argv[1]);
               MulticastSocket socketMulticast = new MulticastSocket(port);
               MessageTemperature msg;
               String piece = new String(argv[2]);

               // Variables pour le TCP
               byte data2[] = new byte[TAILLEBUFF];
               int nb_octets;
               InetAddress adrSysteme = InetAddress.getByName(argv[3]);
               Socket socketTCP = new Socket(adrSysteme, new Integer(argv[4]));
               ByteArrayOutputStream output = new ByteArrayOutputStream(TAILLEBUFF);

               // Connexion au module communication du systeme central
               msg = new MessageTemperature(0, MessageTemperature.CHAUFFER, piece);
               data2 = msg.toBytes();
               output.reset();
               output.write(data2, 0, data2.length);
               output.writeTo(socketTCP.getOutputStream());

               int valeur, i;
               int val[] = new int[4];

               nb_octets = socketTCP.getInputStream().read(data2);
               while (nb_octets > 0) {
                    for (i = 0; i < 4; i++) {
                         if (data2[i] < 0)
                              val[i] = (data2[i] + 256) << (i * 8);
                         else
                              val[i] = data2[i] << (i * 8);
                    }
                    valeur = val[0] | val[1] | val[2] | val[3];

                    System.out.println("Chauffage niveau " + valeur + " dans la piece " + piece);

                    // Envoi des donnees à Air.java
                    msg = new MessageTemperature(valeur, MessageTemperature.CHAUFFER, piece);
                    data = msg.toBytes();
                    socketMulticast.send(new DatagramPacket(data, data.length, group, port));

                    nb_octets = socketTCP.getInputStream().read(data2);
               }
          } catch(Exception e) {
               System.err.println("[Erreur] Lecture socket : " + e);
          }
     }
}
