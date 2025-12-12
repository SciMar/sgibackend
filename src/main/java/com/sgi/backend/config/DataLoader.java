package com.sgi.backend.config;

import com.sgi.backend.model.*;
import com.sgi.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ZonaRepository zonaRepository;

    @Autowired
    private JornadaRepository jornadaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ColegioRepository colegioRepository;

    @Autowired
    private ColegioJornadaRepository colegioJornadaRepository;

    @Autowired
    private RutaRepository rutaRepository;

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String CONTRASENA_GENERICA = "Ciempies2024!";

    // ==========================================
    // LISTAS EXPANDIDAS DE NOMBRES (100+ cada una)
    // ==========================================
    private static final String[] NOMBRES_MASCULINOS = {
            "Juan", "Carlos", "Andrés", "Luis", "Diego", "Santiago", "Sebastián", "Mateo", "Nicolás", "Daniel",
            "David", "Miguel", "Alejandro", "Felipe", "Javier", "Ricardo", "Pablo", "Jorge", "Manuel", "Camilo",
            "Samuel", "Tomás", "Emilio", "Gabriel", "Rafael", "Ángel", "Martín", "Eduardo", "Fernando", "Sergio",
            "Iván", "Óscar", "Adrián", "Hugo", "Rubén", "Álvaro", "Pedro", "Raúl", "Gonzalo", "Víctor",
            "Enrique", "Alberto", "Joaquín", "Ignacio", "Cristian", "Esteban", "Simón", "Leonardo", "Julián", "Maximiliano",
            "Rodrigo", "Fabián", "Mauricio", "Gustavo", "Héctor", "Arturo", "César", "Roberto", "Ernesto", "Germán",
            "Jairo", "Freddy", "Wilson", "Brayan", "Kevin", "Johan", "Jeisson", "Duván", "Yeferson", "Stiven",
            "Brandon", "Jonathan", "Anderson", "Jefferson", "Dilan", "Brahian", "Yeison", "Cristhian", "Jhon", "Arley",
            "Alexis", "Bryan", "Harold", "Edinson", "Maicol", "Fabian", "Leandro", "Ezequiel", "Thiago", "Benjamín",
            "Luciano", "Valentín", "Agustín", "Bruno", "Dante", "Franco", "Gael", "Ian", "Liam", "Noah"
    };

    private static final String[] NOMBRES_FEMENINOS = {
            "María", "Laura", "Ana", "Camila", "Valentina", "Sofía", "Isabella", "Mariana", "Paula", "Andrea",
            "Carolina", "Natalia", "Daniela", "Sara", "Juliana", "Gabriela", "Manuela", "Valeria", "Alejandra", "Catalina",
            "Lucía", "Emma", "Martina", "Victoria", "Elena", "Adriana", "Patricia", "Sandra", "Mónica", "Diana",
            "Claudia", "Lorena", "Marcela", "Viviana", "Ángela", "Paola", "Liliana", "Gloria", "Martha", "Rosa",
            "Fernanda", "Jimena", "Renata", "Regina", "Antonella", "Emilia", "Mía", "Bianca", "Abril", "Luciana",
            "Miranda", "Samantha", "Nicole", "Melanie", "Ashley", "Britney", "Jennifer", "Jessica", "Katherine", "Stephanie",
            "Tatiana", "Vanessa", "Yuliana", "Estefanía", "Karen", "Lina", "Mayra", "Milena", "Yesica", "Leidy",
            "Angie", "Dayana", "Yuri", "Wendy", "Cindy", "Kelly", "Ingrid", "Johana", "Luisa", "Ximena",
            "Rocío", "Pilar", "Inés", "Carmen", "Teresa", "Blanca", "Esperanza", "Luz", "Mercedes", "Amparo",
            "Agustina", "Alma", "Aurora", "Celeste", "Delfina", "Elisa", "Florencia", "Helena", "Irene", "Jazmín"
    };

    private static final String[] APELLIDOS = {
            "García", "Rodríguez", "Martínez", "López", "González", "Pérez", "Sánchez", "Ramírez", "Torres", "Flores",
            "Rivera", "Gómez", "Díaz", "Cruz", "Morales", "Herrera", "Jiménez", "Álvarez", "Romero", "Vargas",
            "Castro", "Ortiz", "Silva", "Mendoza", "Rojas", "Gutiérrez", "Moreno", "Ruiz", "Vásquez", "Molina",
            "Acosta", "Aguilar", "Alvarado", "Arias", "Benavides", "Bermúdez", "Blanco", "Bonilla", "Bravo", "Cabrera",
            "Calderón", "Camacho", "Campos", "Cárdenas", "Carrillo", "Castillo", "Cepeda", "Cervantes", "Chávez", "Contreras",
            "Córdoba", "Coronado", "Corrales", "Cortés", "Cuellar", "Delgado", "Duarte", "Durán", "Escobar", "Espinosa",
            "Estrada", "Fajardo", "Figueroa", "Franco", "Fuentes", "Galindo", "Gallego", "Garay", "Gaviria", "Gil",
            "Giraldo", "Guerrero", "Henao", "Hernández", "Hurtado", "Ibáñez", "Jaramillo", "Lara", "Leal", "León",
            "Londoño", "Lozano", "Luna", "Maldonado", "Marin", "Medina", "Mejía", "Méndez", "Miranda", "Montoya",
            "Mora", "Muñoz", "Murillo", "Navarro", "Niño", "Núñez", "Ochoa", "Ordóñez", "Orozco", "Ospina",
            "Otero", "Palacios", "Pardo", "Paredes", "Parra", "Peña", "Pineda", "Pinto", "Portilla", "Posada",
            "Prieto", "Quintero", "Quiroga", "Restrepo", "Reyes", "Rincón", "Ríos", "Rivas", "Roa", "Robayo"
    };

    // Set para rastrear nombres completos ya usados
    private Set<String> nombresUsados = new HashSet<>();
    private Random random = new Random(42); // Semilla fija para reproducibilidad

    @Override
    public void run(String... args) throws Exception {

        if (usuarioRepository.count() > 0) {
            System.out.println("⚠️  La base de datos ya tiene datos. No se cargara data inicial.");
            return;
        }

        System.out.println("📦 Cargando datos iniciales...");

        String passwordHash = passwordEncoder.encode(CONTRASENA_GENERICA);
        System.out.println("🔐 Contraseña genérica para todos los usuarios: " + CONTRASENA_GENERICA);

        // ==========================================
        // 1. ZONAS
        // ==========================================
        Zona zona1 = crearZona("ZONA 1", "Usaquen", "Norte");
        Zona zona2 = crearZona("ZONA 2", "Santa Fe", "Centro-Oriente");
        Zona zona3 = crearZona("ZONA 3", "San Cristobal", "Sur-Oriente");
        Zona zona4 = crearZona("ZONA 4", "Kennedy", "Sur-Occidente");
        Zona zona5 = crearZona("ZONA 5", "Bosa", "Sur-Occidente");
        Zona zona6 = crearZona("ZONA 6", "Ciudad Bolivar", "Sur");

        // ==========================================
        // 2. JORNADAS (18 jornadas: 3 por zona)
        // ==========================================
        Jornada j1 = crearJornada("J001", TipoJornada.MANANA, zona1);
        Jornada j2 = crearJornada("J002", TipoJornada.UNICA, zona1);
        Jornada j3 = crearJornada("J003", TipoJornada.TARDE, zona1);

        Jornada j4 = crearJornada("J004", TipoJornada.MANANA, zona2);
        Jornada j5 = crearJornada("J005", TipoJornada.UNICA, zona2);
        Jornada j6 = crearJornada("J006", TipoJornada.TARDE, zona2);

        Jornada j7 = crearJornada("J007", TipoJornada.MANANA, zona3);
        Jornada j8 = crearJornada("J008", TipoJornada.UNICA, zona3);
        Jornada j9 = crearJornada("J009", TipoJornada.TARDE, zona3);

        Jornada j10 = crearJornada("J010", TipoJornada.MANANA, zona4);
        Jornada j11 = crearJornada("J011", TipoJornada.UNICA, zona4);
        Jornada j12 = crearJornada("J012", TipoJornada.TARDE, zona4);

        Jornada j13 = crearJornada("J013", TipoJornada.MANANA, zona5);
        Jornada j14 = crearJornada("J014", TipoJornada.UNICA, zona5);
        Jornada j15 = crearJornada("J015", TipoJornada.TARDE, zona5);

        Jornada j16 = crearJornada("J016", TipoJornada.MANANA, zona6);
        Jornada j17 = crearJornada("J017", TipoJornada.UNICA, zona6);
        Jornada j18 = crearJornada("J018", TipoJornada.TARDE, zona6);

        // ==========================================
        // 3. USUARIOS
        // ==========================================
        Usuario admin = crearUsuario("CC", "1000000001", "Juan", "Carlos", "Administrador", "Sistema",
                "admin@ciempies.com", passwordHash, Rol.ADMINISTRADOR, false);

        Usuario enc1 = crearUsuario("CC", "1000000002", "Maria", "Isabel", "Rodriguez", "Gomez",
                "propositocoreo@gmail.com", passwordHash, Rol.ENCARGADO, true);
        Usuario enc2 = crearUsuario("CC", "1000000003", "Pedro", "Luis", "Martinez", "Lopez",
                "patriciacc204@gmail.com", passwordHash, Rol.ENCARGADO, true);
        Usuario enc3 = crearUsuario("CC", "1000000004", "Ana", "Patricia", "Garcia", "Diaz",
                "encargado.sancristobal@ciempies.com", passwordHash, Rol.ENCARGADO, true);
        Usuario enc4 = crearUsuario("CC", "1000000005", "Carlos", "Eduardo", "Hernandez", "Silva",
                "encargado.kennedy@ciempies.com", passwordHash, Rol.ENCARGADO, true);
        Usuario enc5 = crearUsuario("CC", "1000000006", "Laura", "Andrea", "Ramirez", "Castro",
                "encargado.bosa@ciempies.com", passwordHash, Rol.ENCARGADO, true);
        Usuario enc6 = crearUsuario("CC", "1000000007", "Jorge", "Enrique", "Vargas", "Torres",
                "encargado.bolivar@ciempies.com", passwordHash, Rol.ENCARGADO, true);

        Usuario mon1 = crearUsuario("CC", "1000000010", "Carlos", "Andres", "Monitor", "Perez",
                "cmramireza29@gmail.com", passwordHash, Rol.MONITOR, true);
        Usuario mon2 = crearUsuario("CC", "1000000011", "Ana", "Maria", "Supervisora", "Garcia",
                "santiagorioscajamarca@gmail.com", passwordHash, Rol.MONITOR, true);
        Usuario mon3 = crearUsuario("CC", "1000000012", "Luis", "Fernando", "Acompanante", "Diaz",
                "kritho0311@gmail.com", passwordHash, Rol.MONITOR, true);
        Usuario mon4 = crearUsuario("CC", "1000000013", "Diana", "Carolina", "Ruiz", "Moreno",
                "alarconpaula@gmail.com", passwordHash, Rol.MONITOR, true);
        Usuario mon5 = crearUsuario("CC", "1000000014", "Ricardo", "Alberto", "Castro", "Lopez",
                "monitor.sancristobal1@ciempies.com", passwordHash, Rol.MONITOR, true);
        Usuario mon6 = crearUsuario("CC", "1000000015", "Sandra", "Milena", "Gomez", "Martinez",
                "monitor.sancristobal2@ciempies.com", passwordHash, Rol.MONITOR, true);
        Usuario mon7 = crearUsuario("CC", "1000000016", "Miguel", "Angel", "Torres", "Ramirez",
                "monitor.kennedy1@ciempies.com", passwordHash, Rol.MONITOR, true);
        Usuario mon8 = crearUsuario("CC", "1000000017", "Patricia", "Elena", "Mendez", "Silva",
                "monitor.kennedy2@ciempies.com", passwordHash, Rol.MONITOR, true);
        Usuario mon9 = crearUsuario("CC", "1000000018", "Andres", "Felipe", "Herrera", "Gutierrez",
                "monitor.bosa1@ciempies.com", passwordHash, Rol.MONITOR, true);
        Usuario mon10 = crearUsuario("CC", "1000000019", "Monica", "Alejandra", "Jimenez", "Ortiz",
                "monitor.bosa2@ciempies.com", passwordHash, Rol.MONITOR, true);
        Usuario mon11 = crearUsuario("CC", "1000000020", "Javier", "Eduardo", "Morales", "Castro",
                "monitor.bolivar1@ciempies.com", passwordHash, Rol.MONITOR, true);
        Usuario mon12 = crearUsuario("CC", "1000000021", "Claudia", "Marcela", "Rojas", "Vargas",
                "monitor.bolivar2@ciempies.com", passwordHash, Rol.MONITOR, true);

        // ==========================================
        // 4. COLEGIOS (3 por zona = 18 colegios)
        // ==========================================
        Colegio col1 = crearColegio("Colegio Distrital Usaquen", zona1);
        Colegio col2 = crearColegio("Instituto Santa Barbara", zona1);
        Colegio col3 = crearColegio("Liceo Boston", zona1);

        Colegio col4 = crearColegio("Colegio Agustin Nieto Caballero", zona2);
        Colegio col5 = crearColegio("Instituto Francisco Jose de Caldas", zona2);
        Colegio col6 = crearColegio("Colegio San Martin de Porres", zona2);

        Colegio col7 = crearColegio("Colegio San Cristobal Sur", zona3);
        Colegio col8 = crearColegio("Instituto Juan del Corral", zona3);
        Colegio col9 = crearColegio("Colegio Ramon de Zubiria", zona3);

        Colegio col10 = crearColegio("Colegio Kennedy", zona4);
        Colegio col11 = crearColegio("Instituto Tecnico Industrial", zona4);
        Colegio col12 = crearColegio("Colegio Castilla", zona4);

        Colegio col13 = crearColegio("Colegio Integrado de Fontibon", zona5);
        Colegio col14 = crearColegio("Instituto San Bernardino", zona5);
        Colegio col15 = crearColegio("Colegio Paulo VI", zona5);

        Colegio col16 = crearColegio("Colegio Arborizadora Alta", zona6);
        Colegio col17 = crearColegio("Instituto Tecnico Distrital", zona6);
        Colegio col18 = crearColegio("Colegio Ciudad Bolivar", zona6);

        // ==========================================
        // 5. ASIGNACION COLEGIOS - JORNADAS
        // ==========================================
        crearColegioJornada(col1, j1);
        crearColegioJornada(col1, j3);
        crearColegioJornada(col2, j1);
        crearColegioJornada(col2, j3);
        crearColegioJornada(col3, j2);

        crearColegioJornada(col4, j4);
        crearColegioJornada(col4, j6);
        crearColegioJornada(col5, j4);
        crearColegioJornada(col5, j6);
        crearColegioJornada(col6, j5);

        crearColegioJornada(col7, j7);
        crearColegioJornada(col7, j9);
        crearColegioJornada(col8, j7);
        crearColegioJornada(col8, j9);
        crearColegioJornada(col9, j8);

        crearColegioJornada(col10, j10);
        crearColegioJornada(col10, j12);
        crearColegioJornada(col11, j10);
        crearColegioJornada(col11, j12);
        crearColegioJornada(col12, j11);

        crearColegioJornada(col13, j13);
        crearColegioJornada(col13, j15);
        crearColegioJornada(col14, j13);
        crearColegioJornada(col14, j15);
        crearColegioJornada(col15, j14);

        crearColegioJornada(col16, j16);
        crearColegioJornada(col16, j18);
        crearColegioJornada(col17, j16);
        crearColegioJornada(col17, j18);
        crearColegioJornada(col18, j17);

        // ==========================================
        // 6. RUTAS
        // ==========================================
        Ruta r1 = crearRuta("Usaquen Distrital - Manana IDA", TipoRecorrido.IDA, zona1);
        Ruta r2 = crearRuta("Usaquen Distrital - Manana REGRESO", TipoRecorrido.REGRESO, zona1);
        Ruta r3 = crearRuta("Usaquen Distrital - Tarde IDA", TipoRecorrido.IDA, zona1);
        Ruta r4 = crearRuta("Usaquen Distrital - Tarde REGRESO", TipoRecorrido.REGRESO, zona1);
        Ruta r5 = crearRuta("Santa Barbara - Manana IDA", TipoRecorrido.IDA, zona1);
        Ruta r6 = crearRuta("Santa Barbara - Manana REGRESO", TipoRecorrido.REGRESO, zona1);
        Ruta r7 = crearRuta("Santa Barbara - Tarde IDA", TipoRecorrido.IDA, zona1);
        Ruta r8 = crearRuta("Santa Barbara - Tarde REGRESO", TipoRecorrido.REGRESO, zona1);
        Ruta r9 = crearRuta("Liceo Boston - Unica IDA", TipoRecorrido.IDA, zona1);
        Ruta r10 = crearRuta("Liceo Boston - Unica REGRESO", TipoRecorrido.REGRESO, zona1);

        Ruta r11 = crearRuta("Agustin Nieto - Manana IDA", TipoRecorrido.IDA, zona2);
        Ruta r12 = crearRuta("Agustin Nieto - Manana REGRESO", TipoRecorrido.REGRESO, zona2);
        Ruta r13 = crearRuta("Agustin Nieto - Tarde IDA", TipoRecorrido.IDA, zona2);
        Ruta r14 = crearRuta("Agustin Nieto - Tarde REGRESO", TipoRecorrido.REGRESO, zona2);
        Ruta r15 = crearRuta("Caldas - Manana IDA", TipoRecorrido.IDA, zona2);
        Ruta r16 = crearRuta("Caldas - Manana REGRESO", TipoRecorrido.REGRESO, zona2);
        Ruta r17 = crearRuta("Caldas - Tarde IDA", TipoRecorrido.IDA, zona2);
        Ruta r18 = crearRuta("Caldas - Tarde REGRESO", TipoRecorrido.REGRESO, zona2);
        Ruta r19 = crearRuta("San Martin - Unica IDA", TipoRecorrido.IDA, zona2);
        Ruta r20 = crearRuta("San Martin - Unica REGRESO", TipoRecorrido.REGRESO, zona2);

        Ruta r21 = crearRuta("San Cristobal Sur - Manana IDA", TipoRecorrido.IDA, zona3);
        Ruta r22 = crearRuta("San Cristobal Sur - Manana REGRESO", TipoRecorrido.REGRESO, zona3);
        Ruta r23 = crearRuta("San Cristobal Sur - Tarde IDA", TipoRecorrido.IDA, zona3);
        Ruta r24 = crearRuta("San Cristobal Sur - Tarde REGRESO", TipoRecorrido.REGRESO, zona3);
        Ruta r25 = crearRuta("Juan del Corral - Manana IDA", TipoRecorrido.IDA, zona3);
        Ruta r26 = crearRuta("Juan del Corral - Manana REGRESO", TipoRecorrido.REGRESO, zona3);
        Ruta r27 = crearRuta("Juan del Corral - Tarde IDA", TipoRecorrido.IDA, zona3);
        Ruta r28 = crearRuta("Juan del Corral - Tarde REGRESO", TipoRecorrido.REGRESO, zona3);
        Ruta r29 = crearRuta("Ramon de Zubiria - Unica IDA", TipoRecorrido.IDA, zona3);
        Ruta r30 = crearRuta("Ramon de Zubiria - Unica REGRESO", TipoRecorrido.REGRESO, zona3);

        Ruta r31 = crearRuta("Kennedy - Manana IDA", TipoRecorrido.IDA, zona4);
        Ruta r32 = crearRuta("Kennedy - Manana REGRESO", TipoRecorrido.REGRESO, zona4);
        Ruta r33 = crearRuta("Kennedy - Tarde IDA", TipoRecorrido.IDA, zona4);
        Ruta r34 = crearRuta("Kennedy - Tarde REGRESO", TipoRecorrido.REGRESO, zona4);
        Ruta r35 = crearRuta("Tecnico Kennedy - Manana IDA", TipoRecorrido.IDA, zona4);
        Ruta r36 = crearRuta("Tecnico Kennedy - Manana REGRESO", TipoRecorrido.REGRESO, zona4);
        Ruta r37 = crearRuta("Tecnico Kennedy - Tarde IDA", TipoRecorrido.IDA, zona4);
        Ruta r38 = crearRuta("Tecnico Kennedy - Tarde REGRESO", TipoRecorrido.REGRESO, zona4);
        Ruta r39 = crearRuta("Castilla - Unica IDA", TipoRecorrido.IDA, zona4);
        Ruta r40 = crearRuta("Castilla - Unica REGRESO", TipoRecorrido.REGRESO, zona4);

        Ruta r41 = crearRuta("Integrado Fontibon - Manana IDA", TipoRecorrido.IDA, zona5);
        Ruta r42 = crearRuta("Integrado Fontibon - Manana REGRESO", TipoRecorrido.REGRESO, zona5);
        Ruta r43 = crearRuta("Integrado Fontibon - Tarde IDA", TipoRecorrido.IDA, zona5);
        Ruta r44 = crearRuta("Integrado Fontibon - Tarde REGRESO", TipoRecorrido.REGRESO, zona5);
        Ruta r45 = crearRuta("San Bernardino - Manana IDA", TipoRecorrido.IDA, zona5);
        Ruta r46 = crearRuta("San Bernardino - Manana REGRESO", TipoRecorrido.REGRESO, zona5);
        Ruta r47 = crearRuta("San Bernardino - Tarde IDA", TipoRecorrido.IDA, zona5);
        Ruta r48 = crearRuta("San Bernardino - Tarde REGRESO", TipoRecorrido.REGRESO, zona5);
        Ruta r49 = crearRuta("Paulo VI - Unica IDA", TipoRecorrido.IDA, zona5);
        Ruta r50 = crearRuta("Paulo VI - Unica REGRESO", TipoRecorrido.REGRESO, zona5);

        Ruta r51 = crearRuta("Arborizadora Alta - Manana IDA", TipoRecorrido.IDA, zona6);
        Ruta r52 = crearRuta("Arborizadora Alta - Manana REGRESO", TipoRecorrido.REGRESO, zona6);
        Ruta r53 = crearRuta("Arborizadora Alta - Tarde IDA", TipoRecorrido.IDA, zona6);
        Ruta r54 = crearRuta("Arborizadora Alta - Tarde REGRESO", TipoRecorrido.REGRESO, zona6);
        Ruta r55 = crearRuta("Tecnico Bolivar - Manana IDA", TipoRecorrido.IDA, zona6);
        Ruta r56 = crearRuta("Tecnico Bolivar - Manana REGRESO", TipoRecorrido.REGRESO, zona6);
        Ruta r57 = crearRuta("Tecnico Bolivar - Tarde IDA", TipoRecorrido.IDA, zona6);
        Ruta r58 = crearRuta("Tecnico Bolivar - Tarde REGRESO", TipoRecorrido.REGRESO, zona6);
        Ruta r59 = crearRuta("Ciudad Bolivar - Unica IDA", TipoRecorrido.IDA, zona6);
        Ruta r60 = crearRuta("Ciudad Bolivar - Unica REGRESO", TipoRecorrido.REGRESO, zona6);

        // ==========================================
        // 7. MONITORES
        // ==========================================
        crearMonitor(mon1, zona1, j1);
        crearMonitor(mon2, zona1, j3);
        crearMonitor(mon3, zona2, j4);
        crearMonitor(mon4, zona2, j6);
        crearMonitor(mon5, zona3, j7);
        crearMonitor(mon6, zona3, j9);
        crearMonitor(mon7, zona4, j10);
        crearMonitor(mon8, zona4, j12);
        crearMonitor(mon9, zona5, j13);
        crearMonitor(mon10, zona5, j15);
        crearMonitor(mon11, zona6, j16);
        crearMonitor(mon12, zona6, j18);

        crearMonitor(admin, zona1, j1);
        crearMonitor(enc1, zona1, j1);
        crearMonitor(enc2, zona2, j4);
        crearMonitor(enc3, zona3, j7);
        crearMonitor(enc4, zona4, j10);
        crearMonitor(enc5, zona5, j13);
        crearMonitor(enc6, zona6, j16);

        // ==========================================
        // 8. ESTUDIANTES (con nombres únicos)
        // ==========================================
        int estudianteId = 1100000001;

        // USAQUEN
        estudianteId = crearEstudiantesLote(estudianteId, col1, j1, r1, r2, 40);
        estudianteId = crearEstudiantesLote(estudianteId, col1, j3, r3, r4, 35);
        estudianteId = crearEstudiantesLote(estudianteId, col2, j1, r5, r6, 38);
        estudianteId = crearEstudiantesLote(estudianteId, col2, j3, r7, r8, 32);
        estudianteId = crearEstudiantesLote(estudianteId, col3, j2, r9, r10, 45);

        // SANTA FE
        estudianteId = crearEstudiantesLote(estudianteId, col4, j4, r11, r12, 42);
        estudianteId = crearEstudiantesLote(estudianteId, col4, j6, r13, r14, 38);
        estudianteId = crearEstudiantesLote(estudianteId, col5, j4, r15, r16, 35);
        estudianteId = crearEstudiantesLote(estudianteId, col5, j6, r17, r18, 40);
        estudianteId = crearEstudiantesLote(estudianteId, col6, j5, r19, r20, 43);

        // SAN CRISTOBAL
        estudianteId = crearEstudiantesLote(estudianteId, col7, j7, r21, r22, 41);
        estudianteId = crearEstudiantesLote(estudianteId, col7, j9, r23, r24, 36);
        estudianteId = crearEstudiantesLote(estudianteId, col8, j7, r25, r26, 39);
        estudianteId = crearEstudiantesLote(estudianteId, col8, j9, r27, r28, 34);
        estudianteId = crearEstudiantesLote(estudianteId, col9, j8, r29, r30, 44);

        // KENNEDY
        estudianteId = crearEstudiantesLote(estudianteId, col10, j10, r31, r32, 40);
        estudianteId = crearEstudiantesLote(estudianteId, col10, j12, r33, r34, 37);
        estudianteId = crearEstudiantesLote(estudianteId, col11, j10, r35, r36, 36);
        estudianteId = crearEstudiantesLote(estudianteId, col11, j12, r37, r38, 41);
        estudianteId = crearEstudiantesLote(estudianteId, col12, j11, r39, r40, 42);

        // BOSA
        estudianteId = crearEstudiantesLote(estudianteId, col13, j13, r41, r42, 38);
        estudianteId = crearEstudiantesLote(estudianteId, col13, j15, r43, r44, 33);
        estudianteId = crearEstudiantesLote(estudianteId, col14, j13, r45, r46, 40);
        estudianteId = crearEstudiantesLote(estudianteId, col14, j15, r47, r48, 35);
        estudianteId = crearEstudiantesLote(estudianteId, col15, j14, r49, r50, 45);

        // CIUDAD BOLIVAR
        estudianteId = crearEstudiantesLote(estudianteId, col16, j16, r51, r52, 39);
        estudianteId = crearEstudiantesLote(estudianteId, col16, j18, r53, r54, 34);
        estudianteId = crearEstudiantesLote(estudianteId, col17, j16, r55, r56, 37);
        estudianteId = crearEstudiantesLote(estudianteId, col17, j18, r57, r58, 40);
        estudianteId = crearEstudiantesLote(estudianteId, col18, j17, r59, r60, 43);

        int totalEstudiantes = estudianteId - 1100000001;

        System.out.println("");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("🚀 SISTEMA CIEMPIÉS - SERVIDOR INICIADO");
        System.out.println("═══════════════════════════════════════════════════════════");
        System.out.println("");
        System.out.println("🌐 Acceder al sistema:");
        System.out.println("   → http://localhost:8080");
        System.out.println("");
        System.out.println("📖 Documentación API (Swagger):");
        System.out.println("   → http://localhost:8080/swagger-ui.html");
        System.out.println("");
        System.out.println("✅ Datos iniciales cargados correctamente!");
        System.out.println("📋 Resumen:");
        System.out.println("   - Zonas: " + zonaRepository.count());
        System.out.println("   - Jornadas: " + jornadaRepository.count());
        System.out.println("   - Usuarios: " + usuarioRepository.count());
        System.out.println("   - Colegios: " + colegioRepository.count());
        System.out.println("   - Rutas: " + rutaRepository.count());
        System.out.println("   - Estudiantes: " + totalEstudiantes + " (todos con nombres únicos)");
        System.out.println("");
        System.out.println("🔑 CREDENCIALES DE ACCESO:");
        System.out.println("   Admin: admin@ciempies.com / " + CONTRASENA_GENERICA + " (no requiere cambio)");
        System.out.println("   Otros usuarios: [email] / " + CONTRASENA_GENERICA + " (deben cambiar contraseña)");
        System.out.println("");
        System.out.println("🚀 ¡PUEDES HACER LOGIN AHORA!");
        System.out.println("======================================================");
    }

    // ==========================================
    // MÉTODOS AUXILIARES
    // ==========================================

    private Zona crearZona(String codigo, String nombre, String descripcion) {
        Zona zona = new Zona();
        zona.setCodigoZona(codigo);
        zona.setNombreZona(nombre);
        zona.setDescripcion(descripcion);
        zona.setActiva(true);
        return zonaRepository.save(zona);
    }

    private Jornada crearJornada(String codigo, TipoJornada nombre, Zona zona) {
        Jornada jornada = new Jornada();
        jornada.setCodigoJornada(codigo);
        jornada.setNombreJornada(nombre);
        jornada.setZona(zona);
        jornada.setActiva(true);
        return jornadaRepository.save(jornada);
    }

    private Usuario crearUsuario(String tipoId, String numId, String primerNombre, String segundoNombre,
                                 String primerApellido, String segundoApellido, String email,
                                 String passwordHash, Rol rol, boolean primerIngreso) {
        Usuario usuario = new Usuario();
        usuario.setTipoId(TipoId.valueOf(tipoId));
        usuario.setNumId(numId);
        usuario.setPrimerNombre(primerNombre);
        usuario.setSegundoNombre(segundoNombre);
        usuario.setPrimerApellido(primerApellido);
        usuario.setSegundoApellido(segundoApellido);
        usuario.setEmail(email);
        usuario.setContrasena(passwordHash);
        usuario.setRol(rol);
        usuario.setActivo(true);
        usuario.setPrimerIngreso(primerIngreso);
        usuario.setFechaCreacion(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    private Usuario crearUsuario(String tipoId, String numId, String primerNombre, String segundoNombre,
                                 String primerApellido, String segundoApellido, String email,
                                 String passwordHash, Rol rol) {
        return crearUsuario(tipoId, numId, primerNombre, segundoNombre, primerApellido,
                segundoApellido, email, passwordHash, rol, true);
    }

    private Colegio crearColegio(String nombre, Zona zona) {
        Colegio colegio = new Colegio();
        colegio.setNombreColegio(nombre);
        colegio.setZona(zona);
        colegio.setActivo(true);
        return colegioRepository.save(colegio);
    }

    private void crearColegioJornada(Colegio colegio, Jornada jornada) {
        ColegioJornada cj = new ColegioJornada();
        cj.setColegio(colegio);
        cj.setJornada(jornada);
        cj.setActiva(true);
        colegioJornadaRepository.save(cj);
    }

    private Ruta crearRuta(String nombre, TipoRecorrido tipo, Zona zona) {
        Ruta ruta = new Ruta();
        ruta.setNombreRuta(nombre);
        ruta.setTipoRuta(tipo);
        ruta.setZona(zona);
        ruta.setActiva(true);
        return rutaRepository.save(ruta);
    }

    private void crearMonitor(Usuario usuario, Zona zona, Jornada jornada) {
        Monitor monitor = new Monitor();
        monitor.setUsuario(usuario);
        monitor.setZona(zona);
        monitor.setJornada(jornada);
        monitor.setFechaAsignacion(LocalDate.now());
        monitor.setActivo(true);
        monitorRepository.save(monitor);
    }

    /**
     * Genera un nombre completo único (primerNombre + primerApellido)
     */
    private String[] generarNombreUnico(boolean esMasculino) {
        String[] nombres = esMasculino ? NOMBRES_MASCULINOS : NOMBRES_FEMENINOS;
        int maxIntentos = 1000;
        int intentos = 0;

        while (intentos < maxIntentos) {
            String primerNombre = nombres[random.nextInt(nombres.length)];
            String segundoNombre = nombres[random.nextInt(nombres.length)];
            String primerApellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
            String segundoApellido = APELLIDOS[random.nextInt(APELLIDOS.length)];

            // Evitar que segundo nombre sea igual al primero
            while (segundoNombre.equals(primerNombre)) {
                segundoNombre = nombres[random.nextInt(nombres.length)];
            }

            // Evitar que segundo apellido sea igual al primero
            while (segundoApellido.equals(primerApellido)) {
                segundoApellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
            }

            // Crear clave única: primerNombre + primerApellido
            String claveUnica = primerNombre + " " + primerApellido;

            if (!nombresUsados.contains(claveUnica)) {
                nombresUsados.add(claveUnica);
                return new String[]{primerNombre, segundoNombre, primerApellido, segundoApellido};
            }

            intentos++;
        }

        // Si no encontramos único después de muchos intentos, agregar sufijo numérico
        String primerNombre = nombres[random.nextInt(nombres.length)];
        String primerApellido = APELLIDOS[random.nextInt(APELLIDOS.length)] + nombresUsados.size();
        nombresUsados.add(primerNombre + " " + primerApellido);

        return new String[]{
                primerNombre,
                nombres[random.nextInt(nombres.length)],
                primerApellido,
                APELLIDOS[random.nextInt(APELLIDOS.length)]
        };
    }

    private int crearEstudiantesLote(int inicioId, Colegio colegio, Jornada jornada,
                                     Ruta rutaIda, Ruta rutaRegreso, int cantidad) {

        String[] cursos = {"5A", "5B", "6A", "6B", "7A", "7B", "8A", "8B", "9A", "9B", "10A", "10B", "11A", "11B"};
        String[] eps = {"Nueva EPS", "Sanitas", "Sura", "Compensar", "Famisanar", "Salud Total", "Coomeva", "Medimas"};
        String[] direccionesBase = {"Calle", "Carrera", "Avenida", "Diagonal", "Transversal"};

        for (int i = 0; i < cantidad; i++) {
            boolean esMasculino = random.nextBoolean();
            Sexo sexo = esMasculino ? Sexo.MASCULINO : Sexo.FEMENINO;

            // Generar nombre único
            String[] nombreCompleto = generarNombreUnico(esMasculino);
            String primerNombre = nombreCompleto[0];
            String segundoNombre = nombreCompleto[1];
            String primerApellido = nombreCompleto[2];
            String segundoApellido = nombreCompleto[3];

            String numId = String.valueOf(inicioId + i);

            // Fecha de nacimiento (entre 2008 y 2016)
            int año = 2008 + random.nextInt(9);
            int mes = 1 + random.nextInt(12);
            int dia = 1 + random.nextInt(28);
            String fechaNacimiento = String.format("%d-%02d-%02d", año, mes, dia);

            // Dirección aleatoria
            String direccion = String.format("%s %d #%d-%d",
                    direccionesBase[random.nextInt(direccionesBase.length)],
                    10 + random.nextInt(150),
                    5 + random.nextInt(30),
                    10 + random.nextInt(90));

            String curso = cursos[random.nextInt(cursos.length)];
            String epsSeleccionada = eps[random.nextInt(eps.length)];

            // Información del acudiente (basada en apellidos del estudiante)
            String nombreAcudiente = APELLIDOS[random.nextInt(APELLIDOS.length)] + " " +
                    (esMasculino ? NOMBRES_FEMENINOS[random.nextInt(NOMBRES_FEMENINOS.length)] :
                            NOMBRES_MASCULINOS[random.nextInt(NOMBRES_MASCULINOS.length)]);
            String telefonoAcudiente = "3" + String.format("%02d", random.nextInt(23)) +
                    String.format("%07d", random.nextInt(10000000));
            String emailAcudiente = primerNombre.toLowerCase().replace("á", "a").replace("é", "e")
                    .replace("í", "i").replace("ó", "o").replace("ú", "u") + "." +
                    primerApellido.toLowerCase().replace("á", "a").replace("é", "e")
                            .replace("í", "i").replace("ó", "o").replace("ú", "u") +
                    random.nextInt(100) + "@email.com";

            // Alternar entre ruta de IDA y REGRESO
            Ruta ruta = random.nextBoolean() ? rutaIda : rutaRegreso;

            crearEstudiante("TI", numId, primerNombre, segundoNombre, primerApellido, segundoApellido,
                    fechaNacimiento, sexo, direccion, curso, epsSeleccionada, nombreAcudiente,
                    telefonoAcudiente, emailAcudiente, colegio, jornada, ruta);
        }

        return inicioId + cantidad;
    }

    private void crearEstudiante(String tipoId, String numId, String primerNombre, String segundoNombre,
                                 String primerApellido, String segundoApellido, String fechaNacimiento,
                                 Sexo sexo, String direccion, String curso, String eps,
                                 String nombreAcudiente, String telefonoAcudiente, String emailAcudiente,
                                 Colegio colegio, Jornada jornada, Ruta ruta) {
        Estudiante estudiante = new Estudiante();
        estudiante.setTipoId(TipoId.valueOf(tipoId));
        estudiante.setNumId(numId);
        estudiante.setPrimerNombre(primerNombre);
        estudiante.setSegundoNombre(segundoNombre);
        estudiante.setPrimerApellido(primerApellido);
        estudiante.setSegundoApellido(segundoApellido);
        estudiante.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
        estudiante.setSexo(sexo);
        estudiante.setDireccion(direccion);
        estudiante.setCurso(curso);
        estudiante.setEps(eps);
        estudiante.setDiscapacidad("Ninguna");
        estudiante.setEtnia("Ninguna");
        estudiante.setNombreAcudiente(nombreAcudiente);
        estudiante.setTelefonoAcudiente(telefonoAcudiente);
        estudiante.setDireccionAcudiente(direccion);
        estudiante.setEmailAcudiente(emailAcudiente);
        estudiante.setColegio(colegio);
        estudiante.setJornada(jornada);
        estudiante.setRuta(ruta);
        estudiante.setFechaInscripcion(LocalDate.now());
        estudiante.setEstadoInscripcion("ACTIVA");
        estudiante.setFechaRegistro(LocalDate.now());
        estudiante.setActivo(true);
        estudianteRepository.save(estudiante);
    }
}