#ifndef MEMOIRE_H
#define MEMOIRE_H

#include <stdlib.h>
#include <string.h>
#include <pthread.h>

// Definit la structure d'une piece
typedef struct Piece {
     char nom[25]; // Nom de la piece
     int temperature; // Temperature courante de la piece
     int temperatureVoulue; // Temperature voulue par l'utilisateur, entree dans une console
     int nivChauffage; // Niveau de chauffage actuel de la piece
     int nivChauffageVoulu; // Niveau de chauffage voulu par l'utilisateur, entre dans une console
     int priorite; // Priorite de la piece
} Piece;

// Definit la structure du tableau de pieces
typedef struct TabPieces {
     int nbPieces; // Nombre de pieces dans la tableau
     Piece* tabValeurs; // Tableau de Pieces
} TabPieces;

// Declaration et initialisation du tableau de pieces,
// utilise par tous les threads
static TabPieces tabPieces = {0, NULL};

// Declaration et initialisation des mutex
pthread_mutex_t mutex_memoire = PTHREAD_MUTEX_INITIALIZER; // Mutex pour la memoire (tableau de pieces)
pthread_mutex_t mutex_socket = PTHREAD_MUTEX_INITIALIZER; // Mutex pour les sockets (eviter socket double)

/**
 * Fonction pour rechercher l'existence d'une piece
 * dans le tableau des pieces
 * Renvoie -1 si la piece n'est pas trouvee,
 * sinon renvoie la place a laquelle la piece se trouve
 */
int existeDansTabPieces(char *piece)
{
     int i, trouve = 0;

     // Parcours du tableau de pieces a la recherche de la piece consideree
     for (i = 0; i < tabPieces.nbPieces; i++)
          if (strcmp(tabPieces.tabValeurs[i].nom, piece) == 0) {
               trouve = 1;
               break;
          }

     if (trouve)
          return i;
     else
          return -1;
}

/**
 * Fonction pour agrandir le tableau de pieces
 * en passant en parametre le nom de la nouvelle piece
 * Renvoie -1 en cas d'erreur lors de la recallocation,
 * sinon renvoie la place a laquelle la nouvelle piece se trouve
 */
int agrandirTabPieces(char *nom)
{
     // Reallocation du tableau
     Piece* temp = NULL;
     temp = realloc(tabPieces.tabValeurs, (tabPieces.nbPieces + 1) * sizeof(Piece));

     // Verification que la reallocation a marche
     if (temp != NULL)
          tabPieces.tabValeurs = temp;
     else {
          perror("Erreur realloc");
          return -1;
     }

     // Initialisation des valeurs de la piece
     strncpy(tabPieces.tabValeurs[tabPieces.nbPieces].nom, nom, 25);
     tabPieces.tabValeurs[tabPieces.nbPieces].temperature = 0;
     tabPieces.tabValeurs[tabPieces.nbPieces].temperatureVoulue = -1;
     tabPieces.tabValeurs[tabPieces.nbPieces].nivChauffage = 0;
     tabPieces.tabValeurs[tabPieces.nbPieces].nivChauffageVoulu = -1;
     tabPieces.tabValeurs[tabPieces.nbPieces].priorite = 0;

     // Incrementation du nombre de piece
     tabPieces.nbPieces++;

     // Retourne la position de la nouvelle piece dans le tableau
     return (tabPieces.nbPieces - 1);
}

#endif
