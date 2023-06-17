package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CompoundView compoundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compoundView = findViewById(R.id.ticTacToeView_compound_view);
        compoundView.setMainActivity(this);
    }

    public void onGameEnd(int winner) {
        if (winner == 0) {
            showToast("It's a draw!");
        } else {
            showToast("Player " + winner + " wins!");
        }
        compoundView.setClearButton(true);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}