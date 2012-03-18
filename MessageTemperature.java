/**
 * Message contenant des informations ou des demandes sur l'air d'une
 * piece. S'il est de type "mesure", il contient alors la valeur de la
 * temperature courante de l'air. S'il est de type "chauffer", il
 * contient une demande de chauffage a effectuer.
 */
public class MessageTemperature implements java.io.Serializable {

     /**
      * Constante precisant que le message est de type "mesure"
      * (contient la temperature courante de l'air)
      */
     public final static byte MESURE = 0;

     /**
      * Constante precisant que le message est de type "chauffer"
      * (contient une demnande de chauffage de l'air)
      */
     public final static byte CHAUFFER = 1;

     /**
      * Nom de la piece
      */
     protected String piece;

     /**
      * Pour un message de type "messure", contient la temperature
      * courante de l'air. Pour un message de type "chauffer", contient
      * la puissance du chauffage demandee (entre 0 et 5).
      */
     protected int valeur;

     /**
      * Type du message, precise par une des 2 constantes MESURE ou CHAUFFER
      */
     protected byte type;

     /**
      * Retourne la valeur stockee dans le message.
      */
     public int getValeur() {
          return valeur;
     }

     /**
      * Retourne le type du message (MESURE ou CHAUFFER)
      */
     public int getType() {
          return type;
     }

     /**
      * Retourne le nom de la piece
      */
     public String getPiece() {
          return piece;
     }

     /**
      * Convertit le message en son equivalent en tableau de byte.
      */
     public byte[] toBytes() {
          byte tab[] = new byte[piece.length()+5];

          int val = valeur;
          for (int i=0; i < 4; i++) {
               tab[i] = (byte) (val & 0x000000FF);
               val = val >>> 8;
          }

          tab[4] = type;

          byte tabPiece[] = piece.getBytes();
          for (int i=0; i < piece.length(); i++)
               tab[i+5] = tabPiece[i];

          return tab;
     }
     /**
      * Retourne un message a partir de son equivalent en tableau de byte.
      * @param tab le tableau de byte contenant le message
      * @param length le nombre de cases a considerer dans le tableau
      * @return une instance de message initialisee avec le contenu du
      * tableau.
      */
     public static MessageTemperature fromBytes(byte[] tab, int length) {
          int val[] = new int[4];

          for (int i=0; i < 4; i++) {
               if (tab[i] < 0)
                    val[i] = (tab[i] + 256) << (i *8);
               else
                    val[i] = tab[i] << (i *8);
          }
          int valeur = val[0] | val[1] | val[2] | val[3];

          String piece = new String(tab, 5, length - 5);

          return new MessageTemperature(valeur, tab[4], piece);
     }

     public String toString() {
          String msg = "type = ";
          if (type == MessageTemperature.MESURE) msg += "mesure ";
          else if (type == MessageTemperature.CHAUFFER) msg+="modifier ";
          else msg +="inconnu ";
          msg += ", valeur = "+valeur+", piece = "+piece;
          return msg;
     }

     /**
      * Cree un nouveau message.
      * @param valeur le niveau de temperature ou la puissance du chauffage
      * @param type le type du message (<code>MESURE</code> ou <code>CHAUFFER</code>)
      * @param piece le nom de le piece consideree
      */
     public MessageTemperature(int valeur, byte type, String piece) {
          this.valeur = valeur;
          this.type = type;
          this.piece = piece;
     }
}
