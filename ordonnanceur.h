#ifndef ORDONNANCEUR_H
#define ORDONNANCEUR_H

#include "memoire.h"

/**
 * Fonction principale de l'ordonnanceur
 * Calcule les valeurs de chauffage pour chaque piece
 */
void* init_ordonnanceur(void* temps_param)
{
     // Copie du port
     // (Dereferencement du cast du pointeur void* vers int*)
     int temps = *((int*)temps_param);
     // Indice de boucle
     int i;

     while(1) {
          // Attente
          sleep(temps);

          // Recuperation securisee du nombre de pieces
          pthread_mutex_lock(&mutex_memoire);
          int nombrePieces = tabPieces.nbPieces;
          printf("nbpieces = %d\n", nombrePieces);
          pthread_mutex_unlock(&mutex_memoire);

          // Numero de la liste a considerer, et de la piece a chauffer
//        int majFile = -1;
//        int majPiece = -1;

          // Creation des files
//        int indexFile[3] = {0};
//          int* file[3];
//          for (i = 0; i < 3; ++i)
//               file[i] = malloc(nombrePieces * sizeof(int));

          // Verifications a faire :
          /*
          * Temps de derniere maj
          * Niveau de chauffage voulu

          * T = (T° Voulue - T° Effective)
          * File 1: T > 3
          * File 2: 1 < T <= 3
          * File 3: T <= 1
          * File 3: T° Voulue < 0

          */
          // Recupere de maniere securise le nombre de piece
//        for(i = 0; i < nombrePieces; ++i) {
//             // Recuperation des temperatures, et mise dans la liste
//             pthread_mutex_lock(&mutex_memoire);
//             int dT = tabPieces.tabValeurs[i].temperature - tabPieces.tabValeurs[i].temperatureVoulue;
//             // Mise dans la bonne file de l'indice de la piece
//             int numFile = (dT > 3 ? 1 : 1 < dT && dT <= 3 ? 2 : 1);
//             file[numFile][indexFile[numFile]] = i;
//             pthread_mutex_unlock(&mutex_memoire);
//        }

          // Calcul de la piece a mettre a jour

          // Mise a jour de la piece
//        if (majPiece >= 0) {
//             pthread_mutex_lock(&mutex_memoire);
//             pthread_mutex_unlock(&mutex_memoire);
//        }

          // Liberation des files
//          for(i = 0; i < 3; ++i)
//               free(file[i]);
          //free(file);
     }
}

#endif
