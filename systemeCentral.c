#include <pthread.h>
#include "moduleComm.h"
#include "ordonnanceur.h"

int main(int argc, char *argv[])
{
     if(argc != 2) {
          printf("Wrong number of argument\n");
          return -1;
     }

     int port = atoi(argv[1]);

     //Déclaration d'un thread
	const int nbThread = 2;
	pthread_t thread[nbThread];

     pthread_create(&thread[0], NULL, init_moduleComm, (void*)&port);
     pthread_create(&thread[1], NULL, init_ordonnanceur, NULL);

	int i;
	for(i = 0; i < nbThread; ++i)
	     pthread_join(thread[i], NULL);

     return 0;
}
