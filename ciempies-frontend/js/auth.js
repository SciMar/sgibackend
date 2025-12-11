// Módulo de Autenticación
const Auth = {
    // Obtener token
    getToken() {
        return localStorage.getItem('token');
    },

    // Obtener usuario
    getUser() {
        const userStr = localStorage.getItem('user');
        return userStr ? JSON.parse(userStr) : null;
    },

    // Guardar sesión
    saveSession(token, user) {
        localStorage.setItem('token', token);
        localStorage.setItem('user', JSON.stringify(user));
    },

    // Cerrar sesión
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = 'login.html';
    },

    // Verificar si está autenticado
    isAuthenticated() {
        return !!this.getToken();
    },

    // Verificar rol
    hasRole(role) {
        const user = this.getUser();
        return user && user.rol === role;
    },

    // Verificar si tiene alguno de los roles
    hasAnyRole(roles) {
        const user = this.getUser();
        return user && roles.includes(user.rol);
    },

    // Headers para peticiones autenticadas
    getHeaders() {
        return {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${this.getToken()}`
        };
    },

    // Redirigir si no está autenticado
    requireAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = 'login.html';
        }
    },

    // Redirigir si ya está autenticado
    redirectIfAuthenticated() {
        if (this.isAuthenticated()) {
            window.location.href = 'dashboard.html';
        }
    },

    // Verificar permisos
    requireRoles(roles) {
        if (!this.hasAnyRole(roles)) {
            alert('No tiene permisos para acceder a esta página');
            window.location.href = 'dashboard.html';
        }
    }
};

/**
 * Cargar información del usuario en el navbar
 * (Función usada por dashboard y otros módulos)
 */
function loadNavbarUser() {
    const user = Auth.getUser();
    if (!user) return;

    const navUserName = document.getElementById('navUserName');
    const navUserRole = document.getElementById('navUserRole');

    // Construir nombre completo
    const nombreCompleto = user.primerNombre && user.primerApellido
        ? `${user.primerNombre} ${user.primerApellido}`
        : user.nombre || user.username || 'Usuario';

    if (navUserName) {
        navUserName.textContent = nombreCompleto;
    }

    if (navUserRole) {
        navUserRole.textContent = ROL_LABELS ? ROL_LABELS[user.rol] : user.rol;
    }
}

/**
 * Actualiza la visibilidad del menú según el rol del usuario
 * (Función legacy - mantenida por compatibilidad)
 */
function updateMenuByRole() {
    const user = Auth.getUser();
    if (!user) return;

    const rol = user.rol;

    // Elementos del menú
    const menuUsuarios = document.getElementById('menuUsuarios');
    const menuEstudiantes = document.getElementById('menuEstudiantes');
    const menuRutas = document.getElementById('menuRutas');
    const menuColegios = document.getElementById('menuColegios');
    const menuAsistencias = document.getElementById('menuAsistencias');
    const menuNotificaciones = document.getElementById('menuNotificaciones');
    const menuReportes = document.getElementById('menuReportes');

    // ADMINISTRADOR - Ve TODO
    if (rol === 'ADMINISTRADOR') {
        if (menuUsuarios) menuUsuarios.style.display = 'block';
        if (menuEstudiantes) menuEstudiantes.style.display = 'block';
        if (menuRutas) menuRutas.style.display = 'block';
        if (menuColegios) menuColegios.style.display = 'block';
        if (menuAsistencias) menuAsistencias.style.display = 'block';
        if (menuNotificaciones) menuNotificaciones.style.display = 'block';
        if (menuReportes) menuReportes.style.display = 'block';
    }
    // ENCARGADO - Ve todo menos Usuarios
    else if (rol === 'ENCARGADO') {
        if (menuEstudiantes) menuEstudiantes.style.display = 'block';
        if (menuRutas) menuRutas.style.display = 'block';
        if (menuColegios) menuColegios.style.display = 'block';
        if (menuAsistencias) menuAsistencias.style.display = 'block';
        if (menuNotificaciones) menuNotificaciones.style.display = 'block';
        if (menuReportes) menuReportes.style.display = 'block';
    }
    // MONITOR - Solo estudiantes y asistencias
    else if (rol === 'MONITOR') {
        if (menuEstudiantes) menuEstudiantes.style.display = 'block';
        if (menuAsistencias) menuAsistencias.style.display = 'block';
    }
}

/**
 * Actualiza la información del usuario en el navbar
 * (Función legacy - mantenida por compatibilidad)
 */
function updateNavbarUserInfo() {
    loadNavbarUser();
}

/**
 * Cerrar sesión desde el HTML
 */
function logout() {
    if (confirm('¿Está seguro que desea cerrar sesión?')) {
        Auth.logout();
    }
}

/**
 * Verificar autenticación
 */
function checkAuth() {
    Auth.requireAuth();
}