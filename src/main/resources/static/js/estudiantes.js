// Lógica de Gestión de Estudiantes
let zonas = [];
// Verificar autenticación y permisos
Auth.requireAuth();
Auth.requireRoles(['ADMINISTRADOR', 'ENCARGADO', 'MONITOR']);

// Variables globales
let estudiantesOriginales = [];
let estudiantesFiltrados = [];
let colegios = [];
let jornadas = [];
let rutas = [];
let monitorData = null; // ✅ Datos del monitor (zona, jornada)
let modalInstance;
const currentUser = Auth.getUser();

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    loadNavbarUser();
    updateMenuByRole();
    modalInstance = new bootstrap.Modal(document.getElementById('modalEstudiante'));
    cargarDatosIniciales();
});


// Cargar datos iniciales
async function cargarDatosIniciales() {
    // ✅ Si es MONITOR, primero obtener sus datos (zona, jornada)
    if (currentUser.rol === 'MONITOR') {
        monitorData = await obtenerDatosMonitor();
    }

    llenarSelectsEtniaDiscapacidadEps();

    await Promise.all([
        cargarZonas(),
        cargarEstudiantes(),
        cargarColegios(),

    ]);

    // ✅ Después de cargar estudiantes, llenar filtros dinámicamente
    llenarFiltrosSegunEstudiantes();
}

// Cargar estudiantes
async function cargarEstudiantes() {
    showTableLoading('tableBody', 8);

    try {
        let url = `${API_URL}/estudiantes`;

        // Si es MONITOR, usar su zona y jornada
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
            mostrarAlertaAdvertencia('Sesión expirada. Por favor inicie sesión nuevamente.');
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
            llenarSelectColegiosFormulario();
        }
    } catch (error) {
        console.error('Error al cargar colegios:', error);
    }
}


// Cargar zonas
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

// Llenar selects de zonas
function llenarSelectZonas() {
    const selectFiltroZona = document.getElementById('filtroZona');
    const selectZonaForm = document.getElementById('zonaId');

    // Para el filtro
    if (selectFiltroZona) {
        selectFiltroZona.innerHTML = '<option value="">Todas las zonas</option>';
        zonas.forEach(z => {
            selectFiltroZona.innerHTML += `<option value="${z.id}">${z.nombreZona}</option>`;
        });
    }

    // Para el formulario
    if (selectZonaForm) {
        selectZonaForm.innerHTML = '<option value="">Seleccione zona...</option>';
        zonas.forEach(z => {
            selectZonaForm.innerHTML += `<option value="${z.id}">${z.nombreZona}</option>`;
        });
    }
}

// Llenar selects de etnia, discapacidad y EPS
function llenarSelectsEtniaDiscapacidadEps() {
    const selectEtnia = document.getElementById('etnia');
    const selectDiscapacidad = document.getElementById('discapacidad');
    const selectEps = document.getElementById('eps');

    if (selectEtnia) {
        selectEtnia.innerHTML = '<option value="">Seleccione...</option>';
        ETNIAS.forEach(e => {
            selectEtnia.innerHTML += `<option value="${e}">${e}</option>`;
        });
    }

    if (selectDiscapacidad) {
        selectDiscapacidad.innerHTML = '<option value="">Seleccione...</option>';
        DISCAPACIDADES.forEach(d => {
            selectDiscapacidad.innerHTML += `<option value="${d}">${d}</option>`;
        });
    }

    if (selectEps) {
        selectEps.innerHTML = '<option value="">Seleccione...</option>';
        EPS_OPCIONES.forEach(e => {
            selectEps.innerHTML += `<option value="${e}">${e}</option>`;
        });
    }
}

// Cargar colegios por zona (para el FILTRO)
function cargarColegiosFiltro() {
    const zonaId = document.getElementById('filtroZona').value;
    const selectColegio = document.getElementById('filtroColegio');

    if (!zonaId) {
        llenarFiltrosSegunEstudiantes();
        return;
    }

    // Filtrar colegios que pertenecen a esa zona
    const colegiosFiltrados = colegios.filter(c => c.zonaId == zonaId);

    selectColegio.innerHTML = '<option value="">Todos los colegios</option>';
    colegiosFiltrados.forEach(c => {
        selectColegio.innerHTML += `<option value="${c.id}">${c.nombreColegio}</option>`;
    });
}

// Cargar colegios por zona (para el FORMULARIO)
async function cargarColegiosPorZona() {
    const zonaId = document.getElementById('zonaId').value;
    const selectColegio = document.getElementById('colegioId');
    const selectJornada = document.getElementById('jornadaId');

    if (!zonaId) {
        selectColegio.innerHTML = '<option value="">Primero seleccione zona</option>';
        selectJornada.innerHTML = '<option value="">Primero seleccione colegio</option>';
        return;
    }

    // Filtrar colegios de la zona seleccionada
    const colegiosZona = colegios.filter(c => c.zonaId == zonaId);

    selectColegio.innerHTML = '<option value="">Seleccione colegio...</option>';
    colegiosZona.forEach(c => {
        selectColegio.innerHTML += `<option value="${c.id}">${c.nombreColegio}</option>`;
    });

    selectJornada.innerHTML = '<option value="">Primero seleccione colegio</option>';
}

// Cargar jornadas por colegio (para el FORMULARIO)
async function cargarJornadasPorColegio() {
    const colegioId = document.getElementById('colegioId').value;
    const selectJornada = document.getElementById('jornadaId');

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
            selectJornada.innerHTML = '<option value="">Seleccione jornada...</option>';
            jornadasColegio.forEach(j => {
                selectJornada.innerHTML += `<option value="${j.jornadaId}">${j.nombreJornada || JORNADA_LABELS[j.tipoJornada] || j.tipoJornada}</option>`;
            });
        }
    } catch (error) {
        console.error('Error al cargar jornadas:', error);
        selectJornada.innerHTML = '<option value="">Error al cargar</option>';
    }
}

// ✅ Llenar select de colegios solo para el FORMULARIO de crear/editar
function llenarSelectColegiosFormulario() {
    const selectColegio = document.getElementById('colegioId');
    if (!selectColegio) return;

    selectColegio.innerHTML = '<option value="">Seleccione...</option>';
    colegios.forEach(colegio => {
        selectColegio.innerHTML += `<option value="${colegio.id}">${colegio.nombreColegio}</option>`;
    });
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

// =============================================
// VALIDACIONES
// =============================================

// Validar edad del estudiante (entre 5 y 18 años)
function validarEdad(fechaNacimiento) {
    if (!fechaNacimiento) {
        return { valido: false, mensaje: 'La fecha de nacimiento es obligatoria' };
    }

    const hoy = new Date();
    const nacimiento = new Date(fechaNacimiento);
    let edad = hoy.getFullYear() - nacimiento.getFullYear();
    const mes = hoy.getMonth() - nacimiento.getMonth();

    if (mes < 0 || (mes === 0 && hoy.getDate() < nacimiento.getDate())) {
        edad--;
    }

    if (edad < 5) {
        return { valido: false, mensaje: `El estudiante tiene ${edad} años. Debe tener al menos 5 años para inscribirse.` };
    }

    if (edad >= 18) {
        return { valido: false, mensaje: `El estudiante tiene ${edad} años. Debe ser menor de 18 años.` };
    }

    return { valido: true, edad: edad };
}

// Validar número de identificación (solo números)
function validarNumeroId(numId) {
    if (!numId || !numId.trim()) {
        return { valido: false, mensaje: 'El número de identificación es obligatorio' };
    }

    const soloNumeros = /^\d+$/;

    if (!soloNumeros.test(numId.trim())) {
        return { valido: false, mensaje: 'El número de identificación solo debe contener números' };
    }

    if (numId.trim().length < 5) {
        return { valido: false, mensaje: 'El número de identificación debe tener al menos 5 dígitos' };
    }

    if (numId.trim().length > 15) {
        return { valido: false, mensaje: 'El número de identificación no puede tener más de 15 dígitos' };
    }

    return { valido: true };
}

// Validar formato de teléfono
function validarTelefono(telefono) {
    if (!telefono || !telefono.trim()) {
        return { valido: false, mensaje: 'El teléfono del acudiente es obligatorio' };
    }

    const soloNumeros = telefono.replace(/\D/g, '');

    if (soloNumeros.length < 7) {
        return { valido: false, mensaje: 'El teléfono debe tener al menos 7 dígitos' };
    }

    if (soloNumeros.length > 15) {
        return { valido: false, mensaje: 'El teléfono no puede tener más de 15 dígitos' };
    }

    return { valido: true };
}

// Validar formato de email
function validarEmail(email) {
    if (!email || !email.trim()) {
        return { valido: true }; // Email no es obligatorio
    }

    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regexEmail.test(email.trim())) {
        return { valido: false, mensaje: 'El formato del correo electrónico no es válido. Ejemplo: correo@dominio.com' };
    }

    return { valido: true };
}

// Validar solo letras (para nombres)
function validarSoloLetras(texto, nombreCampo) {
    if (!texto || !texto.trim()) {
        return { valido: false, mensaje: `El campo "${nombreCampo}" es obligatorio` };
    }

    const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/;

    if (!soloLetras.test(texto.trim())) {
        return { valido: false, mensaje: `El campo "${nombreCampo}" solo debe contener letras` };
    }

    if (texto.trim().length < 2) {
        return { valido: false, mensaje: `El campo "${nombreCampo}" debe tener al menos 2 caracteres` };
    }

    return { valido: true };
}

// Validar selección de dropdown
function validarSeleccion(valor, nombreCampo) {
    if (!valor || valor === '') {
        return { valido: false, mensaje: `Debe seleccionar ${nombreCampo}` };
    }
    return { valido: true };
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
        mostrarAlertaError('No tiene permisos para crear estudiantes');
        return;
    }

    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-person-plus-fill me-2"></i>Crear Estudiante';
    document.getElementById('formEstudiante').reset();
    document.getElementById('estudianteId').value = '';
    document.getElementById('modoEdicion').value = 'false';

    // Resetear selects dependientes
    document.getElementById('colegioId').innerHTML = '<option value="">Primero seleccione zona</option>';
    document.getElementById('jornadaId').innerHTML = '<option value="">Primero seleccione colegio</option>';

    modalInstance.show();
}

// Editar estudiante
async function editarEstudiante(id) {
    if (!puedeEditar()) {
        mostrarAlertaError('No tiene permisos para editar estudiantes');
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

            // Datos de inscripción - cargar zona primero
            if (estudiante.zonaId) {
                document.getElementById('zonaId').value = estudiante.zonaId;
                await cargarColegiosPorZona();
            }
            document.getElementById('colegioId').value = estudiante.colegioId;
            await cargarJornadasPorColegio();
            document.getElementById('jornadaId').value = estudiante.jornadaId;
            document.getElementById('observacionesInscripcion').value = estudiante.observacionesInscripcion || '';

            modalInstance.show();
        } else {
            mostrarAlertaError('Error al cargar el estudiante');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error de conexión al cargar el estudiante');
    }
}

// =============================================
// ALERTAS BONITAS
// =============================================

function mostrarAlertaError(mensaje) {
    Swal.fire({
        icon: 'error',
        title: 'Error',
        text: mensaje,
        confirmButtonColor: '#667eea'
    });
}

function mostrarAlertaExito(mensaje) {
    Swal.fire({
        icon: 'success',
        title: '¡Éxito!',
        text: mensaje,
        confirmButtonColor: '#667eea'
    });
}

function mostrarAlertaAdvertencia(mensaje) {
    Swal.fire({
        icon: 'warning',
        title: 'Atención',
        text: mensaje,
        confirmButtonColor: '#667eea'
    });
}

// =============================================
// GUARDAR ESTUDIANTE
// =============================================

async function guardarEstudiante() {

    // =============================================
    // VALIDAR DATOS DEL ESTUDIANTE
    // =============================================

    // Tipo de ID
    const tipoId = document.getElementById('tipoId').value;
    if (!tipoId) {
        mostrarAlertaError('Debe seleccionar un tipo de identificación');
        document.getElementById('tipoId').focus();
        return;
    }

    // Número de ID
    const numId = document.getElementById('numId').value;
    const valNumId = validarNumeroId(numId);
    if (!valNumId.valido) {
        mostrarAlertaError(valNumId.mensaje);
        document.getElementById('numId').focus();
        return;
    }

    // Fecha de nacimiento y edad
    const fechaNacimiento = document.getElementById('fechaNacimiento').value;
    const valEdad = validarEdad(fechaNacimiento);
    if (!valEdad.valido) {
        mostrarAlertaError(valEdad.mensaje);
        document.getElementById('fechaNacimiento').focus();
        return;
    }

    // Primer nombre
    const primerNombre = document.getElementById('primerNombre').value;
    const valPrimerNombre = validarSoloLetras(primerNombre, 'Primer Nombre');
    if (!valPrimerNombre.valido) {
        mostrarAlertaError(valPrimerNombre.mensaje);
        document.getElementById('primerNombre').focus();
        return;
    }

    // Segundo nombre (opcional, pero si tiene debe ser válido)
    const segundoNombre = document.getElementById('segundoNombre').value;
    if (segundoNombre && segundoNombre.trim()) {
        const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/;
        if (!soloLetras.test(segundoNombre.trim())) {
            mostrarAlertaError('El segundo nombre solo debe contener letras');
            document.getElementById('segundoNombre').focus();
            return;
        }
    }

    // Primer apellido
    const primerApellido = document.getElementById('primerApellido').value;
    const valPrimerApellido = validarSoloLetras(primerApellido, 'Primer Apellido');
    if (!valPrimerApellido.valido) {
        mostrarAlertaError(valPrimerApellido.mensaje);
        document.getElementById('primerApellido').focus();
        return;
    }

    // Segundo apellido (opcional, pero si tiene debe ser válido)
    const segundoApellido = document.getElementById('segundoApellido').value;
    if (segundoApellido && segundoApellido.trim()) {
        const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/;
        if (!soloLetras.test(segundoApellido.trim())) {
            mostrarAlertaError('El segundo apellido solo debe contener letras');
            document.getElementById('segundoApellido').focus();
            return;
        }
    }

    // =============================================
    // VALIDAR DATOS DEL ACUDIENTE
    // =============================================

    // Nombre del acudiente
    const nombreAcudiente = document.getElementById('nombreAcudiente').value;
    const valNombreAcudiente = validarSoloLetras(nombreAcudiente, 'Nombre del Acudiente');
    if (!valNombreAcudiente.valido) {
        mostrarAlertaError(valNombreAcudiente.mensaje);
        document.getElementById('nombreAcudiente').focus();
        return;
    }

    // Teléfono del acudiente
    const telefonoAcudiente = document.getElementById('telefonoAcudiente').value;
    const valTelefono = validarTelefono(telefonoAcudiente);
    if (!valTelefono.valido) {
        mostrarAlertaError(valTelefono.mensaje);
        document.getElementById('telefonoAcudiente').focus();
        return;
    }

    // Email del acudiente (opcional)
    const emailAcudiente = document.getElementById('emailAcudiente').value;
    const valEmail = validarEmail(emailAcudiente);
    if (!valEmail.valido) {
        mostrarAlertaError(valEmail.mensaje);
        document.getElementById('emailAcudiente').focus();
        return;
    }

    // =============================================
    // VALIDAR DATOS DE INSCRIPCIÓN
    // =============================================

    // Zona
    const zonaId = document.getElementById('zonaId').value;
    if (!zonaId) {
        mostrarAlertaError('Debe seleccionar una zona');
        document.getElementById('zonaId').focus();
        return;
    }

    // Colegio
    const colegioId = document.getElementById('colegioId').value;
    if (!colegioId) {
        mostrarAlertaError('Debe seleccionar un colegio');
        document.getElementById('colegioId').focus();
        return;
    }

    // Jornada
    const jornadaId = document.getElementById('jornadaId').value;
    if (!jornadaId) {
        mostrarAlertaError('Debe seleccionar una jornada');
        document.getElementById('jornadaId').focus();
        return;
    }

    // =============================================
    // CONSTRUIR DTO Y ENVIAR
    // =============================================

    const modoEdicion = document.getElementById('modoEdicion').value === 'true';
    const estudianteId = document.getElementById('estudianteId').value;

    const dto = {
        tipoId: tipoId,
        numId: numId.trim(),
        primerNombre: primerNombre.trim(),
        segundoNombre: segundoNombre ? segundoNombre.trim() : null,
        primerApellido: primerApellido.trim(),
        segundoApellido: segundoApellido ? segundoApellido.trim() : null,
        fechaNacimiento: fechaNacimiento,
        sexo: document.getElementById('sexo').value || null,
        direccion: document.getElementById('direccion').value.trim() || null,
        curso: document.getElementById('curso').value.trim() || null,
        eps: document.getElementById('eps').value || null,
        discapacidad: document.getElementById('discapacidad').value || null,
        etnia: document.getElementById('etnia').value || null,
        nombreAcudiente: nombreAcudiente.trim(),
        telefonoAcudiente: telefonoAcudiente.trim(),
        direccionAcudiente: document.getElementById('direccionAcudiente').value.trim() || null,
        emailAcudiente: emailAcudiente ? emailAcudiente.trim() : null,
        colegioId: parseInt(colegioId),
        jornadaId: parseInt(jornadaId),
        rutaId: null,
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
            await Swal.fire({
                icon: 'success',
                title: modoEdicion ? '¡Actualizado!' : '¡Creado!',
                text: modoEdicion ? 'Estudiante actualizado exitosamente' : 'Estudiante creado exitosamente',
                confirmButtonColor: '#667eea'
            });
            modalInstance.hide();
            cargarEstudiantes();
        } else {
            const errorText = await response.text();

            if (errorText.toLowerCase().includes('ya existe') || errorText.toLowerCase().includes('duplicado') || errorText.toLowerCase().includes('numid')) {
                mostrarAlertaAdvertencia('El número de identificación ya está registrado en el sistema');
            } else if (response.status === 409) {
                mostrarAlertaAdvertencia('El estudiante ya existe en el sistema');
            } else if (response.status === 400) {
                mostrarAlertaError('Error en los datos enviados: ' + errorText);
            } else {
                mostrarAlertaError('Error: ' + errorText);
            }
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error de conexión. Verifique que el servidor esté activo.');
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
        } else {
            mostrarAlertaError('Error al cargar el detalle del estudiante');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error de conexión al cargar el detalle');
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
        mostrarAlertaError('No tiene permisos para eliminar estudiantes');
        return;
    }

    const resultado = await Swal.fire({
        icon: 'warning',
        title: '¿Está seguro?',
        text: 'Esta acción eliminará el estudiante y no se puede deshacer',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    });

    if (!resultado.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/estudiantes/${id}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            mostrarAlertaExito('Estudiante eliminado exitosamente');
            cargarEstudiantes();
        } else {
            mostrarAlertaError('Error al eliminar el estudiante');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error de conexión al eliminar el estudiante');
    }
}

// Función global para logout
function logout() {
    Auth.logout();
}

