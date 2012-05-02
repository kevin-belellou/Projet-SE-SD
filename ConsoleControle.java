import java.util.Scanner; 
import java.rmi.*;
import java.rmi.registry.*;

public class ConsoleControle {
     public static void main(String argv[])
     {
          try {
               System.setSecurityManager(new RMISecurityManager());

               ConsoleInterface c = (ConsoleInterface)Naming.lookup("Console");
			Scanner sc = new Scanner(System.in);
			String choix = null;
			while(true) {
				sc.nextLine();
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
				boolean pieceValide = false;
				int i = 0;
				while(i < piece.length || pieceValide) {
					//Tester si le nom de la pièce est correct
					if(choix.compareToIgnoreCase(piece[i]) == 0)
						pieceValide = true;
					++i;
				}

				if(reset) {
					//Ne rien faire pour re-afficher la liste
				} else if(pieceValide) {
					//Choix contient la pièce valide
					//Regler temperature ou niv chauffage
					sc.nextLine();
					System.out.println("+==================================================+");
					System.out.println("| Tapez \"1\" pour spécifier la temperature voulue |");
					System.out.println("|  Tapez \"2\" pour spécifier le chauffage voulue  |");
					System.out.println("|  		Puis entrez la valeur souhaitée		 |");
					System.out.println("+==================================================+");
					int choixEntier = sc.nextInt();
					sc.nextLine();
					if(choixEntier == 1) {
						int val = sc.nextInt();
						c.setTemperatureVoulue(val, choix);
					} else if(choixEntier== 2) {
						int val = sc.nextInt();
						c.setNivChauffageVoulu(val, choix);
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
