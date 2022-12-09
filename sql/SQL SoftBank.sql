-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 03-12-2022 a las 16:56:23
-- Versión del servidor: 10.4.25-MariaDB
-- Versión de PHP: 8.1.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `softbank`
--
DROP DATABASE IF EXISTS `softbank`;
CREATE DATABASE IF NOT EXISTS `softbank` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `softbank`;

DELIMITER $$
--
-- Procedimientos
--
DROP PROCEDURE IF EXISTS `ActualizarSaldoCC`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ActualizarSaldoCC` (IN `idProductoBancario` INT(11))  DETERMINISTIC BEGIN
    UPDATE CUENTA_CORRIENTE C
    SET C.IMP_SALDO_ACTUAL = (SELECT SUM(M.IMP_MOVIMIENTO) 
                              FROM MOVIMIENTO M
                              WHERE M.ID_PRODUCTO_BANCARIO = C.ID_PRODUCTO_BANCARIO)
    WHERE C.ID_PRODUCTO_BANCARIO = idProductoBancario;
END$$

DROP PROCEDURE IF EXISTS `ActualizarSaldoPT`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ActualizarSaldoPT` (IN `idProductoBancario` INT(11))  DETERMINISTIC BEGIN
    UPDATE PRESTAMO P
    SET P.IMP_SALDO_PENDIENTE = (SELECT SUM(M.IMP_MOVIMIENTO) 
                                 FROM MOVIMIENTO M
                                 WHERE M.ID_PRODUCTO_BANCARIO = P.ID_PRODUCTO_BANCARIO)
    WHERE P.ID_PRODUCTO_BANCARIO = idProductoBancario;
END$$

DROP PROCEDURE IF EXISTS `ActualizarSaldoTC`$$
CREATE DEFINER=`root`@`localhost` PROCEDURE `ActualizarSaldoTC` (IN `idProductoBancario` INT(11))  DETERMINISTIC BEGIN
    UPDATE TARJETA_CREDITO T
    SET T.IMP_SALDO_PENDIENTE = (SELECT SUM(M.IMP_MOVIMIENTO) 
                                 FROM MOVIMIENTO M
                                 WHERE M.ID_PRODUCTO_BANCARIO = T.ID_PRODUCTO_BANCARIO)
    WHERE T.ID_PRODUCTO_BANCARIO = idProductoBancario;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `aval`
--

DROP TABLE IF EXISTS `aval`;
CREATE TABLE `aval` (
  `ID_AVAL` int(11) NOT NULL,
  `ID_PRODUCTO_BANCARIO` int(11) NOT NULL,
  `NUMERO_AVAL` decimal(10,0) NOT NULL,
  `FEC_VENCIMIENTO` date NOT NULL,
  `IMP_AVALADO` decimal(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `aval`:
--   `ID_PRODUCTO_BANCARIO`
--       `producto_bancario` -> `ID_PRODUCTO_BANCARIO`
--

--
-- Truncar tablas antes de insertar `aval`
--

TRUNCATE TABLE `aval`;
-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `catalogo`
--

DROP TABLE IF EXISTS `catalogo`;
CREATE TABLE `catalogo` (
  `ID_CATALOGO` int(11) NOT NULL,
  `COD_PRODUCTO` varchar(50) NOT NULL,
  `NOMBRE_PRODUCTO` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `catalogo`:
--

--
-- Truncar tablas antes de insertar `catalogo`
--

TRUNCATE TABLE `catalogo`;
--
-- Volcado de datos para la tabla `catalogo`
--

INSERT INTO `catalogo` (`ID_CATALOGO`, `COD_PRODUCTO`, `NOMBRE_PRODUCTO`) VALUES
(1, 'CCC', 'CUENTA CORRIENTE'),
(2, 'TJC', 'TARJETA DE CRÉDITO'),
(3, 'PTM', 'PRÉSTAMO'),
(4, 'AVL', 'AVAL BANCARIO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

DROP TABLE IF EXISTS `cliente`;
CREATE TABLE `cliente` (
  `ID_CLIENTE` int(11) NOT NULL,
  `TIPO_IDENTIFICACION` enum('DNI','CIF','NIE','PASAPORTE') NOT NULL,
  `CLAVE_IDENTIFICACION` varchar(9) NOT NULL,
  `NOMBRE_CLIENTE` varchar(50) NOT NULL,
  `APELLIDOS_CLIENTE` varchar(100) DEFAULT NULL,
  `FEC_NACIMIENTO` date NOT NULL,
  `NACIONALIDAD` varchar(30) NOT NULL,
  `ID_DIRECCION` int(11) NOT NULL,
  `ID_EMPLEADO` int(11) NOT NULL COMMENT 'Empleado / Comercial del banco asignado al cliente.',
  `ID_USUARIO` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `cliente`:
--   `ID_DIRECCION`
--       `direccion` -> `ID_DIRECCION`
--   `ID_EMPLEADO`
--       `empleado` -> `ID_EMPLEADO`
--   `ID_USUARIO`
--       `usuario` -> `ID_USUARIO`
--

--
-- Truncar tablas antes de insertar `cliente`
--

TRUNCATE TABLE `cliente`;
--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`ID_CLIENTE`, `TIPO_IDENTIFICACION`, `CLAVE_IDENTIFICACION`, `NOMBRE_CLIENTE`, `APELLIDOS_CLIENTE`, `FEC_NACIMIENTO`, `NACIONALIDAD`, `ID_DIRECCION`, `ID_EMPLEADO`, `ID_USUARIO`) VALUES
(1, 'DNI', '51682917P', 'FRANCISCO', 'JIMÉNEZ DÍAZ', '1975-03-15', 'ESPAÑOLA', 2, 1, 2),
(2, 'CIF', 'B92253467', 'PASTELERÍA MIGAS', NULL, '2005-07-20', 'ESPAÑOLA', 3, 1, 3),
(3, 'PASAPORTE', 'C01X00T47', 'ERIKA', 'MUSTERMANN', '2019-11-09', 'ALEMANA', 5, 2, 11),
(8, 'DNI', '54885457M', 'JESÚS', 'LÓPEZ MARIN', '1971-05-20', 'ESPAÑOLA', 15, 7, 17),
(9, 'DNI', '05874555X', 'ADELA', 'ESTEBAN DE LA CRUZ', '1973-09-20', 'ESPAÑOLA', 15, 7, 18),
(10, 'NIE', 'X6855522G', 'PIERRE', 'TRUDEAU', '1979-02-21', 'FRANCESA', 16, 2, 19),
(13, 'CIF', 'A11063161', 'CHICLANA NATURAL, S.A.', '', '1987-04-21', 'ESPAÑOLA', 18, 13, 32);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente_producto_bancario`
--

DROP TABLE IF EXISTS `cliente_producto_bancario`;
CREATE TABLE `cliente_producto_bancario` (
  `ID_CLIENTE` int(11) NOT NULL,
  `ID_PRODUCTO_BANCARIO` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `cliente_producto_bancario`:
--   `ID_CLIENTE`
--       `cliente` -> `ID_CLIENTE`
--   `ID_PRODUCTO_BANCARIO`
--       `producto_bancario` -> `ID_PRODUCTO_BANCARIO`
--

--
-- Truncar tablas antes de insertar `cliente_producto_bancario`
--

TRUNCATE TABLE `cliente_producto_bancario`;
--
-- Volcado de datos para la tabla `cliente_producto_bancario`
--

INSERT INTO `cliente_producto_bancario` (`ID_CLIENTE`, `ID_PRODUCTO_BANCARIO`) VALUES
(1, 1),
(1, 4),
(1, 20),
(2, 3),
(2, 15),
(3, 2),
(3, 18),
(8, 9),
(8, 11),
(8, 14),
(9, 9),
(9, 19),
(9, 21),
(10, 12),
(10, 13),
(10, 16),
(10, 17),
(13, 22),
(13, 23);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cuenta_corriente`
--

DROP TABLE IF EXISTS `cuenta_corriente`;
CREATE TABLE `cuenta_corriente` (
  `ID_CUENTA_CORRIENTE` int(11) NOT NULL,
  `ID_PRODUCTO_BANCARIO` int(11) NOT NULL,
  `NUMERO_CUENTA` int(10) NOT NULL,
  `IBAN` varchar(24) NOT NULL,
  `TIPO_INTERES` decimal(4,2) NOT NULL,
  `IMP_SALDO_ACTUAL` decimal(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `cuenta_corriente`:
--   `ID_PRODUCTO_BANCARIO`
--       `producto_bancario` -> `ID_PRODUCTO_BANCARIO`
--

--
-- Truncar tablas antes de insertar `cuenta_corriente`
--

TRUNCATE TABLE `cuenta_corriente`;
--
-- Volcado de datos para la tabla `cuenta_corriente`
--

INSERT INTO `cuenta_corriente` (`ID_CUENTA_CORRIENTE`, `ID_PRODUCTO_BANCARIO`, `NUMERO_CUENTA`, `IBAN`, `TIPO_INTERES`, `IMP_SALDO_ACTUAL`) VALUES
(1, 1, 1000000001, 'ES9001381000941000000001', '0.20', '5000.00'),
(2, 3, 1000000002, 'ES4401381000991000000002', '0.10', '120.00'),
(3, 2, 1000000003, 'ES1301382001531000000003', '0.10', '19.30'),
(4, 9, 1000000004, 'ES5201381000981000000004', '0.30', '45300.00'),
(6, 11, 1000000005, 'ES5101381001321000000005', '0.20', '364.43'),
(7, 12, 1000000006, 'ES6701384020971000000006', '0.70', '0.00'),
(8, 13, 1000000007, 'ES1701382000011000000007', '0.20', '0.00'),
(9, 16, 1000000008, 'ES7501384020961000000008', '0.20', '0.00'),
(10, 17, 1000000009, 'ES9201383000201000000009', '0.20', '0.00'),
(11, 22, 1000000010, 'ES5601384020971000000010', '0.10', '12.47');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `direccion`
--

DROP TABLE IF EXISTS `direccion`;
CREATE TABLE `direccion` (
  `ID_DIRECCION` int(11) NOT NULL,
  `TIPO_VIA` set('CALLE','AVENIDA','PASEO','PLAZA','CARRETERA','TRAVESÍA') NOT NULL,
  `NOMBRE_VIA` varchar(100) NOT NULL,
  `NUMERO` decimal(6,3) NOT NULL,
  `POBLACION` varchar(100) NOT NULL,
  `PROVINCIA_ESTADO` varchar(100) NOT NULL,
  `COD_POSTAL` int(5) NOT NULL,
  `PAIS` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `direccion`:
--

--
-- Truncar tablas antes de insertar `direccion`
--

TRUNCATE TABLE `direccion`;
--
-- Volcado de datos para la tabla `direccion`
--

INSERT INTO `direccion` (`ID_DIRECCION`, `TIPO_VIA`, `NOMBRE_VIA`, `NUMERO`, `POBLACION`, `PROVINCIA_ESTADO`, `COD_POSTAL`, `PAIS`) VALUES
(1, 'CALLE', 'ALCALÁ', '75.000', 'MADRID', 'MADRID', 28009, 'ESPAÑA'),
(2, 'AVENIDA', 'PIO XII', '2.000', 'VALENCIA', 'VALENCIA', 46009, 'ESPAÑA'),
(3, 'CALLE', 'PLATINO', '2.000', 'TORREJÓN DE ARDOZ', 'MADRID', 28850, 'ESPAÑA'),
(4, 'PLAZA', 'TETUAN', '9.000', 'BARCELONA', 'BARCELONA', 8010, 'ESPAÑA'),
(5, 'CALLE', 'LLUÍS DALMAU', '25.000', 'BARCELONA', 'BARCELONA', 8019, 'ESPAÑA'),
(6, 'AVENIDA', 'SAN DIEGO', '120.000', 'MADRID', 'MADRID', 28018, 'ESPAÑA'),
(7, 'TRAVESÍA', 'DE LAS FLORES', '8.000', 'VALENCIA', 'VALENCIA', 46011, 'ESPAÑA'),
(11, 'CARRETERA', 'DE LA BARROSA', '35.500', 'CHICLANA DE LA FRONTERA', 'CÁDIZ', 11139, 'ESPAÑA'),
(15, 'PLAZA', 'CASCORRO', '1.000', 'MADRID', 'MADRID', 28005, 'ESPAÑA'),
(16, 'AVENIDA', 'DIAGONAL', '439.000', 'BARCELONA', 'BARCELONA', 8036, 'ESPAÑA'),
(18, 'CALLE', 'DOCTOR PEDRO VELEZ', '5.000', 'CHICLANA DE LA FRONTERA', 'CÁDIZ', 11130, 'ESPAÑA');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

DROP TABLE IF EXISTS `empleado`;
CREATE TABLE `empleado` (
  `ID_EMPLEADO` int(11) NOT NULL,
  `NOMBRE_EMPLEADO` varchar(50) NOT NULL,
  `APELLIDOS_EMPLEADO` varchar(100) NOT NULL,
  `ID_SUCURSAL` int(11) NOT NULL,
  `ID_USUARIO` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `empleado`:
--   `ID_SUCURSAL`
--       `sucursal` -> `ID_SUCURSAL`
--   `ID_USUARIO`
--       `usuario` -> `ID_USUARIO`
--

--
-- Truncar tablas antes de insertar `empleado`
--

TRUNCATE TABLE `empleado`;
--
-- Volcado de datos para la tabla `empleado`
--

INSERT INTO `empleado` (`ID_EMPLEADO`, `NOMBRE_EMPLEADO`, `APELLIDOS_EMPLEADO`, `ID_SUCURSAL`, `ID_USUARIO`) VALUES
(1, 'JOSÉ', 'SÁNCHEZ PÉREZ', 3, 1),
(2, 'ORIOL', 'PUJOL MONTOLIU', 2, 4),
(7, 'IGNACIO', 'PINEDA MARTIN', 1, 10),
(13, 'ADRÍAN', 'VALLE HERNÁNDEZ', 13, 31);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `movimiento`
--

DROP TABLE IF EXISTS `movimiento`;
CREATE TABLE `movimiento` (
  `ID_MOVIMIENTO` int(11) NOT NULL,
  `ID_PRODUCTO_BANCARIO` int(11) NOT NULL,
  `FEC_MOVIMIENTO` date NOT NULL,
  `CONCEPTO` varchar(50) NOT NULL,
  `IMP_MOVIMIENTO` decimal(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `movimiento`:
--   `ID_PRODUCTO_BANCARIO`
--       `producto_bancario` -> `ID_PRODUCTO_BANCARIO`
--

--
-- Truncar tablas antes de insertar `movimiento`
--

TRUNCATE TABLE `movimiento`;
--
-- Volcado de datos para la tabla `movimiento`
--

INSERT INTO `movimiento` (`ID_MOVIMIENTO`, `ID_PRODUCTO_BANCARIO`, `FEC_MOVIMIENTO`, `CONCEPTO`, `IMP_MOVIMIENTO`) VALUES
(1, 4, '2022-10-15', 'COMPRA CARREFOUR VILLAVERDE', '-123.25'),
(2, 4, '2022-10-16', 'COMPRA AMAZON PED. 1134234', '-70.50'),
(4, 1, '2022-10-03', 'INGRESO EN EFECTIVO', '500.00'),
(6, 1, '2022-10-05', 'TRANSFERENCIA RECIBIDA', '1000.00'),
(9, 3, '2022-11-16', 'INGRESO EN EFECTIVO', '20.00'),
(16, 3, '2022-11-01', 'INGRESO EN EFECTIVO', '100.00'),
(18, 9, '2022-10-17', 'INGRESO EN EFECTIVO', '300.00'),
(20, 11, '2022-11-19', 'TRANSFERENCIA RECIBIDA', '500.00'),
(21, 11, '2022-11-21', 'RECIBO UNIÓN FENOSA DISTRIBUCIÓN', '-135.57'),
(22, 2, '2022-11-10', 'NÓMINA', '1423.52'),
(24, 14, '2022-10-23', 'COMPRA CARREFOUR TKT.302883', '-453.23'),
(25, 14, '2022-11-23', 'COMPRA ALCAMPO', '-47.54'),
(26, 14, '2022-11-23', 'DEVOLUCIÓN COMPRA ALCAMPO', '47.54'),
(27, 4, '2022-11-02', 'COMPRA DECATHLON', '-75.25'),
(28, 4, '2022-11-08', 'ITV CTM VALLECAS', '-75.00'),
(30, 18, '2022-10-11', 'COMPRA E.C.I. PRINCESA TKT. 8273720', '-125.80'),
(31, 19, '2022-10-19', 'COMPRA IKEA S.S. DE LOS REYES', '-217.50'),
(32, 19, '2022-10-26', 'COMPRA LEROY MERLÍN RIVAS', '-326.80'),
(33, 18, '2022-11-16', 'COMPRA MERCADONA LAS ROSAS', '-81.23'),
(34, 18, '2022-11-14', 'COMPRA MEDIAMARKT TKT 388229', '-825.33'),
(35, 2, '2022-11-14', 'TRANSFERENCIA EMITIDA ALQUILER', '-800.00'),
(36, 2, '2022-11-14', 'RECIBO UNION FENOSA', '-78.87'),
(37, 2, '2022-11-17', 'RECIBO NATURGY', '-135.35'),
(38, 2, '2022-11-24', 'RETIRADA EFECTIVO', '-390.00'),
(39, 20, '2022-10-04', 'PRÉSTAMO GAR. PERSONAL CONCEDIDO', '-3500.00'),
(40, 1, '2022-10-04', 'PRÉSTAMO GAR. PERSONAL CONCEDIDO', '3500.00'),
(41, 21, '2022-10-16', 'PRÉSTAMO GAR. HIPOTECARIA CONCEDIDO', '-45000.00'),
(42, 9, '2022-10-16', 'PRÉSTAMO GAR. HIPOTECARIA CONCEDIDO', '45000.00'),
(43, 23, '2022-06-03', 'PRÉSTAMO GAR. PERSONAL CONCEDIDO', '-6000.00'),
(44, 22, '2022-06-03', 'PRÉSTAMO GAR. PERSONAL CONCEDIDO', '6000.00'),
(45, 23, '2022-06-30', 'INTERESES PRÉSTAMO', '-16.87'),
(46, 23, '2022-06-30', 'AMORTIZACIÓN PRÉSTAMO', '1811.26'),
(47, 22, '2022-06-30', 'AMORTIZACIÓN PRÉSTAMO', '-1811.26'),
(48, 23, '2022-07-31', 'INTERESES PRÉSTAMO', '-13.14'),
(49, 23, '2022-07-31', 'AMORTIZACIÓN PRÉSTAMO', '2012.51'),
(50, 22, '2022-07-31', 'AMORTIZACIÓN PRÉSTAMO', '-2012.51'),
(51, 23, '2022-08-31', 'INTERESES PRÉSTAMO', '-6.89'),
(52, 23, '2022-08-31', 'AMORTIZACIÓN PRÉSTAMO', '2012.51'),
(53, 22, '2022-08-31', 'AMORTIZACIÓN PRÉSTAMO', '-2012.51'),
(54, 22, '2022-09-01', 'TRANSFERENCIA RECIBIDA', '50.00'),
(55, 23, '2022-09-03', 'INTERESES PRÉSTAMO', '-0.63'),
(56, 23, '2022-09-03', 'AMORTIZACIÓN PRÉSTAMO', '201.25'),
(57, 22, '2022-09-03', 'AMORTIZACIÓN PRÉSTAMO', '-201.25');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `prestamo`
--

DROP TABLE IF EXISTS `prestamo`;
CREATE TABLE `prestamo` (
  `ID_PRESTAMO` int(11) NOT NULL,
  `ID_PRODUCTO_BANCARIO` int(11) NOT NULL,
  `ID_CUENTA_CORRIENTE_PAGO` int(11) NOT NULL,
  `NUMERO_PRESTAMO` decimal(10,0) NOT NULL,
  `TIPO_GARANTIA` enum('PERSONAL','HIPOTECARIA') NOT NULL,
  `TIPO_INTERES` decimal(4,2) NOT NULL,
  `FEC_VENCIMIENTO` date NOT NULL,
  `IMP_CONCEDIDO` decimal(12,2) NOT NULL,
  `IMP_SALDO_PENDIENTE` decimal(12,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `prestamo`:
--   `ID_CUENTA_CORRIENTE_PAGO`
--       `cuenta_corriente` -> `ID_CUENTA_CORRIENTE`
--   `ID_PRODUCTO_BANCARIO`
--       `producto_bancario` -> `ID_PRODUCTO_BANCARIO`
--

--
-- Truncar tablas antes de insertar `prestamo`
--

TRUNCATE TABLE `prestamo`;
--
-- Volcado de datos para la tabla `prestamo`
--

INSERT INTO `prestamo` (`ID_PRESTAMO`, `ID_PRODUCTO_BANCARIO`, `ID_CUENTA_CORRIENTE_PAGO`, `NUMERO_PRESTAMO`, `TIPO_GARANTIA`, `TIPO_INTERES`, `FEC_VENCIMIENTO`, `IMP_CONCEDIDO`, `IMP_SALDO_PENDIENTE`) VALUES
(1, 20, 1, '3000000001', 'PERSONAL', '4.50', '2025-10-04', '-3500.00', '-3500.00'),
(2, 21, 4, '3000000002', 'HIPOTECARIA', '3.50', '2032-10-16', '-45000.00', '-45000.00'),
(3, 23, 11, '3000000003', 'PERSONAL', '3.75', '2022-09-03', '-6000.00', '0.00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto_bancario`
--

DROP TABLE IF EXISTS `producto_bancario`;
CREATE TABLE `producto_bancario` (
  `ID_PRODUCTO_BANCARIO` int(11) NOT NULL,
  `ID_CATALOGO` int(11) NOT NULL,
  `ID_SUCURSAL` int(11) NOT NULL,
  `FEC_APERTURA` date NOT NULL,
  `FEC_CANCELACION` date DEFAULT NULL,
  `FEC_LIQUIDACION` date DEFAULT NULL COMMENT 'Fecha de la última liquidación del producto.'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `producto_bancario`:
--   `ID_CATALOGO`
--       `catalogo` -> `ID_CATALOGO`
--   `ID_SUCURSAL`
--       `sucursal` -> `ID_SUCURSAL`
--

--
-- Truncar tablas antes de insertar `producto_bancario`
--

TRUNCATE TABLE `producto_bancario`;
--
-- Volcado de datos para la tabla `producto_bancario`
--

INSERT INTO `producto_bancario` (`ID_PRODUCTO_BANCARIO`, `ID_CATALOGO`, `ID_SUCURSAL`, `FEC_APERTURA`, `FEC_CANCELACION`, `FEC_LIQUIDACION`) VALUES
(1, 1, 1, '2022-10-03', NULL, NULL),
(2, 1, 2, '2022-10-04', NULL, NULL),
(3, 1, 1, '2022-10-04', NULL, NULL),
(4, 2, 1, '2022-10-01', NULL, NULL),
(9, 1, 1, '2022-10-16', NULL, NULL),
(11, 1, 3, '2022-11-18', NULL, NULL),
(12, 1, 13, '2022-11-21', '2022-11-21', NULL),
(13, 1, 4, '2022-11-22', '2022-11-22', NULL),
(14, 2, 3, '2022-10-15', NULL, NULL),
(15, 2, 1, '2022-10-20', '2022-10-20', NULL),
(16, 1, 13, '2022-11-23', '2022-11-23', NULL),
(17, 1, 6, '2022-11-23', '2022-11-23', NULL),
(18, 2, 2, '2022-10-04', NULL, NULL),
(19, 2, 1, '2022-10-16', NULL, NULL),
(20, 3, 6, '2022-10-04', NULL, NULL),
(21, 3, 1, '2022-10-16', NULL, NULL),
(22, 1, 13, '2022-06-03', NULL, NULL),
(23, 3, 13, '2022-06-03', '2022-09-03', '2022-09-03');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sucursal`
--

DROP TABLE IF EXISTS `sucursal`;
CREATE TABLE `sucursal` (
  `ID_SUCURSAL` int(11) NOT NULL,
  `COD_SUCURSAL` int(4) NOT NULL,
  `NOMBRE_SUCURSAL` varchar(100) NOT NULL,
  `ID_DIRECCION` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `sucursal`:
--   `ID_DIRECCION`
--       `direccion` -> `ID_DIRECCION`
--   `ID_DIRECCION`
--       `direccion` -> `ID_DIRECCION`
--

--
-- Truncar tablas antes de insertar `sucursal`
--

TRUNCATE TABLE `sucursal`;
--
-- Volcado de datos para la tabla `sucursal`
--

INSERT INTO `sucursal` (`ID_SUCURSAL`, `COD_SUCURSAL`, `NOMBRE_SUCURSAL`, `ID_DIRECCION`) VALUES
(1, 1000, 'MADRID - OFICINA PRINCIPAL', 1),
(2, 2001, 'BARCELONA - AG. 1 (TETUAN)', 4),
(3, 1001, 'MADRID - AG. 1 (VALLECAS)', 6),
(4, 2000, 'BARCELONA - OFICINA PRINCIPAL', 5),
(6, 3000, 'VALENCIA - OFICINA PRINCIPAL', 7),
(13, 4020, 'CÁDIZ - CHICLANA DE LA FRONTERA', 11);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarjeta_credito`
--

DROP TABLE IF EXISTS `tarjeta_credito`;
CREATE TABLE `tarjeta_credito` (
  `ID_TARJETA_CREDITO` int(11) NOT NULL,
  `ID_PRODUCTO_BANCARIO` int(11) NOT NULL,
  `ID_CUENTA_CORRIENTE_PAGO` int(11) NOT NULL,
  `NUMERO_TARJETA` decimal(10,0) NOT NULL COMMENT 'Número interno de tarjeta',
  `PAN` decimal(16,0) NOT NULL COMMENT 'PERSONAL ACCOUNT NUMBER',
  `TIPO_INTERES` decimal(4,2) NOT NULL,
  `IMP_LIMITE_TARJETA` decimal(12,2) NOT NULL,
  `IMP_SALDO_PENDIENTE` decimal(12,2) NOT NULL,
  `FORMA_PAGO` enum('CONTADO','APLAZADO') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `tarjeta_credito`:
--   `ID_CUENTA_CORRIENTE_PAGO`
--       `cuenta_corriente` -> `ID_CUENTA_CORRIENTE`
--   `ID_PRODUCTO_BANCARIO`
--       `producto_bancario` -> `ID_PRODUCTO_BANCARIO`
--

--
-- Truncar tablas antes de insertar `tarjeta_credito`
--

TRUNCATE TABLE `tarjeta_credito`;
--
-- Volcado de datos para la tabla `tarjeta_credito`
--

INSERT INTO `tarjeta_credito` (`ID_TARJETA_CREDITO`, `ID_PRODUCTO_BANCARIO`, `ID_CUENTA_CORRIENTE_PAGO`, `NUMERO_TARJETA`, `PAN`, `TIPO_INTERES`, `IMP_LIMITE_TARJETA`, `IMP_SALDO_PENDIENTE`, `FORMA_PAGO`) VALUES
(1, 4, 1, '4000000001', '5013840000000011', '20.00', '-2000.00', '-344.00', 'CONTADO'),
(2, 14, 6, '4000000002', '5013840000000029', '24.00', '-2000.00', '-453.23', 'CONTADO'),
(3, 15, 2, '4000000003', '5013840000000037', '20.00', '-2500.00', '0.00', 'CONTADO'),
(4, 18, 3, '4000000004', '5013840000000045', '24.00', '-2000.00', '-1032.36', 'APLAZADO'),
(5, 19, 4, '4000000005', '5013840000000052', '23.50', '-1500.00', '-544.30', 'APLAZADO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

DROP TABLE IF EXISTS `usuario`;
CREATE TABLE `usuario` (
  `ID_USUARIO` int(11) NOT NULL,
  `COD_USUARIO` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL,
  `PASSWORD` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--
-- RELACIONES PARA LA TABLA `usuario`:
--

--
-- Truncar tablas antes de insertar `usuario`
--

TRUNCATE TABLE `usuario`;
--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`ID_USUARIO`, `COD_USUARIO`, `PASSWORD`) VALUES
(1, 'jsanchezp', 'test'),
(2, 'fjimenezd', 'test'),
(3, 'pmigas', 'test'),
(4, 'opujolm', 'test'),
(10, 'test', 'test'),
(11, 'emustermann', 'test'),
(17, 'jlopezm', 'test'),
(18, 'aestebanc', 'test'),
(19, 'ptrudeau', 'test'),
(31, 'avalleh', 'test'),
(32, 'cnatural', 'test');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `aval`
--
ALTER TABLE `aval`
  ADD PRIMARY KEY (`ID_AVAL`),
  ADD UNIQUE KEY `ID_PRODUCTO_BANCARIO_IND` (`ID_PRODUCTO_BANCARIO`);

--
-- Indices de la tabla `catalogo`
--
ALTER TABLE `catalogo`
  ADD PRIMARY KEY (`ID_CATALOGO`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD PRIMARY KEY (`ID_CLIENTE`),
  ADD UNIQUE KEY `CLAVE_IDENTIFICACION_IND` (`CLAVE_IDENTIFICACION`),
  ADD KEY `FK_CLIENTE_DIRECCION` (`ID_DIRECCION`),
  ADD KEY `FK_CLIENTE_EMPLEADO` (`ID_EMPLEADO`),
  ADD KEY `FK_CLIENTE_USUARIO` (`ID_USUARIO`);

--
-- Indices de la tabla `cliente_producto_bancario`
--
ALTER TABLE `cliente_producto_bancario`
  ADD PRIMARY KEY (`ID_CLIENTE`,`ID_PRODUCTO_BANCARIO`),
  ADD KEY `FK_PRODUCTO_BANCARIO_CPB` (`ID_PRODUCTO_BANCARIO`);

--
-- Indices de la tabla `cuenta_corriente`
--
ALTER TABLE `cuenta_corriente`
  ADD PRIMARY KEY (`ID_CUENTA_CORRIENTE`),
  ADD UNIQUE KEY `ID_PRODUCTO_BANCARIO_IND` (`ID_PRODUCTO_BANCARIO`),
  ADD UNIQUE KEY `NUMERO_CUENTA_IND` (`NUMERO_CUENTA`),
  ADD UNIQUE KEY `IBAN_IND` (`IBAN`) USING BTREE;

--
-- Indices de la tabla `direccion`
--
ALTER TABLE `direccion`
  ADD PRIMARY KEY (`ID_DIRECCION`);

--
-- Indices de la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD PRIMARY KEY (`ID_EMPLEADO`),
  ADD KEY `FK_EMPLEADO_SUCURSAL` (`ID_SUCURSAL`),
  ADD KEY `FK_EMPLEADO_USUARIO` (`ID_USUARIO`);

--
-- Indices de la tabla `movimiento`
--
ALTER TABLE `movimiento`
  ADD PRIMARY KEY (`ID_MOVIMIENTO`),
  ADD KEY `FK_MOVIMIENTO_PRODUCTO_BANCARIO` (`ID_PRODUCTO_BANCARIO`);

--
-- Indices de la tabla `prestamo`
--
ALTER TABLE `prestamo`
  ADD PRIMARY KEY (`ID_PRESTAMO`),
  ADD UNIQUE KEY `ID_PRODUCTO_BANCARIO_IND` (`ID_PRODUCTO_BANCARIO`),
  ADD UNIQUE KEY `NUMERO_PRESTAMO_IND` (`NUMERO_PRESTAMO`),
  ADD KEY `FK_PRESTAMO_CUENTA_CORRIENTE` (`ID_CUENTA_CORRIENTE_PAGO`);

--
-- Indices de la tabla `producto_bancario`
--
ALTER TABLE `producto_bancario`
  ADD PRIMARY KEY (`ID_PRODUCTO_BANCARIO`),
  ADD KEY `FK_PRODUCTO_BANCARIO_CATALOGO` (`ID_CATALOGO`),
  ADD KEY `FK_PRODUCTO_BANCARIO_SUCURSAL` (`ID_SUCURSAL`);

--
-- Indices de la tabla `sucursal`
--
ALTER TABLE `sucursal`
  ADD PRIMARY KEY (`ID_SUCURSAL`),
  ADD UNIQUE KEY `COD_SUCURSAL_IND` (`COD_SUCURSAL`),
  ADD KEY `FK_SUCURSAL_DIRECCION` (`ID_DIRECCION`);

--
-- Indices de la tabla `tarjeta_credito`
--
ALTER TABLE `tarjeta_credito`
  ADD PRIMARY KEY (`ID_TARJETA_CREDITO`),
  ADD UNIQUE KEY `ID_PRODUCTO_BANCARIO_IND` (`ID_PRODUCTO_BANCARIO`),
  ADD UNIQUE KEY `NUMERO_TARJETA_IND` (`NUMERO_TARJETA`),
  ADD UNIQUE KEY `PAN_IND` (`PAN`),
  ADD KEY `FK_TARJETA_CREDITO_CUENTA_CORRIENTE` (`ID_CUENTA_CORRIENTE_PAGO`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`ID_USUARIO`),
  ADD UNIQUE KEY `COD_USUARIO_IND` (`COD_USUARIO`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `aval`
--
ALTER TABLE `aval`
  MODIFY `ID_AVAL` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `catalogo`
--
ALTER TABLE `catalogo`
  MODIFY `ID_CATALOGO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
  MODIFY `ID_CLIENTE` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `cuenta_corriente`
--
ALTER TABLE `cuenta_corriente`
  MODIFY `ID_CUENTA_CORRIENTE` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT de la tabla `direccion`
--
ALTER TABLE `direccion`
  MODIFY `ID_DIRECCION` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT de la tabla `empleado`
--
ALTER TABLE `empleado`
  MODIFY `ID_EMPLEADO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `movimiento`
--
ALTER TABLE `movimiento`
  MODIFY `ID_MOVIMIENTO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- AUTO_INCREMENT de la tabla `prestamo`
--
ALTER TABLE `prestamo`
  MODIFY `ID_PRESTAMO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `producto_bancario`
--
ALTER TABLE `producto_bancario`
  MODIFY `ID_PRODUCTO_BANCARIO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT de la tabla `sucursal`
--
ALTER TABLE `sucursal`
  MODIFY `ID_SUCURSAL` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `tarjeta_credito`
--
ALTER TABLE `tarjeta_credito`
  MODIFY `ID_TARJETA_CREDITO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `ID_USUARIO` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `aval`
--
ALTER TABLE `aval`
  ADD CONSTRAINT `FK_AVAL_PRODUCTO_BANCARIO` FOREIGN KEY (`ID_PRODUCTO_BANCARIO`) REFERENCES `producto_bancario` (`ID_PRODUCTO_BANCARIO`);

--
-- Filtros para la tabla `cliente`
--
ALTER TABLE `cliente`
  ADD CONSTRAINT `FK_CLIENTE_DIRECCION` FOREIGN KEY (`ID_DIRECCION`) REFERENCES `direccion` (`ID_DIRECCION`),
  ADD CONSTRAINT `FK_CLIENTE_EMPLEADO` FOREIGN KEY (`ID_EMPLEADO`) REFERENCES `empleado` (`ID_EMPLEADO`),
  ADD CONSTRAINT `FK_CLIENTE_USUARIO` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuario` (`ID_USUARIO`);

--
-- Filtros para la tabla `cliente_producto_bancario`
--
ALTER TABLE `cliente_producto_bancario`
  ADD CONSTRAINT `FK_CLIENTE_CPB` FOREIGN KEY (`ID_CLIENTE`) REFERENCES `cliente` (`ID_CLIENTE`),
  ADD CONSTRAINT `FK_PRODUCTO_BANCARIO_CPB` FOREIGN KEY (`ID_PRODUCTO_BANCARIO`) REFERENCES `producto_bancario` (`ID_PRODUCTO_BANCARIO`);

--
-- Filtros para la tabla `cuenta_corriente`
--
ALTER TABLE `cuenta_corriente`
  ADD CONSTRAINT `FK_CUENTA_CORRIENTE_PRODUCTO_BANCARIO` FOREIGN KEY (`ID_PRODUCTO_BANCARIO`) REFERENCES `producto_bancario` (`ID_PRODUCTO_BANCARIO`);

--
-- Filtros para la tabla `empleado`
--
ALTER TABLE `empleado`
  ADD CONSTRAINT `FK_EMPLEADO_SUCURSAL` FOREIGN KEY (`ID_SUCURSAL`) REFERENCES `sucursal` (`ID_SUCURSAL`),
  ADD CONSTRAINT `FK_EMPLEADO_USUARIO` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuario` (`ID_USUARIO`);

--
-- Filtros para la tabla `movimiento`
--
ALTER TABLE `movimiento`
  ADD CONSTRAINT `FK_MOVIMIENTO_PRODUCTO_BANCARIO` FOREIGN KEY (`ID_PRODUCTO_BANCARIO`) REFERENCES `producto_bancario` (`ID_PRODUCTO_BANCARIO`);

--
-- Filtros para la tabla `prestamo`
--
ALTER TABLE `prestamo`
  ADD CONSTRAINT `FK_PRESTAMO_CUENTA_CORRIENTE` FOREIGN KEY (`ID_CUENTA_CORRIENTE_PAGO`) REFERENCES `cuenta_corriente` (`ID_CUENTA_CORRIENTE`),
  ADD CONSTRAINT `FK_PRESTAMO_PRODUCTO_BANCARIO` FOREIGN KEY (`ID_PRODUCTO_BANCARIO`) REFERENCES `producto_bancario` (`ID_PRODUCTO_BANCARIO`);

--
-- Filtros para la tabla `producto_bancario`
--
ALTER TABLE `producto_bancario`
  ADD CONSTRAINT `FK_PRODUCTO_BANCARIO_CATALOGO` FOREIGN KEY (`ID_CATALOGO`) REFERENCES `catalogo` (`ID_CATALOGO`),
  ADD CONSTRAINT `FK_PRODUCTO_BANCARIO_SUCURSAL` FOREIGN KEY (`ID_SUCURSAL`) REFERENCES `sucursal` (`ID_SUCURSAL`);

--
-- Filtros para la tabla `sucursal`
--
ALTER TABLE `sucursal`
  ADD CONSTRAINT `FK_SUCURSAL_DIRECCION` FOREIGN KEY (`ID_DIRECCION`) REFERENCES `direccion` (`ID_DIRECCION`),
  ADD CONSTRAINT `sucursal_ibfk_1` FOREIGN KEY (`ID_DIRECCION`) REFERENCES `direccion` (`ID_DIRECCION`);

--
-- Filtros para la tabla `tarjeta_credito`
--
ALTER TABLE `tarjeta_credito`
  ADD CONSTRAINT `FK_TARJETA_CREDITO_CUENTA_CORRIENTE` FOREIGN KEY (`ID_CUENTA_CORRIENTE_PAGO`) REFERENCES `cuenta_corriente` (`ID_CUENTA_CORRIENTE`),
  ADD CONSTRAINT `FK_TARJETA_CREDITO_PRODUCTO_BANCARIO` FOREIGN KEY (`ID_PRODUCTO_BANCARIO`) REFERENCES `producto_bancario` (`ID_PRODUCTO_BANCARIO`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
