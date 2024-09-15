package com.example.animati_app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ShopDB";
    private static final int DATABASE_VERSION = 1;

    // Tabla de Usuarios
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    // Tabla de Productos
    private static final String TABLE_PRODUCTS = "Products";
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_PRICE = "price";

    // Tabla de Carrito
    private static final String TABLE_CART = "Cart";
    private static final String COLUMN_CART_ID = "cart_id";
    private static final String COLUMN_CART_PRODUCT_NAME = "product_name";
    private static final String COLUMN_CART_QUANTITY = "quantity";
    private static final String COLUMN_CART_PRICE = "price";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de usuarios
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL" + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Crear tabla de productos
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + COLUMN_PRODUCT_PRICE + " REAL NOT NULL" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        // Crear tabla de carrito
        String CREATE_CART_TABLE = "CREATE TABLE " + TABLE_CART + "("
                + COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CART_PRODUCT_NAME + " TEXT NOT NULL,"
                + COLUMN_CART_QUANTITY + " INTEGER NOT NULL,"
                + COLUMN_CART_PRICE + " REAL NOT NULL" + ")";
        db.execSQL(CREATE_CART_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    // Método para agregar un producto
    public long addProduct(String name, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        long id = db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return id;
    }

    // Método para obtener todos los productos
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_PRODUCTS;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME));
                double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRODUCT_PRICE));

                Product product = new Product(id, name, price);
                products.add(product);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return products;
    }

    // Método para agregar un producto al carrito
    public long addToCart(String productName, int quantity, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CART_PRODUCT_NAME, productName);
        values.put(COLUMN_CART_QUANTITY, quantity);
        values.put(COLUMN_CART_PRICE, price);
        long id = db.insert(TABLE_CART, null, values);
        db.close();
        return id;
    }

    // Método para obtener los productos del carrito
    public ArrayList<CartItem> getCartItems() {
        ArrayList<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CART;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID));
                String productName = cursor.getString(cursor.getColumnIndex(COLUMN_CART_PRODUCT_NAME));
                int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_QUANTITY));
                double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_CART_PRICE));

                CartItem item = new CartItem(id, productName, quantity, price * quantity);
                cartItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cartItems;
    }
    // Método para verificar si un usuario existe
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta segura para evitar inyección de SQL
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean userExists = (cursor.getCount() > 0); // Si el usuario existe, el cursor tendrá filas
        cursor.close();
        db.close();
        return userExists;
    }
    // Método para agregar un nuevo usuario a la base de datos
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);

        // Insertar fila
        long result = db.insert(TABLE_USERS, null, contentValues);
        db.close(); // Cerrar la base de datos
        return result;
    }
}
