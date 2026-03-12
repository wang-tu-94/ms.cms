# 📝 MS CMS - Microservice Content Management

<div align="center">
  <img src="https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java_25-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 25" />
  <img src="https://img.shields.io/badge/GraalVM-004027?style=for-the-badge&logo=graalvm&logoColor=white" alt="GraalVM" />
  <img src="https://img.shields.io/badge/MongoDB-4EA94B?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB" />
  <img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens" alt="JWT" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</div>

<br />

Ce dépôt contient le code source du microservice de gestion de contenu (**ms.cms**) de l'écosystème. Il est conçu pour fournir des pages dynamiques à une application mobile ou web en utilisant une approche "Headless CMS". 

Le stockage s'appuie sur **MongoDB**, exploitant sa nature orientée document pour sauvegarder des pages composées de listes de blocs polymorphes (Textes, Images, Évènements, etc.). Ce microservice est optimisé pour le Cloud grâce à une compilation native via **GraalVM**.

## 📋 Table des matières
- [Fonctionnalités](#-fonctionnalités)
- [Modèle de Données (Blocs)](#-modèle-de-données-blocs)
- [Prérequis](#-prérequis)
- [Installation et Lancement (Mode JIT classique)](#-installation-et-lancement-mode-jit-classique)
- [🚀 Optimisation GraalVM (Image Native)](#-optimisation-graalvm-image-native)
- [Lancement avec Docker](#-lancement-avec-docker)
- [Aperçu de l'API REST](#-aperçu-de-lapi-rest)

---

## ✨ Fonctionnalités
- **Headless CMS** : Création, mise à jour et récupération de "Mobile Pages" via API REST.
- **Composition Dynamique** : Les pages sont constituées d'une liste de blocs variables, permettant au front-end de s'adapter au contenu.
- **Base de données NoSQL** : Utilisation de Spring Data MongoDB pour gérer efficacement la structure arborescente et polymorphe des pages.
- **Sécurité** : Validation des tokens JWT via Spring Security (`JwtAuthenticationFilter`).
- **Cloud-Native & GraalVM** : Application compilée en image native (AOT - Ahead-Of-Time compilation) pour un temps de démarrage ultra-rapide (idéal pour le serverless/Kubernetes) et une faible empreinte RAM.

---

## 🧩 Modèle de Données (Blocs)

Le cœur du CMS repose sur une architecture de contenu par blocs. Une `MobilePage` contient un tableau d'objets `Block`. Les types de blocs actuellement supportés (via le polymorphisme et Jackson `@JsonSubTypes`) sont :

- `TEXT` : Un bloc de texte enrichi (titre, contenu).
- `IMAGE` : Un bloc image (url, alt text).
- `EVENT` : Un bloc événement (date, localisation).
- `CONTENT_LIST` : Une liste de contenus imbriqués.

---

## 🛠 Prérequis

Pour exécuter ce projet localement, assurez-vous d'avoir installé :
- **Java 25** (JDK 25)
- **GraalVM** (Optionnel en dev, requis si vous buildez le binaire nativement en local)
- **Docker** et **Docker Compose** (pour MongoDB et le build de l'image native)

---

## 💻 Installation et Lancement (Mode JIT classique)

Pour le développement quotidien (avec rechargement rapide) :

### 1. Cloner le projet
```bash
git clone [https://github.com/wang-tu-94/ms.cms.git](https://github.com/wang-tu-94/ms.cms.git)
cd ms.cms
```

### 2. Démarrer MongoDB via Docker
Un fichier `compose.yml` est fourni pour lancer rapidement une instance MongoDB avec les bons identifiants :
```bash
docker-compose up -d
```

### 3. Démarrer l'application avec Gradle
```bash
./gradlew bootRun
```
L'API sera accessible sur : `http://localhost:8086/api/ms-cms`

---

## 🚀 Optimisation GraalVM (Image Native)

Ce projet intègre les build tools de GraalVM pour compiler l'application de manière statique (AOT - *Ahead of Time*). Cela permet à l'application de démarrer en quelques millisecondes et de consommer très peu de mémoire.

**Pour compiler le binaire natif localement (nécessite GraalVM JDK d'installé) :**
```bash
./gradlew nativeCompile
```
*Le fichier exécutable sera généré dans `build/native/nativeCompile/`.*

**Pour générer directement une image Docker native (recommandé) :**
Plutôt que d'installer GraalVM sur votre machine, vous pouvez laisser Paketo Buildpacks créer une image Docker optimisée :
```bash
./gradlew bootBuildImage
```
Cette commande génère une image Docker prête pour la production avec l'exécutable natif à l'intérieur.

---

## 🐳 Lancement avec Docker

Si vous utilisez le `Dockerfile` classique multi-stage fourni dans le dépôt :

**1. Construire l'image :**
```bash
docker build -t magnomos/ms.cms:latest .
```

**2. Exécuter le conteneur :**
```bash
docker run -d -p 8083:8083 \
  -e MONGO_HOST=votre_hote_mongo \
  -e MONGO_PORT=27017 \
  -e MONGO_DB=cms_db \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  -e JWT_SECRET=votre_secret_jwt \
  magnomos/ms.cms:latest
```

---

## 📡 Aperçu de l'API REST

> **Chemin de base :** `/api/ms-cms/v1/mobile-pages`
> **Sécurité :** L'en-tête `Authorization: Bearer <token>` est requis.

### 📄 Pages
- `GET /` : Lister toutes les pages mobiles (supporte le filtrage et la pagination).
- `GET /{id}` : Récupérer une page spécifique et son contenu (ses blocs).
- `POST /` : Créer une nouvelle page avec sa liste de blocs.

---
*Maintenu par [wang-tu-94](https://github.com/wang-tu-94)*
