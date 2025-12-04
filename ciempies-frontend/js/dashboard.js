// Lógica del Dashboard

// Verificar autenticación
Auth.requireAuth();

// Obtener usuario actual
const currentUser = Auth.getUser();

// Inicializar al cargar la página
document.addEventListener('DOMContentLoaded', () => {
    loadUserInfo();
    configurarMenuPorRol();
    cargarEstadisticas();
    cargarClima(); // Cargar clima
});

// Cargar información del usuario
function loadUserInfo() {
    const nombreCompleto = `${currentUser.primerNombre} ${currentUser.primerApellido}`;

    // Navbar
    loadNavbarUser();

    // Welcome card
    document.getElementById('welcomeUserName').textContent = nombreCompleto;
    document.getElementById('welcomeUserRole').textContent = ROL_LABELS[currentUser.rol];
    document.getElementById('welcomeUserEmail').textContent = currentUser.email;
}

// Configurar menú según rol
function configurarMenuPorRol() {
    const rol = currentUser.rol;

    if (rol === 'ADMINISTRADOR') {
        // ADMINISTRADOR: Acceso a TODO
        document.getElementById('menuUsuarios').style.display = 'block';
        document.getElementById('menuEstudiantes').style.display = 'block';
        document.getElementById('menuRutas').style.display = 'block';
        document.getElementById('menuColegios').style.display = 'block';
        document.getElementById('menuAsistencias').style.display = 'block';
        document.getElementById('menuReportes').style.display = 'block';

        // Acciones rápidas
        mostrarAccionesRapidas(rol);

    } else if (rol === 'ENCARGADO') {
        // ENCARGADO: TODO menos Usuarios
        document.getElementById('menuEstudiantes').style.display = 'block';
        document.getElementById('menuRutas').style.display = 'block';
        document.getElementById('menuColegios').style.display = 'block';
        document.getElementById('menuAsistencias').style.display = 'block';
        document.getElementById('menuReportes').style.display = 'block';

        // Acciones rápidas
        mostrarAccionesRapidas(rol);

    } else if (rol === 'MONITOR') {
        // MONITOR: Solo Estudiantes y Asistencias
        document.getElementById('menuEstudiantes').style.display = 'block';
        document.getElementById('menuAsistencias').style.display = 'block';
    }
}

// Mostrar acciones rápidas
function mostrarAccionesRapidas(rol) {
    const container = document.getElementById('quickActionsContainer');
    const buttonsContainer = document.getElementById('quickActionsButtons');
    container.style.display = 'block';

    if (rol === 'ADMINISTRADOR' || rol === 'ENCARGADO') {
        buttonsContainer.innerHTML = `
            <button class="btn btn-primary" onclick="window.location.href='usuarios.html'">
                <i class="bi bi-person-plus-fill me-2"></i>Crear Usuario
            </button>
            <button class="btn btn-success" onclick="window.location.href='rutas.html'">
                <i class="bi bi-plus-circle-fill me-2"></i>Crear Ruta
            </button>
            <button class="btn btn-info" onclick="window.location.href='vehiculos.html'">
                <i class="bi bi-plus-circle-fill me-2"></i>Crear Vehículo
            </button>
        `;
    }
}

// Cargar estadísticas
async function cargarEstadisticas() {
    const container = document.getElementById('statsContainer');

    try {
        if (currentUser.rol === 'ADMINISTRADOR' || currentUser.rol === 'ENCARGADO') {
            const response = await fetch(`${API_URL}/usuarios`, {
                headers: Auth.getHeaders()
            });

            if (response.ok) {
                const usuarios = await response.json();
                mostrarEstadisticasUsuarios(usuarios);
            } else if (response.status === 401) {
                alert('Sesión expirada. Por favor inicie sesión nuevamente.');
                Auth.logout();
            } else {
                container.innerHTML = '<div class="col-12"><div class="alert alert-warning">No se pudieron cargar las estadísticas</div></div>';
            }
        } else {
            // Para monitores mostrar mensaje de bienvenida
            container.innerHTML = `
                <div class="col-12">
                    <div class="card card-custom">
                        <div class="card-body text-center py-5">
                            <i class="bi bi-emoji-smile" style="font-size: 60px; color: var(--primary-color);"></i>
                            <h4 class="mt-3">Bienvenido al Sistema</h4>
                            <p class="text-muted">Utiliza el menú lateral para navegar</p>
                        </div>
                    </div>
                </div>
            `;
        }
    } catch (error) {
        console.error('Error al cargar estadísticas:', error);
        container.innerHTML = '<div class="col-12"><div class="alert alert-danger">Error de conexión</div></div>';
    }
}

// Mostrar estadísticas de usuarios
function mostrarEstadisticasUsuarios(usuarios) {
    const container = document.getElementById('statsContainer');

    const total = usuarios.length;
    const activos = usuarios.filter(u => u.activo).length;
    const administradores = usuarios.filter(u => u.rol === 'ADMINISTRADOR').length;
    const encargados = usuarios.filter(u => u.rol === 'ENCARGADO').length;
    const monitores = usuarios.filter(u => u.rol === 'MONITOR').length;

    container.innerHTML = `
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="stat-card">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="stat-label">Total Usuarios</div>
                        <div class="stat-value">${total}</div>
                    </div>
                    <div class="stat-icon">
                        <i class="bi bi-people-fill"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="stat-card">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="stat-label">Usuarios Activos</div>
                        <div class="stat-value">${activos}</div>
                    </div>
                    <div class="stat-icon">
                        <i class="bi bi-check-circle-fill"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="stat-card">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="stat-label">Administradores</div>
                        <div class="stat-value">${administradores}</div>
                    </div>
                    <div class="stat-icon">
                        <i class="bi bi-shield-fill-check"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="stat-card">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="stat-label">Encargados</div>
                        <div class="stat-value">${encargados}</div>
                    </div>
                    <div class="stat-icon">
                        <i class="bi bi-person-badge-fill"></i>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-6 col-lg-4 mb-4">
            <div class="stat-card">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <div class="stat-label">Monitores</div>
                        <div class="stat-value">${monitores}</div>
                    </div>
                    <div class="stat-icon">
                        <i class="bi bi-person-check-fill"></i>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Función global para logout
function logout() {
    Auth.logout();
}

// ==========================================
// FUNCIONES DEL CLIMA
// ==========================================

let climaData = null;
let modalClimaInstance = null;

// Cargar clima actual
async function cargarClima() {
    try {
        const response = await fetch(`${API_URL}/clima/actual`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            climaData = await response.json();
            mostrarClimaWidget(climaData);
        } else {
            ocultarClimaWidget();
        }
    } catch (error) {
        console.error('Error al cargar clima:', error);
        ocultarClimaWidget();
    }
}

// Mostrar clima en el widget
function mostrarClimaWidget(clima) {
    document.getElementById('climaLoading').style.display = 'none';
    document.getElementById('climaInfo').style.display = 'flex';
    document.getElementById('climaTemp').textContent = `${Math.round(clima.temperatura)}°C`;
    document.getElementById('climaDesc').textContent = clima.descripcion;

    // Cambiar icono según el clima
    const iconoClima = document.querySelector('#climaInfo i');
    if (clima.descripcion.toLowerCase().includes('lluv')) {
        iconoClima.className = 'bi bi-cloud-rain-fill me-2';
    } else if (clima.descripcion.toLowerCase().includes('nub')) {
        iconoClima.className = 'bi bi-cloud-fill me-2';
    } else if (clima.descripcion.toLowerCase().includes('despejado') || clima.descripcion.toLowerCase().includes('sol')) {
        iconoClima.className = 'bi bi-sun-fill me-2';
    } else {
        iconoClima.className = 'bi bi-cloud-sun-fill me-2';
    }
}

// Ocultar widget si falla
function ocultarClimaWidget() {
    document.getElementById('climaWidget').style.display = 'none';
}

// Mostrar modal con detalle del clima
async function mostrarDetalleClima() {
    if (!modalClimaInstance) {
        modalClimaInstance = new bootstrap.Modal(document.getElementById('modalClima'));
    }

    modalClimaInstance.show();

    try {
        // Cargar recomendación
        const response = await fetch(`${API_URL}/clima/recomendacion`, {
            headers: Auth.getHeaders()
        });

        if (response.ok) {
            const data = await response.json();
            mostrarDetalleClimaCompleto(data.clima, data.recomendacion);
        }
    } catch (error) {
        console.error('Error al cargar detalle del clima:', error);
        document.getElementById('climaDetalleContent').innerHTML = `
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                Error al cargar la información del clima
            </div>
        `;
    }
}

// Mostrar detalle completo del clima
function mostrarDetalleClimaCompleto(clima, recomendacion) {
    const nivelAlertaClass = {
        'BAJA': 'success',
        'MEDIA': 'warning',
        'ALTA': 'danger'
    };

    const nivelAlertaIcon = {
        'BAJA': 'check-circle-fill',
        'MEDIA': 'exclamation-triangle-fill',
        'ALTA': 'exclamation-octagon-fill'
    };

    const alertClass = nivelAlertaClass[clima.alerta] || 'info';
    const alertIcon = nivelAlertaIcon[clima.alerta] || 'info-circle-fill';

    document.getElementById('climaDetalleContent').innerHTML = `
        <div class="row g-3">
            <div class="col-12">
                <div class="card border-0 bg-light">
                    <div class="card-body text-center">
                        <h1 class="display-3 mb-0">${Math.round(clima.temperatura)}°C</h1>
                        <p class="text-muted mb-0">${clima.descripcion}</p>
                    </div>
                </div>
            </div>

            <div class="col-6">
                <div class="card border-0 bg-light">
                    <div class="card-body text-center">
                        <i class="bi bi-droplet-fill text-primary" style="font-size: 24px;"></i>
                        <h5 class="mt-2 mb-0">${clima.humedad}%</h5>
                        <small class="text-muted">Humedad</small>
                    </div>
                </div>
            </div>

            <div class="col-6">
                <div class="card border-0 bg-light">
                    <div class="card-body text-center">
                        <i class="bi bi-wind text-info" style="font-size: 24px;"></i>
                        <h5 class="mt-2 mb-0">${clima.viento} km/h</h5>
                        <small class="text-muted">Viento</small>
                    </div>
                </div>
            </div>

            <div class="col-12">
                <div class="alert alert-${alertClass} d-flex align-items-center mb-0">
                    <i class="bi bi-${alertIcon} me-2" style="font-size: 20px;"></i>
                    <div>
                        <strong>Nivel de Alerta: ${clima.alerta}</strong>
                        <p class="mb-0 mt-1 small">${recomendacion}</p>
                    </div>
                </div>
            </div>
        </div>
    `;
}

// Actualizar clima cada 10 minutos
setInterval(cargarClima, 600000);