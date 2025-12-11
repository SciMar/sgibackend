// Lógica de Asistencias (corregido)

// Helper: acceso seguro a elementos por id
function byId(id) {
    return document.getElementById(id);
}

// Verificar autenticación y obtener usuario al iniciar
let currentUser = null;

// Inicializar al cargar la página (UN SOLO listener)
document.addEventListener('DOMContentLoaded', () => {
    Auth.requireAuth();
    Auth.requireRoles(['ADMINISTRADOR', 'ENCARGADO', 'MONITOR']);

    currentUser = Auth.getUser();

    // Inicializaciones
    loadNavbarUser();
    configurarMenuPorRol(currentUser);
    cargarDatosUsuario();
    cargarColegios();
    establecerFechasDefault();
    cargarEstadisticasHoy();
});

// Configurar menú según rol (usa currentUser pasado o global)
function configurarMenuPorRol(user) {
    const rol = user ? user.rol : null;

    // Helper para mostrar un menú si el elemento existe
    const show = id => {
        const el = byId(id);
        if (el) el.style.display = 'block';
    };

    // Opcional: esconder menús por defecto no hace aquí; asumimos que el CSS oculta lo necesario
    if (rol === 'ADMINISTRADOR') {
        show('menuUsuarios');
        show('menuEstudiantes');
        show('menuRutas');
        show('menuColegios');
        show('menuAsistencias');
        show('menuNotificaciones');
        show('menuReportes');
    } else if (rol === 'ENCARGADO') {
        show('menuEstudiantes');
        show('menuRutas');
        show('menuColegios');
        show('menuAsistencias');
        show('menuNotificaciones');
        show('menuReportes');
    } else if (rol === 'MONITOR') {
        show('menuEstudiantes');
        show('menuAsistencias');
    }
}

// ==========================================
// AUTENTICACIÓN Y USUARIO
// ==========================================
function cargarDatosUsuario() {
    const usuario = Auth.getUser();
    if (!usuario) return;

    const elName = byId('userName');
    if (elName) elName.textContent = usuario.nombre || 'Usuario';

    const elRole = byId('userRole');
    if (elRole) elRole.textContent = usuario.rol || 'Sin rol';
}

function logout() {
    Auth.logout();
}

// ==========================================
// CARGAR COLEGIOS PARA FILTRO
// ==========================================
async function cargarColegios() {
    try {
        const response = await fetch(`${CONFIG.API_BASE_URL}/colegios`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar colegios');

        const colegios = await response.json();
        const select = byId('filtroColegio');
        if (!select) return;

        select.innerHTML = '<option value="">Todos los colegios</option>';
        colegios.forEach(colegio => {
            select.innerHTML += `<option value="${colegio.id}">${colegio.nombre}</option>`;
        });
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al cargar la lista de colegios', 'danger');
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

        const response = await fetch(`${CONFIG.API_BASE_URL}/asistencias/estadisticas/hoy`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar estadísticas');

        const stats = await response.json();

        // Actualizar tarjetas de estadísticas
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
        const response = await fetch(`${CONFIG.API_BASE_URL}/asistencias/hoy`, {
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
// APLICAR FILTROS
// ==========================================
async function aplicarFiltros() {
    const fechaInicioEl = byId('fechaInicio');
    const fechaFinEl = byId('fechaFin');
    const filtroColegioEl = byId('filtroColegio');
    const filtroEstadoEl = byId('filtroEstado');

    const fechaInicio = fechaInicioEl ? fechaInicioEl.value : null;
    const fechaFin = fechaFinEl ? fechaFinEl.value : null;
    const colegioId = filtroColegioEl ? filtroColegioEl.value : '';
    const estado = filtroEstadoEl ? filtroEstadoEl.value : '';

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

        let url = `${CONFIG.API_BASE_URL}/asistencias/rango-fechas?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;

        const response = await fetch(url, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar asistencias');

        let asistencias = await response.json();

        // Aplicar filtros adicionales en el cliente (uso seguro de propiedades opcionales)
        if (colegioId) {
            asistencias = asistencias.filter(a => (a.colegio && String(a.colegio.id)) == String(colegioId));
        }

        if (estado) {
            asistencias = asistencias.filter(a => a.estadoAsistencia === estado);
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
    const filtroColegioEl = byId('filtroColegio');
    const filtroEstadoEl = byId('filtroEstado');
    if (filtroColegioEl) filtroColegioEl.value = '';
    if (filtroEstadoEl) filtroEstadoEl.value = '';
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
            ? '<span class="badge bg-success"><i class="bi bi-check-circle"></i> PRESENTE</span>'
            : '<span class="badge bg-danger"><i class="bi bi-x-circle"></i> AUSENTE</span>';

        const tipoRecorrido = asistencia.tipoRecorrido === 'IDA'
            ? '<span class="badge bg-primary"><i class="bi bi-arrow-right-circle"></i> IDA</span>'
            : '<span class="badge bg-info"><i class="bi bi-arrow-left-circle"></i> REGRESO</span>';

        const estudianteNombre = asistencia.estudiante ? asistencia.estudiante.nombre : 'Desconocido';
        const estudianteDocumento = asistencia.estudiante ? asistencia.estudiante.documento : '';
        const colegioNombre = asistencia.colegio ? asistencia.colegio.nombre : '';
        const monitorNombre = asistencia.monitor ? asistencia.monitor.nombre : '';

        return `
            <tr>
                <td><strong>${formatearFecha(asistencia.fecha)}</strong></td>
                <td>
                    <i class="bi bi-clock"></i> ${asistencia.horaRegistro || ''}
                </td>
                <td>
                    <div>
                        <strong>${estudianteNombre}</strong><br>
                        <small class="text-muted">
                            <i class="bi bi-person-badge"></i> ${estudianteDocumento}
                        </small>
                    </div>
                </td>
                <td>
                    <small>${colegioNombre}</small>
                </td>
                <td>${tipoRecorrido}</td>
                <td>${estadoBadge}</td>
                <td>
                    <small class="text-muted">
                        <i class="bi bi-person"></i> ${monitorNombre}
                    </small>
                </td>
            </tr>
        `;
    }).join('');
}

// ==========================================
// ACTUALIZAR GRÁFICOS
// ==========================================
function actualizarGraficos(asistencias) {
    actualizarGraficoDistribucion(asistencias);
    actualizarGraficoTendencia(asistencias);
}

// ==========================================
// GRÁFICO DE DISTRIBUCIÓN (PIE)
// ==========================================
function actualizarGraficoDistribucion(asistencias) {
    const presentes = asistencias.filter(a => a.estadoAsistencia === 'PRESENTE').length;
    const ausentes = asistencias.filter(a => a.estadoAsistencia === 'AUSENTE').length;

    if (chartDistribucion) {
        try { chartDistribucion.destroy(); } catch(e){ /* ignore */ }
    }

    const canvas = byId('chartDistribucion');
    if (!canvas || !canvas.getContext) return;

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
                    labels: {
                        padding: 15,
                        font: { size: 12 }
                    }
                },
                tooltip: {
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.parsed || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = total > 0 ? ((value / total) * 100).toFixed(1) : 0;
                            return `${label}: ${value} (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

// ==========================================
// GRÁFICO DE TENDENCIA (LÍNEA)
// ==========================================
function actualizarGraficoTendencia(asistencias) {
    const porFecha = {};

    asistencias.forEach(asistencia => {
        const fecha = asistencia.fecha;
        if (!fecha) return;
        if (!porFecha[fecha]) {
            porFecha[fecha] = { presentes: 0, ausentes: 0, total: 0 };
        }
        porFecha[fecha].total++;
        if (asistencia.estadoAsistencia === 'PRESENTE') {
            porFecha[fecha].presentes++;
        } else {
            porFecha[fecha].ausentes++;
        }
    });

    const fechasOrdenadas = Object.keys(porFecha).sort();
    const presentes = fechasOrdenadas.map(f => porFecha[f].presentes);
    const ausentes = fechasOrdenadas.map(f => porFecha[f].ausentes);
    const labels = fechasOrdenadas.map(f => formatearFecha(f));

    if (chartTendencia) {
        try { chartTendencia.destroy(); } catch(e){ /* ignore */ }
    }

    const canvas = byId('chartTendencia');
    if (!canvas || !canvas.getContext) return;

    const ctx = canvas.getContext('2d');
    chartTendencia = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Presentes',
                    data: presentes,
                    borderColor: '#28a745',
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 4,
                    pointHoverRadius: 6
                },
                {
                    label: 'Ausentes',
                    data: ausentes,
                    borderColor: '#dc3545',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    tension: 0.4,
                    fill: true,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            plugins: {
                legend: { position: 'top', labels: { padding: 15, font: { size: 12 } } },
                tooltip: { backgroundColor: 'rgba(0,0,0,0.8)', padding: 12 }
            },
            scales: {
                y: { beginAtZero: true, ticks: { stepSize: 1, font: { size: 11 } }, grid: { color: 'rgba(0,0,0,0.05)' } },
                x: { ticks: { font: { size: 11 } }, grid: { display: false } }
            }
        }
    });
}

// ==========================================
// UTILIDADES
// ==========================================
function formatearFecha(fecha) {
    const opciones = { year: 'numeric', month: 'short', day: 'numeric' };
    return new Date(fecha + 'T00:00:00').toLocaleDateString('es-ES', opciones);
}

function mostrarAlerta(mensaje, tipo = 'info') {
    const alertContainer = byId('alertContainer');
    if (!alertContainer) {
        console.warn('Alerta: elemento alertContainer no encontrado. Mensaje:', mensaje);
        return;
    }

    const iconos = {
        success: 'check-circle-fill',
        danger: 'x-circle-fill',
        warning: 'exclamation-triangle-fill',
        info: 'info-circle-fill'
    };

    const alerta = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            <i class="bi bi-${iconos[tipo]}"></i>
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    alertContainer.innerHTML = alerta;

    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) {
            alert.classList.remove('show');
            setTimeout(() => { alertContainer.innerHTML = ''; }, 150);
        }
    }, 5000);
}

function mostrarCargando() {
    const tbody = byId('tablaAsistencias');
    if (!tbody) return;
    tbody.innerHTML = `
        <tr>
            <td colspan="7" class="text-center py-4">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <p class="mt-2 text-muted">Cargando datos...</p>
            </td>
        </tr>
    `;
}

function ocultarCargando() {
    console.log('Carga completada');
}