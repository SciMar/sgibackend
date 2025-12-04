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