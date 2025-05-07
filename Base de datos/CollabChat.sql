-- Tabla de usuarios
CREATE TABLE `usuarios` (
    `id_usuario` INT NOT NULL AUTO_INCREMENT,
    `nombre` VARCHAR(255) NOT NULL,
    `contraseña` VARCHAR(255) NOT NULL,
    `fecha_registro` DATETIME NOT NULL,
    PRIMARY KEY (`id_usuario`)
);

-- Tabla de grupos
CREATE TABLE `grupos` (
    `id_grupo` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `nombre` VARCHAR(255) NOT NULL,
    `descripcion` TEXT NOT NULL,
    `fecha_creacion` DATETIME NOT NULL,
    PRIMARY KEY (`id_grupo`)
);

-- Tabla de datos personales (después de usuarios)
CREATE TABLE `datos_personales` (
    `id_usuario` INT NOT NULL,
    `edad` INT NOT NULL,
    `ciudad_nacimiento` VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id_usuario`),
    FOREIGN KEY (`id_usuario`) REFERENCES `usuarios`(`id_usuario`)
);

-- Tabla de usuarios_grupos (después de usuarios y grupos)
CREATE TABLE `usuarios_grupos` (
    `id_usuario` INT NOT NULL,
    `id_grupo` INT UNSIGNED NOT NULL,
    `fecha_union` DATETIME NOT NULL,
    PRIMARY KEY (`id_usuario`, `id_grupo`),
    FOREIGN KEY (`id_usuario`) REFERENCES `usuarios`(`id_usuario`),
    FOREIGN KEY (`id_grupo`) REFERENCES `grupos`(`id_grupo`)
);

-- Tabla de mensajes (después de usuarios y grupos)
CREATE TABLE `mensajes` (
    `id_mensaje` INT NOT NULL AUTO_INCREMENT,
    `id_usuario` INT NOT NULL,
    `id_grupo` INT UNSIGNED NOT NULL,
    `contenido` TEXT NOT NULL,
    `fecha_envio` DATETIME NOT NULL,
    PRIMARY KEY (`id_mensaje`),
    FOREIGN KEY (`id_usuario`) REFERENCES `usuarios`(`id_usuario`),
    FOREIGN KEY (`id_grupo`) REFERENCES `grupos`(`id_grupo`)
);