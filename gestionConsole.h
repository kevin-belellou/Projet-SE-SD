#ifndef GESTIONCONSOLE_H
#define GESTIONCONSOLE_H

#include "sockets.h"
#include "memoire.h"

#define TAILLEBUF 200

void* init_gestionConsole(void* param)
{
     // adresse de la socket cote serveur
     static struct sockaddr_in addr_client;

     // identifiant du client
     struct hostent* host_client;

     // taille de l'addresse socket
     socklen_t lg;

     // descripteur de la socket locale
     int sock;

     // Creation de la socket et bind via functions.h
     sock = creerSocketUDP(13000);
     if (sock == -1) {
          printf("Erreur socket\n");
          pthread_exit(NULL);
     }

     // chaine a envoyer en reponse
     char reponse[TAILLEBUF];

     // buffer de reception
     char buffer[TAILLEBUF];

     // nombre d'octets lus ou envoyes
     int nb_octets;

     while (1) {
          // attente de donnees venant d'un client
          lg = sizeof(struct sockaddr_in);
          nb_octets = recvfrom(sock, buffer, TAILLEBUF, 0, (struct sockaddr*)&addr_client, &lg);
          if (nb_octets == -1) {
               perror("erreur reception paquet");
               pthread_exit(NULL);
          }

          // recupere nom de la machine emettrice des donnees
          host_client = gethostbyaddr(&(addr_client.sin_addr), sizeof(long), AF_INET);
          if (host_client == NULL) {
               perror("erreur gethostbyaddr");
               pthread_exit(NULL);
          }

          int i, tailleNomPiece, taille = 0;
          char nomPiece[25];

          // Determination type de message
          unsigned char type;
          memcpy(&type, buffer, 1);
          printf("type = %d ; nb_octets = %d\n", (int)type, nb_octets);

          switch ((int)type) {
          case 0: // Si c'est GET_INFO
               pthread_mutex_lock(&mutex_memoire);

               memcpy(reponse, &tabPieces.nbPieces, sizeof(int));
               taille = 4;

               for (i = 0; i < tabPieces.nbPieces; i++) {
                    strcpy(nomPiece, tabPieces.tabValeurs[i].nom);
                    tailleNomPiece = strlen(nomPiece);

                    memcpy(reponse + taille, &tailleNomPiece, sizeof(int));
                    memcpy(reponse + taille + 4, nomPiece, tailleNomPiece);
                    memcpy(reponse + taille + tailleNomPiece + 4, &tabPieces.tabValeurs[i].temperature, sizeof(int));
                    memcpy(reponse + taille + tailleNomPiece + 8, &tabPieces.tabValeurs[i].temperatureVoulue, sizeof(int));
                    memcpy(reponse + taille + tailleNomPiece + 12, &tabPieces.tabValeurs[i].nivChauffage, sizeof(int));
                    memcpy(reponse + taille + tailleNomPiece + 16, &tabPieces.tabValeurs[i].nivChauffageVoulu, sizeof(int));

                    taille += tailleNomPiece + 5 * 4;
               }
               printf("taille = %d\n", taille);
               pthread_mutex_unlock(&mutex_memoire);
               break;
          }

//          // affichage message recu et coordonnees emetteur
//          chaine = (char*)malloc((nb_octets + 1) * sizeof(char));
//          memcpy(chaine, buffer, nb_octets);
//          chaine[nb_octets] = '\0';
//          printf("recu message %s de la part de %s depuis le port %d\n", chaine, host_client->h_name, ntohs(addr_client.sin_port));

//          free(chaine);

//pthread_mutex_lock(&mutex_memoire);
//reponse = tabPieces.tabValeurs[0].nom;
//pthread_mutex_unlock(&mutex_memoire);

          // envoi de la reponse a l'emetteur
          nb_octets = sendto(sock, reponse, taille, 0, (struct sockaddr*)&addr_client, lg);
          if (nb_octets == -1) {
               perror("erreur envoi reponse");
               pthread_exit(NULL);
          }
     }
     // fermeture la socket
     close(sock);

     return 0;
}

#endif
