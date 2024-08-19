#!/bin/bash

# Esegui la fase di build di Maven
mvn clean package

# Costruisci l'immagine Docker
docker build -t harmonization-app:latest .

# Esegui il container Docker
docker run -p 8080:8080 harmonization-app:latest
0 harmonization-app:latest