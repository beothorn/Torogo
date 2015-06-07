package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import beothorn.github.com.toroidalgo.painters.BackgroundPainter;
import beothorn.github.com.toroidalgo.painters.BoardPainter;
import beothorn.github.com.toroidalgo.painters.TextPainter;

public class GoView extends View{

    public static final int MIN_BLOCK_SIZE = 40;
    public static final int MAX_BLOCK_SIZE = 200;
    private ScaleGestureDetector scaleGestureDetector;

    private BackgroundPainter backgroundPainter;
    private BoardPainter boardPainter;
    private TextPainter textPainter;

    private float mLastTouchX;
    private float mLastTouchY;

    private float mLastBoardX;
    private float mLastBoardY;

    private int moveTolerance = 10;

    private int blockSize;
    private int boardSlotsCount;
    private int boardX;
    private int boardY;
    private GoGameController controller;


    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        backgroundPainter = new BackgroundPainter(this);
        boardPainter = new BoardPainter();
        textPainter = new TextPainter();

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float span = detector.getCurrentSpan();
                float scaleFactor = detector.getScaleFactor();

                rescale(span, scaleFactor);
                return true;
            }

            private void rescale(float span, float scaleFactor) {
                int scale = 100;
                if(scaleFactor < 1){
                    scale *= -1;
                }
                updateBlockSize((int) (blockSize + (span/scale)));
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) { return true; }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {}
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

        redraw();
    }

    public void redraw() {
        boardPainter.updateBoard(controller, boardSlotsCount, blockSize);
        invalidate();
    }

    public void setText(String text){
        textPainter.setText(text);
    }

    public void clearText(){
        textPainter.clearText();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleGestureDetector.onTouchEvent(ev);

        final int action = ev.getAction();
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
                mLastTouchX = ev.getX();
                mLastTouchY = ev.getY();

                float boardDeltaX = Math.abs(mLastBoardX - boardX);
                float boardDeltaY = Math.abs(mLastBoardY - boardY);

                if(boardDeltaX < moveTolerance && boardDeltaY < moveTolerance){
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

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(controller == null) return;

        backgroundPainter.paintOn(canvas, boardX, boardY, blockSize);
        boardPainter.paint(canvas, blockSize, boardSlotsCount, boardX, boardY, getMeasuredWidth(), getMeasuredHeight());
        textPainter.paintText(canvas);
    }

    public String asString() {
        return boardX+","+boardY+","+blockSize+","+boardSlotsCount;
    }

    public void recoverFromString(String viewState) {
        String[] splitted = viewState.split(",");
        boardX = Integer.valueOf(splitted[0]);
        boardY = Integer.valueOf(splitted[1]);
        blockSize = Integer.valueOf(splitted[2]);
        boardSlotsCount = Integer.valueOf(splitted[3]);
    }
}