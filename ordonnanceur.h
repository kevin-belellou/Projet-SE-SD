#ifndef ORDONNANCEUR_H
#define ORDONNANCEUR_H

#include "memoire.h"

int determinerFile(int piece);
void calculerChauffage(int piece);

/**
 * Fonction principale de l'ordonnanceur
 * Calcule les valeurs de chauffage pour chaque piece
 */
void* init_ordonnanceur(void* temps_param)
{
     // Copie du parametre
     // (Dereferencement du cast du pointeur void* vers int*)
     int temps = *((int*)temps_param);

     // Indices de boucle
     int i, j, k;

     // Quantum
     int quantum = 0;
     int quantumMax = 3;

     // Nombre de pieces actuellement gerees
     int nbPiecesGerees = 0;

     // Nombre de reallocations reussies
     int reallocOK;

     // Files
     int* file[3];

     // Initialisation des files (necessaire pour les realloc)
     for (i = 0; i < 3; i++)
          file[i] = malloc(sizeof(int));

     // Taille reelle des files
     int tailleFile[3] = {0};

     // Variables diverses
     int nouvelleFile, pieceTraitee;

     while(1) {
          // Attente
          sleep(temps);

          // Lock du mutex_memoire
          pthread_mutex_lock(&mutex_memoire);

          // Verifie si les files ont besoin d'etre reallouees
          if (tabPieces.nbPieces != 0 && tabPieces.nbPieces != nbPiecesGerees) {
               reallocOK = 0;

               // Pour chaque file
               for (i = 0; i < 3; i++) {
                    // Reallocation
                    int* temp;
                    temp = realloc(file[i], tabPieces.nbPieces * sizeof(int));

                    // Verification que la reallocation a marche
                    if (temp != NULL) {
                         file[i] = temp;
                         reallocOK++;
                    }
               }

               // Si toutes les reallocations ont marche
               if (reallocOK == 3) {
                    // Calcul de la place des nouvelles pieces
                    for (j = nbPiecesGerees; j < tabPieces.nbPieces; j++) {
                         // Determination de la file dans laquelle la piece doit se trouver
                         nouvelleFile = determinerFile(j);

                         // Ajout dans la file
                         file[nouvelleFile][tailleFile[nouvelleFile]] = j;

                         // Incrementation de la taille reelle de la file
                         tailleFile[nouvelleFile]++;
                    }

                    // Mise a jour du nombre de pieces gerees
                    nbPiecesGerees = tabPieces.nbPieces;
               }
          }

          // Unlock du mutex_memoire
          pthread_mutex_unlock(&mutex_memoire);

          // Gestion du quantum
          quantum++;

          if (quantum == quantumMax) {
               printf("quantum atteint\n");
               // Lock du mutex_memoire
               pthread_mutex_lock(&mutex_memoire);

               // Remise a zero des tailles des files
               for (i = 0; i < 3; i++)
                    tailleFile[i] = 0;

               // Veillissement des priorites
               for (j = 0; j < tabPieces.nbPieces; j++) {
                    // Creation d'un pointeur sur la piece
                    Piece* pPiece = &tabPieces.tabValeurs[j];

                    // Division par 2 de la priorite
                    pPiece->priorite /= 2;

                    // Ajout dans la nouvelle file correspondante
                    file[pPiece->priorite][tailleFile[pPiece->priorite]] = j;

                    // Incrementation de la taille reelle de la file
                    tailleFile[pPiece->priorite]++;
               }

               // Unlock du mutex_memoire
               pthread_mutex_unlock(&mutex_memoire);

               // Remise a zero du quantum
               quantum = 0;
          }

          for (i = 0; i < 3; i++) {
               if (tailleFile[i] == 0) {
                    printf("file[%d] vide\n", i);
                    continue;
               }

               for (k = 0; k < tailleFile[i]; k++) {
                    printf("file[%d][%d] = %d ; ", i, k, file[i][k]);
               }
               printf("\n");
          }

          if (nbPiecesGerees > 0)
               for (i = 0; i < 3; i++)
                    if (tailleFile[i] > 0) {
                         // Recuperation de la piece
                         pieceTraitee = file[i][0];

                         // Lock du mutex_memoire
                         pthread_mutex_lock(&mutex_memoire);

                         // Calcul du chauffage
                         calculerChauffage(pieceTraitee);

                         // Determination de la nouvelle file dans laquelle la piece va aller
                         nouvelleFile = determinerFile(pieceTraitee);

                         // Unlock du mutex_memoire
                         pthread_mutex_unlock(&mutex_memoire);

                         // Enlevement de la piece dans l'ancienne file (reecriture)
                         for (k = 1; k < tailleFile[i]; k++)
                              file[i][k -1] = file[i][k];

                         // Decrementation de la taille reelle de l'ancienne file
                         tailleFile[i]--;

                         // Ajout de la piece dans la nouvelle file
                         file[nouvelleFile][tailleFile[nouvelleFile]] = pieceTraitee;

                         // Incrementation de la taille reelle de la nouvelle file
                         tailleFile[nouvelleFile]++;

                         // Sortie du for
                         break;
                    }
     }
}

/**
 * Determine dans quelle file de priorite une piece doit etre placee
 */
int determinerFile(int piece)
{
     // Creation d'un pointeur sur la piece
     Piece* pPiece = &tabPieces.tabValeurs[piece];

     if (pPiece->nivChauffageVoulu >= 0) // Si l'utilisateur a ordonne un niveau de chauffage
          pPiece->priorite = 0;
     else if (pPiece->temperatureVoulue < 0) // Si l'utilisateur n'a pas ordonne de temperature voulue
          pPiece->priorite = 2;
     else {
          // Calcul de la valeur absolue de la difference entre
          // la temperature courante et celle voulue par l'utilisateur
          int diffTemp = abs(pPiece->temperatureVoulue - pPiece->temperature);

          if (diffTemp > 3)
               pPiece->priorite = 0;
          else if (diffTemp > 1 && diffTemp <= 3)
               pPiece->priorite = 1;
          else
               pPiece->priorite = 2;
     }
     return pPiece->priorite;
}

/**
 * Determine le niveau de chauffage a appliquer pour une piece
 */
void calculerChauffage(int piece)
{
     // Creation d'un pointeur sur la piece
     Piece* pPiece = &tabPieces.tabValeurs[piece];

     // Calcul de la difference entre la temperature courante
     // et celle voulue par l'utilisateur
     int diffTemp = pPiece->temperatureVoulue - pPiece->temperature;

     if (diffTemp <= 0) // Si l'on a atteint ou depasse la temperature voulue
          pPiece->nivChauffage = 0;
     else if (diffTemp > 0 && diffTemp <= 5) // Si la difference de temperature est inferieure a 5 degres
          pPiece->nivChauffage = 3;
     else
          pPiece->nivChauffage = 5;
}

#endif
