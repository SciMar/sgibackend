// Verificar autenticación
Auth.requireAuth();

// Obtener usuario actual
const currentUser = Auth.getUser();

// Variables globales
let colegiosOriginales = [];
let colegiosFiltrados = [];
let zonas = [];
let jornadasDisponibles = [];

// Modales
let modalCrear = null;
let modalEditar = null;
let modalJornadas = null;

// ==========================================
// INICIALIZACIÓN
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    loadNavbarUser();
    updateMenuByRole();
    inicializar();
});

async function inicializar() {
    // Verificar acceso - solo ADMINISTRADOR y ENCARGADO
    if (currentUser.rol === 'MONITOR') {
        mostrarAlertaError('Acceso Denegado', 'No tienes permisos para acceder a este módulo.');
        setTimeout(() => window.location.href = 'dashboard.html', 2000);
        return;
    }

    // Inicializar modales
    const modalCrearEl = document.getElementById('modalCrear');
    const modalEditarEl = document.getElementById('modalEditar');
    const modalJornadasEl = document.getElementById('modalJornadas');

    if (modalCrearEl) modalCrear = new bootstrap.Modal(modalCrearEl);
    if (modalEditarEl) modalEditar = new bootstrap.Modal(modalEditarEl);
    if (modalJornadasEl) modalJornadas = new bootstrap.Modal(modalJornadasEl);

    // Cargar datos
    await cargarZonas();
    await cargarColegios();
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
    }
}

function llenarSelectZonas() {
    const selectCrear = document.getElementById('crearZonaId');
    const selectFiltro = document.getElementById('filtroZona');

    if (selectCrear) {
        selectCrear.innerHTML = '<option value="">Seleccione zona...</option>';
        zonas.forEach(zona => {
            selectCrear.innerHTML += `<option value="${zona.id}">${zona.nombreZona}</option>`;
        });
    }

    if (selectFiltro) {
        selectFiltro.innerHTML = '<option value="">Todas</option>';
        zonas.forEach(zona => {
            selectFiltro.innerHTML += `<option value="${zona.id}">${zona.nombreZona}</option>`;
        });
    }
}

async function cargarColegios() {
    showTableLoading('tableBody', 7);

    try {
        const response = await fetch(`${API_URL}/colegios`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            colegiosOriginales = await response.json();
            colegiosFiltrados = [...colegiosOriginales];
            mostrarColegios(colegiosFiltrados);
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
// MOSTRAR COLEGIOS EN TABLA
// ==========================================

function mostrarColegios(colegios) {
    const tbody = document.getElementById('tableBody');

    if (colegios.length === 0) {
        showTableEmpty('tableBody', 7);
        return;
    }

    tbody.innerHTML = colegios.map(colegio => `
        <tr>
            <td><span class="badge bg-secondary">${colegio.id}</span></td>
            <td><strong>${colegio.nombreColegio}</strong></td>
            <td>${colegio.nombreZona || '-'}</td>
            <td>
                <div class="jornadas-container">
                    ${renderizarJornadas(colegio.jornadas)}
                </div>
            </td>
            <td>
                <span class="badge bg-info">${colegio.totalEstudiantes || 0}</span>
            </td>
            <td>
                <span class="badge ${colegio.activo ? 'bg-success' : 'bg-danger'}">
                    ${colegio.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary" onclick="verDetalle(${colegio.id})" title="Ver detalle">
                        <i class="bi bi-eye-fill"></i>
                    </button>
                    <button class="btn btn-outline-info" onclick="gestionarJornadas(${colegio.id})" title="Gestionar jornadas">
                        <i class="bi bi-clock-fill"></i>
                    </button>
                    <button class="btn btn-outline-warning" onclick="editarColegio(${colegio.id})" title="Editar">
                        <i class="bi bi-pencil-fill"></i>
                    </button>
                    ${colegio.activo ?
                        `<button class="btn btn-outline-secondary" onclick="cambiarEstado(${colegio.id}, false)" title="Desactivar">
                            <i class="bi bi-toggle-on"></i>
                        </button>` :
                        `<button class="btn btn-outline-success" onclick="cambiarEstado(${colegio.id}, true)" title="Activar">
                            <i class="bi bi-toggle-off"></i>
                        </button>`
                    }
                    ${currentUser.rol === 'ADMINISTRADOR' ?
                        `<button class="btn btn-outline-danger" onclick="eliminarColegio(${colegio.id})" title="Eliminar">
                            <i class="bi bi-trash-fill"></i>
                        </button>` : ''
                    }
                </div>
            </td>
        </tr>
    `).join('');
}

function renderizarJornadas(jornadas) {
    if (!jornadas || jornadas.length === 0) {
        return '<span class="text-muted small">Sin jornadas</span>';
    }

    return jornadas.map(j => {
        const nombre = JORNADA_LABELS[j.nombre] || j.nombre;
        const badgeClass = obtenerClaseJornada(j.nombre);
        return `<span class="badge ${badgeClass} jornada-badge">${nombre}</span>`;
    }).join('');
}

function obtenerClaseJornada(tipo) {
    const clases = {
        'MANANA': 'bg-warning text-dark',
        'TARDE': 'bg-info',
        'UNICA': 'bg-primary'
    };
    return clases[tipo] || 'bg-secondary';
}

// ==========================================
// MODAL CREAR
// ==========================================

function mostrarModalCrear() {
    document.getElementById('formCrear').reset();
    document.getElementById('divJornadasCrear').style.display = 'none';
    document.getElementById('checkboxesJornadas').innerHTML = '<p class="text-muted mb-0">Seleccione una zona primero...</p>';
    modalCrear.show();
}

// Cargar jornadas disponibles cuando se selecciona zona en crear
async function cargarJornadasParaCrear() {
    const zonaId = document.getElementById('crearZonaId').value;
    const divJornadas = document.getElementById('divJornadasCrear');
    const container = document.getElementById('checkboxesJornadas');

    if (!zonaId) {
        divJornadas.style.display = 'none';
        container.innerHTML = '<p class="text-muted mb-0">Seleccione una zona primero...</p>';
        return;
    }

    container.innerHTML = '<p class="text-muted mb-0">Cargando jornadas...</p>';
    divJornadas.style.display = 'block';

    try {
        const response = await fetch(`${API_URL}/jornadas/zona/${zonaId}/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const jornadas = await response.json();

            if (jornadas.length === 0) {
                container.innerHTML = '<p class="text-muted mb-0">No hay jornadas disponibles en esta zona.</p>';
                return;
            }

            container.innerHTML = jornadas.map(jornada => {
                const nombre = JORNADA_LABELS[jornada.nombreJornada] || jornada.nombreJornada;
                const badgeClass = obtenerClaseJornada(jornada.nombreJornada);
                return `
                    <div class="form-check mb-2">
                        <input class="form-check-input jornada-checkbox" type="checkbox"
                               value="${jornada.id}" id="jornada_${jornada.id}">
                        <label class="form-check-label" for="jornada_${jornada.id}">
                            <span class="badge ${badgeClass}">${nombre}</span>
                        </label>
                    </div>
                `;
            }).join('');
        } else {
            container.innerHTML = '<p class="text-danger mb-0">Error al cargar jornadas.</p>';
        }
    } catch (error) {
        console.error('Error:', error);
        container.innerHTML = '<p class="text-danger mb-0">Error al cargar jornadas.</p>';
    }
}

// ==========================================
// VALIDACIONES
// ==========================================

function validarNombreColegio(nombre) {
    if (!nombre || nombre.trim() === '') {
        mostrarAlertaError('Campo Requerido', 'El nombre del colegio es obligatorio.');
        return false;
    }

    if (nombre.trim().length < 3) {
        mostrarAlertaError('Nombre Muy Corto', 'El nombre debe tener al menos 3 caracteres.');
        return false;
    }

    if (nombre.trim().length > 200) {
        mostrarAlertaError('Nombre Muy Largo', 'El nombre no puede exceder 200 caracteres.');
        return false;
    }

    return true;
}

// ==========================================
// GUARDAR COLEGIO (CREAR)
// ==========================================

async function guardarColegio() {
    const nombreColegio = document.getElementById('crearNombreColegio').value;
    const zonaId = document.getElementById('crearZonaId').value;

    if (!validarNombreColegio(nombreColegio)) return;

    if (!zonaId) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar una zona.');
        return;
    }

    // Obtener jornadas seleccionadas
    const jornadasSeleccionadas = [];
    document.querySelectorAll('.jornada-checkbox:checked').forEach(cb => {
        jornadasSeleccionadas.push(parseInt(cb.value));
    });

    const dto = {
        nombreColegio: nombreColegio.trim(),
        zonaId: parseInt(zonaId)
    };

    try {
        // 1. Crear el colegio
        const response = await fetch(`${API_URL}/colegios`, {
            method: 'POST',
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            const colegioCreado = await response.json();

            // 2. Asignar jornadas si hay seleccionadas
            if (jornadasSeleccionadas.length > 0) {
                try {
                    await fetch(`${API_URL}/colegio-jornadas/colegio/${colegioCreado.id}/asignar-varias`, {
                        method: 'POST',
                        headers: Auth.getHeaders(),
                        body: JSON.stringify(jornadasSeleccionadas)
                    });
                } catch (error) {
                    console.error('Error al asignar jornadas:', error);
                    // No mostramos error porque el colegio ya se creó
                }
            }

            mostrarAlertaExito('¡Colegio Creado!', 'El colegio se creó correctamente.');
            modalCrear.hide();
            cargarColegios();
        } else {
            const errorText = await response.text();
            if (errorText.toLowerCase().includes('ya existe')) {
                mostrarAlertaError('Colegio Duplicado', 'Ya existe un colegio con ese nombre en esta zona.');
            } else {
                mostrarAlertaError('Error', errorText || 'No se pudo crear el colegio.');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al crear el colegio.');
    }
}

// ==========================================
// EDITAR COLEGIO
// ==========================================

async function editarColegio(id) {
    try {
        const response = await fetch(`${API_URL}/colegios/${id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const colegio = await response.json();

            document.getElementById('editarColegioId').value = colegio.id;
            document.getElementById('editarNombreColegio').value = colegio.nombreColegio;
            document.getElementById('editarZonaNombre').value = colegio.nombreZona || 'Sin zona';

            modalEditar.show();
        } else {
            mostrarAlertaError('Error', 'No se pudo cargar la información del colegio.');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error al cargar el colegio.');
    }
}

// ==========================================
// ACTUALIZAR COLEGIO
// ==========================================

async function actualizarColegio() {
    const colegioId = document.getElementById('editarColegioId').value;
    const nombreColegio = document.getElementById('editarNombreColegio').value;

    if (!validarNombreColegio(nombreColegio)) return;

    const dto = {
        nombreColegio: nombreColegio.trim()
    };

    try {
        const response = await fetch(`${API_URL}/colegios/${colegioId}`, {
            method: 'PUT',
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            mostrarAlertaExito('¡Colegio Actualizado!', 'El colegio se actualizó correctamente.');
            modalEditar.hide();
            cargarColegios();
        } else {
            const errorText = await response.text();
            if (errorText.toLowerCase().includes('ya existe')) {
                mostrarAlertaError('Nombre Duplicado', 'Ya existe otro colegio con ese nombre en esta zona.');
            } else {
                mostrarAlertaError('Error', errorText || 'No se pudo actualizar el colegio.');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al actualizar el colegio.');
    }
}

// ==========================================
// VER DETALLE
// ==========================================

async function verDetalle(id) {
    try {
        const response = await fetch(`${API_URL}/colegios/${id}/estadisticas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const colegio = await response.json();

            Swal.fire({
                title: `<i class="bi bi-building-fill me-2"></i>${colegio.nombreColegio}`,
                html: `
                    <div class="text-start">
                        <div class="row g-3">
                            <div class="col-6">
                                <label class="text-muted small d-block">Zona</label>
                                <p class="fw-bold mb-0">${colegio.nombreZona || '-'}</p>
                            </div>
                            <div class="col-6">
                                <label class="text-muted small d-block">Estado</label>
                                <span class="badge ${colegio.activo ? 'bg-success' : 'bg-danger'}" style="font-size: 14px;">
                                    ${colegio.activo ? 'Activo' : 'Inactivo'}
                                </span>
                            </div>
                            <div class="col-12">
                                <label class="text-muted small d-block">Jornadas</label>
                                <div class="mt-1">
                                    ${renderizarJornadas(colegio.jornadas) || '<span class="text-muted">Sin jornadas asignadas</span>'}
                                </div>
                            </div>
                            <div class="col-6">
                                <label class="text-muted small d-block">Total Estudiantes</label>
                                <span class="badge bg-info" style="font-size: 18px;">${colegio.totalEstudiantes || 0}</span>
                            </div>
                        </div>
                    </div>
                `,
                confirmButtonColor: '#667eea',
                confirmButtonText: 'Cerrar',
                width: 500
            });
        } else {
            mostrarAlertaError('Error', 'No se pudo cargar el detalle del colegio.');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error al cargar el detalle.');
    }
}

// ==========================================
// GESTIONAR JORNADAS
// ==========================================

async function gestionarJornadas(colegioId) {
    const colegio = colegiosOriginales.find(c => c.id === colegioId);
    if (!colegio) {
        mostrarAlertaError('Error', 'Colegio no encontrado.');
        return;
    }

    document.getElementById('jornadasColegioId').value = colegioId;
    document.getElementById('jornadasColegioNombre').textContent = colegio.nombreColegio;

    // Cargar jornadas disponibles para la zona del colegio
    await cargarJornadasDisponibles(colegio.zonaId);

    // Cargar jornadas asignadas al colegio
    await cargarJornadasAsignadas(colegioId);

    modalJornadas.show();
}

async function cargarJornadasDisponibles(zonaId) {
    const select = document.getElementById('selectNuevaJornada');
    select.innerHTML = '<option value="">Cargando jornadas...</option>';

    try {
        // Cargar todas las jornadas de la zona
        const response = await fetch(`${API_URL}/jornadas/zona/${zonaId}/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            jornadasDisponibles = await response.json();

            select.innerHTML = '<option value="">Seleccione jornada para agregar...</option>';
            jornadasDisponibles.forEach(jornada => {
                const nombre = JORNADA_LABELS[jornada.nombreJornada] || jornada.nombreJornada;
                select.innerHTML += `<option value="${jornada.id}">${nombre}</option>`;
            });
        } else {
            select.innerHTML = '<option value="">Error al cargar jornadas</option>';
        }
    } catch (error) {
        console.error('Error al cargar jornadas:', error);
        select.innerHTML = '<option value="">Error al cargar jornadas</option>';
    }
}

async function cargarJornadasAsignadas(colegioId) {
    const container = document.getElementById('listaJornadasAsignadas');
    container.innerHTML = '<p class="text-muted text-center">Cargando...</p>';

    try {
        const response = await fetch(`${API_URL}/colegio-jornadas/colegio/${colegioId}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const jornadasAsignadas = await response.json();

            if (jornadasAsignadas.length === 0) {
                container.innerHTML = '<p class="text-muted text-center">No hay jornadas asignadas a este colegio.</p>';
                return;
            }

            container.innerHTML = jornadasAsignadas.map(cj => {
                const nombre = JORNADA_LABELS[cj.nombreJornada] || cj.nombreJornada;
                const estadoClass = cj.activa ? 'active' : 'inactive';
                const estadoBadge = cj.activa ?
                    '<span class="badge bg-success">Activa</span>' :
                    '<span class="badge bg-danger">Inactiva</span>';

                return `
                    <div class="jornada-item ${estadoClass}">
                        <div>
                            <strong>${nombre}</strong>
                            ${estadoBadge}
                        </div>
                        <div class="btn-group btn-group-sm">
                            ${cj.activa ?
                                `<button class="btn btn-outline-secondary btn-sm" onclick="toggleJornada(${cj.id}, false)" title="Desactivar">
                                    <i class="bi bi-toggle-on"></i>
                                </button>` :
                                `<button class="btn btn-outline-success btn-sm" onclick="toggleJornada(${cj.id}, true)" title="Activar">
                                    <i class="bi bi-toggle-off"></i>
                                </button>`
                            }
                            <button class="btn btn-outline-danger btn-sm" onclick="quitarJornada(${cj.id})" title="Quitar">
                                <i class="bi bi-trash-fill"></i>
                            </button>
                        </div>
                    </div>
                `;
            }).join('');
        } else {
            container.innerHTML = '<p class="text-danger text-center">Error al cargar jornadas</p>';
        }
    } catch (error) {
        console.error('Error:', error);
        container.innerHTML = '<p class="text-danger text-center">Error al cargar jornadas</p>';
    }
}

async function agregarJornada() {
    const colegioId = document.getElementById('jornadasColegioId').value;
    const jornadaId = document.getElementById('selectNuevaJornada').value;

    if (!jornadaId) {
        mostrarAlertaError('Selección Requerida', 'Debe seleccionar una jornada para agregar.');
        return;
    }

    const dto = {
        colegioId: parseInt(colegioId),
        jornadaId: parseInt(jornadaId)
    };

    try {
        const response = await fetch(`${API_URL}/colegio-jornadas`, {
            method: 'POST',
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            mostrarAlertaExito('¡Jornada Asignada!', 'La jornada se asignó correctamente al colegio.');
            document.getElementById('selectNuevaJornada').value = '';
            await cargarJornadasAsignadas(colegioId);
            cargarColegios(); // Actualizar tabla principal
        } else {
            const errorText = await response.text();
            if (errorText.toLowerCase().includes('ya existe') || errorText.toLowerCase().includes('asignada')) {
                mostrarAlertaError('Ya Asignada', 'Esta jornada ya está asignada a este colegio.');
            } else {
                mostrarAlertaError('Error', errorText || 'No se pudo asignar la jornada.');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al asignar la jornada.');
    }
}

async function toggleJornada(colegioJornadaId, activar) {
    try {
        const endpoint = activar ? 'activar' : 'desactivar';
        const response = await fetch(`${API_URL}/colegio-jornadas/${colegioJornadaId}/${endpoint}`, {
            method: 'PATCH',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const colegioId = document.getElementById('jornadasColegioId').value;
            await cargarJornadasAsignadas(colegioId);
        } else {
            mostrarAlertaError('Error', `No se pudo ${activar ? 'activar' : 'desactivar'} la jornada.`);
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión.');
    }
}

async function quitarJornada(colegioJornadaId) {
    const result = await Swal.fire({
        title: '¿Quitar Jornada?',
        text: '¿Está seguro de quitar esta jornada del colegio?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '<i class="bi bi-trash-fill me-1"></i>Sí, Quitar',
        cancelButtonText: 'Cancelar'
    });

    if (!result.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/colegio-jornadas/${colegioJornadaId}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            mostrarAlertaExito('¡Jornada Quitada!', 'La jornada se quitó del colegio.');
            const colegioId = document.getElementById('jornadasColegioId').value;
            await cargarJornadasAsignadas(colegioId);
            cargarColegios(); // Actualizar tabla principal
        } else {
            const errorText = await response.text();
            mostrarAlertaError('Error', errorText || 'No se pudo quitar la jornada.');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al quitar la jornada.');
    }
}

// ==========================================
// CAMBIAR ESTADO (ACTIVAR/DESACTIVAR)
// ==========================================

async function cambiarEstado(id, activar) {
    const colegio = colegiosOriginales.find(c => c.id === id);
    const nombreColegio = colegio ? colegio.nombreColegio : 'este colegio';

    const result = await Swal.fire({
        title: activar ? '¿Activar Colegio?' : '¿Desactivar Colegio?',
        html: `¿Está seguro de ${activar ? 'activar' : 'desactivar'} el colegio <strong>"${nombreColegio}"</strong>?`,
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
        const response = await fetch(`${API_URL}/colegios/${id}/${endpoint}`, {
            method: 'PATCH',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            mostrarAlertaExito(
                activar ? '¡Colegio Activado!' : '¡Colegio Desactivado!',
                `El colegio "${nombreColegio}" ha sido ${activar ? 'activado' : 'desactivado'} correctamente.`
            );
            cargarColegios();
        } else {
            const errorText = await response.text();
            mostrarAlertaError('Error', errorText || `No se pudo ${activar ? 'activar' : 'desactivar'} el colegio.`);
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', `Error de conexión al ${activar ? 'activar' : 'desactivar'} el colegio.`);
    }
}

// ==========================================
// ELIMINAR COLEGIO
// ==========================================

async function eliminarColegio(id) {
    const colegio = colegiosOriginales.find(c => c.id === id);
    const nombreColegio = colegio ? colegio.nombreColegio : 'este colegio';
    const totalEstudiantes = colegio ? (colegio.totalEstudiantes || 0) : 0;

    let advertencia = '';
    if (totalEstudiantes > 0) {
        advertencia = `<br><br><span class="text-danger"><i class="bi bi-exclamation-triangle-fill me-1"></i>Este colegio tiene <strong>${totalEstudiantes} estudiante(s)</strong> registrado(s).</span>`;
    }

    const result = await Swal.fire({
        title: '¿Eliminar Colegio?',
        html: `¿Está seguro de eliminar el colegio <strong>"${nombreColegio}"</strong>?${advertencia}<br><br><small class="text-muted">Esta acción no se puede deshacer.</small>`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '<i class="bi bi-trash-fill me-1"></i>Sí, Eliminar',
        cancelButtonText: 'Cancelar'
    });

    if (!result.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/colegios/${id}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            mostrarAlertaExito('¡Colegio Eliminado!', `El colegio "${nombreColegio}" ha sido eliminado correctamente.`);
            cargarColegios();
        } else {
            const errorText = await response.text();
            if (errorText.toLowerCase().includes('estudiantes')) {
                mostrarAlertaError('No se puede eliminar', 'El colegio tiene estudiantes registrados. Debe reasignarlos primero.');
            } else {
                mostrarAlertaError('Error', errorText || 'No se pudo eliminar el colegio.');
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error', 'Error de conexión al eliminar el colegio.');
    }
}

// ==========================================
// FILTROS Y BÚSQUEDA
// ==========================================

function filtrarColegios() {
    const zona = document.getElementById('filtroZona').value;
    const estado = document.getElementById('filtroEstado').value;

    colegiosFiltrados = colegiosOriginales.filter(colegio => {
        const coincideZona = !zona || colegio.zonaId == zona;
        const coincideEstado = estado === '' || colegio.activo.toString() === estado;
        return coincideZona && coincideEstado;
    });

    buscarColegios();
}

function buscarColegios() {
    const busqueda = document.getElementById('busqueda').value.toLowerCase().trim();

    if (!busqueda) {
        mostrarColegios(colegiosFiltrados);
        return;
    }

    const resultados = colegiosFiltrados.filter(colegio =>
        colegio.nombreColegio.toLowerCase().includes(busqueda) ||
        (colegio.nombreZona && colegio.nombreZona.toLowerCase().includes(busqueda))
    );

    mostrarColegios(resultados);
}

// ==========================================
// LOGOUT
// ==========================================

function logout() {
    Auth.logout();
}