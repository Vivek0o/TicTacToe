package com.example.tictactoe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CompoundView extends FrameLayout {

    private TicTacToeView ticTacToeView;
    private Button clearButton;

    public CompoundView(@NonNull Context context) {
        this(context, null);
    }

    public CompoundView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.compound_layout_view, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        clearButton = findViewById(R.id.clearButton);
        ticTacToeView = findViewById(R.id.ticTacToeView);

        this.clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                ticTacToeView.resetGame();
                clearButton.setEnabled(false);
            }
        });
    }

    public void setClearButton (boolean enable) {
        clearButton.setEnabled(enable);
    }

    public void setMainActivity (MainActivity mainActivity) {
        ticTacToeView.setMainActivity(mainActivity);
    }
}
