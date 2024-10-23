package com.example.animatiappandroid;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{
    private List<Product> productList;
    private Context context;

    public ProductAdapter (List<Product> productList,Context context){
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
        Product product=productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText("$"+product.getPrice());
        holder.addToCart.setOnClickListener(v -> {


            //Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show();


        });
    };

    @Override
    public int getItemCount(){
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView productName, productPrice;
        ImageButton addToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            addToCart = itemView.findViewById(R.id.add_to_cart);
        }
    }
}
