// Lógica de Asistencias - Vistas diferenciadas por rol

// Helper: acceso seguro a elementos por id
function byId(id) {
    return document.getElementById(id);
}

// Variables globales
let currentUser = null;
let monitorData = null;
let asistenciasActuales = [];
let chartDistribucion = null;
let chartTendencia = null;

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', async () => {
    Auth.requireAuth();
    currentUser = Auth.getUser();

    loadNavbarUser();
    configurarMenuPorRol();

    // Cargar datos del monitor si aplica
    await cargarDatosMonitor();

    // Configurar vista según rol
    configurarVistaPorRol();
});

// ==========================================
// CONFIGURAR MENÚ SEGÚN ROL
// ==========================================
function configurarMenuPorRol() {
    const rol = currentUser?.rol;
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
// CARGAR DATOS DEL MONITOR
// ==========================================
async function cargarDatosMonitor() {
    try {
        const response = await fetch(`${API_URL}/monitores/usuario/${currentUser.id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            monitorData = await response.json();
            console.log('Datos del monitor:', monitorData);
        }
    } catch (error) {
        console.error('Error cargando datos del monitor:', error);
    }
}

// ==========================================
// CONFIGURAR VISTA SEGÚN ROL
// ==========================================
function configurarVistaPorRol() {
    const rol = currentUser?.rol;

    if (rol === 'MONITOR') {
        configurarVistaMonitor();
    } else {
        configurarVistaAdminEncargado();
    }
}

// ==========================================
// VISTA MONITOR - Simplificada
// ==========================================
function configurarVistaMonitor() {
    // Ocultar filtros avanzados para monitor
    const filtrosCard = byId('filtrosAvanzados');
    if (filtrosCard) filtrosCard.style.display = 'none';

    // Mostrar info de zona del monitor
    const infoZona = byId('infoZonaMonitor');
    if (infoZona && monitorData) {
        infoZona.style.display = 'block';
        infoZona.innerHTML = `
            <div class="alert alert-info mb-4">
                <i class="bi bi-geo-alt-fill me-2"></i>
                <strong>Tu Zona:</strong> ${monitorData.nombreZona || 'N/A'} |
                <strong>Jornada:</strong> ${monitorData.nombreJornada || 'N/A'}
            </div>
        `;
    }

    // Cargar solo estadísticas de hoy para el monitor
    cargarEstadisticasMonitor();
}

// ==========================================
// VISTA ADMIN/ENCARGADO - Completa
// ==========================================
function configurarVistaAdminEncargado() {
    // Mostrar filtros avanzados
    const filtrosCard = byId('filtrosAvanzados');
    if (filtrosCard) filtrosCard.style.display = 'block';

    // Ocultar info de zona (solo para monitor)
    const infoZona = byId('infoZonaMonitor');
    if (infoZona) infoZona.style.display = 'none';

    // Cargar zonas y colegios para filtros
    cargarZonasFiltro();
    cargarColegiosFiltro();
    establecerFechasDefault();

    // Cargar estadísticas generales
    cargarEstadisticasHoy();
}

// ==========================================
// ESTADÍSTICAS PARA MONITOR
// ==========================================
async function cargarEstadisticasMonitor() {
    try {
        mostrarCargando();

        const hoy = new Date().toISOString().split('T')[0];

        // Cargar asistencias de hoy
        const response = await fetch(`${API_URL}/asistencias/hoy`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar asistencias');

        let asistencias = await response.json();

        // Filtrar solo las de la zona del monitor
        if (monitorData && monitorData.zonaId) {
            asistencias = asistencias.filter(a => {
                // Filtrar por zona (el colegio del estudiante debe estar en la zona del monitor)
                return a.colegioId && monitorData.zonaId;
            });
        }

        // Filtrar solo las registradas por este monitor
        if (monitorData && monitorData.id) {
            const misAsistencias = asistencias.filter(a => a.monitorId === monitorData.id);

            // Mostrar estadísticas
            const stats = calcularEstadisticas(misAsistencias);
            actualizarEstadisticas(stats);

            // Mostrar en tabla
            asistenciasActuales = misAsistencias;
            renderizarTabla(misAsistencias);
            actualizarGraficos(misAsistencias);
        } else {
            // Si no hay monitorData, mostrar todas de hoy
            const stats = calcularEstadisticas(asistencias);
            actualizarEstadisticas(stats);
            asistenciasActuales = asistencias;
            renderizarTabla(asistencias);
            actualizarGraficos(asistencias);
        }

        ocultarCargando();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al cargar las estadísticas', 'danger');
        ocultarCargando();
    }
}

// ==========================================
// CARGAR ZONAS PARA FILTRO
// ==========================================
async function cargarZonasFiltro() {
    try {
        const response = await fetch(`${API_URL}/zonas`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) return;

        const zonas = await response.json();
        const select = byId('filtroZona');
        if (!select) return;

        select.innerHTML = '<option value="">Todas las zonas</option>';
        zonas.filter(z => z.activa !== false).forEach(zona => {
            select.innerHTML += `<option value="${zona.id}">${zona.nombreZona}</option>`;
        });
    } catch (error) {
        console.error('Error cargando zonas:', error);
    }
}

// ==========================================
// CARGAR COLEGIOS PARA FILTRO
// ==========================================
async function cargarColegiosFiltro() {
    try {
        const response = await fetch(`${API_URL}/colegios`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) return;

        const colegios = await response.json();
        const select = byId('filtroColegio');
        if (!select) return;

        select.innerHTML = '<option value="">Todos los colegios</option>';
        colegios.filter(c => c.activo).forEach(colegio => {
            select.innerHTML += `<option value="${colegio.id}">${colegio.nombreColegio}</option>`;
        });
    } catch (error) {
        console.error('Error cargando colegios:', error);
    }
}

// ==========================================
// FECHAS DEFAULT
// ==========================================
function establecerFechasDefault() {
    const hoy = new Date().toISOString().split('T')[0];
    const hace7dias = new Date();
    hace7dias.setDate(hace7dias.getDate() - 7);
    const fecha7dias = hace7dias.toISOString().split('T')[0];

    const inicioEl = byId('fechaInicio');
    const finEl = byId('fechaFin');

    if (inicioEl) inicioEl.value = fecha7dias;
    if (finEl) finEl.value = hoy;
}

// ==========================================
// CARGAR ESTADÍSTICAS DE HOY
// ==========================================
async function cargarEstadisticasHoy() {
    try {
        mostrarCargando();

        const response = await fetch(`${API_URL}/asistencias/estadisticas/hoy`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar estadísticas');

        const stats = await response.json();
        actualizarEstadisticas(stats);

        // Cargar registros de hoy
        await cargarAsistenciasHoy();

        ocultarCargando();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al cargar las estadísticas del día', 'danger');
        ocultarCargando();
    }
}

// ==========================================
// CARGAR ASISTENCIAS DE HOY
// ==========================================
async function cargarAsistenciasHoy() {
    try {
        const response = await fetch(`${API_URL}/asistencias/hoy`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar asistencias');

        const asistencias = await response.json();
        asistenciasActuales = asistencias || [];

        renderizarTabla(asistenciasActuales);
        actualizarGraficos(asistenciasActuales);
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al cargar los registros de asistencia', 'danger');
    }
}

// ==========================================
// APLICAR FILTROS (Solo Admin/Encargado)
// ==========================================
async function aplicarFiltros() {
    const fechaInicio = byId('fechaInicio')?.value;
    const fechaFin = byId('fechaFin')?.value;
    const zonaId = byId('filtroZona')?.value;
    const colegioId = byId('filtroColegio')?.value;
    const estado = byId('filtroEstado')?.value;
    const tipoRecorrido = byId('filtroTipo')?.value;

    if (!fechaInicio || !fechaFin) {
        mostrarAlerta('Por favor selecciona un rango de fechas', 'warning');
        return;
    }

    if (new Date(fechaInicio) > new Date(fechaFin)) {
        mostrarAlerta('La fecha de inicio no puede ser mayor a la fecha fin', 'warning');
        return;
    }

    try {
        mostrarCargando();

        const response = await fetch(`${API_URL}/asistencias/rango-fechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar asistencias');

        let asistencias = await response.json();

        // Aplicar filtros adicionales
        if (colegioId) {
            asistencias = asistencias.filter(a => String(a.colegioId) === String(colegioId));
        }

        if (estado) {
            asistencias = asistencias.filter(a => a.estadoAsistencia === estado);
        }

        if (tipoRecorrido) {
            asistencias = asistencias.filter(a => a.tipoRecorrido === tipoRecorrido);
        }

        asistenciasActuales = asistencias;

        const stats = calcularEstadisticas(asistencias);
        actualizarEstadisticas(stats);

        renderizarTabla(asistencias);
        actualizarGraficos(asistencias);

        mostrarAlerta(`Se encontraron ${asistencias.length} registros`, 'success');
        ocultarCargando();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al aplicar los filtros', 'danger');
        ocultarCargando();
    }
}

// ==========================================
// LIMPIAR FILTROS
// ==========================================
function limpiarFiltros() {
    establecerFechasDefault();

    const filtroZona = byId('filtroZona');
    const filtroColegio = byId('filtroColegio');
    const filtroEstado = byId('filtroEstado');
    const filtroTipo = byId('filtroTipo');

    if (filtroZona) filtroZona.value = '';
    if (filtroColegio) filtroColegio.value = '';
    if (filtroEstado) filtroEstado.value = '';
    if (filtroTipo) filtroTipo.value = '';

    cargarEstadisticasHoy();
}

// ==========================================
// CALCULAR ESTADÍSTICAS
// ==========================================
function calcularEstadisticas(asistencias) {
    const total = asistencias.length;
    const presentes = asistencias.filter(a => a.estadoAsistencia === 'PRESENTE').length;
    const ausentes = asistencias.filter(a => a.estadoAsistencia === 'AUSENTE').length;

    return {
        total: total,
        presentes: presentes,
        ausentes: ausentes,
        porcentajeAsistencia: total > 0 ? ((presentes / total) * 100).toFixed(1) : 0
    };
}

// ==========================================
// ACTUALIZAR ESTADÍSTICAS EN TARJETAS
// ==========================================
function actualizarEstadisticas(stats) {
    const totalEl = byId('totalRegistros');
    const presentesEl = byId('totalPresentes');
    const ausentesEl = byId('totalAusentes');
    const porcentajeEl = byId('porcentajeAsistencia');

    if (totalEl) totalEl.textContent = stats.total || 0;
    if (presentesEl) presentesEl.textContent = stats.presentes || 0;
    if (ausentesEl) ausentesEl.textContent = stats.ausentes || 0;
    if (porcentajeEl) porcentajeEl.textContent = (stats.porcentajeAsistencia || 0) + '%';
}

// ==========================================
// RENDERIZAR TABLA
// ==========================================
function renderizarTabla(asistencias) {
    const tbody = byId('tablaAsistencias');
    const badge = byId('totalTabla');

    if (badge) badge.textContent = `${asistencias.length} registros`;
    if (!tbody) return;

    if (asistencias.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted py-4">
                    <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                    <p class="mt-2">No se encontraron registros</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = asistencias.map(asistencia => {
        const estadoBadge = asistencia.estadoAsistencia === 'PRESENTE'
            ? '<span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Presente</span>'
            : '<span class="badge bg-danger"><i class="bi bi-x-circle me-1"></i>Ausente</span>';

        const tipoRecorrido = asistencia.tipoRecorrido === 'IDA'
            ? '<span class="badge bg-primary">🏠→🏫 Ida</span>'
            : '<span class="badge bg-info">🏫→🏠 Regreso</span>';

        return `
            <tr>
                <td><strong>${formatearFecha(asistencia.fecha)}</strong></td>
                <td><i class="bi bi-clock me-1"></i>${asistencia.horaRegistro || '-'}</td>
                <td>
                    <strong>${asistencia.nombreEstudiante || 'N/A'}</strong><br>
                    <small class="text-muted">${asistencia.numIdEstudiante || ''}</small>
                </td>
                <td><small>${asistencia.nombreColegio || 'N/A'}</small></td>
                <td>${tipoRecorrido}</td>
                <td>${estadoBadge}</td>
                <td><small class="text-muted"><i class="bi bi-person me-1"></i>${asistencia.nombreMonitor || 'N/A'}</small></td>
            </tr>
        `;
    }).join('');
}

// ==========================================
// ACTUALIZAR GRÁFICOS
// ==========================================
async function actualizarGraficos(asistencias) {
    actualizarGraficoDistribucion(asistencias);
    await actualizarGraficoTendencia(asistencias);
}

// ==========================================
// GRÁFICO DE DISTRIBUCIÓN (DONUT)
// ==========================================
function actualizarGraficoDistribucion(asistencias) {
    const presentes = asistencias.filter(a => a.estadoAsistencia === 'PRESENTE').length;
    const ausentes = asistencias.filter(a => a.estadoAsistencia === 'AUSENTE').length;

    if (chartDistribucion) {
        try { chartDistribucion.destroy(); } catch(e) {}
    }

    const canvas = byId('chartDistribucion');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    chartDistribucion = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Presentes', 'Ausentes'],
            datasets: [{
                data: [presentes, ausentes],
                backgroundColor: ['#28a745', '#dc3545'],
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom',
                    labels: { padding: 15, font: { size: 12 } }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                            return `${context.label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

// ==========================================
// GRÁFICO DE ASISTENCIA POR ZONA (BARRAS)
// ==========================================
async function actualizarGraficoTendencia(asistencias) {
    // Cargar colegios para obtener las zonas
    let colegiosMap = {};
    let zonasMap = {};

    try {
        const [colegiosRes, zonasRes] = await Promise.all([
            fetch(`${API_URL}/colegios`, { headers: Auth.getHeaders() }),
            fetch(`${API_URL}/zonas`, { headers: Auth.getHeaders() })
        ]);

        if (colegiosRes.ok && zonasRes.ok) {
            const colegios = await colegiosRes.json();
            const zonas = await zonasRes.json();

            // Mapear zonas por ID
            zonas.forEach(z => {
                zonasMap[z.id] = z.nombreZona;
            });

            // Mapear colegios a zonas
            colegios.forEach(c => {
                colegiosMap[c.id] = zonasMap[c.zonaId] || 'Sin zona';
            });
        }
    } catch (e) {
        console.error('Error cargando datos para gráfico:', e);
    }

    // Agrupar por zona
    const porZona = {};

    asistencias.forEach(a => {
        const zona = colegiosMap[a.colegioId] || 'Sin zona';

        if (!porZona[zona]) {
            porZona[zona] = { presentes: 0, ausentes: 0 };
        }

        if (a.estadoAsistencia === 'PRESENTE') {
            porZona[zona].presentes++;
        } else {
            porZona[zona].ausentes++;
        }
    });

    const zonas = Object.keys(porZona).sort();
    const presentes = zonas.map(z => porZona[z].presentes);
    const ausentes = zonas.map(z => porZona[z].ausentes);

    if (chartTendencia) {
        try { chartTendencia.destroy(); } catch(e) {}
    }

    const canvas = byId('chartTendencia');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    chartTendencia = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: zonas,
            datasets: [
                {
                    label: 'Presentes',
                    data: presentes,
                    backgroundColor: 'rgba(40, 167, 69, 0.8)',
                    borderColor: '#28a745',
                    borderWidth: 1
                },
                {
                    label: 'Ausentes',
                    data: ausentes,
                    backgroundColor: 'rgba(220, 53, 69, 0.8)',
                    borderColor: '#dc3545',
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { position: 'top' },
                title: {
                    display: false
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: { stepSize: 1 }
                },
                x: {
                    ticks: { font: { size: 11 } }
                }
            }
        }
    });
}

// ==========================================
// UTILIDADES
// ==========================================
function formatearFecha(fecha) {
    if (!fecha) return '-';
    const opciones = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(fecha + 'T00:00:00').toLocaleDateString('es-ES', opciones);
}

function formatearFechaCorta(fecha) {
    if (!fecha) return '-';
    const opciones = { month: 'short', day: 'numeric' };
    return new Date(fecha + 'T00:00:00').toLocaleDateString('es-ES', opciones);
}

function mostrarAlerta(mensaje, tipo = 'info') {
    const alertContainer = byId('alertContainer');
    if (!alertContainer) return;

    const iconos = {
        success: 'check-circle-fill',
        danger: 'x-circle-fill',
        warning: 'exclamation-triangle-fill',
        info: 'info-circle-fill'
    };

    alertContainer.innerHTML = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            <i class="bi bi-${iconos[tipo]} me-2"></i>${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) alert.remove();
    }, 5000);
}

function mostrarCargando() {
    const tbody = byId('tablaAsistencias');
    if (!tbody) return;
    tbody.innerHTML = `
        <tr>
            <td colspan="7" class="text-center py-4">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="mt-2 text-muted">Cargando datos...</p>
            </td>
        </tr>
    `;
}

function ocultarCargando() {
    // Completado
}

function logout() {
    Auth.logout();
}
