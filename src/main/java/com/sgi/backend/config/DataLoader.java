package com.sgi.backend.config;

import com.sgi.backend.model.*;
import com.sgi.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
                "encargado.usaquen@ciempies.com", passwordHash, Rol.ENCARGADO);
        Usuario enc2 = crearUsuario("CC", "1000000003", "Pedro", "Luis", "Martinez", "Lopez",
                "encargado.santafe@ciempies.com", passwordHash, Rol.ENCARGADO);
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
        // 4. COLEGIOS (3 por localidad = 18 colegios)
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
        // Usaquen
        crearColegioJornada(col1, j1);
        crearColegioJornada(col1, j3);
        crearColegioJornada(col2, j1);
        crearColegioJornada(col3, j2);

        // Santa Fe
        crearColegioJornada(col4, j4);
        crearColegioJornada(col4, j6);
        crearColegioJornada(col5, j6);
        crearColegioJornada(col6, j5);

        // San Cristobal
        crearColegioJornada(col7, j7);
        crearColegioJornada(col7, j9);
        crearColegioJornada(col8, j7);
        crearColegioJornada(col9, j8);

        // Kennedy
        crearColegioJornada(col10, j10);
        crearColegioJornada(col10, j12);
        crearColegioJornada(col11, j12);
        crearColegioJornada(col12, j11);

        // Bosa
        crearColegioJornada(col13, j13);
        crearColegioJornada(col13, j15);
        crearColegioJornada(col14, j13);
        crearColegioJornada(col15, j14);

        // Ciudad Bolivar
        crearColegioJornada(col16, j16);
        crearColegioJornada(col16, j18);
        crearColegioJornada(col17, j18);
        crearColegioJornada(col18, j17);

        // ==========================================
        // 6. RUTAS
        // ==========================================
        // Usaquen - Colegio Distrital Usaquen (JM y JT)
        Ruta r1 = crearRuta("Usaquen Distrital - Manana IDA", TipoRecorrido.IDA, zona1);
        Ruta r2 = crearRuta("Usaquen Distrital - Manana REGRESO", TipoRecorrido.REGRESO, zona1);
        Ruta r3 = crearRuta("Usaquen Distrital - Tarde IDA", TipoRecorrido.IDA, zona1);
        Ruta r4 = crearRuta("Usaquen Distrital - Tarde REGRESO", TipoRecorrido.REGRESO, zona1);

        // Usaquen - Instituto Santa Barbara (Solo JM)
        Ruta r5 = crearRuta("Santa Barbara - Manana IDA", TipoRecorrido.IDA, zona1);
        Ruta r6 = crearRuta("Santa Barbara - Manana REGRESO", TipoRecorrido.REGRESO, zona1);

        // Usaquen - Liceo Boston (JU)
        Ruta r7 = crearRuta("Liceo Boston - Unica IDA", TipoRecorrido.IDA, zona1);
        Ruta r8 = crearRuta("Liceo Boston - Unica REGRESO", TipoRecorrido.REGRESO, zona1);

        // Santa Fe - Colegio Agustin Nieto Caballero (JM y JT)
        Ruta r9 = crearRuta("Agustin Nieto - Manana IDA", TipoRecorrido.IDA, zona2);
        Ruta r10 = crearRuta("Agustin Nieto - Manana REGRESO", TipoRecorrido.REGRESO, zona2);
        Ruta r11 = crearRuta("Agustin Nieto - Tarde IDA", TipoRecorrido.IDA, zona2);
        Ruta r12 = crearRuta("Agustin Nieto - Tarde REGRESO", TipoRecorrido.REGRESO, zona2);

        // Santa Fe - Instituto Caldas (Solo JT)
        Ruta r13 = crearRuta("Caldas - Tarde IDA", TipoRecorrido.IDA, zona2);
        Ruta r14 = crearRuta("Caldas - Tarde REGRESO", TipoRecorrido.REGRESO, zona2);

        // Santa Fe - Colegio San Martin (JU)
        Ruta r15 = crearRuta("San Martin - Unica IDA", TipoRecorrido.IDA, zona2);
        Ruta r16 = crearRuta("San Martin - Unica REGRESO", TipoRecorrido.REGRESO, zona2);

        // San Cristobal - Colegio San Cristobal Sur (JM y JT)
        Ruta r17 = crearRuta("San Cristobal Sur - Manana IDA", TipoRecorrido.IDA, zona3);
        Ruta r18 = crearRuta("San Cristobal Sur - Manana REGRESO", TipoRecorrido.REGRESO, zona3);
        Ruta r19 = crearRuta("San Cristobal Sur - Tarde IDA", TipoRecorrido.IDA, zona3);
        Ruta r20 = crearRuta("San Cristobal Sur - Tarde REGRESO", TipoRecorrido.REGRESO, zona3);

        // San Cristobal - Instituto Juan del Corral (Solo JM)
        Ruta r21 = crearRuta("Juan del Corral - Manana IDA", TipoRecorrido.IDA, zona3);
        Ruta r22 = crearRuta("Juan del Corral - Manana REGRESO", TipoRecorrido.REGRESO, zona3);

        // San Cristobal - Colegio Ramon de Zubiria (JU)
        Ruta r23 = crearRuta("Ramon de Zubiria - Unica IDA", TipoRecorrido.IDA, zona3);
        Ruta r24 = crearRuta("Ramon de Zubiria - Unica REGRESO", TipoRecorrido.REGRESO, zona3);

        // Kennedy - Colegio Kennedy (JM y JT)
        Ruta r25 = crearRuta("Kennedy - Manana IDA", TipoRecorrido.IDA, zona4);
        Ruta r26 = crearRuta("Kennedy - Manana REGRESO", TipoRecorrido.REGRESO, zona4);
        Ruta r27 = crearRuta("Kennedy - Tarde IDA", TipoRecorrido.IDA, zona4);
        Ruta r28 = crearRuta("Kennedy - Tarde REGRESO", TipoRecorrido.REGRESO, zona4);

        // Kennedy - Instituto Tecnico Industrial (Solo JT)
        Ruta r29 = crearRuta("Tecnico Kennedy - Tarde IDA", TipoRecorrido.IDA, zona4);
        Ruta r30 = crearRuta("Tecnico Kennedy - Tarde REGRESO", TipoRecorrido.REGRESO, zona4);

        // Kennedy - Colegio Castilla (JU)
        Ruta r31 = crearRuta("Castilla - Unica IDA", TipoRecorrido.IDA, zona4);
        Ruta r32 = crearRuta("Castilla - Unica REGRESO", TipoRecorrido.REGRESO, zona4);

        // Bosa - Colegio Integrado de Fontibon (JM y JT)
        Ruta r33 = crearRuta("Integrado Fontibon - Manana IDA", TipoRecorrido.IDA, zona5);
        Ruta r34 = crearRuta("Integrado Fontibon - Manana REGRESO", TipoRecorrido.REGRESO, zona5);
        Ruta r35 = crearRuta("Integrado Fontibon - Tarde IDA", TipoRecorrido.IDA, zona5);
        Ruta r36 = crearRuta("Integrado Fontibon - Tarde REGRESO", TipoRecorrido.REGRESO, zona5);

        // Bosa - Instituto San Bernardino (Solo JM)
        Ruta r37 = crearRuta("San Bernardino - Manana IDA", TipoRecorrido.IDA, zona5);
        Ruta r38 = crearRuta("San Bernardino - Manana REGRESO", TipoRecorrido.REGRESO, zona5);

        // Bosa - Colegio Paulo VI (JU)
        Ruta r39 = crearRuta("Paulo VI - Unica IDA", TipoRecorrido.IDA, zona5);
        Ruta r40 = crearRuta("Paulo VI - Unica REGRESO", TipoRecorrido.REGRESO, zona5);

        // Ciudad Bolivar - Colegio Arborizadora Alta (JM y JT)
        Ruta r41 = crearRuta("Arborizadora Alta - Manana IDA", TipoRecorrido.IDA, zona6);
        Ruta r42 = crearRuta("Arborizadora Alta - Manana REGRESO", TipoRecorrido.REGRESO, zona6);
        Ruta r43 = crearRuta("Arborizadora Alta - Tarde IDA", TipoRecorrido.IDA, zona6);
        Ruta r44 = crearRuta("Arborizadora Alta - Tarde REGRESO", TipoRecorrido.REGRESO, zona6);

        // Ciudad Bolivar - Instituto Tecnico Distrital (Solo JT)
        Ruta r45 = crearRuta("Tecnico Bolivar - Tarde IDA", TipoRecorrido.IDA, zona6);
        Ruta r46 = crearRuta("Tecnico Bolivar - Tarde REGRESO", TipoRecorrido.REGRESO, zona6);

        // Ciudad Bolivar - Colegio Ciudad Bolivar (JU)
        Ruta r47 = crearRuta("Ciudad Bolivar - Unica IDA", TipoRecorrido.IDA, zona6);
        Ruta r48 = crearRuta("Ciudad Bolivar - Unica REGRESO", TipoRecorrido.REGRESO, zona6);

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

        // ==========================================
        // 8. ESTUDIANTES (3 por zona = 18 estudiantes)
        // ==========================================
        // Usaquen
        crearEstudiante("TI", "1100000001", "Juan", "Pablo", "Rodriguez", "Gomez", "2014-05-15", Sexo.MASCULINO,
                "Calle 170 #15-20", "5A", "Nueva EPS", "Maria Rodriguez", "3001234567",
                "maria.rodriguez@example.com", col1, j1, r1);

        crearEstudiante("TI", "1100000002", "Maria", "Fernanda", "Martinez", "Lopez", "2014-08-20", Sexo.FEMENINO,
                "Carrera 7 #145-40", "5B", "Sanitas", "Pedro Martinez", "3009876543",
                "pedro.martinez@example.com", col2, j1, r5);

        crearEstudiante("TI", "1100000003", "Carlos", "Andres", "Garcia", "Perez", "2013-03-10", Sexo.MASCULINO,
                "Calle 165 #20-30", "6A", "Sura", "Ana Garcia", "3005551234",
                "ana.garcia@example.com", col3, j2, r7);

        // Santa Fe
        crearEstudiante("TI", "1100000004", "Laura", "Valentina", "Ramirez", "Castro", "2014-11-25", Sexo.FEMENINO,
                "Carrera 5 #25-35", "5A", "Nueva EPS", "Carlos Ramirez", "3001112233",
                "carlos.ramirez@example.com", col4, j4, r9);

        crearEstudiante("TI", "1100000005", "Diego", "Alejandro", "Vargas", "Moreno", "2013-07-18", Sexo.MASCULINO,
                "Calle 19 #3-50", "6B", "Sanitas", "Sandra Vargas", "3002223344",
                "sandra.vargas@example.com", col5, j6, r13);

        crearEstudiante("TI", "1100000006", "Sara", "Isabella", "Mendez", "Ortiz", "2014-02-14", Sexo.FEMENINO,
                "Carrera 10 #15-20", "5A", "Sura", "Luis Mendez", "3003334455",
                "luis.mendez@example.com", col6, j5, r15);

        // San Cristobal
        crearEstudiante("TI", "1100000007", "Andres", "Felipe", "Jimenez", "Rojas", "2013-09-22", Sexo.MASCULINO,
                "Calle 1 Sur #18-25", "6A", "Nueva EPS", "Patricia Jimenez", "3004445566",
                "patricia.jimenez@example.com", col7, j7, r17);

        crearEstudiante("TI", "1100000008", "Camila", "Andrea", "Morales", "Gutierrez", "2014-06-30", Sexo.FEMENINO,
                "Carrera 5 Este #5-30", "5B", "Sanitas", "Jorge Morales", "3005556677",
                "jorge.morales@example.com", col8, j7, r21);

        crearEstudiante("TI", "1100000009", "Sebastian", "David", "Castro", "Herrera", "2013-12-05", Sexo.MASCULINO,
                "Calle 8 Sur #10-35", "6B", "Sura", "Monica Castro", "3006667788",
                "monica.castro@example.com", col9, j8, r23);

        // Kennedy
        crearEstudiante("TI", "1100000010", "Valentina", "Sofia", "Torres", "Silva", "2014-04-17", Sexo.FEMENINO,
                "Carrera 78 #40-50", "5A", "Nueva EPS", "Ricardo Torres", "3007778899",
                "ricardo.torres@example.com", col10, j10, r25);

        crearEstudiante("TI", "1100000011", "Daniel", "Santiago", "Lopez", "Gomez", "2013-10-08", Sexo.MASCULINO,
                "Calle 38 Sur #80-20", "6A", "Sanitas", "Andrea Lopez", "3008889900",
                "andrea.lopez@example.com", col11, j12, r29);

        crearEstudiante("TI", "1100000012", "Isabella", "Marcela", "Sanchez", "Rojas", "2014-01-20", Sexo.FEMENINO,
                "Carrera 86 #42-30", "5B", "Sura", "Felipe Sanchez", "3009990011",
                "felipe.sanchez@example.com", col12, j11, r31);

        // Bosa
        crearEstudiante("TI", "1100000013", "Nicolas", "Alejandro", "Ruiz", "Martinez", "2013-05-12", Sexo.MASCULINO,
                "Carrera 98 #60-25", "6A", "Nueva EPS", "Carolina Ruiz", "3001011122",
                "carolina.ruiz@example.com", col13, j13, r33);

        crearEstudiante("TI", "1100000014", "Mariana", "Alejandra", "Diaz", "Castro", "2014-09-03", Sexo.FEMENINO,
                "Calle 57 Sur #89-40", "5A", "Sanitas", "Miguel Diaz", "3002022233",
                "miguel.diaz@example.com", col14, j13, r37);

        crearEstudiante("TI", "1100000015", "Santiago", "Esteban", "Gutierrez", "Silva", "2013-11-28", Sexo.MASCULINO,
                "Carrera 92 #65-15", "6B", "Sura", "Gloria Gutierrez", "3003033344",
                "gloria.gutierrez@example.com", col15, j14, r39);

        // Ciudad Bolivar
        crearEstudiante("TI", "1100000016", "Sofia", "Camila", "Herrera", "Moreno", "2014-07-22", Sexo.FEMENINO,
                "Calle 65 Sur #10-20", "5A", "Nueva EPS", "Javier Herrera", "3004044455",
                "javier.herrera@example.com", col16, j16, r41);

        crearEstudiante("TI", "1100000017", "Mateo", "Andres", "Ospina", "Ramirez", "2013-04-05", Sexo.MASCULINO,
                "Carrera 24 Sur #68-30", "6A", "Sanitas", "Paola Ospina", "3005055566",
                "paola.ospina@example.com", col17, j18, r45);

        crearEstudiante("TI", "1100000018", "Emma", "Valentina", "Molina", "Torres", "2014-12-15", Sexo.FEMENINO,
                "Calle 70 Sur #15-40", "5B", "Sura", "German Molina", "3006066677",
                "german.molina@example.com", col18, j17, r47);

        System.out.println("======================================================");
        System.out.println("✅ DATOS INICIALES CARGADOS CORRECTAMENTE");
        System.out.println("======================================================");
        System.out.println("   ✓ 6 Zonas");
        System.out.println("   ✓ 18 Jornadas");
        System.out.println("   ✓ 19 Usuarios (1 Admin + 6 Encargados + 12 Monitores)");
        System.out.println("   ✓ 18 Colegios");
        System.out.println("   ✓ 24 Asignaciones colegio-jornada");
        System.out.println("   ✓ 48 Rutas");
        System.out.println("   ✓ 12 Monitores asignados");
        System.out.println("   ✓ 18 Estudiantes");
        System.out.println("");
        System.out.println("🔐 CREDENCIALES DE ACCESO:");
        System.out.println("   Email: admin@ciempies.com");
        System.out.println("   Contrasena: admin123");
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
}