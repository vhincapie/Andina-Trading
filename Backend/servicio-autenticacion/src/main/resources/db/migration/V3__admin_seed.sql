-- contrasena: Admin#2025

INSERT INTO usuarios (correo, contrasena_hash, rol_id)
VALUES (
           'admin@gmail.com',
           '$2a$12$VkQzHQJUYvc5SnMdbaaf6u9U8V5x.Chd0TPNds5d.K1EM4inzwmgm',
           (SELECT id FROM roles WHERE nombre='ADMIN')
       );