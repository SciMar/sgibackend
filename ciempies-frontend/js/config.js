// Configuración global de la aplicación
const API_URL = 'http://localhost:8080/api';

// Etiquetas de roles
const ROL_LABELS = {
    'ADMINISTRADOR': 'Administrador',
    'ENCARGADO': 'Encargado',
    'MONITOR': 'Monitor'
};

// Etiquetas de tipos de ID
const TIPO_ID_LABELS = {
    'RC': 'Registro Civil',
    'TI': 'Tarjeta de Identidad',
    'CC': 'Cédula de Ciudadanía',
    'CE': 'Cédula de Extranjería',
    'PA': 'Pasaporte'
};

// Etiquetas de sexo
const SEXO_LABELS = {
    'MASCULINO': 'Masculino',
    'FEMENINO': 'Femenino',
    'OTRO': 'Otro'
};

// Etiquetas de estado de inscripción
const ESTADO_INSCRIPCION_LABELS = {
    'ACTIVA': 'Activa',
    'SUSPENDIDA': 'Suspendida',
    'FINALIZADA': 'Finalizada'
};

// Clases de badge por rol
const ROL_BADGE_CLASS = {
    'ADMINISTRADOR': 'bg-danger',
    'ENCARGADO': 'bg-primary',
    'MONITOR': 'bg-info'
};

// Clases de badge por estado de inscripción
const ESTADO_INSCRIPCION_BADGE_CLASS = {
    'ACTIVA': 'bg-success',
    'SUSPENDIDA': 'bg-warning',
    'FINALIZADA': 'bg-secondary'
};