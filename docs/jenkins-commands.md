# Jenkins local con Docker

## 1. Descargar imagen LTS
```bash
docker pull jenkins/jenkins:lts-jdk21
```

## 2. Crear volumen
```bash
docker volume create jenkins_home
```

## 3. Ejecutar contenedor
```bash
docker run -d \
  --name jenkins-practica2 \
  -p 8080:8080 \
  -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  jenkins/jenkins:lts-jdk21
```

## 4. Ver contraseña inicial
```bash
docker exec jenkins-practica2 cat /var/jenkins_home/secrets/initialAdminPassword
```

## 5. Abrir Jenkins
- Navegador: http://localhost:8080

## 6. Crear una tarea Pipeline
- New Item
- Nombre: `pipeline-practica2`
- Tipo: Pipeline
- Pegar el contenido de `docs/jenkins-pipeline.groovy`
- Save
- Build Now
```