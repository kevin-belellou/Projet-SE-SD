#include <pthread.h>
#include "memoire.h"
#include "moduleComm.h"
#include "ordonnanceur.h"
#include "gestionConsole.h"

int main(int argc, char* argv[])
{
     // Verification des arguments
     if(argc != 3) {
          printf("Erreur dans les arguments !\nParametre attendu : portTCP portUDP\n");
          return -1;
     }

     // Port d'ecoute pour le module de communication
     int portTCP = atoi(argv[1]);

     // Port d'ecoute pour le module de communication
     int portUDP = atoi(argv[2]);

     // Quantum de temps pour l'ordonnanceur
     int temps = 3;

     // Declaration des threads
     const int nbThread = 3;
     pthread_t thread[nbThread];

     // Lancement des threads
     pthread_create(&thread[0], NULL, init_moduleComm, (void*)&portTCP);
     pthread_create(&thread[1], NULL, init_ordonnanceur, (void*)&temps);
     pthread_create(&thread[2], NULL, init_gestionConsole, (void*)&portUDP);

     // Attente de fin des threads (ne devrait pas arriver en temps normal)
     int i;
     for(i = 0; i < nbThread; ++i)
          pthread_join(thread[i], NULL);

     return 0;
}
