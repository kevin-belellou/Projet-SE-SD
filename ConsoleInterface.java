import java.rmi.*;

/**
 * Interface de Console pour le RMI
 */
public interface ConsoleInterface extends Remote {
     /**
      * Renvoie un tableau de String qui contient toutes
      * les infos à afficher pour toutes les pieces.
      * Les infos sont rangees dans cet ordre :
      *   - Nom de la piece
      *   - Temperature actuelle
      *   - Temperature voulue
      *   - Niveau de chauffage actuel
      *   - Niveau de chauffage voulu
      */
     public String[] getInfos() throws RemoteException;

     /**
      * Renvoie un tableau de String qui contient le nom de chaque piece.
      */
     public String[] getNoms() throws RemoteException;

     /**
      * Permet de modifier la temperature voulue d'une piece.
      * Les parametres sont :
      *   - temp : temperature voulue
      *   - piece : piece concernee
      * Renvoie VRAI si tout s'est bien passe, FAUX sinon.
      */
     public boolean setTemperatureVoulue(int temp, String piece) throws RemoteException;

     /**
      * Permet de modifier le niveau de chauffage voulu d'une piece.
      * Les parametres sont :
      *   - niv : niveau de chauffage voulu
      *   - piece : piece concernee
      * Renvoie VRAI si tout s'est bien passe, FAUX sinon.
      */
     public boolean setNivChauffageVoulu(int niv, String piece) throws RemoteException;
}
