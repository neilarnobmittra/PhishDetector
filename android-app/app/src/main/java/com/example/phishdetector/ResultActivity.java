package com.example.phishdetector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class ResultActivity extends AppCompatActivity {

    private TextView tvRiskBadge, tvScore, tvExplanation, tvAction, tvOriginal, tvRiskEmoji;
    private MaterialCardView cardRisk;
    private MaterialButton btnShare, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        tvRiskBadge = findViewById(R.id.tvRiskBadge);
        tvScore = findViewById(R.id.tvScore);
        tvExplanation = findViewById(R.id.tvExplanation);
        tvAction = findViewById(R.id.tvAction);
        tvOriginal = findViewById(R.id.tvOriginal);
        tvRiskEmoji = findViewById(R.id.tvRiskEmoji);
        cardRisk = findViewById(R.id.cardRisk);
        btnShare = findViewById(R.id.btnShare);
        btnBack = findViewById(R.id.btnBack);

        Intent intent = getIntent();
        String risk = intent.getStringExtra("risk");
        int score = intent.getIntExtra("score", 0);
        String explanation = intent.getStringExtra("explanation");
        String action = intent.getStringExtra("action");
        String original = intent.getStringExtra("original_text");

        tvRiskBadge.setText(risk != null ? risk : "Unknown");
        tvScore.setText(score + "% Confidence");
        tvExplanation.setText(explanation != null ? explanation : "-");
        tvAction.setText(action != null ? action : "-");
        tvOriginal.setText(original != null ? original : "-");

        // Color + emoji based on risk
        int color;
        String emoji;
        if ("High Risk".equalsIgnoreCase(risk)) {
            color = ContextCompat.getColor(this, R.color.risk_high);
            emoji = "🚨";
        } else if ("Suspicious".equalsIgnoreCase(risk)) {
            color = ContextCompat.getColor(this, R.color.risk_suspicious);
            emoji = "⚠️";
        } else {
            color = ContextCompat.getColor(this, R.color.risk_safe);
            emoji = "✅";
        }

        cardRisk.setCardBackgroundColor(color);
        tvRiskBadge.setTextColor(Color.WHITE);
        tvScore.setTextColor(Color.WHITE);
        tvRiskEmoji.setText(emoji);

        btnShare.setOnClickListener(v -> shareResult(risk, score, explanation, action));
        btnBack.setOnClickListener(v -> finish());
    }

    private void shareResult(String risk, int score, String explanation, String action) {
        String shareText = "Phishing Analysis Result\n\n"
                + "Risk Level: " + risk + "\n"
                + "Confidence: " + score + "%\n\n"
                + "Explanation:\n" + explanation + "\n\n"
                + "Recommended Action:\n" + action + "\n\n"
                + "— Analyzed with PhishDetector App";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Phishing Scan Result");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }
}
