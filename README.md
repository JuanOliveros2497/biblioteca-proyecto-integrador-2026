# biblioteca-proyecto-integrador-2026
Sistema de gestión de biblioteca - Calidad de Software 2026
# biblioteca-proyecto-integrador-2026

# 📚 Sistema de Gestión de Biblioteca

### Proyecto Integrador - Calidad de Software 2026A

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![JUnit](https://img.shields.io/badge/JUnit-5-blue)
![Selenium](https://img.shields.io/badge/Selenium-4.18-yellow)
![JMeter](https://img.shields.io/badge/JMeter-5.6.3-red)
![SonarQube](https://img.shields.io/badge/SonarQube-Community-purple)
![Docker](https://img.shields.io/badge/Docker-✓-blue)

## 📋 Descripción

Sistema web de gestión de biblioteca desarrollado en Java con Spring Boot, que incluye una suite completa de pruebas automatizadas aplicando estándares internacionales de calidad de software (ISO/IEC 25010, ISO/IEC 29119).

## 👥 Equipo

| Nombre           | Rol                            |
| ---------------- | ------------------------------ |
| Juan Oliveros    | Backend, JUnit, Docker, CI/CD  |
| Daniel Poveda    | Selenium, JMeter               |
| Charith Chavarro | Documentación, Plan de Pruebas |

## 🛠️ Stack Tecnológico

| Capa                | Tecnología                  |
| ------------------- | --------------------------- |
| Frontend            | Thymeleaf + Bootstrap 5     |
| Backend             | Java 17 + Spring Boot 3.2.0 |
| Base de datos       | H2 (persistente en archivo) |
| Pruebas unitarias   | JUnit 5 + Mockito + JaCoCo  |
| Pruebas funcionales | Selenium WebDriver 4 + POM  |
| Pruebas de carga    | Apache JMeter 5.6.3         |
| Análisis de calidad | SonarQube Community         |
| Contenedores        | Docker + Docker Compose     |
| CI/CD               | GitHub Actions              |

## ✅ Requisitos Funcionales

| #   | Requisito                          | Módulo     |
| --- | ---------------------------------- | ---------- |
| 1   | Registro de usuario                | Auth       |
| 2   | Login / Logout                     | Auth       |
| 3   | Ver y editar perfil de usuario     | Auth       |
| 4   | Registrar libro (admin)            | Libros     |
| 5   | Listar todos los libros            | Libros     |
| 6   | Buscar libro por título/autor      | Libros     |
| 7   | Editar información de libro        | Libros     |
| 8   | Eliminar libro                     | Libros     |
| 9   | Registrar préstamo de libro        | Préstamos  |
| 10  | Devolver libro                     | Préstamos  |
| 11  | Ver historial de préstamos         | Préstamos  |
| 12  | Gestión de categorías              | Categorías |
| 13  | Notificación de préstamos vencidos | Alertas    |

## 📁 Estructura del Repositorio

```text
biblioteca-proyecto-integrador-2026/
├── app/                          # Código fuente Spring Boot
│   ├── src/main/java/com/biblioteca/
│   │   ├── config/               # Spring Security
│   │   ├── controller/           # Controladores HTTP
│   │   ├── model/                # Entidades JPA
│   │   ├── repository/           # Repositorios Spring Data
│   │   └── service/              # Lógica de negocio
│   ├── src/main/resources/
│   │   ├── templates/            # Vistas Thymeleaf
│   │   └── static/css/           # Estilos CSS
│   └── src/test/                 # Tests JUnit 5
├── tests/
│   ├── selenium/                 # Tests funcionales Selenium
│   └── jmeter/                   # Planes de prueba JMeter
├── docker/
│   ├── Dockerfile
│   └── docker-compose.yml
├── reports/
│   ├── jacoco/                   # Reporte de cobertura
│   ├── jmeter/                   # Reportes de carga
│   └── sonarqube/                # Capturas SonarQube
├── docs/                         # Documentación
└── .github/workflows/ci.yml      # GitHub Actions
```

## 🚀 Instalación y Ejecución

### Prerrequisitos

- Java 17+
- Maven 3.8+
- Docker y Docker Compose

### Opción 1 — Sin Docker (desarrollo)

```bash
# Clonar el repositorio
git clone https://github.com/JuanOliveros2497/biblioteca-proyecto-integrador-2026.git
cd biblioteca-proyecto-integrador-2026/app

# Ejecutar la aplicación
./mvnw spring-boot:run
```

Abrir en el navegador: `http://localhost:8081`

**Credenciales por defecto:**

- Admin: `admin@biblioteca.com` / `admin123`

### Opción 2 — Con Docker

```bash
cd docker
docker compose up --build
```

- App: `http://localhost:8081`
- SonarQube: `http://localhost:9000`

## 🧪 Ejecutar Pruebas

### JUnit 5 (pruebas unitarias)

```bash
cd app
./mvnw test
```

Reporte de cobertura JaCoCo: `app/target/site/jacoco/index.html`

### Selenium WebDriver (pruebas funcionales)

```bash
# Asegurarse de que la app esté corriendo en localhost:8081
cd tests/selenium
mvn test
```

### JMeter (pruebas de carga)

```bash
# Carga normal (5 usuarios)
~/apache-jmeter-5.6.3/bin/jmeter -n \
  -t tests/jmeter/carga-normal.jmx \
  -l tests/jmeter/carga-normal-resultados.jtl

# Carga alta (50 usuarios)
~/apache-jmeter-5.6.3/bin/jmeter -n \
  -t tests/jmeter/carga-alta.jmx \
  -l tests/jmeter/carga-alta-resultados.jtl
```

## 📊 Métricas de Calidad

### JUnit + JaCoCo

| Clase            | Cobertura |
| ---------------- | --------- |
| PrestamoService  | 100%      |
| CategoriaService | 100%      |
| UsuarioService   | 91%       |
| LibroService     | 61%       |
| **Total**        | **83%**   |

### SonarQube

| Métrica         | Resultado |
| --------------- | --------- |
| Bugs            | 0         |
| Reliability     | A         |
| Maintainability | A         |
| Duplicaciones   | 0%        |
| Quality Gate    | ✅ Passed |

### JMeter

| Escenario    | Usuarios | Throughput  | Errores |
| ------------ | -------- | ----------- | ------- |
| Carga normal | 5        | 3,785 req/s | 0%      |
| Carga alta   | 50       | 7,522 req/s | 0%      |

## 🔄 CI/CD

El pipeline de GitHub Actions se ejecuta automáticamente en cada push:

1. Checkout del código
2. Configurar Java 17
3. Cache de dependencias Maven
4. Compilar y ejecutar tests JUnit
5. Publicar reporte JaCoCo

## 📖 Estándares Aplicados

- **ISO/IEC 25010** — Modelo de calidad del producto software
- **ISO/IEC 29119** — Procesos de prueba de software
- **ISO 9001** — Sistema de gestión de calidad
- **CMMI** — Capability Maturity Model Integration
