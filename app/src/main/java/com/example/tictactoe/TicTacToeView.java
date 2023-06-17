package com.example.tictactoe;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TicTacToeView extends View {

    private static final int GRID_SIZE = 3;
    private static final int CELL_COUNT = GRID_SIZE * GRID_SIZE;
    private float cellSize;  // Size of each cell
    private float cellPadding = 10f;  // Padding around each cell

    private Paint gridPaint;
    private Paint xPaint;
    private Paint oPaint;
    private Rect[] cellRects;
    private int[] cellOwners;
    private int currentPlayer;
    private boolean gameOver;
    private MainActivity mainActivity;
    private Random random;
    private List<Integer> availableCells;
    private int usedCells[];
    private int winningLine[];

    public TicTacToeView(Context context) {
        super(context);
        init();
    }

    public TicTacToeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(5f);

        xPaint = new Paint();
        xPaint.setColor(Color.RED);
        xPaint.setStrokeWidth(10f);

        oPaint = new Paint();
        oPaint.setColor(Color.BLACK);
        oPaint.setStrokeWidth(10f);

        cellRects = new Rect[CELL_COUNT];
        cellOwners = new int[CELL_COUNT];
        currentPlayer = 1;
        gameOver = false;
        random = new Random();
        availableCells = new ArrayList<>();
        usedCells = new int[9];
        winningLine = new int[3];

        for (int i = 0; i < CELL_COUNT; i++) {
            availableCells.add(i);
        }
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        // Calculate the cell size based on the available width and height
        float availableWidth = width - (2 * cellPadding);
        float availableHeight = height - (2 * cellPadding);
        cellSize = Math.min(availableWidth / 3f, availableHeight / 3f);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int cellSize = getWidth() / GRID_SIZE;

        for (int i = 0; i < CELL_COUNT; i++) {
            int row = i / GRID_SIZE;
            int col = i % GRID_SIZE;
            int cellLeft = col * cellSize;
            int cellTop = row * cellSize;
            int cellRight = cellLeft + cellSize;
            int cellBottom = cellTop + cellSize;
            cellRects[i] = new Rect(cellLeft, cellTop, cellRight, cellBottom);
        }
    }

    private int getCellIndexFromCoordinates(int x, int y) {
        for (int i = 0; i < CELL_COUNT; i++) {
            if (cellRects[i].contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int cellWidth = width / 3;
        int cellHeight = height / 3;

        gridPaint.setStrokeWidth(5);
        canvas.drawLine(cellWidth, 0, cellWidth, height, gridPaint);
        canvas.drawLine(cellWidth * 2, 0, cellWidth * 2, height, gridPaint);
        canvas.drawLine(0, cellHeight, width, cellHeight, gridPaint);
        canvas.drawLine(0, cellHeight * 2, width, cellHeight * 2, gridPaint);

        //Draw X and O for each cell
        gridPaint.setStrokeWidth(10);
        for (int i = 0; i < CELL_COUNT; i++) {
            int row = i / 3;
            int col = i % 3;
            int cellLeft = col * cellWidth;
            int cellTop = row * cellHeight;
            int cellRight = cellLeft + cellWidth;
            int cellBottom = cellTop + cellHeight;

            if (cellOwners[i] == 2) {
                canvas.drawOval(cellLeft, cellTop, cellRight, cellBottom, oPaint);
            } else if (cellOwners[i] == 1) {
                canvas.drawLine(cellLeft, cellTop, cellRight, cellBottom, xPaint);
                canvas.drawLine(cellRight, cellTop, cellLeft, cellBottom, xPaint);
            }
        }

        if (gameOver && winningLine != null) {
            striker(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !gameOver) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            int cellIndex = getCellIndexFromCoordinates(x, y);
            if (cellIndex != -1 && cellOwners[cellIndex] == 0) {
                cellOwners[cellIndex] = currentPlayer;
                invalidate();

                checkForWin();

                if (!gameOver) {
                    currentPlayer = 3 - currentPlayer; // Switch players
                    makeSystemPlay();
                    invalidate();
                }
            }
            usedCells[cellIndex] = cellIndex;
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void makeSystemPlay() {
        if (availableCells.size() > 0) {
            int randomIndex;
            do {
                randomIndex = random.nextInt(availableCells.size());
            } while (cellOwners[availableCells.get(randomIndex)] != 0);
            int cellIndex = availableCells.get(randomIndex);
            cellOwners[cellIndex] = currentPlayer;
            availableCells.remove(randomIndex);
            checkForWin();
            currentPlayer = 3 - currentPlayer; // Switch players
        }
    }

    private void addWinningLine(int a, int b, int c) {
        winningLine[0] = a;
        winningLine[1] = b;
        winningLine[2] = c;
    }

    private void checkForWin() {
        // Check rows
        for (int i = 0; i < GRID_SIZE; i++) {
            int startCellIndex = i * GRID_SIZE;
            int owner = cellOwners[startCellIndex];
            if (owner != 0 && cellOwners[startCellIndex + 1] == owner && cellOwners[startCellIndex + 2] == owner) {
                gameOver = true;
                addWinningLine(startCellIndex, startCellIndex + 1, startCellIndex + 2);
                mainActivity.onGameEnd(owner);
                return;
            }
        }

        // Check columns
        for (int i = 0; i < GRID_SIZE; i++) {
            int startCellIndex = i;
            int owner = cellOwners[startCellIndex];
            if (owner != 0 && cellOwners[startCellIndex + GRID_SIZE] == owner && cellOwners[startCellIndex + 2 * GRID_SIZE] == owner) {
                gameOver = true;
                addWinningLine(startCellIndex, startCellIndex + GRID_SIZE, startCellIndex + 2 * GRID_SIZE);
                System.out.println("$$$$$2" + startCellIndex);
                mainActivity.onGameEnd(owner);
                return;
            }
        }

        // Check diagonals
        if (cellOwners[0] != 0 && cellOwners[4] == cellOwners[0] && cellOwners[8] == cellOwners[0]) {
            gameOver = true;
            addWinningLine(0, 4, 8);
            mainActivity.onGameEnd(cellOwners[0]);
            return;
        }
        if (cellOwners[2] != 0 && cellOwners[4] == cellOwners[2] && cellOwners[6] == cellOwners[2]) {
            gameOver = true;
            addWinningLine(2, 4, 6);
            mainActivity.onGameEnd(cellOwners[2]);
            return;
        }

        // Check for a draw
        if (availableCells.isEmpty()) {
            gameOver = true;
            mainActivity.onGameEnd(0);
            return;
        }
    }

    private void striker(Canvas canvas) {
        // Set up paint for striker
        Paint strikerPaint = new Paint();
        strikerPaint.setColor(Color.RED);
        strikerPaint.setStrokeWidth(10f);
        strikerPaint.setStyle(Paint.Style.STROKE);
        strikerPaint.setStrokeCap(Paint.Cap.ROUND);
        float startX, startY, endX, endY;
        System.out.println(winningLine[0]);
        System.out.println(winningLine[1]);
        System.out.println(winningLine[2]);

        // Draw the strike based on the winning pattern
        if (winningLine[0] % 3 == winningLine[1] % 3 && winningLine[0] % 3 == winningLine[2] % 3) {
            // Vertical line
            startX = (winningLine[0] % 3) * cellSize + cellSize / 2;
            startY = (winningLine[0] / 3) * cellSize;
            endX = startX;
            endY = startY + cellSize * 3;
        } else if (winningLine[0] / 3 == winningLine[1] / 3 && winningLine[0] / 3 == winningLine[2] / 3) {
            // Horizontal line
            startX = (winningLine[0] % 3) * cellSize;
            startY = (winningLine[0] / 3) * cellSize + cellSize / 2;
            endX = startX + cellSize * 3;
            endY = startY;
        } else if ((winningLine[0] == 0 && winningLine[1] == 4 && winningLine[2] == 8) ||
                (winningLine[0] == 2 && winningLine[1] == 4 && winningLine[2] == 6)) {
            // Diagonal line (top-left to bottom-right or top-right to bottom-left)
            startX = 0;
            startY = 0;
            endX = cellSize * 3;
            endY = cellSize * 3;
        } else {
            // Invalid winning pattern
            return;
        }
        // Draw the strike line
        canvas.drawLine(startX, startY, endX, endY, strikerPaint);
    }

    public void resetGame() {
        for (int i = 0; i < CELL_COUNT; i++) {
            cellOwners[i] = 0;
        }
        currentPlayer = 1;
        gameOver = false;
        availableCells.clear();
        for (int i = 0; i < CELL_COUNT; i++) {
            availableCells.add(i);
        }
        for (int i = 0; i < 3; i++) {
            winningLine[i] = 0;
        }
        invalidate();
    }
}
