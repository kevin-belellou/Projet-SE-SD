import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.net.*;
import java.util.HashMap;

public class Console extends UnicastRemoteObject implements ConsoleInterface {
     protected static final int TAILLEBUFF = 200;

     protected static final byte GET_INFOS = 0;

     protected static final byte GET_NOMS = 1;

     protected static final byte SET_TEMP = 2;

     protected static final byte SET_NIV = 3;

     public Console() throws RemoteException
     {
          super();
     }

     public String[] getInfos() throws RemoteException
     {
          return null;
     }

     public String[] getNoms() throws RemoteException
     {
          byte[] data = new byte[1];
          data[0] = Console.GET_INFOS;
          String[] resultat = null;

          HashMap retour;
          retour = this.envoyerEtRecevoir(data);

          if (!retour.isEmpty()) {
               Object[] key = retour.keySet().toArray();
               Integer tailleTableau = new Integer((Integer)key[0]);

               Object value = retour.get(key[0]);
               byte[] valeurs = (byte[])value;

               Integer nbPieces = new Integer(this.convertirInt(valeurs, 0));
               resultat = new String[nbPieces * 5];

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

     public int setTemperatureVoulue(int temp, String piece) throws RemoteException
     {
          return 0;
     }

     public int setNivChauffageVoulu(int niv, String piece) throws RemoteException
     {
          return 0;
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

     protected HashMap envoyerEtRecevoir(byte[] dataAEnvoyer)
     {
          HashMap<Integer, byte[]> valeurRetour = new HashMap<Integer, byte[]>(1);
          try
          {
               byte[] dataRecue = new byte[TAILLEBUFF];
               InetAddress adr;
               DatagramPacket packetAEnvoyer;
               DatagramPacket packetRecu = new DatagramPacket(dataRecue, dataRecue.length);
               DatagramSocket socket;

               // adr contient l'@IP de la partie serveur
               adr = InetAddress.getByName("localhost");

               // création du paquet avec les données et en précisant l'adresse du serveur
               packetAEnvoyer = new DatagramPacket(dataAEnvoyer, dataAEnvoyer.length, adr, 13000);

               // création d'une socket, sans la lier à un port particulier
               socket = new DatagramSocket();

               socket.setSoTimeout(500);
                
               int nb_octets = 0, cpt = 0;
               boolean reussi = true;

               do {
                    try {
                         reussi = true;
                         cpt++;
                         socket.send(packetAEnvoyer);
                         socket.receive(packetRecu);
                         dataRecue = packetRecu.getData();
                         nb_octets = packetRecu.getLength();

                         valeurRetour.put(nb_octets, dataRecue);
                    } catch (SocketTimeoutException ste) {
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
