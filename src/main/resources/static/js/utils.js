// Utilidades comunes

// Mostrar alerta de Bootstrap
function showAlert(containerId, type, message, dismissible = true) {
    const container = document.getElementById(containerId);
    if (!container) return;

    const alert = document.createElement('div');
    alert.className = `alert alert-${type} ${dismissible ? 'alert-dismissible fade show' : ''}`;
    alert.setAttribute('role', 'alert');
    alert.innerHTML = `
        ${message}
        ${dismissible ? '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>' : ''}
    `;
    container.appendChild(alert);

    // Auto-remover después de 5 segundos
    if (dismissible) {
        setTimeout(() => {
            alert.remove();
        }, 5000);
    }
}

// Limpiar alertas
function clearAlerts(containerId) {
    const container = document.getElementById(containerId);
    if (container) {
        container.innerHTML = '';
    }
}

// Formatear fecha
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Formatear solo fecha
function formatDateOnly(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString('es-CO', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
    });
}

// Mostrar loading en tabla
function showTableLoading(tableBodyId, colspan, message = 'Cargando...') {
    const tbody = document.getElementById(tableBodyId);
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="${colspan}" class="text-center py-4">
                    <div class="spinner-border text-primary" role="status">
                        <span class="visually-hidden">Cargando...</span>
                    </div>
                    <p class="mt-2 mb-0">${message}</p>
                </td>
            </tr>
        `;
    }
}

// Mostrar error en tabla
function showTableError(tableBodyId, colspan, message = 'Error al cargar los datos') {
    const tbody = document.getElementById(tableBodyId);
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="${colspan}" class="text-center py-4">
                    <i class="bi bi-exclamation-triangle-fill text-danger" style="font-size: 40px;"></i>
                    <p class="mt-2 mb-0 text-danger">${message}</p>
                </td>
            </tr>
        `;
    }
}

// Mostrar tabla vacía
function showTableEmpty(tableBodyId, colspan, message = 'No se encontraron registros') {
    const tbody = document.getElementById(tableBodyId);
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="${colspan}" class="text-center py-4">
                    <i class="bi bi-inbox" style="font-size: 40px; color: #ccc;"></i>
                    <p class="mt-2 mb-0 text-muted">${message}</p>
                </td>
            </tr>
        `;
    }
}

// Confirmar acción
function confirmAction(message) {
    return confirm(message);
}

// Deshabilitar botón con loading
function setButtonLoading(button, loading, originalText = '') {
    if (loading) {
        button.dataset.originalText = button.innerHTML;
        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Procesando...';
    } else {
        button.disabled = false;
        button.innerHTML = button.dataset.originalText || originalText;
    }
}

// Cargar información del usuario en navbar
function loadNavbarUser() {
    const user = Auth.getUser();
    if (!user) return;

    const nombreCompleto = `${user.primerNombre} ${user.primerApellido}`;

    const navUserName = document.getElementById('navUserName');
    const navUserRole = document.getElementById('navUserRole');

    if (navUserName) navUserName.textContent = nombreCompleto;
    if (navUserRole) navUserRole.textContent = ROL_LABELS[user.rol] || user.rol;
}