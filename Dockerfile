# -------- Stage 1 : Build native image --------
FROM ghcr.io/graalvm/native-image-community:21 AS builder

WORKDIR /app

RUN microdnf install -y findutils

# Copier les fichiers Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Donner les droits d'exécution au wrapper
RUN chmod +x gradlew

# Télécharger les dépendances (cache Docker)
RUN ./gradlew dependencies --no-daemon

# Copier le code source
COPY src src

# Build de l'image native
RUN ./gradlew processAot nativeCompile --no-daemon

# -------- Stage 2 : Runtime léger --------
FROM debian:bookworm-slim

WORKDIR /app

RUN apt-get update \
 && apt-get install -y zlib1g \
 && rm -rf /var/lib/apt/lists/*

# Copier le binaire natif généré
COPY --from=builder /app/build/native/nativeCompile/* .

# Exposer le port Spring Boot
EXPOSE 8086

# Lancer l'application
ENTRYPOINT ["./ms.cms", "--spring.aot.enabled=false"]