// Lógica de Gestión de Usuarios

// Variables globales
let currentUser = null;
let usuariosData = [];
let usuariosFiltrados = [];
let zonasData = [];
let jornadasData = [];
let monitoresData = [];

// Constante de contraseña por defecto
const CONTRASENA_DEFAULT = 'Ciempies2024!';

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
// VALIDACIONES
// =============================================

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

// Validar formato de email
function validarEmail(email) {
    if (!email || !email.trim()) {
        return { valido: false, mensaje: 'El correo electrónico es obligatorio' };
    }

    const regexEmail = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!regexEmail.test(email.trim())) {
        return { valido: false, mensaje: 'El formato del correo electrónico no es válido. Ejemplo: correo@dominio.com' };
    }

    return { valido: true };
}

// Helper
function byId(id) {
    return document.getElementById(id);
}

// Inicialización
document.addEventListener('DOMContentLoaded', async () => {
    Auth.requireAuth();
    Auth.requireRoles(['ADMINISTRADOR']);

    currentUser = Auth.getUser();
    loadNavbarUser();
    configurarMenuPorRol();

    await cargarDatosIniciales();
});

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
// CARGAR DATOS INICIALES
// ==========================================
async function cargarDatosIniciales() {
    try {
        await Promise.all([
            cargarUsuarios(),
            cargarZonas(),
            cargarMonitores()
        ]);
    } catch (error) {
        console.error('Error cargando datos:', error);
        mostrarAlerta('Error al cargar datos iniciales', 'danger');
    }
}

// ==========================================
// CARGAR USUARIOS
// ==========================================
async function cargarUsuarios() {
    try {
        const response = await fetch(`${API_URL}/usuarios`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cargar usuarios');

        usuariosData = await response.json();
        usuariosFiltrados = [...usuariosData];
        renderizarTabla();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta('Error al cargar usuarios', 'danger');
    }
}

// ==========================================
// CARGAR ZONAS
// ==========================================
async function cargarZonas() {
    try {
        const response = await fetch(`${API_URL}/zonas`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            zonasData = await response.json();
        }
    } catch (error) {
        console.error('Error cargando zonas:', error);
    }
}

// ==========================================
// CARGAR MONITORES (para saber asignaciones)
// ==========================================
async function cargarMonitores() {
    try {
        const response = await fetch(`${API_URL}/monitores`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            monitoresData = await response.json();
        }
    } catch (error) {
        console.error('Error cargando monitores:', error);
    }
}

// ==========================================
// CARGAR JORNADAS POR ZONA
// ==========================================
async function cargarJornadasPorZona() {
    const zonaId = byId('asignarZona')?.value;
    const selectJornada = byId('asignarJornada');

    if (!zonaId || !selectJornada) {
        if (selectJornada) selectJornada.innerHTML = '<option value="">Primero seleccione zona</option>';
        return;
    }

    try {
        const response = await fetch(`${API_URL}/jornadas/zona/${zonaId}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            jornadasData = await response.json();

            selectJornada.innerHTML = '<option value="">Seleccione jornada...</option>';
            jornadasData.filter(j => j.activa !== false).forEach(j => {
                selectJornada.innerHTML += `<option value="${j.id}">${j.nombreJornada}</option>`;
            });
        }
    } catch (error) {
        console.error('Error cargando jornadas:', error);
    }
}

// ==========================================
// RENDERIZAR TABLA
// ==========================================
function renderizarTabla() {
    const tbody = byId('tableBody');
    const totalEl = byId('totalUsuarios');

    if (totalEl) totalEl.textContent = `${usuariosFiltrados.length} usuarios`;
    if (!tbody) return;

    if (usuariosFiltrados.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" class="text-center text-muted py-4">
                    <i class="bi bi-inbox" style="font-size: 3rem;"></i>
                    <p class="mt-2">No se encontraron usuarios</p>
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = usuariosFiltrados.map(usuario => {
        const nombreCompleto = `${usuario.primerNombre} ${usuario.segundoNombre || ''} ${usuario.primerApellido} ${usuario.segundoApellido || ''}`.trim();

        // Buscar asignación de monitor
        const monitor = monitoresData.find(m => m.usuarioId === usuario.id);
        let asignacionHtml = '<span class="text-muted">-</span>';

        if (monitor) {
            asignacionHtml = `
                <small>
                    <i class="bi bi-geo-alt text-primary"></i> ${monitor.nombreZona || 'N/A'}<br>
                    <i class="bi bi-clock text-info"></i> ${monitor.nombreJornada || 'N/A'}
                </small>
            `;
        }

        // Badge de rol
        const rolBadges = {
            'ADMINISTRADOR': 'bg-danger',
            'ENCARGADO': 'bg-warning text-dark',
            'MONITOR': 'bg-info'
        };

        // Badge de estado
        const estadoBadge = usuario.activo
            ? '<span class="badge bg-success"><i class="bi bi-check-circle me-1"></i>Activo</span>'
            : '<span class="badge bg-secondary"><i class="bi bi-x-circle me-1"></i>Inactivo</span>';

        return `
            <tr>
                <td><strong>${usuario.id}</strong></td>
                <td>
                    <small class="text-muted">${usuario.tipoId}</small><br>
                    ${usuario.numId}
                </td>
                <td><strong>${nombreCompleto}</strong></td>
                <td><a href="mailto:${usuario.email}">${usuario.email}</a></td>
                <td><span class="badge ${rolBadges[usuario.rol] || 'bg-secondary'}">${usuario.rol}</span></td>
                <td>${asignacionHtml}</td>
                <td>${estadoBadge}</td>
                <td>
                    <div class="dropdown">
                        <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown">
                            <i class="bi bi-three-dots-vertical"></i>
                        </button>
                        <ul class="dropdown-menu dropdown-menu-end">
                            <li>
                                <a class="dropdown-item" href="#" onclick="mostrarModalEditar(${usuario.id})">
                                    <i class="bi bi-pencil-fill text-primary me-2"></i>Editar
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="#" onclick="mostrarModalAsignar(${usuario.id})">
                                    <i class="bi bi-geo-alt-fill text-info me-2"></i>Asignar Zona/Jornada
                                </a>
                            </li>
                            <li>
                                <a class="dropdown-item" href="#" onclick="resetearContrasena(${usuario.id})">
                                    <i class="bi bi-key-fill text-warning me-2"></i>Resetear Contraseña
                                </a>
                            </li>
                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <a class="dropdown-item" href="#" onclick="toggleEstado(${usuario.id}, ${usuario.activo})">
                                    ${usuario.activo
                                        ? '<i class="bi bi-x-circle-fill text-danger me-2"></i>Desactivar'
                                        : '<i class="bi bi-check-circle-fill text-success me-2"></i>Activar'
                                    }
                                </a>
                            </li>
                            ${usuario.id !== currentUser.id ? `
                            <li><hr class="dropdown-divider"></li>
                            <li>
                                <a class="dropdown-item text-danger" href="#" onclick="eliminarUsuario(${usuario.id})">
                                    <i class="bi bi-trash-fill me-2"></i>Eliminar
                                </a>
                            </li>
                            ` : ''}
                        </ul>
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

// ==========================================
// FILTRAR USUARIOS
// ==========================================
function filtrarUsuarios() {
    const rol = byId('filtroRol')?.value;
    const estado = byId('filtroEstado')?.value;
    const busqueda = byId('busqueda')?.value?.toLowerCase() || '';

    usuariosFiltrados = usuariosData.filter(u => {
        if (rol && u.rol !== rol) return false;
        if (estado !== '' && String(u.activo) !== estado) return false;
        if (busqueda) {
            const nombreCompleto = `${u.primerNombre} ${u.segundoNombre || ''} ${u.primerApellido} ${u.segundoApellido || ''}`.toLowerCase();
            if (!nombreCompleto.includes(busqueda) &&
                !u.email.toLowerCase().includes(busqueda) &&
                !u.numId.includes(busqueda)) {
                return false;
            }
        }
        return true;
    });

    renderizarTabla();
}

function buscarUsuarios() {
    filtrarUsuarios();
}

// ==========================================
// MODAL CREAR USUARIO
// ==========================================
function mostrarModalCrear() {
    byId('modalTitle').innerHTML = '<i class="bi bi-person-plus-fill me-2"></i>Crear Usuario';
    byId('formUsuario').reset();
    byId('usuarioId').value = '';
    byId('modoEdicion').value = 'false';
    byId('infoContrasena').style.display = 'block';

    new bootstrap.Modal(byId('modalUsuario')).show();
}

// ==========================================
// MODAL EDITAR USUARIO
// ==========================================
function mostrarModalEditar(id) {
    const usuario = usuariosData.find(u => u.id === id);
    if (!usuario) return;

    byId('modalTitle').innerHTML = '<i class="bi bi-pencil-fill me-2"></i>Editar Usuario';
    byId('usuarioId').value = usuario.id;
    byId('modoEdicion').value = 'true';
    byId('tipoId').value = usuario.tipoId;
    byId('numId').value = usuario.numId;
    byId('primerNombre').value = usuario.primerNombre;
    byId('segundoNombre').value = usuario.segundoNombre || '';
    byId('primerApellido').value = usuario.primerApellido;
    byId('segundoApellido').value = usuario.segundoApellido || '';
    byId('email').value = usuario.email;
    byId('rol').value = usuario.rol;
    byId('infoContrasena').style.display = 'none';

    new bootstrap.Modal(byId('modalUsuario')).show();
}

// ==========================================
// GUARDAR USUARIO
// ==========================================
async function guardarUsuario() {

    // =============================================
    // VALIDACIONES
    // =============================================

    // Tipo de ID
    const tipoId = byId('tipoId').value;
    if (!tipoId) {
        mostrarAlertaError('Debe seleccionar un tipo de identificación');
        byId('tipoId').focus();
        return;
    }

    // Número de ID
    const numId = byId('numId').value;
    const valNumId = validarNumeroId(numId);
    if (!valNumId.valido) {
        mostrarAlertaError(valNumId.mensaje);
        byId('numId').focus();
        return;
    }

    // Primer nombre
    const primerNombre = byId('primerNombre').value;
    const valPrimerNombre = validarSoloLetras(primerNombre, 'Primer Nombre');
    if (!valPrimerNombre.valido) {
        mostrarAlertaError(valPrimerNombre.mensaje);
        byId('primerNombre').focus();
        return;
    }

    // Segundo nombre (opcional, pero si tiene debe ser válido)
    const segundoNombre = byId('segundoNombre').value;
    if (segundoNombre && segundoNombre.trim()) {
        const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/;
        if (!soloLetras.test(segundoNombre.trim())) {
            mostrarAlertaError('El segundo nombre solo debe contener letras');
            byId('segundoNombre').focus();
            return;
        }
    }

    // Primer apellido
    const primerApellido = byId('primerApellido').value;
    const valPrimerApellido = validarSoloLetras(primerApellido, 'Primer Apellido');
    if (!valPrimerApellido.valido) {
        mostrarAlertaError(valPrimerApellido.mensaje);
        byId('primerApellido').focus();
        return;
    }

    // Segundo apellido (opcional, pero si tiene debe ser válido)
    const segundoApellido = byId('segundoApellido').value;
    if (segundoApellido && segundoApellido.trim()) {
        const soloLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\s]+$/;
        if (!soloLetras.test(segundoApellido.trim())) {
            mostrarAlertaError('El segundo apellido solo debe contener letras');
            byId('segundoApellido').focus();
            return;
        }
    }

    // Email
    const email = byId('email').value;
    const valEmail = validarEmail(email);
    if (!valEmail.valido) {
        mostrarAlertaError(valEmail.mensaje);
        byId('email').focus();
        return;
    }

    // Rol
    const rol = byId('rol').value;
    if (!rol) {
        mostrarAlertaError('Debe seleccionar un rol');
        byId('rol').focus();
        return;
    }

    // =============================================
    // CONSTRUIR DATOS Y ENVIAR
    // =============================================

    const id = byId('usuarioId').value;
    const esEdicion = byId('modoEdicion').value === 'true';

    const datos = {
        tipoId: tipoId,
        numId: numId.trim(),
        primerNombre: primerNombre.trim(),
        segundoNombre: segundoNombre ? segundoNombre.trim() : null,
        primerApellido: primerApellido.trim(),
        segundoApellido: segundoApellido ? segundoApellido.trim() : null,
        email: email.trim(),
        rol: rol
    };

    // Si es creación, agregar contraseña por defecto
    if (!esEdicion) {
        datos.contrasena = CONTRASENA_DEFAULT;
        datos.primerIngreso = true;
    }

    try {
        const url = esEdicion ? `${API_URL}/usuarios/${id}` : `${API_URL}/usuarios`;
        const method = esEdicion ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: {
                ...Auth.getHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datos)
        });

        if (!response.ok) {
            const errorText = await response.text();

            // Manejar errores específicos del backend
            // IMPORTANTE: Verificar email PRIMERO porque "ya existe" también aparece en ese mensaje
            if (errorText.toLowerCase().includes('email') || errorText.toLowerCase().includes('correo')) {
                mostrarAlertaAdvertencia('El correo electrónico ya está registrado en el sistema');
            } else if (errorText.toLowerCase().includes('num_id') || errorText.toLowerCase().includes('numid') || errorText.toLowerCase().includes('identificación')) {
                mostrarAlertaAdvertencia('El número de identificación ya está registrado en el sistema');
            } else if (errorText.toLowerCase().includes('ya existe') || errorText.toLowerCase().includes('duplicado')) {
                mostrarAlertaAdvertencia('El usuario ya existe en el sistema');
            } else if (response.status === 409) {
                mostrarAlertaAdvertencia('El usuario ya existe en el sistema');
            } else {
                mostrarAlertaError('Error al guardar: ' + errorText);
            }
            return;
        }

        const usuarioGuardado = await response.json();

        // Si es creación y el rol no es ADMINISTRADOR, crear registro de monitor
        if (!esEdicion && datos.rol !== 'ADMINISTRADOR') {
            await crearRegistroMonitor(usuarioGuardado.id);
        }

        bootstrap.Modal.getInstance(byId('modalUsuario')).hide();
        mostrarAlertaExito(esEdicion ? 'Usuario actualizado correctamente' : 'Usuario creado correctamente');
        await cargarDatosIniciales();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error de conexión. Verifique que el servidor esté activo.');
    }
}

// ==========================================
// CREAR REGISTRO DE MONITOR
// ==========================================
async function crearRegistroMonitor(usuarioId) {
    try {
        // Crear monitor con zona y jornada por defecto (primera disponible)
        const zonaDefault = zonasData.find(z => z.activa !== false);
        if (!zonaDefault) return;

        // Obtener jornadas de esa zona
        const response = await fetch(`${API_URL}/jornadas/zona/${zonaDefault.id}`, {
            headers: Auth.getHeaders()
        });

        if (!response.ok) return;

        const jornadas = await response.json();
        const jornadaDefault = jornadas.find(j => j.activa !== false);
        if (!jornadaDefault) return;

        // Crear el monitor
        await fetch(`${API_URL}/monitores`, {
            method: 'POST',
            headers: {
                ...Auth.getHeaders(),
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                usuarioId: usuarioId,
                zonaId: zonaDefault.id,
                jornadaId: jornadaDefault.id
            })
        });
    } catch (error) {
        console.error('Error creando registro de monitor:', error);
    }
}

// ==========================================
// MODAL ASIGNAR ZONA/JORNADA
// ==========================================
function mostrarModalAsignar(id) {
    const usuario = usuariosData.find(u => u.id === id);
    if (!usuario) return;

    const nombreCompleto = `${usuario.primerNombre} ${usuario.primerApellido}`;
    byId('asignarUsuarioId').value = id;
    byId('asignarUsuarioNombre').textContent = `${nombreCompleto} (${usuario.rol})`;

    // Cargar zonas
    const selectZona = byId('asignarZona');
    selectZona.innerHTML = '<option value="">Seleccione zona...</option>';
    zonasData.filter(z => z.activa !== false).forEach(z => {
        selectZona.innerHTML += `<option value="${z.id}">${z.nombreZona}</option>`;
    });

    // Resetear jornada
    byId('asignarJornada').innerHTML = '<option value="">Primero seleccione zona</option>';

    // Mostrar asignación actual si existe
    const monitor = monitoresData.find(m => m.usuarioId === id);
    const infoActual = byId('infoAsignacionActual');

    if (monitor) {
        infoActual.style.display = 'block';
        byId('zonaActual').textContent = monitor.nombreZona || 'N/A';
        byId('jornadaActual').textContent = monitor.nombreJornada || 'N/A';

        // Pre-seleccionar zona actual
        selectZona.value = monitor.zonaId || '';
        if (monitor.zonaId) {
            cargarJornadasPorZona().then(() => {
                byId('asignarJornada').value = monitor.jornadaId || '';
            });
        }
    } else {
        infoActual.style.display = 'none';
    }

    new bootstrap.Modal(byId('modalAsignar')).show();
}

// ==========================================
// GUARDAR ASIGNACIÓN
// ==========================================
async function guardarAsignacion() {
    const usuarioId = byId('asignarUsuarioId').value;
    const zonaId = byId('asignarZona').value;
    const jornadaId = byId('asignarJornada').value;

    if (!zonaId || !jornadaId) {
        mostrarAlerta('Debe seleccionar zona y jornada', 'warning');
        return;
    }

    try {
        // Verificar si ya existe un monitor para este usuario
        const monitorExistente = monitoresData.find(m => m.usuarioId === parseInt(usuarioId));

        let response;
        if (monitorExistente) {
            // Actualizar monitor existente
            response = await fetch(`${API_URL}/monitores/${monitorExistente.id}`, {
                method: 'PUT',
                headers: {
                    ...Auth.getHeaders(),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    usuarioId: parseInt(usuarioId),
                    zonaId: parseInt(zonaId),
                    jornadaId: parseInt(jornadaId)
                })
            });
        } else {
            // Crear nuevo monitor
            response = await fetch(`${API_URL}/monitores`, {
                method: 'POST',
                headers: {
                    ...Auth.getHeaders(),
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    usuarioId: parseInt(usuarioId),
                    zonaId: parseInt(zonaId),
                    jornadaId: parseInt(jornadaId)
                })
            });
        }

        if (!response.ok) {
            throw new Error('Error al asignar zona/jornada');
        }

        bootstrap.Modal.getInstance(byId('modalAsignar')).hide();
        mostrarAlerta('Zona y jornada asignadas correctamente', 'success');
        await cargarDatosIniciales();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlerta(error.message || 'Error al asignar', 'danger');
    }
}

// ==========================================
// RESETEAR CONTRASEÑA
// ==========================================
async function resetearContrasena(id) {
    const usuario = usuariosData.find(u => u.id === id);
    if (!usuario) return;

    const resultado = await Swal.fire({
        icon: 'question',
        title: '¿Resetear contraseña?',
        html: `Se reseteará la contraseña de <strong>${usuario.primerNombre} ${usuario.primerApellido}</strong><br><br>
               <small>La nueva contraseña será: <code>${CONTRASENA_DEFAULT}</code></small>`,
        showCancelButton: true,
        confirmButtonColor: '#667eea',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, resetear',
        cancelButtonText: 'Cancelar'
    });

    if (!resultado.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/usuarios/${id}/resetear-contrasena`, {
            method: 'POST',
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al resetear contraseña');

        mostrarAlertaExito('Contraseña reseteada correctamente');
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error al resetear contraseña');
    }
}

// ==========================================
// TOGGLE ESTADO
// ==========================================
async function toggleEstado(id, estadoActual) {
    const usuario = usuariosData.find(u => u.id === id);
    if (!usuario) return;

    const accion = estadoActual ? 'desactivar' : 'activar';

    const resultado = await Swal.fire({
        icon: 'warning',
        title: `¿${estadoActual ? 'Desactivar' : 'Activar'} usuario?`,
        text: `¿Desea ${accion} al usuario ${usuario.primerNombre} ${usuario.primerApellido}?`,
        showCancelButton: true,
        confirmButtonColor: estadoActual ? '#dc3545' : '#28a745',
        cancelButtonColor: '#6c757d',
        confirmButtonText: `Sí, ${accion}`,
        cancelButtonText: 'Cancelar'
    });

    if (!resultado.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/usuarios/${id}/${accion}`, {
            method: 'PATCH',
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al cambiar estado');

        mostrarAlertaExito(`Usuario ${accion === 'activar' ? 'activado' : 'desactivado'} correctamente`);
        await cargarUsuarios();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error al cambiar estado');
    }
}


// ==========================================
// UTILIDADES
// ==========================================
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

function logout() {
    Auth.logout();
}

// ==========================================
// ELIMINAR USUARIO
// ==========================================
async function eliminarUsuario(id) {
    const usuario = usuariosData.find(u => u.id === id);
    if (!usuario) return;

    // No permitir eliminarse a sí mismo
    if (id === currentUser.id) {
        mostrarAlertaError('No puede eliminar su propio usuario');
        return;
    }

    const resultado = await Swal.fire({
        icon: 'warning',
        title: '¿Eliminar usuario?',
        html: `Esta acción eliminará permanentemente a <strong>${usuario.primerNombre} ${usuario.primerApellido}</strong><br><br>
               <span class="text-danger"><i class="bi bi-exclamation-triangle me-1"></i>Esta acción no se puede deshacer</span>`,
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    });

    if (!resultado.isConfirmed) return;

    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (!response.ok) throw new Error('Error al eliminar usuario');

        mostrarAlertaExito('Usuario eliminado correctamente');
        await cargarDatosIniciales();
    } catch (error) {
        console.error('Error:', error);
        mostrarAlertaError('Error al eliminar usuario');
    }
}