#ifndef STRUCTURES_H
#define STRUCTURES_H

#include <stdlib.h>
#include <string.h>
#include <pthread.h>

// Definit la structure d'une piece
typedef struct Piece {
     char nom[25];
     int temperature;
     int temperatureVoulue;
     int nivChauffage;
} Piece;

// Definit la structure du tableau de pieces
typedef struct TabPieces {
     int nbPieces;
     Piece* tabValeurs;
} TabPieces;

// Declaration et initialisation du tableau de pieces 
// utilise par tous les threads
static TabPieces tabPieces = {0, NULL};

// Declaration et initialisation du mutex
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;

// Fonction pour rechercher l'existence d'une piece
// dans le tableau des pieces
// Renvoie -1 si la piece n'est pas trouvee,
// sinon renvoie la place à laquelle la piece se trouve
int existeDansTabPieces(char *piece)
{
     int i, trouve = 0;

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

// Fonction pour aggrandir le tableau de pieces
// en passant en parametre le nom de la nouvelle piece
// Renvoie -1 en cas d'erreur lors de la recallocation,
// sinon renvoie la place à laquelle la nouvelle piece se trouve
int aggrandirTabPieces(char *nom)
{
     // Reallocation du tableau
     Piece *temp = NULL;
     temp = realloc(tabPieces.tabValeurs, (tabPieces.nbPieces + 1) * sizeof(Piece));

     // Verification que la reallocation a marché
     if (temp != NULL)
          tabPieces.tabValeurs = temp;
     else {
          perror("Erreur realloc");
          return -1;
     }

     // Initialisation des valeurs de la piece
     strncpy(tabPieces.tabValeurs[tabPieces.nbPieces].nom, nom, 25);
     tabPieces.tabValeurs[tabPieces.nbPieces].temperature = 0;
     tabPieces.tabValeurs[tabPieces.nbPieces].temperatureVoulue = 0;
     tabPieces.tabValeurs[tabPieces.nbPieces].nivChauffage = 0;

     // Incrementation du nombre de piece
     tabPieces.nbPieces++;

     // Retourne la position de la nouvelle piece dans le tableau
     return (tabPieces.nbPieces - 1);
}

#endif
