#include "functions.h"

#define TAILLEBUFF 100

void* traiter_communication(void* socket);
void communication_thermometre(int socket, char *piece);
void communication_chauffage(int socket, char *piece);

void* init_moduleComm(void* port_param)
{
     // Copie du port
	// (Déréférencement du cast du pointer void* vers int*)
	int port = *((int*)port_param);

     // adresse socket coté client
     static struct sockaddr_in addr_client;

     // adresse socket locale
     static struct sockaddr_in addr_serveur;

     // longueur adresse
     int lg_addr;

     // socket d'écoute et de service
     int socket_ecoute, socket_service;

     // Création de la socket et bind via functions.h
     socket_ecoute = creerSocketTCP(port);
     if (socket_ecoute == -1) {
          printf("fuck\n");
          exit(-1);
     }

     if (listen(socket_ecoute, 10) == -1) {
          perror("erreur listen");
          exit(1);
     }

     // On attend la connexion du client
     lg_addr = sizeof(struct sockaddr_in);

	//Déclaration d'un thread
	pthread_t thread;
     while(1) {
		//Récupération de la socket
          socket_service = accept(socket_ecoute, (struct sockaddr *)&addr_client, &lg_addr);
		//Lancement du Thread avec passage de la socket en paramètre
		pthread_create(&thread, NULL, traiter_communication, (void*)&socket_service);
     }
}

void* traiter_communication(void* socket_param)
{
	// Copie de la socket
	// (Déréférencement du cast du pointer void* vers int*)
	int socket = *((int*)socket_param);

     char message[TAILLEBUFF];
     int nb_octets;
     char *piece;
     int *type;

     nb_octets = read(socket, message, TAILLEBUFF);

     piece = (char *)malloc((nb_octets - 5 + 1) * sizeof(char));
     memcpy(piece, message + 5, nb_octets - 5);
     piece[nb_octets - 5] = '\0';

     type = (int *)malloc(sizeof(int));
     memcpy(type, message + 4, 1);

     if (*type == 0) // Si c'est un message de type MESURE
          communication_thermometre(socket, piece);
     else if (*type == 1) // Si c'est un message de type CHAUFFER
          communication_chauffage(socket, piece);
     else {
          fprintf(stderr, "Type de message non connu\n");
          exit(-1);
     }

	// Traitement effectué, fermeture de la socket
     close(socket);
}

void communication_thermometre(int socket, char *piece)
{
     // Buffer qui contiendra le message reçu
     char message[TAILLEBUFF];

     // Donnees recues
     int nb_octets;
     int *temperature;
     temperature = (int *)malloc(sizeof(int));

     nb_octets = read(socket, message, TAILLEBUFF);
     while (nb_octets > 0) {
          memcpy(temperature, message, 4);

          printf("%d ; piece : %s ; temperature : %d\n", getpid(), piece, *temperature);
          nb_octets = read(socket, message, TAILLEBUFF);
     }
     printf("%d : j'exit\n", getpid());
}

void communication_chauffage(int socket, char *piece)
{
     int i, valeur;

     while (1) {
          sleep(1);

          valeur = (rand() % 6);
          printf("%d ; piece : %s ; chauffage : %d\n", getpid(), piece, valeur);

          write(socket, &valeur, sizeof(int));
     }
     printf("%d : j'exit\n", getpid());
}
