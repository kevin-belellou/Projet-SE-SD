#include "functions.h"
#include <signal.h>

#define TAILLEBUFF 100

void traiter_communication(int socket);
void communication_thermometre(int socket);
void communication_chauffage(int socket);

int main(int argc, char* argv[])
{
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

     if (listen(socket_ecoute, 10) == -1) {
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
               close(socket_service);
               exit(0);
          }
          close(socket_service);
     }
}

void traiter_communication(int socket)
{
     char message[TAILLEBUFF];
     int *type;

     read(socket, message, TAILLEBUFF);
     type = (int *)malloc(sizeof(int));
     memcpy(type, message + 4, 1);

     if (*type == 0) // Si c'est un message de type MESURE
          communication_thermometre(socket);
     else if (*type == 1) // Si c'est un message de type CHAUFFER
          communication_chauffage(socket);
     else {
          fprintf(stderr, "Type de message non connu\n");
          exit(-1);
     }
}

void communication_thermometre(int socket)
{
     // buffer qui contiendra le message reçu
     char message[TAILLEBUFF];

     // donnees reçues
     char *piece;
     int *temperature;

     int nb_octets;

     nb_octets = read(socket, message, TAILLEBUFF);
     while (nb_octets != 0 && nb_octets != 1) {
          printf("je suis %d\n", getpid());

          piece = (char *)malloc((nb_octets - 5 + 1) * sizeof(char));
          memcpy(piece, message + 5, nb_octets - 5);
          piece[nb_octets - 5] = '\0';

          temperature = (int *)malloc(sizeof(int));
          memcpy(temperature, message, 4);

          printf("piece : %s; temperature : %d\n", piece, *temperature);
          nb_octets = read(socket, message, TAILLEBUFF);
     }
     printf("%d : j'exit\n", getpid());
}

void communication_chauffage(int socket)
{
     exit(0);
}
