import java.rmi.*;
import java.rmi.registry.*;

import java.lang.Thread.*;

public class ConsoleAffichage {
     public static void main(String argv[])
     {
          try {
               System.setSecurityManager(new RMISecurityManager());

               ConsoleInterface c = (ConsoleInterface)Naming.lookup("Console");

               int i = 0;

               while (true) {
                    Thread.sleep(1000);
                    i++;
                    
                    String[] resultat = c.getNoms();

                    for(int k = 0; k < resultat.length; k++)
                         System.out.println(" le tableau monTableau = " + resultat[k]);

                    System.out.println("Demande " + i + " envoyee");
               }
          } catch(Exception e) {
               e.printStackTrace();
          }
     }
}
