package com.example.animatiappandroid;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResumenVentasActivity extends AppCompatActivity {

    private String token;

    private TextView tvTotalVentas, tvTotalIngresos, tvProductosVendidos;
    private BarChart barChart;
    private static final String URL_ENDPOINT = "https://animatiapp.up.railway.app/api/pedidos/resumenCompras";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumen_ventas);
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Resumen de Ventas");
        }

        SharedPreferences preferences = getSharedPreferences("AnimatiPreferencias", MODE_PRIVATE);
        token = preferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "No tienes permisos para ver este resumen", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvTotalVentas = findViewById(R.id.tvTotalVentas);
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos);
        tvProductosVendidos = findViewById(R.id.tvProductosVendidos);
        barChart = findViewById(R.id.barChart);
        
        
        findViewById(R.id.btnVolver).setOnClickListener(v -> onBackPressed());
        
        configurarGrafico();
        cargarResumenVentasDesdeAPI();
    }
    
    private void configurarGrafico() {
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setDrawBarShadow(false);
        barChart.setDrawGridBackground(false);
        
        Legend legend = barChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(true);
        
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f);
        
        barChart.getAxisRight().setEnabled(false);
        barChart.animateY(1000);
    }

    private void cargarResumenVentasDesdeAPI() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL_ENDPOINT,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int totalVentas = response.getInt("total_ventas");
                            double totalIngresos = response.getDouble("total_ingresos");
                            int productosVendidos = response.getInt("productos_vendidos");

                            tvTotalVentas.setText(String.valueOf(totalVentas));
                            tvTotalIngresos.setText("$" + totalIngresos);
                            tvProductosVendidos.setText("Productos vendidos: " + productosVendidos);
                            actualizarGrafico(totalVentas, totalIngresos);

                        } catch (JSONException e) {
                            Toast.makeText(ResumenVentasActivity.this, "Error parseando los datos", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String mensajeError = "Error en la conexión";
                        if (error.networkResponse != null && error.networkResponse.statusCode == 401) {
                            mensajeError = "No autorizado. Solo el administrador puede acceder.";
                        }
                        Toast.makeText(ResumenVentasActivity.this, mensajeError, Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }
    
    private void actualizarGrafico(int totalVentas, double totalIngresos) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, totalVentas));
        entries.add(new BarEntry(1f, (float) totalIngresos));
        
        BarDataSet dataSet = new BarDataSet(entries, "Resumen de Ventas");
        dataSet.setColors(new int[] {Color.rgb(64, 89, 128), Color.rgb(149, 165, 124)});
        dataSet.setDrawValues(true);
        
        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);
        
        String[] labels = new String[] {"Ventas", "Ingresos"};
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        
        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.invalidate();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
