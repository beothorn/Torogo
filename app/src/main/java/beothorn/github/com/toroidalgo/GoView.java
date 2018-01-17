package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import beothorn.github.com.toroidalgo.go.impl.logging.GoLogger;
import beothorn.github.com.toroidalgo.painters.BackgroundPainter;
import beothorn.github.com.toroidalgo.painters.BoardPainter;
import beothorn.github.com.toroidalgo.painters.TextPainter;

public class GoView extends View{

    public static final String CLASSIFIED_NAME = "beothorn.github.com.toroidalgo.GoView";

    public static final int MIN_BLOCK_SIZE = 40;
    public static final int MAX_BLOCK_SIZE = 200;

    private static final int MOVE_TOLERANCE = 20;
    private ScaleGestureDetector scaleGestureDetector;
    private BackgroundPainter backgroundPainter;
    private Paint paintCrosshair;
    private int sizeOfCrosshair = 120;

    private BoardPainter boardPainter;
    private TextPainter textPainter;

    private float mLastTouchX;
    private float mLastTouchY;

    private float mLastBoardX;

    private float mLastBoardY;
    private int blockSize;
    private int boardSlotsCount;
    private int boardX;
    private int boardY;


    private GoGameController controller;
    private long zoomEventEndingTimestamp;

    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        backgroundPainter = new BackgroundPainter(this);
        boardPainter = new BoardPainter();
        textPainter = new TextPainter();

        paintCrosshair = new Paint();
        paintCrosshair.setColor(Color.RED);
        paintCrosshair.setStyle(Paint.Style.STROKE);
        paintCrosshair.setStrokeWidth(5f);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float span = detector.getCurrentSpan();
                float scaleFactor = detector.getScaleFactor();
                float focusX = detector.getFocusX();
                float focusY = detector.getFocusY();
                rescale(span, scaleFactor, focusX, focusY);
                return true;
            }

            private void rescale(float span, float scaleFactor, float focusX, float focusY) {
                if( Math.abs(1 - scaleFactor) < 0.001) return;
                int scale = 100;
                if(scaleFactor < 1){
                    scale *= -1;
                }

                float focusBlockX = (focusX - boardX)/ blockSize;
                float focusBlockY = (focusY - boardY) / blockSize;

                updateBlockSize((int) (blockSize + (span / scale)));

                boardX = (int) (focusX - (blockSize * focusBlockX));
                boardY = (int) (focusY - (blockSize * focusBlockY));
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) { return true; }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                zoomEventEndingTimestamp = System.currentTimeMillis();
            }
        });
    }

    public void setController(GoGameController controller) {
        boardSlotsCount = controller.getSize();
        this.controller = controller;
        updateBlockSize((MAX_BLOCK_SIZE+MIN_BLOCK_SIZE)/2);
    }

    public void play(int column, int line) {
        controller.play(line, column);
    }

    private void updateBlockSize(int newSize) {
        if(newSize == blockSize) return;
        if(newSize < MIN_BLOCK_SIZE)
            newSize = MIN_BLOCK_SIZE;
        if(newSize > MAX_BLOCK_SIZE)
            newSize = MAX_BLOCK_SIZE;
        blockSize = newSize;

        redrawBoard();
    }

    public void redrawBoard() {
        boardPainter.updateBoard(controller, boardSlotsCount, blockSize);
        invalidate();
    }

    public void setText(String text){
        GoLogger.log("Set Text to:" + text);
        textPainter.setText(text);
        redrawBoard();
    }

    public void clearText(){
        GoLogger.log("Clear text");
        textPainter.clearText();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);

        final int action = ev.getAction();

        long zoomWaitingInterval = 400;
        if (System.currentTimeMillis() - zoomEventEndingTimestamp < zoomWaitingInterval)
            return true;

        switch (action & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;

                mLastBoardX = boardX;
                mLastBoardY = boardY;

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final float x = ev.getX();
                final float y = ev.getY();

                if (!scaleGestureDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    boardX += dx;
                    if(boardX < 0){
                        boardX = boardX + (boardSlotsCount * blockSize);
                    }else if(boardX > getMeasuredWidth()){
                        boardX = boardX - (boardSlotsCount * blockSize);
                    }

                    boardY += dy;
                    if(boardY < 0){
                        boardY = boardY + (boardSlotsCount * blockSize);
                    }else if(boardY > getMeasuredHeight()){
                        boardY = boardY - (boardSlotsCount * blockSize);
                    }

                    mLastTouchX = x;
                    mLastTouchY = y;

                    invalidate();
                }


                break;
            }

            case MotionEvent.ACTION_UP: {
                mLastTouchX = centerX();
                mLastTouchY = centerY();

                float boardDeltaX = Math.abs(mLastBoardX - boardX);
                float boardDeltaY = Math.abs(mLastBoardY - boardY);

                if(boardDeltaX < MOVE_TOLERANCE && boardDeltaY < MOVE_TOLERANCE){
                    int column = Math.round((mLastTouchX - boardX) / blockSize) % boardSlotsCount;
                    if(column < 0){
                        column = boardSlotsCount + column;
                    }
                    int line = Math.round((mLastTouchY - boardY) / blockSize) % boardSlotsCount;
                    if(line < 0){
                        line = boardSlotsCount + line;
                    }
                    play(column, line);
                    boardPainter.updateBoard(controller, boardSlotsCount, blockSize);
                }
                invalidate();

                break;
            }
        }

        return true;
    }

    private int centerY() {
        return getMeasuredHeight() / 2;
    }

    private int centerX() {
        return getMeasuredWidth() / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(controller == null) return;

        backgroundPainter.paintOn(canvas, boardX, boardY, blockSize);
        boardPainter.paint(canvas, blockSize, boardSlotsCount, boardX, boardY, getMeasuredWidth(), getMeasuredHeight());
        textPainter.paintText(canvas);

        if (controller.isMyTurn())
            drawCrosshair(canvas);
    }

    private void drawCrosshair(Canvas canvas) {
        int left = centerX() - sizeOfCrosshair / 2;
        int top = centerY() - sizeOfCrosshair / 2;
        int right = centerX() + sizeOfCrosshair / 2;
        int bottom = centerY() + sizeOfCrosshair / 2;
        canvas.drawRect(left, top, right, bottom, paintCrosshair);
    }

    public void save(Bundle outState) {
        outState.putInt(CLASSIFIED_NAME + "boardX", boardX);
        outState.putInt(CLASSIFIED_NAME + "boardY", boardY);
        outState.putInt(CLASSIFIED_NAME + "blockSize", blockSize);
        outState.putInt(CLASSIFIED_NAME + "boardSlotsCount", boardSlotsCount);
        outState.putString(CLASSIFIED_NAME + "textPainter.getText", textPainter.getText());
    }

    public void recoverFrom(Bundle savedInstanceState) {
        boardX = savedInstanceState.getInt(CLASSIFIED_NAME + "boardX");
        boardY = savedInstanceState.getInt(CLASSIFIED_NAME + "boardY");
        blockSize = savedInstanceState.getInt(CLASSIFIED_NAME + "blockSize");
        boardSlotsCount = savedInstanceState.getInt(CLASSIFIED_NAME + "boardSlotsCount");
        textPainter.setText(savedInstanceState.getString(CLASSIFIED_NAME + "textPainter.getText"));

        redrawBoard();
    }
}