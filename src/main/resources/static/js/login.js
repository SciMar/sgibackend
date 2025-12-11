// Lógica del Login

// Redirigir si ya está autenticado
Auth.redirectIfAuthenticated();

// Elementos del DOM
const loginForm = document.getElementById('loginForm');
const btnSubmit = loginForm.querySelector('button[type="submit"]');

// Manejar envío del formulario
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Limpiar alertas previas
    clearAlerts('alertContainer');

    // Obtener datos del formulario
    const loginDTO = {
        email: document.getElementById('email').value.trim(),
        contrasena: document.getElementById('contrasena').value
    };

    // Validación básica
    if (!loginDTO.email || !loginDTO.contrasena) {
        showAlert('alertContainer', 'danger', 'Por favor complete todos los campos');
        return;
    }

    // Deshabilitar botón y mostrar loading
    setButtonLoading(btnSubmit, true);

    try {
        const response = await fetch(`${API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginDTO)
        });

        if (response.ok) {
            const data = await response.json();

            // Guardar token y usuario
            Auth.saveSession(data.token, data.usuario);

            // ✅ VERIFICAR SI ES PRIMER INGRESO
            if (data.usuario.primerIngreso) {
                showAlert('alertContainer', 'warning', 'Debe cambiar su contraseña. Redirigiendo...', false);

                setTimeout(() => {
                    window.location.href = 'cambiar-contrasena.html';
                }, 1500);
            } else {
                // Mostrar éxito
                showAlert('alertContainer', 'success', '¡Bienvenido! Redirigiendo...', false);

                // Redirigir según el rol
                setTimeout(() => {
                    redirigirSegunRol(data.usuario.rol);
                }, 1000);
            }
        } else {
            // Error de autenticación
            const errorText = response.status === 401
                ? 'Email o contraseña incorrectos'
                : 'Error al iniciar sesión';
            showAlert('alertContainer', 'danger', errorText);
            setButtonLoading(btnSubmit, false);
        }
    } catch (error) {
        console.error('Error en login:', error);
        showAlert('alertContainer', 'danger', 'Error de conexión. Verifique que el servidor esté activo.');
        setButtonLoading(btnSubmit, false);
    }
});

// ✅ FUNCIÓN PARA REDIRIGIR SEGÚN ROL
function redirigirSegunRol(rol) {
    switch(rol) {
        case 'ADMINISTRADOR':
            window.location.href = 'dashboard.html';
            break;
        case 'ENCARGADO':
            window.location.href = 'dashboard.html';
            break;
        case 'MONITOR':
            window.location.href = 'dashboard.html'; // O 'monitor-dashboard.html' si tienes uno específico
            break;
        default:
            window.location.href = 'dashboard.html';
    }
}

// Limpiar alertas al escribir
document.getElementById('email').addEventListener('input', () => {
    clearAlerts('alertContainer');
});

document.getElementById('contrasena').addEventListener('input', () => {
    clearAlerts('alertContainer');
});