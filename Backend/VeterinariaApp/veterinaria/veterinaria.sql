-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-01-2026 a las 20:20:11
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `veterinaria`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `alergia`
--

CREATE TABLE `alergia` (
  `idAlergia` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `historial_clinico`
--

CREATE TABLE `historial_clinico` (
  `idHistorial` bigint(20) NOT NULL,
  `diagnostico` varchar(255) DEFAULT NULL,
  `fechaAtencion` datetime(6) DEFAULT NULL,
  `motivoConsulta` varchar(255) DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `peso` double DEFAULT NULL,
  `temperatura` double DEFAULT NULL,
  `id_mascota` bigint(20) NOT NULL,
  `id_veterinario` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mascota`
--

CREATE TABLE `mascota` (
  `idMascota` bigint(20) NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `especie` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `esterilizado` bit(1) DEFAULT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `pesoActual` double DEFAULT NULL,
  `raza` varchar(255) DEFAULT NULL,
  `sexo` varchar(255) DEFAULT NULL,
  `id_propietario` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `mascota`
--

INSERT INTO `mascota` (`idMascota`, `color`, `especie`, `estado`, `esterilizado`, `fechaNacimiento`, `nombre`, `pesoActual`, `raza`, `sexo`, `id_propietario`) VALUES
(1, 'Blanco', 'Perro', 'Excelente', b'1', '2025-02-04', 'Tobi', 7, 'French poodle', 'M', 1),
(2, 'Rojo', 'Gato', 'Bueno', b'0', '2023-07-24', 'Felix', 5, 'Crialla', 'H', 2),
(3, 'Beige', 'Perro', NULL, NULL, '2017-04-11', 'Rex', 9, 'Poodle', 'M', 1),
(4, 'Cafe Negro', 'Perro', NULL, NULL, '2025-01-26', 'Tiranosaurio Rex', 18, 'Pastor Aleman', 'M', 2),
(5, 'Blanco y gris', 'Uron', NULL, NULL, '2024-02-04', 'Tobis', 4, 'Comun', 'H', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `mascota_alergia`
--

CREATE TABLE `mascota_alergia` (
  `id` bigint(20) NOT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `id_alergia` bigint(20) NOT NULL,
  `id_mascota` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medicamento`
--

CREATE TABLE `medicamento` (
  `idMedicamento` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `presentacion` varchar(255) DEFAULT NULL,
  `tipo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `propietario`
--

CREATE TABLE `propietario` (
  `idPropietario` bigint(20) NOT NULL,
  `apellidos` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nombres` varchar(255) DEFAULT NULL,
  `numeroDocumento` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `tipoDocumento` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `propietario`
--

INSERT INTO `propietario` (`idPropietario`, `apellidos`, `direccion`, `email`, `nombres`, `numeroDocumento`, `telefono`, `tipoDocumento`) VALUES
(1, 'Palacios', 'calle 25', 'propietario1@propietario.com', 'Jose', '79876555', '2331144', 'Cedula'),
(2, 'Suarez', 'avenida siempreviva', 'yolanda@propietario.com', 'Yolanda', '52456789', '7889966', 'Cedula');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `idRol` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`idRol`, `descripcion`, `nombre`) VALUES
(1, 'Administrador tempral', 'Administrador'),
(2, 'Veterinario experto', 'Veterinario'),
(3, 'Recepcionista de puerta', 'Recepcion'),
(4, 'Consulta de casa', 'Consulta');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tratamiento`
--

CREATE TABLE `tratamiento` (
  `idTratamiento` bigint(20) NOT NULL,
  `dosis` varchar(255) DEFAULT NULL,
  `duracion` int(11) DEFAULT NULL,
  `frecuencia` varchar(255) DEFAULT NULL,
  `indicaciones` varchar(255) DEFAULT NULL,
  `id_historial` bigint(20) NOT NULL,
  `id_medicamento` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `idUsuario` bigint(20) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `estado` bit(1) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `passwordHash` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`idUsuario`, `email`, `estado`, `nombre`, `passwordHash`, `username`) VALUES
(1, 'admin@admin.com', b'1', 'Administrador', '$2a$10$Tc8010rZAj66Paf/7D6a.OfZRQqnArlLZwxCksCAIWq4OCNbl/6cW', 'Admin'),
(2, 'vete@vete.com', b'1', 'Veterinario', '987654321', 'Vete'),
(3, 'recep@recep.com', b'1', 'Recepcionista', '159357', 'Recep'),
(4, 'consul@consul.com', b'1', 'Consultor', '951753', 'Consul');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_rol`
--

CREATE TABLE `usuario_rol` (
  `id` bigint(20) NOT NULL,
  `id_rol` bigint(20) DEFAULT NULL,
  `id_usuario` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `usuario_rol`
--

INSERT INTO `usuario_rol` (`id`, `id_rol`, `id_usuario`) VALUES
(1, 1, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vacuna`
--

CREATE TABLE `vacuna` (
  `idVacuna` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `especieAplicable` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vacunacion`
--

CREATE TABLE `vacunacion` (
  `idVacunacion` bigint(20) NOT NULL,
  `fechaAplicacion` date DEFAULT NULL,
  `fechaProxima` date DEFAULT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `id_mascota` bigint(20) NOT NULL,
  `id_vacuna` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `veterinario`
--

CREATE TABLE `veterinario` (
  `idVeterinario` bigint(20) NOT NULL,
  `apellidos` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `especialidad` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `nombres` varchar(255) DEFAULT NULL,
  `numeroDocumento` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `tipoDocumento` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_spanish_ci;

--
-- Volcado de datos para la tabla `veterinario`
--

INSERT INTO `veterinario` (`idVeterinario`, `apellidos`, `email`, `especialidad`, `estado`, `nombres`, `numeroDocumento`, `telefono`, `tipoDocumento`) VALUES
(1, 'Reyes', 'daniel@veterinario.com', 'General', 'Activo', 'Daniel', '1001123456', '3125474748', 'Cedula');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `alergia`
--
ALTER TABLE `alergia`
  ADD PRIMARY KEY (`idAlergia`);

--
-- Indices de la tabla `historial_clinico`
--
ALTER TABLE `historial_clinico`
  ADD PRIMARY KEY (`idHistorial`),
  ADD KEY `FKbj9ruanaik98al9awh8lnpxey` (`id_mascota`),
  ADD KEY `FKrkse8m5lnso86ipc2j44g11rr` (`id_veterinario`);

--
-- Indices de la tabla `mascota`
--
ALTER TABLE `mascota`
  ADD PRIMARY KEY (`idMascota`),
  ADD KEY `FKsssk96eor25ho8ox5ivrj7xcg` (`id_propietario`);

--
-- Indices de la tabla `mascota_alergia`
--
ALTER TABLE `mascota_alergia`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKfhkbhpb7iqyiyfvvqgjajno0m` (`id_alergia`),
  ADD KEY `FKmgc2nq5yblvl3cv3yas9daush` (`id_mascota`);

--
-- Indices de la tabla `medicamento`
--
ALTER TABLE `medicamento`
  ADD PRIMARY KEY (`idMedicamento`);

--
-- Indices de la tabla `propietario`
--
ALTER TABLE `propietario`
  ADD PRIMARY KEY (`idPropietario`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`idRol`);

--
-- Indices de la tabla `tratamiento`
--
ALTER TABLE `tratamiento`
  ADD PRIMARY KEY (`idTratamiento`),
  ADD KEY `FKlrefo1dmr69vgsmld3lgkcr4j` (`id_historial`),
  ADD KEY `FKpwom5hyoclsmslhloxviyf1tp` (`id_medicamento`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`idUsuario`);

--
-- Indices de la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKkxcv7htfnm9x1wkofnud0ewql` (`id_rol`),
  ADD KEY `FK3ftpt75ebughsiy5g03b11akt` (`id_usuario`);

--
-- Indices de la tabla `vacuna`
--
ALTER TABLE `vacuna`
  ADD PRIMARY KEY (`idVacuna`);

--
-- Indices de la tabla `vacunacion`
--
ALTER TABLE `vacunacion`
  ADD PRIMARY KEY (`idVacunacion`),
  ADD KEY `FKcw3b85ebndjhsuedltwvqco8h` (`id_mascota`),
  ADD KEY `FKbo9va5fw6ofxpahxequm0mnvt` (`id_vacuna`);

--
-- Indices de la tabla `veterinario`
--
ALTER TABLE `veterinario`
  ADD PRIMARY KEY (`idVeterinario`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `alergia`
--
ALTER TABLE `alergia`
  MODIFY `idAlergia` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `historial_clinico`
--
ALTER TABLE `historial_clinico`
  MODIFY `idHistorial` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `mascota`
--
ALTER TABLE `mascota`
  MODIFY `idMascota` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `mascota_alergia`
--
ALTER TABLE `mascota_alergia`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `medicamento`
--
ALTER TABLE `medicamento`
  MODIFY `idMedicamento` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `propietario`
--
ALTER TABLE `propietario`
  MODIFY `idPropietario` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `idRol` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `tratamiento`
--
ALTER TABLE `tratamiento`
  MODIFY `idTratamiento` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `idUsuario` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `vacuna`
--
ALTER TABLE `vacuna`
  MODIFY `idVacuna` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `vacunacion`
--
ALTER TABLE `vacunacion`
  MODIFY `idVacunacion` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `veterinario`
--
ALTER TABLE `veterinario`
  MODIFY `idVeterinario` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `historial_clinico`
--
ALTER TABLE `historial_clinico`
  ADD CONSTRAINT `FKbj9ruanaik98al9awh8lnpxey` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`idMascota`),
  ADD CONSTRAINT `FKrkse8m5lnso86ipc2j44g11rr` FOREIGN KEY (`id_veterinario`) REFERENCES `veterinario` (`idVeterinario`);

--
-- Filtros para la tabla `mascota`
--
ALTER TABLE `mascota`
  ADD CONSTRAINT `FKsssk96eor25ho8ox5ivrj7xcg` FOREIGN KEY (`id_propietario`) REFERENCES `propietario` (`idPropietario`);

--
-- Filtros para la tabla `mascota_alergia`
--
ALTER TABLE `mascota_alergia`
  ADD CONSTRAINT `FKfhkbhpb7iqyiyfvvqgjajno0m` FOREIGN KEY (`id_alergia`) REFERENCES `alergia` (`idAlergia`),
  ADD CONSTRAINT `FKmgc2nq5yblvl3cv3yas9daush` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`idMascota`);

--
-- Filtros para la tabla `tratamiento`
--
ALTER TABLE `tratamiento`
  ADD CONSTRAINT `FKlrefo1dmr69vgsmld3lgkcr4j` FOREIGN KEY (`id_historial`) REFERENCES `historial_clinico` (`idHistorial`),
  ADD CONSTRAINT `FKpwom5hyoclsmslhloxviyf1tp` FOREIGN KEY (`id_medicamento`) REFERENCES `medicamento` (`idMedicamento`);

--
-- Filtros para la tabla `usuario_rol`
--
ALTER TABLE `usuario_rol`
  ADD CONSTRAINT `FK3ftpt75ebughsiy5g03b11akt` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`idUsuario`),
  ADD CONSTRAINT `FKkxcv7htfnm9x1wkofnud0ewql` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`idRol`);

--
-- Filtros para la tabla `vacunacion`
--
ALTER TABLE `vacunacion`
  ADD CONSTRAINT `FKbo9va5fw6ofxpahxequm0mnvt` FOREIGN KEY (`id_vacuna`) REFERENCES `vacuna` (`idVacuna`),
  ADD CONSTRAINT `FKcw3b85ebndjhsuedltwvqco8h` FOREIGN KEY (`id_mascota`) REFERENCES `mascota` (`idMascota`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
