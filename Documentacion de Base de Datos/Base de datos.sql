CREATE DATABASE tiendaAnimati_BD;
USE tiendaAnimati_BD;

CREATE TABLE `Categoria` (
  `Nro_categoria` int PRIMARY KEY AUTO_INCREMENT,
  `Nombre_categoria` varchar(30),
  `Descripcion_categoria` varchar(30)
);

INSERT INTO Categoria(Nro_categoria, Nombre_categoria, Descripcion_categoria)
VALUES (1, "Papeleria", "hojas y cuadernos"), (2, "Accesorios", "mucha variedad de Accesorios");


/*------------------------------------------------------------------------------------------*/
CREATE TABLE `Producto` (
  `ID_Producto` int AUTO_INCREMENT PRIMARY KEY NOT NULL,
  `Nombre_Producto` VARCHAR(25),
  `Precio` int,
  `Stock` int,
  `Nro_categoria` int,
  FOREIGN KEY (`Nro_categoria`) REFERENCES Categoria(`Nro_categoria`)
);

INSERT INTO Producto (Nombre_Producto, Precio, Stock, `Nro_categoria`)
VALUES 
("Posters", 200, 30, 1),
("Polaroid", 50, 400, 2),
("Vasos", 1000, 5, 1);


/*------------------------------------------------------------------------------------------*/
CREATE TABLE `Cliente` (
  `DNI` int (8) PRIMARY KEY NOT NULL,
  `Correo_Electronico` VARCHAR(25),
  `Nombre` varchar(30),
  `Apellido` varchar(30),
  `Direccion` varchar(40),
  `Telefono` int (20)
);

INSERT INTO Cliente (DNI, Correo_Electronico, Nombre, Apellido, Direccion, Telefono)
VALUES
 (45345678, "juanito_perez@gmail.com", "Juan", "Perez", "Velez Sarfield 45", 351405598),
 (40456789, "maria_diaz@gmail.com", "Maria", "Diaz", "Av Color 1500", 351579913),
 (35567019, "esteban_lopez@gmail.com", "Esteban", "Lopez", "Simon Bolivar 560", 351506798);

/*------------------------------------------------------------------------------------------*/
CREATE TABLE `Pedidos` (
  `Nro_pedido` int AUTO_INCREMENT PRIMARY KEY,
  `ID_Producto` int,
  `DNI` int,
  `Cantidad` int,
  CONSTRAINT `fk_producto` FOREIGN KEY (`ID_Producto`) REFERENCES `Producto`(`ID_Producto`),
  CONSTRAINT `fk_cliente` FOREIGN KEY (`DNI`) REFERENCES `Cliente`(`DNI`)
);

INSERT INTO Pedidos (Nro_pedido, ID_Producto, DNI, Cantidad)
VALUES
(1, 1, 35567019, 3),
(2, 2, 40456789, 1),
(3, 3, 45345678, 2);


/*------------------------------------------------------------------------------------------*/
CREATE TABLE `Usuario` (
  `ID_Usuario` int PRIMARY KEY auto_increment,
  `Nombre_Usuario` varchar (30),
  `Correo_Usuario` varchar (30),
  `Numero_Telefono` varchar (30),
  `password` varchar (30)
);

INSERT INTO Usuario (ID_Usuario, Nombre_Usuario, Correo_Usuario, Numero_Telefono, password )
VALUES ( 1, "Pedro", "Pepedro@hotmail.com", 351876512, "1234"), (2, "naty", "naty02@hotmail.com", 2451123222 ,"1233" );


/*
 1) corregi nombre de atributos, la tabla producto guardara el id de la categoria y no un nombre para hacer la relacion. 
   2) cree tabla categoria.
   3) quite en la tabla Cliente el `Nro_pedido`con clave foranea a Pedido, ya que cuando carguemos el pedido traera el cliente y no al reves
   4) agregue clave foranea a pedido , con cliente a traves de su DNI.
   5) agregue los atributo fecha y cantidad a la tabla pedidos.
   6) Agregue tabla usuario.

*/