package com.example.animatiappandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.bumptech.glide.Glide;

import java.util.List;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.ProductViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onEditClick(int position);
    }

    private final List<ProductAdmin> productList;  // Cambiado a ProductAdmin
    private final Context context;
    private final RequestQueue requestQueue;
    private final OnItemClickListener listener;

    public AdminProductAdapter(Context context, List<ProductAdmin> productList, RequestQueue requestQueue, OnItemClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.requestQueue = requestQueue;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_product, parent, false);
        return new ProductViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductAdmin product = productList.get(position);  // Cambiado a ProductAdmin

        holder.nameText.setText("Nombre: " + product.getName());
        holder.priceText.setText("Precio: $" + product.getPrice());
        holder.stockText.setText("Stock: " + product.getStock());
        holder.categoryIdText.setText("Categoría: " + product.getCategoria());

        Glide.with(context)
                .load(product.getImagen())
                .placeholder(R.drawable.imagen_placeholder)
                .error(R.drawable.imagen_error)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameText, priceText, stockText, categoryIdText;
        ImageView productImage, deleteIcon, editIcon;
        OnItemClickListener listener;

        public ProductViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            this.listener = listener;

            nameText = itemView.findViewById(R.id.admin_product_name);
            priceText = itemView.findViewById(R.id.admin_product_price);
            stockText = itemView.findViewById(R.id.admin_product_stock);
            categoryIdText = itemView.findViewById(R.id.admin_product_category_id);
            productImage = itemView.findViewById(R.id.admin_product_image);
            deleteIcon = itemView.findViewById(R.id.admin_delete_icon);
            editIcon = itemView.findViewById(R.id.admin_edit_icon);

            itemView.setOnClickListener(this);

            deleteIcon.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(position);
                    }
                }
            });

            editIcon.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onEditClick(position);
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }
}
