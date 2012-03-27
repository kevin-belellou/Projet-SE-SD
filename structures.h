#ifndef STRUCTURES_H
#define STRUCTURES_H

typedef struct Piece {
     char nom[25];
     int temperature;
     int temperatureVoulu;
     int nivChauffage;
} Piece;

typedef struct ParamModuleCom {
     int port;
     Piece* tabValeurs;
} ParamModuleCom;

#endif
