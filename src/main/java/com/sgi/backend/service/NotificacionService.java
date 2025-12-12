package com.sgi.backend.service;

import com.sgi.backend.model.Usuario;
import com.sgi.backend.repository.UsuarioRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificacionService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Enviar correo masivo a todos los usuarios del sistema
     */
    public int enviarCorreoMasivo(String asunto, String mensaje) {
        List<Usuario> usuarios = usuarioRepository.findAll();

        List<String> emails = usuarios.stream()
                .filter(u -> u.getActivo())
                .map(Usuario::getEmail)
                .filter(email -> email != null && !email.isEmpty())
                .distinct()
                .collect(Collectors.toList());

        if (emails.isEmpty()) {
            throw new RuntimeException("No se encontraron emails de usuarios");
        }

        int enviados = 0;

        for (String email : emails) {
            try {
                enviarCorreoHTML(email, asunto, mensaje);
                enviados++;
            } catch (Exception e) {
                System.err.println("Error enviando a " + email + ": " + e.getMessage());
            }
        }

        System.out.println("✓ Correos enviados: " + enviados + " de " + emails.size() + " usuarios");
        return enviados;
    }

    /**
     * Enviar correo HTML individual con logo
     */
    private void enviarCorreoHTML(String destinatario, String asunto, String mensaje) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setFrom(fromEmail);

        String contenidoHTML = generarPlantillaHTML(asunto, mensaje);
        helper.setText(contenidoHTML, true);

        // Adjuntar logo como imagen inline
        try {
            ClassPathResource logoResource = new ClassPathResource("static/images/logo.jpeg");
            if (logoResource.exists()) {
                helper.addInline("logo", logoResource);
            }
        } catch (Exception e) {
            System.err.println("No se pudo adjuntar el logo: " + e.getMessage());
        }

        mailSender.send(mimeMessage);
    }

    /**
     * Generar plantilla HTML profesional para el correo
     */
    private String generarPlantillaHTML(String asunto, String mensaje) {
        String mensajeHTML = mensaje.replace("\n", "<br>");

        return """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f0f2f5;">
                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background-color: #f0f2f5;">
                    <tr>
                        <td align="center" style="padding: 30px 20px;">
                            <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="background-color: #ffffff; border-radius: 20px; box-shadow: 0 10px 40px rgba(102, 126, 234, 0.15); overflow: hidden;">
                                
                                <!-- HEADER CON LOGO -->
                                <tr>
                                    <td style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 50%%, #9f7aea 100%%); padding: 50px 30px; text-align: center;">
                                        <!-- Logo circular centrado -->
                                        <table role="presentation" align="center" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td style="width: 120px; height: 120px; background: white; border-radius: 60px; text-align: center; vertical-align: middle; box-shadow: 0 8px 32px rgba(0,0,0,0.25);">
                                                    <img src="cid:logo" alt="Logo Ciempiés" width="90" height="90" style="border-radius: 45px; display: block; margin: 15px auto;">
                                                </td>
                                            </tr>
                                        </table>
                                        <h1 style="color: white; margin: 25px 0 0; font-size: 32px; font-weight: 700; text-shadow: 0 2px 10px rgba(0,0,0,0.2);">Sistema Ciempiés</h1>
                                        <p style="color: rgba(255,255,255,0.95); margin: 10px 0 0; font-size: 15px; font-weight: 300;">Sistema de Gestión de Transporte Escolar</p>
                                        <div style="width: 60px; height: 3px; background: rgba(255,255,255,0.5); margin: 20px auto 0; border-radius: 2px;"></div>
                                    </td>
                                </tr>
                                
                                <!-- CONTENIDO -->
                                <tr>
                                    <td style="padding: 45px 40px;">
                                        <!-- Asunto -->
                                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                            <tr>
                                                <td style="background: linear-gradient(135deg, #667eea15 0%%, #764ba215 100%%); border-left: 4px solid #667eea; border-radius: 0 10px 10px 0; padding: 20px 25px;">
                                                    <h2 style="color: #333; font-size: 20px; margin: 0; font-weight: 600;">
                                                        %s
                                                    </h2>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Mensaje -->
                                        <div style="color: #555; font-size: 15px; line-height: 1.9; margin-top: 30px; padding: 0 5px;">
                                            %s
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- FOOTER -->
                                <tr>
                                    <td style="background: linear-gradient(180deg, #f8f9fa 0%%, #f0f2f5 100%%); padding: 35px 30px; text-align: center; border-top: 1px solid #e9ecef;">
                                        <!-- Logo pequeño circular -->
                                        <table role="presentation" align="center" cellspacing="0" cellpadding="0" style="margin-bottom: 20px;">
                                            <tr>
                                                <td style="width: 60px; height: 60px; background: white; border-radius: 30px; text-align: center; vertical-align: middle; box-shadow: 0 4px 15px rgba(102, 126, 234, 0.2);">
                                                    <img src="cid:logo" alt="Logo" width="45" height="45" style="border-radius: 23px; display: block; margin: 7px auto;">
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Firma -->
                                        <p style="margin: 0 0 5px; font-weight: 700; color: #333; font-size: 17px;">
                                            Equipo Sistema Ciempiés
                                        </p>
                                        <p style="margin: 0 0 25px; color: #888; font-size: 13px;">
                                            Sistema de Gestión de Transporte Escolar
                                        </p>
                                        
                                        <!-- Redes Sociales -->
                                        <div style="margin: 25px 0;">
                                            <a href="#" style="display: inline-block; width: 38px; height: 38px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; border-radius: 19px; line-height: 38px; text-decoration: none; margin: 0 6px; font-size: 15px; font-weight: bold;">f</a>
                                            <a href="#" style="display: inline-block; width: 38px; height: 38px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; border-radius: 19px; line-height: 38px; text-decoration: none; margin: 0 6px; font-size: 15px; font-weight: bold;">X</a>
                                            <a href="#" style="display: inline-block; width: 38px; height: 38px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; border-radius: 19px; line-height: 38px; text-decoration: none; margin: 0 6px; font-size: 13px; font-weight: bold;">in</a>
                                            <a href="#" style="display: inline-block; width: 38px; height: 38px; background: linear-gradient(135deg, #25D366 0%%, #128C7E 100%%); color: white; border-radius: 19px; line-height: 38px; text-decoration: none; margin: 0 6px; font-size: 15px; font-weight: bold;">W</a>
                                        </div>
                                        
                                        <!-- Separador -->
                                        <div style="width: 80px; height: 2px; background: linear-gradient(90deg, transparent, #667eea, transparent); margin: 25px auto;"></div>
                                        
                                        <!-- Texto legal -->
                                        <p style="margin: 0; font-size: 11px; color: #aaa; line-height: 1.7;">
                                            Este correo fue enviado desde el Sistema Ciempiés.<br>
                                            Por favor no responda a este correo, es enviado de forma automática.<br>
                                            © 2024 Sistema Ciempiés - Todos los derechos reservados.
                                        </p>
                                    </td>
                                </tr>
                                
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """.formatted(asunto, mensajeHTML);
    }

    /**
     * Enviar correo de bienvenida a nuevo usuario
     */
    public void enviarCorreoBienvenida(String email, String nombre, String password) {
        String asunto = "¡Bienvenido al Sistema Ciempiés!";
        String mensaje = String.format("""
            Hola %s,
            
            ¡Bienvenido al Sistema Ciempiés! 🎉
            
            Tu cuenta ha sido creada exitosamente. A continuación encontrarás tus credenciales de acceso:
            
            📧 Usuario: %s
            🔐 Contraseña: %s
            
            ⚠️ Te recomendamos cambiar tu contraseña después del primer inicio de sesión.
            
            Si tienes alguna pregunta, no dudes en contactar al administrador del sistema.
            
            ¡Gracias por ser parte del equipo!
            """, nombre, email, password);

        try {
            enviarCorreoHTML(email, asunto, mensaje);
        } catch (Exception e) {
            System.err.println("Error enviando correo de bienvenida: " + e.getMessage());
        }
    }

    /**
     * Enviar correo de recuperación de contraseña
     */
    public void enviarCorreoRecuperacion(String email, String nuevaPassword) {
        String asunto = "Recuperación de Contraseña - Sistema Ciempiés";
        String mensaje = String.format("""
            Hola,
            
            Se ha solicitado un restablecimiento de contraseña para tu cuenta.
            
            Tu nueva contraseña temporal es:
            
            🔐 %s
            
            ⚠️ Por seguridad, te recomendamos cambiar esta contraseña inmediatamente después de iniciar sesión.
            
            Si no solicitaste este cambio, contacta al administrador del sistema.
            """, nuevaPassword);

        try {
            enviarCorreoHTML(email, asunto, mensaje);
        } catch (Exception e) {
            System.err.println("Error enviando correo de recuperación: " + e.getMessage());
        }
    }
}
