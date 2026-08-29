package com.example.phishdetector;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.phishdetector.model.ScanHistory;
import com.example.phishdetector.utils.PreferenceHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private MaterialButton btnClear;
    private PreferenceHelper preferenceHelper;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerView = findViewById(R.id.recyclerHistory);
        emptyState = findViewById(R.id.emptyState);
        btnClear = findViewById(R.id.btnClear);

        preferenceHelper = new PreferenceHelper(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadHistory();

        btnClear.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Clear History")
                    .setMessage("Delete all scan history?")
                    .setPositiveButton("Clear", (d, w) -> {
                        preferenceHelper.clearHistory();
                        loadHistory();
                        Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void loadHistory() {
        List<ScanHistory> list = preferenceHelper.getHistory();
        if (list.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new HistoryAdapter(list);
            recyclerView.setAdapter(adapter);
        }
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        private final List<ScanHistory> items;
        private final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());

        HistoryAdapter(List<ScanHistory> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ScanHistory item = items.get(position);

            holder.tvRisk.setText(item.getRisk());
            holder.tvScore.setText(item.getScore() + "%");
            holder.tvText.setText(item.getText());
            holder.tvTime.setText(sdf.format(new Date(item.getTimestamp())));

            int color;
            if ("High Risk".equalsIgnoreCase(item.getRisk())) {
                color = ContextCompat.getColor(HistoryActivity.this, R.color.risk_high);
            } else if ("Suspicious".equalsIgnoreCase(item.getRisk())) {
                color = ContextCompat.getColor(HistoryActivity.this, R.color.risk_suspicious);
            } else {
                color = ContextCompat.getColor(HistoryActivity.this, R.color.risk_safe);
            }
            holder.cardRisk.setCardBackgroundColor(color);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(HistoryActivity.this, ResultActivity.class);
                intent.putExtra("risk", item.getRisk());
                intent.putExtra("score", item.getScore());
                intent.putExtra("explanation", item.getExplanation());
                intent.putExtra("action", item.getAction());
                intent.putExtra("original_text", item.getText());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            MaterialCardView cardRisk;
            TextView tvRisk, tvScore, tvText, tvTime;

            ViewHolder(View itemView) {
                super(itemView);
                cardRisk = itemView.findViewById(R.id.cardRisk);
                tvRisk = itemView.findViewById(R.id.tvRisk);
                tvScore = itemView.findViewById(R.id.tvScore);
                tvText = itemView.findViewById(R.id.tvText);
                tvTime = itemView.findViewById(R.id.tvTime);
            }
        }
    }
}
