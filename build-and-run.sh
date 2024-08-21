#!/bin/bash
#docker rm harmonization-app:latest

# Esegui la fase di build di Maven
mvn clean package

# Costruisci l'immagine Docker
docker build -t harmonization-app:latest .

#docker compose up
# Esegui il container Docker
#docker run -p 8080:8080 harmonization-app:latest

docker tag harmonization-app:latest lucaruberto/harmonization-app:latest

docker compose up
#docker push lucaruberto/harmonization-app:latest




