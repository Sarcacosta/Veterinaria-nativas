-- Ejecutar sobre veterinaria. Si la consulta muestra duplicados, resuélvelos antes de continuar.
SELECT username, COUNT(*) AS cantidad FROM usuario GROUP BY username HAVING COUNT(*) > 1;

INSERT INTO rol (nombre, descripcion)
SELECT 'Propietario', 'Titular de mascotas'
WHERE NOT EXISTS (SELECT 1 FROM rol WHERE nombre = 'Propietario');

-- Se puede ejecutar nuevamente si el índice ya existe con este nombre.
SET @crear_indice = IF(EXISTS(
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'usuario' AND index_name = 'uk_usuario_username'
), 'SELECT 1', 'ALTER TABLE usuario ADD CONSTRAINT uk_usuario_username UNIQUE (username)');
PREPARE migracion FROM @crear_indice;
EXECUTE migracion;
DEALLOCATE PREPARE migracion;
