// Verificar autenticación
Auth.requireAuth();

// Obtener usuario actual
const currentUser = Auth.getUser();

// Variables globales
let rutasOriginales = [];
let rutasFiltradas = [];
let zonas = [];
let modalInstance = null;

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    loadNavbarUser();
    configurarMenuSegunRol();
    inicializar();
});

// Inicializar componentes
async function inicializar() {
    // Inicializar modal
    const modalElement = document.getElementById('modalRuta');
    modalInstance = new bootstrap.Modal(modalElement);

    // Cargar datos
    await cargarZonas();
    await cargarRutas();
}

// Configurar menú según rol
function configurarMenuSegunRol() {
    const rol = currentUser.rol;

    if (rol === 'ADMINISTRADOR') {
        document.getElementById('menuUsuarios').style.display = 'block';
        document.getElementById('menuColegios').style.display = 'block';
        document.getElementById('menuReportes').style.display = 'block';
    } else if (rol === 'ENCARGADO') {
        document.getElementById('menuColegios').style.display = 'block';
        document.getElementById('menuReportes').style.display = 'block';
    } else if (rol === 'MONITOR') {
        // MONITOR no tiene acceso a rutas
        window.location.href = 'dashboard.html';
    }
}

// Cargar zonas
async function cargarZonas() {
    try {
        const response = await fetch(`${API_URL}/zonas/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            zonas = await response.json();
            llenarSelectZonas();
        }
    } catch (error) {
        console.error('Error al cargar zonas:', error);
    }
}

// Llenar select de zonas
function llenarSelectZonas() {
    const selectZona = document.getElementById('zonaId');
    const selectFiltroZona = document.getElementById('filtroZona');

    selectZona.innerHTML = '<option value="">Seleccione...</option>';
    selectFiltroZona.innerHTML = '<option value="">Todas</option>';

    zonas.forEach(zona => {
        const option = `<option value="${zona.id}">${zona.nombreZona}</option>`;
        selectZona.innerHTML += option;
        selectFiltroZona.innerHTML += option;
    });
}

// Cargar rutas
async function cargarRutas() {
    showTableLoading('tableBody', 7);

    try {
        const response = await fetch(`${API_URL}/rutas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            rutasOriginales = await response.json();
            rutasFiltradas = [...rutasOriginales];
            mostrarRutas(rutasFiltradas);
        } else if (response.status === 401) {
            showToast('Sesión expirada. Por favor inicie sesión nuevamente.', 'warning');
            Auth.logout();
        } else {
            showTableError('tableBody', 7);
        }
    } catch (error) {
        console.error('Error:', error);
        showTableError('tableBody', 7);
    }
}

// Mostrar rutas en la tabla
function mostrarRutas(rutas) {
    const tbody = document.getElementById('tableBody');

    if (rutas.length === 0) {
        showTableEmpty('tableBody', 7);
        return;
    }

    tbody.innerHTML = rutas.map(ruta => `
        <tr>
            <td>${ruta.id}</td>
            <td><strong>${ruta.nombreRuta}</strong></td>
            <td>
                <span class="badge ${TIPO_RECORRIDO_BADGE_CLASS[ruta.tipoRuta]}">
                    ${TIPO_RECORRIDO_LABELS[ruta.tipoRuta]}
                </span>
            </td>
            <td>${ruta.nombreZona || '-'}</td>
            <td>
                <span class="badge bg-info">${ruta.totalEstudiantes || 0}</span>
            </td>
            <td>
                <span class="badge ${ruta.activa ? 'bg-success' : 'bg-danger'}">
                    ${ruta.activa ? 'Activa' : 'Inactiva'}
                </span>
            </td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" onclick="verDetalle(${ruta.id})" title="Ver detalle">
                        <i class="bi bi-eye-fill"></i>
                    </button>
                    <button class="btn btn-outline-warning" onclick="editarRuta(${ruta.id})" title="Editar">
                        <i class="bi bi-pencil-fill"></i>
                    </button>
                    ${ruta.activa ?
                        `<button class="btn btn-outline-secondary" onclick="cambiarEstado(${ruta.id}, false)" title="Desactivar">
                            <i class="bi bi-toggle-on"></i>
                        </button>` :
                        `<button class="btn btn-outline-success" onclick="cambiarEstado(${ruta.id}, true)" title="Activar">
                            <i class="bi bi-toggle-off"></i>
                        </button>`
                    }
                    ${currentUser.rol === 'ADMINISTRADOR' ?
                        `<button class="btn btn-outline-danger" onclick="eliminarRuta(${ruta.id})" title="Eliminar">
                            <i class="bi bi-trash-fill"></i>
                        </button>` : ''
                    }
                </div>
            </td>
        </tr>
    `).join('');
}

// Mostrar modal para crear
function mostrarModalCrear() {
    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-plus-circle-fill me-2"></i>Crear Ruta';
    document.getElementById('formRuta').reset();
    document.getElementById('rutaId').value = '';
    document.getElementById('modoEdicion').value = 'false';
    document.getElementById('divZona').style.display = 'block';
    modalInstance.show();
}

// Editar ruta
async function editarRuta(id) {
    try {
        const response = await fetch(`${API_URL}/rutas/${id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const ruta = await response.json();

            document.getElementById('modalTitle').innerHTML = '<i class="bi bi-pencil-fill me-2"></i>Editar Ruta';
            document.getElementById('rutaId').value = ruta.id;
            document.getElementById('modoEdicion').value = 'true';
            document.getElementById('nombreRuta').value = ruta.nombreRuta;
            document.getElementById('tipoRuta').value = ruta.tipoRuta;
            document.getElementById('zonaId').value = ruta.zonaId;

            // Ocultar zona en edición (no se puede cambiar)
            document.getElementById('divZona').style.display = 'none';

            modalInstance.show();
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al cargar la ruta', 'error');
    }
}

// Guardar ruta
async function guardarRuta() {
    const form = document.getElementById('formRuta');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const modoEdicion = document.getElementById('modoEdicion').value === 'true';
    const rutaId = document.getElementById('rutaId').value;

    // Construir DTO
    const dto = {
        nombreRuta: document.getElementById('nombreRuta').value.trim(),
        tipoRuta: document.getElementById('tipoRuta').value
    };

    // Agregar zonaId solo en creación
    if (!modoEdicion) {
        dto.zonaId = parseInt(document.getElementById('zonaId').value);
    }

    try {
        const url = modoEdicion ? `${API_URL}/rutas/${rutaId}` : `${API_URL}/rutas`;
        const method = modoEdicion ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            showToast(modoEdicion ? 'Ruta actualizada exitosamente' : 'Ruta creada exitosamente', 'success');
            modalInstance.hide();
            cargarRutas();
        } else {
            const error = await response.text();
            showToast('Error: ' + error, 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al guardar la ruta', 'error');
    }
}

// Ver detalle de ruta
async function verDetalle(id) {
    try {
        const response = await fetch(`${API_URL}/rutas/${id}/estadisticas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const ruta = await response.json();
            mostrarModalDetalle(ruta);
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al cargar el detalle', 'error');
    }
}

// Mostrar modal de detalle
function mostrarModalDetalle(ruta) {
    // Crear modal si no existe
    let modal = document.getElementById('modalDetalle');
    if (!modal) {
        modal = document.createElement('div');
        modal.className = 'modal fade';
        modal.id = 'modalDetalle';
        modal.innerHTML = `
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                        <h5 class="modal-title"><i class="bi bi-info-circle-fill me-2"></i>Detalle de Ruta</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body" id="modalDetalleContent"></div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(modal);
    }

    // Llenar contenido
    document.getElementById('modalDetalleContent').innerHTML = `
        <div class="row g-3">
            <div class="col-12">
                <h6 class="border-bottom pb-2" style="color: #667eea;">
                    <i class="bi bi-map-fill me-2"></i>Información de la Ruta
                </h6>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Nombre</label>
                <p class="fw-bold mb-0">${ruta.nombreRuta}</p>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Tipo de Recorrido</label>
                <p class="mb-0">
                    <span class="badge ${TIPO_RECORRIDO_BADGE_CLASS[ruta.tipoRuta]}">
                        ${TIPO_RECORRIDO_LABELS[ruta.tipoRuta]}
                    </span>
                </p>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Zona</label>
                <p class="mb-0">${ruta.nombreZona}</p>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Estado</label>
                <p class="mb-0">
                    <span class="badge ${ruta.activa ? 'bg-success' : 'bg-danger'}">
                        ${ruta.activa ? 'Activa' : 'Inactiva'}
                    </span>
                </p>
            </div>
            <div class="col-12 mt-3">
                <h6 class="border-bottom pb-2" style="color: #667eea;">
                    <i class="bi bi-bar-chart-fill me-2"></i>Estadísticas
                </h6>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Total de Estudiantes</label>
                <p class="fw-bold mb-0">
                    <span class="badge bg-info" style="font-size: 16px;">${ruta.totalEstudiantes || 0}</span>
                </p>
            </div>
        </div>
    `;

    // Mostrar modal
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}

// Cambiar estado (activar/desactivar)
async function cambiarEstado(id, activar) {
    if (!confirm(`¿Está seguro de ${activar ? 'activar' : 'desactivar'} esta ruta?`)) {
        return;
    }

    try {
        const endpoint = activar ? 'activar' : 'desactivar';
        const response = await fetch(`${API_URL}/rutas/${id}/${endpoint}`, {
            method: 'PATCH',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            showToast(`Ruta ${activar ? 'activada' : 'desactivada'} exitosamente`, 'success');
            cargarRutas();
        } else {
            showToast('Error al cambiar el estado de la ruta', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al cambiar el estado de la ruta', 'error');
    }
}

// Eliminar ruta
async function eliminarRuta(id) {
    if (!confirm('¿Está seguro de eliminar esta ruta? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/rutas/${id}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            showToast('Ruta eliminada exitosamente', 'success');
            cargarRutas();
        } else {
            showToast('Error al eliminar la ruta', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showToast('Error al eliminar la ruta', 'error');
    }
}

// Filtrar rutas
function filtrarRutas() {
    const zona = document.getElementById('filtroZona').value;
    const tipo = document.getElementById('filtroTipo').value;

    rutasFiltradas = rutasOriginales.filter(ruta => {
        const coincideZona = !zona || ruta.zonaId == zona;
        const coincideTipo = !tipo || ruta.tipoRuta === tipo;
        return coincideZona && coincideTipo;
    });

    buscarRutas();
}

// Buscar rutas
function buscarRutas() {
    const busqueda = document.getElementById('busqueda').value.toLowerCase();

    if (!busqueda) {
        mostrarRutas(rutasFiltradas);
        return;
    }

    const resultados = rutasFiltradas.filter(ruta =>
        ruta.nombreRuta.toLowerCase().includes(busqueda) ||
        ruta.nombreZona.toLowerCase().includes(busqueda)
    );

    mostrarRutas(resultados);
}

// Función global para logout
function logout() {
    Auth.logout();
}