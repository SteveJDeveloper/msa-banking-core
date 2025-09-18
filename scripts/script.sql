--
-- PostgreSQL database
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

CREATE SCHEMA bank;

ALTER SCHEMA bank OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

CREATE TABLE bank.clientes (
    id bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    genero character varying(9) NOT NULL,
    edad integer NOT NULL,
    identificacion character varying(10) NOT NULL,
    direccion character varying(255) NOT NULL,
    telefono character varying(10) NOT NULL,
    contrasenia character varying NOT NULL,
    estado boolean NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT now() NOT NULL,
    fecha_actualizacion timestamp without time zone
);

ALTER TABLE bank.clientes OWNER TO postgres;

CREATE TABLE bank.cuentas (
    id bigint NOT NULL,
    numero_cuenta bigint NOT NULL,
    tipo_cuenta character varying(20) NOT NULL,
    balance numeric(15,2) NOT NULL,
    estado boolean NOT NULL,
    id_cliente bigint NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT now() NOT NULL,
    fecha_actualizacion timestamp without time zone
);


ALTER TABLE bank.cuentas OWNER TO postgres;

ALTER TABLE bank.cuentas ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME bank.cuentas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

CREATE TABLE bank.movimientos (
    id bigint NOT NULL,
    id_cuenta bigint NOT NULL,
    tipo_movimiento character varying(20) NOT NULL,
    valor numeric(15,2) NOT NULL,
    balance numeric(15,2) NOT NULL,
    fecha timestamp with time zone NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now() NOT NULL,
    fecha_actualizacion timestamp without time zone
);

ALTER TABLE bank.movimientos OWNER TO postgres;

ALTER TABLE bank.movimientos ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME bank.movimientos_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

ALTER TABLE bank.clientes ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME bank.persona_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);

INSERT INTO bank.clientes (nombre, genero, edad, identificacion, direccion, telefono, contrasenia, estado)
VALUES
('Jose Lema', 'MASCULINO', 35, '1726725301', 'Otavalo sn y principal', '098254785', '1234', true),
('Marianela Montalvo', 'FEMENINO', 28, '1726725302', 'Amazonas y NNUU', '097548965', '5678', true),
('Juan Osorio', 'MASCULINO', 32, '1726725303', '13 junio y Equinoccial', '098874587', '1245', true);

-- Jose Lema
INSERT INTO bank.cuentas (numero_cuenta, tipo_cuenta, balance, estado, id_cliente)
VALUES (478758, 'AHORROS', 2000.00, true, 1);

-- Marianela Montalvo
INSERT INTO bank.cuentas (numero_cuenta, tipo_cuenta, balance, estado, id_cliente)
VALUES (225487, 'CORRIENTE', 100.00, true, 2);

-- Juan Osorio
INSERT INTO bank.cuentas (numero_cuenta, tipo_cuenta, balance, estado, id_cliente)
VALUES (495878, 'AHORROS', 0.00, true, 3);

-- Marianela Montalvo (segunda cuenta)
INSERT INTO bank.cuentas (numero_cuenta, tipo_cuenta, balance, estado, id_cliente)
VALUES (496825, 'AHORROS', 540.00, true, 2);

-- Nueva cuenta Corriente de Jose Lema
INSERT INTO bank.cuentas (numero_cuenta, tipo_cuenta, balance, estado, id_cliente)
VALUES (585545, 'CORRIENTE', 1000.00, true, 1);

INSERT INTO bank.movimientos (id_cuenta, tipo_movimiento, valor, balance, fecha)
VALUES (1, 'DEBITO', 575.00, 1425.00, now());

INSERT INTO bank.movimientos (id_cuenta, tipo_movimiento, valor, balance, fecha)
VALUES (2, 'CREDITO', 600.00, 700.00, now());

INSERT INTO bank.movimientos (id_cuenta, tipo_movimiento, valor, balance, fecha)
VALUES (4, 'DEBITO', 540.00, 0.00, now());
