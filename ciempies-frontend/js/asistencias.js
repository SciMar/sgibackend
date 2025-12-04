let chartTendencia;
let chartDistribucion;
let asistenciasData = [];

// Cargar información del usuario
function loadUserInfo() {
    const user = getCurrentUser();
    if (user) {
        document.getElementById('userName').textContent =
            `${user.primerNombre} ${user.primerApellido}`;
        document.getElementById('userRole').textContent = user.rol;
    }
}

// Cargar colegios para el filtro
async function cargarColegios() {
    try {
        const colegios = await API.get('/colegios');
        const select = document.getElementById('filtroColegio');

        colegios.forEach(colegio => {
            const option = document.createElement('option');
            option.value = colegio.id;
            option.textContent = colegio.nombreColegio;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando colegios:', error);
    }
}

// Cargar estadísticas de hoy
async function cargarEstadisticasHoy() {
    const hoy = new Date().toISOString().split('T')[0];
    document.getElementById('fechaInicio').value = hoy;
    document.getElementById('fechaFin').value = hoy;
    await aplicarFiltros();
}

// Aplicar filtros y cargar datos
async function aplicarFiltros() {
    const fechaInicio = document.getElementById('fechaInicio').value;
    const fechaFin = document.getElementById('fechaFin').value;
    const colegioId = document.getElementById('filtroColegio').value;
    const estado = document.getElementById('filtroEstado').value;

    if (!fechaInicio || !fechaFin) {
        showAlert('Por favor selecciona un rango de fechas', 'warning');
        return;
    }

    try {
        // Construir URL con parámetros
        let url = `/asistencias?fechaInicio=${fechaInicio}&fechaFin=${fechaFin}`;
        if (colegioId) url += `&colegioId=${colegioId}`;
        if (estado) url += `&estado=${estado}`;

        asistenciasData = await API.get(url);

        // Actualizar estadísticas
        actualizarEstadisticas(asistenciasData);

        // Actualizar gráficos
        actualizarGraficos(asistenciasData);

        // Actualizar tabla
        actualizarTabla(asistenciasData);

    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al cargar las asistencias', 'danger');
    }
}

// Actualizar estadísticas
function actualizarEstadisticas(datos) {
    const total = datos.length;
    const presentes = datos.filter(a => a.estadoAsistencia === 'PRESENTE').length;
    const ausentes = datos.filter(a => a.estadoAsistencia === 'AUSENTE').length;
    const porcentaje = total > 0 ? ((presentes / total) * 100).toFixed(1) : 0;

    document.getElementById('totalRegistros').textContent = total;
    document.getElementById('totalPresentes').textContent = presentes;
    document.getElementById('totalAusentes').textContent = ausentes;
    document.getElementById('porcentajeAsistencia').textContent = porcentaje + '%';
}

// Actualizar gráficos
function actualizarGraficos(datos) {
    // Agrupar por fecha
    const porFecha = {};
    datos.forEach(asistencia => {
        const fecha = asistencia.fecha;
        if (!porFecha[fecha]) {
            porFecha[fecha] = { presentes: 0, ausentes: 0 };
        }
        if (asistencia.estadoAsistencia === 'PRESENTE') {
            porFecha[fecha].presentes++;
        } else {
            porFecha[fecha].ausentes++;
        }
    });

    const fechas = Object.keys(porFecha).sort();
    const presentes = fechas.map(f => porFecha[f].presentes);
    const ausentes = fechas.map(f => porFecha[f].ausentes);

    // Gráfico de tendencia
    if (chartTendencia) chartTendencia.destroy();

    const ctxTendencia = document.getElementById('chartTendencia').getContext('2d');
    chartTendencia = new Chart(ctxTendencia, {
        type: 'line',
        data: {
            labels: fechas.map(f => new Date(f).toLocaleDateString('es-CO')),
            datasets: [
                {
                    label: 'Presentes',
                    data: presentes,
                    borderColor: '#28a745',
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    tension: 0.4,
                    fill: true
                },
                {
                    label: 'Ausentes',
                    data: ausentes,
                    borderColor: '#dc3545',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    tension: 0.4,
                    fill: true
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    }
                }
            }
        }
    });

    // Gráfico de distribución (pie)
    if (chartDistribucion) chartDistribucion.destroy();

    const totalPresentes = datos.filter(a => a.estadoAsistencia === 'PRESENTE').length;
    const totalAusentes = datos.filter(a => a.estadoAsistencia === 'AUSENTE').length;

    const ctxDistribucion = document.getElementById('chartDistribucion').getContext('2d');
    chartDistribucion = new Chart(ctxDistribucion, {
        type: 'doughnut',
        data: {
            labels: ['Presentes', 'Ausentes'],
            datasets: [{
                data: [totalPresentes, totalAusentes],
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
                    position: 'bottom'
                }
            }
        }
    });
}

// Actualizar tabla
function actualizarTabla(datos) {
    const tbody = document.getElementById('tablaAsistencias');
    document.getElementById('totalTabla').textContent = `${datos.length} registros`;

    if (datos.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted">
                    No se encontraron registros con los filtros seleccionados
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = '';

    // Ordenar por fecha y hora (más recientes primero)
    datos.sort((a, b) => new Date(b.fecha + ' ' + b.hora) - new Date(a.fecha + ' ' + a.hora));

    datos.forEach(asistencia => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${new Date(asistencia.fecha).toLocaleDateString('es-CO')}</td>
            <td>${asistencia.hora || 'N/A'}</td>
            <td>${asistencia.nombreEstudiante || 'N/A'}</td>
            <td>${asistencia.nombreColegio || 'N/A'}</td>
            <td><span class="badge bg-info">${asistencia.tipoRecorrido}</span></td>
            <td>
                <span class="badge ${asistencia.estadoAsistencia === 'PRESENTE' ? 'bg-success' : 'bg-danger'}">
                    ${asistencia.estadoAsistencia}
                </span>
            </td>
            <td>${asistencia.nombreMonitor || 'N/A'}</td>
        `;
        tbody.appendChild(tr);
    });
}

// Limpiar filtros
function limpiarFiltros() {
    document.getElementById('fechaInicio').value = '';
    document.getElementById('fechaFin').value = '';
    document.getElementById('filtroColegio').value = '';
    document.getElementById('filtroEstado').value = '';

    // Limpiar tablas y gráficos
    document.getElementById('tablaAsistencias').innerHTML = `
        <tr>
            <td colspan="7" class="text-center text-muted">
                Selecciona un rango de fechas para ver los registros
            </td>
        </tr>
    `;

    document.getElementById('totalRegistros').textContent = '0';
    document.getElementById('totalPresentes').textContent = '0';
    document.getElementById('totalAusentes').textContent = '0';
    document.getElementById('porcentajeAsistencia').textContent = '0%';
    document.getElementById('totalTabla').textContent = '0 registros';

    if (chartTendencia) chartTendencia.destroy();
    if (chartDistribucion) chartDistribucion.destroy();
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
    cargarColegios();
    cargarEstadisticasHoy(); // Cargar estadísticas de hoy por defecto
});