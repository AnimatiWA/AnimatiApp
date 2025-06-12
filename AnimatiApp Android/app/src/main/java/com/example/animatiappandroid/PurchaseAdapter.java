    package com.example.animatiappandroid;

    import android.content.Context;
    import android.content.Intent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;
    import android.widget.ImageButton;
    import androidx.annotation.NonNull;
    import androidx.cardview.widget.CardView;
    import androidx.core.content.ContextCompat;
    import androidx.recyclerview.widget.RecyclerView;
    import com.example.animatiappandroid.R;
    import com.example.animatiappandroid.PurchaseItem;

    import java.util.List;

    public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

        private List<PurchaseItem> purchaseList;
        private Context context;
        private OnRegretClickListener regretClickListener;

        public interface OnRegretClickListener {

            void onRregretClick(PurchaseItem item);
        }

        public PurchaseAdapter(Context context, List<PurchaseItem> purchaseList, OnRegretClickListener regretClickListener) {

            this.context = context;
            this.purchaseList = purchaseList;
            this.regretClickListener = regretClickListener;
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

            if(!item.getConfirmado()){

                holder.price.setTextColor(ContextCompat.getColor(this.context, R.color.red));
                holder.confirm.setVisibility(View.VISIBLE);
                holder.cardContainer.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.light_gray));
                holder.regret_button.setVisibility(View.GONE);
            } else { //Esto tambien es innescesario en realidad peeeero lo dejo por si las moscas.

                holder.confirm.setVisibility(View.GONE);
                holder.cardContainer.setCardBackgroundColor(ContextCompat.getColor(this.context, R.color.white));
                holder.regret_button.setVisibility(View.VISIBLE);
            }

            holder.regret_button.setOnClickListener(v -> {

                regretClickListener.onRregretClick(item);
            });
        }

        @Override
        public int getItemCount() {
            return purchaseList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView date, quantity, price, confirm;
            ImageButton regret_button;
            CardView cardContainer;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                date = itemView.findViewById(R.id.dateTextView);
                quantity = itemView.findViewById(R.id.quantityTextView);
                price = itemView.findViewById(R.id.priceTextView);
                confirm = itemView.findViewById(R.id.confirmTextView);
                regret_button = itemView.findViewById(R.id.regret_button);
                cardContainer = itemView.findViewById(R.id.card_container);
            }
        }
    }
