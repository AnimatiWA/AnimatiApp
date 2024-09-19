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



from .models import *
from .serializers import *
# Create your views here.

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
                return Response({
                    'token': login_serializer.validated_data.get('access'),
                    'refresh-token': login_serializer.validated_data.get('refresh'),
                    'user': user_serializer.data,
                    'message': 'Inicio de Sesion Existoso'
                }, status=status.HTTP_200_OK)
            return Response({'error': 'Contraseña o nombre de usuario incorrectos'}, status=status.HTTP_400_BAD_REQUEST)
        return Response({'error': 'Contraseña o nombre de usuario incorrectos'}, status=status.HTTP_400_BAD_REQUEST)

    

class LogoutView(GenericAPIView):
    permission_classes = [permissions.AllowAny] 
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
    queryset = User.objects.all()
    serializer_class = UsuarioSerializer
    http_method_names = ['get', 'post']
    permission_classes = [ permissions.AllowAny]
    
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
    permission_classes = [permissions.AllowAny]
    serializer_class = UsuarioSerializer
    http_method_names = ['get', 'patch']
    
    def get(self, request, *args, **kwargs):

        pk = self.kwargs.get('pk')
        try:
            user = User.objects.get(pk = pk)
        
        except User.DoesNotExist:
            return Response({'error': 'Usuario no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        serializer = self.get_serializer(user)
        return Response(serializer.data, status=status.HTTP_200_OK)
        
    def patch(self, request, *args, **kwargs):
        pk = self.kwargs.get('pk')
        try:
            user = User.objects.get(id=pk)
        except User.DoesNotExist:
            return Response({'error': 'Usuario no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        if not request.data:
            return Response({'error': 'No se han proporcionado datos para actualizar'}, status=status.HTTP_400_BAD_REQUEST)

        serializer = UsuarioSerializer(user, data=request.data, partial=True)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_200_OK)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



#----------------------Vistas Categoria------------------------------------------------

class CategoriaViewSet(viewsets.ModelViewSet):
    permission_classes = [permissions.AllowAny]
    queryset = Categoria.objects.all()
    serializer_class = CategoriaSerializer
    def get_object(self, request):

        queryset = self.get_queryset()
        serializers = CategoriaSerializer(queryset, many=True)
        return Response(serializers.data)



#--------------Vistas Productos------------
    
    
class ListaProductos(APIView):
    permission_classes = [permissions.AllowAny]
    http_method_names = [
        'delete',
        'get',]
    def get(self, request, format=None):
        productos = Producto.objects.all()
        serializers = ProductosSerializer(productos, many=True)
        return Response(serializers.data)
    
    def delete(self, request, Codigo_Producto, format=None):
        producto = Producto.objects.filter(pk=Codigo_Producto).first()
        if producto is None:
            return Response({'error': 'Producto no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        producto.delete()
        return Response({'message':'Producto Eliminado'},status=status.HTTP_200_OK)
    
class ActualizarProductoApiView(generics.UpdateAPIView):
    serializer_class = ProductosSerializer
    queryset= Producto.objects.all()
    permission_classes = [permissions.AllowAny]
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
    permission_classes = [permissions.AllowAny]
    http_method_names = ['post']

    def post(self, request, format=None):
        serializer = ProductosSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data,status=status.HTTP_201_CREATED)
        return Response({"error": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
    

class DetalleCarrito(APIView):
    permission_classes = [permissions.AllowAny]
    http_method_names = ['get']
    
    def get(self, request, id):
        try:
            carrito = Carrito.objects.get(id=id)
            serializer = CarritoSerializer(carrito)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except Carrito.DoesNotExist:
            return Response({"error": "Carrito no encontrado"}, status=status.HTTP_404_NOT_FOUND)


class ListaCarritos(generics.ListCreateAPIView):
    permission_classes = [permissions.AllowAny]
    serializer_class = CarritoSerializer
    queryset = Carrito.objects.all()


class CrearCarrito(APIView):
    permission_classes = [permissions.AllowAny]
    http_method_names = ['post']
    
    def post(self, request, format=None):
        serializer = CarritoSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response({"error": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)



class ActualizarCarrito(generics.UpdateAPIView):
    permission_classes = [permissions.AllowAny]
    serializer_class = CarritoSerializer
    queryset = Carrito.objects.all()

    def patch(self, request, id):
        carrito = self.get_queryset().filter(id=id).first()

        if carrito:
            carrito_serializer = self.serializer_class(carrito, data=request.data, 
            partial=True)

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
    permission_classes = [permissions.AllowAny]
    http_method_names = ['delete']

    def delete(self, request, id, format=None):
        carrito = Carrito.objects.filter(id=id).first()
        if carrito is None:
            return Response({'error': 'Carrito no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        carrito.delete()
        return Response({'message':'Carrito Eliminado'},status=status.HTTP_200_OK)


class DetalleProductosCarrito(APIView):
    permission_classes = [permissions.AllowAny]
    http_method_names = ['get']
    
    def get(self, request, id):
        try:
            productoCarrito = ProductoCarrito.objects.get(id=id)
            serializer = ProductoCarritoSerializer(productoCarrito)
            return Response(serializer.data, status=status.HTTP_200_OK)
        except ProductoCarrito.DoesNotExist:
            return Response({"error": "Producto en carrito no encontrado"}, status=status.HTTP_404_NOT_FOUND)


class ListarProductosEnCarrito(generics.ListCreateAPIView):
    permission_classes = [permissions.AllowAny]
    queryset = ProductoCarrito.objects.all()
    serializer_class = ProductoCarritoSerializer

class ListarProductosEnCarritoEspecifico(APIView):
    permission_classes = [permissions.AllowAny]
    http_method_names = ['get']

    def get(self, request, carrito_id, format=None):
        productosCarrito = ProductoCarrito.objects.filter(Carrito = carrito_id)

        if not productosCarrito.exists():
            return Response({"error": "No hay productos en el carrito o el carrito no existe"}, status=status.HTTP_404_NOT_FOUND)
        
        serializer = ProductoCarritoSerializer(productosCarrito, many=True)
        return Response(serializer.data, status=status.HTTP_200_OK)

class CrearProductosCarrito(APIView):
    permission_classes = [permissions.AllowAny]
    model = ProductoCarritoSerializer
    http_method_names = ['post']

    def post(self, request, format=None):
        serializer = ProductoCarritoSerializer(data=request.data)

        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        else:
            return Response({"error": serializer.errors}, status=status.HTTP_400_BAD_REQUEST)
        

class ActualizarProductoenCarrito(generics.UpdateAPIView):
    permission_classes = [permissions.AllowAny]
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
    permission_classes = [permissions.AllowAny]
    http_method_names = ['delete']

    def delete(self, request, id, format=None):
        productoCarrito = ProductoCarrito.objects.filter(id=id).first()
        if productoCarrito is None:
            return Response({'error': 'Producto en carrito no encontrado'}, status=status.HTTP_404_NOT_FOUND)
        
        productoCarrito.delete()
        return Response({'message':'Producto en carrito Eliminado'},status=status.HTTP_200_OK)