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
                    System.out.println("Nom = " + c.getNom(i));
                    System.out.println("Demande " + i + " envoyee");
                    System.out.println("Temp = " + c.getTemperature(i));
               }
          } catch(Exception e) {
               e.printStackTrace();
          }
     }
}
