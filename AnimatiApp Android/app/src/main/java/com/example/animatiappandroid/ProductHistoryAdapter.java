package com.example.animatiappandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProductHistoryAdapter extends RecyclerView.Adapter<ProductHistoryAdapter.ProductViewHolder> {

    private List<Product> productList;

    public ProductHistoryAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, priceTextview, priceTotalTextView, quantityTextView;

        public ProductViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.product_name);
            priceTotalTextView = itemView.findViewById(R.id.product_price_total);
            quantityTextView = itemView.findViewById(R.id.product_quantity);
            priceTextview = itemView.findViewById(R.id.product_price);
        }

        public void bind(Product product) {
            nameTextView.setText(product.getName());
            priceTotalTextView.setText("Total: $" + product.getPrice());
            quantityTextView.setText("Cantidad: " + product.getQuantity());

            Double precioUnidad = product.getPrice() / product.getQuantity();
            priceTextview.setText(String.format("Unidad: $%.2f", precioUnidad));
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_history_item, parent, false);
        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
