// Cargar información del usuario
function loadUserInfo() {
    const user = getCurrentUser();
    if (user) {
        document.getElementById('userName').textContent =
            `${user.primerNombre} ${user.primerApellido}`;
        document.getElementById('userRole').textContent = user.rol;
    }
}

// Descargar reporte tabular
async function descargarReporte(tipo, formato) {
    const button = event.target.closest('button');
    const originalText = button.innerHTML;

    try {
        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Descargando...';

        const response = await fetch(
            `${API_BASE_URL}/reportes/${tipo}/${formato}`,
            {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('token')}`
                }
            }
        );

        if (!response.ok) {
            throw new Error('Error al generar el reporte');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${tipo}.${formato === 'excel' ? 'xlsx' : 'pdf'}`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(url);

        showAlert(`Reporte de ${tipo} descargado exitosamente`, 'success');

    } catch (error) {
        console.error('Error:', error);
        showAlert(error.message || 'Error al descargar el reporte', 'danger');
    } finally {
        button.disabled = false;
        button.innerHTML = originalText;
    }
}

// Descargar estadísticas generales
async function descargarEstadisticasGenerales(event) {
    event.preventDefault();

    const fecha = document.getElementById('fechaGeneral').value;
    const button = event.target.querySelector('button[type="submit"]');
    const originalText = button.innerHTML;

    try {
        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Generando...';

        let url = `${API_BASE_URL}/reportes/estadisticas/general`;
        if (fecha) {
            url += `?fecha=${fecha}`;
        }

        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error('Error al generar el reporte estadístico');
        }

        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = `estadisticas-general.pdf`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(downloadUrl);

        showAlert('Reporte estadístico descargado exitosamente', 'success');

    } catch (error) {
        console.error('Error:', error);
        showAlert(error.message || 'Error al descargar el reporte', 'danger');
    } finally {
        button.disabled = false;
        button.innerHTML = originalText;
    }
}

// Descargar estadísticas por estudiante
async function descargarEstadisticasEstudiante(event) {
    event.preventDefault();

    const estudianteId = document.getElementById('estudianteId').value;
    const fechaInicio = document.getElementById('fechaInicioEst').value;
    const fechaFin = document.getElementById('fechaFinEst').value;
    const button = event.target.querySelector('button[type="submit"]');
    const originalText = button.innerHTML;

    try {
        button.disabled = true;
        button.innerHTML = '<span class="spinner-border spinner-border-sm"></span> Generando...';

        let url = `${API_BASE_URL}/reportes/estadisticas/estudiante/${estudianteId}`;
        const params = [];
        if (fechaInicio) params.push(`fechaInicio=${fechaInicio}`);
        if (fechaFin) params.push(`fechaFin=${fechaFin}`);
        if (params.length > 0) url += `?${params.join('&')}`;

        const response = await fetch(url, {
            headers: {
                'Authorization': `Bearer ${localStorage.getItem('token')}`
            }
        });

        if (!response.ok) {
            throw new Error('Error al generar el reporte estadístico');
        }

        const blob = await response.blob();
        const downloadUrl = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = downloadUrl;
        a.download = `estadisticas-estudiante-${estudianteId}.pdf`;
        document.body.appendChild(a);
        a.click();
        a.remove();
        window.URL.revokeObjectURL(downloadUrl);

        showAlert('Reporte estadístico descargado exitosamente', 'success');

    } catch (error) {
        console.error('Error:', error);
        showAlert(error.message || 'Error al descargar el reporte', 'danger');
    } finally {
        button.disabled = false;
        button.innerHTML = originalText;
    }
}

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
});