// Lógica de Gestión de Estudiantes

// Verificar autenticación y permisos
Auth.requireAuth();
Auth.requireRoles(['ADMINISTRADOR', 'ENCARGADO', 'MONITOR']);

// Variables globales
let estudiantesOriginales = [];
let estudiantesFiltrados = [];
let colegios = [];
let jornadas = [];
let rutas = [];
let modalInstance;
const currentUser = Auth.getUser();

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    loadNavbarUser();
    configurarMenuSegunRol();
    modalInstance = new bootstrap.Modal(document.getElementById('modalEstudiante'));
    cargarDatosIniciales();
});

// Configurar menú según rol
function configurarMenuSegunRol() {
    const rol = currentUser.rol;

    if (rol === 'ADMINISTRADOR') {
        document.getElementById('menuUsuarios').style.display = 'block';
        document.getElementById('menuRutas').style.display = 'block';
        document.getElementById('menuColegios').style.display = 'block';
        document.getElementById('menuReportes').style.display = 'block';
    } else if (rol === 'ENCARGADO') {
        document.getElementById('menuRutas').style.display = 'block';
        document.getElementById('menuColegios').style.display = 'block';
        document.getElementById('menuReportes').style.display = 'block';
    } else if (rol === 'MONITOR') {
        // MONITOR: Solo ve estudiantes y asistencias (sin botón crear)
        const btnCrear = document.getElementById('btnCrear');
        if (btnCrear) btnCrear.style.display = 'none';
    }
}

// Cargar datos iniciales
async function cargarDatosIniciales() {
    await Promise.all([
        cargarEstudiantes(),
        cargarColegios(),
        cargarRutas()
    ]);
}

// Cargar estudiantes
async function cargarEstudiantes() {
    showTableLoading('tableBody', 8);

    try {
        let url = `${API_URL}/estudiantes`;

        // Si es MONITOR, primero obtener su zona y jornada
        if (currentUser.rol === 'MONITOR') {
            const monitorData = await obtenerDatosMonitor();
            if (monitorData && monitorData.zonaId && monitorData.jornadaId) {
                url = `${API_URL}/estudiantes/monitor/zona/${monitorData.zonaId}/jornada/${monitorData.jornadaId}`;
            }
        }

        const response = await fetch(url, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            estudiantesOriginales = await response.json();
            estudiantesFiltrados = [...estudiantesOriginales];
            mostrarEstudiantes(estudiantesFiltrados);

            // Si es MONITOR y no hay colegios cargados, llenar filtros desde estudiantes
            if (currentUser.rol === 'MONITOR' && colegios.length === 0) {
                llenarFiltrosDesdeEstudiantes();
            }
        } else if (response.status === 401) {
            alert('Sesión expirada. Por favor inicie sesión nuevamente.');
            Auth.logout();
        } else {
            showTableError('tableBody', 8);
        }
    } catch (error) {
        console.error('Error:', error);
        showTableError('tableBody', 8);
    }
}

// Obtener datos del monitor (zona y jornada)
async function obtenerDatosMonitor() {
    try {
        const response = await fetch(`${API_URL}/monitores/usuario/${currentUser.id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            return await response.json();
        }
        return null;
    } catch (error) {
        console.error('Error al obtener datos del monitor:', error);
        return null;
    }
}

// Cargar colegios
async function cargarColegios() {
    try {
        const response = await fetch(`${API_URL}/colegios`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            colegios = await response.json();
            llenarSelectColegios();
        }
    } catch (error) {
        console.error('Error al cargar colegios:', error);
    }
}

// Cargar rutas
async function cargarRutas() {
    try {
        const response = await fetch(`${API_URL}/rutas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            rutas = await response.json();
            llenarSelectRutas();
        }
    } catch (error) {
        console.error('Error al cargar rutas:', error);
    }
}

// Llenar select de colegios
function llenarSelectColegios() {
    const selectColegio = document.getElementById('colegioId');
    const selectFiltroColegio = document.getElementById('filtroColegio');

    selectColegio.innerHTML = '<option value="">Seleccione...</option>';
    selectFiltroColegio.innerHTML = '<option value="">Todos</option>';

    colegios.forEach(colegio => {
        const option = `<option value="${colegio.id}">${colegio.nombreColegio}</option>`;
        selectColegio.innerHTML += option;
        selectFiltroColegio.innerHTML += option;
    });

    // Si es MONITOR y no hay colegios (sin permisos), llenar desde estudiantes
    if (currentUser.rol === 'MONITOR' && colegios.length === 0 && estudiantesOriginales.length > 0) {
        llenarFiltrosDesdeEstudiantes();
    }
}

// Llenar filtros desde los estudiantes (para MONITOR)
function llenarFiltrosDesdeEstudiantes() {
    const selectFiltroColegio = document.getElementById('filtroColegio');

    // Extraer colegios únicos de los estudiantes
    const colegiosUnicos = {};
    estudiantesOriginales.forEach(e => {
        if (e.colegioId && !colegiosUnicos[e.colegioId]) {
            colegiosUnicos[e.colegioId] = e.nombreColegio;
        }
    });

    // Llenar select de filtro
    selectFiltroColegio.innerHTML = '<option value="">Todos</option>';
    Object.keys(colegiosUnicos).forEach(colegioId => {
        selectFiltroColegio.innerHTML += `<option value="${colegioId}">${colegiosUnicos[colegioId]}</option>`;
    });

    console.log('Filtros cargados desde estudiantes para MONITOR');
}

// Llenar select de rutas
function llenarSelectRutas() {
    const selectRuta = document.getElementById('rutaId');
    selectRuta.innerHTML = '<option value="">Ninguna</option>';

    rutas.forEach(ruta => {
        selectRuta.innerHTML += `<option value="${ruta.id}">${ruta.nombre}</option>`;
    });
}

// Cargar jornadas del colegio seleccionado
async function cargarJornadas() {
    const colegioId = document.getElementById('colegioId').value;
    const selectJornada = document.getElementById('jornadaId');

    if (!colegioId) {
        selectJornada.innerHTML = '<option value="">Primero seleccione un colegio</option>';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/colegio-jornadas/colegio/${colegioId}/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const jornadasColegio = await response.json();
            selectJornada.innerHTML = '<option value="">Seleccione...</option>';
            jornadasColegio.forEach(cj => {
                selectJornada.innerHTML += `<option value="${cj.jornadaId}">${cj.nombreJornada}</option>`;
            });
        } else {
            selectJornada.innerHTML = '<option value="">Error al cargar jornadas</option>';
        }
    } catch (error) {
        console.error('Error al cargar jornadas:', error);
        selectJornada.innerHTML = '<option value="">Error al cargar jornadas</option>';
    }
}

// Cargar jornadas para filtro
async function cargarJornadasFiltro() {
    const colegioId = document.getElementById('filtroColegio').value;
    const selectJornada = document.getElementById('filtroJornada');

    selectJornada.innerHTML = '<option value="">Todas</option>';

    if (!colegioId) {
        // Si no hay colegio seleccionado, mostrar todas las jornadas únicas de los estudiantes
        const jornadasUnicas = [...new Set(estudiantesOriginales.map(e => e.jornadaId))];
        const jornadasInfo = estudiantesOriginales
            .filter(e => jornadasUnicas.includes(e.jornadaId))
            .reduce((acc, e) => {
                if (!acc[e.jornadaId]) {
                    acc[e.jornadaId] = e.nombreJornada;
                }
                return acc;
            }, {});

        Object.keys(jornadasInfo).forEach(jornadaId => {
            selectJornada.innerHTML += `<option value="${jornadaId}">${jornadasInfo[jornadaId]}</option>`;
        });
        return;
    }

    // Usar el endpoint real del backend
    try {
        const response = await fetch(`${API_URL}/colegio-jornadas/colegio/${colegioId}/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const jornadasColegio = await response.json();
            jornadasColegio.forEach(cj => {
                selectJornada.innerHTML += `<option value="${cj.jornadaId}">${cj.nombreJornada}</option>`;
            });
        }
    } catch (error) {
        console.error('Error al cargar jornadas:', error);
        // Fallback: usar jornadas de estudiantes
        const jornadasDelColegio = estudiantesOriginales
            .filter(e => e.colegioId == colegioId)
            .reduce((acc, e) => {
                if (!acc[e.jornadaId]) {
                    acc[e.jornadaId] = e.nombreJornada;
                }
                return acc;
            }, {});

        Object.keys(jornadasDelColegio).forEach(jornadaId => {
            selectJornada.innerHTML += `<option value="${jornadaId}">${jornadasDelColegio[jornadaId]}</option>`;
        });
    }
}

// Mostrar estudiantes en la tabla
function mostrarEstudiantes(estudiantes) {
    const tableBody = document.getElementById('tableBody');

    if (estudiantes.length === 0) {
        showTableEmpty('tableBody', 8, 'No se encontraron estudiantes');
        return;
    }

    tableBody.innerHTML = estudiantes.map(e => `
        <tr>
            <td>${e.id}</td>
            <td>
                <span class="badge bg-secondary">${TIPO_ID_LABELS[e.tipoId] || e.tipoId}</span>
                ${e.numId}
            </td>
            <td>${e.nombreCompleto || `${e.primerNombre} ${e.primerApellido}`}</td>
            <td>${e.curso || '-'}</td>
            <td>${e.nombreColegio || '-'}</td>
            <td>${e.nombreJornada || '-'}</td>
            <td><span class="badge ${ESTADO_INSCRIPCION_BADGE_CLASS[e.estadoInscripcion]}">${ESTADO_INSCRIPCION_LABELS[e.estadoInscripcion] || e.estadoInscripcion}</span></td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-info btn-action" onclick="verDetalleEstudiante(${e.id})" title="Ver detalle">
                        <i class="bi bi-eye-fill"></i>
                    </button>
                    ${puedeEditar() ? `
                        <button class="btn btn-outline-primary btn-action" onclick="editarEstudiante(${e.id})" title="Editar">
                            <i class="bi bi-pencil-fill"></i>
                        </button>
                    ` : ''}
                    ${puedeEliminar() ? `
                        <button class="btn btn-outline-danger btn-action" onclick="eliminarEstudiante(${e.id})" title="Eliminar">
                            <i class="bi bi-trash-fill"></i>
                        </button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

// Verificar permisos
function puedeEditar() {
    return ['ADMINISTRADOR', 'ENCARGADO'].includes(currentUser.rol);
}

function puedeEliminar() {
    return currentUser.rol === 'ADMINISTRADOR';
}

// Filtrar estudiantes
function filtrarEstudiantes() {
    const colegio = document.getElementById('filtroColegio').value;
    const jornada = document.getElementById('filtroJornada').value;
    const estado = document.getElementById('filtroEstado').value;

    estudiantesFiltrados = estudiantesOriginales.filter(e => {
        const coincideColegio = !colegio || e.colegioId == colegio;
        const coincideJornada = !jornada || e.jornadaId == jornada;
        const coincideEstado = !estado || e.estadoInscripcion === estado;
        return coincideColegio && coincideJornada && coincideEstado;
    });

    buscarEstudiantes();
}

// Buscar estudiantes
function buscarEstudiantes() {
    const busqueda = document.getElementById('busqueda').value.toLowerCase();

    if (!busqueda) {
        mostrarEstudiantes(estudiantesFiltrados);
        return;
    }

    const resultados = estudiantesFiltrados.filter(e => {
        const nombreCompleto = `${e.primerNombre} ${e.segundoNombre || ''} ${e.primerApellido} ${e.segundoApellido || ''}`.toLowerCase();
        return nombreCompleto.includes(busqueda) || e.numId.includes(busqueda);
    });

    mostrarEstudiantes(resultados);
}

// Mostrar modal para crear
function mostrarModalCrear() {
    if (!puedeEditar()) {
        alert('No tiene permisos para crear estudiantes');
        return;
    }

    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-person-plus-fill me-2"></i>Crear Estudiante';
    document.getElementById('formEstudiante').reset();
    document.getElementById('estudianteId').value = '';
    document.getElementById('modoEdicion').value = 'false';
    modalInstance.show();
}

// Editar estudiante
async function editarEstudiante(id) {
    if (!puedeEditar()) {
        alert('No tiene permisos para editar estudiantes');
        return;
    }

    try {
        const response = await fetch(`${API_URL}/estudiantes/${id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const estudiante = await response.json();

            document.getElementById('modalTitle').innerHTML = '<i class="bi bi-pencil-fill me-2"></i>Editar Estudiante';
            document.getElementById('estudianteId').value = estudiante.id;
            document.getElementById('modoEdicion').value = 'true';

            // Llenar formulario
            document.getElementById('tipoId').value = estudiante.tipoId || '';
            document.getElementById('numId').value = estudiante.numId;
            document.getElementById('primerNombre').value = estudiante.primerNombre;
            document.getElementById('segundoNombre').value = estudiante.segundoNombre || '';
            document.getElementById('primerApellido').value = estudiante.primerApellido;
            document.getElementById('segundoApellido').value = estudiante.segundoApellido || '';
            document.getElementById('fechaNacimiento').value = estudiante.fechaNacimiento || '';
            document.getElementById('sexo').value = estudiante.sexo || '';
            document.getElementById('direccion').value = estudiante.direccion || '';
            document.getElementById('curso').value = estudiante.curso || '';
            document.getElementById('eps').value = estudiante.eps || '';
            document.getElementById('discapacidad').value = estudiante.discapacidad || '';
            document.getElementById('etnia').value = estudiante.etnia || '';

            // Datos del acudiente
            document.getElementById('nombreAcudiente').value = estudiante.nombreAcudiente;
            document.getElementById('telefonoAcudiente').value = estudiante.telefonoAcudiente;
            document.getElementById('direccionAcudiente').value = estudiante.direccionAcudiente || '';
            document.getElementById('emailAcudiente').value = estudiante.emailAcudiente || '';

            // Datos de inscripción
            document.getElementById('colegioId').value = estudiante.colegioId;
            await cargarJornadas();
            document.getElementById('jornadaId').value = estudiante.jornadaId;
            document.getElementById('rutaId').value = estudiante.rutaId || '';
            document.getElementById('observacionesInscripcion').value = estudiante.observacionesInscripcion || '';

            modalInstance.show();
        } else {
            alert('Error al cargar el estudiante');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar el estudiante');
    }
}

// Guardar estudiante
async function guardarEstudiante() {
    const form = document.getElementById('formEstudiante');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const modoEdicion = document.getElementById('modoEdicion').value === 'true';
    const estudianteId = document.getElementById('estudianteId').value;

    // Construir DTO
    const dto = {
        tipoId: document.getElementById('tipoId').value || null,
        numId: document.getElementById('numId').value.trim(),
        primerNombre: document.getElementById('primerNombre').value.trim(),
        segundoNombre: document.getElementById('segundoNombre').value.trim() || null,
        primerApellido: document.getElementById('primerApellido').value.trim(),
        segundoApellido: document.getElementById('segundoApellido').value.trim() || null,
        fechaNacimiento: document.getElementById('fechaNacimiento').value || null,
        sexo: document.getElementById('sexo').value || null,
        direccion: document.getElementById('direccion').value.trim() || null,
        curso: document.getElementById('curso').value.trim() || null,
        eps: document.getElementById('eps').value.trim() || null,
        discapacidad: document.getElementById('discapacidad').value.trim() || null,
        etnia: document.getElementById('etnia').value.trim() || null,
        nombreAcudiente: document.getElementById('nombreAcudiente').value.trim(),
        telefonoAcudiente: document.getElementById('telefonoAcudiente').value.trim(),
        direccionAcudiente: document.getElementById('direccionAcudiente').value.trim() || null,
        emailAcudiente: document.getElementById('emailAcudiente').value.trim() || null,
        colegioId: parseInt(document.getElementById('colegioId').value),
        jornadaId: parseInt(document.getElementById('jornadaId').value),
        rutaId: document.getElementById('rutaId').value ? parseInt(document.getElementById('rutaId').value) : null,
        observacionesInscripcion: document.getElementById('observacionesInscripcion').value.trim() || null
    };

    try {
        const url = modoEdicion ? `${API_URL}/estudiantes/${estudianteId}` : `${API_URL}/estudiantes`;
        const method = modoEdicion ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            alert(modoEdicion ? 'Estudiante actualizado exitosamente' : 'Estudiante creado exitosamente');
            modalInstance.hide();
            cargarEstudiantes();
        } else {
            const error = await response.text();
            alert('Error: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar el estudiante');
    }
}

// Ver detalle del estudiante
async function verDetalleEstudiante(id) {
    try {
        const response = await fetch(`${API_URL}/estudiantes/${id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const estudiante = await response.json();
            mostrarModalDetalle(estudiante);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar el detalle');
    }
}

// Mostrar modal de detalle completo
function mostrarModalDetalle(e) {
    const nombreCompleto = `${e.primerNombre} ${e.segundoNombre || ''} ${e.primerApellido} ${e.segundoApellido || ''}`.trim();

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
                        <h5 class="modal-title"><i class="bi bi-person-lines-fill me-2"></i>Detalle del Estudiante</h5>
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
            <!-- Datos del Estudiante -->
            <div class="col-12">
                <h6 class="border-bottom pb-2" style="color: #667eea;">
                    <i class="bi bi-person-fill me-2"></i>Datos del Estudiante
                </h6>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Nombre Completo</label>
                <p class="fw-bold mb-0">${nombreCompleto}</p>
            </div>
            <div class="col-md-3">
                <label class="text-muted small">Tipo ID</label>
                <p class="mb-0"><span class="badge bg-secondary">${TIPO_ID_LABELS[e.tipoId] || e.tipoId}</span></p>
            </div>
            <div class="col-md-3">
                <label class="text-muted small">Número ID</label>
                <p class="fw-bold mb-0">${e.numId}</p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Fecha de Nacimiento</label>
                <p class="mb-0">${e.fechaNacimiento ? formatDateOnly(e.fechaNacimiento) : '-'}</p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Sexo</label>
                <p class="mb-0">${SEXO_LABELS[e.sexo] || e.sexo || '-'}</p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Curso</label>
                <p class="mb-0">${e.curso || '-'}</p>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Dirección</label>
                <p class="mb-0">${e.direccion || '-'}</p>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">EPS</label>
                <p class="mb-0">${e.eps || '-'}</p>
            </div>
            ${e.discapacidad ? `
            <div class="col-md-6">
                <label class="text-muted small">Discapacidad</label>
                <p class="mb-0">${e.discapacidad}</p>
            </div>
            ` : ''}
            ${e.etnia ? `
            <div class="col-md-6">
                <label class="text-muted small">Etnia</label>
                <p class="mb-0">${e.etnia}</p>
            </div>
            ` : ''}

            <!-- Datos del Acudiente -->
            <div class="col-12 mt-3">
                <h6 class="border-bottom pb-2" style="color: #667eea;">
                    <i class="bi bi-person-circle me-2"></i>Datos del Acudiente
                </h6>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Nombre Completo</label>
                <p class="fw-bold mb-0">${e.nombreAcudiente}</p>
            </div>
            <div class="col-md-6">
                <label class="text-muted small">Teléfono</label>
                <p class="fw-bold mb-0">
                    <i class="bi bi-telephone-fill text-success me-2"></i>${e.telefonoAcudiente}
                </p>
            </div>
            ${e.direccionAcudiente ? `
            <div class="col-md-6">
                <label class="text-muted small">Dirección</label>
                <p class="mb-0">${e.direccionAcudiente}</p>
            </div>
            ` : ''}
            ${e.emailAcudiente ? `
            <div class="col-md-6">
                <label class="text-muted small">Email</label>
                <p class="mb-0"><i class="bi bi-envelope-fill text-primary me-2"></i>${e.emailAcudiente}</p>
            </div>
            ` : ''}

            <!-- Datos de Inscripción -->
            <div class="col-12 mt-3">
                <h6 class="border-bottom pb-2" style="color: #667eea;">
                    <i class="bi bi-building me-2"></i>Datos de Inscripción
                </h6>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Colegio</label>
                <p class="mb-0">${e.nombreColegio || '-'}</p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Jornada</label>
                <p class="mb-0">${e.nombreJornada || '-'}</p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Ruta</label>
                <p class="mb-0">${e.nombreRuta || 'Sin ruta asignada'}</p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Fecha de Inscripción</label>
                <p class="mb-0">${e.fechaInscripcion ? formatDateOnly(e.fechaInscripcion) : '-'}</p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Estado</label>
                <p class="mb-0">
                    <span class="badge ${ESTADO_INSCRIPCION_BADGE_CLASS[e.estadoInscripcion]}">
                        ${ESTADO_INSCRIPCION_LABELS[e.estadoInscripcion] || e.estadoInscripcion}
                    </span>
                </p>
            </div>
            <div class="col-md-4">
                <label class="text-muted small">Estado Activo</label>
                <p class="mb-0">
                    <span class="badge ${e.activo ? 'bg-success' : 'bg-danger'}">
                        ${e.activo ? 'Sí' : 'No'}
                    </span>
                </p>
            </div>
            ${e.observacionesInscripcion ? `
            <div class="col-12">
                <label class="text-muted small">Observaciones</label>
                <p class="mb-0">${e.observacionesInscripcion}</p>
            </div>
            ` : ''}
        </div>
    `;

    // Mostrar modal
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}

// Eliminar estudiante
async function eliminarEstudiante(id) {
    if (!puedeEliminar()) {
        alert('No tiene permisos para eliminar estudiantes');
        return;
    }

    if (!confirmAction('¿Está seguro de eliminar este estudiante? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/estudiantes/${id}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            alert('Estudiante eliminado exitosamente');
            cargarEstudiantes();
        } else {
            alert('Error al eliminar el estudiante');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al eliminar el estudiante');
    }
}

// Función global para logout
function logout() {
    Auth.logout();
}
