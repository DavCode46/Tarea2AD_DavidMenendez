-- Creación de la base de datos
CREATE DATABASE IF NOT EXISTS bdperegrinos_DavidMenendez;
USE bdperegrinos_DavidMenendez;

-- Tabla Usuarios
CREATE TABLE Usuarios (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	usuario VARCHAR(50) NOT NULL,
	password VARCHAR(50) NOT NULL,
	perfil VARCHAR(50) NOT NULL
);

-- Tabla Paradas
CREATE TABLE Paradas (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(50) NOT NULL,
	region CHAR(1) NOT NULL,
	responsable VARCHAR(50) NOT NULL,
	id_usuario BIGINT NOT NULL,
	FOREIGN KEY (id_usuario) REFERENCES Usuarios(id)
);

-- Tabla Carnets
CREATE TABLE Carnets (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	fechaexp DATE DEFAULT CURRENT_DATE,
	distancia DOUBLE(10, 2) DEFAULT 0.0,
	nvips INT DEFAULT 0,
	parada_inicial BIGINT NOT NULL,
	FOREIGN KEY (parada_inicial) REFERENCES Paradas(id)
);

-- Tabla Peregrinos
CREATE TABLE Peregrinos (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	nombre VARCHAR(50) NOT NULL,
	nacionalidad VARCHAR(50) NOT NULL,
	id_carnet BIGINT NOT NULL,
	id_usuario BIGINT NOT NULL,
	FOREIGN KEY (id_carnet) REFERENCES Carnets(id),
	FOREIGN KEY (id_usuario) REFERENCES Usuarios(id)
);

-- Tabla Estancias
CREATE TABLE Estancias (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	id_peregrino BIGINT NOT NULL,
	id_parada BIGINT NOT NULL,
	fecha DATE NOT NULL,
	vip BOOLEAN DEFAULT FALSE,
	FOREIGN KEY (id_peregrino) REFERENCES Peregrinos(id),
	FOREIGN KEY (id_parada) REFERENCES Paradas(id)
);

-- Tabla Peregrinos_Paradas (N:M)
CREATE TABLE Peregrinos_Paradas (
	id_peregrino BIGINT AUTO_INCREMENT,
	id_parada BIGINT AUTO_INCREMENT,
	fecha DATE NOT NULL,
	PRIMARY KEY (id_peregrino, id_parada),
	FOREIGN KEY (id_peregrino) REFERENCES Peregrinos(id),
	FOREIGN KEY (id_parada) REFERENCES Paradas(id)
);


-- Insertar datos en la tabla Usuarios
INSERT INTO Usuarios (usuario, password, perfil) VALUES
('admin', 'admin', 'administrador'),
('responsable1', 'respass1', 'responsable'),
('responsable2', 'respass2', 'responsable'),
('responsable3', 'respass3', 'responsable'),
('responsable4', 'respass4', 'responsable'),
('responsable5', 'respass5', 'responsable'),
('peregrino1', 'perepass1', 'peregrino'),
('peregrino2', 'perepass2', 'peregrino'),
('peregrino3', 'perepass3', 'peregrino'),
('peregrino4', 'perepass4', 'peregrino'),
('peregrino5', 'perepass5', 'peregrino');

-- Insertar datos en la tabla Paradas
INSERT INTO Paradas (nombre, region, responsable, id_usuario) VALUES
('Gijón', 'N', 'responsable1', 2),    
('Oviedo', 'N', 'responsable2', 3),   
('Avilés', 'N', 'responsable3', 4),   
('Ribadeo', 'N', 'responsable4', 5),  
('Santiago', 'N', 'responsable5', 6); 

-- Insertar datos en la tabla Carnets
INSERT INTO Carnets (fechaexp, distancia, nvips, parada_inicial) VALUES
('2024-01-01', 500.0, 3, 1),
('2024-02-01', 500.0, 2, 2),
('2024-03-01', 50.0, 0, 3),
('2024-04-01', 0.0, 0, 4),
('2024-05-01', 0.0, 0, 5);

-- Insertar datos en la tabla Peregrinos
INSERT INTO Peregrinos (nombre, nacionalidad, id_carnet, id_usuario) VALUES
('Juan Pérez', 'España', 1, 7),
('Laura García', 'México', 2, 8),
('Carlos Fernández', 'Colombia', 3, 9),
('Ana López', 'Argentina', 4, 10),
('Pedro Gómez', 'España', 5, 11);

-- Insertar datos en la tabla Estancias
INSERT INTO Estancias (id_peregrino, id_parada, fecha, vip) VALUES
(1, 1, '2024-01-02', TRUE),
(1, 2, '2024-01-03', FALSE),
(1, 3, '2024-01-04', TRUE),
(1, 4, '2024-01-05', FALSE),
(1, 5, '2024-01-06', TRUE),
(2, 2, '2024-02-02', FALSE),
(2, 5, '2024-02-06', TRUE);

-- Insertar datos en la tabla Peregrinos_Paradas
INSERT INTO Peregrinos_Paradas (id_peregrino, id_parada, fecha) VALUES
(1, 1, '2024-01-02'),
(1, 2, '2024-01-03'),
(1, 3, '2024-01-04'),
(1, 4, '2024-01-05'),
(1, 5, '2024-01-06'),
(2, 1, '2024-02-01'),
(2, 2, '2024-02-02'),
(2, 3, '2024-02-03'),
(2, 4, '2024-02-04'),
(2, 5, '2024-02-05'),
(3, 2, '2024-03-02'),
(3, 4, '2024-03-03'),
(4, 3, '2024-04-01'),
(5, 5, '2024-05-01');
