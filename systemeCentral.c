#include <pthread.h>
#include "memoire.h"
#include "moduleComm.h"
#include "ordonnanceur.h"

int main(int argc, char* argv[])
{
     // Verification des arguments
     if(argc != 2) {
          printf("Erreur dans les arguments !\nParametre attendu : portEcoute\n");
          return -1;
     }

     // Port d'ecoute pour le module de communication
     int port = atoi(argv[1]);

     // Quantum de temps pour l'ordonnanceur
     int temps = 3;

     // Declaration des threads
     const int nbThread = 2;
     pthread_t thread[nbThread];

     // Lancement des threads
     pthread_create(&thread[0], NULL, init_moduleComm, (void*)&port);
     pthread_create(&thread[1], NULL, init_ordonnanceur, (void*)&temps);

     // Attente de fin des threads (ne devrait pas arriver en temps normal)
     int i;
     for(i = 0; i < nbThread; ++i)
          pthread_join(thread[i], NULL);

     return 0;
}
