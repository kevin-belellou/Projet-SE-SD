#ifndef STRUCTURES_H
#define STRUCTURES_H

typedef struct Piece {
     char nom[25];
     int temperature;
     int temperatureVoulue;
     int nivChauffage;
} Piece;

static Piece* tabValeurs = NULL;

#endif
