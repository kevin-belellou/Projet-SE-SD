#ifndef GESTIONCONSOLE_H
#define GESTIONCONSOLE_H

#include "sockets.h"

#define TAILLEBUF 20

void* init_gestionConsole(void* param)
{
     // adresse de la socket cote serveur
     static struct sockaddr_in addr_client;

     // identifiant du client
     struct hostent *host_client;

     // taille de l'addresse socket
     socklen_t lg;

     // descripteur de la socket locale
     int sock;

     // Creation de la socket et bind via functions.h
     sock = creerSocketUDP(13000);
     if (sock == -1) {
          printf("Erreur socket\n");
          exit(-1);
     }

     // chaine a envoyer en reponse
     char *reponse = "bien recu";

     // buffer de reception
     char buffer[TAILLEBUF];

     // chaine recue
     char *chaine;

     // nombre d'octets lus ou envoyes
     int nb_octets;

     while (1) {
          // attente de donnees venant d'un client
          lg = sizeof(struct sockaddr_in);
          nb_octets = recvfrom(sock, buffer, TAILLEBUF, 0, (struct sockaddr *)&addr_client, &lg);
          if (nb_octets == -1) {
               perror("erreur reception paquet");
               exit(1);
          }

          // recupere nom de la machine emettrice des donnees
          host_client = gethostbyaddr(&(addr_client.sin_addr), sizeof(long), AF_INET);
          if (host_client == NULL) {
               perror("erreur gethostbyaddr");
               exit(1);
          }

          // affichage message recu et coordonnees emetteur
          chaine = (char *)malloc((nb_octets + 1) * sizeof(char));
          memcpy(chaine, buffer, nb_octets);
          chaine[nb_octets] = '\0';
          printf("recu message %s de la part de %s depuis le port %d\n", chaine, host_client->h_name, ntohs(addr_client.sin_port));

          free(chaine);

//          // envoi de la reponse a l'emetteur
//          nb_octets = sendto(sock, reponse, strlen(reponse)+1, 0, (struct sockaddr*)&addr_client, lg);
//          if (nb_octets == -1) {
//               perror("erreur envoi reponse");
//               exit(1);
//          }
     }
     // fermeture la socket
     close(sock);

     return 0;
}

#endif
