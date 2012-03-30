#ifndef MODULECOMM_H
#define MODULECOMM_H

#include <pthread.h>
#include "fonctions.h"
#include "memoire.h"

#define TAILLEBUFF 100

void* traiter_communication(void* socket);
void communication_thermometre(int socket, int place);
void communication_chauffage(int socket, int place);

/**
 * Fonction principale du module de communication
 * Initie les communications avec les Thermometres et Chauffages
 */
void* init_moduleComm(void* port_param)
{
     // Copie du port
     // (Dereferencement du cast du pointeur void* vers int*)
     int port = *((int*)port_param);

     // adresse socket cote client
     static struct sockaddr_in addr_client;

     // adresse socket locale
     static struct sockaddr_in addr_serveur;

     // longueur adresse
     int lg_addr;

     // socket d'ecoute et de service
     int socket_ecoute, socket_service;

     // Creation de la socket et bind via functions.h
     socket_ecoute = creerSocketTCP(port);
     if (socket_ecoute == -1) {
          perror("erreur creation socket_ecoute");
          exit(-1);
     }

     if (listen(socket_ecoute, 10) == -1) {
          perror("erreur listen");
          exit(1);
     }

     // On attend la connexion du client
     lg_addr = sizeof(struct sockaddr_in);

     // Declaration d'un thread
     pthread_t thread;
     while(1) {
          // Recuperation de la socket
          socket_service = accept(socket_ecoute, (struct sockaddr *)&addr_client, &lg_addr);

          // Lancement du thread avec passage de la socket en parametre
          pthread_create(&thread, NULL, traiter_communication, (void*)&socket_service);
     }
}

/**
 * Determine le type de communication (Thermometre ou Chauffage)
 * et la piece a laquelle la communication se rapporte
 * Lance ensuite la fonction de communication correspondante
 */
void* traiter_communication(void* socket_param)
{
     // Copie de la socket
     // (Dereferencement du cast du pointeur void* vers int*)
     int socket = *((int*)socket_param);

     char message[TAILLEBUFF];
     int nb_octets;
     char *piece;
     int *type;

     // Lecture de la socket
     nb_octets = read(socket, message, TAILLEBUFF);

     // Recuperation du nom de la piece
     piece = (char *)malloc((nb_octets - 5 + 1) * sizeof(char));
     memcpy(piece, message + 5, nb_octets - 5);
     piece[nb_octets - 5] = '\0';

     // Recuperation du type de message
     type = (int *)malloc(sizeof(int));
     memcpy(type, message + 4, 1);

     // Lock du mutex
     pthread_mutex_lock(&mutex);

     // Recuperation de la place de la piece dans le tableau des pieces
     // La piece est ajoutee au tableau si elle n'y etait deja pas
     int place;
     int trouve = existeDansTabPieces(piece);
     if (trouve >= 0)
          place = trouve;
     else
          place = aggrandirTabPieces(piece);

     // Unlock du mutex
     pthread_mutex_unlock(&mutex);

     if (*type == 0) // Si c'est un message de type MESURE
          communication_thermometre(socket, place);
     else if (*type == 1) // Si c'est un message de type CHAUFFER
          communication_chauffage(socket, place);
     else {
          fprintf(stderr, "Type de message non connu\n");
          exit(-1);
     }

     // Traitement effectue, fermeture de la socket
     close(socket);

     // Liberation de la memoire et destruction du thread
     free(piece);
     free(type);
     pthread_exit(NULL);
}

/**
 * Communication avec les Thermometres
 * Recupere la valeur courante de la temperature dans la piece
 * et la place dans le tableau des valeurs correspondant
 */
void communication_thermometre(int socket, int place)
{
     // Recuperation du nom de la piece
     char *piece = tabPieces.tabValeurs[place].nom;

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

          tabPieces.tabValeurs[place].temperature = *temperature;

          nb_octets = read(socket, message, TAILLEBUFF);
     }
     printf("%d : j'exit\n", getpid());

     // Liberation donnee
     free(temperature);
}

/**
 * Communication avec les Chauffages
 * Recupere le niveau de chauffage calcule par l'ordonnanceur
 * et l'envoie au Chauffage
 */
void communication_chauffage(int socket, int place)
{
     int i, valeur;

     // Recuperation du nom de la piece
     char *piece = tabPieces.tabValeurs[place].nom;

     while (1) {
          sleep(1);

          valeur = tabPieces.tabValeurs[place].nivChauffage;

          valeur = (rand() % 6);
          printf("%d ; piece : %s ; chauffage : %d\n", getpid(), piece, valeur);

          write(socket, &valeur, sizeof(int));
     }
     printf("%d : j'exit\n", getpid());
}

#endif
