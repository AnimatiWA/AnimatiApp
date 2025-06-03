from django.contrib.auth import authenticate
from rest_framework_simplejwt.serializers import TokenObtainPairSerializer
from rest_framework import serializers
from django.db import models
from .models import *



class CategoriaSerializer(serializers.ModelSerializer):
    class Meta:
        model = Categoria
        fields = '__all__'


class ProductosSerializer(serializers.ModelSerializer):
    class Meta:
        model = Producto
        fields = '__all__'   

class CarritoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Carrito
        fields = '__all__' 

class ProductoCarritoSerializer(serializers.ModelSerializer):
    
    Cantidad = serializers.IntegerField(required=False, default=1, min_value=1)
    Precio = serializers.ReadOnlyField()
    nombre_producto = serializers.SerializerMethodField()
    imagen_producto = serializers.SerializerMethodField()

    class Meta:
        model = ProductoCarrito
        fields = ["id", 'Codigo', 'Carrito', 'Cantidad', 'Precio', 'nombre_producto', 'imagen_producto']

    def get_nombre_producto(self, obj):

        return obj.Codigo.Nombre_Producto
    
    def get_imagen_producto(self, obj):

        return obj.Codigo.Imagen

class CustomTokenObtainPairSerializer(TokenObtainPairSerializer):
    pass

class CustomUsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'username','email','first_name','last_name')

class UsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = get_user_model()
        fields = '__all__'
        extra_kwargs:{'password':{'write_only': True}} # type: ignore


class CrearUsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('id', 'username', 'email', 'password', 'first_name', 'last_name')
        extra_kwargs = {
            'password': {'required': True}
        }

    
    def create(self, validated_data):
        user = User.objects.create_user(**validated_data)
        return user


class ActualizarUsuarioSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ('first_name', 'last_name', 'email', 'password')

    def update(self, instance, validated_data):
        password = validated_data.pop('password')
        if password:
            instance.set_password(password)
        instance = super().update(instance, validated_data)
        return instance


class PasswordSerializer(serializers.Serializer):
    password = serializers.CharField(max_length=128, min_length=6, write_only=True)
    password2 = serializers.CharField(max_length=128, min_length=6, write_only=True)

    def validate(self, data):
        if data['password'] != data['password2']:
            raise serializers.ValidationError(
                {'password':'Debe ingresar ambas contraseñas iguales'}
            )
        return data

class UsuarioListaSerializer(serializers.ModelSerializer):
    class Meta:
        model = User

    def to_representation(self, instance):
        return {
            'id': instance['id'],
            'first_name': instance['first_name'],
            'username': instance['username'],
            'email': instance['email']
        }
    
class CorreoContactoSerializer(serializers.ModelSerializer):

    class Meta:
        model =  CorreoContacto
        fields = ('id', 'nombre', 'email', 'mensaje', 'creado')

class PasswordRecoveryEmailSerializer(serializers.Serializer):
    email = serializers.EmailField()

# Aplica a RecoveryPasswordActivity
class PasswordRecoverySerializer(serializers.ModelSerializer):
    codigo = serializers.CharField(write_only=True, required=True)
    password = serializers.CharField(write_only=True, required=True)
    password2 = serializers.CharField(write_only=True, required=True)

    class Meta:
        model = User
        fields = ('codigo', 'password', 'password2')

    # La validación presente en el .java se repite para mayor robustez.
    def validate(self, data):
        if data['password'] != data['password2']:
            raise serializers.ValidationError("Las contraseñas no coinciden.")
        return data

    def validate_password(self, value):
        if len(value) < 6:
            raise serializers.ValidationError("La contraseña debe tener al menos 6 caracteres.")
        return value
    
class PasswordResetSerializer(serializers.ModelSerializer):
    password = serializers.CharField(write_only=True, required=True)
    password2 = serializers.CharField(write_only=True, required=True)

    class Meta:
        model = User
        fields = ('password', 'password2')

    # La validación presente en el .java se repite para mayor robustez.
    def validate(self, data):
        if data['password'] != data['password2']:
            raise serializers.ValidationError("Las contraseñas no coinciden.")
        return data

    def validate_password(self, value):
        if len(value) < 6:
            raise serializers.ValidationError("La contraseña debe tener al menos 6 caracteres.")
        return value