#include "functions.h"
#include <signal.h>

#define TAILLEBUFF 100

void traiter_communication(int socketService);

int main(int argc, char* argv[]) {
     if(argc != 2) {
          printf("Wrong number of argument\n");
          return -1;
     }

     // adresse socket coté client
     static struct sockaddr_in addr_client;

     // adresse socket locale
     static struct sockaddr_in addr_serveur;

     // longueur adresse
     int lg_addr;

     // socket d'écoute et de service
     int socket_ecoute, socket_service;

     // Création de la socket et bind via functions.h
     socket_ecoute = creerSocketTCP(atoi(argv[1]));
     if (socket_ecoute == -1) {
          printf("fuck\n");
          exit(-1);
     }
     
     if (listen(socket_ecoute, 10) == -1)
     {
          perror("erreur listen");
          exit(1);
     }

     // on attend la connexion du client
     lg_addr = sizeof(struct sockaddr_in);

     signal(SIGCHLD, SIG_IGN);

     while(1) {
          socket_service = accept(socket_ecoute, (struct sockaddr *)&addr_client,&lg_addr);
          if (fork() == 0) {
               printf("Je fork, fils num %d cree\n", getpid());
               // on est dans le fils
               close(socket_ecoute);
               // fonction qui gère la communication avec le client
               traiter_communication(socket_service);
/*               close(socket_service);*/
               exit(0);
          }
          close(socket_service);
     }
}

void traiter_communication(int sock) {
     // buffer qui contiendra le message reçu
     char message[TAILLEBUFF];

     // chaîne reçue du client
     char *chaine_recue;
     int *temperature;
     int *type;
     
     int nb_octets;
     
     nb_octets = read(sock, message, TAILLEBUFF);
     while (nb_octets != 0 && nb_octets != 1) {
          printf("je suis %d\n", getpid());

/*          nb_octets = read(sock, message, TAILLEBUFF);*/
          
          chaine_recue = (char *)malloc((nb_octets - 5 + 1) * sizeof(char));
          memcpy(chaine_recue, message + 5, nb_octets - 5);
          chaine_recue[nb_octets - 5] = '\0';

          printf("chaine ok\n");
          temperature = (int *)malloc(sizeof(int));
          memcpy(temperature, message, 4);

          printf("piece : %s; temperature : %d\n", chaine_recue, *temperature);
          nb_octets = read(sock, message, TAILLEBUFF);
     }
     printf("%d : j'exit", getpid());
}
