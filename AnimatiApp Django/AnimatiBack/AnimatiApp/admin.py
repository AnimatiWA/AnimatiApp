from django.contrib import admin
from django.contrib.auth import get_user_model
from django.contrib.auth.admin import UserAdmin
from .models import *

# Register your models here.


class CategoriaAdmin(admin.ModelAdmin):
    list_display =('Nombre_Categoria', 'Descripcion_Categoria')

class ProductoAdmin(admin.ModelAdmin):
    list_display = ('Nombre_Producto', 'Precio', 'Stock', 'Id_Categoria')

class ClienteAdmin(admin.ModelAdmin):
    list_display = ('Nombre', 'Apellido', 'Correo_Electronico', 'Direccion', 'Telefono')

class CarritoAdmin(admin.ModelAdmin):
    list_display = ('Usuario', 'Creado')

class ProductoCarritoAdmin(admin.ModelAdmin):
    list_display = ('Codigo', 'Cantidad', 'Precio', 'Carrito')

class ContactoAdmin(admin.ModelAdmin):
    list_display = ('nombre', 'email', 'mensaje', 'creado')

class PedidoAdmin(admin.ModelAdmin):
    list_display = ('user', 'carrito', 'estado', 'total', 'creado')

class UserAdmin(admin.ModelAdmin):
    list_display = ('username', 'email', 'first_name', 'last_name', 'is_active', 'is_staff')

@admin.register(get_user_model())
class CustomUsuarioAdmin(UserAdmin):
    pass

admin.site.register(Categoria, CategoriaAdmin)
admin.site.register(Producto, ProductoAdmin)
admin.site.register(Cliente, ClienteAdmin)   
admin.site.register(Carrito, CarritoAdmin)   
admin.site.register(ProductoCarrito, ProductoCarritoAdmin)
admin.site.register(CorreoContacto, ContactoAdmin)
admin.site.register(Pedido, PedidoAdmin)