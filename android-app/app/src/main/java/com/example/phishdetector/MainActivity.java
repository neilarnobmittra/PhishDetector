package com.example.phishdetector;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.phishdetector.api.AnalyzeRequest;
import com.example.phishdetector.api.AnalyzeResponse;
import com.example.phishdetector.api.ApiService;
import com.example.phishdetector.api.RetrofitClient;
import com.example.phishdetector.model.ScanHistory;
import com.example.phishdetector.utils.PreferenceHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etMessage;
    private MaterialButton btnAnalyze;
    private MaterialButton btnHistory;
    private FrameLayout loadingOverlay;
    private PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etMessage = findViewById(R.id.etMessage);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnHistory = findViewById(R.id.btnHistory);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        preferenceHelper = new PreferenceHelper(this);

        btnAnalyze.setOnClickListener(v -> analyzeMessage());
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });
    }

    private void analyzeMessage() {
        String text = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";

        if (TextUtils.isEmpty(text)) {
            etMessage.setError("Please paste a message or URL");
            return;
        }

        if (text.length() < 5) {
            etMessage.setError("Message is too short");
            return;
        }

        setLoading(true);

        ApiService api = RetrofitClient.getApiService();
        Call<AnalyzeResponse> call = api.analyzeText(new AnalyzeRequest(text));

        call.enqueue(new Callback<AnalyzeResponse>() {
            @Override
            public void onResponse(Call<AnalyzeResponse> call, Response<AnalyzeResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    AnalyzeResponse result = response.body();

                    // Save to history
                    ScanHistory historyItem = new ScanHistory(
                            text,
                            result.getRisk(),
                            result.getScore(),
                            result.getExplanation(),
                            result.getAction(),
                            System.currentTimeMillis()
                    );
                    preferenceHelper.saveScan(historyItem);

                    // Open result screen
                    Intent intent = new Intent(MainActivity.this, ResultActivity.class);
                    intent.putExtra("risk", result.getRisk());
                    intent.putExtra("score", result.getScore());
                    intent.putExtra("explanation", result.getExplanation());
                    intent.putExtra("action", result.getAction());
                    intent.putExtra("original_text", text);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Server error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<AnalyzeResponse> call, Throwable t) {
                setLoading(false);
                Toast.makeText(MainActivity.this,
                        "Network error. Is the backend running?\n" + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean loading) {
        loadingOverlay.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnAnalyze.setEnabled(!loading);
        btnHistory.setEnabled(!loading);
    }
}
