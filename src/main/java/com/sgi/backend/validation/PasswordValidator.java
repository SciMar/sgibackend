package com.sgi.backend.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Validador para contraseñas robustas.
 *
 * Reglas de validación:
 * 1. Mínimo 8 caracteres
 * 2. Máximo 50 caracteres
 * 3. Al menos una letra mayúscula (A-Z)
 * 4. Al menos una letra minúscula (a-z)
 * 5. Al menos un número (0-9)
 * 6. Al menos un carácter especial (@#$%^&+=!.*_-)
 * 7. No puede contener espacios en blanco
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 50;

    // Expresiones regulares para cada requisito
    private static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    private static final String LOWERCASE_PATTERN = ".*[a-z].*";
    private static final String DIGIT_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[@#$%^&+=!.*_-].*";
    private static final String WHITESPACE_PATTERN = ".*\\s.*";

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        // No se necesita inicialización
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Si la contraseña es null, dejamos que @NotNull/@NotBlank lo maneje
        if (password == null) {
            return true;
        }

        List<String> errores = new ArrayList<>();

        // Validar longitud mínima
        if (password.length() < MIN_LENGTH) {
            errores.add("Debe tener al menos " + MIN_LENGTH + " caracteres");
        }

        // Validar longitud máxima
        if (password.length() > MAX_LENGTH) {
            errores.add("No puede exceder " + MAX_LENGTH + " caracteres");
        }

        // Validar mayúscula
        if (!password.matches(UPPERCASE_PATTERN)) {
            errores.add("Debe contener al menos una letra mayúscula");
        }

        // Validar minúscula
        if (!password.matches(LOWERCASE_PATTERN)) {
            errores.add("Debe contener al menos una letra minúscula");
        }

        // Validar número
        if (!password.matches(DIGIT_PATTERN)) {
            errores.add("Debe contener al menos un número");
        }

        // Validar carácter especial
        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            errores.add("Debe contener al menos un carácter especial (@#$%^&+=!.*_-)");
        }

        // Validar que no tenga espacios
        if (password.matches(WHITESPACE_PATTERN)) {
            errores.add("No puede contener espacios en blanco");
        }

        // Si hay errores, construir mensaje personalizado
        if (!errores.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "La contraseña no cumple los requisitos: " + String.join(", ", errores)
            ).addConstraintViolation();
            return false;
        }

        return true;
    }

    /**
     * Método estático para validar contraseña sin contexto de validación.
     * Útil para validaciones en el servicio.
     */
    public static ValidationResult validate(String password) {
        List<String> errores = new ArrayList<>();

        if (password == null || password.isEmpty()) {
            errores.add("La contraseña no puede estar vacía");
            return new ValidationResult(false, errores);
        }

        if (password.length() < MIN_LENGTH) {
            errores.add("Debe tener al menos " + MIN_LENGTH + " caracteres");
        }

        if (password.length() > MAX_LENGTH) {
            errores.add("No puede exceder " + MAX_LENGTH + " caracteres");
        }

        if (!password.matches(UPPERCASE_PATTERN)) {
            errores.add("Debe contener al menos una letra mayúscula");
        }

        if (!password.matches(LOWERCASE_PATTERN)) {
            errores.add("Debe contener al menos una letra minúscula");
        }

        if (!password.matches(DIGIT_PATTERN)) {
            errores.add("Debe contener al menos un número");
        }

        if (!password.matches(SPECIAL_CHAR_PATTERN)) {
            errores.add("Debe contener al menos un carácter especial (@#$%^&+=!.*_-)");
        }

        if (password.matches(WHITESPACE_PATTERN)) {
            errores.add("No puede contener espacios en blanco");
        }

        return new ValidationResult(errores.isEmpty(), errores);
    }

    /**
     * Clase para el resultado de la validación
     */
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;

        public ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors;
        }

        public boolean isValid() {
            return valid;
        }

        public List<String> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}
