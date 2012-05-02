#ifndef MODULECOMM_H
#define MODULECOMM_H

#include <stdio.h>
#include <pthread.h>
#include <signal.h>
#include "sockets.h"
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

     // longueur adresse
     int lg_addr;

     // socket d'ecoute et de service
     int socket_ecoute, socket_service;

     // Creation de la socket et bind via functions.h
     socket_ecoute = creerSocketTCP(port);
     if (socket_ecoute == -1) {
          perror("erreur creation socket_ecoute");
          pthread_exit(NULL);
     }

     if (listen(socket_ecoute, 10) == -1) {
          perror("erreur listen");
          pthread_exit(NULL);
     }

     // On attend la connexion du client
     lg_addr = sizeof(struct sockaddr_in);

     // Declaration d'un thread
     pthread_t thread;
     while(1) {
          // Lock du mutex_socket pour eviter socket double
          pthread_mutex_lock(&mutex_socket);

          // Recuperation de la socket
          socket_service = accept(socket_ecoute, (struct sockaddr*)&addr_client, (socklen_t*)&lg_addr);

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
     printf("%d : socket = %d\n", (int)pthread_self(), socket);

     char message[TAILLEBUFF];
     int nb_octets;
     char* piece;
     unsigned char type;

     // Lecture de la socket
     nb_octets = read(socket, message, TAILLEBUFF);

     // Recuperation du nom de la piece
     piece = (char*)malloc((nb_octets - 5 + 1) * sizeof(char));
     memcpy(piece, message + 5, nb_octets - 5);
     piece[nb_octets - 5] = '\0';

     // Recuperation du type de message
     memcpy(&type, message + 4, 1);

     // Recuperation de la place de la piece dans le tableau des pieces
     // La piece est ajoutee au tableau si elle n'y etait deja pas
     pthread_mutex_lock(&mutex_memoire); // Lock du mutex_memoire

     int place = -1;
     int trouve = existeDansTabPieces(piece);
     if (trouve >= 0) // Si la piece existe deja dans le tableau
          place = trouve;
     else // Sinon
          place = agrandirTabPieces(piece);

     if (place == -1) {
          fprintf(stderr, "%d : Realloc tableau echouee\n", (int)pthread_self());
          pthread_exit(NULL);
     }
     // Unlock du mutex_memoire
     pthread_mutex_unlock(&mutex_memoire);

     // Unlock du mutex_socket
     pthread_mutex_unlock(&mutex_socket);

     switch ((int)type) {
     case 0: // Si c'est un message de type MESURE
          printf("%d = thermometre\n", (int)pthread_self());
          communication_thermometre(socket, place);
          break;
     case 1: // Si c'est un message de type CHAUFFER
          printf("%d = chauffage\n", (int)pthread_self());
          communication_chauffage(socket, place);
          break;
     default:
          fprintf(stderr, "%d : Type de message non connu\n", (int)pthread_self());
          pthread_exit(NULL);
     }

     // Traitement effectue, fermeture de la socket
     close(socket);

     // Liberation de la memoire et destruction du thread
     free(piece);
     printf("%d : je me suicide\n", (int)pthread_self());
     pthread_exit(NULL);
}

/**
 * Communication avec les Thermometres
 * Recupere la valeur courante de la temperature dans la piece
 * et la place dans le tableau des valeurs correspondant
 */
void communication_thermometre(int socket, int place)
{
     // Buffer qui contiendra le message reçu
     char message[TAILLEBUFF];

     // Donnees recues
     int nb_octets, temperature;

     nb_octets = read(socket, message, TAILLEBUFF);
     while (nb_octets > 0) {
          memcpy(&temperature, message, 4);

          pthread_mutex_lock(&mutex_memoire);

          // Enregistrement de la temperature dans la memoire
          tabPieces.tabValeurs[place].temperature = temperature;
          printf("piece : %s ; temperature : %d\n", tabPieces.tabValeurs[place].nom, tabPieces.tabValeurs[place].temperature);

          pthread_mutex_unlock(&mutex_memoire);

          nb_octets = read(socket, message, TAILLEBUFF);
     }
     printf("%d (thermometre) : j'exit\n", (int)pthread_self());
}

/**
 * Communication avec les Chauffages
 * Recupere le niveau de chauffage calcule par l'ordonnanceur
 * et l'envoie au Chauffage
 */
void communication_chauffage(int socket, int place)
{
     // Niveau de chauffage
     int valeur = 0;

     // Nombre d'octets envoyes
     int nb_octets = write(socket, &valeur, sizeof(int));

     // Handler pour le signal SIGPIPE
     // Previent un crash du systeme en cas de broken pipe
     signal(SIGPIPE, SIG_IGN);

     while (nb_octets > 0) {
          // Attente
          sleep(1);

          pthread_mutex_lock(&mutex_memoire);

          // Recuperation de la valeur depuis la memoire
          valeur = tabPieces.tabValeurs[place].nivChauffage;
          printf("piece : %s ; chauffage : %d\n", tabPieces.tabValeurs[place].nom, valeur);

          pthread_mutex_unlock(&mutex_memoire);

          // Envoi du niveau de chauffage a Air
          nb_octets = write(socket, &valeur, sizeof(int));
     }
     printf("%d (chauffage) : j'exit\n", (int)pthread_self());
}

#endif
