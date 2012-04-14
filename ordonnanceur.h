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
     // Copie du port
     // (Dereferencement du cast du pointeur void* vers int*)
     int temps = *((int*)temps_param);

     // Indices de boucle
     int i, j, k;

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

     int nouvelleFile, pieceTraitee;

     while(1) {
          // Attente
          sleep(temps);

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
          pthread_mutex_unlock(&mutex_memoire);

          if (nbPiecesGerees > 0)
               for (i = 0; i < 3; i++)
                    if (tailleFile[i] > 0) {
                         // Recuperation de la piece
                         pieceTraitee = file[i][0];

                         // Calcul du chauffage
                         calculerChauffage(pieceTraitee);

                         // Determination de la nouvelle file dans laquelle la piece va aller
                         nouvelleFile = determinerFile(pieceTraitee);

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
     /*
     * Temps de derniere maj
     * Niveau de chauffage voulu

     * T = |T° Voulue - T° Effective|
     * File 1: T > 3
     * File 2: 1 < T <= 3
     * File 3: T <= 1
     * File 3: T° Voulue < 0

     */
     return 0;
}

/**
 * Determine le niveau de chauffage a appliquer pour une piece
 */
void calculerChauffage(int piece)
{

}

#endif
