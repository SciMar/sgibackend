// Registro de asistencias por lote (corregido y más robusto)

// Helper: acceso seguro a elementos por id
function byId(id) {
    return document.getElementById(id);
}

// Estado global
let currentUser = null;
let estudiantesData = [];
let asistenciasRegistradas = {};

// ==========================================
// SISTEMA DE NOTIFICACIONES TOAST MEJORADO
// ==========================================
function showToast(type, title, message, options = {}) {
    const defaults = { autoClose: false, duration: 10000, showTimer: false, position: 'top-right' };
    const config = { ...defaults, ...options };
    const toastContainer = byId('toastContainer');
    if (!toastContainer) return;

    const toastId = 'toast-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);
    const iconMap = {
        'success': '<i class="bi bi-check-circle-fill me-2" style="font-size: 1.5rem;"></i>',
        'danger': '<i class="bi bi-x-circle-fill me-2" style="font-size: 1.5rem;"></i>',
        'warning': '<i class="bi bi-exclamation-triangle-fill me-2" style="font-size: 1.5rem;"></i>',
        'info': '<i class="bi bi-info-circle-fill me-2" style="font-size: 1.5rem;"></i>'
    };

    let timerHtml = '';
    if (config.autoClose && config.showTimer) {
        timerHtml = `<div class="toast-timer"><div class="toast-timer-bar" id="${toastId}-timer" style="width: 100%;"></div></div>`;
    }

    const toastDiv = document.createElement('div');
    toastDiv.id = toastId;
    toastDiv.className = `toast-custom alert alert-${type} alert-dismissible fade show`;
    toastDiv.setAttribute('role', 'alert');
    toastDiv.style.position = 'relative';
    toastDiv.innerHTML = `
        <div class="d-flex align-items-start">
            ${iconMap[type] || iconMap.info}
            <div class="flex-grow-1">
                <strong>${title}</strong>
                <div class="mt-1">${message}</div>
            </div>
            <button type="button" class="btn-close ms-3" onclick="cerrarToast('${toastId}')"></button>
        </div>
        ${timerHtml}
    `;

    toastContainer.appendChild(toastDiv);

    if (config.autoClose) {
        const timerBar = byId(`${toastId}-timer`);
        if (timerBar) {
            timerBar.style.transition = `width ${config.duration}ms linear`;
            setTimeout(() => { timerBar.style.width = '0%'; }, 50);
        }
        setTimeout(() => { cerrarToast(toastId); }, config.duration);
    }

    return toastId;
}

function cerrarToast(toastId) {
    const toast = byId(toastId);
    if (toast) {
        toast.classList.add('removing');
        setTimeout(() => { if (toast.parentNode) toast.remove(); }, 300);
    }
}

function cerrarTodosLosToasts() {
    const toasts = document.querySelectorAll('.toast-custom');
    toasts.forEach(t => cerrarToast(t.id));
}

// ==========================================
// SHOW ALERT (legacy container alerts)
// ==========================================
function showAlert(containerId, type, message) {
    const container = byId(containerId);
    if (!container) return;
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.innerHTML = `${message}<button type="button" class="btn-close" data-bs-dismiss="alert"></button>`;
    container.innerHTML = '';
    container.appendChild(alertDiv);
}

// ==========================================
// INICIALIZACIÓN (UN SOLO DOMContentLoaded)
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    // Autenticación y usuario actual
    try {
        Auth.requireAuth();
    } catch (e) {
        console.error('No autenticado:', e);
        return;
    }
    currentUser = Auth.getUser();

    // Inicializaciones UI seguras
    loadNavbarUser?.();
    configurarMenuPorRol();
    cargarColegios();
    // Establecer fecha de hoy por defecto si elemento existe
    const hoy = new Date().toISOString().split('T')[0];
    const fechaEl = byId('fechaAsistencia');
    if (fechaEl) fechaEl.value = hoy;

    // Event listeners seguros
    const filtroColegioEl = byId('filtroColegio');
    if (filtroColegioEl) filtroColegioEl.addEventListener('change', cargarJornadas);
});

// ==========================================
// CONFIGURAR MENÚ SEGÚN ROL (robusto)
// ==========================================
function configurarMenuPorRol() {
    const rol = currentUser ? currentUser.rol : null;
    const show = id => { const el = byId(id); if (el) el.style.display = 'block'; };

    if (rol === 'ADMINISTRADOR') {
        ['menuUsuarios','menuEstudiantes','menuRutas','menuColegios','menuAsistencias','menuNotificaciones','menuReportes']
            .forEach(show);
    } else if (rol === 'ENCARGADO') {
        ['menuEstudiantes','menuRutas','menuColegios','menuAsistencias','menuNotificaciones','menuReportes']
            .forEach(show);
    } else if (rol === 'MONITOR') {
        ['menuEstudiantes','menuAsistencias'].forEach(show);
    }
}

// ==========================================
// CARGAR COLEGIOS
// ==========================================
async function cargarColegios() {
    try {
        const response = await fetch(`${API_URL}/colegios`, { headers: Auth.getHeaders() });
        if (!response.ok) throw new Error('Error al cargar colegios');
        const colegios = await response.json();
        const select = byId('filtroColegio');
        if (!select) return;

        select.innerHTML = '<option value="">Seleccione...</option>';
        colegios.forEach(colegio => {
            const option = document.createElement('option');
            option.value = colegio.id;
            option.textContent = colegio.nombreColegio || colegio.nombre || '';
            if (colegio.zonaId) option.dataset.zonaId = colegio.zonaId;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando colegios:', error);
        showAlert('alertContainer', 'danger', 'Error al cargar los colegios');
    }
}

// ==========================================
// CARGAR JORNADAS SEGÚN COLEGIO
// ==========================================
async function cargarJornadas() {
    const colegioSelect = byId('filtroColegio');
    const jornadaSelect = byId('filtroJornada');
    if (!colegioSelect || !jornadaSelect) return;

    const colegioId = colegioSelect.value;
    if (!colegioId) {
        jornadaSelect.innerHTML = '<option value="">Primero seleccione colegio</option>';
        jornadaSelect.disabled = true;
        return;
    }

    try {
        const selectedOption = colegioSelect.options[colegioSelect.selectedIndex];
        const zonaId = selectedOption ? selectedOption.dataset.zonaId : null;

        const response = await fetch(`${API_URL}/jornadas/zona/${zonaId}`, { headers: Auth.getHeaders() });
        if (!response.ok) throw new Error('Error al cargar jornadas');
        const jornadas = await response.json();

        jornadaSelect.innerHTML = '<option value="">Seleccione...</option>';
        jornadas.forEach(jornada => {
            const option = document.createElement('option');
            option.value = jornada.id;
            option.textContent = `${jornada.nombreJornada || jornada.nombre} - ${jornada.codigoJornada || jornada.codigo || ''}`;
            jornadaSelect.appendChild(option);
        });
        jornadaSelect.disabled = false;
    } catch (error) {
        console.error('Error cargando jornadas:', error);
        if (jornadaSelect) jornadaSelect.innerHTML = '<option value="">Error cargando jornadas</option>';
        showAlert('alertContainer', 'danger', 'Error al cargar las jornadas');
    }
}

// ==========================================
// CARGAR ESTUDIANTES SEGÚN FILTROS
// ==========================================
async function cargarEstudiantes() {
    const colegioEl = byId('filtroColegio');
    const jornadaEl = byId('filtroJornada');
    const tipoRecorridoEl = byId('tipoRecorrido');
    const fechaEl = byId('fechaAsistencia');

    // Limpiar alertas previas
    const alertContainer = byId('alertContainer');
    if (alertContainer) alertContainer.innerHTML = '';
    cerrarTodosLosToasts();

    const colegioId = colegioEl ? colegioEl.value : null;
    const jornadaId = jornadaEl ? jornadaEl.value : null;
    const tipoRecorrido = tipoRecorridoEl ? tipoRecorridoEl.value : null;
    const fecha = fechaEl ? fechaEl.value : null;

    if (!colegioId) { showToast('warning','Colegio requerido','Por favor selecciona un colegio antes de continuar.'); if (colegioEl) colegioEl.focus(); return; }
    if (!jornadaId) { showToast('warning','Jornada requerida','Por favor selecciona una jornada antes de continuar.'); if (jornadaEl) jornadaEl.focus(); return; }
    if (!tipoRecorrido) { showToast('warning','Tipo de Recorrido requerido','Por favor selecciona si es IDA o REGRESO.'); if (tipoRecorridoEl) tipoRecorridoEl.focus(); return; }
    if (!fecha) { showToast('warning','Fecha requerida','Por favor selecciona la fecha de la asistencia.'); if (fechaEl) fechaEl.focus(); return; }

    try {
        const loadingToast = showToast('info','Buscando estudiantes...','Por favor espera un momento.', { autoClose: true, duration: 30000 });

        const response = await fetch(`${API_URL}/estudiantes`, { headers: Auth.getHeaders() });
        if (!response.ok) throw new Error('Error al cargar estudiantes');
        const todosEstudiantes = await response.json();

        estudiantesData = todosEstudiantes.filter(e =>
            e.activo &&
            String(e.colegioId) === String(colegioId) &&
            String(e.jornadaId) === String(jornadaId)
        );

        cerrarToast(loadingToast);

        if (!estudiantesData || estudiantesData.length === 0) {
            const nombreColegio = colegioEl ? colegioEl.options[colegioEl.selectedIndex].text : '—';
            const nombreJornada = jornadaEl ? jornadaEl.options[jornadaEl.selectedIndex].text : '—';
            showToast('warning','Sin estudiantes',
                `No hay estudiantes activos en <strong>${nombreColegio}</strong> - <strong>${nombreJornada}</strong><br><small class="mt-2 d-block">Verifica que el colegio y jornada tengan estudiantes registrados y activos.</small>`
            );
            return;
        }

        await verificarAsistenciasExistentes(fecha, tipoRecorrido);

    } catch (error) {
        console.error('Error:', error);
        showToast('danger','Error al cargar', error.message || 'No se pudieron cargar los estudiantes');
    }
}

// ==========================================
// VERIFICAR ASISTENCIAS EXISTENTES
// ==========================================
async function verificarAsistenciasExistentes(fecha, tipoRecorrido) {
    try {
        const response = await fetch(`${API_URL}/asistencias/fecha/${fecha}`, { headers: Auth.getHeaders() });

        if (!response.ok) {
            mostrarEstudiantesParaRegistro();
            return;
        }

        const asistenciasExistentes = await response.json();
        const estudiantesConAsistencia = asistenciasExistentes
            .filter(a => a.tipoRecorrido === tipoRecorrido)
            .map(a => a.estudianteId);

        const estudiantesDuplicados = estudiantesData.filter(e => estudiantesConAsistencia.includes(e.id));

        if (estudiantesDuplicados.length > 0) {
            const nombresTipo = tipoRecorrido === 'IDA' ? 'IDA (Casa → Colegio)' : 'REGRESO (Colegio → Casa)';
            showAlert('alertContainer', 'warning',
                `<strong><i class="bi bi-exclamation-triangle-fill me-2"></i>Asistencias Ya Registradas</strong><br>
                <strong>${estudiantesDuplicados.length}</strong> de <strong>${estudiantesData.length}</strong> estudiantes ya tienen asistencia registrada de tipo <strong>${nombresTipo}</strong> para la fecha <strong>${fecha}</strong>.<br><br>
                <div class="mt-2">
                    <button class="btn btn-sm btn-primary me-2" onclick="mostrarEstudiantesPendientes()"> <i class="bi bi-filter me-1"></i>Mostrar solo estudiantes sin registro</button>
                    <button class="btn btn-sm btn-secondary" onclick="document.getElementById('alertContainer').innerHTML = ''"> <i class="bi bi-x-circle me-1"></i>Cancelar</button>
                </div>`
            );
        } else {
            mostrarEstudiantesParaRegistro();
        }
    } catch (error) {
        console.log('No se pudieron verificar asistencias existentes, continuando...');
        mostrarEstudiantesParaRegistro();
    }
}

// ==========================================
// MOSTRAR SOLO ESTUDIANTES PENDIENTES
// ==========================================
async function mostrarEstudiantesPendientes() {
    const fechaEl = byId('fechaAsistencia');
    const tipoRecorridoEl = byId('tipoRecorrido');
    const fecha = fechaEl ? fechaEl.value : null;
    const tipoRecorrido = tipoRecorridoEl ? tipoRecorridoEl.value : null;

    try {
        const response = await fetch(`${API_URL}/asistencias/fecha/${fecha}`, { headers: Auth.getHeaders() });
        if (response.ok) {
            const asistenciasExistentes = await response.json();
            const estudiantesConAsistencia = asistenciasExistentes
                .filter(a => a.tipoRecorrido === tipoRecorrido)
                .map(a => a.estudianteId);

            estudiantesData = estudiantesData.filter(e => !estudiantesConAsistencia.includes(e.id));
        }

        if (!estudiantesData || estudiantesData.length === 0) {
            showAlert('alertContainer', 'info',
                `<strong><i class="bi bi-check-circle-fill me-2"></i>Todos registrados</strong><br>Todos los estudiantes ya tienen asistencia registrada para esta fecha y tipo de recorrido.`
            );
            return;
        }

        mostrarEstudiantesParaRegistro();
    } catch (error) {
        mostrarEstudiantesParaRegistro();
    }
}

// ==========================================
// MOSTRAR ESTUDIANTES PARA REGISTRO
// ==========================================
function mostrarEstudiantesParaRegistro() {
    asistenciasRegistradas = {};
    mostrarEstudiantes();

    const seccionFiltros = byId('seccionFiltros');
    const seccionEstudiantes = byId('seccionEstudiantes');
    if (seccionFiltros) seccionFiltros.style.display = 'none';
    if (seccionEstudiantes) seccionEstudiantes.style.display = 'block';

    const step1 = byId('step1');
    const step2 = byId('step2');
    if (step1) { step1.classList.remove('active'); step1.classList.add('completed'); }
    if (step2) step2.classList.add('active');

    const alertContainer = byId('alertContainer');
    if (alertContainer) alertContainer.innerHTML = '';
}

// ==========================================
// MOSTRAR ESTUDIANTES EN TARJETAS
// ==========================================
function mostrarEstudiantes() {
    const container = byId('listaEstudiantes');
    if (!container) return;
    container.innerHTML = '';

    const totalEstudiantesEl = byId('totalEstudiantes');
    if (totalEstudiantesEl) totalEstudiantesEl.textContent = estudiantesData.length || 0;

    estudiantesData.forEach(estudiante => {
        const col = document.createElement('div');
        col.className = 'col-md-6 col-lg-4';

        const primerNombre = estudiante.primerNombre || estudiante.nombre || '';
        const primerApellido = estudiante.primerApellido || estudiante.apellido || '';
        const tipoId = estudiante.tipoId || '';
        const numId = estudiante.numId || '';
        const curso = estudiante.curso || 'N/A';

        col.innerHTML = `
            <div class="student-card" id="card-${estudiante.id}" data-estudiante-id="${estudiante.id}">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <div>
                        <h6 class="mb-1">
                            <i class="bi bi-person-fill text-primary"></i>
                            ${primerNombre} ${primerApellido}
                        </h6>
                        <small class="text-muted">${tipoId} ${numId}</small>
                    </div>
                    <span class="badge bg-info">${curso}</span>
                </div>

                <div class="btn-group w-100" role="group">
                    <button type="button" class="btn btn-sm btn-outline-success btn-state" onclick="marcarEstudiante(${estudiante.id}, 'PRESENTE')">
                        <i class="bi bi-check-circle"></i> Presente
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-state" onclick="marcarEstudiante(${estudiante.id}, 'AUSENTE')">
                        <i class="bi bi-x-circle"></i> Ausente
                    </button>
                </div>
            </div>
        `;

        container.appendChild(col);
    });

    actualizarContadores();
}

// ==========================================
// MARCAR ESTUDIANTES
// ==========================================
function marcarEstudiante(estudianteId, estado) {
    const card = byId(`card-${estudianteId}`);
    // siempre registrar el estado en el objeto (aunque card falte)
    asistenciasRegistradas[estudianteId] = estado;

    if (card) {
        card.classList.remove('selected', 'ausente');
        if (estado === 'PRESENTE') card.classList.add('selected');
        if (estado === 'AUSENTE') card.classList.add('ausente');
    }

    actualizarContadores();
}

function marcarTodos(estado) {
    estudiantesData.forEach(estudiante => {
        marcarEstudiante(estudiante.id, estado);
    });
}

function limpiarSeleccion() {
    asistenciasRegistradas = {};
    document.querySelectorAll('.student-card').forEach(card => {
        card.classList.remove('selected', 'ausente');
    });
    actualizarContadores();
}

// ==========================================
// ACTUALIZAR CONTADORES
// ==========================================
function actualizarContadores() {
    const valores = Object.values(asistenciasRegistradas);
    const presentes = valores.filter(v => v === 'PRESENTE').length;
    const ausentes = valores.filter(v => v === 'AUSENTE').length;
    const total = valores.length;

    const presentesEl = byId('totalPresentes');
    const ausentesEl = byId('totalAusentes');
    const contadorEl = byId('contadorSeleccionados');

    if (presentesEl) presentesEl.textContent = presentes;
    if (ausentesEl) ausentesEl.textContent = ausentes;
    if (contadorEl) contadorEl.textContent = total;
}

// ==========================================
// GUARDAR ASISTENCIAS
// ==========================================
async function guardarAsistencias() {
    const totalMarcados = Object.keys(asistenciasRegistradas).length;
    if (totalMarcados === 0) {
        showAlert('alertContainer', 'warning',
            `<strong><i class="bi bi-info-circle me-2"></i>Sin estudiantes marcados</strong><br>Debes marcar al menos un estudiante como presente o ausente antes de guardar.`
        );
        return;
    }

    try {
        const tipoRecorridoEl = byId('tipoRecorrido');
        const fechaEl = byId('fechaAsistencia');
        const tipoRecorrido = tipoRecorridoEl ? tipoRecorridoEl.value : null;
        const fecha = fechaEl ? fechaEl.value : null;

        const presentes = Object.values(asistenciasRegistradas).filter(v => v === 'PRESENTE').length;
        const ausentes = Object.values(asistenciasRegistradas).filter(v => v === 'AUSENTE').length;

        const dtos = Object.entries(asistenciasRegistradas).map(([estudianteId, estado]) => ({
            estudianteId: parseInt(estudianteId, 10),
            tipoRecorrido: tipoRecorrido,
            estadoAsistencia: estado,
            fecha: fecha,
            observaciones: null
        }));

        showAlert('alertContainer', 'info',
            `<div class="d-flex align-items-center">
                <div class="spinner-border spinner-border-sm me-3" role="status"></div>
                <div><strong>Guardando ${totalMarcados} asistencias...</strong><br><small>${presentes} presentes • ${ausentes} ausentes</small></div>
            </div>`
        );

        // Elegir monitorId de forma segura:
        // Si el usuario tiene una propiedad monitorId prefiera esa, sino use su id.
        const monitorIdCandidate = currentUser && (currentUser.monitorId || currentUser.id) ? (currentUser.monitorId || currentUser.id) : null;
        const monitorQuery = monitorIdCandidate ? `?monitorId=${encodeURIComponent(monitorIdCandidate)}` : '';

        const response = await fetch(`${API_URL}/asistencias/registrar-masivo${monitorQuery}`, {
            method: 'POST',
            headers: { ...Auth.getHeaders(), 'Content-Type': 'application/json' },
            body: JSON.stringify(dtos)
        });

        // Preferir parseo JSON directo si posible
        let resultado;
        try {
            resultado = response.status !== 204 ? await response.json() : [];
        } catch (e) {
            const text = await response.text();
            try { resultado = JSON.parse(text); } catch { resultado = text; }
        }

        if (!response.ok) {
            let errorMessage = 'Error al guardar las asistencias';
            if (resultado && typeof resultado === 'object') {
                errorMessage = resultado.message || JSON.stringify(resultado) || errorMessage;
            } else if (typeof resultado === 'string') {
                errorMessage = resultado;
            }
            throw new Error(errorMessage);
        }

        // Resultado esperado: array con asistencias guardadas
        const exitosos = Array.isArray(resultado) ? resultado.length : (resultado && resultado.successCount) ? resultado.successCount : 0;
        const fallidos = totalMarcados - exitosos;

        const step2 = byId('step2');
        const step3 = byId('step3');
        if (step2) { step2.classList.remove('active'); step2.classList.add('completed'); }
        if (step3) step3.classList.add('completed');

        if (fallidos === 0) {
            showAlert('alertContainer', 'success',
                `<h5 class="alert-heading"><i class="bi bi-check-circle-fill me-2"></i>¡Guardado Exitoso!</h5>
                <p>Se guardaron <strong>${exitosos}</strong> registros de asistencia correctamente.</p>
                <p class="mb-0"><small>✓ ${presentes} presentes • ${ausentes} ausentes</small></p>
                <hr>
                <div class="mt-3">
                    <button class="btn btn-primary btn-sm me-2" onclick="volverAFiltros()"><i class="bi bi-plus-circle me-1"></i>Registrar Otro Grupo</button>
                    <button class="btn btn-success btn-sm" onclick="window.location.href='asistencias.html'"><i class="bi bi-bar-chart-fill me-1"></i>Ver Estadísticas</button>
                </div>`
            );
        } else {
            showAlert('alertContainer', 'warning',
                `<h5 class="alert-heading"><i class="bi bi-exclamation-triangle-fill me-2"></i>Guardado Parcial</h5>
                <p>Se guardaron <strong>${exitosos}</strong> registros correctamente.</p>
                <p><strong>${fallidos}</strong> registros fallaron (posiblemente ya existían o hay problemas de permisos).</p>
                <hr>
                <div class="mt-3">
                    <button class="btn btn-primary btn-sm me-2" onclick="volverAFiltros()"><i class="bi bi-plus-circle me-1"></i>Registrar Otro Grupo</button>
                    <button class="btn btn-success btn-sm" onclick="window.location.href='asistencias.html'"><i class="bi bi-bar-chart-fill me-1"></i>Ver Estadísticas</button>
                </div>`
            );
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('alertContainer', 'danger',
            `<h5 class="alert-heading"><i class="bi bi-x-circle-fill me-2"></i>Error al Guardar</h5>
            <p class="mb-0">No se pudieron guardar las asistencias: ${error.message}</p>`
        );
    }
}

// ==========================================
// VOLVER A FILTROS
// ==========================================
function volverAFiltros() {
    const seccionFiltros = byId('seccionFiltros');
    const seccionEstudiantes = byId('seccionEstudiantes');
    if (seccionFiltros) seccionFiltros.style.display = 'block';
    if (seccionEstudiantes) seccionEstudiantes.style.display = 'none';

    const step1 = byId('step1');
    const step2 = byId('step2');
    const step3 = byId('step3');

    if (step1) { step1.classList.add('active'); step1.classList.remove('completed'); }
    if (step2) step2.classList.remove('active', 'completed');
    if (step3) step3.classList.remove('completed');

    asistenciasRegistradas = {};
    estudiantesData = [];

    const alertContainer = byId('alertContainer');
    if (alertContainer) alertContainer.innerHTML = '';
}

// ==========================================
// LOGOUT
// ==========================================
function logout() {
    Auth.logout();
}