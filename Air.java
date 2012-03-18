import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;

import java.lang.Thread;

import java.util.Random;
import java.lang.Math;
import java.text.DecimalFormat;

/**
 * Programme qui simule l'evolution de la temperature de l'air d'une
 * piece en fonction de la temperature exterieure et du niveau de
 * chauffage demande. Chaque seconde la temperature courante est
 * envoyee via un message de type MessageTemperature sur le groupe
 * multicast du programme.
 *
 * Les demandes de chauffage sont prises en compte une fois toutes les
 * 3 secondes (si plusieurs demandes sont arrivees dans un intervalle
 * de 3 secondes, on prend uniquement en compte la derniere). Une
 * demande de chauffage est traitee une seule fois. Si l'on veut
 * chauffer en continu l'air, il faut donc envoyer regulierement des
 * demandes de chauffage. Une demande de chauffage est effectuee par
 * l'envoi d'un message de type MessageTemperature sur le groupe
 * multicast du programme.
 *
 * La temperature exterieure evolue entre une temperature minimale (la
 * nuit) et une temperature maximale (le jour) de maniere lineaire, en
 * augmentant d'abord la premiere demi-journee puis diminuant ensuite
 * lors de la seconde. Une journee complete dure 5 minutes. Les
 * temperatures minimales et maximales sont modifiees a la fin de
 * chaque journee.
 *
 * Le programme se lance avec les parametres suivants :
 * $ java Air groupeMulticast portMulticast nomPiece [seedRandom]
 *   groupeMulticast : adresse IP du groupe multicast a utiliser pour la piece
 *   port : port du groupe multicast
 *   nomPiece : nom de la piece
 *   seedRandom : parametre optionnel initialisant le generateur de nombres aleatoires qui
 *   determine la temperature exterieure. On lancera de preference les programmes Air de
 *   toutes les pieces avec la meme valeur.
 */
public class Air extends Thread {

     /**
      * Adresse du groupe multicast de la piece
      */
     protected InetAddress groupMulticast;

     /**
      * Port du groupe multicast
      */
     protected int port;

     /**
      * Socket multicast
      */
     protected MulticastSocket socket;

     /**
      * Nom de la piece
      */
     protected String nomPiece;

     /**
      * Temperature courante de la piece
      */
     protected volatile float temperatureCourante;

     /**
      * Temperature exterieure de la maison
      */
     protected float temperatureExt;

     /**
      * Generateur de nombre aleatoire servant a initiliser puis
      * modifier la temperature exterieure
      */
     protected Random generateur;

     /**
      * Format d'affichage des flottants
      */
     protected DecimalFormat format;

     /**
      * Fonction qui envoie sur le groupe multicast un message
      * precisant la temperature courante. Affiche sur la sortie
      * standard la valeur de la temperature courante et exterieure.
      * Affiche sur la sortie d'erreur un message en cas de probleme.
      */
     public void envoyerTemp() {
          try {
               System.out.println(this.toString());
               MessageTemperature msg = new MessageTemperature(Math.round(temperatureCourante), MessageTemperature.MESURE, nomPiece);
               byte tab[] = msg.toBytes();
               socket.send(new DatagramPacket(tab, tab.length, groupMulticast, port));
          } catch(Exception e) {
               System.err.println("[Erreur] envoi mesure temperature : "+e);
          }
     }

     /**
      * Fonction qui calcule et gere les variations de la temperature
      * courante en fonction de la temperature exterieure. Affiche sur
      * la sortie standard et envoie sur le groupe multicast la valeur
      * de la temperature courante 1 fois par seconde.
      */
     public void variations() {
          float tempNuit, tempJour;
          // duree complete d'une journee
          int intervalle = 30;

          // initialisation des temperatures du jour et de la nuit
          tempNuit = generateur.nextFloat() * 10 - 5;
          tempJour = generateur.nextFloat() * 10 + 10;
          temperatureExt = tempNuit;
          temperatureCourante = (tempJour - tempNuit) / 2;

          System.out.println(" *** valeurs initiales : nuit = "+ format.format(tempNuit) +" jour = "+format.format(tempJour));

          while (true) {
               for (int j = 0; j < intervalle ; j++) {
                    for (int i=0; i < 10 ; i++) {
                         try {
                              Thread.sleep(1000);
                         } catch (Exception e) { }
                         // on modifie la temperature courante selon la temperature exterieure
                         temperatureCourante += (temperatureExt - temperatureCourante) * 0.02;
                         envoyerTemp();
                    }
                    // le jour : on augmente la temperature exterieure
                    if ( j < intervalle / 2)
                         temperatureExt += (tempJour - tempNuit) / ( intervalle / 2);
                    // la nuit : on diminue la temperature exterieure
                    else
                         temperatureExt -= (tempJour - tempNuit) / ( intervalle / 2);
               }

               // on modifie a la fin complete de la journee les temperatures max et min
               tempNuit += generateur.nextFloat()* 6 - 3;
               tempJour += generateur.nextFloat()* 6 - 3;
               if (tempNuit > tempJour) {
                    float temp = tempNuit;
                    tempNuit = tempJour;
                    tempJour = temp;
               }
          }
     }

     /**
      * Thread qui toutes les 3 secondes recupere la valeur de la
      * derniere demande de chauffage et modifie le cas echeant la
      * temperature courante en fonction de la puissance de
      * chauffage. En cas d'erreur, se termine.
      */
     public void run() {
          // lance le thread qui attend les messages sur la socket
          AttentePaquet attente = new AttentePaquet(socket);
          attente.start();

          int valeur;
          try {
               while(true) {
                    Thread.sleep(3000);
                    valeur = attente.getDernier();
                    if (valeur >= 0) {
                         System.out.println(" == demande de chauffage de niveau "+valeur);
                         temperatureCourante += valeur / 4.0;
                    }
               }
          } catch (Exception e) {
               System.err.println("[Erreur] reception donnees chauffage : "+e);
          }
     }

     /**
      * Initialise la socket multicast. En cas d'erreur, le programme
      * se termine.
      */
     protected void initMulticast(String nomMachine, int port) {
          try {
               groupMulticast = InetAddress.getByName(nomMachine);
               socket = new MulticastSocket(port);
               socket.joinGroup(groupMulticast);
          } catch(Exception e) {
               System.err.println("[Erreur] Impossible de creer la socket multicast : "+e);

               System.exit(1);
          }
     }

     public String toString() {
          return "Piece = "+nomPiece+" | temp = "+format.format(temperatureCourante)+
                 " | ext = "+format.format(temperatureExt);
     }

     public Air(String adrMulti, int port, String piece, int initRandom) {
          initMulticast(adrMulti, port);
          this.port = port;
          nomPiece = piece;

          generateur = new Random(initRandom);

          format = new DecimalFormat("00.00");
     }

     public Air(String adrMulti, int port, String piece) {
          initMulticast(adrMulti, port);
          this.port = port;
          nomPiece = piece;

          generateur = new Random(0);

          format = new DecimalFormat("00,00");
     }

     /**
      * Lance le programme Air. Les parametres sont les suivants :
      * $ java Air groupeMulticast portMulticast nomPiece [seedRandom]
      *   groupeMulticast : adresse IP du groupe multicast a utiliser pour la piece
      *   port : port du groupe multicast
      *   nomPiece : nom de la piece
      *   seedRandom : parametre optionnel initialisant le generateur de nombres aleatoires qui
      *   determine la temperature exterieure. On lancera de preference les programmes Air de
      *   toutes les pieces avec la meme valeur.
      */
     public static void main(String argv[]) {
          if (argv.length < 3) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java Air groupeMulticast portMulticast nomPiece [seedRandom]");
               System.exit(1);
          }
          String group = argv[0];
          int port = (new Integer(argv[1])).intValue();
          String piece = argv[2];
          int seed;
          if (argv.length >= 4)
               seed = (new Integer(argv[3])).intValue();
          else seed = 0;

          Air air = new Air(group, port, piece, seed);
          air.start();
          air.variations();
     }

     /**
      * Thread qui attend les paquets sur la socket
      */
     protected class AttentePaquet extends Thread {
          /**
           * Derniere demande de chauffage a prendre en compte. Une
           * valeur de -1 signifie qu'aucune demande n'a eu lieu depuis
           * la derniere lecture de la valeur.
           */
          protected int dernier = -1;

          /**
           * La socket multicast sur laquelle on attend les messages.
           */
          protected MulticastSocket socket;

          /**
           * Retourne la derniere demande de chauffage et la remet a -1.
           */
          public synchronized int getDernier() {
               int temp = dernier;
               dernier = -1;
               return temp;
          }

          protected synchronized void setDernier(int val) {
               dernier = val;
          }

          /**
           * Attend en permanence des paquets sur la socket. S'il s'agit
           * d'une demande de chauffage, modifie la valeur de l'attribut
           * dernier. En cas d'erreur, se termine (plus aucune lecture
           * n'est alors faite sur la socket).
           */
          public void run() {
               try {
                    byte tab[] = new byte[100];
                    DatagramPacket dp = new DatagramPacket(tab, tab.length);
                    MessageTemperature msg;

                    while (true) {
                         socket.receive(dp);
                         msg = MessageTemperature.fromBytes(dp.getData(),dp.getLength());
                         if (msg.getType() == MessageTemperature.CHAUFFER) {
                              if (msg.getValeur() >= 5)
                                   setDernier(5);
                              else setDernier(msg.getValeur());
                         }
                    }
               } catch(Exception e) {
                    System.err.println("[Erreur] Lecture socket : "+e);
               }
          }

          public AttentePaquet(MulticastSocket socket) {
               this.socket = socket;
          }
     }
}
