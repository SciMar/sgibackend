package com.sgi.backend.config;

import com.sgi.backend.model.*;
import com.sgi.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Override
    public void run(String... args) throws Exception {

        // Verificar si ya hay datos
        if (usuarioRepository.count() > 0) {
            System.out.println("⚠️  La base de datos ya tiene datos. No se cargara data inicial.");
            return;
        }

        System.out.println("📦 Cargando datos iniciales...");

        // Hash para contraseña "admin123"
        String passwordHash = "$2a$10$UB5trHg7K/IWB3.Rg5tnyeagnQUrGgdD8epXkCLbqtQpMvGz4DKbG";

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
        // Usaquen
        Jornada j1 = crearJornada("J001", TipoJornada.MANANA, zona1);
        Jornada j2 = crearJornada("J002", TipoJornada.UNICA, zona1);
        Jornada j3 = crearJornada("J003", TipoJornada.TARDE, zona1);

        // Santa Fe
        Jornada j4 = crearJornada("J004", TipoJornada.MANANA, zona2);
        Jornada j5 = crearJornada("J005", TipoJornada.UNICA, zona2);
        Jornada j6 = crearJornada("J006", TipoJornada.TARDE, zona2);

        // San Cristobal
        Jornada j7 = crearJornada("J007", TipoJornada.MANANA, zona3);
        Jornada j8 = crearJornada("J008", TipoJornada.UNICA, zona3);
        Jornada j9 = crearJornada("J009", TipoJornada.TARDE, zona3);

        // Kennedy
        Jornada j10 = crearJornada("J010", TipoJornada.MANANA, zona4);
        Jornada j11 = crearJornada("J011", TipoJornada.UNICA, zona4);
        Jornada j12 = crearJornada("J012", TipoJornada.TARDE, zona4);

        // Bosa
        Jornada j13 = crearJornada("J013", TipoJornada.MANANA, zona5);
        Jornada j14 = crearJornada("J014", TipoJornada.UNICA, zona5);
        Jornada j15 = crearJornada("J015", TipoJornada.TARDE, zona5);

        // Ciudad Bolivar
        Jornada j16 = crearJornada("J016", TipoJornada.MANANA, zona6);
        Jornada j17 = crearJornada("J017", TipoJornada.UNICA, zona6);
        Jornada j18 = crearJornada("J018", TipoJornada.TARDE, zona6);

        // ==========================================
        // 3. USUARIOS
        // ==========================================
        // ADMINISTRADOR
        Usuario admin = crearUsuario("CC", "1000000001", "Juan", "Carlos", "Administrador", "Sistema",
                "admin@ciempies.com", passwordHash, Rol.ADMINISTRADOR);

        // ENCARGADOS (uno por zona)
        Usuario enc1 = crearUsuario("CC", "1000000002", "Maria", "Isabel", "Rodriguez", "Gomez",
                "propositocorreo@gmail.com", passwordHash, Rol.ENCARGADO);
        Usuario enc2 = crearUsuario("CC", "1000000003", "Pedro", "Luis", "Martinez", "Lopez",
                "patriciacc2074@gmail.com", passwordHash, Rol.ENCARGADO);
        Usuario enc3 = crearUsuario("CC", "1000000004", "Ana", "Patricia", "Garcia", "Diaz",
                "encargado.sancristobal@ciempies.com", passwordHash, Rol.ENCARGADO);
        Usuario enc4 = crearUsuario("CC", "1000000005", "Carlos", "Eduardo", "Hernandez", "Silva",
                "encargado.kennedy@ciempies.com", passwordHash, Rol.ENCARGADO);
        Usuario enc5 = crearUsuario("CC", "1000000006", "Laura", "Andrea", "Ramirez", "Castro",
                "encargado.bosa@ciempies.com", passwordHash, Rol.ENCARGADO);
        Usuario enc6 = crearUsuario("CC", "1000000007", "Jorge", "Enrique", "Vargas", "Torres",
                "encargado.bolivar@ciempies.com", passwordHash, Rol.ENCARGADO);

        // MONITORES (2 por zona = 12 monitores)
        Usuario mon1 = crearUsuario("CC", "1000000010", "Carlos", "Andres", "Monitor", "Perez",
                "cmramireza29@gmail.com", passwordHash, Rol.MONITOR);
        Usuario mon2 = crearUsuario("CC", "1000000011", "Ana", "Maria", "Supervisora", "Garcia",
                "santiagorioscajamarca@gmail.com", passwordHash, Rol.MONITOR);
        Usuario mon3 = crearUsuario("CC", "1000000012", "Luis", "Fernando", "Acompanante", "Diaz",
                "kritho0311@gmail.com", passwordHash, Rol.MONITOR);
        Usuario mon4 = crearUsuario("CC", "1000000013", "Diana", "Carolina", "Ruiz", "Moreno",
                "alarconpaula992@gmail.com", passwordHash, Rol.MONITOR);
        Usuario mon5 = crearUsuario("CC", "1000000014", "Ricardo", "Alberto", "Castro", "Lopez",
                "monitor.sancristobal1@ciempies.com", passwordHash, Rol.MONITOR);
        Usuario mon6 = crearUsuario("CC", "1000000015", "Sandra", "Milena", "Gomez", "Martinez",
                "monitor.sancristobal2@ciempies.com", passwordHash, Rol.MONITOR);
        Usuario mon7 = crearUsuario("CC", "1000000016", "Miguel", "Angel", "Torres", "Ramirez",
                "monitor.kennedy1@ciempies.com", passwordHash, Rol.MONITOR);
        Usuario mon8 = crearUsuario("CC", "1000000017", "Patricia", "Elena", "Mendez", "Silva",
                "monitor.kennedy2@ciempies.com", passwordHash, Rol.MONITOR);
        Usuario mon9 = crearUsuario("CC", "1000000018", "Andres", "Felipe", "Herrera", "Gutierrez",
                "monitor.bosa1@ciempies.com", passwordHash, Rol.MONITOR);
        Usuario mon10 = crearUsuario("CC", "1000000019", "Monica", "Alejandra", "Jimenez", "Ortiz",
                "monitor.bosa2@ciempies.com", passwordHash, Rol.MONITOR);
        Usuario mon11 = crearUsuario("CC", "1000000020", "Javier", "Eduardo", "Morales", "Castro",
                "monitor.bolivar1@ciempies.com", passwordHash, Rol.MONITOR);
        Usuario mon12 = crearUsuario("CC", "1000000021", "Claudia", "Marcela", "Rojas", "Vargas",
                "monitor.bolivar2@ciempies.com", passwordHash, Rol.MONITOR);

        // ==========================================
        // 4. COLEGIOS (3 por zona = 18 colegios)
        // ==========================================
        // Usaquen
        Colegio col1 = crearColegio("Colegio Distrital Usaquen", zona1);
        Colegio col2 = crearColegio("Instituto Santa Barbara", zona1);
        Colegio col3 = crearColegio("Liceo Boston", zona1);

        // Santa Fe
        Colegio col4 = crearColegio("Colegio Agustin Nieto Caballero", zona2);
        Colegio col5 = crearColegio("Instituto Francisco Jose de Caldas", zona2);
        Colegio col6 = crearColegio("Colegio San Martin de Porres", zona2);

        // San Cristobal
        Colegio col7 = crearColegio("Colegio San Cristobal Sur", zona3);
        Colegio col8 = crearColegio("Instituto Juan del Corral", zona3);
        Colegio col9 = crearColegio("Colegio Ramon de Zubiria", zona3);

        // Kennedy
        Colegio col10 = crearColegio("Colegio Kennedy", zona4);
        Colegio col11 = crearColegio("Instituto Tecnico Industrial", zona4);
        Colegio col12 = crearColegio("Colegio Castilla", zona4);

        // Bosa
        Colegio col13 = crearColegio("Colegio Integrado de Fontibon", zona5);
        Colegio col14 = crearColegio("Instituto San Bernardino", zona5);
        Colegio col15 = crearColegio("Colegio Paulo VI", zona5);

        // Ciudad Bolivar
        Colegio col16 = crearColegio("Colegio Arborizadora Alta", zona6);
        Colegio col17 = crearColegio("Instituto Tecnico Distrital", zona6);
        Colegio col18 = crearColegio("Colegio Ciudad Bolivar", zona6);

        // ==========================================
        // 5. ASIGNACION COLEGIOS - JORNADAS
        // ==========================================
        // USAQUEN
        crearColegioJornada(col1, j1);  // Distrital Usaquen - Mañana
        crearColegioJornada(col1, j3);  // Distrital Usaquen - Tarde
        crearColegioJornada(col2, j1);  // Santa Barbara - Mañana
        crearColegioJornada(col2, j3);  // Santa Barbara - Tarde
        crearColegioJornada(col3, j2);  // Liceo Boston - Única

        // SANTA FE
        crearColegioJornada(col4, j4);  // Agustin Nieto - Mañana
        crearColegioJornada(col4, j6);  // Agustin Nieto - Tarde
        crearColegioJornada(col5, j4);  // Caldas - Mañana
        crearColegioJornada(col5, j6);  // Caldas - Tarde
        crearColegioJornada(col6, j5);  // San Martin - Única

        // SAN CRISTOBAL
        crearColegioJornada(col7, j7);  // San Cristobal Sur - Mañana
        crearColegioJornada(col7, j9);  // San Cristobal Sur - Tarde
        crearColegioJornada(col8, j7);  // Juan del Corral - Mañana
        crearColegioJornada(col8, j9);  // Juan del Corral - Tarde
        crearColegioJornada(col9, j8);  // Ramon de Zubiria - Única

        // KENNEDY
        crearColegioJornada(col10, j10); // Kennedy - Mañana
        crearColegioJornada(col10, j12); // Kennedy - Tarde
        crearColegioJornada(col11, j10); // Tecnico Industrial - Mañana
        crearColegioJornada(col11, j12); // Tecnico Industrial - Tarde
        crearColegioJornada(col12, j11); // Castilla - Única

        // BOSA
        crearColegioJornada(col13, j13); // Integrado Fontibon - Mañana
        crearColegioJornada(col13, j15); // Integrado Fontibon - Tarde
        crearColegioJornada(col14, j13); // San Bernardino - Mañana
        crearColegioJornada(col14, j15); // San Bernardino - Tarde
        crearColegioJornada(col15, j14); // Paulo VI - Única

        // CIUDAD BOLIVAR
        crearColegioJornada(col16, j16); // Arborizadora Alta - Mañana
        crearColegioJornada(col16, j18); // Arborizadora Alta - Tarde
        crearColegioJornada(col17, j16); // Tecnico Distrital - Mañana
        crearColegioJornada(col17, j18); // Tecnico Distrital - Tarde
        crearColegioJornada(col18, j17); // Ciudad Bolivar - Única

        // ==========================================
        // 6. RUTAS (60 rutas: IDA y REGRESO para cada colegio-jornada)
        // ==========================================
        // USAQUEN
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

        // SANTA FE
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

        // SAN CRISTOBAL
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

        // KENNEDY
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

        // BOSA
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

        // CIUDAD BOLIVAR
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
        // 7. MONITORES (2 por zona = 12 monitores)
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

        // ==========================================
        // 8. ESTUDIANTES (30-45 por colegio-jornada)
        // ==========================================
        int estudianteId = 1100000001;

        // USAQUEN
        estudianteId = crearEstudiantesLote(estudianteId, col1, j1, r1, r2, 40, zona1);
        estudianteId = crearEstudiantesLote(estudianteId, col1, j3, r3, r4, 35, zona1);
        estudianteId = crearEstudiantesLote(estudianteId, col2, j1, r5, r6, 38, zona1);
        estudianteId = crearEstudiantesLote(estudianteId, col2, j3, r7, r8, 32, zona1);
        estudianteId = crearEstudiantesLote(estudianteId, col3, j2, r9, r10, 45, zona1);

        // SANTA FE
        estudianteId = crearEstudiantesLote(estudianteId, col4, j4, r11, r12, 42, zona2);
        estudianteId = crearEstudiantesLote(estudianteId, col4, j6, r13, r14, 38, zona2);
        estudianteId = crearEstudiantesLote(estudianteId, col5, j4, r15, r16, 35, zona2);
        estudianteId = crearEstudiantesLote(estudianteId, col5, j6, r17, r18, 40, zona2);
        estudianteId = crearEstudiantesLote(estudianteId, col6, j5, r19, r20, 43, zona2);

        // SAN CRISTOBAL
        estudianteId = crearEstudiantesLote(estudianteId, col7, j7, r21, r22, 41, zona3);
        estudianteId = crearEstudiantesLote(estudianteId, col7, j9, r23, r24, 36, zona3);
        estudianteId = crearEstudiantesLote(estudianteId, col8, j7, r25, r26, 39, zona3);
        estudianteId = crearEstudiantesLote(estudianteId, col8, j9, r27, r28, 34, zona3);
        estudianteId = crearEstudiantesLote(estudianteId, col9, j8, r29, r30, 44, zona3);

        // KENNEDY
        estudianteId = crearEstudiantesLote(estudianteId, col10, j10, r31, r32, 40, zona4);
        estudianteId = crearEstudiantesLote(estudianteId, col10, j12, r33, r34, 37, zona4);
        estudianteId = crearEstudiantesLote(estudianteId, col11, j10, r35, r36, 36, zona4);
        estudianteId = crearEstudiantesLote(estudianteId, col11, j12, r37, r38, 41, zona4);
        estudianteId = crearEstudiantesLote(estudianteId, col12, j11, r39, r40, 42, zona4);

        // BOSA
        estudianteId = crearEstudiantesLote(estudianteId, col13, j13, r41, r42, 38, zona5);
        estudianteId = crearEstudiantesLote(estudianteId, col13, j15, r43, r44, 33, zona5);
        estudianteId = crearEstudiantesLote(estudianteId, col14, j13, r45, r46, 40, zona5);
        estudianteId = crearEstudiantesLote(estudianteId, col14, j15, r47, r48, 35, zona5);
        estudianteId = crearEstudiantesLote(estudianteId, col15, j14, r49, r50, 45, zona5);

        // CIUDAD BOLIVAR
        estudianteId = crearEstudiantesLote(estudianteId, col16, j16, r51, r52, 39, zona6);
        estudianteId = crearEstudiantesLote(estudianteId, col16, j18, r53, r54, 34, zona6);
        estudianteId = crearEstudiantesLote(estudianteId, col17, j16, r55, r56, 37, zona6);
        estudianteId = crearEstudiantesLote(estudianteId, col17, j18, r57, r58, 40, zona6);
        estudianteId = crearEstudiantesLote(estudianteId, col18, j17, r59, r60, 43, zona6);

        int totalEstudiantes = estudianteId - 1100000001;

        System.out.println("======================================================");
        System.out.println("✅ DATOS INICIALES CARGADOS CORRECTAMENTE");
        System.out.println("======================================================");
        System.out.println("   ✓ 6 Zonas");
        System.out.println("   ✓ 18 Jornadas (3 por zona)");
        System.out.println("   ✓ 19 Usuarios (1 Admin + 6 Encargados + 12 Monitores)");
        System.out.println("   ✓ 18 Colegios (3 por zona)");
        System.out.println("   ✓ 36 Asignaciones colegio-jornada");
        System.out.println("   ✓ 60 Rutas (IDA y REGRESO para cada colegio-jornada)");
        System.out.println("   ✓ 12 Monitores asignados (2 por zona)");
        System.out.println("   ✓ " + totalEstudiantes + " Estudiantes (30-45 por colegio-jornada)");
        System.out.println("");
        System.out.println("📊 DISTRIBUCIÓN DE ESTUDIANTES:");
        System.out.println("   • Colegios con doble jornada: ~70-80 estudiantes");
        System.out.println("   • Colegios con jornada única: ~40-45 estudiantes");
        System.out.println("");
        System.out.println("🔐 CREDENCIALES DE ACCESO:");
        System.out.println("   Email: admin@ciempies.com");
        System.out.println("   Contraseña: admin123");
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
                                 String passwordHash, Rol rol) {
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
        usuario.setFechaCreacion(LocalDateTime.now());
        return usuarioRepository.save(usuario);
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

    // ==========================================
    // MÉTODO PARA CREAR ESTUDIANTES EN LOTE
    // ==========================================
    private int crearEstudiantesLote(int inicioId, Colegio colegio, Jornada jornada,
                                     Ruta rutaIda, Ruta rutaRegreso, int cantidad, Zona zona) {

        String[] nombresM = {"Juan", "Carlos", "Andrés", "Luis", "Diego", "Santiago", "Sebastián",
                "Mateo", "Nicolás", "Daniel", "David", "Miguel", "Alejandro", "Felipe",
                "Javier", "Ricardo", "Pablo", "Jorge", "Manuel", "Camilo"};

        String[] nombresF = {"María", "Laura", "Ana", "Camila", "Valentina", "Sofia", "Isabella",
                "Mariana", "Paula", "Andrea", "Carolina", "Natalia", "Daniela", "Sara",
                "Juliana", "Gabriela", "Manuela", "Valeria", "Alejandra", "Catalina"};

        String[] apellidos = {"García", "Rodríguez", "Martínez", "López", "González", "Pérez",
                "Sánchez", "Ramírez", "Torres", "Flores", "Rivera", "Gómez", "Díaz",
                "Cruz", "Morales", "Herrera", "Jiménez", "Álvarez", "Romero", "Vargas",
                "Castro", "Ortiz", "Silva", "Mendoza", "Rojas", "Gutiérrez", "Moreno",
                "Ruiz", "Vásquez", "Molina"};

        String[] cursos = {"5A", "5B", "6A", "6B", "7A", "7B", "8A", "8B", "9A", "9B"};
        String[] eps = {"Nueva EPS", "Sanitas", "Sura", "Compensar", "Famisanar", "Salud Total"};

        String[] direccionesBase = {"Calle", "Carrera", "Avenida", "Diagonal", "Transversal"};

        for (int i = 0; i < cantidad; i++) {
            boolean esMasculino = (i % 2 == 0);
            Sexo sexo = esMasculino ? Sexo.MASCULINO : Sexo.FEMENINO;

            // Seleccionar nombres
            String primerNombre = esMasculino ?
                    nombresM[i % nombresM.length] :
                    nombresF[i % nombresF.length];
            String segundoNombre = esMasculino ?
                    nombresM[(i + 3) % nombresM.length] :
                    nombresF[(i + 3) % nombresF.length];

            String primerApellido = apellidos[i % apellidos.length];
            String segundoApellido = apellidos[(i + 7) % apellidos.length];

            // Número de identificación
            String numId = String.valueOf(inicioId + i);

            // Fecha de nacimiento (entre 2013 y 2015)
            int año = 2013 + (i % 3);
            int mes = 1 + (i % 12);
            int dia = 1 + (i % 28);
            String fechaNacimiento = String.format("%d-%02d-%02d", año, mes, dia);

            // Dirección
            int numCalle = 10 + (i * 3 % 150);
            int numCarrera = 5 + (i * 2 % 30);
            String direccion = String.format("%s %d #%d-%d",
                    direccionesBase[i % direccionesBase.length],
                    numCalle, numCarrera, 10 + (i % 90));

            // Curso y EPS
            String curso = cursos[i % cursos.length];
            String epsSeleccionada = eps[i % eps.length];

            // Información del acudiente
            String nombreAcudiente = apellidos[(i + 5) % apellidos.length] + " " +
                    (esMasculino ? nombresF[i % nombresF.length] : nombresM[i % nombresM.length]);
            String telefonoAcudiente = "300" + String.format("%07d", 1000000 + i);
            String emailAcudiente = primerNombre.toLowerCase() + "." +
                    primerApellido.toLowerCase() + i + "@example.com";

            // Alternar entre ruta de IDA y REGRESO
            Ruta ruta = (i % 2 == 0) ? rutaIda : rutaRegreso;

            crearEstudiante("TI", numId, primerNombre, segundoNombre, primerApellido, segundoApellido,
                    fechaNacimiento, sexo, direccion, curso, epsSeleccionada, nombreAcudiente,
                    telefonoAcudiente, emailAcudiente, colegio, jornada, ruta);
        }

        return inicioId + cantidad;
    }
}