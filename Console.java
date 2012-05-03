import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.*;
import java.util.HashMap;

/**
 * Serveur RMI pour la communication avec les Consoles,
 * implemente l'interface ConsoleInterface.
 */
public class Console extends UnicastRemoteObject implements ConsoleInterface {
     /**
      * Constante pour la taille des buffers
      */
     protected static final int TAILLEBUFF = 200;

     /**
      * Constante precisant que la commande appelee est getInfos().
      */
     protected static final byte GET_INFOS = 0;

     /**
      * Constante precisant que la commande appelee est getNoms().
      */
     protected static final byte GET_NOMS = 1;

     /**
      * Constante precisant que la commande appelee est setTemperatureVoulue().
      */
     protected static final byte SET_TEMP = 2;

     /**
      * Constante precisant que la commande appelee est setNivChauffageVoulu().
      */
     protected static final byte SET_NIV = 3;

     /**
      * Variable contenant l'adresse du serveur UDP du systeme.
      */
     protected static InetAddress adrServeur;

     /**
      * Variable contenant le port du serveur UDP du systeme.
      */
     protected static int portServeur;

     /**
      * Constructeur de la classe Console.
      * Les parametres sont :
      *   - adr : adresse du serveur UDP du systeme
      *   - port : port du serveur UDP du systeme
      */
     public Console(String adr, int port) throws RemoteException
     {
          // Constructeur par defaut
          super();

          // Initialisation des parametres
          try {
               this.adrServeur = InetAddress.getByName(adr);
               this.portServeur = port;
          } catch (Exception e) {
               e.printStackTrace();
          }
     }

     /**
      * Renvoie un tableau de String qui contient toutes
      * les infos a afficher pour toutes les pieces.
      * Les infos sont rangees dans cet ordre :
      *   - Nom de la piece
      *   - Temperature actuelle
      *   - Temperature voulue
      *   - Niveau de chauffage actuel
      *   - Niveau de chauffage voulu
      */
     public String[] getInfos() throws RemoteException
     {
          // Declaration du tableau de byte a envoyer
          byte[] data = new byte[1];

          // On indique quelle est la demande
          data[0] = Console.GET_INFOS;

          // Initialisation de la valeur de retour
          String[] resultat = null;

          // Envoi et reception
          HashMap retour = this.envoyerEtRecevoir(data);

          if (!retour.isEmpty()) { // Si l'envoi/reception s'est bien passe
               // Recuperation de la taille du tableau
               Object[] key = retour.keySet().toArray();
               Integer tailleTableau = new Integer((Integer)key[0]);

               // Recuperation du tableau
               Object value = retour.get(key[0]);
               byte[] valeurs = (byte[])value;

               // Allocation de resultat selon le nombre de pieces
               Integer nbPieces = new Integer(this.convertirInt(valeurs, 0));
               resultat = new String[nbPieces * 5];

               // Remplissage de resultat avec les donnee recues
               int cpt = 4, i = 0;
               while (cpt < tailleTableau) {
                    Integer tailleNomPiece = new Integer(this.convertirInt(valeurs, cpt));
                    cpt += 4;

                    resultat[i++] = new String(valeurs, cpt, tailleNomPiece);
                    cpt += tailleNomPiece;

                    for (int j = 0; j < 4; j++) {
                         resultat[i++] = this.convertirInt(valeurs, cpt);
                         cpt += 4;
                    }
               }
          }

          return resultat;
     }

     /**
      * Renvoie un tableau de String qui contient le nom de chaque piece.
      */
     public String[] getNoms() throws RemoteException
     {
          // Declaration du tableau de byte a envoyer
          byte[] data = new byte[1];

          // On indique quelle est la demande
          data[0] = Console.GET_NOMS;

          // Initialisation de la valeur de retour
          String[] resultat = null;

          // Envoi et reception
          HashMap retour = this.envoyerEtRecevoir(data);

          if (!retour.isEmpty()) { // Si l'envoi/reception s'est bien passe
               // Recuperation de la taille du tableau
               Object[] key = retour.keySet().toArray();
               Integer tailleTableau = new Integer((Integer)key[0]);

               // Recuperation du tableau
               Object value = retour.get(key[0]);
               byte[] valeurs = (byte[])value;

               // Allocation de resultat selon le nombre de pieces
               Integer nbPieces = new Integer(this.convertirInt(valeurs, 0));
               resultat = new String[nbPieces];

               // Remplissage de resultat avec les donnee recues
               int cpt = 4, i = 0;
               while (cpt < tailleTableau) {
                    Integer tailleNomPiece = new Integer(this.convertirInt(valeurs, cpt));
                    cpt += 4;

                    resultat[i++] = new String(valeurs, cpt, tailleNomPiece);
                    cpt += tailleNomPiece;
               }
          }

          return resultat;
     }

     /**
      * Permet de modifier la temperature voulue d'une piece.
      * Les parametres sont :
      *   - temp : temperature voulue
      *   - piece : piece concernee
      * Renvoie VRAI si tout s'est bien passe, FAUX sinon.
      */
     public boolean setTemperatureVoulue(int temp, String piece) throws RemoteException
     {
          // Declaration du tableau de byte a envoyer
          byte[] data = new byte[5 + piece.length()];

          // On indique quelle est la demande
          data[0] = Console.SET_TEMP;

          // Initialisation de la valeur de retour
          boolean resultat = false;

          // On ecrit temp dans data
          for (int i = 0; i < 4; i++) {
               data[i + 1] = (byte) (temp & 0x000000FF);
               temp = temp >>> 8;
          }

          // On ecrit piece dans data
          byte[] tabPiece = piece.getBytes();
          for (int i = 0; i < piece.length(); i++)
               data[i + 5] = tabPiece[i];

          // Envoi et reception
          HashMap retour = this.envoyerEtRecevoir(data);

          if (!retour.isEmpty()) { // Si l'envoi/reception s'est bien passe
               // Recuperation de la taille du tableau
               Object[] key = retour.keySet().toArray();
               Integer tailleTableau = new Integer((Integer)key[0]);

               // Recuperation du tableau
               Object value = retour.get(key[0]);
               byte[] valeurs = (byte[])value;

               // Recuperation de la valeur de retour
               Integer valeurRetournee = new Integer(this.convertirInt(valeurs, 0));

               if (valeurRetournee == 1)
                    resultat = true;
          }

          return resultat;
     }

     /**
      * Permet de modifier le niveau de chauffage voulu d'une piece.
      * Les parametres sont :
      *   - niv : niveau de chauffage voulu
      *   - piece : piece concernee
      * Renvoie VRAI si tout s'est bien passe, FAUX sinon.
      */
     public boolean setNivChauffageVoulu(int niv, String piece) throws RemoteException
     {
          // Declaration du tableau de byte a envoyer
          byte[] data = new byte[5 + piece.length()];

          // On indique quelle est la demande
          data[0] = Console.SET_NIV;

          // Initialisation de la valeur de retour
          boolean resultat = false;

          // On ecrit niv dans data
          for (int i = 0; i < 4; i++) {
               data[i + 1] = (byte) (niv & 0x000000FF);
               niv = niv >>> 8;
          }

          // On ecrit piece dans data
          byte[] tabPiece = piece.getBytes();
          for (int i = 0; i < piece.length(); i++)
               data[i + 5] = tabPiece[i];

          // Envoi et reception
          HashMap retour = this.envoyerEtRecevoir(data);

          if (!retour.isEmpty()) { // Si l'envoi/reception s'est bien passe
               // Recuperation de la taille du tableau
               Object[] key = retour.keySet().toArray();
               Integer tailleTableau = new Integer((Integer)key[0]);

               // Recuperation du tableau
               Object value = retour.get(key[0]);
               byte[] valeurs = (byte[])value;

               // Recuperation de la valeur de retour
               Integer valeurRetournee = new Integer(this.convertirInt(valeurs, 0));

               if (valeurRetournee == 1)
                    resultat = true;
          }

          return resultat;
     }

     /**
      * Lance le programme Console. Les parametres sont les suivants :
      * $ java -Djava.security.policy=java.policy Console adrServeurUDP portServeurUDP
      *   - adrServeurUDP : adresse du serveur UDP du systeme
      *   - portServeurUDP : port du serveur UDP du systeme
      */
     public static void main(String argv[])
     {
          // Verification des arguments
          if (argv.length != 2) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java -Djava.security.policy=java.policy Console adrServeurUDP portServeurUDP");
               System.exit(1);
          }

          try {
               // Lancement security manager
               System.setSecurityManager(new RMISecurityManager());

               // Lancement registry sur port par defaut
               LocateRegistry.createRegistry(1099);

               // Creation de la console avec parametres
               Console c = new Console(argv[0], Integer.valueOf(argv[1]));

               // Declaration de la console au registry
               Naming.bind("Console", c);

               System.out.println("Ready");
          } catch(Exception e) {
               e.printStackTrace();
          }
     }

     /**
      * Permet l'envoi et la reception des tableaux de bytes representant
      * les demandes effectuees par les consoles et les reponses associees.
      * Renvoie un HashMap qui ne contient q'une seule entree.
      * Cette entree ayant pour cle la taille du tableau de bytes recu,
      * et pour valeur, le tableau de bytes
      */
     protected HashMap envoyerEtRecevoir(byte[] dataAEnvoyer)
     {
          // Declaration du HashMap de taille 1
          HashMap<Integer, byte[]> valeurRetour = new HashMap<Integer, byte[]>(1);

          try {
               // Tableau de bytes pour la reception
               byte[] dataRecue = new byte[TAILLEBUFF];
               
               DatagramPacket packetAEnvoyer;
               DatagramPacket packetRecu = new DatagramPacket(dataRecue, dataRecue.length);
               DatagramSocket socket;

               // creation du paquet avec les donnees et en precisant l'adresse du serveur
               packetAEnvoyer = new DatagramPacket(dataAEnvoyer, dataAEnvoyer.length, this.adrServeur, this.portServeur);

               // creation d'une socket, sans la lier a un port particulier
               socket = new DatagramSocket();

               // Activation d'un temps maximum pour la lecture sur la socket
               socket.setSoTimeout(500);

               int nb_octets = 0, cpt = 0;
               boolean reussi = true;

               // Fonction d'envoi et de reception
               // Envoi des donnees puis tentative de lecture
               // Si timeout de la lecture alors recommencer (max 4 fois)
               do {
                    try {
                         reussi = true;
                         cpt++;

                         // Envoi de la demande et des donnees
                         socket.send(packetAEnvoyer);

                         // Reception du resultat
                         socket.receive(packetRecu);
                         dataRecue = packetRecu.getData();
                         nb_octets = packetRecu.getLength();

                         // Ecriture du resultat dans le HashMap
                         valeurRetour.put(nb_octets, dataRecue);
                    } catch (SocketTimeoutException ste) { // On a eu un timeout
                         reussi = false;
                    } catch (Exception e) {
                         e.printStackTrace();
                    }
               } while (!reussi && cpt < 4);

               socket.close();
          } catch (Exception e) {
               System.err.println(e);
          }
          return valeurRetour;
     }

     /**
      * Convertit les entiers stockes sous forme de tableaux de 4 bytes,
      * en int puis en String
      */
     protected String convertirInt(byte[] tab, int debut)
     {
          int[] val = new int[4];

          for (int i = 0; i < 4; i++) {
               if (tab[i] < 0)
                    val[i] = (tab[debut + i] + 256) << (i * 8);
               else
                    val[i] = tab[debut + i] << (i * 8);
          }
          int valeur = val[0] | val[1] | val[2] | val[3];

          return new String(new Integer(valeur).toString());
     }
}
