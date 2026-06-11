# People Image Dataset Builder

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Maven-3.9+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/Selenium-4.33.0-43B02A?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenium" />
  <img src="https://img.shields.io/badge/Desktop%20App-Java%20Swing-blue?style=for-the-badge" alt="Java Swing" />
  <img src="https://img.shields.io/badge/Export-TAR.GZ-yellow?style=for-the-badge" alt="TAR.GZ Export" />
</p>

Aplicación de escritorio desarrollada en Java para construir datasets de imágenes de personas a partir de búsquedas automatizadas en Google Images.

El sistema permite cargar un archivo JSON con personas, buscar imágenes por nombre y sinónimos, descargar imágenes, generar metadata individual en formato JSON y empaquetar el resultado final en un archivo `.tar.gz` listo para distribución o procesamiento posterior.

---

## Índice

- [Características principales](#características-principales)
- [Herramientas y tecnologías utilizadas](#herramientas-y-tecnologías-utilizadas)
- [Arquitectura del proyecto](#arquitectura-del-proyecto)
- [Requisitos](#requisitos)
- [Instalación y compilación](#instalación-y-compilación)
- [Ejecución](#ejecución)
  - [Modo interfaz gráfica](#modo-interfaz-gráfica)
  - [Modo archivo de configuración](#modo-archivo-de-configuración)
- [Archivo `config.properties`](#archivo-configproperties)
- [Archivo de personas `people.json`](#archivo-de-personas-peoplejson)
- [Filtros de búsqueda](#filtros-de-búsqueda)
- [Salida generada](#salida-generada)
- [Flujo de funcionamiento](#flujo-de-funcionamiento)
- [Google Chrome, Selenium y captcha](#google-chrome-selenium-y-captcha)
- [Archivos de profiling](#archivos-de-profiling)
- [Troubleshooting](#troubleshooting)
- [Notas técnicas importantes](#notas-técnicas-importantes)
- [Mejoras sugeridas](#mejoras-sugeridas)
- [Comandos útiles](#comandos-útiles)

---

## Características principales

- Búsqueda automática de imágenes en Google Images.
- Lectura de personas desde un archivo JSON.
- Soporte para nombres alternativos o sinónimos por persona.
- Descarga de imágenes originales cuando están disponibles.
- Generación de metadata JSON por imagen descargada.
- Generación de metadata JSON por persona cuando `identified_face=true`.
- Soporte para imágenes reales y fake/artwork mediante configuración.
- Filtros configurables de búsqueda: resolución, tipo, formato y licencia.
- Generación automática de archivo final `.tar.gz`.
- Interfaz gráfica simple usando Java Swing.
- Ejecución alternativa por archivo `.properties`.
- Uso de Selenium Manager para resolver ChromeDriver automáticamente.
- Registro de tiempos y fallos mediante archivos JSON auxiliares.

---

## Herramientas y tecnologías utilizadas

Este proyecto fue desarrollado como una aplicación de escritorio en Java para automatizar la búsqueda, descarga, registro y empaquetado de imágenes de personas.

<p align="left">
  <img src="https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  <img src="https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven" />
  <img src="https://img.shields.io/badge/Selenium-Web%20Automation-43B02A?style=for-the-badge&logo=selenium&logoColor=white" alt="Selenium" />
  <img src="https://img.shields.io/badge/Google%20Chrome-Browser-4285F4?style=for-the-badge&logo=googlechrome&logoColor=white" alt="Google Chrome" />
  <img src="https://img.shields.io/badge/Java%20Swing-Desktop%20UI-blue?style=for-the-badge" alt="Java Swing" />
</p>

<p align="left">
  <img src="https://img.shields.io/badge/Gson-JSON%20Processing-8A2BE2?style=for-the-badge" alt="Gson" />
  <img src="https://img.shields.io/badge/Apache%20Tika-MIME%20Detection-5D6D7E?style=for-the-badge&logo=apache&logoColor=white" alt="Apache Tika" />
  <img src="https://img.shields.io/badge/Apache%20Commons-Utilities-D22128?style=for-the-badge&logo=apache&logoColor=white" alt="Apache Commons" />
  <img src="https://img.shields.io/badge/SLF4J-Logging-2C3E50?style=for-the-badge" alt="SLF4J" />
  <img src="https://img.shields.io/badge/WebP-Support-00A98F?style=for-the-badge" alt="WebP Support" />
</p>

### Stack principal

| Tecnología | Uso dentro del proyecto |
|---|---|
| **Java 21** | Lenguaje principal utilizado para desarrollar la aplicación. |
| **Maven** | Gestión de dependencias, compilación y generación del `.jar`. |
| **Java Swing** | Interfaz gráfica de escritorio para ejecutar el flujo de descarga. |
| **Selenium WebDriver** | Automatización del navegador para realizar búsquedas en Google Images. |
| **Google Chrome / ChromeDriver** | Navegador utilizado por Selenium para obtener resultados de imágenes. |
| **Selenium Manager** | Resolución automática del ChromeDriver compatible con la versión instalada de Chrome. |
| **Gson** | Lectura y escritura de archivos JSON de entrada, metadata y perfiles. |
| **Apache Tika** | Detección del tipo MIME de las imágenes descargadas. |
| **Apache Commons Lang** | Utilidades generales usadas por el proyecto. |
| **Apache Commons Compress** | Generación de archivos comprimidos `.tar.gz`. |
| **SLF4J + Simple Logger** | Registro de logs de ejecución, tiempos y errores. |
| **webp-imageio** | Soporte adicional para trabajar con imágenes en formato WebP. |

### Funcionalidades implementadas

<p align="left">
  <img src="https://img.shields.io/badge/Image%20Search-Automated-success?style=flat-square" alt="Automated Image Search" />
  <img src="https://img.shields.io/badge/Image%20Download-Enabled-success?style=flat-square" alt="Image Download" />
  <img src="https://img.shields.io/badge/Metadata-JSON-blue?style=flat-square" alt="JSON Metadata" />
  <img src="https://img.shields.io/badge/Export-TAR.GZ-yellow?style=flat-square" alt="TAR.GZ Export" />
  <img src="https://img.shields.io/badge/Desktop%20App-Swing-lightgrey?style=flat-square" alt="Desktop App" />
</p>

- Búsqueda automática de imágenes por nombre de persona y sinónimos.
- Descarga de imágenes desde resultados de Google Images.
- Generación de metadata individual por imagen en formato JSON.
- Registro de tiempos de ejecución en `profile.json`.
- Registro de fallos de descarga en `failsProfile.json`.
- Empaquetado final del dataset en formato `.tar.gz`.
- Configuración externa mediante archivos JSON y properties.
- Ejecución desde interfaz gráfica Java Swing.

### Consideraciones técnicas

> Google Images puede mostrar captchas o pantallas de verificación cuando detecta automatización. Para reducir este problema, se recomienda ejecutar Chrome en modo visible y utilizar un perfil persistente mediante `--user-data-dir`.

> El proyecto utiliza Selenium Manager para resolver automáticamente el ChromeDriver compatible con la versión instalada de Google Chrome.

---

## Arquitectura del proyecto

Estructura principal:

```text
Programa/
├── pom.xml
├── config.properties
├── profile.json
├── failsProfile.json
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/mycompany/
│   │   │       ├── imagesdataset/
│   │   │       ├── utils/
│   │   │       ├── views/
│   │   │       └── exceptions/
│   │   └── resources/
│   │       └── simplelogger.properties
│   └── test/
└── target/
```

Componentes principales:

| Archivo | Responsabilidad |
|---|---|
| `Main.java` | Punto de entrada. Abre la interfaz gráfica o ejecuta desde un archivo de configuración. |
| `Window.java` | Interfaz gráfica Swing para seleccionar carpeta, archivo de personas, filtros y cantidad de imágenes. |
| `ImageManager.java` | Orquesta el proceso completo: lectura de personas, descarga, generación de metadata y compresión final. |
| `ImageDownloader.java` | Maneja Selenium, abre Google Images, recolecta URLs, descarga imágenes y genera JSON por imagen. |
| `UrlQuery.java` | Construye la URL de búsqueda en Google Images. |
| `SearchFilters.java` | Traduce filtros seleccionados/configurados a parámetros `tbs` de Google Images. |
| `ImageProperties.java` | Lee propiedades globales desde `config.properties`. |
| `FileUtils.java` | Maneja lectura de propiedades, creación de carpetas, generación `.tar.gz` y limpieza de carpetas auxiliares. |
| `Person.java` | Modelo de persona de entrada. |
| `PersonInfo.java` | Modelo de información adicional de la persona. |
| `OutputImageData.java` | Modelo de metadata generada para cada imagen. |
| `OutputFakeFacesImageData.java` | Extiende la metadata para imágenes fake/artwork. |
| `OutputPersonData.java` | Modelo de metadata generada por persona. |

---

## Requisitos

- Windows 10/11, Linux o macOS.
- Java JDK 17 o superior. Recomendado: JDK 21.
- Maven 3.9 o superior.
- Google Chrome instalado.
- Conexión a internet.

Verificar versiones:

```powershell
java -version
javac -version
mvn -version
```

`mvn -version` debe mostrar Java 17 o superior. Si muestra Java 8, Maven está usando un `JAVA_HOME` incorrecto.

Ejemplo correcto:

```text
Java version: 21.0.x
```

---

## Instalación y compilación

Desde la raíz del proyecto, donde está el `pom.xml`:

```powershell
mvn clean package -DskipTests
```

Esto genera los archivos compilados dentro de `target/`, incluyendo un JAR con dependencias:

```text
target/ImagesDataset-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## Ejecución

La aplicación puede ejecutarse de dos formas:

1. Con interfaz gráfica.
2. Con un archivo de configuración `.properties` como argumento.

### Modo interfaz gráfica

Ejecutar la clase principal:

```powershell
mvn exec:java
```

O ejecutar directamente el JAR:

```powershell
java -jar target/ImagesDataset-1.0-SNAPSHOT-jar-with-dependencies.jar
```

La interfaz permite seleccionar:

- Carpeta destino.
- Archivo JSON con personas.
- Límite de imágenes por persona.
- Iniciales del operador.
- Filtros opcionales de búsqueda.

### Modo archivo de configuración

También se puede ejecutar pasando un archivo `.properties` como argumento:

```powershell
java -jar target/ImagesDataset-1.0-SNAPSHOT-jar-with-dependencies.jar config.properties
```

En este modo, el programa toma las rutas, filtros y opciones desde el archivo indicado.

---

## Archivo `config.properties`

Ejemplo:

```properties
destinationFolderPath=carpeta
peopleFilePath=people.json
imagesAmount=10
operatorId=MP

bulk=200
face_type=real
identified_face=true

resolution=2mp
type=face
format=jpg
licences=ol

image_recopilation_method=web_crawling
image_recopilation_tool=java_crawler_mp

fake_group=artwork
fake_type=caricature
fake_detailed_type=pencil b&w caricature
artwork_generation_method=ai_generated
```

### Propiedades principales

| Propiedad | Descripción | Ejemplo |
|---|---|---|
| `destinationFolderPath` | Carpeta donde se generará la salida. | `carpeta` |
| `peopleFilePath` | Ruta del archivo JSON con personas. | `people.json` |
| `imagesAmount` | Cantidad máxima de imágenes por persona. | `10` |
| `operatorId` | Iniciales o identificador del operador. | `MP` |
| `bulk` | Identificador incremental del lote. Se actualiza automáticamente. | `200` |
| `face_type` | Tipo de dataset: `real` o `fake`. | `real` |
| `identified_face` | Indica si las imágenes están asociadas a personas identificadas. | `true` |
| `resolution` | Filtro de resolución. | `2mp` |
| `type` | Filtro de tipo de imagen. | `face` |
| `format` | Formato esperado. | `jpg` |
| `licences` | Filtro de licencia. | `ol` |
| `image_recopilation_method` | Método de recolección documentado en metadata. | `web_crawling` |
| `image_recopilation_tool` | Herramienta de recolección documentada en metadata. | `java_crawler_mp` |
| `fake_group` | Grupo para imágenes fake/artwork. | `artwork` |
| `fake_type` | Tipo fake/artwork. | `caricature` |
| `fake_detailed_type` | Detalle del tipo fake/artwork. | `pencil b&w caricature` |
| `artwork_generation_method` | Método de generación de artwork. | `ai_generated` |

> Nota: para ejecución por archivo de configuración, el código espera la clave `licences`, no `licence`.

---

## Archivo de personas `people.json`

El archivo de entrada debe ser un array JSON de personas.

Ejemplo mínimo:

```json
[
  {
    "id": 1,
    "name": "Clarice Lispector",
    "standardized_name": "clarice_lispector",
    "synonyms": [
      "Clarice Lispector writer",
      "Clarice Lispector portrait"
    ],
    "person_info": {
      "gender": "Female",
      "citizenship_1_b": "Brazil",
      "birth": 1920,
      "death": 1977,
      "level1_main_occ": "Culture",
      "level2_main_occ": "Writer"
    }
  }
]
```

Campos principales:

| Campo | Requerido | Descripción |
|---|---:|---|
| `id` | Sí | Identificador numérico de la persona. |
| `name` | Sí | Nombre principal usado para buscar imágenes. |
| `standardized_name` | Sí | Nombre normalizado usado para metadata y nombres de archivo. |
| `synonyms` | No | Lista de búsquedas alternativas. |
| `person_info` | No | Información adicional que se replica en el JSON de persona. |

Cuando existen `synonyms`, el programa busca tanto por `name` como por cada sinónimo.

---

## Filtros de búsqueda

Los filtros se traducen a parámetros `tbs` de Google Images.

### Resolución

Valores soportados desde la interfaz:

```text
400x300, 640x480, 800x600, 1024x768, 2mp, 4mp, 6mp, 8mp, 10mp, 12mp, 15mp, 20mp, 40mp, 70mp
```

### Tipo

```text
face, photo, clipart, lineart, animated
```

### Formato

```text
jpg, png, bmp, webp
```

### Licencias

Desde la interfaz:

```text
Licencias creative commons
Licencias comerciales y otras
```

Desde `config.properties`, los valores internos son:

```properties
licences=cl
licences=ol
```

---

## Salida generada

Durante la ejecución, el programa crea una carpeta de trabajo dentro de la carpeta destino:

```text
1_real/
```

O, si `face_type` no es `real`:

```text
0_fake/
```

Dentro de esa carpeta se generan:

- Imágenes descargadas.
- JSON de metadata por imagen.
- JSON de metadata por persona, si `identified_face=true`.

Al finalizar, se genera un archivo comprimido:

```text
google_images-<operatorId>-b<bulk>.tar.gz
```

Ejemplo:

```text
google_images-MP-b0200.tar.gz
```

### Nombres de imágenes

Formato general:

```text
google_images-<operatorId>-b<bulk>-<face_type>-<personIdentification>-i<imageId>-<hash>-origin.<ext>
```

Ejemplo:

```text
google_images-MP-b0200-real-p00000001+clarice_lispector-i00000001-a1b2c3-origin.jpg
```

### Metadata por imagen

Cada imagen tiene un JSON asociado con información como:

- Fecha y hora.
- Query usada.
- URL original.
- ID de imagen.
- Operador.
- Bulk ID.
- Hash SHA-256.
- Fuente.
- Tipo de rostro.
- Persona asociada.
- Método/herramienta de recolección.
- Extensión de imagen.
- Originalidad.

### Metadata por persona

Cuando `identified_face=true`, se genera un JSON por persona con:

- Nombre.
- Nombre estandarizado.
- Identificación de persona.
- ID.
- Sinónimos.
- Fuente.
- Bulk ID.
- Operador.
- Fecha y hora.
- Información adicional de `person_info`.

---

## Flujo de funcionamiento

1. El usuario selecciona o configura la carpeta destino.
2. El usuario selecciona o configura el archivo `people.json`.
3. El programa lee `config.properties`.
4. Se incrementa automáticamente el valor `bulk`.
5. Se crea una carpeta temporal de trabajo (`1_real` o `0_fake`).
6. Para cada persona:
   - Se genera metadata de persona si corresponde.
   - Se construyen búsquedas por nombre y sinónimos.
   - Selenium abre Google Images.
   - Se recolectan URLs de imágenes.
   - Se descargan imágenes válidas.
   - Se genera metadata JSON por imagen.
7. Se comprime la carpeta de trabajo en `.tar.gz`.
8. Se eliminan carpetas auxiliares dentro de la carpeta destino.

---

## Google Chrome, Selenium y captcha

El proyecto usa Selenium con Google Chrome.

Configuración actual recomendada:

```java
ChromeOptions options = new ChromeOptions();
// options.addArguments("--headless=new");
options.addArguments("--lang=en");
options.addArguments("--remote-allow-origins=*");
options.addArguments("--disable-gpu");
options.addArguments("--window-size=1920,1080");
```

El modo `headless` está comentado porque Google puede mostrar captcha o una página distinta cuando detecta automatización.

### Recomendación para reducir captcha

- Ejecutar Chrome visible, no headless.
- Usar pausas razonables entre búsquedas si se procesa un volumen grande.
- Procesar por tandas.
- Evitar reiniciar cookies/sesión constantemente.
- Resolver manualmente el captcha si aparece.

Opcionalmente, se puede usar un perfil persistente de Chrome:

```java
options.addArguments("--user-data-dir=C:/selenium-chrome-profile");
```

Esto permite conservar cookies, sesión y consentimiento entre ejecuciones.

---

## Archivos de profiling

El proyecto usa dos archivos JSON en la raíz de ejecución:

```text
profile.json
failsProfile.json
```

Ambos deben existir o ser creados automáticamente según la versión del código.

Contenido inicial recomendado:

```json
[]
```

### `profile.json`

Registra tiempos por persona:

- Tiempo de scroll.
- Tiempo de obtención de URLs.
- Tiempo de descarga de imágenes.
- Tiempo total por persona.

### `failsProfile.json`

Registra descargas fallidas por timeout u otros errores controlados.

---

## Troubleshooting

### Maven usa Java 8 aunque Windows tiene Java 21

Síntoma:

```text
invalid target release: 11
```

O:

```text
class file has wrong version 55.0, should be 52.0
```

Solución temporal en PowerShell:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
$env:Path="$env:JAVA_HOME\bin;$env:Path"
mvn -version
```

Luego recompilar:

```powershell
mvn clean package -DskipTests
```

### ChromeDriver incompatible con Chrome

Síntoma:

```text
This version of ChromeDriver only supports Chrome version 114
Current browser version is 149...
```

Solución:

- Usar Selenium moderno.
- No usar WebDriverManager viejo.
- Dejar que Selenium Manager resuelva ChromeDriver.
- Borrar cache viejo si hace falta:

```powershell
Remove-Item -Recurse -Force "$env:USERPROFILE\.cache\selenium"
```

### Captcha de Google

Síntoma:

```text
Thumbnails encontrados: 0
URLs finales encontradas: 0
```

Y al abrir Chrome se ve captcha.

Solución:

- Ejecutar sin headless.
- Resolver captcha manualmente.
- Considerar perfil persistente con `--user-data-dir`.
- Reducir velocidad y volumen de búsquedas.

### Falta `profile.json`

Síntoma:

```text
java.nio.file.NoSuchFileException: profile.json
```

Solución:

Crear archivos en la raíz desde donde se ejecuta el programa:

```powershell
'[]' | Out-File -Encoding utf8 profile.json
'[]' | Out-File -Encoding utf8 failsProfile.json
```

### Faltan clases `TarArchiveOutputStream` o `GzipCompressorOutputStream`

Síntoma:

```text
package org.apache.commons.compress.archivers.tar does not exist
```

Solución:

Agregar dependencia Maven:

```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-compress</artifactId>
    <version>1.26.2</version>
</dependency>
```

### No encuentra imágenes reales y descarga logos/SVG

Síntoma:

```text
ImageIO could not read image
```

Causa común:

- Selenium recolectó una URL que no es imagen descargable, por ejemplo un SVG o logo interno de Google.

Solución:

- Mantener filtros de URL que excluyan `gstatic.com`, `fonts.gstatic.com`, `.svg`, `.ico`, `data:` y `base64`.

---

## Notas técnicas importantes

### Usar una carpeta destino dedicada

Al final del proceso, el método `removeAuxFolders` elimina elementos auxiliares dentro de la carpeta destino y conserva los archivos `.tar.gz`.

Por seguridad, usar siempre una carpeta destino dedicada para la ejecución, por ejemplo:

```text
C:\datasets\images-output
```

No usar como destino carpetas generales como:

```text
Desktop
Documents
Downloads
```

### `bulk` se incrementa automáticamente

Cada ejecución incrementa el valor `bulk` en `config.properties`. Esto evita repetir identificadores de lote.

### Dependencia de Google Images

El proyecto depende de la estructura HTML de Google Images. Google puede cambiar selectores, flujos visuales, captcha o comportamiento de carga. Si deja de recolectar imágenes, revisar:

- Captcha.
- Consentimiento/cookies.
- Selectores de thumbnails.
- Selectores de imágenes completas.
- Modo headless.

### Licencias

El filtro de licencias de Google Images ayuda a limitar resultados, pero no reemplaza una revisión legal/manual de uso de las imágenes.

---

## Mejoras sugeridas

- Agregar perfil persistente de Chrome configurable desde `config.properties`.
- Agregar pausas configurables entre búsquedas para reducir captcha.
- Guardar screenshots de debug cuando se detectan `0` thumbnails.
- Validar automáticamente si Google muestra captcha.
- Evitar borrar carpetas no generadas por el proceso dentro de `destinationFolderPath`.
- Permitir ejecución por CLI con argumentos directos además de archivo `.properties`.
- Mejorar logs para diferenciar: captcha, sin resultados, selectores rotos y errores de descarga.
- Agregar tests unitarios para `SearchFilters`, `UrlQuery` y generación de nombres de archivo.
- Agregar un `people.example.json` con formato de entrada documentado.
- Agregar `.gitignore` para excluir `target/`, imágenes generadas, `.tar.gz`, perfiles de Chrome y archivos temporales.

---

## Comandos útiles

Compilar:

```powershell
mvn clean package -DskipTests
```

Ejecutar con interfaz gráfica:

```powershell
java -jar target/ImagesDataset-1.0-SNAPSHOT-jar-with-dependencies.jar
```

Ejecutar con configuración:

```powershell
java -jar target/ImagesDataset-1.0-SNAPSHOT-jar-with-dependencies.jar config.properties
```

Validar Java/Maven:

```powershell
java -version
javac -version
mvn -version
```

Crear archivos de profiling iniciales:

```powershell
'[]' | Out-File -Encoding utf8 profile.json
'[]' | Out-File -Encoding utf8 failsProfile.json
```

---

## Estado actual

El proyecto compila con Maven usando Java 17+ y Selenium 4.33.0. La ejecución depende de Google Chrome instalado y de que Google Images no bloquee la sesión con captcha. Para mayor estabilidad, se recomienda ejecutar con Chrome visible y no en modo headless.
