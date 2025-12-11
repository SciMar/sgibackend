// L// Lógica de Cambiar Contraseña - Primer Ingreso

    // Configuración de la API
    const API_URL = 'http://localhost:8080/api';

    // Obtener datos del usuario usando Auth
    const token = Auth.getToken();
    const userData = Auth.getUser();

    // Verificar que el usuario esté logueado y sea primer ingreso
    document.addEventListener('DOMContentLoaded', function() {
        if (!token || !userData || !userData.id) {
            window.location.href = 'login.html';
            return;
        }

        // Mostrar nombre del usuario
        const nombreCompleto = `${userData.primerNombre || ''} ${userData.primerApellido || ''}`.trim();
        document.getElementById('userName').textContent = nombreCompleto || userData.email;

        // Si no es primer ingreso, redirigir al dashboard
        if (userData.primerIngreso !== true) {
            window.location.href = 'dashboard.html';
            return;
        }

        // Inicializar validaciones
        initPasswordValidation();
    });

    // Inicializar validación de contraseña
    function initPasswordValidation() {
        const newPassword = document.getElementById('newPassword');
        const confirmPassword = document.getElementById('confirmPassword');

        // Validar contraseña en tiempo real
        newPassword.addEventListener('input', validatePassword);
        confirmPassword.addEventListener('input', checkPasswordsMatch);
    }

    // Requisitos de contraseña
    const requirements = {
        length: { regex: /.{8,}/ },
        uppercase: { regex: /[A-Z]/ },
        lowercase: { regex: /[a-z]/ },
        number: { regex: /[0-9]/ },
        special: { regex: /[@#$%^&+=!.*_-]/ },
        nospace: { regex: /^\S+$/ }
    };

    // Validar contraseña
    function validatePassword() {
        const password = document.getElementById('newPassword').value;
        let validCount = 0;

        // Verificar cada requisito
        for (const [key, req] of Object.entries(requirements)) {
            const element = document.getElementById(`req-${key}`);
            const isValid = req.regex.test(password);
            const icon = element.querySelector('i');

            if (isValid) {
                element.classList.add('valid');
                element.classList.remove('invalid');
                icon.classList.remove('bi-circle', 'bi-x-circle');
                icon.classList.add('bi-check-circle-fill');
                validCount++;
            } else if (password.length > 0) {
                element.classList.add('invalid');
                element.classList.remove('valid');
                icon.classList.remove('bi-circle', 'bi-check-circle-fill');
                icon.classList.add('bi-x-circle');
            } else {
                element.classList.remove('valid', 'invalid');
                icon.classList.remove('bi-check-circle-fill', 'bi-x-circle');
                icon.classList.add('bi-circle');
            }
        }

        // Actualizar barra de fortaleza
        updateStrengthBar(validCount);

        // Verificar coincidencia
        checkPasswordsMatch();
    }

    // Verificar que las contraseñas coincidan
    function checkPasswordsMatch() {
        const passwordsMatchDiv = document.getElementById('passwordsMatch');
        const newPass = document.getElementById('newPassword').value;
        const confirmPass = document.getElementById('confirmPassword').value;
        const submitBtn = document.getElementById('submitBtn');

        if (confirmPass.length === 0) {
            passwordsMatchDiv.innerHTML = '';
            passwordsMatchDiv.className = 'passwords-match';
        } else if (newPass === confirmPass) {
            passwordsMatchDiv.innerHTML = '<i class="bi bi-check-circle-fill"></i> Las contraseñas coinciden';
            passwordsMatchDiv.className = 'passwords-match match';
        } else {
            passwordsMatchDiv.innerHTML = '<i class="bi bi-x-circle-fill"></i> Las contraseñas no coinciden';
            passwordsMatchDiv.className = 'passwords-match no-match';
        }

        // Habilitar/deshabilitar botón
        const allValid = Object.values(requirements).every(req => req.regex.test(newPass));
        submitBtn.disabled = !(allValid && newPass === confirmPass && confirmPass.length > 0);
    }

    // Actualizar barra de fortaleza
    function updateStrengthBar(validCount) {
        const strengthBar = document.getElementById('strengthBar');
        const strengthText = document.getElementById('strengthText');

        strengthBar.className = 'strength-bar-fill';

        if (validCount <= 2) {
            strengthBar.classList.add('strength-weak');
            strengthText.textContent = 'Débil';
            strengthText.style.color = '#dc3545';
        } else if (validCount <= 4) {
            strengthBar.classList.add('strength-fair');
            strengthText.textContent = 'Regular';
            strengthText.style.color = '#ffc107';
        } else if (validCount <= 5) {
            strengthBar.classList.add('strength-good');
            strengthText.textContent = 'Buena';
            strengthText.style.color = '#17a2b8';
        } else {
            strengthBar.classList.add('strength-strong');
            strengthText.textContent = 'Excelente';
            strengthText.style.color = '#28a745';
        }
    }

    // Toggle mostrar/ocultar contraseña
    function togglePassword(inputId, iconId) {
        const input = document.getElementById(inputId);
        const icon = document.getElementById(iconId);

        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('bi-eye');
            icon.classList.add('bi-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('bi-eye-slash');
            icon.classList.add('bi-eye');
        }
    }

    // Enviar formulario
    document.getElementById('changePasswordForm').addEventListener('submit', async function(e) {
        e.preventDefault();

        const newPass = document.getElementById('newPassword').value;
        const confirmPass = document.getElementById('confirmPassword').value;
        const submitBtn = document.getElementById('submitBtn');

        if (newPass !== confirmPass) {
            showAlert('Las contraseñas no coinciden', 'danger');
            return;
        }

        submitBtn.disabled = true;
        submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2"></span>Guardando...';

        try {
            const response = await fetch(`${API_URL}/usuarios/${userData.id}/cambiar-contrasena-primer-ingreso`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({
                    contrasenaNueva: newPass,
                    confirmarContrasena: confirmPass
                })
            });

            const data = await response.json();

            if (response.ok) {
                // ✅ Actualizar usando Auth para que actualice ambos (user y userData)
                Auth.updatePrimerIngreso(false);

                showAlert('¡Contraseña actualizada exitosamente!', 'success');

                // Redirigir al dashboard después de 2 segundos
                setTimeout(() => {
                    window.location.href = 'dashboard.html';
                }, 2000);
            } else {
                throw new Error(data.error || 'Error al cambiar la contraseña');
            }
        } catch (error) {
            showAlert(error.message, 'danger');
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<i class="bi bi-check-circle me-2"></i>Guardar Nueva Contraseña';
        }
    });

    // Mostrar alertas
    function showAlert(message, type) {
        // Remover alertas anteriores
        const existingAlerts = document.querySelectorAll('.alert-floating');
        existingAlerts.forEach(alert => alert.remove());

        const alertDiv = document.createElement('div');
        alertDiv.className = `alert alert-${type} alert-floating alert-dismissible fade show`;
        alertDiv.innerHTML = `
            <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.appendChild(alertDiv);

        // Auto-cerrar después de 5 segundos
        setTimeout(() => {
            alertDiv.remove();
        }, 5000);
    }

    // Prevenir que el usuario regrese sin cambiar la contraseña
    window.history.pushState(null, '', window.location.href);
    window.onpopstate = function() {
        window.history.pushState(null, '', window.location.href);
    };