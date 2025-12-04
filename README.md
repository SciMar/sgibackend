# Panel de Administración - Sistema Ciempiés

Frontend adaptado 100% al backend real del proyecto.

## 📁 Archivos del Frontend

```
├── login.html           - Página de inicio de sesión
├── admin-panel.html     - Panel principal de administración
└── admin-panel.js       - Lógica JavaScript del panel
```

## 🚀 Cómo usar

### 1. Configurar la URL del Backend

En `admin-panel.js` y `login.html`, ajusta la URL si es necesario:

```javascript
const API_URL = 'http://localhost:8080/api';
```

### 2. Iniciar el Backend

Asegúrate de que tu backend de Spring Boot esté corriendo en el puerto 8080.

### 3. Abrir el Frontend

Puedes usar cualquiera de estas opciones:

**Opción A: Servidor simple con Python**
```bash
python -m http.server 8000
```
Luego abre: http://localhost:8000/login.html

**Opción B: Live Server (VS Code)**
- Instala la extensión "Live Server"
- Click derecho en `login.html` → "Open with Live Server"

**Opción C: Directamente en el navegador**
- Abre `login.html` en tu navegador

### 4. Iniciar Sesión

Usa las credenciales de administrador:
```
Email: admin@ciempies.com
Contraseña: admin123
```

## 📋 Funcionalidades Implementadas

### ✅ Autenticación
- Login con email y contraseña
- Almacenamiento de JWT en localStorage
- Cierre de sesión
- Validación de sesión en cada petición

### ✅ Gestión de Monitores
- ✓ Crear nuevo monitor (con zona y jornada)
- ✓ Listar todos los monitores
- ✓ Ver detalles (nombre completo, email, zona, jornada)
- ✓ Activar/Desactivar monitor
- ✓ Eliminar monitor
- ✓ Estadísticas (total y activos)

### ✅ Gestión de Usuarios
- ✓ Crear nuevo usuario (con rol)
- ✓ Listar todos los usuarios
- ✓ Ver información completa
- ✓ Activar/Desactivar usuario
- ✓ Eliminar usuario
- ✓ Filtrado por rol

## 🎨 Características de la UI

- ✨ Diseño moderno y responsivo
- 🎯 Interfaz intuitiva con tabs
- 📊 Estadísticas en tiempo real
- ⚡ Feedback visual (alertas de éxito/error)
- 🔄 Loading states
- 📱 Optimizado para móviles

## 🔌 Endpoints Utilizados

### Autenticación
```
POST /api/auth/login
```

### Monitores
```
GET    /api/monitores              - Listar todos
POST   /api/monitores              - Crear nuevo
PATCH  /api/monitores/{id}/activar - Activar
PATCH  /api/monitores/{id}/desactivar - Desactivar
DELETE /api/monitores/{id}         - Eliminar
```

### Usuarios
```
GET    /api/usuarios               - Listar todos
POST   /api/usuarios               - Crear nuevo
PATCH  /api/usuarios/{id}/activar  - Activar
PATCH  /api/usuarios/{id}/desactivar - Desactivar
DELETE /api/usuarios/{id}          - Eliminar
```

### Zonas y Jornadas
```
GET /api/zonas     - Listar zonas
GET /api/jornadas  - Listar jornadas
```

## 📦 DTOs Utilizados

### Login Request
```json
{
  "email": "string",
  "contrasena": "string"
}
```

### Login Response
```json
{
  "usuario": {
    "id": number,
    "primerNombre": "string",
    "primerApellido": "string",
    "email": "string",
    "rol": "ADMINISTRADOR|ENCARGADO|MONITOR",
    "activo": boolean
  },
  "token": "string",
  "tipo": "Bearer"
}
```

### Crear Monitor Request
```json
{
  "tipoId": "CC|TI|CE",
  "numId": "string",
  "primerNombre": "string",
  "segundoNombre": "string",
  "primerApellido": "string",
  "segundoApellido": "string",
  "email": "string",
  "contrasena": "string",
  "zonaId": number,
  "jornadaId": number
}
```

### Crear Usuario Request
```json
{
  "tipoId": "CC|TI|CE",
  "numId": "string",
  "primerNombre": "string",
  "segundoNombre": "string",
  "primerApellido": "string",
  "segundoApellido": "string",
  "email": "string",
  "contrasena": "string",
  "rol": "ADMINISTRADOR|ENCARGADO|MONITOR"
}
```

## 🔒 Seguridad

- JWT almacenado en localStorage
- Authorization header en todas las peticiones: `Bearer {token}`
- Validación de sesión expirada (401)
- Redirección automática a login si no está autenticado

## 🐛 Troubleshooting

### Error de CORS
Si recibes error de CORS, asegúrate de que tu backend tenga:
```java
@CrossOrigin(origins = "*")
```

### Token expirado
El sistema detecta automáticamente cuando el token expira y te redirige al login.

### Backend no responde
Verifica que:
1. El backend esté corriendo en el puerto 8080
2. La URL en `API_URL` sea correcta
3. Los endpoints estén accesibles

## 🎯 Próximos Pasos

Para mejorar el sistema, puedes agregar:

1. **Seguridad por Roles en Backend**
    - Agregar `@PreAuthorize` a los controladores
    - Implementar SecurityConfig

2. **Funcionalidades Adicionales**
    - Editar monitor/usuario
    - Búsqueda y filtros
    - Paginación
    - Exportar datos

3. **Mejoras de UI**
    - Modo oscuro
    - Notificaciones toast
    - Confirmaciones más elegantes
    - Dashboard con gráficos

## 👥 Roles del Sistema

- **ADMINISTRADOR**: Acceso total al sistema
- **ENCARGADO**: Gestión de monitores y zonas
- **MONITOR**: Acceso limitado a sus propios datos

## 📝 Notas

- Este frontend está 100% adaptado a tu backend actual
- Todos los endpoints, DTOs y estructuras de datos coinciden exactamente
- El código está comentado y es fácil de mantener
- Listo para producción (solo falta agregar validaciones de roles en backend)

## 🤝 Desarrollado por

NovaHer Technologies - Sistema Ciempiés