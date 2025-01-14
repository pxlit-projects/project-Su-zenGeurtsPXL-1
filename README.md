# Fullstack Java Project

## Su-zen Geurts (3AONa)
Change the name and Class in the title above

## Folder structure

- Readme.md
- _architecture_: this folder contains documentation regarding the architecture of your system.
- `docker-compose.yml` : to start the backend (starts all microservices)
- _backend-java_: contains microservices written in java
- _demo-artifacts_: contains images, files, etc that are useful for demo purposes.
- _frontend-web_: contains the Angular webclient

Each folder contains its own specific `.gitignore` file.  
**:warning: complete these files asap, so you don't litter your repository with binary build artifacts!**

## How to setup and run this application
###### Clone the repository
```sh
git clone https://github.com/pxlit-projects/project-Su-zenGeurtsPXL-1.git
```

### Backend
###### Navigate to the project
```sh
cd project-Su-zenGeurtsPXL-1/backend-java/PxlNews
```
###### Setup Docker containers
```sh
docker compose up -d
```

###### Run the application
1. Right mouse click on the application in every microservice and choose run 
   1. ConfigServiceApplication
   2. DiscoveryServiceApplication
   3. GatewayServiceApplication
   4. PostServiceApplication
   5. ReviewServiceApplication
   6. CommentServiceApplication

### Frontend
###### Navigate to the project
```sh
cd project-Su-zenGeurtsPXL-1/frontend-web/news-app
```
###### Install the dependencies
```sh
npm install
```

###### Build the application
```shell
ng build --configuration=production
```

###### Setup Docker and 
```sh

docker build -t news-app .
```

###### run the application
```sh
docker run -d -p 8080:80 news-app
```
