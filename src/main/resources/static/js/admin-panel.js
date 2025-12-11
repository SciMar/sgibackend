// ==========================================
// CONFIGURACIÓN
// ==========================================

const API_URL = 'http://localhost:8080/api';
let currentUser = null;
let authToken = null;

// ==========================================
// INICIALIZACIÓN
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
});

function checkAuth() {
    authToken = localStorage.getItem('token');
    const userJson = localStorage.getItem('user');

    if (!authToken || !userJson) {
        window.location.href = 'login.html';
        return;
    }

    currentUser = JSON.parse(userJson);
    updateUserInfo();

    // Cargar datos iniciales
    loadMonitores();
    loadZonasYJornadas();
}

function updateUserInfo() {
    const nombreCompleto = `${currentUser.primerNombre} ${currentUser.primerApellido}`;
    document.getElementById('userName').textContent = nombreCompleto;
    document.getElementById('userRole').textContent = currentUser.rol;
}

// ==========================================
// AUTENTICACIÓN
// ==========================================

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'login.html';
}

// ==========================================
// TABS
// ==========================================

function switchTab(tabName) {
    // Actualizar botones
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
    });
    event.target.classList.add('active');

    // Actualizar contenido
    document.querySelectorAll('.tab-content').forEach(content => {
        content.classList.remove('active');
    });
    document.getElementById(`tab-${tabName}`).classList.add('active');

    // Cargar datos según la pestaña
    if (tabName === 'monitores') {
        loadMonitores();
    } else if (tabName === 'usuarios') {
        loadUsuarios();
    }
}

// ==========================================
// UTILIDADES HTTP
// ==========================================

async function fetchAPI(endpoint, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${authToken}`
        }
    };

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...defaultOptions,
        ...options,
        headers: {
            ...defaultOptions.headers,
            ...options.headers
        }
    });

    if (response.status === 401) {
        showError('Sesión expirada. Por favor, inicia sesión nuevamente.');
        logout();
        return null;
    }

    return response;
}

// ==========================================
// ALERTAS
// ==========================================

function showSuccess(message) {
    const alert = document.getElementById('alertSuccess');
    alert.textContent = message;
    alert.classList.add('active');

    setTimeout(() => {
        alert.classList.remove('active');
    }, 5000);
}

function showError(message) {
    const alert = document.getElementById('alertError');
    alert.textContent = message;
    alert.classList.add('active');

    setTimeout(() => {
        alert.classList.remove('active');
    }, 5000);
}

// ==========================================
// MODALES
// ==========================================

function openModal(modalId) {
    document.getElementById(modalId).classList.add('active');
}

function closeModal(modalId) {
    document.getElementById(modalId).classList.remove('active');
    // Limpiar formulario si existe
    const form = document.querySelector(`#${modalId} form`);
    if (form) form.reset();
}

function openCreateMonitorModal() {
    openModal('modalCreateMonitor');
}

function openCreateUserModal() {
    openModal('modalCreateUser');
}

// ==========================================
// MONITORES - CRUD
// ==========================================

async function loadMonitores() {
    const loadingEl = document.getElementById('loadingMonitores');
    const tableBody = document.getElementById('monitoresTableBody');

    loadingEl.classList.add('active');

    try {
        const response = await fetchAPI('/monitores');

        if (!response.ok) {
            throw new Error('Error al cargar monitores');
        }

        const monitores = await response.json();

        // Actualizar estadísticas
        const activos = monitores.filter(m => m.activo).length;
        document.getElementById('statTotalMonitores').textContent = monitores.length;
        document.getElementById('statMonitoresActivos').textContent = activos;

        // Renderizar tabla
        tableBody.innerHTML = '';

        if (monitores.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="8" style="text-align: center; padding: 2rem;">
                        <div class="empty-state">
                            <div class="empty-state-icon">📋</div>
                            <p>No hay monitores registrados</p>
                        </div>
                    </td>
                </tr>
            `;
        } else {
            monitores.forEach(monitor => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${monitor.id}</td>
                    <td>${monitor.nombreCompleto}</td>
                    <td>${monitor.email}</td>
                    <td>${monitor.nombreZona || '-'}</td>
                    <td>${monitor.nombreJornada || '-'}</td>
                    <td>${formatDate(monitor.fechaAsignacion)}</td>
                    <td>
                        <span class="badge ${monitor.activo ? 'badge-success' : 'badge-danger'}">
                            ${monitor.activo ? 'Activo' : 'Inactivo'}
                        </span>
                    </td>
                    <td>
                        <div class="actions">
                            ${monitor.activo ?
                                `<button class="btn btn-sm btn-warning" onclick="toggleMonitorStatus(${monitor.id}, false)">Desactivar</button>` :
                                `<button class="btn btn-sm btn-success" onclick="toggleMonitorStatus(${monitor.id}, true)">Activar</button>`
                            }
                            <button class="btn btn-sm btn-danger" onclick="deleteMonitor(${monitor.id})">Eliminar</button>
                        </div>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        }

    } catch (error) {
        console.error('Error:', error);
        showError('Error al cargar los monitores');
    } finally {
        loadingEl.classList.remove('active');
    }
}

async function createMonitor(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    const data = {
        tipoId: formData.get('tipoId') || 'CC',
        numId: formData.get('numId'),
        primerNombre: formData.get('primerNombre'),
        segundoNombre: formData.get('segundoNombre') || null,
        primerApellido: formData.get('primerApellido'),
        segundoApellido: formData.get('segundoApellido') || null,
        email: formData.get('email'),
        contrasena: formData.get('contrasena'),
        zonaId: parseInt(formData.get('zonaId')),
        jornadaId: parseInt(formData.get('jornadaId'))
    };

    try {
        const response = await fetchAPI('/monitores', {
            method: 'POST',
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Error al crear el monitor');
        }

        showSuccess('Monitor creado exitosamente');
        closeModal('modalCreateMonitor');
        loadMonitores();

    } catch (error) {
        console.error('Error:', error);
        showError(error.message || 'Error al crear el monitor');
    }
}

async function toggleMonitorStatus(monitorId, activate) {
    const action = activate ? 'activar' : 'desactivar';

    if (!confirm(`¿Está seguro de ${action} este monitor?`)) {
        return;
    }

    try {
        const response = await fetchAPI(`/monitores/${monitorId}/${action}`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error(`Error al ${action} el monitor`);
        }

        showSuccess(`Monitor ${activate ? 'activado' : 'desactivado'} exitosamente`);
        loadMonitores();

    } catch (error) {
        console.error('Error:', error);
        showError(error.message);
    }
}

async function deleteMonitor(monitorId) {
    if (!confirm('¿Está seguro de eliminar este monitor? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetchAPI(`/monitores/${monitorId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Error al eliminar el monitor');
        }

        showSuccess('Monitor eliminado exitosamente');
        loadMonitores();

    } catch (error) {
        console.error('Error:', error);
        showError(error.message);
    }
}

// ==========================================
// USUARIOS - CRUD
// ==========================================

async function loadUsuarios() {
    const loadingEl = document.getElementById('loadingUsuarios');
    const tableBody = document.getElementById('usuariosTableBody');

    loadingEl.classList.add('active');

    try {
        const response = await fetchAPI('/usuarios');

        if (!response.ok) {
            throw new Error('Error al cargar usuarios');
        }

        const usuarios = await response.json();

        tableBody.innerHTML = '';

        if (usuarios.length === 0) {
            tableBody.innerHTML = `
                <tr>
                    <td colspan="8" style="text-align: center; padding: 2rem;">
                        <div class="empty-state">
                            <div class="empty-state-icon">👥</div>
                            <p>No hay usuarios registrados</p>
                        </div>
                    </td>
                </tr>
            `;
        } else {
            usuarios.forEach(usuario => {
                const nombreCompleto = `${usuario.primerNombre} ${usuario.segundoNombre || ''} ${usuario.primerApellido} ${usuario.segundoApellido || ''}`.trim();

                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${usuario.id}</td>
                    <td>${usuario.tipoId || '-'} ${usuario.numId}</td>
                    <td>${nombreCompleto}</td>
                    <td>${usuario.email}</td>
                    <td><span class="badge badge-info">${usuario.rol}</span></td>
                    <td>
                        <span class="badge ${usuario.activo ? 'badge-success' : 'badge-danger'}">
                            ${usuario.activo ? 'Activo' : 'Inactivo'}
                        </span>
                    </td>
                    <td>${formatDate(usuario.fechaCreacion)}</td>
                    <td>
                        <div class="actions">
                            ${usuario.activo ?
                                `<button class="btn btn-sm btn-warning" onclick="toggleUserStatus(${usuario.id}, false)">Desactivar</button>` :
                                `<button class="btn btn-sm btn-success" onclick="toggleUserStatus(${usuario.id}, true)">Activar</button>`
                            }
                            <button class="btn btn-sm btn-danger" onclick="deleteUser(${usuario.id})">Eliminar</button>
                        </div>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        }

    } catch (error) {
        console.error('Error:', error);
        showError('Error al cargar los usuarios');
    } finally {
        loadingEl.classList.remove('active');
    }
}

async function createUser(event) {
    event.preventDefault();

    const form = event.target;
    const formData = new FormData(form);

    const data = {
        tipoId: formData.get('tipoId') || 'CC',
        numId: formData.get('numId'),
        primerNombre: formData.get('primerNombre'),
        segundoNombre: formData.get('segundoNombre') || null,
        primerApellido: formData.get('primerApellido'),
        segundoApellido: formData.get('segundoApellido') || null,
        email: formData.get('email'),
        contrasena: formData.get('contrasena'),
        rol: formData.get('rol')
    };

    try {
        const response = await fetchAPI('/usuarios', {
            method: 'POST',
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Error al crear el usuario');
        }

        showSuccess('Usuario creado exitosamente');
        closeModal('modalCreateUser');
        loadUsuarios();

    } catch (error) {
        console.error('Error:', error);
        showError(error.message || 'Error al crear el usuario');
    }
}

async function toggleUserStatus(userId, activate) {
    const action = activate ? 'activar' : 'desactivar';

    if (!confirm(`¿Está seguro de ${action} este usuario?`)) {
        return;
    }

    try {
        const response = await fetchAPI(`/usuarios/${userId}/${action}`, {
            method: 'PATCH'
        });

        if (!response.ok) {
            throw new Error(`Error al ${action} el usuario`);
        }

        showSuccess(`Usuario ${activate ? 'activado' : 'desactivado'} exitosamente`);
        loadUsuarios();

    } catch (error) {
        console.error('Error:', error);
        showError(error.message);
    }
}

async function deleteUser(userId) {
    if (!confirm('¿Está seguro de eliminar este usuario? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetchAPI(`/usuarios/${userId}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error('Error al eliminar el usuario');
        }

        showSuccess('Usuario eliminado exitosamente');
        loadUsuarios();

    } catch (error) {
        console.error('Error:', error);
        showError(error.message);
    }
}

// ==========================================
// ZONAS Y JORNADAS
// ==========================================

async function loadZonasYJornadas() {
    try {
        // Cargar zonas
        const zonasResponse = await fetchAPI('/zonas');
        if (zonasResponse.ok) {
            const zonas = await zonasResponse.json();
            const zonaSelect = document.getElementById('zonaSelect');
            zonaSelect.innerHTML = '<option value="">Seleccione una zona</option>';
            zonas.forEach(zona => {
                zonaSelect.innerHTML += `<option value="${zona.id}">${zona.nombre}</option>`;
            });
        }

        // Cargar jornadas
        const jornadasResponse = await fetchAPI('/jornadas');
        if (jornadasResponse.ok) {
            const jornadas = await jornadasResponse.json();
            const jornadaSelect = document.getElementById('jornadaSelect');
            jornadaSelect.innerHTML = '<option value="">Seleccione una jornada</option>';
            jornadas.forEach(jornada => {
                jornadaSelect.innerHTML += `<option value="${jornada.id}">${jornada.nombre}</option>`;
            });
        }
    } catch (error) {
        console.error('Error cargando zonas y jornadas:', error);
    }
}

// ==========================================
// UTILIDADES
// ==========================================

function formatDate(dateString) {
    if (!dateString) return '-';

    const date = new Date(dateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
}