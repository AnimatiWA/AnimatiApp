
from django.db import models


# Create your models here.

from datetime import datetime
from django.db import models
from django.utils import timezone
from django.contrib.auth.models import BaseUserManager, AbstractBaseUser, PermissionsMixin
from django.contrib.auth import get_user_model
from datetime import timedelta

# Create your models here.

class UserManager(BaseUserManager):
    def _create_user(self, username, email, first_name,last_name, password, is_staff, is_superuser, **extra_fields):
        user = self.model(
            username = username,
            email = email,
            first_name = first_name,
            last_name = last_name,
            is_staff = is_staff,
            is_superuser = is_superuser,
            **extra_fields
        )
        user.set_password(password)
        user.save(using=self.db)
        return user

    def create_user(self, username, email, first_name, last_name, password=None, **extra_fields):
        return self._create_user(username, email, first_name, last_name, password, False, False, **extra_fields)

    def create_superuser(self, username, email, first_name, last_name, password=None, **extra_fields):
        return self._create_user(username, email, first_name, last_name, password, True, True, **extra_fields)

class User(AbstractBaseUser, PermissionsMixin):
    id = models.BigAutoField(primary_key=True,)
    username = models.CharField(max_length = 255, unique = True)
    email = models.EmailField('Correo Electrónico',max_length = 255, unique = True,)
    first_name = models.CharField('Nombres', max_length = 255, blank = True, null = True)
    last_name = models.CharField('Apellidos', max_length = 255, blank = True, null = True)
    image = models.ImageField('Imagen de perfil', upload_to='perfil/', max_length=255, null=True, blank = True)
    is_active = models.BooleanField(default = True)
    is_staff = models.BooleanField(default = True)
    objects = UserManager()

    class Meta:
        verbose_name = 'Usuario'
        verbose_name_plural = 'Usuarios'

    USERNAME_FIELD = 'username'
    REQUIRED_FIELDS = ['email','first_name','last_name', 'password']

    def __str__(self):
        return f'{self.username} {self.last_name}'
    

class Categoria(models.Model):
    Id_Categoria = models.AutoField(primary_key=True)
    Nombre_Categoria = models.CharField(max_length=100, blank=False)
    Descripcion_Categoria = models.CharField(max_length=100, blank=False)
    class Meta:
        db_table = 'categoria'
        verbose_name = 'Categoria'
        verbose_name_plural = 'Categorias'
    def __unicode__(self):
        return self.Nombre_Categoria
    def __str__(self):
        return self.Nombre_Categoria


class Producto(models.Model):

    Codigo_Producto = models.AutoField(primary_key=True)
    Nombre_Producto = models.CharField(max_length=70, blank=False)
    Imagen = models.CharField(max_length=250)
    Precio = models.DecimalField(blank=False, default=2000, decimal_places=2, max_digits=10)
    Stock = models.PositiveIntegerField(blank=False, default=0)
    Id_Categoria = models.ForeignKey(Categoria, to_field='Id_Categoria', on_delete=models.CASCADE)
    class Meta:
        db_table = 'producto'
        verbose_name = 'Producto'
        verbose_name_plural = 'Productos'

    def __unicode__(self):
        return self.Nombre_Producto
    def __str__(self):
        return self.Nombre_Producto  

    @property
    def ImagenURL(self):
        try:
            url = self.Imagen.url
        except:
            url = ''
        return url


class Carrito(models.Model):
    Usuario = models.ForeignKey(User, to_field='id', on_delete=models.CASCADE)
    Creado = models.DateTimeField(default=timezone.now)
    is_active = models.BooleanField(default = True)
    Deshabilitado = models.DateTimeField(null=True, blank=True)
    confirmado = models.BooleanField(default = False)

class ProductoCarrito(models.Model):
    Codigo = models.ForeignKey(Producto, on_delete=models.CASCADE)
    Cantidad = models.IntegerField(default=1)
    Precio = models.FloatField(blank=True, editable=False)
    Carrito = models.ForeignKey(Carrito, on_delete=models.CASCADE)

    def save(self, *args, **kwargs):
        if(self.Codigo):
            self.Precio = self.Codigo.Precio * self.Cantidad
        super().save(*args, **kwargs)

    def __str__(self):
        return f"{self.Codigo} - {self.Cantidad}" 


class Cliente(models.Model):
    
    DNI = models.IntegerField(primary_key=True)
    Nombre = models.CharField(max_length=100, blank=False)
    Apellido = models.CharField(max_length=100, blank=False)
    Correo_Electronico = models.CharField(max_length=130, blank=False)
    Direccion = models.CharField(max_length=120, blank=False)
    Telefono = models.IntegerField(blank=False, default=2000)
    Id_usuario = models.ForeignKey(User, to_field='id', on_delete=models.CASCADE)

    class Meta:
        db_table = 'cliente'
        verbose_name = 'Cliente'
        verbose_name_plural = 'Clientes'

    def __unicode__(self):
        return self.Nombre
    def __str__(self):
        return self.Nombre
     
class CorreoContacto(models.Model):

    nombre = models.CharField(max_length=100, blank=False)
    email = models.EmailField()
    mensaje = models.TextField()
    creado = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Mensaje de {self.nombre}"
    
class PasswordResetToken(models.Model):
    user = models.ForeignKey(User, on_delete=models.CASCADE)
    token = models.CharField(max_length=255, unique=True)
    created_at = models.DateTimeField(auto_now_add=True)
    expires_at = models.DateTimeField()

    def save(self, *args, **kwargs):
        if not self.expires_at:
            self.expires_at = timezone.now() + timedelta(hours=1)  # El token expira en 1 hora
        super().save(*args, **kwargs)

    def is_expired(self):
        return timezone.now() > self.expires_at
