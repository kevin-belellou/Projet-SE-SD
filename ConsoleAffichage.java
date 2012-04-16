import java.rmi.*;
import java.rmi.registry.*;

public class ConsoleAffichage {
     public static void main(String argv[])
     {
          try {
               System.setSecurityManager(new RMISecurityManager());

               ConsoleInterface c = (ConsoleInterface)Naming.lookup("Console");

               int i = 0;

               System.out.println("Nom = " + c.getNom(i));

               System.out.println("Temp = " + c.getTemperature(i));
          } catch(Exception e) {
               e.printStackTrace();
          }
     }
}
