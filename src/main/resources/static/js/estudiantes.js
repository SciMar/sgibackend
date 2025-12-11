// Lógica de Gestión de Estudiantes

// Verificar autenticación y permisos
Auth.requireAuth();
Auth.requireRoles(['ADMINISTRADOR', 'ENCARGADO', 'MONITOR']);

// Variables globales
let estudiantesOriginales = [];
let estudiantesFiltrados = [];
let zonas = [];      // ✅ Agregar zonas
let colegios = [];
let jornadas = [];
let rutas = [];
let monitorData = null;
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
    // Si es MONITOR, primero obtener sus datos (zona, jornada)
    if (currentUser.rol === 'MONITOR') {
        monitorData = await obtenerDatosMonitor();
    }

    await Promise.all([
        cargarEstudiantes(),
        cargarZonas(),      // ✅ Cargar zonas para el formulario
        cargarColegios(),
        cargarRutas()
    ]);

    // Después de cargar estudiantes, llenar filtros dinámicamente
    llenarFiltrosSegunEstudiantes();
}

// Cargar estudiantes
async function cargarEstudiantes() {
    showTableLoading('tableBody', 8);

    try {
        let url = `${API_URL}/estudiantes`;

        // ✅ Si es MONITOR, usar su zona y jornada
        if (currentUser.rol === 'MONITOR' && monitorData && monitorData.zonaId && monitorData.jornadaId) {
            url = `${API_URL}/estudiantes/monitor/zona/${monitorData.zonaId}/jornada/${monitorData.jornadaId}`;
        }

        const response = await fetch(url, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            estudiantesOriginales = await response.json();
            estudiantesFiltrados = [...estudiantesOriginales];
            mostrarEstudiantes(estudiantesFiltrados);
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

// Cargar colegios (para el formulario de crear/editar)
async function cargarColegios() {
    try {
        const response = await fetch(`${API_URL}/colegios`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            colegios = await response.json();
        }
    } catch (error) {
        console.error('Error al cargar colegios:', error);
    }
}

// ✅ Cargar zonas para el formulario
async function cargarZonas() {
    try {
        const response = await fetch(`${API_URL}/zonas`, {
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

// ✅ Llenar select de zonas en el formulario
function llenarSelectZonas() {
    const selectZona = document.getElementById('zonaId');
    if (!selectZona) return;

    selectZona.innerHTML = '<option value="">Seleccione zona...</option>';
    zonas.forEach(zona => {
        if (zona.activa) {
            selectZona.innerHTML += `<option value="${zona.id}">${zona.nombreZona}</option>`;
        }
    });
}

// ✅ Cargar colegios filtrados por zona seleccionada
async function cargarColegiosPorZona() {
    const zonaId = document.getElementById('zonaId').value;
    const selectColegio = document.getElementById('colegioId');
    const selectJornada = document.getElementById('jornadaId');

    // Resetear selects dependientes
    selectColegio.innerHTML = '<option value="">Seleccione colegio...</option>';
    selectJornada.innerHTML = '<option value="">Primero seleccione colegio</option>';

    if (!zonaId) return;

    try {
        const response = await fetch(`${API_URL}/colegios/zona/${zonaId}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const colegiosZona = await response.json();
            colegiosZona.forEach(colegio => {
                if (colegio.activo) {
                    selectColegio.innerHTML += `<option value="${colegio.id}">${colegio.nombreColegio}</option>`;
                }
            });
        }
    } catch (error) {
        console.error('Error al cargar colegios por zona:', error);
    }
}

// ✅ Cargar jornadas del colegio seleccionado
async function cargarJornadasPorColegio() {
    const colegioId = document.getElementById('colegioId').value;
    const selectJornada = document.getElementById('jornadaId');

    selectJornada.innerHTML = '<option value="">Seleccione jornada...</option>';

    if (!colegioId) {
        selectJornada.innerHTML = '<option value="">Primero seleccione colegio</option>';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/colegio-jornadas/colegio/${colegioId}/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const jornadasColegio = await response.json();
            jornadasColegio.forEach(cj => {
                selectJornada.innerHTML += `<option value="${cj.jornadaId}">${cj.nombreJornada || JORNADA_LABELS[cj.tipoJornada] || cj.tipoJornada}</option>`;
            });
        }
    } catch (error) {
        console.error('Error al cargar jornadas:', error);
        selectJornada.innerHTML = '<option value="">Error al cargar jornadas</option>';
    }
}

// Función legacy para compatibilidad
async function cargarJornadas() {
    await cargarJornadasPorColegio();
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

// ✅ Llenar filtros basándose en los estudiantes disponibles
function llenarFiltrosSegunEstudiantes() {
    const selectFiltroColegio = document.getElementById('filtroColegio');
    const selectFiltroJornada = document.getElementById('filtroJornada');

    // Extraer colegios únicos de los estudiantes
    const colegiosUnicos = {};
    estudiantesOriginales.forEach(e => {
        if (e.colegioId && e.nombreColegio && !colegiosUnicos[e.colegioId]) {
            colegiosUnicos[e.colegioId] = e.nombreColegio;
        }
    });

    // Llenar filtro de colegios solo con los que tienen estudiantes
    selectFiltroColegio.innerHTML = '<option value="">Todos los colegios</option>';
    Object.keys(colegiosUnicos).forEach(colegioId => {
        selectFiltroColegio.innerHTML += `<option value="${colegioId}">${colegiosUnicos[colegioId]}</option>`;
    });

    // Extraer jornadas únicas de los estudiantes
    const jornadasUnicas = new Set();
    estudiantesOriginales.forEach(e => {
        if (e.tipoJornada) {
            jornadasUnicas.add(e.tipoJornada);
        }
    });

    // Llenar filtro de jornadas solo con las que tienen estudiantes
    selectFiltroJornada.innerHTML = '<option value="">Todas</option>';
    jornadasUnicas.forEach(tipoJornada => {
        selectFiltroJornada.innerHTML += `<option value="${tipoJornada}">${JORNADA_LABELS[tipoJornada] || tipoJornada}</option>`;
    });

    console.log(`Filtros cargados: ${Object.keys(colegiosUnicos).length} colegios, ${jornadasUnicas.size} jornadas`);
}

// ✅ Actualizar jornadas cuando se selecciona un colegio en el filtro
function actualizarJornadasPorColegio() {
    const colegioId = document.getElementById('filtroColegio').value;
    const selectFiltroJornada = document.getElementById('filtroJornada');

    // Si no hay colegio seleccionado, mostrar todas las jornadas disponibles
    let estudiantesAFiltrar = estudiantesOriginales;

    // Si hay colegio seleccionado, filtrar solo estudiantes de ese colegio
    if (colegioId) {
        estudiantesAFiltrar = estudiantesOriginales.filter(e => e.colegioId == colegioId);
    }

    // Extraer jornadas únicas
    const jornadasUnicas = new Set();
    estudiantesAFiltrar.forEach(e => {
        if (e.tipoJornada) {
            jornadasUnicas.add(e.tipoJornada);
        }
    });

    // Llenar select
    selectFiltroJornada.innerHTML = '<option value="">Todas</option>';
    jornadasUnicas.forEach(tipoJornada => {
        selectFiltroJornada.innerHTML += `<option value="${tipoJornada}">${JORNADA_LABELS[tipoJornada] || tipoJornada}</option>`;
    });
}

// Llenar select de rutas
function llenarSelectRutas() {
    const selectRuta = document.getElementById('rutaId');
    if (!selectRuta) return;

    selectRuta.innerHTML = '<option value="">Ninguna</option>';

    rutas.forEach(ruta => {
        selectRuta.innerHTML += `<option value="${ruta.id}">${ruta.nombre}</option>`;
    });
}

// Cargar jornadas del colegio seleccionado (para el formulario de crear/editar)
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

// ✅ Ya no necesitamos cargarJornadasFiltro() porque usamos tipos fijos

// Mostrar estudiantes en la tabla
function mostrarEstudiantes(estudiantes) {
    const tableBody = document.getElementById('tableBody');

    if (estudiantes.length === 0) {
        showTableEmpty('tableBody', 8, 'No se encontraron estudiantes');
        actualizarContador();
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
            <td><span class="badge bg-info">${JORNADA_LABELS[e.tipoJornada] || e.tipoJornada || '-'}</span></td>
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

    actualizarContador();
}

// Verificar permisos
function puedeEditar() {
    return ['ADMINISTRADOR', 'ENCARGADO'].includes(currentUser.rol);
}

function puedeEliminar() {
    return currentUser.rol === 'ADMINISTRADOR';
}

// ✅ Aplicar filtros (llamado por el botón Filtrar)
function aplicarFiltros() {
    const colegio = document.getElementById('filtroColegio').value;
    const tipoJornada = document.getElementById('filtroJornada').value;
    const estado = document.getElementById('filtroEstado').value;
    const busqueda = document.getElementById('busqueda').value.toLowerCase().trim();

    estudiantesFiltrados = estudiantesOriginales.filter(e => {
        // Filtrar por colegio
        const coincideColegio = !colegio || e.colegioId == colegio;

        // Filtrar por tipo de jornada
        const coincideJornada = !tipoJornada || e.tipoJornada === tipoJornada;

        // Filtrar por estado
        const coincideEstado = !estado || e.estadoInscripcion === estado;

        // Filtrar por búsqueda (nombre o documento)
        let coincideBusqueda = true;
        if (busqueda) {
            const nombreCompleto = `${e.primerNombre} ${e.segundoNombre || ''} ${e.primerApellido} ${e.segundoApellido || ''}`.toLowerCase();
            coincideBusqueda = nombreCompleto.includes(busqueda) || (e.numId && e.numId.toLowerCase().includes(busqueda));
        }

        return coincideColegio && coincideJornada && coincideEstado && coincideBusqueda;
    });

    mostrarEstudiantes(estudiantesFiltrados);
    actualizarContador();
}

// ✅ Limpiar todos los filtros
function limpiarFiltros() {
    document.getElementById('filtroColegio').value = '';
    document.getElementById('filtroJornada').value = '';
    document.getElementById('filtroEstado').value = '';
    document.getElementById('busqueda').value = '';

    estudiantesFiltrados = [...estudiantesOriginales];
    mostrarEstudiantes(estudiantesFiltrados);
    actualizarContador();
}

// ✅ Actualizar contador de resultados
function actualizarContador() {
    const contador = document.getElementById('resultsCount');
    if (contador) {
        const total = estudiantesFiltrados.length;
        contador.textContent = `${total} estudiante${total !== 1 ? 's' : ''}`;
    }
}

// Función legacy para compatibilidad
function filtrarEstudiantes() {
    aplicarFiltros();
}

// Búsqueda por tecla Enter
document.addEventListener('DOMContentLoaded', () => {
    const busquedaInput = document.getElementById('busqueda');
    if (busquedaInput) {
        busquedaInput.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                aplicarFiltros();
            }
        });
    }
});

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

    // Ocultar campo de estado en modo crear
    document.getElementById('estadoContainer').style.display = 'none';

    // Resetear selects en cascada
    document.getElementById('zonaId').value = '';
    document.getElementById('colegioId').innerHTML = '<option value="">Primero seleccione zona</option>';
    document.getElementById('jornadaId').innerHTML = '<option value="">Primero seleccione colegio</option>';

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

            // Llenar datos del estudiante
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

            // ✅ Mostrar campo de estado en modo edición
            document.getElementById('estadoContainer').style.display = 'block';
            document.getElementById('estadoInscripcion').value = estudiante.estadoInscripcion || 'ACTIVA';

            // ✅ Cargar datos de inscripción en cascada
            // Primero necesitamos obtener la zona del colegio
            if (estudiante.colegioId) {
                // Buscar la zona del colegio
                const colegioResponse = await fetch(`${API_URL}/colegios/${estudiante.colegioId}`, {
                    headers: Auth.getHeaders()
                });

                if (colegioResponse.ok) {
                    const colegio = await colegioResponse.json();

                    // Establecer zona
                    document.getElementById('zonaId').value = colegio.zonaId || '';

                    // Cargar colegios de esa zona
                    await cargarColegiosPorZona();

                    // Establecer colegio
                    document.getElementById('colegioId').value = estudiante.colegioId;

                    // Cargar jornadas del colegio
                    await cargarJornadasPorColegio();

                    // Establecer jornada
                    document.getElementById('jornadaId').value = estudiante.jornadaId || '';
                }
            }

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
        observacionesInscripcion: document.getElementById('observacionesInscripcion').value.trim() || null
    };

    // ✅ Agregar estado solo en modo edición
    if (modoEdicion) {
        dto.estadoInscripcion = document.getElementById('estadoInscripcion').value;
    }

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