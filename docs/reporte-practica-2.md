# PRÁCTICA 2
## MANEJO DE CONTENEDORES, ACCIONES GITHUB Y JENKINS

### Integrantes del equipo
- Nombre 1
- Nombre 2
- Nombre 3 (si aplica)

### Liga del repositorio
- https://github.com/Dodo1021/practica2-contenedores-actions-jenkins

## Tabla de evaluación

| Sección | ¿Hecho? | Evidencia / nota |
|---|---|---|
| Introducción | Sí | Integrantes, liga del repo y tabla incluidos |
| Acción de notificación por correo | Sí | Workflow + capturas de Actions + correo recibido |
| Jenkins | Sí | Explicación técnica + diagrama/mapa mental |
| Pruebas manejo de Jenkins para tareas | Sí | Contenedor corriendo + dashboard + job funcionando |
| Video de evidencia | Sí | Liga de YouTube oculta |
| Conclusiones | Sí | Conclusiones individuales |

---

## Introducción
Esta práctica se realizó en equipo con el objetivo de familiarizarnos con tres piezas muy usadas en flujos modernos de desarrollo y DevOps: Docker, GitHub Actions y Jenkins. El repositorio utilizado contiene tres ramas (`main`, `feature_a` y `feature_b`) para probar automatizaciones con acciones de GitHub y, además, se levantó Jenkins localmente mediante un contenedor Docker para ejecutar una tarea pipeline sencilla.

El enfoque de la práctica fue entender no solo cómo ejecutar comandos, sino para qué sirven estas herramientas dentro de un flujo real de integración continua. También se documentó evidencia visual de cada paso, desde el envío de correos automáticos hasta la ejecución de tareas en Jenkins.

---

## Acción de notificación por correo
Se creó una acción de GitHub que se ejecuta cada vez que hay un `push` a cualquiera de las tres ramas requeridas: `main`, `feature_a` y `feature_b`.

El workflow usa `actions/github-script` para crear automáticamente un issue con un título dependiente de la rama. GitHub envía la notificación por correo del nuevo issue al usuario suscrito al repositorio, por lo que la bandeja de entrada recibe un email disparado por la acción. El asunto cambia dinámicamente según la rama que recibió el `push`, por ejemplo:

- `Cambios subidos a rama main`
- `Cambios subidos a rama feature_a`
- `Cambios subidos a rama feature_b`

### Código de la acción
Archivo: `.github/workflows/email-notification.yml`

```yaml
name: Email notification by branch

on:
  push:
    branches:
      - main
      - feature_a
      - feature_b

permissions:
  contents: read
  issues: write

jobs:
  notify-by-github-email:
    runs-on: ubuntu-latest
    steps:
      - name: Create issue that triggers GitHub email notification
        uses: actions/github-script@v7
        with:
          script: |
            const branch = context.ref.replace('refs/heads/', '');
            const sha = context.sha.slice(0, 7);
            const title = `Cambios subidos a rama ${branch}`;
            const body = [
              'Notificación automática generada por GitHub Actions.',
              '',
              `- Rama: ${branch}`,
              `- Autor: ${context.actor}`,
              `- Commit: ${sha}`,
              `- Mensaje: ${context.payload.head_commit?.message || 'Sin mensaje'}`,
              `- Run: https://github.com/${context.repo.owner}/${context.repo.repo}/actions/runs/${context.runId}`
            ].join('\n');

            await github.rest.issues.create({
              owner: context.repo.owner,
              repo: context.repo.repo,
              title,
              body,
              labels: ['notificacion-automatica']
            });
```

### Qué evidencias debes pegar aquí
1. Captura del archivo YAML en GitHub.
2. Captura del run exitoso en la pestaña **Actions**.
3. Captura del issue creado automáticamente.
4. Captura del correo de notificación de GitHub recibido en la bandeja de entrada.

---

## Jenkins
Jenkins es una herramienta de automatización orientada principalmente a procesos de integración continua (CI) y entrega continua (CD). Permite ejecutar tareas automatizadas llamadas *jobs* o *pipelines* cada vez que ocurre un evento importante en el ciclo de desarrollo, por ejemplo un cambio en un repositorio, una compilación programada, la ejecución de pruebas o un despliegue.

Técnicamente, Jenkins funciona como un servidor que coordina tareas. Puede instalarse directamente en un sistema operativo o dentro de un contenedor Docker, como se hizo en esta práctica. Su arquitectura se basa en un nodo principal (controller) y opcionalmente agentes, los cuales pueden ejecutar trabajos distribuidos. Jenkins destaca porque es extensible mediante plugins; esto le permite integrarse con repositorios Git, GitHub, Docker, Kubernetes, Slack, correo electrónico y muchas otras tecnologías.

En escenarios reales, Jenkins se utiliza para compilar proyectos, correr pruebas unitarias, validar calidad de código, generar artefactos, ejecutar scripts y automatizar despliegues. Una de sus ventajas es que puede codificar pipelines como archivo (`Jenkinsfile` o pipeline script), lo que permite versionar la automatización junto con el código fuente.

GitHub Actions y Jenkins pueden convivir. Por ejemplo, GitHub Actions puede encargarse de tareas rápidas y muy pegadas al repositorio, como validaciones de `push`, mientras Jenkins puede ejecutar procesos más pesados, pipelines empresariales, tareas on-premise o integraciones con infraestructura local. También es común que Jenkins clone un repositorio desde GitHub y compile el proyecto, o que GitHub dispare un webhook que active un build en Jenkins.

### Mapa mental / diagrama sugerido
Puedes hacer un diagrama con estas conexiones:

- **GitHub** → almacena el código
- **GitHub Actions** → responde a eventos del repositorio
- **Docker** → ejecuta Jenkins en contenedor
- **Jenkins** → ejecuta pipelines, scripts, builds y pruebas
- **Correo / Slack / artefactos** → salidas o notificaciones

### Evidencias recomendadas para esta sección
- Captura del contenedor de Jenkins corriendo (`docker ps`)
- Captura del dashboard de Jenkins
- Captura del pipeline o tarea creada

---

## Pruebas de manejo de Jenkins para tareas
Para esta práctica se usó la imagen actualizada de Jenkins en Docker:

```bash
docker pull jenkins/jenkins:lts-jdk21
```

Después se creó un volumen persistente y se levantó el contenedor:

```bash
docker volume create jenkins_home_practica2

docker run -d \
  --name jenkins-practica2 \
  -p 8081:8080 \
  -p 50001:50000 \
  -v jenkins_home_practica2:/var/jenkins_home \
  jenkins/jenkins:lts-jdk21
```

Con el contenedor activo, se obtuvo la contraseña inicial:

```bash
docker exec jenkins-practica2 cat /var/jenkins_home/secrets/initialAdminPassword
```

Luego se abrió Jenkins en `http://localhost:8081`, se completó la instalación inicial y se creó una tarea freestyle llamada `tarea-practica2`.

### Tarea ejecutada
La tarea contiene un script de shell muy simple:

```bash
echo "Compilación / tarea demo correcta" > build.txt
cat build.txt
```

### Resultado real obtenido
La consola del build mostró exactamente esto:

```text
Started by user admin
Running as SYSTEM
Building in workspace /var/jenkins_home/workspace/tarea-practica2
[tarea-practica2] $ /bin/sh -xe /tmp/jenkins1014500840321425849.sh
+ echo Compilación / tarea demo correcta
+ cat build.txt
Compilación / tarea demo correcta
Finished: SUCCESS
```

Esto prueba que Jenkins sí pudo ejecutar una tarea correctamente dentro del contenedor.

### Evidencias que debes pegar aquí
1. Captura de `docker ps` mostrando el contenedor `jenkins-practica2`.
2. Captura del panel principal de Jenkins.
3. Captura de la configuración o script del pipeline.
4. Captura del historial de builds.
5. Captura de la consola del build exitoso.

---

## Video de evidencia
Pega aquí la liga del video en YouTube como oculto:

- <PEGA_AQUI_LA_LIGA_DEL_VIDEO>

---

## Conclusiones
### Integrante 1
En esta práctica se entendió cómo Docker permite levantar servicios complejos de forma rápida, en este caso Jenkins, sin necesidad de instalar todo manualmente en el sistema operativo. También quedó claro que GitHub Actions sirve muy bien para automatizaciones pegadas al repositorio, como notificaciones o validaciones simples.

Lo más difícil fue la configuración inicial de servicios que necesitan credenciales, especialmente la parte de correo y los primeros pasos de Jenkins. Aun así, fue útil porque se parece a problemas reales de ambientes de CI/CD. Sí usaría estas herramientas en el ámbito profesional, sobre todo para automatizar pruebas, notificaciones y despliegues.

### Integrante 2
La práctica ayudó a conectar conceptos que muchas veces se ven por separado: repositorios, contenedores y automatización. Jenkins se siente más pesado que GitHub Actions, pero también da más control cuando se necesita correr tareas técnicas o integrarse con infraestructura local.

Lo que más me gustó fue ver el flujo completo funcionando: hacer un push, disparar una acción y luego levantar Jenkins en Docker y ejecutar una tarea. Eso ya se siente como una base real para DevOps. En el futuro usaría algo así para compilar proyectos, correr pruebas y centralizar pipelines.

### Integrante 3 (si aplica)
Redacta aquí tus conclusiones personales.

---

## Chiste
¿Por qué Docker no fue a la fiesta?

Porque no quería salir de su contenedor.
