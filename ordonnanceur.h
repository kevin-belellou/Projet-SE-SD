#ifndef ORDONNANCEUR_H
#define ORDONNANCEUR_H

#include "memoire.h"

void* init_ordonnanceur(void* param)
{
	//Indice de boucle
	int i;
	while(true) {
		//ordonnanceur fainéant, attend 5s
		::sleep(5);
		
		//Recupération sécurisé du nombre de piece
		pthread_mutex_lock(&mutex);
		int nombrePieces = tabPieces.nbPieces;
		pthread_mutex_lock(&mutex);
		
		//Numéro de la liste à considérer, et de la pièce a chauffer
		int majFile = -1;
		int majPiece = -1;
		//Création des files
		int	indexFile[3] = {0};
		int* file[3];
		for(i = 0; i < 3; ++i)
			file[i] = malloc(sizeof(int) * nombrePieces);

		//Verification à faire:
		/*
		* Temps de dernière maj
		* Niveau de chauffage voulu

		* T = (T° Voulue - T° Effective)
		* File 1: T > 3
		* File 2: 1 < T <= 3
		* File 3: T <= 1
		* File 3: T° Voulue < 0 

		*/
		//Récupère de manière sécurisé le nombre de pièce
		for(i = 0; i < nombrePieces; ++i) {
			//Récuperation des temperatures, et mise dans la liste
			pthread_mutex_lock(&mutex);
			int dT = tabPieces.tabValeurs[i].temperature - tabPieces.tabValeurs[i].temperatureVoulue;
			//Mise dans la bonne file de l'indice de la pièce
			int numFile = (dT > 3 ? 1 : 1 < dT && dT <= 3 ? 2 : 1);
			file[numFile][indexFile[numFile]] = i;
			pthread_mutex_unlock(&mutex);		
		}
		
		//Calcul de la pièce à mettre à jour

		//Mise à jour de la pièce
		if(majPiece >= 0) {
			pthread_mutex_lock(&mutex);
			pthread_mutex_unlock(&mutex);
		}
		
		//Liberation des files
		for(i = 0; i < 3; ++i)
			free(file[i]);
		free(file);
	}
}

#endif
