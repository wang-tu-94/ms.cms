# -------- Stage 1 : Build native image --------
FROM ghcr.io/graalvm/native-image-community:25 AS builder

WORKDIR /app

RUN microdnf install -y findutils

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

RUN chmod +x gradlew

RUN ./gradlew dependencies --no-daemon

COPY src src

RUN ./gradlew nativeCompile --no-daemon

# -------- Stage 2 : Runtime léger --------
FROM debian:bookworm-slim

WORKDIR /app

RUN apt-get update \
 && apt-get install -y zlib1g \
 && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/build/native/nativeCompile/ms.cms ./ms.cms
RUN chmod +x ./ms.cms

EXPOSE 8086

ENTRYPOINT ["./ms.cms"]