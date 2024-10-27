
USE tiendaAnimati_BD;

INSERT INTO Categoria(Nombre_categoria, Descripcion_categoria)
VALUES ("Papeleria", "hojas y cuadernos"), 
( "Accesorios", "mucha variedad de Accesorios"),
("Regalos", "articulos pensados para la regaleria"),
("Descartables", "articulos descartables y desechables");

INSERT INTO Producto (Codigo_Producto, Nombre_Producto, Imagen, Precio, Stock, Nro_categoria)
VALUES 
(223344, "Posters","https://res.cloudinary.com/dlku7pic1/image/upload/v1717468745/prod-4_tsbbed.jpg", 200, 30, 1),
(1122244, "Polaroid","https://res.cloudinary.com/dlku7pic1/image/upload/v1717468746/prod-5_nycpqe.jpg", 50, 400, 2),
(34253, "Llaveros","https://res.cloudinary.com/dlku7pic1/image/upload/v1717468745/prod-2_d9fqtw.jpg", 1000, 5, 1),
(87654, "Cubecraft", "https://res.cloudinary.com/dlku7pic1/image/upload/v1717468745/prod-3_vd3gqq.jpg", 222, 21, 3);


#esta fila insertar una vez que el ausuario ya se haya logeado
#el usuario admin es: Animatiweb y la contraseña es: 1234
INSERT INTO Cliente (DNI, Correo_Electronico, Nombre, Apellido, Direccion, Telefono, Id_usuario)
VALUES
 (45345678, "juanito_perez@gmail.com", "Juan", "Perez", "Velez Sarfield 45", 351405598, 1),
 (40456789, "maria_diaz@gmail.com", "Maria", "Diaz", "Av Color 1500", 351579913, 2),
 (35567019, "esteban_lopez@gmail.com", "Esteban", "Lopez", "Simon Bolivar 560", 351506798, 3);