# 🚌 Sistema Ciempiés

Sistema de Gestión Integral de Transporte Escolar

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![License](https://img.shields.io/badge/license-MIT-green.svg)
![Java](https://img.shields.io/badge/Java-17+-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5.3-purple.svg)

---

## 📋 Descripción

**Sistema Ciempiés** es una plataforma web completa para la gestión del transporte escolar. Permite administrar estudiantes, rutas, colegios, monitores, asistencias y generar reportes detallados en PDF y Excel.

El sistema está diseñado para facilitar el control y seguimiento del transporte escolar, garantizando la seguridad de los estudiantes y optimizando la gestión operativa.

---

## ✨ Características Principales

- 👥 **Gestión de Usuarios** - Administración de usuarios con roles (Administrador, Encargado, Monitor)
- 🎓 **Gestión de Estudiantes** - Registro completo con datos personales, acudientes y asignación de rutas
- 🚌 **Gestión de Rutas** - Creación automática de rutas por colegio, jornada y tipo (Ida/Regreso)
- 🏫 **Gestión de Colegios** - Administración de colegios con jornadas y zonas
- 📍 **Gestión de Zonas** - Organización geográfica del servicio
- ✅ **Control de Asistencias** - Registro de asistencias con estados (Presente/Ausente)
- 📊 **Reportes y Estadísticas** - Generación de reportes en PDF y Excel con gráficos
- 📧 **Notificaciones** - Envío de correos masivos con plantillas profesionales
- 🔐 **Autenticación JWT** - Sistema seguro de autenticación con tokens

---

## 🛠️ Tecnologías Utilizadas

### Backend
| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| Java | 17+ | Lenguaje de programación |
| Spring Boot | 3.x | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Persistencia de datos |
| MySQL | 8.x | Base de datos relacional |
| JWT | 0.11.5 | Tokens de autenticación |
| iText | 5.5.13 | Generación de PDFs |
| Apache POI | 5.2.3 | Generación de Excel |
| JFreeChart | 1.5.4 | Gráficos estadísticos |
| JavaMail | 2.x | Envío de correos |

### Frontend
| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| HTML5 | - | Estructura |
| CSS3 | - | Estilos personalizados |
| JavaScript | ES6+ | Lógica del cliente |
| Bootstrap | 5.3 | Framework CSS |
| Bootstrap Icons | 1.11 | Iconografía |
| SweetAlert2 | 11 | Alertas y modales |

---

## 📁 Estructura del Proyecto

```
sistema-ciempies/
├── backend/
│   ├── src/main/java/com/sgi/backend/
│   │   ├── config/          # Configuraciones (Security, CORS, etc.)
│   │   ├── controller/      # Controladores REST
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # Entidades JPA
│   │   ├── repository/      # Repositorios Spring Data
│   │   ├── service/         # Lógica de negocio
│   │   ├── adapter/         # Patrón Adapter para reportes
│   │   └── external/        # Generadores externos (PDF, Excel)
│   └── src/main/resources/
│       ├── application.properties
│       └── static/images/   # Logo e imágenes
│
└── frontend/
    ├── css/
    │   └── styles.css       # Estilos globales
    ├── js/
    │   ├── config.js        # Configuración y constantes
    │   ├── auth.js          # Autenticación y manejo de sesión
    │   ├── utils.js         # Funciones utilitarias
    │   ├── dashboard.js     # Lógica del dashboard
    │   ├── usuarios.js      # Módulo de usuarios
    │   ├── estudiantes.js   # Módulo de estudiantes
    │   ├── rutas.js         # Módulo de rutas
    │   ├── colegios.js      # Módulo de colegios
    │   ├── asistencias.js   # Módulo de asistencias
    │   ├── reportes.js      # Módulo de reportes
    │   └── notificaciones.js # Módulo de notificaciones
    ├── images/
    │   └── logo.jpeg        # Logo del sistema
    ├── index.html           # Login
    ├── dashboard.html       # Panel principal
    ├── usuarios.html        # Gestión de usuarios
    ├── estudiantes.html     # Gestión de estudiantes
    ├── rutas.html           # Gestión de rutas
    ├── colegios.html        # Gestión de colegios
    ├── asistencias.html     # Control de asistencias
    ├── reportes.html        # Centro de reportes
    └── notificaciones.html  # Notificaciones masivas
```

---

## 🚀 Instalación y Configuración

### Prerrequisitos

- Java JDK 17 o superior
- Maven 3.8+
- MySQL 8.x
- Node.js (opcional, para servidor de desarrollo frontend)

### 1. Clonar el Repositorio

```bash
git clone https://github.com/tu-usuario/sistema-ciempies.git
cd sistema-ciempies
```

### 2. Configurar Base de Datos

Crear la base de datos en MySQL:

```sql
CREATE DATABASE sistema_ciempies;
CREATE USER 'ciempies_user'@'localhost' IDENTIFIED BY 'tu_password';
GRANT ALL PRIVILEGES ON sistema_ciempies.* TO 'ciempies_user'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configurar Backend

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
```

### 4. Ejecutar Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

El servidor estará disponible en `http://localhost:8080`

### 5. Configurar Frontend

Editar `frontend/js/config.js`:

```javascript
const API_URL = 'http://localhost:8080/api';
```

### 6. Ejecutar Frontend

Puedes usar cualquier servidor web estático:

```bash
# Con Python
cd frontend
python -m http.server 5500

# Con Node.js (live-server)
npx live-server frontend
```

El frontend estará disponible en `http://localhost:5500`

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

✅ Acceso completo | 👁️ Solo lectura | ⚠️ Acceso limitado | ❌ Sin acceso

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

### Rutas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/rutas` | Listar todas |
| GET | `/api/rutas/zona/{id}` | Por zona |
| POST | `/api/rutas` | Crear ruta |
| PUT | `/api/rutas/{id}` | Actualizar |

### Colegios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/colegios` | Listar todos |
| GET | `/api/colegios/zona/{id}` | Por zona |
| POST | `/api/colegios` | Crear colegio |
| PUT | `/api/colegios/{id}` | Actualizar |

### Asistencias
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/asistencias/hoy` | Asistencias de hoy |
| GET | `/api/asistencias/fecha/{fecha}` | Por fecha |
| POST | `/api/asistencias` | Registrar asistencia |
| POST | `/api/asistencias/registrar-masivo` | Registro masivo |

### Reportes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/reportes/usuarios/{formato}` | Reporte usuarios |
| GET | `/api/reportes/estudiantes/{formato}` | Reporte estudiantes |
| GET | `/api/reportes/asistencias/{formato}` | Reporte asistencias |
| GET | `/api/reportes/estadisticas/general` | Estadísticas PDF |

### Notificaciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/notificaciones/enviar-masivo` | Correo masivo |

---

## 📧 Plantilla de Correos

El sistema incluye correos HTML profesionales con:

- ✅ Logo corporativo
- ✅ Header con gradiente
- ✅ Firma corporativa
- ✅ Redes sociales
- ✅ Diseño responsive

---

## 📈 Reportes Disponibles

### Reportes Rápidos (PDF/Excel)
- Usuarios del sistema
- Estudiantes
- Monitores
- Rutas
- Colegios
- Zonas
- Jornadas

### Reportes con Filtros
- Asistencias por fecha, colegio, monitor o estudiante

### Reportes Estadísticos (PDF con gráficos)
- Estadísticas generales
- Estadísticas por colegio
- Estadísticas por estudiante

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

## 🔒 Seguridad

- Autenticación basada en JWT
- Contraseñas encriptadas con BCrypt
- Protección CORS configurada
- Validación de roles en cada endpoint
- Tokens con expiración configurable

---

## 🧪 Testing

```bash
# Ejecutar tests del backend
cd backend
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

---

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 👥 Autores

- **Marcela** - *Desarrollo Full Stack* - SENA ADSO

---

## 📞 Soporte

Si tienes preguntas o necesitas ayuda:

- 📧 Email: ciempiesmovilidad@gmail.com
- 🐛 Issues: [GitHub Issues](https://github.com/tu-usuario/sistema-ciempies/issues)

---

<p align="center">
  <img src="frontend/images/logo.jpeg" alt="Logo Ciempiés" width="100">
  <br>
  <b>Sistema Ciempiés</b>
  <br>
  Sistema de Gestión de Transporte Escolar
  <br>
  © 2024 - Todos los derechos reservados
</p>