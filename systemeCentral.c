#include <pthread.h>
#include "moduleComm.h"

int main(int argc, char *argv[])
{
     if(argc != 2) {
          printf("Wrong number of argument\n");
          return -1;
     }

     int port = atoi(argv[1]);

     //Déclaration d'un thread
	pthread_t thread;

     pthread_create(&thread, NULL, init_moduleComm, (void*)&port);

     pthread_join(thread, NULL);

     return 0;
}
