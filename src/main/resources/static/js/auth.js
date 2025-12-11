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
        // ✅ También guardar como 'userData' para compatibilidad
        localStorage.setItem('userData', JSON.stringify(user));
    },

    // Cerrar sesión
    logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        localStorage.removeItem('userData');
        window.location.href = 'login.html';
    },

    // Verificar si está autenticado
    isAuthenticated() {
        return !!this.getToken();
    },

    // ✅ Verificar si es primer ingreso
    isPrimerIngreso() {
        const user = this.getUser();
        return user && user.primerIngreso === true;
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

    // ✅ Redirigir si no está autenticado O si es primer ingreso
    requireAuth() {
        if (!this.isAuthenticated()) {
            window.location.href = 'login.html';
            return;
        }

        // ✅ Si es primer ingreso, redirigir a cambiar contraseña
        if (this.isPrimerIngreso()) {
            // Solo redirigir si NO estamos ya en la página de cambiar contraseña
            if (!window.location.href.includes('cambiar-contrasena.html')) {
                window.location.href = 'cambiar-contrasena.html';
            }
        }
    },

    // Redirigir si ya está autenticado
    redirectIfAuthenticated() {
        if (this.isAuthenticated()) {
            // ✅ Si es primer ingreso, ir a cambiar contraseña
            if (this.isPrimerIngreso()) {
                window.location.href = 'cambiar-contrasena.html';
            } else {
                window.location.href = 'dashboard.html';
            }
        }
    },

    // Verificar permisos
    requireRoles(roles) {
        if (!this.hasAnyRole(roles)) {
            alert('No tiene permisos para acceder a esta página');
            window.location.href = 'dashboard.html';
        }
    },

    // ✅ Actualizar estado de primer ingreso después de cambiar contraseña
    updatePrimerIngreso(value) {
        const user = this.getUser();
        if (user) {
            user.primerIngreso = value;
            localStorage.setItem('user', JSON.stringify(user));
            localStorage.setItem('userData', JSON.stringify(user));
        }
    }
};

/**
 * Cargar información del usuario en el navbar
 */
function loadNavbarUser() {
    const user = Auth.getUser();
    if (!user) return;

    const navUserName = document.getElementById('navUserName');
    const navUserRole = document.getElementById('navUserRole');

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
 */
function updateMenuByRole() {
    const user = Auth.getUser();
    if (!user) return;

    const rol = user.rol;

    const menuUsuarios = document.getElementById('menuUsuarios');
    const menuEstudiantes = document.getElementById('menuEstudiantes');
    const menuRutas = document.getElementById('menuRutas');
    const menuColegios = document.getElementById('menuColegios');
    const menuAsistencias = document.getElementById('menuAsistencias');
    const menuNotificaciones = document.getElementById('menuNotificaciones');
    const menuReportes = document.getElementById('menuReportes');

    if (rol === 'ADMINISTRADOR') {
        if (menuUsuarios) menuUsuarios.style.display = 'block';
        if (menuEstudiantes) menuEstudiantes.style.display = 'block';
        if (menuRutas) menuRutas.style.display = 'block';
        if (menuColegios) menuColegios.style.display = 'block';
        if (menuAsistencias) menuAsistencias.style.display = 'block';
        if (menuNotificaciones) menuNotificaciones.style.display = 'block';
        if (menuReportes) menuReportes.style.display = 'block';
    }
    else if (rol === 'ENCARGADO') {
        if (menuEstudiantes) menuEstudiantes.style.display = 'block';
        if (menuRutas) menuRutas.style.display = 'block';
        if (menuColegios) menuColegios.style.display = 'block';
        if (menuAsistencias) menuAsistencias.style.display = 'block';
        if (menuNotificaciones) menuNotificaciones.style.display = 'block';
        if (menuReportes) menuReportes.style.display = 'block';
    }
    else if (rol === 'MONITOR') {
        if (menuEstudiantes) menuEstudiantes.style.display = 'block';
        if (menuAsistencias) menuAsistencias.style.display = 'block';
    }
}

/**
 * Actualiza la información del usuario en el navbar
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