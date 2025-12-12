// Registro de asistencias por lote - CORREGIDO
// Filtros en cascada: Zona → Colegio → Jornada

// Helper: acceso seguro a elementos por id
function byId(id) {
    return document.getElementById(id);
}

// Estado global
let currentUser = null;
let monitorData = null; // Datos del monitor (zona, jornada, monitorId)
let estudiantesData = [];
let asistenciasRegistradas = {};

// ==========================================
// SISTEMA DE NOTIFICACIONES TOAST
// ==========================================
function showToast(type, title, message, options = {}) {
    const defaults = { autoClose: false, duration: 10000, showTimer: false };
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
    `;

    toastContainer.appendChild(toastDiv);

    if (config.autoClose) {
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
// SHOW ALERT
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
// INICIALIZACIÓN
// ==========================================
document.addEventListener('DOMContentLoaded', async () => {
    try {
        Auth.requireAuth();
    } catch (e) {
        console.error('No autenticado:', e);
        return;
    }

    currentUser = Auth.getUser();
    loadNavbarUser?.();
    configurarMenuPorRol();

    // Establecer fecha de hoy
   const ahora = new Date();
      const hoy = ahora.getFullYear() + '-' +
                  String(ahora.getMonth() + 1).padStart(2, '0') + '-' +
                  String(ahora.getDate()).padStart(2, '0');
      const fechaEl = byId('fechaAsistencia');
      if (fechaEl) {
          fechaEl.value = hoy;

          // MONITOR solo puede registrar asistencias del día actual
          if (currentUser.rol === 'MONITOR') {
              fechaEl.min = hoy;
              fechaEl.max = hoy;
              fechaEl.disabled = true;
          }
      }

    // Cargar datos del monitor para obtener el monitorId
    await cargarDatosMonitor();

    // Cargar zonas para iniciar la cascada
    await cargarZonas();
});

// ==========================================
// CONFIGURAR MENÚ SEGÚN ROL
// ==========================================
function configurarMenuPorRol() {
    const rol = currentUser ? currentUser.rol : null;
    const show = id => { const el = byId(id); if (el) el.style.display = 'block'; };

    if (rol === 'ADMINISTRADOR') {
        ['menuUsuarios','menuEstudiantes','menuRutas','menuColegios','menuAsistencias','menuNotificaciones','menuReportes'].forEach(show);
    } else if (rol === 'ENCARGADO') {
        ['menuEstudiantes','menuRutas','menuColegios','menuAsistencias','menuNotificaciones','menuReportes'].forEach(show);
    } else if (rol === 'MONITOR') {
        ['menuEstudiantes','menuAsistencias'].forEach(show);
    }
}

// ==========================================
// CARGAR DATOS DEL MONITOR (para obtener monitorId)
// ==========================================
async function cargarDatosMonitor() {
    try {
        const response = await fetch(`${API_URL}/monitores/usuario/${currentUser.id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            monitorData = await response.json();
            console.log('Datos del monitor:', monitorData);
        } else {
            console.warn('No se encontró registro de monitor para este usuario');
            monitorData = null;
        }
    } catch (error) {
        console.error('Error cargando datos del monitor:', error);
        monitorData = null;
    }
}

// ==========================================
// CARGAR ZONAS
// ==========================================
async function cargarZonas() {
    try {
        const response = await fetch(`${API_URL}/zonas`, { headers: Auth.getHeaders() });
        if (!response.ok) throw new Error('Error al cargar zonas');

        const zonas = await response.json();
        const select = byId('filtroZona');
        if (!select) return;

        select.innerHTML = '<option value="">Seleccione zona...</option>';

        // Si es MONITOR, solo mostrar su zona
        if (currentUser.rol === 'MONITOR' && monitorData) {
            const zonaMonitor = zonas.find(z => z.id === monitorData.zonaId);
            if (zonaMonitor) {
                select.innerHTML = `<option value="${zonaMonitor.id}" selected>${zonaMonitor.nombreZona}</option>`;
                select.disabled = true;
                // Cargar colegios automáticamente
                await cargarColegiosPorZona();
            }
        } else {
            // Admin/Encargado ven todas las zonas
            zonas.filter(z => z.activa !== false).forEach(zona => {
                select.innerHTML += `<option value="${zona.id}">${zona.nombreZona}</option>`;
            });
        }
    } catch (error) {
        console.error('Error cargando zonas:', error);
        showAlert('alertContainer', 'danger', 'Error al cargar las zonas');
    }
}

// ==========================================
// CARGAR COLEGIOS POR ZONA
// ==========================================
async function cargarColegiosPorZona() {
    const zonaId = byId('filtroZona')?.value;
    const selectColegio = byId('filtroColegio');
    const selectJornada = byId('filtroJornada');

    if (!selectColegio) return;

    selectColegio.innerHTML = '<option value="">Seleccione colegio...</option>';
    selectColegio.disabled = true;

    if (selectJornada) {
        selectJornada.innerHTML = '<option value="">Primero seleccione colegio</option>';
        selectJornada.disabled = true;
    }

    if (!zonaId) return;

    try {
        const response = await fetch(`${API_URL}/colegios/zona/${zonaId}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const colegios = await response.json();
            colegios.filter(c => c.activo).forEach(colegio => {
                selectColegio.innerHTML += `<option value="${colegio.id}">${colegio.nombreColegio}</option>`;
            });
            selectColegio.disabled = false;
        }
    } catch (error) {
        console.error('Error cargando colegios:', error);
    }
}

// ==========================================
// CARGAR JORNADAS POR COLEGIO
// ==========================================
async function cargarJornadasPorColegio() {
    const colegioId = byId('filtroColegio')?.value;
    const selectJornada = byId('filtroJornada');

    if (!selectJornada) return;

    selectJornada.innerHTML = '<option value="">Seleccione jornada...</option>';
    selectJornada.disabled = true;

    if (!colegioId) {
        selectJornada.innerHTML = '<option value="">Primero seleccione colegio</option>';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/colegio-jornadas/colegio/${colegioId}/activas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const jornadas = await response.json();

            jornadas.forEach(cj => {
                selectJornada.innerHTML += `<option value="${cj.jornadaId}">${cj.nombreJornada}</option>`;
            });

            selectJornada.disabled = false;
        }
    } catch (error) {
        console.error('Error cargando jornadas:', error);
        selectJornada.innerHTML = '<option value="">Error al cargar</option>';
    }
}

// ==========================================
// CARGAR ESTUDIANTES SEGÚN FILTROS
// ==========================================
async function cargarEstudiantes() {
    const zonaId = byId('filtroZona')?.value;
    const colegioId = byId('filtroColegio')?.value;
    const jornadaId = byId('filtroJornada')?.value;
    const tipoRecorrido = byId('tipoRecorrido')?.value;
    const fecha = byId('fechaAsistencia')?.value;

    // Limpiar alertas
    const alertContainer = byId('alertContainer');
    if (alertContainer) alertContainer.innerHTML = '';
    cerrarTodosLosToasts();

    // Validaciones
    if (!zonaId) { showToast('warning', 'Zona requerida', 'Por favor selecciona una zona.'); return; }
    if (!colegioId) { showToast('warning', 'Colegio requerido', 'Por favor selecciona un colegio.'); return; }
    if (!jornadaId) { showToast('warning', 'Jornada requerida', 'Por favor selecciona una jornada.'); return; }
    if (!tipoRecorrido) { showToast('warning', 'Tipo requerido', 'Por favor selecciona IDA o REGRESO.'); return; }
    if (!fecha) { showToast('warning', 'Fecha requerida', 'Por favor selecciona la fecha.'); return; }

    try {
        const loadingToast = showToast('info', 'Buscando estudiantes...', 'Por favor espera.', { autoClose: true, duration: 30000 });

        // Cargar estudiantes filtrados
        const response = await fetch(`${API_URL}/estudiantes`, { headers: Auth.getHeaders() });
        if (!response.ok) throw new Error('Error al cargar estudiantes');

        const todosEstudiantes = await response.json();

        // Filtrar por colegio, jornada y estado activo
        estudiantesData = todosEstudiantes.filter(e =>
            e.estadoInscripcion === 'ACTIVA' &&
            String(e.colegioId) === String(colegioId) &&
            String(e.jornadaId) === String(jornadaId)
        );

        cerrarToast(loadingToast);

        if (!estudiantesData || estudiantesData.length === 0) {
            const nombreColegio = byId('filtroColegio')?.options[byId('filtroColegio').selectedIndex]?.text || '';
            const nombreJornada = byId('filtroJornada')?.options[byId('filtroJornada').selectedIndex]?.text || '';
            showToast('warning', 'Sin estudiantes',
                `No hay estudiantes activos en <strong>${nombreColegio}</strong> - <strong>${nombreJornada}</strong>`
            );
            return;
        }

        await verificarAsistenciasExistentes(fecha, tipoRecorrido);

    } catch (error) {
        console.error('Error:', error);
        showToast('danger', 'Error al cargar', error.message || 'No se pudieron cargar los estudiantes');
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
                <strong>${estudiantesDuplicados.length}</strong> de <strong>${estudiantesData.length}</strong> estudiantes ya tienen asistencia de tipo <strong>${nombresTipo}</strong> para <strong>${fecha}</strong>.<br><br>
                <div class="mt-2">
                    <button class="btn btn-sm btn-primary me-2" onclick="mostrarEstudiantesPendientes()">
                        <i class="bi bi-filter me-1"></i>Mostrar solo pendientes
                    </button>
                    <button class="btn btn-sm btn-secondary" onclick="mostrarEstudiantesParaRegistro()">
                        <i class="bi bi-people me-1"></i>Mostrar todos
                    </button>
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
    const fecha = byId('fechaAsistencia')?.value;
    const tipoRecorrido = byId('tipoRecorrido')?.value;

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
                `<strong><i class="bi bi-check-circle-fill me-2"></i>Todos registrados</strong><br>
                Todos los estudiantes ya tienen asistencia registrada.`
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

    byId('seccionFiltros').style.display = 'none';
    byId('seccionEstudiantes').style.display = 'block';

    byId('step1')?.classList.remove('active');
    byId('step1')?.classList.add('completed');
    byId('step2')?.classList.add('active');

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

    const totalEl = byId('totalEstudiantes');
    if (totalEl) totalEl.textContent = estudiantesData.length || 0;

    estudiantesData.forEach(estudiante => {
        const col = document.createElement('div');
        col.className = 'col-md-6 col-lg-4';

        const nombre = `${estudiante.primerNombre || ''} ${estudiante.primerApellido || ''}`.trim();
        const documento = `${estudiante.tipoId || ''} ${estudiante.numId || ''}`.trim();
        const curso = estudiante.curso || 'N/A';

        col.innerHTML = `
            <div class="student-card" id="card-${estudiante.id}" data-estudiante-id="${estudiante.id}">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <div>
                        <h6 class="mb-1">
                            <i class="bi bi-person-fill text-primary"></i>
                            ${nombre}
                        </h6>
                        <small class="text-muted">${documento}</small>
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
    asistenciasRegistradas[estudianteId] = estado;

    const card = byId(`card-${estudianteId}`);
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
    const totalMarcados = valores.length;
    const totalEstudiantes = estudiantesData.length;
    const faltantes = totalEstudiantes - totalMarcados;

    const presentesEl = byId('totalPresentes');
    const ausentesEl = byId('totalAusentes');
    const contadorEl = byId('contadorSeleccionados');

    if (presentesEl) presentesEl.textContent = presentes;
    if (ausentesEl) ausentesEl.textContent = ausentes;

    // Mostrar cuántos faltan o si está completo
    if (contadorEl) {
        if (faltantes > 0) {
            contadorEl.textContent = `Faltan ${faltantes}`;
            contadorEl.closest('button')?.classList.remove('btn-success');
            contadorEl.closest('button')?.classList.add('btn-warning');
        } else {
            contadorEl.textContent = `${totalMarcados} estudiantes`;
            contadorEl.closest('button')?.classList.remove('btn-warning');
            contadorEl.closest('button')?.classList.add('btn-success');
        }
    }
}

// ==========================================
// GUARDAR ASISTENCIAS
// ==========================================
async function guardarAsistencias() {
    const totalMarcados = Object.keys(asistenciasRegistradas).length;
    const totalEstudiantes = estudiantesData.length;

    // Validar que TODOS los estudiantes estén marcados
    if (totalMarcados === 0) {
        showAlert('alertContainer', 'warning',
            `<strong><i class="bi bi-info-circle me-2"></i>Sin estudiantes marcados</strong><br>
            Debes marcar todos los estudiantes antes de guardar.`
        );
        return;
    }

    if (totalMarcados < totalEstudiantes) {
        const faltantes = totalEstudiantes - totalMarcados;
        showAlert('alertContainer', 'warning',
            `<strong><i class="bi bi-exclamation-triangle me-2"></i>Faltan estudiantes por marcar</strong><br>
            Debes marcar <strong>todos</strong> los estudiantes. Faltan <strong>${faltantes}</strong> por marcar.`
        );
        return;
    }

    // Verificar que tenemos monitorId
    if (!monitorData || !monitorData.id) {
        showAlert('alertContainer', 'danger',
            `<strong><i class="bi bi-exclamation-triangle me-2"></i>Error de configuración</strong><br>
            No se encontró el registro de monitor para tu usuario. Contacta al administrador.`
        );
        return;
    }

    try {
        const tipoRecorrido = byId('tipoRecorrido')?.value;
        const fecha = byId('fechaAsistencia')?.value;

        const presentes = Object.values(asistenciasRegistradas).filter(v => v === 'PRESENTE').length;
        const ausentes = Object.values(asistenciasRegistradas).filter(v => v === 'AUSENTE').length;

        // Construir DTOs según RegistrarAsistenciaDTO
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
                <div><strong>Guardando ${totalMarcados} asistencias...</strong><br>
                <small>${presentes} presentes • ${ausentes} ausentes</small></div>
            </div>`
        );

        // Usar el monitorId correcto (de la tabla Monitor, no del Usuario)
        const response = await fetch(`${API_URL}/asistencias/registrar-masivo?monitorId=${monitorData.id}`, {
            method: 'POST',
            headers: { ...Auth.getHeaders(), 'Content-Type': 'application/json' },
            body: JSON.stringify(dtos)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || 'Error al guardar las asistencias');
        }

        const resultado = await response.json();
        const exitosos = Array.isArray(resultado) ? resultado.length : 0;
        const fallidos = totalMarcados - exitosos;

        // Actualizar pasos
        byId('step2')?.classList.remove('active');
        byId('step2')?.classList.add('completed');
        byId('step3')?.classList.add('completed');

        if (fallidos === 0) {
            showAlert('alertContainer', 'success',
                `<h5 class="alert-heading"><i class="bi bi-check-circle-fill me-2"></i>¡Guardado Exitoso!</h5>
                <p>Se guardaron <strong>${exitosos}</strong> registros correctamente.</p>
                <p class="mb-0"><small>✓ ${presentes} presentes • ${ausentes} ausentes</small></p>
                <hr>
                <div class="mt-3">
                    <button class="btn btn-primary btn-sm me-2" onclick="volverAFiltros()">
                        <i class="bi bi-plus-circle me-1"></i>Registrar Otro Grupo
                    </button>
                    <button class="btn btn-success btn-sm" onclick="window.location.href='asistencias.html'">
                        <i class="bi bi-bar-chart-fill me-1"></i>Ver Estadísticas
                    </button>
                </div>`
            );
        } else {
            showAlert('alertContainer', 'warning',
                `<h5 class="alert-heading"><i class="bi bi-exclamation-triangle-fill me-2"></i>Guardado Parcial</h5>
                <p>Se guardaron <strong>${exitosos}</strong> registros. <strong>${fallidos}</strong> fallaron (posiblemente duplicados).</p>
                <hr>
                <div class="mt-3">
                    <button class="btn btn-primary btn-sm me-2" onclick="volverAFiltros()">
                        <i class="bi bi-plus-circle me-1"></i>Registrar Otro Grupo
                    </button>
                </div>`
            );
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('alertContainer', 'danger',
            `<h5 class="alert-heading"><i class="bi bi-x-circle-fill me-2"></i>Error al Guardar</h5>
            <p class="mb-0">${error.message}</p>`
        );
    }
}

// ==========================================
// VOLVER A FILTROS
// ==========================================
function volverAFiltros() {
    byId('seccionFiltros').style.display = 'block';
    byId('seccionEstudiantes').style.display = 'none';

    byId('step1')?.classList.add('active');
    byId('step1')?.classList.remove('completed');
    byId('step2')?.classList.remove('active', 'completed');
    byId('step3')?.classList.remove('completed');

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