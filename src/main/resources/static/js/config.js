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
    'PA': 'Pasaporte',
    'PPT': 'Permiso Protección Temporal',
    'PEP': 'Permiso Especial Permanencia'
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

const JORNADA_LABELS = {
    'MANANA': 'Mañana',
    'TARDE': 'Tarde',
    'UNICA': 'Única'
};

// Opciones de etnia
const ETNIAS = [
    'Ninguna',
    'Afrocolombiano',
    'Indígena',
    'Raizal',
    'Palenquero',
    'Rom/Gitano',
    'Mestizo',
    'Otro'
];

// Opciones de discapacidad
const DISCAPACIDADES = [
    'Ninguna',
    'Física',
    'Visual',
    'Auditiva',
    'Cognitiva',
    'Psicosocial',
    'Múltiple',
    'Sordoceguera',
    'Otra'
];

// Opciones de EPS
const EPS_OPCIONES = [
    'Ninguna',
    'Sura EPS',
    'Nueva EPS',
    'Sanitas',
    'Compensar',
    'Famisanar',
    'Salud Total',
    'Coomeva EPS',
    'Coosalud',
    'Mutual Ser',
    'Aliansalud',
    'Savia Salud',
    'Capital Salud',
    'Asmet Salud',
    'Emssanar',
    'Mallamas',
    'Pijaos Salud',
    'Comfenalco',
    'Sisben/Subsidiado',
    'Otra'
];

// ==========================================
// CONSTANTES PARA RUTAS
// ==========================================

const TIPO_RECORRIDO_LABELS = {
    'IDA': 'Ida',
    'REGRESO': 'Regreso'
};

const TIPO_RECORRIDO_BADGE_CLASS = {
    'IDA': 'bg-primary',
    'REGRESO': 'bg-warning text-dark'
};