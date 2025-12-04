let estudiantesData = [];
let asistenciasRegistradas = {};

// Cargar información del usuario
function loadUserInfo() {
    const user = getCurrentUser();
    if (user) {
        document.getElementById('userName').textContent =
            `${user.primerNombre} ${user.primerApellido}`;
        document.getElementById('userRole').textContent = user.rol;
    }
}

// Cargar colegios
async function cargarColegios() {
    try {
        const colegios = await API.get('/colegios');
        const select = document.getElementById('filtroColegio');

        select.innerHTML = '<option value="">Seleccione...</option>';
        colegios.forEach(colegio => {
            const option = document.createElement('option');
            option.value = colegio.id;
            option.textContent = colegio.nombreColegio;
            option.dataset.zonaId = colegio.zonaId;
            select.appendChild(option);
        });

    } catch (error) {
        console.error('Error cargando colegios:', error);
        showAlert('Error al cargar los colegios', 'danger');
    }
}

// Cargar jornadas según el colegio seleccionado
async function cargarJornadas() {
    const colegioSelect = document.getElementById('filtroColegio');
    const jornadaSelect = document.getElementById('filtroJornada');
    const colegioId = colegioSelect.value;

    if (!colegioId) {
        jornadaSelect.innerHTML = '<option value="">Primero seleccione colegio</option>';
        jornadaSelect.disabled = true;
        return;
    }

    try {
        // Obtener zona del colegio
        const selectedOption = colegioSelect.options[colegioSelect.selectedIndex];
        const zonaId = selectedOption.dataset.zonaId;

        // Obtener jornadas de esa zona
        const jornadas = await API.get(`/jornadas/zona/${zonaId}`);

        jornadaSelect.innerHTML = '<option value="">Seleccione...</option>';
        jornadas.forEach(jornada => {
            const option = document.createElement('option');
            option.value = jornada.id;
            option.textContent = `${jornada.nombreJornada} - ${jornada.codigoJornada}`;
            jornadaSelect.appendChild(option);
        });

        jornadaSelect.disabled = false;

    } catch (error) {
        console.error('Error cargando jornadas:', error);
        jornadaSelect.innerHTML = '<option value="">Error cargando jornadas</option>';
    }
}

// Cargar estudiantes según filtros
async function cargarEstudiantes() {
    const colegioId = document.getElementById('filtroColegio').value;
    const jornadaId = document.getElementById('filtroJornada').value;
    const tipoRecorrido = document.getElementById('tipoRecorrido').value;
    const fecha = document.getElementById('fechaAsistencia').value;

    // Validar que todos los campos estén llenos
    if (!colegioId || !jornadaId || !tipoRecorrido || !fecha) {
        showAlert('Por favor completa todos los campos', 'warning');
        return;
    }

    try {
        // Obtener estudiantes del colegio y jornada
        const todosEstudiantes = await API.get('/estudiantes');

        estudiantesData = todosEstudiantes.filter(e =>
            e.activo &&
            e.colegioId == colegioId &&
            e.jornadaId == jornadaId
        );

        if (estudiantesData.length === 0) {
            showAlert('No se encontraron estudiantes con esos filtros', 'info');
            return;
        }

        // Resetear asistencias
        asistenciasRegistradas = {};

        // Mostrar estudiantes
        mostrarEstudiantes();

        // Cambiar a paso 2
        document.getElementById('seccionFiltros').style.display = 'none';
        document.getElementById('seccionEstudiantes').style.display = 'block';

        // Actualizar indicadores
        document.getElementById('step1').classList.remove('active');
        document.getElementById('step1').classList.add('completed');
        document.getElementById('step2').classList.add('active');

    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al cargar los estudiantes', 'danger');
    }
}

// Mostrar estudiantes en tarjetas
function mostrarEstudiantes() {
    const container = document.getElementById('listaEstudiantes');
    container.innerHTML = '';

    document.getElementById('totalEstudiantes').textContent = estudiantesData.length;

    estudiantesData.forEach(estudiante => {
        const col = document.createElement('div');
        col.className = 'col-md-6 col-lg-4';

        col.innerHTML = `
            <div class="student-card" id="card-${estudiante.id}" data-estudiante-id="${estudiante.id}">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <div>
                        <h6 class="mb-1">
                            <i class="bi bi-person-fill text-primary"></i>
                            ${estudiante.primerNombre} ${estudiante.primerApellido}
                        </h6>
                        <small class="text-muted">
                            ${estudiante.tipoId} ${estudiante.numId}
                        </small>
                    </div>
                    <span class="badge bg-info">${estudiante.curso || 'N/A'}</span>
                </div>

                <div class="btn-group w-100" role="group">
                    <button type="button" class="btn btn-sm btn-outline-success btn-state"
                            onclick="marcarEstudiante(${estudiante.id}, 'PRESENTE')">
                        <i class="bi bi-check-circle"></i> Presente
                    </button>
                    <button type="button" class="btn btn-sm btn-outline-danger btn-state"
                            onclick="marcarEstudiante(${estudiante.id}, 'AUSENTE')">
                        <i class="bi bi-x-circle"></i> Ausente
                    </button>
                </div>
            </div>
        `;

        container.appendChild(col);
    });

    actualizarContadores();
}

// Marcar un estudiante específico
function marcarEstudiante(estudianteId, estado) {
    const card = document.getElementById(`card-${estudianteId}`);

    // Guardar el estado
    asistenciasRegistradas[estudianteId] = estado;

    // Actualizar visualmente
    card.classList.remove('selected', 'ausente');

    if (estado === 'PRESENTE') {
        card.classList.add('selected');
    } else if (estado === 'AUSENTE') {
        card.classList.add('ausente');
    }

    actualizarContadores();
}

// Marcar todos los estudiantes
function marcarTodos(estado) {
    estudiantesData.forEach(estudiante => {
        marcarEstudiante(estudiante.id, estado);
    });
}

// Limpiar selección
function limpiarSeleccion() {
    asistenciasRegistradas = {};

    document.querySelectorAll('.student-card').forEach(card => {
        card.classList.remove('selected', 'ausente');
    });

    actualizarContadores();
}

// Actualizar contadores
function actualizarContadores() {
    const valores = Object.values(asistenciasRegistradas);
    const presentes = valores.filter(v => v === 'PRESENTE').length;
    const ausentes = valores.filter(v => v === 'AUSENTE').length;
    const total = valores.length;

    document.getElementById('totalPresentes').textContent = presentes;
    document.getElementById('totalAusentes').textContent = ausentes;
    document.getElementById('contadorSeleccionados').textContent = total;
}

// Guardar asistencias
async function guardarAsistencias() {
    const totalMarcados = Object.keys(asistenciasRegistradas).length;

    if (totalMarcados === 0) {
        showAlert('Debes marcar al menos un estudiante', 'warning');
        return;
    }

    // Confirmar
    const confirmacion = confirm(
        `¿Estás seguro de guardar ${totalMarcados} registros de asistencia?\n\n` +
        `Presentes: ${Object.values(asistenciasRegistradas).filter(v => v === 'PRESENTE').length}\n` +
        `Ausentes: ${Object.values(asistenciasRegistradas).filter(v => v === 'AUSENTE').length}`
    );

    if (!confirmacion) return;

    try {
        const user = getCurrentUser();
        const tipoRecorrido = document.getElementById('tipoRecorrido').value;
        const fecha = document.getElementById('fechaAsistencia').value;

        // Preparar array de promesas
        const promesas = [];

        for (const [estudianteId, estado] of Object.entries(asistenciasRegistradas)) {
            const datos = {
                estudianteId: parseInt(estudianteId),
                tipoRecorrido: tipoRecorrido,
                estadoAsistencia: estado,
                observaciones: null
            };

            // Agregar fecha si no es hoy
            const hoy = new Date().toISOString().split('T')[0];
            let url = `/asistencias?monitorId=${user.id}`;
            if (fecha !== hoy) {
                url += `&fecha=${fecha}`;
            }

            promesas.push(API.post(url, datos));
        }

        // Ejecutar todas las peticiones
        showAlert('⏳ Guardando asistencias...', 'info');

        await Promise.all(promesas);

        // Actualizar paso 3
        document.getElementById('step2').classList.remove('active');
        document.getElementById('step2').classList.add('completed');
        document.getElementById('step3').classList.add('completed');

        showAlert(
            `✅ Se guardaron exitosamente ${totalMarcados} registros de asistencia`,
            'success'
        );

        // Preguntar si desea registrar otro lote
        setTimeout(() => {
            if (confirm('¿Deseas registrar asistencias de otro grupo?')) {
                volverAFiltros();
            } else {
                window.location.href = 'asistencias.html';
            }
        }, 2000);

    } catch (error) {
        console.error('Error:', error);
        showAlert('Error al guardar las asistencias: ' + error.message, 'danger');
    }
}

// Volver a filtros
function volverAFiltros() {
    document.getElementById('seccionFiltros').style.display = 'block';
    document.getElementById('seccionEstudiantes').style.display = 'none';

    // Resetear indicadores
    document.getElementById('step1').classList.add('active');
    document.getElementById('step1').classList.remove('completed');
    document.getElementById('step2').classList.remove('active', 'completed');
    document.getElementById('step3').classList.remove('completed');

    // Limpiar datos
    asistenciasRegistradas = {};
    estudiantesData = [];
}

// Event listeners
document.getElementById('filtroColegio')?.addEventListener('change', cargarJornadas);

// Establecer fecha de hoy por defecto
document.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
    cargarColegios();

    const hoy = new Date().toISOString().split('T')[0];
    document.getElementById('fechaAsistencia').value = hoy;
});