// Configuración de la API
const API_BASE_URL = 'http://localhost:8080/api';

// Utilidad para hacer peticiones HTTP
class API {
    static async request(endpoint, options = {}) {
        const token = localStorage.getItem('token');

        const config = {
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            },
            ...options
        };

        try {
            const response = await fetch(`${API_BASE_URL}${endpoint}`, config);

            // Si es 401, redirigir al login
            if (response.status === 401) {
                localStorage.removeItem('token');
                localStorage.removeItem('user');
                window.location.href = 'index.html';
                return null;
            }

            // Si es descarga de archivo
            if (options.download) {
                return response.blob();
            }

            // CORRECCIÓN: Verificar si hay contenido antes de parsear JSON
            const contentType = response.headers.get('content-type');

            // Si no hay contenido (204 No Content) o está vacío
            if (response.status === 204 || response.status === 201) {
                return { success: true };
            }

            // Si no es JSON, retornar texto
            if (!contentType || !contentType.includes('application/json')) {
                const text = await response.text();
                if (!text) {
                    return { success: true };
                }
                return { message: text };
            }

            // Intentar parsear JSON
            const text = await response.text();
            if (!text) {
                return { success: true };
            }

            const data = JSON.parse(text);

            if (!response.ok) {
                throw new Error(data.error || data.message || 'Error en la petición');
            }

            return data;
        } catch (error) {
            console.error('Error en la petición:', error);
            throw error;
        }
    }

    static get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    static post(endpoint, body) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    }

    static put(endpoint, body) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(body)
        });
    }

    static delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }

    static download(endpoint) {
        return this.request(endpoint, {
            method: 'GET',
            download: true
        });
    }
}

// Utilidad para mostrar alertas
function showAlert(message, type = 'success') {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) return;

    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    alertContainer.innerHTML = '';
    alertContainer.appendChild(alert);

    // Auto-cerrar después de 5 segundos
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

// Utilidad para formatear fechas
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// Utilidad para formatear hora
function formatTime(dateString) {
    const date = new Date(dateString);
    return date.toLocaleTimeString('es-CO', {
        hour: '2-digit',
        minute: '2-digit'
    });
}