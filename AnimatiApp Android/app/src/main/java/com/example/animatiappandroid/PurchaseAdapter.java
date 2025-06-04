package com.example.animatiappandroid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.animatiappandroid.R;
import com.example.animatiappandroid.PurchaseItem;

import java.util.List;

public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

    private List<PurchaseItem> purchaseList;
    private Context context;

    public PurchaseAdapter(Context context, List<PurchaseItem> purchaseList) {

        this.context = context;
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

        holder.regret_button.setOnClickListener(v -> {

            Intent intent = new Intent(context, PurchaseRegretActivity.class);
            intent.putExtra("purchaseItem", item);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, quantity, price;
        ImageButton regret_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTextView);
            quantity = itemView.findViewById(R.id.quantityTextView);
            price = itemView.findViewById(R.id.priceTextView);
            regret_button = itemView.findViewById(R.id.regret_button);
        }
    }
}
