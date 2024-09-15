// File: app/src/main/java/com/example/animati_app/CartAdapter.java
package com.example.animati_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<CartItem> cartItems;

    public CartAdapter(Context context, ArrayList<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public int getCount() {
        return cartItems.size();
    }

    @Override
    public Object getItem(int position) {
        return cartItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        }

        CartItem currentItem = cartItems.get(position);

        TextView productName = convertView.findViewById(R.id.cartProductName);
        TextView quantity = convertView.findViewById(R.id.cartProductQuantity);
        TextView totalPrice = convertView.findViewById(R.id.cartProductPrice);

        productName.setText(currentItem.getProductName());
        quantity.setText("Qty: " + currentItem.getQuantity());
        totalPrice.setText("$" + currentItem.getTotalPrice());

        return convertView;
    }
}
