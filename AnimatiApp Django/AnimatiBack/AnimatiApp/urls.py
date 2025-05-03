from django.urls import path, include, re_path
from django.contrib import admin
from rest_framework import routers 
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

from AnimatiApp import views
from .views import *

router=routers.DefaultRouter()
router.register(r'Categoria', views.CategoriaViewSet)





urlpatterns = [
    path('login', LoginAPIView.as_view(), name='login'),
    path('logout', LogoutView.as_view(), name = 'logout'),
    path('token', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('token/refresh', TokenRefreshView.as_view(), name='token_refresh'),
    path('registro', CreateUserAPI.as_view(), name='registro'),
    path('perfilusuario/<int:pk>', PerfilView.as_view(), name='perfilusuario'),
    path('usuarios', ListaDeUsuarios.as_view(), name='listadeusuarios'),
    path('categoria', CategoriaViewSet.as_view({'get': 'list'}), name='vercategoria'),
    path('producto/crear', anadirProducto.as_view(), name='añadirproducto'), 
    path('producto/eliminar/<int:Codigo_Producto>', EliminarProductos.as_view(), name='eliminarproducto'),
    path('producto/actualizar/<int:Codigo_Producto>', ActualizarProductoApiView.as_view(), name='actualizarproducto'), 
    path('producto/lista', ListaProductos.as_view(), name='listaproducto'),    
    path('carrito/<int:id>', DetalleCarrito.as_view(), name='detallecarrito'),
    path('carrito/crear', CrearCarrito.as_view(), name='crearcarrito'),
    path('carrito/actualizar/<int:id>', ActualizarCarrito.as_view(), name='actualizarcarrito'),
    path('carrito/eliminar/<int:id>', EliminarCarrito.as_view(), name='eliminarcarrito'),
    path('carrito/lista', ListaCarritos.as_view(), name='listacarrito'),
    path('carrito/historial', ListaCarritos.as_view(), name='historialcarrito'),
    path('carritoProductos/<int:id>', DetalleProductosCarrito.as_view(), name='detalleproductoencarrito'),
    path('carritoProductos/lista', ListarProductosEnCarrito.as_view(), name='listacarritoProductos'),
    path('carritoProductos/lista/carrito/<int:carrito_id>', ListarProductosEnCarritoEspecifico.as_view(), name='listacarritoEspecificoProductos'),
    path('carritoProductos/crear', CrearProductosCarrito.as_view(), name='crearproductoencarrito'),
    path('carritoProductos/actualizar/<int:id>', ActualizarProductoenCarrito.as_view(), name='actualizarproductoencarrito'),
    path('carritoProductos/eliminar/<int:id>', EliminarItemEnCarrito.as_view(), name='eliminarproductodelcarrito'),
    path('carritoProductos/eliminarUnidad/<int:id>', EliminarUnidadItemEnCarrito.as_view(), name='eliminarunidadproductodelcarrito'),
    path('passwordrecovery/<int:pk>', PasswordResetView.as_view(), name='passwordrecovery'),
    path('contacto', ContactMessageView.as_view(), name='contacto'),
    path('passwordRecovery', PasswordRecoveryEmailAPIView.as_view(), name='passwordrecoveryemail'),
    path('resetPassword', EmailPasswordResetView.as_view(), name='passwordreset'), 

    path('', include(router.urls)),
]
"""
    fuera de uso

    path('carrito', CarritoComprasVista.as_view(), name='carritodecompras'),
"""