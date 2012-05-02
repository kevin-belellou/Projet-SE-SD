import java.rmi.*;
import java.rmi.registry.*;

public class ConsoleControle {
     public static void main(String argv[])
     {
          try {
               System.setSecurityManager(new RMISecurityManager());

               ConsoleInterface c = (ConsoleInterface)Naming.lookup("Console");

               System.out.println("OK");
          } catch(Exception e) {
               e.printStackTrace();
          }
     }
}
