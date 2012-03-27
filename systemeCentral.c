#include <pthread.h>
#include "moduleComm.h"

typedef struct Piece Piece;
struct Piece {
     char nom[25];
     int temperature;
     int nivChauffage;
};
Piece *tabValeurs = NULL;

int main(int argc, char *argv[])
{
     if(argc != 2) {
          printf("Erreur dans les arguments !\nParamètre attendu : portEcoute\n");
          return -1;
     }

     int port = atoi(argv[1]);
     printf("sizeof(Piece) = %d\n", sizeof(Piece));

     Piece *temp = NULL;
     temp = realloc(tabValeurs, sizeof(Piece));

     if (temp != NULL)
          tabValeurs = temp;
     else {
          printf("Erreur realloc\n");
          return -1;
     }

     tabValeurs[0].temperature = 5;
     tabValeurs[0].nom = "polo";
     printf("temperature = %d\n", tabValeurs[0].temperature);

     return 0;

     // Déclaration du thread de communication
	pthread_t thread_comm;

     // Lancement du thread de communication
     pthread_create(&thread_comm, NULL, init_moduleComm, (void*)&port);

     // Attente de la fin du thread de communication
     pthread_join(thread_comm, NULL);

     return 0;
}
