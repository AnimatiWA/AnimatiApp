package com.example.animatiappandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;
// Importaciones para CartActivity
import android.content.Intent;
import android.widget.Button;

public class activity_inicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        Button buttonGaleria = findViewById(R.id.button1);
        buttonGaleria.setOnClickListener(view -> {
            Intent intent = new Intent(activity_inicio.this, Gallery.class);
            startActivity(intent);
        });

        Button buttonMisDatos = findViewById(R.id.button3);
        buttonMisDatos.setOnClickListener(view -> {
            Intent intent = new Intent(activity_inicio.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button buttonContacto = findViewById(R.id.button2);
        buttonContacto.setOnClickListener(view -> {
            Intent intent = new Intent(activity_inicio.this, ContactoActivity.class);
            startActivity(intent);
        });

        Button redirectButton = findViewById(R.id.button_redirect_website);
        redirectButton.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.animati.com.ar"));
            startActivity(browserIntent);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));  // Grid de 2 columnas

        List<Card> cardList = new ArrayList<>();
        cardList.add(new Card("Caja Anime", "Temáticas para guardar tus cosas", R.drawable.cajas));
        cardList.add(new Card("Colgantes ", "De tus personajes favoritos", R.drawable.colgantes));
        cardList.add(new Card("Separadores ", "Marcá las páginas de tus libros", R.drawable.separadores));
        cardList.add(new Card("Set de Stikers ", "Para pegar en todos tus espacios", R.drawable.stikers));
        cardList.add(new Card("Cubecraft", "Llevate a tu muñeco preferido", R.drawable.cubecraft));
        cardList.add(new Card("Anotadores", "Con tu diseño favoritos", R.drawable.anotadores));

        CardAdapter cardAdapter = new CardAdapter(cardList);
        recyclerView.setAdapter(cardAdapter);
    }


    class Card {
        String title;
        String description;
        int imageResource;

        Card(String title, String description, int imageResource) {
            this.title = title;
            this.description = description;
            this.imageResource = imageResource;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public int getImageResource() {
            return imageResource;
        }
    }


    class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

        private List<Card> cardList;

        CardAdapter(List<Card> cardList) {
            this.cardList = cardList;
        }

        @Override
        public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
            return new CardViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CardViewHolder holder, int position) {
            Card card = cardList.get(position);
            holder.cardTitle.setText(card.getTitle());
            holder.cardDescription.setText(card.getDescription());
            holder.cardImage.setImageResource(card.getImageResource());
        }

        @Override
        public int getItemCount() {
            return cardList.size();
        }

        class CardViewHolder extends RecyclerView.ViewHolder {
            ImageView cardImage;
            TextView cardTitle, cardDescription;

            CardViewHolder(View itemView) {
                super(itemView);
                cardImage = itemView.findViewById(R.id.card_image);
                cardTitle = itemView.findViewById(R.id.card_title);
                cardDescription = itemView.findViewById(R.id.card_description);
            }
        }
    }
}
