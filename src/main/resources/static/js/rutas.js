// Verificar autenticación
Auth.requireAuth();

// Obtener usuario actual
const currentUser = Auth.getUser();

// Variables globales
let rutasOriginales = [];
let rutasFiltradas = [];
let zonas = [];
let colegios = [];
let jornadas = [];

// Modales
let modalCrear = null;
let modalEditar = null;

// Datos para generar nombre
let colegioSeleccionado = null;

// ==========================================
// INICIALIZACIÓN
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    loadNavbarUser();
    updateMenuByRole();
    inicializar();
});

async function inicializar() {
    // Verificar acceso - MONITOR no tiene acceso a rutas
    if (currentUser.rol === 'MONITOR') {
        mostrarAlertaError('Acceso Denegado', 'No tienes permisos para acceder a este módulo.');
        setTimeout(() => window.location.href = 'dashboard.html', 2000);
        return;
    }

    // Inicializar modales
    const modalCrearElement = document.getElementById('modalCrear');
    const modalEditarElement = document.getElementById('modalEditar');

    if (modalCrearElement) {
        modalCrear = new bootstrap.Modal(modalCrearElement);
    }
    if (modalEditarElement) {
        modalEditar = new bootstrap.Modal(modalEditarElement);
    }

    // Cargar datos
    await cargarZonas();
    await cargarRutas();
}

// ==========================================
// FUNCIONES DE ALERTA (SweetAlert2)
// ==========================================

function mostrarAlertaError(titulo, mensaje) {
    Swal.fire({
        icon: 'error',
        title: titulo,
        text: mensaje,
        confirmButtonColor: '#667eea'
    });
}

function mostrarAlertaExito(titulo, mensaje) {
    Swal.fire({
        icon: 'success',
        title: titulo,
        text: mensaje,
        confirmButtonColor: '#667eea',
        timer: 2000,
        timerProgressBar: true
    });
}

function mostrarAlertaAdvertencia(titulo, mensaje) {
    Swal.fire({
        icon: 'warning',
        title: titulo,
        text: mensaje,
        confirmButtonColor: '#667eea'
    });
}

// ==========================================
// CARGAR DATOS
// ==========================================

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
        mostrarAlertaError('Error', 'No se pudieron cargar las zonas.');
    }
}

function llenarSelectZonas() {
    const selectZonaCrear = document.getElementById('crearZonaId');
    const selectFiltroZona = document.getElementById('filtroZona');

    if (selectZonaCrear) {
        selectZonaCrear.innerHTML = '<option value="">Seleccione zona...</option>';
        zonas.forEach(zona => {
            selectZonaCrear.innerHTML += `<option value="${zona.id}">${zona.nombreZona}</option>`;
        });
    }

    if (selectFiltroZona) {
        selectFiltroZona.innerHTML = '<option value="">Todas</option>';
        zonas.forEach(zona => {
            selectFiltroZona.innerHTML += `<option value="${zona.id}">${zona.nombreZona}</option>`;
        });
    }
}

async function cargarColegiosPorZona() {
    const zonaId = document.getElementById('crearZonaId').value;
    const selectColegio = document.getElementById('crearColegioId');
    const selectJornada = document.getElementById('crearColegioJornadaId');

    // Resetear selects dependientes
    selectColegio.innerHTML = '<option value="">Cargando colegios...</option>';
    selectColegio.disabled = true;
    selectJornada.innerHTML = '<option value="">Primero seleccione colegio...</option>';
    selectJornada.disabled = true;

    // Resetear datos
    colegioSeleccionado = null;
    actualizarVistaPrevia();

    if (!zonaId) {
        selectColegio.innerHTML = '<option value="">Primero seleccione zona...</option>';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/colegios/zona/${zonaId}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            colegios = await response.json();

            if (colegios.length === 0) {
                selectColegio.innerHTML = '<option value="">No hay colegios en esta zona</option>';
            } else {
                selectColegio.innerHTML = '<option value="">Seleccione colegio...</option>';
                colegios.forEach(colegio => {
                    if (colegio.activo) {
                        selectColegio.innerHTML += `<option value="${colegio.id}">${colegio.nombreColegio}</option>`;
                    }
                });
                selectColegio.disabled = false;
            }
        }
    } catch (error) {
        console.error('Error al cargar colegios:', error);
        selectColegio.innerHTML = '<option value="">Error al cargar colegios</option>';
    }
}

async function cargarJornadasPorColegio() {
    const colegioId = document.getElementById('crearColegioId').value;
    const selectJornada = document.getElementById('crearColegioJornadaId');

    // Resetear select de jornada
    selectJornada.innerHTML = '<option value="">Cargando jornadas...</option>';
    selectJornada.disabled = true;

    // Guardar colegio seleccionado
    colegioSeleccionado = colegios.find(c => c.id == colegioId) || null;
    actualizarVistaPrevia();

    if (!colegioId) {
        selectJornada.innerHTML = '<option value="">Primero seleccione colegio...</option>';
        colegioSeleccionado = null;
        return;
    }

    try {
        const response = await fetch(`${API_URL}/colegio-jornadas/colegio/${colegioId}/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            jornadas = await response.json();

            if (jornadas.length === 0) {
                selectJornada.innerHTML = '<option value="">No hay jornadas para este colegio</option>';
            } else {
                selectJornada.innerHTML = '<option value="">Seleccione jornada...</option>';
                jornadas.forEach(cj => {
                    const nombreJornada = obtenerNombreJornada(cj);
                    selectJornada.innerHTML += `<option value="${cj.id}" data-nombre="${nombreJornada}">${nombreJornada}</option>`;
                });
                selectJornada.disabled = false;
            }
        }
    } catch (error) {
        console.error('Error al cargar jornadas:', error);
        selectJornada.innerHTML = '<option value="">Error al cargar jornadas</option>';
    }
}

// Obtener nombre de jornada desde el objeto ColegioJornada
function obtenerNombreJornada(colegioJornada) {
    if (colegioJornada.jornada) {
        // Si tiene el objeto jornada completo
        const tipo = colegioJornada.jornada.nombreJornada;
        if (JORNADA_LABELS && JORNADA_LABELS[tipo]) {
            return JORNADA_LABELS[tipo];
        }
        return tipo || 'Jornada';
    }
    // Si solo tiene el nombre directo
    if (colegioJornada.nombreJornada) {
        return JORNADA_LABELS[colegioJornada.nombreJornada] || colegioJornada.nombreJornada;
    }
    return 'Jornada';
}

// ==========================================
// VISTA PREVIA DEL NOMBRE
// ==========================================

function actualizarVistaPrevia() {
    const previewDiv = document.getElementById('previewNombre');
    const nombreSpan = document.getElementById('nombreGenerado');

    if (!previewDiv || !nombreSpan) return;

    const selectJornada = document.getElementById('crearColegioJornadaId');
    const tipoRuta = document.getElementById('crearTipoRuta').value;

    // Obtener jornada seleccionada
    const jornadaOption = selectJornada.options[selectJornada.selectedIndex];
    const nombreJornada = jornadaOption?.dataset?.nombre || null;

    if (colegioSeleccionado && nombreJornada && tipoRuta) {
        const nombreGenerado = `${colegioSeleccionado.nombreColegio} - ${nombreJornada} ${tipoRuta}`;
        nombreSpan.textContent = nombreGenerado;
        previewDiv.style.display = 'block';
    } else {
        nombreSpan.textContent = '-';
        previewDiv.style.display = 'none';
    }
}

// ==========================================
// CARGAR RUTAS
// ==========================================

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
            mostrarAlertaAdvertencia('Sesión Expirada', 'Por favor inicie sesión nuevamente.');
            Auth.logout();
        } else {
            showTableError('tableBody', 7);
        }
    } catch (error) {
        console.error('Error:', error);
        showTableError('tableBody', 7);
    }
}

// ==========================================
// MOSTRAR RUTAS EN TABLA
// ==========================================

function mostrarRutas(rutas) {
    const tbody = document.getElementById('tableBody');

    if (rutas.length === 0) {
        showTableEmpty('tableBody', 7);
        return;
    }

    tbody.innerHTML = rutas.map(ruta => `
        <tr>
            <td><span class="badge bg-secondary">${ruta.id}</span></td>
            <td><strong>${ruta.nombreRuta}</strong></td>
            <td>
                <span class="badge ${TIPO_RECORRIDO_BADGE_CLASS[ruta.tipoRuta] || 'bg-secondary'}">
                    ${TIPO_RECORRIDO_LABELS[ruta.tipoRuta] || ruta.tipoRuta}
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

// ==========================================
// MODAL CREAR
// ==========================================

function mostrarModalCrear() {
    // Resetear formulario
    document.getElementById('formCrear').reset();

    // Resetear selects
    document.getElementById('crearColegioId').innerHTML = '<option value="">Primero seleccione zona...</option>';
    document.getElementById('crearColegioId').disabled = true;
    document.getElementById('crearColegioJornadaId').innerHTML = '<option value="">Primero seleccione colegio...</option>';
    document.getElementById('crearColegioJornadaId').disabled = true;

    // Ocultar vista previa
    document.getElementById('previewNombre').style.display = 'none';

    // Resetear datos
    colegioSeleccionado = null;

    modalCrear.show();
}

// ==========================================
// EDITAR RUTA
// ==========================================

async function editarRuta(id) {
    try {
        const response = await fetch(`${API_URL}/rutas/${id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const ruta = await response.json();

            document.getElementById('editarRutaId').value = ruta.id;
            document.getElementById('editarNombreRuta').value = ruta.nombreRuta;
            document.getElementById('editarTipoRuta').value = ruta.tipoRuta;
            document.getElementById('editarZonaNombre').value = ruta.nombreZona || 'Sin zona';

            modalEditar.show();
        } else {
            mostrarAlertaError('Error', 'No se pudo cargar la información de la ruta.');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error al cargar la ruta.');
    }
}

// ==========================================
// VALIDACIONES
// ==========================================

function validarFormularioCrear() {
    const zonaId = document.getElementById('crearZonaId').value;
    const colegioId = document.getElementById('crearColegioId').value;
    const colegioJornadaId = document.getElementById('crearColegioJornadaId').value;
    const tipoRuta = document.getElementById('crearTipoRuta').value;

    if (!zonaId) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar una zona.');
        return false;
    }

    if (!colegioId) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar un colegio.');
        return false;
    }

    if (!colegioJornadaId) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar una jornada.');
        return false;
    }

    if (!tipoRuta) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar el tipo de recorrido.');
        return false;
    }

    return true;
}

function validarFormularioEditar() {
    const nombreRuta = document.getElementById('editarNombreRuta').value.trim();
    const tipoRuta = document.getElementById('editarTipoRuta').value;

    if (!nombreRuta || nombreRuta.length < 3) {
        mostrarAlertaError('Campo Requerido', 'El nombre de la ruta debe tener al menos 3 caracteres.');
        return false;
    }

    if (nombreRuta.length > 200) {
        mostrarAlertaError('Nombre Muy Largo', 'El nombre no puede exceder 200 caracteres.');
        return false;
    }

    if (!tipoRuta) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar el tipo de recorrido.');
        return false;
    }

    return true;
}

// ==========================================
// GUARDAR RUTA (CREAR)
// ==========================================

async function guardarRuta() {
    if (!validarFormularioCrear()) return;

    const dto = {
        colegioJornadaId: parseInt(document.getElementById('crearColegioJornadaId').value),
        tipoRuta: document.getElementById('crearTipoRuta').value
    };

    try {
        const response = await fetch(`${API_URL}/rutas`, {
            method: 'POST',
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            mostrarAlertaExito('¡Ruta Creada!', 'La ruta se creó correctamente.');
            modalCrear.hide();
            cargarRutas();
        } else {
            const errorText = await response.text();
            if (errorText.toLowerCase().includes('ya existe')) {
                mostrarAlertaError('Ruta Duplicada', 'Ya existe esta ruta. Verifique el colegio, jornada y tipo de recorrido.');
            } else {
                mostrarAlertaError('Error', errorText || 'No se pudo crear la ruta.');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al crear la ruta.');
    }
}

// ==========================================
// ACTUALIZAR RUTA (EDITAR)
// ==========================================

async function actualizarRuta() {
    if (!validarFormularioEditar()) return;

    const rutaId = document.getElementById('editarRutaId').value;
    const dto = {
        nombreRuta: document.getElementById('editarNombreRuta').value.trim(),
        tipoRuta: document.getElementById('editarTipoRuta').value
    };

    try {
        const response = await fetch(`${API_URL}/rutas/${rutaId}`, {
            method: 'PUT',
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            mostrarAlertaExito('¡Ruta Actualizada!', 'La ruta se actualizó correctamente.');
            modalEditar.hide();
            cargarRutas();
        } else {
            const errorText = await response.text();
            if (errorText.toLowerCase().includes('ya existe')) {
                mostrarAlertaError('Ruta Duplicada', 'Ya existe una ruta con ese nombre en esta zona.');
            } else {
                mostrarAlertaError('Error', errorText || 'No se pudo actualizar la ruta.');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al actualizar la ruta.');
    }
}

// ==========================================
// VER DETALLE
// ==========================================

async function verDetalle(id) {
    try {
        const response = await fetch(`${API_URL}/rutas/${id}/estadisticas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const ruta = await response.json();

            Swal.fire({
                title: `<i class="bi bi-map-fill me-2"></i>${ruta.nombreRuta}`,
                html: `
                    <div class="text-start">
                        <div class="row g-3">
                            <div class="col-6">
                                <label class="text-muted small d-block">Tipo de Recorrido</label>
                                <span class="badge ${TIPO_RECORRIDO_BADGE_CLASS[ruta.tipoRuta] || 'bg-secondary'}" style="font-size: 14px;">
                                    ${TIPO_RECORRIDO_LABELS[ruta.tipoRuta] || ruta.tipoRuta}
                                </span>
                            </div>
                            <div class="col-6">
                                <label class="text-muted small d-block">Estado</label>
                                <span class="badge ${ruta.activa ? 'bg-success' : 'bg-danger'}" style="font-size: 14px;">
                                    ${ruta.activa ? 'Activa' : 'Inactiva'}
                                </span>
                            </div>
                            <div class="col-6">
                                <label class="text-muted small d-block">Zona</label>
                                <p class="fw-bold mb-0">${ruta.nombreZona || '-'}</p>
                            </div>
                            <div class="col-6">
                                <label class="text-muted small d-block">Total Estudiantes</label>
                                <span class="badge bg-info" style="font-size: 18px;">${ruta.totalEstudiantes || 0}</span>
                            </div>
                        </div>
                    </div>
                `,
                confirmButtonColor: '#667eea',
                confirmButtonText: 'Cerrar',
                width: 500
            });
        } else {
            mostrarAlertaError('Error', 'No se pudo cargar el detalle de la ruta.');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error al cargar el detalle.');
    }
}

// ==========================================
// CAMBIAR ESTADO (ACTIVAR/DESACTIVAR)
// ==========================================

async function cambiarEstado(id, activar) {
    const ruta = rutasOriginales.find(r => r.id === id);
    const nombreRuta = ruta ? ruta.nombreRuta : 'esta ruta';

    const result = await Swal.fire({
        title: activar ? '¿Activar Ruta?' : '¿Desactivar Ruta?',
        html: `¿Está seguro de ${activar ? 'activar' : 'desactivar'} la ruta <strong>"${nombreRuta}"</strong>?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: activar ? '#28a745' : '#6c757d',
        cancelButtonColor: '#dc3545',
        confirmButtonText: activar ? '<i class="bi bi-toggle-on me-1"></i>Sí, Activar' : '<i class="bi bi-toggle-off me-1"></i>Sí, Desactivar',
        cancelButtonText: 'Cancelar'
    });

    if (!result.isConfirmed) return;

    try {
        const endpoint = activar ? 'activar' : 'desactivar';
        const response = await fetch(`${API_URL}/rutas/${id}/${endpoint}`, {
            method: 'PATCH',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            mostrarAlertaExito(
                activar ? '¡Ruta Activada!' : '¡Ruta Desactivada!',
                `La ruta "${nombreRuta}" ha sido ${activar ? 'activada' : 'desactivada'} correctamente.`
            );
            cargarRutas();
        } else {
            const errorText = await response.text();
            mostrarAlertaError('Error', errorText || `No se pudo ${activar ? 'activar' : 'desactivar'} la ruta.`);
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', `Error de conexión al ${activar ? 'activar' : 'desactivar'} la ruta.`);
    }
}

// ==========================================
// ELIMINAR RUTA
// ==========================================

async function eliminarRuta(id) {
    const ruta = rutasOriginales.find(r => r.id === id);
    const nombreRuta = ruta ? ruta.nombreRuta : 'esta ruta';
    const totalEstudiantes = ruta ? (ruta.totalEstudiantes || 0) : 0;

    let advertenciaEstudiantes = '';
    if (totalEstudiantes > 0) {
        advertenciaEstudiantes = `<br><br><span class="text-danger"><i class="bi bi-exclamation-triangle-fill me-1"></i>Esta ruta tiene <strong>${totalEstudiantes} estudiante(s)</strong> asignado(s).</span>`;
    }

    const result = await Swal.fire({
        title: '¿Eliminar Ruta?',
        html: `¿Está seguro de eliminar la ruta <strong>"${nombreRuta}"</strong>?${advertenciaEstudiantes}<br><br><small class="text-muted">Esta acción no se puede deshacer.</small>`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '<i class="bi bi-trash-fill me-1"></i>Sí, Eliminar',
        cancelButtonText: 'Cancelar'
    });

    if (!result.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/rutas/${id}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            mostrarAlertaExito('¡Ruta Eliminada!', `La ruta "${nombreRuta}" ha sido eliminada correctamente.`);
            cargarRutas();
        } else {
            const errorText = await response.text();

            if (errorText.toLowerCase().includes('estudiantes') || errorText.toLowerCase().includes('asignados')) {
                mostrarAlertaError('No se puede eliminar', 'La ruta tiene estudiantes asignados. Debe reasignarlos antes de eliminar.');
            } else {
                mostrarAlertaError('Error', errorText || 'No se pudo eliminar la ruta.');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al eliminar la ruta.');
    }
}

// ==========================================
// FILTROS Y BÚSQUEDA
// ==========================================

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

function buscarRutas() {
    const busqueda = document.getElementById('busqueda').value.toLowerCase().trim();

    if (!busqueda) {
        mostrarRutas(rutasFiltradas);
        return;
    }

    const resultados = rutasFiltradas.filter(ruta =>
        ruta.nombreRuta.toLowerCase().includes(busqueda) ||
        (ruta.nombreZona && ruta.nombreZona.toLowerCase().includes(busqueda))
    );

    mostrarRutas(resultados);
}

// ==========================================
// LOGOUT
// ==========================================

function logout() {
    Auth.logout();
}