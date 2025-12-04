-- ==========================================
-- CARGA INICIAL DE DATOS - SISTEMA CIEMPIÉS
-- BOGOTÁ - 6 LOCALIDADES ACTIVAS
-- ==========================================

-- ==========================================
-- CONTRASEÑAS PARA TODOS LOS USUARIOS: "admin123"
-- Hash BCrypt: $2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K
-- ==========================================

-- ==========================================
-- 1. ZONAS (6 LOCALIDADES SELECCIONADAS)
-- ==========================================
INSERT INTO zonas (codigo_zona, nombre_zona, descripcion, activa) VALUES
('ZONA 1', 'Usaquén', 'Norte', true),
('ZONA 2', 'Santa Fe', 'Centro-Oriente', true),
('ZONA 3', 'San Cristóbal', 'Sur-Oriente', true),
('ZONA 4', 'Kennedy', 'Sur-Occidente', true),
('ZONA 5', 'Bosa', 'Sur-Occidente', true),
('ZONA 6', 'Ciudad Bolívar', 'Sur', true);

-- ==========================================
-- 2. JORNADAS (3 por cada localidad: MAÑANA, ÚNICA, TARDE = 18 jornadas)
-- ==========================================
INSERT INTO jornadas (codigo_jornada, nombre_jornada, fk_zona, activa) VALUES
-- Usaquén
('J001', 'MANANA', 1, true),
('J002', 'UNICA', 1, true),
('J003', 'TARDE', 1, true),
-- Santa Fe
('J004', 'MANANA', 2, true),
('J005', 'UNICA', 2, true),
('J006', 'TARDE', 2, true),
-- San Cristóbal
('J007', 'MANANA', 3, true),
('J008', 'UNICA', 3, true),
('J009', 'TARDE', 3, true),
-- Kennedy
('J010', 'MANANA', 4, true),
('J011', 'UNICA', 4, true),
('J012', 'TARDE', 4, true),
-- Bosa
('J013', 'MANANA', 5, true),
('J014', 'UNICA', 5, true),
('J015', 'TARDE', 5, true),
-- Ciudad Bolívar
('J016', 'MANANA', 6, true),
('J017', 'UNICA', 6, true),
('J018', 'TARDE', 6, true);

-- ==========================================
-- 3. USUARIOS
-- ==========================================
INSERT INTO usuarios (tipo_id, num_id, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, email, contrasena, rol, activo, fecha_creacion) VALUES
-- ADMINISTRADOR
('CC', '1000000001', 'Juan', 'Carlos', 'Administrador', 'Sistema', 'admin@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'ADMINISTRADOR', true, NOW()),

-- ENCARGADOS (uno por zona)
('CC', '1000000002', 'Maria', 'Isabel', 'Rodriguez', 'Gomez', 'encargado.usaquen@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'ENCARGADO', true, NOW()),
('CC', '1000000003', 'Pedro', 'Luis', 'Martinez', 'Lopez', 'encargado.santafe@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'ENCARGADO', true, NOW()),
('CC', '1000000004', 'Ana', 'Patricia', 'Garcia', 'Diaz', 'encargado.sancristobal@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'ENCARGADO', true, NOW()),
('CC', '1000000005', 'Carlos', 'Eduardo', 'Hernandez', 'Silva', 'encargado.kennedy@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'ENCARGADO', true, NOW()),
('CC', '1000000006', 'Laura', 'Andrea', 'Ramirez', 'Castro', 'encargado.bosa@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'ENCARGADO', true, NOW()),
('CC', '1000000007', 'Jorge', 'Enrique', 'Vargas', 'Torres', 'encargado.bolivar@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'ENCARGADO', true, NOW()),

-- MONITORES (2 por zona = 12 monitores)
('CC', '1000000010', 'Carlos', 'Andres', 'Monitor', 'Perez', 'monitor.usaquen1@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000011', 'Ana', 'Maria', 'Supervisora', 'Garcia', 'monitor.usaquen2@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000012', 'Luis', 'Fernando', 'Acompañante', 'Diaz', 'monitor.santafe1@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000013', 'Diana', 'Carolina', 'Ruiz', 'Moreno', 'monitor.santafe2@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000014', 'Ricardo', 'Alberto', 'Castro', 'Lopez', 'monitor.sancristobal1@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000015', 'Sandra', 'Milena', 'Gomez', 'Martinez', 'monitor.sancristobal2@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000016', 'Miguel', 'Angel', 'Torres', 'Ramirez', 'monitor.kennedy1@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000017', 'Patricia', 'Elena', 'Mendez', 'Silva', 'monitor.kennedy2@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000018', 'Andres', 'Felipe', 'Herrera', 'Gutierrez', 'monitor.bosa1@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000019', 'Monica', 'Alejandra', 'Jimenez', 'Ortiz', 'monitor.bosa2@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000020', 'Javier', 'Eduardo', 'Morales', 'Castro', 'monitor.bolivar1@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW()),
('CC', '1000000021', 'Claudia', 'Marcela', 'Rojas', 'Vargas', 'monitor.bolivar2@ciempies.com', '$2a$10$N9qo8uLOickgx2ZMRZoMye5V6Y2A0pNJjZmWWfgZQvhxCXJJJfp1K', 'MONITOR', true, NOW());

-- ==========================================
-- 4. COLEGIOS (3 por localidad = 18 colegios)
-- ==========================================
INSERT INTO colegios (nombre_colegio, fk_zona, activo) VALUES
-- Usaquén (3 colegios)
('Colegio Distrital Usaquén', 1, true),           -- ID 1: JM y JT
('Instituto Santa Bárbara', 1, true),             -- ID 2: Solo JM
('Liceo Boston', 1, true),                        -- ID 3: JU

-- Santa Fe (3 colegios)
('Colegio Agustín Nieto Caballero', 2, true),    -- ID 4: JM y JT
('Instituto Francisco José de Caldas', 2, true), -- ID 5: Solo JT
('Colegio San Martín de Porres', 2, true),       -- ID 6: JU

-- San Cristóbal (3 colegios)
('Colegio San Cristóbal Sur', 3, true),          -- ID 7: JM y JT
('Instituto Juan del Corral', 3, true),          -- ID 8: Solo JM
('Colegio Ramón de Zubiría', 3, true),           -- ID 9: JU

-- Kennedy (3 colegios)
('Colegio Kennedy', 4, true),                    -- ID 10: JM y JT
('Instituto Técnico Industrial', 4, true),       -- ID 11: Solo JT
('Colegio Castilla', 4, true),                   -- ID 12: JU

-- Bosa (3 colegios)
('Colegio Integrado de Fontibón', 5, true),      -- ID 13: JM y JT
('Instituto San Bernardino', 5, true),           -- ID 14: Solo JM
('Colegio Paulo VI', 5, true),                   -- ID 15: JU

-- Ciudad Bolívar (3 colegios)
('Colegio Arborizadora Alta', 6, true),          -- ID 16: JM y JT
('Instituto Técnico Distrital', 6, true),        -- ID 17: Solo JT
('Colegio Ciudad Bolívar', 6, true);             -- ID 18: JU

-- ==========================================
-- 5. ASIGNACIÓN COLEGIOS - JORNADAS
-- Cada colegio con sus jornadas específicas
-- ==========================================
INSERT INTO colegio_jornadas (fk_colegio, fk_jornada, activa) VALUES
-- Usaquén
(1, 1, true),  -- Distrital Usaquén: JM
(1, 3, true),  -- Distrital Usaquén: JT
(2, 1, true),  -- Santa Bárbara: Solo JM
(3, 2, true),  -- Liceo Boston: JU

-- Santa Fe
(4, 4, true),  -- Agustín Nieto: JM
(4, 6, true),  -- Agustín Nieto: JT
(5, 6, true),  -- Caldas: Solo JT
(6, 5, true),  -- San Martín: JU

-- San Cristóbal
(7, 7, true),  -- San Cristóbal Sur: JM
(7, 9, true),  -- San Cristóbal Sur: JT
(8, 7, true),  -- Juan del Corral: Solo JM
(9, 8, true),  -- Ramón de Zubiría: JU

-- Kennedy
(10, 10, true), -- Kennedy: JM
(10, 12, true), -- Kennedy: JT
(11, 12, true), -- Técnico Industrial: Solo JT
(12, 11, true), -- Castilla: JU

-- Bosa
(13, 13, true), -- Integrado Fontibón: JM
(13, 15, true), -- Integrado Fontibón: JT
(14, 13, true), -- San Bernardino: Solo JM
(15, 14, true), -- Paulo VI: JU

-- Ciudad Bolívar
(16, 16, true), -- Arborizadora Alta: JM
(16, 18, true), -- Arborizadora Alta: JT
(17, 18, true), -- Técnico Distrital: Solo JT
(18, 17, true); -- Ciudad Bolívar: JU

-- ==========================================
-- 6. RUTAS (2 rutas por cada jornada de cada colegio: IDA y REGRESO)
-- ==========================================
INSERT INTO rutas (nombre_ruta, tipo_ruta, fk_zona, activa) VALUES
-- Usaquén - Colegio Distrital Usaquén (JM y JT)
('Usaquén Distrital - Mañana IDA', 'IDA', 1, true),
('Usaquén Distrital - Mañana REGRESO', 'REGRESO', 1, true),
('Usaquén Distrital - Tarde IDA', 'IDA', 1, true),
('Usaquén Distrital - Tarde REGRESO', 'REGRESO', 1, true),

-- Usaquén - Instituto Santa Bárbara (Solo JM)
('Santa Bárbara - Mañana IDA', 'IDA', 1, true),
('Santa Bárbara - Mañana REGRESO', 'REGRESO', 1, true),

-- Usaquén - Liceo Boston (JU)
('Liceo Boston - Única IDA', 'IDA', 1, true),
('Liceo Boston - Única REGRESO', 'REGRESO', 1, true),

-- Santa Fe - Colegio Agustín Nieto Caballero (JM y JT)
('Agustín Nieto - Mañana IDA', 'IDA', 2, true),
('Agustín Nieto - Mañana REGRESO', 'REGRESO', 2, true),
('Agustín Nieto - Tarde IDA', 'IDA', 2, true),
('Agustín Nieto - Tarde REGRESO', 'REGRESO', 2, true),

-- Santa Fe - Instituto Caldas (Solo JT)
('Caldas - Tarde IDA', 'IDA', 2, true),
('Caldas - Tarde REGRESO', 'REGRESO', 2, true),

-- Santa Fe - Colegio San Martín (JU)
('San Martín - Única IDA', 'IDA', 2, true),
('San Martín - Única REGRESO', 'REGRESO', 2, true),

-- San Cristóbal - Colegio San Cristóbal Sur (JM y JT)
('San Cristóbal Sur - Mañana IDA', 'IDA', 3, true),
('San Cristóbal Sur - Mañana REGRESO', 'REGRESO', 3, true),
('San Cristóbal Sur - Tarde IDA', 'IDA', 3, true),
('San Cristóbal Sur - Tarde REGRESO', 'REGRESO', 3, true),

-- San Cristóbal - Instituto Juan del Corral (Solo JM)
('Juan del Corral - Mañana IDA', 'IDA', 3, true),
('Juan del Corral - Mañana REGRESO', 'REGRESO', 3, true),

-- San Cristóbal - Colegio Ramón de Zubiría (JU)
('Ramón de Zubiría - Única IDA', 'IDA', 3, true),
('Ramón de Zubiría - Única REGRESO', 'REGRESO', 3, true),

-- Kennedy - Colegio Kennedy (JM y JT)
('Kennedy - Mañana IDA', 'IDA', 4, true),
('Kennedy - Mañana REGRESO', 'REGRESO', 4, true),
('Kennedy - Tarde IDA', 'IDA', 4, true),
('Kennedy - Tarde REGRESO', 'REGRESO', 4, true),

-- Kennedy - Instituto Técnico Industrial (Solo JT)
('Técnico Kennedy - Tarde IDA', 'IDA', 4, true),
('Técnico Kennedy - Tarde REGRESO', 'REGRESO', 4, true),

-- Kennedy - Colegio Castilla (JU)
('Castilla - Única IDA', 'IDA', 4, true),
('Castilla - Única REGRESO', 'REGRESO', 4, true),

-- Bosa - Colegio Integrado de Fontibón (JM y JT)
('Integrado Fontibón - Mañana IDA', 'IDA', 5, true),
('Integrado Fontibón - Mañana REGRESO', 'REGRESO', 5, true),
('Integrado Fontibón - Tarde IDA', 'IDA', 5, true),
('Integrado Fontibón - Tarde REGRESO', 'REGRESO', 5, true),

-- Bosa - Instituto San Bernardino (Solo JM)
('San Bernardino - Mañana IDA', 'IDA', 5, true),
('San Bernardino - Mañana REGRESO', 'REGRESO', 5, true),

-- Bosa - Colegio Paulo VI (JU)
('Paulo VI - Única IDA', 'IDA', 5, true),
('Paulo VI - Única REGRESO', 'REGRESO', 5, true),

-- Ciudad Bolívar - Colegio Arborizadora Alta (JM y JT)
('Arborizadora Alta - Mañana IDA', 'IDA', 6, true),
('Arborizadora Alta - Mañana REGRESO', 'REGRESO', 6, true),
('Arborizadora Alta - Tarde IDA', 'IDA', 6, true),
('Arborizadora Alta - Tarde REGRESO', 'REGRESO', 6, true),

-- Ciudad Bolívar - Instituto Técnico Distrital (Solo JT)
('Técnico Bolívar - Tarde IDA', 'IDA', 6, true),
('Técnico Bolívar - Tarde REGRESO', 'REGRESO', 6, true),

-- Ciudad Bolívar - Colegio Ciudad Bolívar (JU)
('Ciudad Bolívar - Única IDA', 'IDA', 6, true),
('Ciudad Bolívar - Única REGRESO', 'REGRESO', 6, true);

-- ==========================================
-- 7. MONITORES (Asignación de monitores a zonas y jornadas)
-- ==========================================
INSERT INTO monitores (fk_usuario, fk_zona, fk_jornada, fecha_asignacion, activo) VALUES
-- Usaquén
(8, 1, 1, CURDATE(), true),   -- Monitor Usaquén 1 - Mañana
(9, 1, 3, CURDATE(), true),   -- Monitor Usaquén 2 - Tarde
-- Santa Fe
(10, 2, 4, CURDATE(), true),  -- Monitor Santa Fe 1 - Mañana
(11, 2, 6, CURDATE(), true),  -- Monitor Santa Fe 2 - Tarde
-- San Cristóbal
(12, 3, 7, CURDATE(), true),  -- Monitor San Cristóbal 1 - Mañana
(13, 3, 9, CURDATE(), true),  -- Monitor San Cristóbal 2 - Tarde
-- Kennedy
(14, 4, 10, CURDATE(), true), -- Monitor Kennedy 1 - Mañana
(15, 4, 12, CURDATE(), true), -- Monitor Kennedy 2 - Tarde
-- Bosa
(16, 5, 13, CURDATE(), true), -- Monitor Bosa 1 - Mañana
(17, 5, 15, CURDATE(), true), -- Monitor Bosa 2 - Tarde
-- Ciudad Bolívar
(18, 6, 16, CURDATE(), true), -- Monitor Bolívar 1 - Mañana
(19, 6, 18, CURDATE(), true); -- Monitor Bolívar 2 - Tarde

-- ==========================================
-- 8. ESTUDIANTES (3 por zona, en diferentes colegios y jornadas)
-- ==========================================
INSERT INTO estudiantes (tipo_id, num_id, primer_nombre, segundo_nombre, primer_apellido, segundo_apellido, fecha_nacimiento, sexo, direccion, curso, eps, discapacidad, etnia, nombre_acudiente, telefono_acudiente, direccion_acudiente, email_acudiente, fk_colegio, fk_jornada, fk_ruta, fecha_inscripcion, estado_inscripcion, fecha_registro, activo) VALUES
-- Usaquén
('TI', '1100000001', 'Juan', 'Pablo', 'Rodriguez', 'Gomez', '2014-05-15', 'MASCULINO', 'Calle 170 #15-20', '5A', 'Nueva EPS', 'Ninguna', 'Ninguna', 'Maria Rodriguez', '3001234567', 'Calle 170 #15-20', 'maria.rodriguez@example.com', 1, 1, 1, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000002', 'Maria', 'Fernanda', 'Martinez', 'Lopez', '2014-08-20', 'FEMENINO', 'Carrera 7 #145-40', '5B', 'Sanitas', 'Ninguna', 'Ninguna', 'Pedro Martinez', '3009876543', 'Carrera 7 #145-40', 'pedro.martinez@example.com', 2, 1, 5, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000003', 'Carlos', 'Andres', 'Garcia', 'Perez', '2013-03-10', 'MASCULINO', 'Calle 165 #20-30', '6A', 'Sura', 'Ninguna', 'Ninguna', 'Ana Garcia', '3005551234', 'Calle 165 #20-30', 'ana.garcia@example.com', 3, 2, 7, CURDATE(), 'ACTIVA', CURDATE(), true),

-- Santa Fe
('TI', '1100000004', 'Laura', 'Valentina', 'Ramirez', 'Castro', '2014-11-25', 'FEMENINO', 'Carrera 5 #25-35', '5A', 'Nueva EPS', 'Ninguna', 'Ninguna', 'Carlos Ramirez', '3001112233', 'Carrera 5 #25-35', 'carlos.ramirez@example.com', 4, 4, 9, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000005', 'Diego', 'Alejandro', 'Vargas', 'Moreno', '2013-07-18', 'MASCULINO', 'Calle 19 #3-50', '6B', 'Sanitas', 'Ninguna', 'Ninguna', 'Sandra Vargas', '3002223344', 'Calle 19 #3-50', 'sandra.vargas@example.com', 5, 6, 13, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000006', 'Sara', 'Isabella', 'Mendez', 'Ortiz', '2014-02-14', 'FEMENINO', 'Carrera 10 #15-20', '5A', 'Sura', 'Ninguna', 'Ninguna', 'Luis Mendez', '3003334455', 'Carrera 10 #15-20', 'luis.mendez@example.com', 6, 5, 15, CURDATE(), 'ACTIVA', CURDATE(), true),

-- San Cristóbal
('TI', '1100000007', 'Andres', 'Felipe', 'Jimenez', 'Rojas', '2013-09-22', 'MASCULINO', 'Calle 1 Sur #18-25', '6A', 'Nueva EPS', 'Ninguna', 'Ninguna', 'Patricia Jimenez', '3004445566', 'Calle 1 Sur #18-25', 'patricia.jimenez@example.com', 7, 7, 17, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000008', 'Camila', 'Andrea', 'Morales', 'Gutierrez', '2014-06-30', 'FEMENINO', 'Carrera 5 Este #5-30', '5B', 'Sanitas', 'Ninguna', 'Ninguna', 'Jorge Morales', '3005556677', 'Carrera 5 Este #5-30', 'jorge.morales@example.com', 8, 7, 21, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000009', 'Sebastian', 'David', 'Castro', 'Herrera', '2013-12-05', 'MASCULINO', 'Calle 8 Sur #10-35', '6B', 'Sura', 'Ninguna', 'Ninguna', 'Monica Castro', '3006667788', 'Calle 8 Sur #10-35', 'monica.castro@example.com', 9, 8, 23, CURDATE(), 'ACTIVA', CURDATE(), true),

-- Kennedy
('TI', '1100000010', 'Valentina', 'Sofia', 'Torres', 'Silva', '2014-04-17', 'FEMENINO', 'Carrera 78 #40-50', '5A', 'Nueva EPS', 'Ninguna', 'Ninguna', 'Ricardo Torres', '3007778899', 'Carrera 78 #40-50', 'ricardo.torres@example.com', 10, 10, 25, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000011', 'Daniel', 'Santiago', 'Lopez', 'Gomez', '2013-10-08', 'MASCULINO', 'Calle 38 Sur #80-20', '6A', 'Sanitas', 'Ninguna', 'Ninguna', 'Andrea Lopez', '3008889900', 'Calle 38 Sur #80-20', 'andrea.lopez@example.com', 11, 12, 29, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000012', 'Isabella', 'Marcela', 'Sanchez', 'Rojas', '2014-01-20', 'FEMENINO', 'Carrera 86 #42-30', '5B', 'Sura', 'Ninguna', 'Ninguna', 'Felipe Sanchez', '3009990011', 'Carrera 86 #42-30', 'felipe.sanchez@example.com', 12, 11, 31, CURDATE(), 'ACTIVA', CURDATE(), true),

-- Bosa
('TI', '1100000013', 'Nicolas', 'Alejandro', 'Ruiz', 'Martinez', '2013-05-12', 'MASCULINO', 'Carrera 98 #60-25', '6A', 'Nueva EPS', 'Ninguna', 'Ninguna', 'Carolina Ruiz', '3001011122', 'Carrera 98 #60-25', 'carolina.ruiz@example.com', 13, 13, 33, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000014', 'Mariana', 'Alejandra', 'Diaz', 'Castro', '2014-09-03', 'FEMENINO', 'Calle 57 Sur #89-40', '5A', 'Sanitas', 'Ninguna', 'Ninguna', 'Miguel Diaz', '3002022233', 'Calle 57 Sur #89-40', 'miguel.diaz@example.com', 14, 13, 37, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000015', 'Santiago', 'Esteban', 'Gutierrez', 'Silva', '2013-11-28', 'MASCULINO', 'Carrera 92 #65-15', '6B', 'Sura', 'Ninguna', 'Ninguna', 'Gloria Gutierrez', '3003033344', 'Carrera 92 #65-15', 'gloria.gutierrez@example.com', 15, 14, 39, CURDATE(), 'ACTIVA', CURDATE(), true),

-- Ciudad Bolívar
('TI', '1100000016', 'Sofia', 'Camila', 'Herrera', 'Moreno', '2014-07-22', 'FEMENINO', 'Calle 65 Sur #10-20', '5A', 'Nueva EPS', 'Ninguna', 'Ninguna', 'Javier Herrera', '3004044455', 'Calle 65 Sur #10-20', 'javier.herrera@example.com', 16, 16, 41, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000017', 'Mateo', 'Andres', 'Ospina', 'Ramirez', '2013-04-05', 'MASCULINO', 'Carrera 24 Sur #68-30', '6A', 'Sanitas', 'Ninguna', 'Ninguna', 'Paola Ospina', '3005055566', 'Carrera 24 Sur #68-30', 'paola.ospina@example.com', 17, 18, 45, CURDATE(), 'ACTIVA', CURDATE(), true),
('TI', '1100000018', 'Emma', 'Valentina', 'Molina', 'Torres', '2014-12-15', 'FEMENINO', 'Calle 70 Sur #15-40', '5B', 'Sura', 'Ninguna', 'Ninguna', 'German Molina', '3006066677', 'Calle 70 Sur #15-40', 'german.molina@example.com', 18, 17, 47, CURDATE(), 'ACTIVA', CURDATE(), true);