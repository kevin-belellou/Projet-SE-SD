#ifndef STRUCTURES_H
#define STRUCTURES_H

#include <stdlib.h>
#include <string.h>

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

// Declaration du tableau de pieces utilise par tous les threads
static TabPieces tabPieces = {0, NULL};

// Fonction pour aggrandir le tableau de pieces
// en passant en parametre le nom de la nouvelle piece
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
