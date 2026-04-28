# 🚌 Sistema Ciempiés

**Sistema de Gestión Integral de Transporte Escolar**

[![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)]()
[![License](https://img.shields.io/badge/license-MIT-green.svg)]()
[![Java](https://img.shields.io/badge/Java-21-orange.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)]()
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple.svg)]()

---

## 📋 Descripción

**Sistema Ciempiés** es una plataforma web full-stack para la gestión del programa de transporte escolar **"Ciempiés - Caminos Seguros"** de la Secretaría Distrital de Movilidad de Bogotá. Permite administrar estudiantes, rutas, colegios, monitores y asistencias, generar reportes estadísticos detallados y enviar notificaciones masivas al equipo operativo.

El sistema fue diseñado para reemplazar el registro manual de asistencias en papel por un flujo digital eficiente, garantizando la trazabilidad del servicio y la seguridad de los estudiantes.

---

## ✨ Características Principales

- 👥 **Gestión de Usuarios** — Administración con roles (Administrador, Encargado, Monitor)
- 🎓 **Gestión de Estudiantes** — Registro completo con datos personales, acudientes y asignación de rutas
- 🚌 **Gestión de Rutas** — Creación de rutas por colegio, jornada y tipo (Ida/Regreso)
- 🏫 **Gestión de Colegios y Zonas** — Organización geográfica del servicio
- ✅ **Registro de Asistencias por Lote** — Marca múltiples estudiantes simultáneamente, reduciendo el tiempo de toma de asistencia en ~90%
- 📊 **Reportes Tabulares y Estadísticos** — Generación en PDF y Excel con gráficos (patrón **Adapter GoF**)
- 🌦️ **Integración con OpenWeatherMap** — Alertas meteorológicas automáticas para rutas escolares
- 📧 **Notificaciones Masivas** — Envío de correos con plantillas profesionales
- 🔐 **Autenticación JWT** — Sistema seguro con control de acceso por roles

---

## 🛠️ Tecnologías Utilizadas

### Backend

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| Java | 21 | Lenguaje de programación |
| Spring Boot | 3.2.x | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Persistencia de datos |
| MySQL | 8.x | Base de datos relacional |
| H2 Database | — | Base de datos en memoria (desarrollo y testing) |
| JWT (jjwt) | 0.11.5 | Tokens de autenticación |
| BCrypt | — | Encriptación de contraseñas |
| iText | 5.5.13 | Generación de PDFs |
| Apache POI | 5.2.3 | Generación de Excel |
| JFreeChart | 1.5.4 | Gráficos estadísticos en reportes |
| JavaMail | 2.x | Envío de correos masivos |

### Frontend

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| HTML5 | — | Estructura |
| CSS3 | — | Estilos personalizados |
| JavaScript | ES6+ | Lógica del cliente |
| Bootstrap | 5.3 | Framework CSS |
| Chart.js | 4.4 | Visualización de datos en dashboard |
| DataTables | 1.13 | Tablas interactivas |
| Bootstrap Icons | 1.11 | Iconografía |
| SweetAlert2 | 11 | Alertas y modales |

### Testing

| Herramienta | Uso |
|-------------|-----|
| **JUnit 5** | Framework de pruebas unitarias |
| **Mockito** | Mocking de dependencias |
| **Apache JMeter** | Pruebas de carga y rendimiento (10–100 usuarios concurrentes) |

### Integraciones Externas

- **OpenWeatherMap API** — Consulta de clima en tiempo real para Bogotá y generación de alertas meteorológicas

### Patrones de Diseño

- **Adapter (GoF)** — Implementado para unificar la generación de reportes en múltiples formatos (PDF con iText y Excel con Apache POI), permitiendo extender a nuevos formatos sin modificar la lógica de negocio.

---

## 📁 Estructura del Proyecto

```
sistema-ciempies/
├── backend/
│   ├── src/main/java/com/sgi/backend/
│   │   ├── config/          # Configuraciones (Security, CORS)
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositorios Spring Data
│   │   ├── service/         # Lógica de negocio
│   │   ├── adapter/         # Patrón Adapter para reportes
│   │   ├── external/        # Generadores externos (PDF, Excel)
│   │   └── security/        # JWT, UserDetails
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── static/images/
│   └── src/test/            # Pruebas unitarias (JUnit 5 + Mockito)
│
└── frontend/
    ├── css/
    ├── js/
    │   ├── api.js           # Cliente API REST
    │   ├── auth.js          # Autenticación
    │   ├── dashboard.js
    │   ├── estudiantes.js
    │   ├── asistencias.js
    │   ├── registrar-lote.js
    │   └── reportes.js
    ├── index.html           # Login
    ├── dashboard.html
    ├── estudiantes.html
    ├── asistencias.html
    ├── registrar-asistencia.html
    ├── reportes.html
    └── notificaciones.html
```

---

## 🚀 Instalación y Configuración

### Prerrequisitos

- Java JDK 21 o superior
- Maven 3.8+
- MySQL 8.x

### 1. Clonar el Repositorio

```bash
git clone https://github.com/SciMar/sgibackend.git
cd sgibackend
```

### 2. Configurar Base de Datos

```sql
CREATE DATABASE sistema_ciempies;
CREATE USER 'ciempies_user'@'localhost' IDENTIFIED BY 'tu_password';
GRANT ALL PRIVILEGES ON sistema_ciempies.* TO 'ciempies_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar Variables de Entorno

Editar `backend/src/main/resources/application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/sistema_ciempies
spring.datasource.username=ciempies_user
spring.datasource.password=tu_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=tu_clave_secreta_muy_larga_y_segura
jwt.expiration=86400000

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_correo@gmail.com
spring.mail.password=tu_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# OpenWeatherMap
openweather.api.key=tu_api_key
```

### 4. Ejecutar Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Servidor disponible en `http://localhost:8080`

### 5. Ejecutar Frontend

```bash
# Con Python
cd frontend
python -m http.server 5500

# Con Node.js (live-server)
npx live-server frontend
```

Frontend disponible en `http://localhost:5500`

---

## 🧪 Testing

### Pruebas Unitarias (JUnit 5 + Mockito)

```bash
cd backend
mvn test

# Con reporte de cobertura
mvn test jacoco:report
```

### Pruebas de Carga (Apache JMeter)

Escenarios cubiertos:

- Login concurrente de usuarios
- Consultas masivas de estudiantes
- Registro simultáneo de asistencias
- Generación de reportes bajo carga

**Configuración:** 10–100 usuarios concurrentes, ramp-up de 10 segundos, 10 iteraciones por usuario.

---

## 👤 Roles y Permisos

| Módulo | Administrador | Encargado | Monitor |
|--------|:-------------:|:---------:|:-------:|
| Dashboard | ✅ | ✅ | ✅ |
| Usuarios | ✅ | ❌ | ❌ |
| Estudiantes | ✅ | ✅ | 👁️ |
| Rutas | ✅ | ✅ | 👁️ |
| Colegios | ✅ | ✅ | ❌ |
| Asistencias | ✅ | ✅ | ✅ |
| Reportes | ✅ | ✅ | ⚠️ |
| Notificaciones | ✅ | ❌ | ❌ |

✅ Acceso completo &nbsp;|&nbsp; 👁️ Solo lectura &nbsp;|&nbsp; ⚠️ Acceso limitado &nbsp;|&nbsp; ❌ Sin acceso

---

## 📊 API Endpoints

### Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/auth/registro` | Registrar usuario |

### Usuarios

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Listar todos |
| GET | `/api/usuarios/{id}` | Obtener por ID |
| POST | `/api/usuarios` | Crear usuario |
| PUT | `/api/usuarios/{id}` | Actualizar |
| DELETE | `/api/usuarios/{id}` | Eliminar |

### Estudiantes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/estudiantes` | Listar todos |
| GET | `/api/estudiantes/activos` | Listar activos |
| GET | `/api/estudiantes/colegio/{id}` | Por colegio |
| POST | `/api/estudiantes` | Crear estudiante |
| PUT | `/api/estudiantes/{id}` | Actualizar |

### Asistencias

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/asistencias/hoy` | Asistencias de hoy |
| GET | `/api/asistencias/fecha/{fecha}` | Por fecha |
| POST | `/api/asistencias` | Registrar asistencia |
| POST | `/api/asistencias/registrar-masivo` | **Registro por lote** |

### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes/usuarios/{formato}` | Reporte de usuarios (PDF/Excel) |
| GET | `/api/reportes/estudiantes/{formato}` | Reporte de estudiantes |
| GET | `/api/reportes/asistencias/{formato}` | Reporte de asistencias |
| GET | `/api/reportes/estadisticas/general` | Estadísticas con gráficos (PDF) |

### Clima

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/clima/actual` | Clima actual de Bogotá |
| GET | `/api/clima/alertas` | Verificar alertas meteorológicas |

### Notificaciones

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/notificaciones/enviar-masivo` | Envío masivo de correos |

---

## 📈 Reportes Disponibles

### Reportes Tabulares (PDF/Excel)

- Usuarios del sistema
- Estudiantes
- Monitores
- Rutas
- Colegios
- Zonas
- Jornadas
- Asistencias (con filtros avanzados)

### Reportes Estadísticos (PDF con gráficos)

- Estadísticas generales del sistema
- Estadísticas por colegio
- Estadísticas por estudiante (rango de fechas)
- Gráficos de pastel y barras con colores semafóricos (Verde >80%, Amarillo >60%, Rojo <60%)

---

## 🔒 Seguridad

- Autenticación basada en **JWT** con expiración configurable
- Contraseñas encriptadas con **BCrypt**
- Protección **CORS** configurada
- Validación de roles en cada endpoint con `@PreAuthorize`
- Tokens almacenados en LocalStorage del cliente

---

## 🎨 Paleta de Colores

| Color | Hex | Uso |
|-------|-----|-----|
| Primary | `#667eea` | Color principal |
| Secondary | `#764ba2` | Color secundario |
| Success | `#4caf50` | Estados positivos |
| Danger | `#f44336` | Estados negativos |
| Warning | `#ffc107` | Advertencias |
| Info | `#2196f3` | Información |

---

## 👥 Equipo

Proyecto desarrollado con metodología **Scrum** por estudiantes del programa **SENA ADSO** (Análisis y Desarrollo de Sistemas de Información).

| Nombre | Rol |
|--------|-----|
| Marcela Ramírez | Scrum Master / Backend Developer |
| Carolina López | Product Owner / Backend Developer |
| Santiago Ríos | Backend Developer |

**Instructor:** Pedro Germain Gutierrez Vergara

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 📞 Soporte

¿Preguntas o sugerencias?

- 📧 Email: ciempiesmovilidad@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/SciMar/sgibackend/issues)

---

**Sistema Ciempiés** &nbsp;·&nbsp; Sistema de Gestión Integral de Transporte Escolar
© 2025 — Todos los derechos reservados
