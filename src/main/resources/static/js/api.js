// ==========================================
// CONFIGURACIÓN DE LA API
// ==========================================

// Cambia esta URL por la de tu backend
const API_BASE_URL = 'http://localhost:8080/api';

// ==========================================
// OBTENER HEADERS CON AUTENTICACIÓN
// ==========================================
function getAuthHeaders() {
    const token = localStorage.getItem('token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
    };
}

// ==========================================
// OBTENER HEADERS PARA FORMULARIOS
// ==========================================
function getAuthHeadersMultipart() {
    const token = localStorage.getItem('token');
    return {
        'Authorization': `Bearer ${token}`
    };
}

// ==========================================
// REALIZAR PETICIÓN GET
// ==========================================
async function apiGet(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'GET',
            headers: getAuthHeaders()
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            throw new Error('Sesión expirada');
        }

        if (!response.ok) {
            throw new Error(`Error ${response.status}: ${response.statusText}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error en petición GET:', error);
        throw error;
    }
}

// ==========================================
// REALIZAR PETICIÓN POST
// ==========================================
async function apiPost(endpoint, data) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            throw new Error('Sesión expirada');
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Error ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error en petición POST:', error);
        throw error;
    }
}

// ==========================================
// REALIZAR PETICIÓN PUT
// ==========================================
async function apiPut(endpoint, data) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(data)
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            throw new Error('Sesión expirada');
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Error ${response.status}`);
        }

        return await response.json();
    } catch (error) {
        console.error('Error en petición PUT:', error);
        throw error;
    }
}

// ==========================================
// REALIZAR PETICIÓN DELETE
// ==========================================
async function apiDelete(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.status === 401) {
            localStorage.clear();
            window.location.href = 'login.html';
            throw new Error('Sesión expirada');
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Error ${response.status}`);
        }

        // DELETE puede no retornar contenido (204)
        if (response.status === 204) {
            return { success: true };
        }

        return await response.json();
    } catch (error) {
        console.error('Error en petición DELETE:', error);
        throw error;
    }
}

// ==========================================
// MANEJO DE ERRORES GLOBAL
// ==========================================
window.addEventListener('unhandledrejection', function(event) {
    console.error('Error no manejado:', event.reason);
});