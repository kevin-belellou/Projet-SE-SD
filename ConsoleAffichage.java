import java.rmi.*;
import java.rmi.registry.*;

import java.lang.Thread.*;

public class ConsoleAffichage {
     public static void main(String argv[])
     {
          try {
               System.setSecurityManager(new RMISecurityManager());

               ConsoleInterface c = (ConsoleInterface)Naming.lookup("Console");

               while (true) {
                    Thread.sleep(3000);
                    
				//Renvoie un tableau de:
				/*
				* nom;
				* temp;
				* tempVoulue
				* nivChauffage
				* nivChauffageVoulu
				*/
                    String[] resultat = c.getInfos();

                    for(int k = 0; k < resultat.length; k+=5) {
                         System.out.println(" Piece                  = " + resultat[k]);
                         System.out.println(" \t> Temperature        = " + resultat[k+1]);
                         System.out.println(" \t> Temp. Voulue       = " + resultat[k+2]);
                         System.out.println(" \t> Niv. Chauffage     = " + resultat[k+3]);
                         System.out.println(" \t> Niv. Chauff Voulu  = " + resultat[k+4]);
                         System.out.println("\n");
				}

                    System.out.println("\n----------------------------------------------\n");
               }
          } catch(Exception e) {
               e.printStackTrace();
          }
     }
}
