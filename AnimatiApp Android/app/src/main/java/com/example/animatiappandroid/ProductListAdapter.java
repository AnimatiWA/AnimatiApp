package com.example.animatiappandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> productos;
    private final onRemoveClickListener removeClickListener;

    public ProductListAdapter(Context context, ArrayList<String> productos, onRemoveClickListener listener) {

        super(context, R.layout.cart_product_item_list, productos);
        this.context = context;
        this.productos = productos;
        this.removeClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.cart_product_item_list, parent, false);

        TextView productName = rowView.findViewById(R.id.product_name);
        TextView productPrice = rowView.findViewById(R.id.product_price);
        TextView productTotal = rowView.findViewById(R.id.product_total);
        Button removeButton = rowView.findViewById(R.id.remove_button);
        Button removeOneButton = rowView.findViewById(R.id.remove_one_button);

        String[] productInfo = productos.get(position).split(",");

        productName.setText(productInfo[0]);
        productPrice.setText(productInfo[1]);
        productTotal.setText(productInfo[2]);

        removeButton.setOnClickListener(v -> {

            removeClickListener.onRemoveClick(position);
        });

        removeOneButton.setOnClickListener(v -> {

            removeClickListener.onRemoveOneClick(position);
        });

        return rowView;
    }

    public interface onRemoveClickListener {

        void onRemoveClick(int position);
        void onRemoveOneClick(int position);
    }
}
