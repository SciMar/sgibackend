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

function inicializar() {
    // Verificar acceso - solo ADMINISTRADOR puede enviar notificaciones masivas
    if (currentUser.rol !== 'ADMINISTRADOR') {
        mostrarAlertaError('Acceso Denegado', 'Solo los administradores pueden enviar notificaciones masivas.');
        setTimeout(() => window.location.href = 'dashboard.html', 2000);
        return;
    }

    // Inicializar vista previa
    actualizarVistaPrevia();
}

// ==========================================
// FUNCIONES DE ALERTA (SweetAlert2)
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
        timer: 3000,
        timerProgressBar: true
    });
}

function mostrarAlertaAdvertencia(titulo, mensaje) {
    Swal.fire({
        icon: 'warning',
        title: titulo,
        text: mensaje,
        confirmButtonColor: '#667eea'
    });
}

// ==========================================
// VISTA PREVIA
// ==========================================

function actualizarVistaPrevia() {
    const asunto = document.getElementById('asunto').value.trim();
    const mensaje = document.getElementById('mensaje').value.trim();

    const previewAsunto = document.getElementById('previewAsunto');
    const previewMensaje = document.getElementById('previewMensaje');

    if (asunto) {
        previewAsunto.textContent = asunto;
        previewAsunto.style.color = '#333';
    } else {
        previewAsunto.textContent = '[Escribe un asunto para ver la vista previa]';
        previewAsunto.style.color = '#999';
    }

    if (mensaje) {
        previewMensaje.textContent = mensaje;
        previewMensaje.style.color = '#555';
    } else {
        previewMensaje.textContent = 'El contenido de tu mensaje aparecerá aquí mientras escribes...';
        previewMensaje.style.color = '#999';
    }
}

// ==========================================
// PLANTILLAS RÁPIDAS
// ==========================================

function usarPlantilla(tipo) {
    const plantillas = {
        reunion: {
            asunto: 'Convocatoria a Reunión - Sistema Ciempiés',
            mensaje: `Estimado equipo,

Se les convoca cordialmente a una reunión general que se llevará a cabo con los siguientes detalles:

📅 Fecha: [DÍA, FECHA]
🕐 Hora: [HORA]
📍 Lugar: [LUGAR/ENLACE DE VIDEOLLAMADA]

Temas a tratar:
• Revisión de actividades del período
• Planificación de rutas
• Asuntos varios

Se solicita puntualidad y confirmar asistencia respondiendo a este correo.

Agradecemos su participación.

Cordialmente,
Administración`
        },
        aviso: {
            asunto: 'Aviso Importante - Sistema Ciempiés',
            mensaje: `Estimado equipo,

Por medio del presente, les informamos sobre una actualización importante:

📢 AVISO: [DESCRIPCIÓN DEL AVISO]

Detalles:
• [Detalle 1]
• [Detalle 2]
• [Detalle 3]

📅 Fecha de aplicación: [FECHA]

Para cualquier duda o consulta, pueden comunicarse con la administración.

Agradecemos su atención y colaboración.

Atentamente,
Administración`
        },
        recordatorio: {
            asunto: 'Recordatorio Importante - Sistema Ciempiés',
            mensaje: `Estimado equipo,

Les enviamos este recordatorio sobre una actividad próxima:

⏰ RECORDATORIO: [EVENTO/ACTIVIDAD]

📅 Fecha: [FECHA]
🕐 Hora: [HORA]
📍 Lugar: [LUGAR]

Acciones requeridas:
• [Acción 1]
• [Acción 2]

No olviden [INFORMACIÓN ADICIONAL IMPORTANTE].

Quedamos atentos a cualquier consulta.

Saludos cordiales,
Administración`
        }
    };

    if (plantillas[tipo]) {
        document.getElementById('asunto').value = plantillas[tipo].asunto;
        document.getElementById('mensaje').value = plantillas[tipo].mensaje;
        actualizarVistaPrevia();

        // Mostrar notificación
        Swal.fire({
            icon: 'info',
            title: 'Plantilla Cargada',
            text: 'Recuerda personalizar los campos entre corchetes [ ]',
            confirmButtonColor: '#667eea',
            timer: 2500,
            timerProgressBar: true,
            toast: true,
            position: 'top-end',
            showConfirmButton: false
        });
    }
}

// ==========================================
// VALIDACIONES
// ==========================================

function validarFormulario() {
    const asunto = document.getElementById('asunto').value.trim();
    const mensaje = document.getElementById('mensaje').value.trim();

    if (!asunto) {
        mostrarAlertaError('Campo Requerido', 'El asunto del correo es obligatorio.');
        document.getElementById('asunto').focus();
        return false;
    }

    if (asunto.length < 5) {
        mostrarAlertaError('Asunto Muy Corto', 'El asunto debe tener al menos 5 caracteres.');
        document.getElementById('asunto').focus();
        return false;
    }

    if (!mensaje) {
        mostrarAlertaError('Campo Requerido', 'El mensaje del correo es obligatorio.');
        document.getElementById('mensaje').focus();
        return false;
    }

    if (mensaje.length < 20) {
        mostrarAlertaError('Mensaje Muy Corto', 'El mensaje debe tener al menos 20 caracteres.');
        document.getElementById('mensaje').focus();
        return false;
    }

    // Advertir si hay corchetes sin reemplazar
    if (mensaje.includes('[') && mensaje.includes(']')) {
        return new Promise((resolve) => {
            Swal.fire({
                icon: 'warning',
                title: 'Posibles campos sin completar',
                html: 'El mensaje parece contener campos de plantilla sin completar <strong>(texto entre corchetes [ ])</strong>.<br><br>¿Desea continuar de todas formas?',
                showCancelButton: true,
                confirmButtonColor: '#667eea',
                cancelButtonColor: '#6c757d',
                confirmButtonText: 'Sí, enviar así',
                cancelButtonText: 'Revisar mensaje'
            }).then((result) => {
                resolve(result.isConfirmed);
            });
        });
    }

    return true;
}

// ==========================================
// ENVIAR NOTIFICACIÓN
// ==========================================

async function enviarNotificacion() {
    // Validar formulario
    const esValido = await validarFormulario();
    if (!esValido) return;

    const asunto = document.getElementById('asunto').value.trim();
    const mensaje = document.getElementById('mensaje').value.trim();

    // Confirmar envío
    const confirmacion = await Swal.fire({
        icon: 'question',
        title: '¿Enviar correos masivos?',
        html: `
            <p>Está a punto de enviar un correo a <strong>todos los usuarios activos</strong> del sistema.</p>
            <div class="text-start mt-3 p-3 bg-light rounded">
                <strong>Asunto:</strong> ${asunto}
            </div>
        `,
        showCancelButton: true,
        confirmButtonColor: '#667eea',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '<i class="bi bi-send-fill me-1"></i>Sí, Enviar',
        cancelButtonText: 'Cancelar',
        reverseButtons: true
    });

    if (!confirmacion.isConfirmed) return;

    // Mostrar loading
    Swal.fire({
        title: 'Enviando correos...',
        html: 'Por favor espere mientras se envían los correos a todos los usuarios.',
        allowOutsideClick: false,
        allowEscapeKey: false,
        showConfirmButton: false,
        willOpen: () => {
            Swal.showLoading();
        }
    });

    try {
        const response = await fetch(`${API_URL}/api/notificaciones/enviar-masivo`, {
            method: 'POST',
            headers: Auth.getHeaders(),
            body: JSON.stringify({
                asunto: asunto,
                mensaje: mensaje
            })
        });

        if (response.ok) {
            const resultado = await response.json();

            Swal.fire({
                icon: 'success',
                title: '¡Correos Enviados!',
                html: `
                    <p>Los correos fueron enviados exitosamente.</p>
                    <div class="mt-3">
                        <span class="badge bg-success" style="font-size: 18px;">
                            <i class="bi bi-check-circle-fill me-1"></i>
                            ${resultado.destinatarios || resultado.totalEnviados || 'Todos los'} destinatarios
                        </span>
                    </div>
                `,
                confirmButtonColor: '#667eea'
            });

            // Limpiar formulario
            limpiarFormulario();
        } else {
            const errorText = await response.text();
            Swal.fire({
                icon: 'error',
                title: 'Error al Enviar',
                text: errorText || 'No se pudieron enviar los correos. Intente nuevamente.',
                confirmButtonColor: '#667eea'
            });
        }
    } catch (error) {
        console.error('Error:', error);
        Swal.fire({
            icon: 'error',
            title: 'Error de Conexión',
            text: 'No se pudo conectar con el servidor. Verifique su conexión e intente nuevamente.',
            confirmButtonColor: '#667eea'
        });
    }
}

// ==========================================
// LIMPIAR FORMULARIO
// ==========================================

function limpiarFormulario() {
    document.getElementById('formNotificacion').reset();
    actualizarVistaPrevia();
}

// ==========================================
// LOGOUT
// ==========================================

function logout() {
    Auth.logout();
}