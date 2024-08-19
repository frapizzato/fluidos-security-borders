# Usa un'immagine base di OpenJDK
FROM openjdk:17-jdk-alpine

# Imposta la directory di lavoro
WORKDIR /app

# Copia il file JAR dalla directory target nella directory di lavoro del container
COPY target/harmonization.jar /app/harmonization.jar
COPY xsd /app/xsd
COPY testfile /app/testfile

# Espone la porta 8080 per l'applicazione
EXPOSE 8080

# Comando per eseguire il JAR
ENTRYPOINT ["java", "-jar", "/app/harmonization.jar"]
