import calendar
from decimal import Decimal
from django.http import Http404
from django.shortcuts import render
from django.contrib.auth import authenticate, logout
from rest_framework_simplejwt.tokens import RefreshToken
from rest_framework_simplejwt.views import TokenObtainPairView
from django.views.generic import  DetailView, ListView, CreateView, UpdateView, DeleteView
from rest_framework import permissions
from rest_framework.views import APIView
from rest_framework.decorators import api_view
from rest_framework.exceptions import ValidationError
from rest_framework.generics import CreateAPIView, UpdateAPIView, GenericAPIView, RetrieveAPIView, DestroyAPIView
from rest_framework.permissions import AllowAny, IsAdminUser, IsAuthenticated
from rest_framework.response import Response
from rest_framework import status, viewsets, generics

# Importaciones ForgotPassword
from django.core.mail import send_mail
from django.http import JsonResponse
from django.contrib.auth.models import User
from django.views.decorators.csrf import csrf_exempt
import json
from django.utils.crypto import get_random_string
from datetime import timedelta
from django.utils import timezone

# Importación RecoveryPassword
from django.contrib.auth.hashers import make_password

from django.db.models import Max
from django.db.models import Sum

from django.conf import settings

from .models import *
from .serializers import *

# Importacion MercadoPago

import mercadopago

# Listado de Views.

class CreateUserAPI(CreateAPIView):
    queryset = User.objects.all()
    serializer_class = CrearUsuarioSerializer
    permission_classes = [permissions.AllowAny]

    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        try:
            serializer.is_valid(raise_exception=True)
            self.perform_create(serializer)
            headers = self.get_success_headers(serializer.data)
            return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)
        except ValidationError as e:
            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)
        except Exception as e:
            return Response({'error': 'Error inesperado: ' + str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)


class ActualizarUsuarioSerializerAPI(UpdateAPIView):
    permission_classes = [permissions.AllowAny]
    queryset = User.objects.all()
    serializer_class = ActualizarUsuarioSerializer


class LoginAPIView(TokenObtainPairView):
    permission_classes = [AllowAny] 

    serializer_class = CustomTokenObtainPairSerializer

    def post(self, request, *args, **kwargs):
        username = request.data.get('username', '')
        password = request.data.get('password', '')
        user = authenticate(
            username=username,
            password=password
        )

        if user:
            login_serializer = self.serializer_class(data=request.data)

            if login_serializer.is_valid():
                user_serializer = CustomUsuarioSerializer(user)
                
                active_cart_id = Carrito.objects.filter(Usuario=user).aggregate(Max('id'))['id__max']

                if active_cart_id is None:
                    active_cart_id = -1

                return Response({
                    'token': login_serializer.validated_data.get('access'),
                    'refresh-token': login_serializer.validated_data.get('refresh'),
                    'user': user_serializer.data,
                    'carrito': active_cart_id,
                    'message': 'Inicio de Sesion Existoso'
                }, status=status.HTTP_200_OK)
            return Response({'error': 'Contraseña o nombre de usuario incorrectos'}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error': 'Contraseña o nombre de usuario incorrectos'}, status=status.HTTP_400_BAD_REQUEST)

    

class LogoutView(GenericAPIView):
    permission_classes = [permissions.IsAuthenticated]
    
    def post(self, request):
        refresh_token = request.data.get('refresh_token')

        if not refresh_token:
            return Response({'error': 'Token no proporcionado'}, status=status.HTTP_400_BAD_REQUEST)
        try:

            token = RefreshToken(refresh_token)

            token.blacklist()
            return Response({'message':'Sesión Cerrada.'},status=status.HTTP_200_OK)
        
        except Exception as e:

            return Response({'error': str(e)}, status=status.HTTP_400_BAD_REQUEST)



class ListaDeUsuarios(generics.ListCreateAPIView):
    permission_classes = [permissions.IsAdminUser]
    queryset = User.objects.all()
    serializer_class = UsuarioSerializer
    http_method_names = ['get', 'post']
    
    def list(self, request):
        queryset = self.get_queryset()
        serializer = UsuarioSerializer(queryset, many=True)
        return Response(serializer.data)
    
    def post(self, request):
        serializer = UsuarioSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data,status=status.HTTP_200_OK)
        return Response({"error": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)

class PerfilView(GenericAPIView):
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = UsuarioSerializer
    http_method_names = ['get', 'patch']
    
    def get(self, request, *args, **kwargs):

        user = request.user
        serializer = self.get_serializer(user)

        return Response(serializer.data, status=status.HTTP_200_OK)
        
    def patch(self, request, *args, **kwargs):
        
        user = request.user
        
        if not request.data:
            return Response({'error': 'No se han proporcionado datos para actualizar'}, status=status.HTTP_400_BAD_REQUEST)

        serializer = UsuarioSerializer(user, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



#----------------------Vistas Categoria------------------------------------------------

class CategoriaViewSet(viewsets.ModelViewSet):
    permission_classes = [permissions.IsAuthenticated]
    queryset = Categoria.objects.all()
    serializer_class = CategoriaSerializer

    def get_object(self, request):

        queryset = self.get_queryset()
        serializers = CategoriaSerializer(queryset, many=True)
        return Response(serializers.data)



#--------------Vistas Productos------------
    
class EliminarProductos(APIView):
    permission_classes = [permissions.IsAdminUser]
    http_method_names = ['delete']
    
    def delete(self, request, Codigo_Producto, format=None):
        producto = Producto.objects.filter(pk=Codigo_Producto).first()
        if producto is None:
            return Response({'error': 'Producto no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        producto.delete()
        return Response({'message':'Producto Eliminado'},status=status.HTTP_200_OK)

    
class ListaProductos(APIView):
    permission_classes = [permissions.AllowAny]
    http_method_names = ['get']
    
    def get(self, request, format=None):
        productos = Producto.objects.filter(Stock__gt=0)
        serializers = ProductosSerializer(productos, many=True)
        return Response(serializers.data)
class ActualizarProductoApiView(generics.UpdateAPIView):
    permission_classes = [permissions.IsAdminUser]
    serializer_class = ProductosSerializer
    queryset= Producto.objects.all()
    lookup_field = 'Codigo_Producto'

    def patch(self, request, Codigo_Producto):
        producto = self.get_queryset().filter(Codigo_Producto=Codigo_Producto).first()

        if producto:
            producto_serializer = self.serializer_class(producto, data=request.data, 
            partial=True)

            if producto_serializer.is_valid():

                producto_serializer.save()
                return Response(producto_serializer.data, status=status.HTTP_200_OK)
            return Response({"error": producto_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error':'Producto no encontado'}, status=status.HTTP_404_NOT_FOUND)

    def put(self, request, Codigo_Producto):
        
        producto = self.get_queryset().filter(Codigo_Producto=Codigo_Producto).first()

        if producto:
            producto_serializer = self.serializer_class(producto, data=request.data)

            if producto_serializer.is_valid():

                producto_serializer.save()
                return Response(producto_serializer.data, status=status.HTTP_200_OK)
            return Response({"error": producto_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error':'Producto no encontado'}, status=status.HTTP_404_NOT_FOUND)


class anadirProducto(APIView):
    permission_classes = [permissions.IsAdminUser]
    http_method_names = ['post']

    def post(self, request, format=None):
        serializer = ProductosSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data,status=status.HTTP_201_CREATED)
        return Response({"error": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
    

class DetalleCarrito(APIView):
    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['get']
    
    def get(self, request, id):
        try:
            carrito = Carrito.objects.get(id=id)
            serializer = CarritoSerializer(carrito)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Carrito.DoesNotExist:
            return Response({"error": "Carrito no encontrado"}, status=status.HTTP_404_NOT_FOUND)

class DetalleCarritoUsuario(APIView):
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = CarritoSerializer
    http_method_names = ['get']
    
    def get(self, request):

        usuario = request.user

        try:
            carrito = Carrito.objects.get(Usuario=usuario, is_active=True)
            serializer = self.serializer_class(carrito)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Carrito.DoesNotExist:
            return Response({"error": "No se encontró un carrito activo para el usuario."}, status=status.HTTP_404_NOT_FOUND)

class ListaCarritos(generics.ListCreateAPIView):
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = CarritoSerializer
    queryset = Carrito.objects.all()


class CrearCarrito(APIView):
    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['post']
    
    def post(self, request, format=None):

        user = request.user

        carrito_activo = Carrito.objects.filter(Usuario=user, is_active=True).first()

        if(carrito_activo): #Esto se deja pero ahora no cierra automaticament el carrito sino que indica que ya hay uno activo.

            return Response({"error": "ya existe un carrito activo para este usuario"}, status=status.HTTP_409_CONFLICT)

        datos = {
            'Usuario': user.id,
        }

        serializer = CarritoSerializer(data=datos)

        if serializer.is_valid():

            serializer.save()

            return Response(serializer.data, status=status.HTTP_201_CREATED)
        
        return Response({"error": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)



class ActualizarCarrito(generics.UpdateAPIView):
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = CarritoSerializer
    queryset = Carrito.objects.all()

    def patch(self, request, id):
        carrito = self.get_queryset().filter(id=id).first()

        if carrito:
            carrito_serializer = self.serializer_class(carrito, data=request.data, partial=True)

            if carrito_serializer.is_valid():

                carrito_serializer.save()
                return Response(carrito_serializer.data, status=status.HTTP_200_OK)
            return Response({"error": carrito_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error':'Carrito no encontado'}, status=status.HTTP_404_NOT_FOUND)

    def put(self, request, id):
        carrito = self.get_queryset().filter(id=id).first()

        if carrito:
            carrito_serializer = self.serializer_class(carrito, data=request.data)

            if carrito_serializer.is_valid():

                carrito_serializer.save()
                return Response(carrito_serializer.data, status=status.HTTP_200_OK)
            return Response({"error": carrito_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error':'Carrito no encontado'}, status=status.HTTP_404_NOT_FOUND)


class EliminarCarrito(APIView):
    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['delete']

    def delete(self, request, id, format=None):
        carrito = Carrito.objects.filter(id=id).first()
        if carrito is None:
            return Response({'error': 'Carrito no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        carrito.delete()
        return Response({'message':'Carrito Eliminado'},status=status.HTTP_200_OK)


class DetalleProductosCarrito(APIView):
    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['get']
    
    def get(self, request, id):
        try:
            productoCarrito = ProductoCarrito.objects.get(id=id)
            serializer = ProductoCarritoSerializer(productoCarrito)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except ProductoCarrito.DoesNotExist:
            return Response({"error": "Producto en carrito no encontrado"}, status=status.HTTP_404_NOT_FOUND)


class ListarProductosEnCarrito(generics.ListCreateAPIView):
    permission_classes = [permissions.IsAuthenticated]
    queryset = ProductoCarrito.objects.all()
    serializer_class = ProductoCarritoSerializer

class ListarProductosEnCarritoEspecifico(APIView):
    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['get']

    def get(self, request, carrito_id, format=None):
        productosCarrito = ProductoCarrito.objects.select_related('Codigo').filter(Carrito = carrito_id)

        if not productosCarrito.exists():
            return Response({"error": "No hay productos en el carrito o el carrito no existe"}, status=status.HTTP_404_NOT_FOUND)
        
        serializer = ProductoCarritoSerializer(productosCarrito, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

class CrearProductosCarrito(APIView):
    permission_classes = [permissions.IsAuthenticated]
    model = ProductoCarritoSerializer
    http_method_names = ['post']

    def post(self, request, format=None):
        
        codigo_producto = request.data.get('Codigo')
        cantidad = int(request.data.get('Cantidad', 1))

        try:
            carrito = Carrito.objects.get(Usuario=request.user, is_active=True)
            carrito_id = carrito.id
        except Carrito.DoesNotExist:
            return Response({"error": "No se encontró un carrito activo para el usuario."}, status=status.HTTP_404_NOT_FOUND)

        try:

            producto = Producto.objects.get(Codigo_Producto=codigo_producto)
        except Producto.DoesNotExist:

            return Response({"error": "Producto no encontrado."}, status=status.HTTP_404_NOT_FOUND)

        try:
            #Si ya existe un producto en carrito con este codigo, solo lo actualizo
            producto_carrito = ProductoCarrito.objects.get(Codigo_id=codigo_producto, Carrito_id=carrito_id)

            if(producto_carrito.Cantidad + cantidad > producto.Stock):

                return Response({"error": "Stock insuficiente"}, status=status.HTTP_400_BAD_REQUEST)

            producto_carrito.Cantidad += cantidad
            producto_carrito.save()
            
            serializer = ProductoCarritoSerializer(producto_carrito)

            return Response(serializer.data, status=status.HTTP_200_OK)

        except ProductoCarrito.DoesNotExist:

            if(cantidad > producto.Stock):

                return Response({"error": "Stock insuficiente"}, status=status.HTTP_400_BAD_REQUEST)
            
            data = {

                "Codigo": codigo_producto,
                "Cantidad": cantidad,
                "Carrito": carrito.id
            }
            
            serializer = ProductoCarritoSerializer(data=data)

            if serializer.is_valid():
                serializer.save()
                return Response(serializer.data, status=status.HTTP_201_CREATED)
            else:
                return Response({"error": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        

class ActualizarProductoenCarrito(generics.UpdateAPIView):
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = ProductoCarritoSerializer
    queryset = ProductoCarrito.objects.all()

    def patch(self, request, id):
        productosCarrito = self.get_queryset().filter(id=id).first()

        if productosCarrito:
            producto_carrito_serializer = self.serializer_class(productosCarrito, data=request.data, 
            partial=True)

            if producto_carrito_serializer.is_valid():

                producto_carrito_serializer.save()
                return Response(producto_carrito_serializer.data, status=status.HTTP_200_OK)
            return Response({"error": producto_carrito_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error':'Producto en carrito no encontado'}, status=status.HTTP_404_NOT_FOUND)

    def put(self, request, id):
        carrito = self.get_queryset().filter(id=id).first()

        if carrito:
            carrito_serializer = self.serializer_class(carrito, data=request.data)

            if carrito_serializer.is_valid():

                carrito_serializer.save()
                return Response(carrito_serializer.data, status=status.HTTP_200_OK)
            return Response({"error": carrito_serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error':'Producto en carrito no encontado'}, status=status.HTTP_404_NOT_FOUND)


class EliminarItemEnCarrito(APIView):
    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['delete']

    def delete(self, request, id, format=None):
        productoCarrito = ProductoCarrito.objects.filter(id=id).first()
        if productoCarrito is None:
            return Response({'error': 'Producto en carrito no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        productoCarrito.delete()
        return Response({'message':'Producto en carrito Eliminado'},status=status.HTTP_200_OK)

class EliminarUnidadItemEnCarrito(APIView):
    permission_classes = [permissions.IsAuthenticated]
    serializer_class = ProductoCarritoSerializer
    http_method_names = ['patch']

    def patch(self, request, id, format=None):
        productoCarrito = ProductoCarrito.objects.filter(id=id).first()

        if productoCarrito is None:
            return Response({'error': 'Producto en carrito no encontrado'}, status=status.HTTP_404_NOT_FOUND)

        if(productoCarrito.Cantidad > 1):

            productoCarrito.Cantidad -= 1
            productoCarrito.save()

            serializer = self.serializer_class(productoCarrito)

            return Response(serializer.data, status=status.HTTP_200_OK)
        
        productoCarrito.delete()
        return Response({'message':'Producto en carrito Eliminado'},status=status.HTTP_200_OK)

class PasswordRecoveryEmailAPIView(APIView):
    
    permission_classes = [AllowAny]
    serializer_class = PasswordRecoveryEmailSerializer
    http_method_names = ['post']

    def post(self, request):

        serializer =  self.serializer_class(data=request.data)

        if serializer.is_valid():

            email = serializer.validated_data['email']

            try:

                user = User.objects.get(email=email)
                
                code = get_random_string(length=6, allowed_chars='ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789')
                
                reset_token = PasswordResetToken.objects.update_or_create(
                    user=user,
                    token=code,
                    expires_at=timezone.now() + timedelta(hours=1)
                )

                send_mail(
                    subject='Recuperación de contraseña',
                    message=f"Tu codigo de cambio de contraseña para AnimatiApp es el siguiente: {code}",
                    from_email=settings.DEFAULT_FROM_EMAIL,
                    recipient_list=[email],
                )
                return Response({'message': 'Correo de recuperación enviado.'}, status=status.HTTP_201_CREATED)
            except User.DoesNotExist:
                return Response({'error': 'Correo no encontrado.'}, status=status.HTTP_404_NOT_FOUND)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

class EmailPasswordResetView(APIView):
    permission_classes = [AllowAny]
    serializer_class = PasswordRecoverySerializer
    http_method_names = ["post"]

    def post(self, request, *args, **kwargs):

        serializer = self.serializer_class(data=request.data)

        if(serializer.is_valid()):

            code = serializer.validated_data['codigo']
            password = serializer.validated_data['password']

            try:

                reset_token = PasswordResetToken.objects.get(token=code)
                if reset_token.is_expired():
                    return Response({'error': 'Codigo de verificación expirado.'}, status=status.HTTP_400_BAD_REQUEST)

                user = reset_token.user
                user.password = make_password(password)
                user.save()

                reset_token.delete()

                return Response({"message": "Contraseña actualizada exitosamente."}, status=status.HTTP_200_OK)
                
            except PasswordResetToken.DoesNotExist:

                return Response({'error': 'Token inválido.'}, status=status.HTTP_404_NOT_FOUND)
            
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

    
# Proceso del cambio de contraseña
class PasswordResetView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request, *args, **kwargs):
        serializer = PasswordResetSerializer(data=request.data)
        if serializer.is_valid():
            user = request.user
            user.password = make_password(serializer.validated_data['password'])
            user.save()
            return Response({"message": "Contraseña actualizada exitosamente."}, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class ContactMessageView(APIView):

    authentication_classes = []  
    permission_classes = [permissions.AllowAny]
    http_method_names = ['post']

    def post(self, request):

        serializer = CorreoContactoSerializer(data=request.data)

        if serializer.is_valid():

            email_de_contacto = serializer.save()

            send_mail(

                subject='Confirmación de recepción de consulta',
                message=f'Estimado {email_de_contacto.nombre}, nos ponemos en contacto con Ud. Para confirmar que recibimos el mensaje de contacto que nos envió a través de la aplicación movil de Animati. Nuestro equipo se pondrá en contacto con Ud. A la brevedad. \nDesde ya muchas gracias por su paciencia.',
                from_email=settings.DEFAULT_FROM_EMAIL,
                recipient_list=[email_de_contacto.email],
            )

            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.data, status=status.HTTP_400_BAD_REQUEST)
    
class HistorialCarritoView(APIView):


    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['get']

    def get(self, request, *args, **kwargs):

        user = request.user

        pedidos = Pedido.objects.filter(user=user,estado__in=["aprobado", "pendiente"]).order_by('-creado')

        if not pedidos.exists():

            return Response([], status=status.HTTP_204_NO_CONTENT)
        
        historial = []

        for pedido in pedidos:

            carrito = pedido.carrito

            if not carrito:
                continue

            productos = ProductoCarrito.objects.filter(Carrito=carrito) if carrito else []

            total_precio = productos.aggregate(total=Sum('Precio'))['total'] or 0.0
            total_cantidad = productos.aggregate(total=Sum('Cantidad'))['total'] or 0


            historial.append({

                'Id': pedido.id,
                'CarritoId': carrito.id if carrito else None,
                'Fecha': pedido.creado.date().isoformat(),
                'Cantidad': total_cantidad,
                'Precio': float(total_precio),
                'Confirmado': pedido.estado,
            })

        return Response(historial, status=status.HTTP_200_OK)
    
    # MercadoPago

class CreatePreferenceView(APIView):
    
    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['post']

    def post(self, request):

        user = request.user

        #Esto no se usa pero mercadopago rompe las bolainas sino
        success_url = request.data.get('success', '') or 'https://example.com/success'
        failure_url = request.data.get('failure', '') or 'https://example.com/failure'
        pending_url = request.data.get('pending', '') or 'https://example.com/pending'

        carrito_activo = Carrito.objects.filter(Usuario=user, is_active=True).first()


        if not carrito_activo:

            return Response({"error":"No hay un carrito activo"}, status=status.HTTP_404_NOT_FOUND)

        productos_carrito = ProductoCarrito.objects.filter(Carrito=carrito_activo)

        if not productos_carrito:

            return Response({"error":"El carrito está vacío"}, status=status.HTTP_400_BAD_REQUEST)

        productos_sin_stock = []

        for producto in productos_carrito:

            if producto.Cantidad > producto.Codigo.Stock:

                productos_sin_stock.append(producto.Codigo.Nombre_Producto)

        if productos_sin_stock:

            return Response({"error": f"Stock insuficiente para los productos: {', '.join(productos_sin_stock)}"}, status=status.HTTP_400_BAD_REQUEST)
        
        try:

            items = []
            total = 0

            for producto in productos_carrito:

                items.append({

                    "title": producto.Codigo.Nombre_Producto,
                    "quantity": producto.Cantidad,
                    "unit_price": float(producto.Codigo.Precio),
                    "currency_id": "ARS",
                })

                total += producto.Codigo.Precio * producto.Cantidad

            #Cancelo el pedido pendiente anterior si es que existe porque el salame del cliente se olvidó de pagar o cerro la pagina de ML
            
            pedido_pendiente = Pedido.objects.filter(

                user=user,
                carrito=carrito_activo,
                estado='pendiente'
            ).update(estado='cancelado')

            pedido = Pedido.objects.create(
                user=request.user,
                carrito=carrito_activo,
                estado='pendiente',
                total=total
            )

            preference_data = {

                "items": items,
                "external_reference": str(pedido.id),
                # "back_urls": {

                #     # Esto no se usa en mobile, qudan vacios ahí porque estoy usando webhooks.
                #     # (Era esto o hacer vistas separadas para web y app, no da la verdad)

                #     "success": success_url,
                #     "failure": failure_url,
                #     "pending": pending_url
                # },
                # "auto_return": "approved"
            }

            sdk = mercadopago.SDK(settings.SDK)
            preference_response = sdk.preference().create(preference_data)
            init_point = preference_response["response"].get("init_point")

            return Response({
                "init_point": init_point,
                "pedido_id": pedido.id
                }, status=status.HTTP_200_OK)
            
        except Exception as e:

            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        
class MercadopagoWebhook(APIView):

    permission_classes = [permissions.AllowAny]
    http_method_names = ['post']

    def post(self, request):

        try:

            topic = request.query_params.get('type') or request.data.get('type')
            payment_id = (
                request.query_params.get('data.id')
                or request.data.get('data', {}).get('id')
            )


            if topic != "payment":
                print(f"Topico '{topic}' no soportado")
                return Response({"error": f"Tópico '{topic}' no soportado, solo se aceptan pagos"}, status=status.HTTP_400_BAD_REQUEST)
            
            if not payment_id:
                print ("No se recibió id de pago")
                return Response({"error": "No se recibió id de pago"}, status=status.HTTP_400_BAD_REQUEST)

            sdk = mercadopago.SDK(settings.SDK)
            payment_info = sdk.payment().get(payment_id)
            payment_data = payment_info["response"]
                
            # Aca voy a hacer la integración con la BD, primero quiero probar que todo este OK

            if payment_data["status"] == "approved":

                pedido_id = payment_data.get("external_reference")

                try:

                    pedido = Pedido.objects.get(id=pedido_id)
                    pedido.estado = "aprobado"
                    pedido.save()
                    print("Pago recibido:", payment_data)

                    user = pedido.user

                    carrito = Carrito.objects.filter(Usuario=user, is_active=True).first()

                    if(carrito):

                        carrito.is_active = False
                        carrito.confirmado = True
                        carrito.Deshabilitado = timezone.now()
                        carrito.save()

                        productos = ProductoCarrito.objects.filter(Carrito=carrito)

                        for producto in productos:

                            productoCarrito = producto.Codigo
                            productoCarrito.Stock -= producto.Cantidad
                            productoCarrito.save()
                        
                    # nuevo_carrito = Carrito.objects.create(Usuario=user, is_active=True)
                
                except Pedido.DoesNotExist:

                    print("Pedido no encontrado")
                    return Response({"error": "Pedido no econtrado"}, status=status.HTTP_404_NOT_FOUND)

            print("Pago recibido")
            return Response({"message": "Pago recibido"}, status=status.HTTP_200_OK)

        except Exception as e:

            print("Error 500. " + str(e))
            return Response({"error": str(e)}, status=status.HTTP_500_INTERNAL_SERVER_ERROR)
        
class EstadoPagoView(APIView):

    permission_classes = [permissions.IsAuthenticated]
    http_method_names = ['get']

    def get(self, request):
        
        pedido_id = request.query_params.get("pedido_id")

        if not pedido_id:

            return Response({"error": "Falta el ID del pedido", "estado": "rechazado"}, status=status.HTTP_400_BAD_REQUEST)
        
        try:

            pedido = Pedido.objects.get(id=pedido_id, user=request.user)
            
            if(pedido.estado == "aprobado"):
                return Response({"message": "Pedido aprobado", "estado": "aprobado"}, status=status.HTTP_200_OK)
            elif(pedido.estado == "pendiente"):
                return Response({"message": "Pedido pendiente", "estado": "pendiente"}, status=status.HTTP_202_ACCEPTED)
            elif(pedido.estado == "rechazado"):
                return Response({"message": "Pedido rechazado", "estado": "rechazado"}, status=status.HTTP_402_PAYMENT_REQUIRED)

        except Pedido.DoesNotExist:

            return Response({"error": "Pedido no encontrado", "estado": "rechazado"}, status=status.HTTP_404_NOT_FOUND)
        
class ResumenComprasView(APIView):

    permission_classes = [permissions.IsAdminUser]
    http_method_names = ['get']

    def get(self, request):

        pedidos = Pedido.objects.filter(estado='aprobado', carrito__isnull=False) \
        .select_related('carrito') \
        .prefetch_related('carrito__productocarrito_set') #Esto esta piola. select_related es como hacer un JOIN asi nos ahorramos una consulta. Sirve para consultas 1:1
                                                         #Y prefetch_related es masomenos lo mismo pero para consultas 1:N. No hace una consulta pero si como que las junta y queda mas optimizado.

        total_ventas = 0
        total_ingresos = 0.00
        productos_vendidos = 0

        total_ultimo_mes = 0.00
        total_penultimo_mes = 0.00
        total_antepenultimo_mes = 0.00

        for pedido in pedidos:

            total_ventas += 1

            for producto in pedido.carrito.productocarrito_set.all(): #Aca accedo al objeto precargado de prefetch_related que no requiere hacer otra consulta.

                total_ingresos += producto.Precio

                productos_vendidos += producto.Cantidad

        for offset in range(1, 4):

            inicio, fin = self.rango_mes(offset)
            total_mes = Pedido.objects.filter(estado='aprobado', carrito__isnull=False, carrito__Deshabilitado__range=(inicio, fin)) \
            .aggregate(total=Sum("carrito__productocarrito__Precio"))["total"] or 0.00

            if offset == 1:
                total_ultimo_mes = total_mes

            if offset == 2:
                total_penultimo_mes = total_mes

            if offset == 3:
                total_antepenultimo_mes = total_mes

        resumen = {
            "total_ventas": total_ventas,
            "total_ingresos": total_ingresos,
            "productos_vendidos": productos_vendidos,
            "meses_previos": {
                "ultimo_mes": total_ultimo_mes,
                "penultimo_mes": total_penultimo_mes,
                "antepenultimo_mes": total_antepenultimo_mes,
            }
        } 

        return Response(resumen, status=status.HTTP_200_OK)
    
    def rango_mes(self, offset):
        """
        esto es para intentar calcular los ingresos mes por mes, puntualmente para identificar cuales son los meses anteriores, vamos a ver si sale
        
        Args:
            offset (int): Cuantos meses hacia atras queremos revisar el tamaño del mes.

        Returns:
            tuple(datetime, datetime):
                - start_date -> Primer dia del mes a las 00:00:00
                - end_date -> Ultimo dia del mes a las 23:59:59 
        """

        ahora = timezone.now()

        anio = ahora.year
        mes = ahora.month - offset

        while mes <= 0: #Esto es para corregir la fecha despues de aplicar el offset.

            mes += 12
            anio -= 1

        fecha_inicio_mes = timezone.make_aware(timezone.datetime(anio, mes, 1)) #Fecha de inicio el primer dia del mes a las 00:00:00
        fecha_fin_mes = timezone.make_aware(timezone.datetime(anio, mes, calendar.monthrange(anio, mes)[1], 23, 59, 59)) #Aca tengo que poner las 23:59:59 a mano :P

        return fecha_inicio_mes, fecha_fin_mes
