package com.example.animatiappandroid;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private List<ProductAdmin> productList;
    private Context context;
    private int posicionBotonSeleccionado = -1;

    public ProductAdapter (List<ProductAdmin> productList,Context context){
        this.productList=productList;
        this.context=context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewtype){
        View view=LayoutInflater.from(context).inflate(R.layout.gallery_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder,int position){
        ProductAdmin product=productList.get(position);

        holder.productName.setText(product.getName());
        holder.productPrice.setText("$"+product.getPrice());
        holder.productStock.setText("En stock: " + product.getStock());

        Glide.with(context)
                .load(product.getImagen())
                .placeholder(R.drawable.imagen_placeholder)
                .error(R.drawable.imagen_error)
                .into(holder.productImage);

        if(posicionBotonSeleccionado == position){

            holder.addToCart.setVisibility(View.GONE);
            holder.productQuantity.setVisibility(View.VISIBLE);
            holder.confirmAddToCart.setVisibility(View.VISIBLE);
            holder.decrementQuantity.setVisibility(View.VISIBLE);
            holder.incrementQuantity.setVisibility(View.VISIBLE);
        } else {

            holder.addToCart.setVisibility(View.VISIBLE);
            holder.productQuantity.setVisibility(View.GONE);
            holder.confirmAddToCart.setVisibility(View.GONE);
            holder.decrementQuantity.setVisibility(View.GONE);
            holder.incrementQuantity.setVisibility(View.GONE);
        }

        holder.addToCart.setOnClickListener(v -> {

            int posicionPrevia = posicionBotonSeleccionado;
            posicionBotonSeleccionado = position;

            notifyItemChanged(posicionPrevia);
            notifyItemChanged(posicionBotonSeleccionado);
        });

        holder.incrementQuantity.setOnClickListener(v ->{

            int cantidad = Integer.parseInt(holder.productQuantity.getText().toString());

            if(cantidad < 9999999){

                cantidad++;
                holder.productQuantity.setText(String.valueOf(cantidad));
            }
        });

        holder.decrementQuantity.setOnClickListener(v ->{

            int cantidad = Integer.parseInt(holder.productQuantity.getText().toString());

            if(cantidad > 1){

                cantidad--;
                holder.productQuantity.setText(String.valueOf(cantidad));
            }
        });

        holder.confirmAddToCart.setOnClickListener(v -> {

            int quantity = Integer.parseInt(holder.productQuantity.getText().toString());
            product.setQuantity(quantity);

            ((Gallery) context).agregarAlCarrito(product);

            posicionBotonSeleccionado = -1;
            notifyItemChanged(position);
        });
    };

    @Override
    public int getItemCount(){
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView productName, productPrice, productStock;
        ImageButton addToCart;
        EditText productQuantity;
        Button confirmAddToCart, decrementQuantity, incrementQuantity;
        ImageView productImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productStock = itemView.findViewById(R.id.product_stock);
            addToCart = itemView.findViewById(R.id.add_to_cart);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            confirmAddToCart = itemView.findViewById(R.id.confirm_add_to_cart);
            decrementQuantity = itemView.findViewById(R.id.decrement_quantity);
            incrementQuantity = itemView.findViewById(R.id.increment_quantity);
            productImage = itemView.findViewById(R.id.product_image);
        }
    }
}
