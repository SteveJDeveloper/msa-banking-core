CREATE SCHEMA IF NOT EXISTS bank;

CREATE TABLE IF NOT EXISTS bank.clientes (
    id bigint NOT NULL,
    nombre character varying(100) NOT NULL,
    genero character varying(9) NOT NULL,
    edad integer NOT NULL,
    identificacion character varying(10) NOT NULL,
    direccion character varying(255) NOT NULL,
    telefono character varying(9) NOT NULL,
    contrasenia character varying NOT NULL,
    estado boolean NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT now() NOT NULL,
    fecha_actualizacion timestamp without time zone
);

CREATE TABLE IF NOT EXISTS bank.cuentas (
    id bigint NOT NULL,
    numero_cuenta bigint NOT NULL,
    tipo_cuenta character varying(20) NOT NULL,
    balance numeric(15,2) NOT NULL,
    estado boolean NOT NULL,
    id_cliente bigint NOT NULL,
    fecha_creacion timestamp without time zone DEFAULT now() NOT NULL,
    fecha_actualizacion timestamp without time zone
);

ALTER TABLE bank.cuentas ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME bank.cuentas_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


CREATE TABLE IF NOT EXISTS bank.movimientos (
    id bigint NOT NULL,
    id_cuenta bigint NOT NULL,
    tipo_movimiento bit varying(20) NOT NULL,
    valor numeric(15,2) NOT NULL,
    balance numeric(15,2) NOT NULL,
    fecha timestamp with time zone NOT NULL,
    fecha_creacion timestamp with time zone DEFAULT now() NOT NULL,
    fecha_actualizacion timestamp without time zone
);

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

SELECT pg_catalog.setval('bank.cuentas_id_seq', 1, false);
SELECT pg_catalog.setval('bank.movimientos_id_seq', 1, false);
SELECT pg_catalog.setval('bank.persona_id_seq', 1, true);

ALTER TABLE ONLY bank.cuentas
    ADD CONSTRAINT pk_cuentas PRIMARY KEY (id);

ALTER TABLE ONLY bank.movimientos
    ADD CONSTRAINT pk_movimientos PRIMARY KEY (id);

ALTER TABLE ONLY bank.clientes
    ADD CONSTRAINT pk_personas PRIMARY KEY (id);

ALTER TABLE ONLY bank.clientes
    ADD CONSTRAINT unique_identificacion UNIQUE (identificacion);

ALTER TABLE ONLY bank.cuentas
    ADD CONSTRAINT unique_numero_cuenta UNIQUE (numero_cuenta);

ALTER TABLE ONLY bank.cuentas
    ADD CONSTRAINT fk_cuentas_cliente FOREIGN KEY (id_cliente) REFERENCES bank.clientes(id);

ALTER TABLE ONLY bank.movimientos
    ADD CONSTRAINT fk_movimientos_cuenta FOREIGN KEY (id_cuenta) REFERENCES bank.cuentas(id) ON DELETE CASCADE;
