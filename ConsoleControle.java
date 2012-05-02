import java.rmi.*;
import java.rmi.registry.*;
import java.util.Scanner;

public class ConsoleControle {
     public static void main(String argv[])
     {
          // Verification des arguments
          if (argv.length != 1) {
               System.err.println("Erreur dans les arguments !");
               System.err.println("Usage : $ java -Djava.security.policy=java.policy ConsoleControle adrServeurRMI");
               System.exit(1);
          }

          try {
               System.setSecurityManager(new RMISecurityManager());

               ConsoleInterface c = (ConsoleInterface)Naming.lookup("//" + argv[0] + "/Console");
               Scanner sc = new Scanner(System.in);
               String choix = null;
               while(true) {
                    choix = null;
                    System.out.println("+=================================================+");
                    System.out.println("| Tapez \"reset\" pour remettre à jour les pièces |");
                    System.out.println("|     Sinon choississez la pièce par son nom      |");
                    System.out.println("+=================================================+");
                    System.out.println("|     		   Liste des pièces				|");
                    System.out.println("+- - - - - - - - - - - - - - - - - - - - - - - - -+");
                    String[] piece = c.getNoms();
                    for(int i = 0; i < piece.length; ++i) {
                         System.out.println("\t> "+piece[i]);
                    }
                    System.out.println("<- - - - - - - - - - - - - - - - - - - - - - - - ->");
                    choix = sc.nextLine();
                    boolean reset = (choix.compareToIgnoreCase("reset") == 0);
                    boolean pieceTrouve = false;
                    int i = 0;
                    while(i < piece.length && !pieceTrouve) {
                         //Tester si le nom de la pièce est correct
                         if(choix.compareToIgnoreCase(piece[i]) == 0)
                              pieceTrouve = true;
                         ++i;
                    }

                    if(reset) {
                         //Ne rien faire pour re-afficher la liste
                    } else if(pieceTrouve) {
                         //Choix contient la pièce valide
                         //Regler temperature ou niv chauffage
                         //sc.nextLine();
                         System.out.println("+==================================================+");
                         System.out.println("| Tapez \"1\" pour spécifier la temperature voulue |");
                         System.out.println("|  Tapez \"2\" pour spécifier le chauffage voulue  |");
                         System.out.println("|  		Puis entrez la valeur souhaitée		 |");
                         System.out.println("+==================================================+");
                         int choixEntier = sc.nextInt();
                         //sc.nextLine();
                         //Faire la bonne opération
                         if(choixEntier == 1) {
                              //Récupération de la valeur
                              int val = sc.nextInt();
                              //Test du bon déourlement de la méthode
                              if(c.setTemperatureVoulue(val, choix))
                                   System.out.println("\t\t> Opération effectuée ! < ");
                              else System.out.println("\t\t> Une erreur est survenue ! < ");
                              System.out.println("\t\t> Appuyez sur une touche < ");
                              sc.nextLine();
                         } else if(choixEntier== 2) {
                              //Récupération de la valeur
                              int val = sc.nextInt();
                              //Test du bon déourlement de la méthode
                              if(c.setNivChauffageVoulu(val, choix))
                                   System.out.println("\t\t> Opération effectuée ! < ");
                              else System.out.println("\t\t> Une erreur est survenue ! < ");
                              System.out.println("\t\t> Appuyez sur une touche < ");
                              sc.nextLine();
                         } else {
                              System.out.println("\t\t> Le numéro ne correspond pas ! < ");
                         }
                    } else {
                         System.out.println("\t\t> La pièce n'existe pas ! <");
                    }
               }

          } catch(Exception e) {
               e.printStackTrace();
          }
     }
}
