// Lógica de Gestión de Usuarios

// Verificar autenticación y permisos
Auth.requireAuth();
Auth.requireRoles(['ADMINISTRADOR', 'ENCARGADO']);

// Variables globales
let usuariosOriginales = [];
let usuariosFiltrados = [];
let modalInstance;

// Inicializar
document.addEventListener('DOMContentLoaded', () => {
    loadNavbarUser();
    modalInstance = new bootstrap.Modal(document.getElementById('modalUsuario'));
    cargarUsuarios();
});

// Cargar usuarios desde el backend
async function cargarUsuarios() {
    showTableLoading('tableBody', 7);

    try {
        const response = await fetch(`${API_URL}/usuarios`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            usuariosOriginales = await response.json();
            usuariosFiltrados = [...usuariosOriginales];
            mostrarUsuarios(usuariosFiltrados);
        } else if (response.status === 401) {
            alert('Sesión expirada. Por favor inicie sesión nuevamente.');
            Auth.logout();
        } else {
            showTableError('tableBody', 7);
        }
    } catch (error) {
        console.error('Error:', error);
        showTableError('tableBody', 7);
    }
}

// Mostrar usuarios en la tabla
function mostrarUsuarios(usuarios) {
    const tableBody = document.getElementById('tableBody');

    if (usuarios.length === 0) {
        showTableEmpty('tableBody', 7, 'No se encontraron usuarios');
        return;
    }

    tableBody.innerHTML = usuarios.map(u => `
        <tr>
            <td>${u.id}</td>
            <td>
                <span class="badge bg-secondary">${TIPO_ID_LABELS[u.tipoId] || u.tipoId}</span>
                ${u.numId}
            </td>
            <td>${u.primerNombre} ${u.segundoNombre || ''} ${u.primerApellido} ${u.segundoApellido || ''}</td>
            <td>${u.email}</td>
            <td><span class="badge ${ROL_BADGE_CLASS[u.rol]}">${ROL_LABELS[u.rol] || u.rol}</span></td>
            <td>
                ${u.activo
                    ? '<span class="badge bg-success">Activo</span>'
                    : '<span class="badge bg-danger">Inactivo</span>'}
            </td>
            <td>
                <div class="btn-group btn-group-sm">
                    <button class="btn btn-outline-primary btn-action" onclick="editarUsuario(${u.id})" title="Editar">
                        <i class="bi bi-pencil-fill"></i>
                    </button>
                    ${u.activo
                        ? `<button class="btn btn-outline-warning btn-action" onclick="cambiarEstado(${u.id}, false)" title="Desactivar">
                            <i class="bi bi-x-circle-fill"></i>
                           </button>`
                        : `<button class="btn btn-outline-success btn-action" onclick="cambiarEstado(${u.id}, true)" title="Activar">
                            <i class="bi bi-check-circle-fill"></i>
                           </button>`
                    }
                    <button class="btn btn-outline-danger btn-action" onclick="eliminarUsuario(${u.id})" title="Eliminar">
                        <i class="bi bi-trash-fill"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Filtrar usuarios
function filtrarUsuarios() {
    const rol = document.getElementById('filtroRol').value;
    const estado = document.getElementById('filtroEstado').value;

    usuariosFiltrados = usuariosOriginales.filter(u => {
        const coincideRol = !rol || u.rol === rol;
        const coincideEstado = !estado || u.activo.toString() === estado;
        return coincideRol && coincideEstado;
    });

    buscarUsuarios();
}

// Buscar usuarios
function buscarUsuarios() {
    const busqueda = document.getElementById('busqueda').value.toLowerCase();

    if (!busqueda) {
        mostrarUsuarios(usuariosFiltrados);
        return;
    }

    const resultados = usuariosFiltrados.filter(u => {
        const nombreCompleto = `${u.primerNombre} ${u.segundoNombre || ''} ${u.primerApellido} ${u.segundoApellido || ''}`.toLowerCase();
        return nombreCompleto.includes(busqueda) ||
               u.email.toLowerCase().includes(busqueda) ||
               u.numId.includes(busqueda);
    });

    mostrarUsuarios(resultados);
}

// Mostrar modal para crear
function mostrarModalCrear() {
    document.getElementById('modalTitle').innerHTML = '<i class="bi bi-person-plus-fill me-2"></i>Crear Usuario';
    document.getElementById('formUsuario').reset();
    document.getElementById('usuarioId').value = '';
    document.getElementById('modoEdicion').value = 'false';
    document.getElementById('labelContrasena').textContent = 'Contraseña';
    document.getElementById('labelContrasena').classList.add('required');
    document.getElementById('contrasena').required = true;
    document.getElementById('helpContrasena').textContent = 'Mínimo 6 caracteres';
    modalInstance.show();
}

// Editar usuario
async function editarUsuario(id) {
    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const usuario = await response.json();

            document.getElementById('modalTitle').innerHTML = '<i class="bi bi-pencil-fill me-2"></i>Editar Usuario';
            document.getElementById('usuarioId').value = usuario.id;
            document.getElementById('modoEdicion').value = 'true';
            document.getElementById('tipoId').value = usuario.tipoId;
            document.getElementById('numId').value = usuario.numId;
            document.getElementById('primerNombre').value = usuario.primerNombre;
            document.getElementById('segundoNombre').value = usuario.segundoNombre || '';
            document.getElementById('primerApellido').value = usuario.primerApellido;
            document.getElementById('segundoApellido').value = usuario.segundoApellido || '';
            document.getElementById('email').value = usuario.email;
            document.getElementById('rol').value = usuario.rol;
            document.getElementById('contrasena').value = '';
            document.getElementById('labelContrasena').textContent = 'Contraseña (dejar en blanco para no cambiar)';
            document.getElementById('labelContrasena').classList.remove('required');
            document.getElementById('contrasena').required = false;
            document.getElementById('helpContrasena').textContent = 'Dejar en blanco para mantener la contraseña actual';

            modalInstance.show();
        } else {
            alert('Error al cargar el usuario');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cargar el usuario');
    }
}

// Guardar usuario (crear o actualizar)
async function guardarUsuario() {
    const form = document.getElementById('formUsuario');
    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }

    const modoEdicion = document.getElementById('modoEdicion').value === 'true';
    const usuarioId = document.getElementById('usuarioId').value;

    // Construir el DTO según sea crear o actualizar
    const dto = {
        tipoId: document.getElementById('tipoId').value,
        numId: document.getElementById('numId').value.trim(),
        primerNombre: document.getElementById('primerNombre').value.trim(),
        segundoNombre: document.getElementById('segundoNombre').value.trim() || null,
        primerApellido: document.getElementById('primerApellido').value.trim(),
        segundoApellido: document.getElementById('segundoApellido').value.trim() || null,
        email: document.getElementById('email').value.trim()
    };

    // Para crear, el rol y contraseña son obligatorios
    if (!modoEdicion) {
        dto.rol = document.getElementById('rol').value;
        dto.contrasena = document.getElementById('contrasena').value;
    } else {
        // Para actualizar, la contraseña es opcional
        const contrasena = document.getElementById('contrasena').value;
        if (contrasena) {
            dto.contrasena = contrasena;
        }
    }

    try {
        const url = modoEdicion ? `${API_URL}/usuarios/${usuarioId}` : `${API_URL}/usuarios`;
        const method = modoEdicion ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: Auth.getHeaders(),
            body: JSON.stringify(dto)
        });

        if (response.ok) {
            alert(modoEdicion ? 'Usuario actualizado exitosamente' : 'Usuario creado exitosamente');
            modalInstance.hide();
            cargarUsuarios();
        } else {
            const error = await response.text();
            alert('Error: ' + error);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al guardar el usuario');
    }
}

// Cambiar estado del usuario
async function cambiarEstado(id, activar) {
    if (!confirmAction(`¿Está seguro de ${activar ? 'activar' : 'desactivar'} este usuario?`)) {
        return;
    }

    try {
        const endpoint = activar ? 'activar' : 'desactivar';
        const response = await fetch(`${API_URL}/usuarios/${id}/${endpoint}`, {
            method: 'PATCH',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            alert(`Usuario ${activar ? 'activado' : 'desactivado'} exitosamente`);
            cargarUsuarios();
        } else {
            alert('Error al cambiar el estado del usuario');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al cambiar el estado del usuario');
    }
}

// Eliminar usuario
async function eliminarUsuario(id) {
    if (!confirmAction('¿Está seguro de eliminar este usuario? Esta acción no se puede deshacer.')) {
        return;
    }

    try {
        const response = await fetch(`${API_URL}/usuarios/${id}`, {
            method: 'DELETE',
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            alert('Usuario eliminado exitosamente');
            cargarUsuarios();
        } else {
            alert('Error al eliminar el usuario');
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al eliminar el usuario');
    }
}

// Función global para logout
function logout() {
    Auth.logout();
}