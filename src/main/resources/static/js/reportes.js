// Verificar autenticación
Auth.requireAuth();

// Obtener usuario actual
const currentUser = Auth.getUser();

// ==========================================
// INICIALIZACIÓN
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    loadNavbarUser();
    updateMenuByRole();
    inicializar();
});

async function inicializar() {
    // Establecer fechas por defecto (último mes)
    const hoy = new Date();
    const primerDiaMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);

    document.getElementById('filtroFechaInicio').value = formatDate(primerDiaMes);
    document.getElementById('filtroFechaFin').value = formatDate(hoy);

    // Cargar dropdowns
    await Promise.all([
        cargarColegios(),
        cargarMonitores(),
        cargarEstudiantes()
    ]);
}

function formatDate(date) {
    return date.toISOString().split('T')[0];
}

// ==========================================
// FUNCIONES DE ALERTA
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

function mostrarLoading(mensaje) {
    Swal.fire({
        title: mensaje,
        html: 'Por favor espere...',
        allowOutsideClick: false,
        allowEscapeKey: false,
        showConfirmButton: false,
        willOpen: () => {
            Swal.showLoading();
        }
    });
}

// ==========================================
// CARGAR DROPDOWNS
// ==========================================

async function cargarColegios() {
    try {
        const response = await fetch(`${API_URL}/colegios`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const colegios = await response.json();
            const selectFiltro = document.getElementById('filtroColegio');
            const selectEstadisticas = document.getElementById('colegioEstadisticas');
            const selectParaEstudiante = document.getElementById('colegioParaEstudiante');

            colegios.filter(c => c.activo).forEach(colegio => {
                const option1 = new Option(colegio.nombreColegio, colegio.id);
                const option2 = new Option(colegio.nombreColegio, colegio.id);
                const option3 = new Option(colegio.nombreColegio, colegio.id);
                selectFiltro.add(option1);
                selectEstadisticas.add(option2);
                selectParaEstudiante.add(option3);
            });
        }
    } catch (error) {
        console.error('Error cargando colegios:', error);
    }
}

async function cargarMonitores() {
    try {
        const response = await fetch(`${API_URL}/monitores`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const monitores = await response.json();
            const select = document.getElementById('filtroMonitor');

            monitores.filter(m => m.activo).forEach(monitor => {
                const nombre = monitor.nombreCompleto || `Monitor ${monitor.id}`;
                select.add(new Option(nombre, monitor.id));
            });
        }
    } catch (error) {
        console.error('Error cargando monitores:', error);
    }
}

async function cargarEstudiantes() {
    try {
        const response = await fetch(`${API_URL}/estudiantes`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const estudiantes = await response.json();

            // Guardar todos los estudiantes para filtrar después
            window.todosLosEstudiantes = estudiantes.filter(e => e.activo);

            const selectFiltro = document.getElementById('filtroEstudiante');
            const selectEstadisticas = document.getElementById('estudianteEstadisticas');

            window.todosLosEstudiantes.forEach(est => {
                const nombre = est.nombreCompleto || `${est.primerNombre} ${est.primerApellido}`;
                const option1 = new Option(nombre, est.id);
                const option2 = new Option(nombre, est.id);
                selectFiltro.add(option1);
                selectEstadisticas.add(option2);
            });
        }
    } catch (error) {
        console.error('Error cargando estudiantes:', error);
    }
}

// Filtrar estudiantes por colegio seleccionado
function cargarEstudiantesPorColegio() {
    const colegioId = document.getElementById('colegioParaEstudiante').value;
    const selectEstudiantes = document.getElementById('estudianteEstadisticas');

    // Limpiar select
    selectEstudiantes.innerHTML = '<option value="">Seleccione estudiante...</option>';

    if (!window.todosLosEstudiantes) return;

    // Filtrar por colegio si está seleccionado
    let estudiantesFiltrados = window.todosLosEstudiantes;
    if (colegioId) {
        estudiantesFiltrados = window.todosLosEstudiantes.filter(
            est => est.colegioId == colegioId
        );
    }

    // Agregar opciones
    estudiantesFiltrados.forEach(est => {
        const nombre = est.nombreCompleto || `${est.primerNombre} ${est.primerApellido}`;
        selectEstudiantes.add(new Option(nombre, est.id));
    });
}

// ==========================================
// REPORTES RÁPIDOS (Sin filtros)
// ==========================================

async function descargarReporte(tipo, formato) {
    mostrarLoading(`Generando reporte de ${tipo}...`);

    try {
        const response = await fetch(`${API_URL}/reportes/${tipo}/${formato}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const blob = await response.blob();
            const extension = formato === 'pdf' ? '.pdf' : '.xlsx';
            descargarArchivo(blob, `${tipo}${extension}`);
            Swal.close();
            mostrarAlertaExito('¡Reporte Generado!', `El reporte de ${tipo} se descargó correctamente.`);
        } else {
            Swal.close();
            mostrarAlertaError('Error', 'No se pudo generar el reporte.');
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.close();
        mostrarAlertaError('Error', 'Error de conexión al generar el reporte.');
    }
}

// ==========================================
// REPORTE DE ASISTENCIAS (Con filtros)
// ==========================================

async function descargarReporteAsistencias(formato) {
    const fechaInicio = document.getElementById('filtroFechaInicio').value;
    const fechaFin = document.getElementById('filtroFechaFin').value;
    const colegioId = document.getElementById('filtroColegio').value;
    const monitorId = document.getElementById('filtroMonitor').value;
    const estudianteId = document.getElementById('filtroEstudiante').value;

    // Construir query params
    let params = new URLSearchParams();
    if (fechaInicio) params.append('fechaInicio', fechaInicio);
    if (fechaFin) params.append('fechaFin', fechaFin);
    if (colegioId) params.append('colegioId', colegioId);
    if (monitorId) params.append('monitorId', monitorId);
    if (estudianteId) params.append('estudianteId', estudianteId);

    const queryString = params.toString() ? `?${params.toString()}` : '';

    mostrarLoading('Generando reporte de asistencias...');

    try {
        const response = await fetch(`${API_URL}/reportes/asistencias/${formato}${queryString}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const blob = await response.blob();
            const extension = formato === 'pdf' ? '.pdf' : '.xlsx';
            const fechaArchivo = new Date().toISOString().split('T')[0];
            descargarArchivo(blob, `asistencias_${fechaArchivo}${extension}`);
            Swal.close();
            mostrarAlertaExito('¡Reporte Generado!', 'El reporte de asistencias se descargó correctamente.');
        } else {
            Swal.close();
            mostrarAlertaError('Error', 'No se pudo generar el reporte de asistencias.');
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.close();
        mostrarAlertaError('Error', 'Error de conexión al generar el reporte.');
    }
}

function limpiarFiltrosAsistencias() {
    const hoy = new Date();
    const primerDiaMes = new Date(hoy.getFullYear(), hoy.getMonth(), 1);

    document.getElementById('filtroFechaInicio').value = formatDate(primerDiaMes);
    document.getElementById('filtroFechaFin').value = formatDate(hoy);
    document.getElementById('filtroColegio').value = '';
    document.getElementById('filtroMonitor').value = '';
    document.getElementById('filtroEstudiante').value = '';
}

// ==========================================
// REPORTES ESTADÍSTICOS
// ==========================================

async function descargarEstadisticasGenerales() {
    const fecha = document.getElementById('fechaEstGeneral').value;
    const queryString = fecha ? `?fecha=${fecha}` : '';

    mostrarLoading('Generando estadísticas generales...');

    try {
        const response = await fetch(`${API_URL}/reportes/estadisticas/general${queryString}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const blob = await response.blob();
            descargarArchivo(blob, 'estadisticas_generales.pdf');
            Swal.close();
            mostrarAlertaExito('¡Reporte Generado!', 'Las estadísticas generales se descargaron correctamente.');
        } else {
            Swal.close();
            mostrarAlertaError('Error', 'No se pudo generar el reporte.');
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.close();
        mostrarAlertaError('Error', 'Error de conexión al generar el reporte.');
    }
}

async function descargarEstadisticasColegio() {
    const colegioId = document.getElementById('colegioEstadisticas').value;
    const fecha = document.getElementById('fechaEstColegio').value;

    if (!colegioId) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar un colegio.');
        return;
    }

    const queryString = fecha ? `?fecha=${fecha}` : '';

    mostrarLoading('Generando estadísticas del colegio...');

    try {
        const response = await fetch(`${API_URL}/reportes/estadisticas/colegio/${colegioId}${queryString}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const blob = await response.blob();
            descargarArchivo(blob, `estadisticas_colegio_${colegioId}.pdf`);
            Swal.close();
            mostrarAlertaExito('¡Reporte Generado!', 'Las estadísticas del colegio se descargaron correctamente.');
        } else {
            Swal.close();
            mostrarAlertaError('Error', 'No se pudo generar el reporte.');
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.close();
        mostrarAlertaError('Error', 'Error de conexión al generar el reporte.');
    }
}

async function descargarEstadisticasEstudiante() {
    const estudianteId = document.getElementById('estudianteEstadisticas').value;
    const fechaInicio = document.getElementById('fechaInicioEst').value;
    const fechaFin = document.getElementById('fechaFinEst').value;

    if (!estudianteId) {
        mostrarAlertaError('Campo Requerido', 'Debe seleccionar un estudiante.');
        return;
    }

    let params = new URLSearchParams();
    if (fechaInicio) params.append('fechaInicio', fechaInicio);
    if (fechaFin) params.append('fechaFin', fechaFin);

    const queryString = params.toString() ? `?${params.toString()}` : '';

    mostrarLoading('Generando estadísticas del estudiante...');

    try {
        const response = await fetch(`${API_URL}/reportes/estadisticas/estudiante/${estudianteId}${queryString}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const blob = await response.blob();
            descargarArchivo(blob, `estadisticas_estudiante_${estudianteId}.pdf`);
            Swal.close();
            mostrarAlertaExito('¡Reporte Generado!', 'Las estadísticas del estudiante se descargaron correctamente.');
        } else {
            Swal.close();
            mostrarAlertaError('Error', 'No se pudo generar el reporte.');
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.close();
        mostrarAlertaError('Error', 'Error de conexión al generar el reporte.');
    }
}

// ==========================================
// UTILIDADES
// ==========================================

function descargarArchivo(blob, nombreArchivo) {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = nombreArchivo;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

// ==========================================
// LOGOUT
// ==========================================

function logout() {
    Auth.logout();
}