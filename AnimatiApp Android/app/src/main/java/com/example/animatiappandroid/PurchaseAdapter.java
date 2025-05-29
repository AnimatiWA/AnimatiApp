package com.example.animatiappandroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.animatiappandroid.R;
import com.example.animatiappandroid.PurchaseItem;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

    private List<PurchaseItem> purchaseList;

    public PurchaseAdapter(List<PurchaseItem> purchaseList) {
        this.purchaseList = purchaseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_purchase, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PurchaseItem item = purchaseList.get(position);
        holder.date.setText("Fecha de compra: " + item.getDate().replace("-", "/"));
        holder.quantity.setText("Cantidad de articulos: " + String.valueOf(item.getQuantity()));
        holder.price.setText("Precio total: $" + String.valueOf(item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, quantity, price;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTextView);
            quantity = itemView.findViewById(R.id.quantityTextView);
            price = itemView.findViewById(R.id.priceTextView);
        }
    }
}
