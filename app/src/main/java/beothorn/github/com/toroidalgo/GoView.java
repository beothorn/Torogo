package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;

public class GoView extends View{

    private final Paint paint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint whitePaint = new Paint();
    private final Paint shadowPaint = new Paint();

    private float mLastTouchX;
    private float mLastTouchY;

    private float mLastBoardX;
    private float mLastBoardY;

    private int blockSize = 30;
    private int moveTolerance = 10;
    private int boardSize = 9;
    private int boardX = 10;
    private int boardY = 10;
    private int rowSize = blockSize * boardSize;
    private GoGameController controller;

    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setStrokeWidth(3);
        blackPaint.setColor(Color.BLACK);

        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setStrokeWidth(2);
        whitePaint.setColor(Color.WHITE);

        shadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setStyle(Paint.Style.FILL);

        paint.setStrokeWidth(3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_SCROLL: {
                blockSize += ev.getAxisValue(MotionEvent.AXIS_VSCROLL);
                System.out.println(blockSize);

                break;
            }

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

                final float dx = x - mLastTouchX;
                final float dy = y - mLastTouchY;

                boardX += dx;
                boardY += dy;

                if(boardX < 0){
                    boardX = boardX + (boardSize * blockSize);
                }else if(boardX > getMeasuredWidth()){
                    boardX = boardX - (boardSize * blockSize);
                }

                if(boardY < 0){
                    boardY = boardY + (boardSize * blockSize);
                }else if(boardY > getMeasuredHeight()){
                    boardY = boardY - (boardSize * blockSize);
                }

                mLastTouchX = x;
                mLastTouchY = y;

                invalidate();
                break;
            }

            case MotionEvent.ACTION_UP: {
                mLastTouchX = ev.getX();
                mLastTouchY = ev.getY();

                float boardDeltaX = Math.abs(mLastBoardX - boardX);
                float boardDeltaY = Math.abs(mLastBoardY - boardY);

                if(boardDeltaX < moveTolerance && boardDeltaY < moveTolerance){
                    int column = Math.round((mLastTouchX - boardX) / blockSize) % boardSize;
                    if(column < 0){
                        column = boardSize + column;
                    }
                    int line = Math.round((mLastTouchY - boardY) / blockSize) % boardSize;
                    if(line < 0){
                        line = boardSize + line;
                    }
                    play(column, line);
                }
                invalidate();

                break;
            }
        }

        return true;
    }

    public void play(int column, int line) {
        controller.play(line, column);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(controller == null) return;

        int min = Math.min(getMeasuredWidth(),getMeasuredHeight());

        int newBlockSize = min / (boardSize + 2);
        if(blockSize != newBlockSize){
            blockSize = newBlockSize;
            rowSize = blockSize * (boardSize);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(240, 182, 98));
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);


        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.wood);//http://tiled-bg.blogspot.com.br/
        BitmapDrawable tileImg = new BitmapDrawable(getResources(), img);
        tileImg.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        tileImg.setTileModeXY(Shader.TileMode.REPEAT.REPEAT, Shader.TileMode.REPEAT.REPEAT);
        tileImg.draw(canvas);

        int onBoardX = boardX;
        int onBoardY = boardY;

        int startDrawingX = onBoardX;
        while(startDrawingX > 0){
            startDrawingX -= (blockSize*(boardSize));
        }

        int startDrawingY = onBoardY;
        while(startDrawingY > 0){
            startDrawingY -= (blockSize*(boardSize));
        }

        int currentBoardX = startDrawingX;
        int currentBoardY = startDrawingY;
        while(currentBoardX < getMeasuredWidth() && currentBoardY < getMeasuredHeight() ){
            while(currentBoardX < getMeasuredWidth()){
                drawGrid(currentBoardX, currentBoardY, canvas, Color.BLACK);
                drawPieces(currentBoardX, currentBoardY, canvas);

                currentBoardX += (blockSize*(boardSize));
            }

            currentBoardX = startDrawingX;
            currentBoardY += (blockSize*(boardSize));
        }
    }

    private void drawPieces(int onBoardX, int onBoardY, Canvas canvas) {
        for(int line = 0; line < boardSize; line++){
            for(int column = 0; column < boardSize; column++){
                if(controller.getPieceAt(line, column) == null) continue;
                int shadowDistance = 4;
                canvas.drawCircle(((column * blockSize) + onBoardX) +shadowDistance, ((line * blockSize) + onBoardY) +shadowDistance, blockSize / 2, shadowPaint);

            }
        }

        for(int line = 0; line < boardSize; line++){
            for(int column = 0; column < boardSize; column++){
                if(controller.getPieceAt(line, column) == null) continue;

                if(controller.getPieceAt(line, column).equals(GoBoard.StoneColor.BLACK)){
                    canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 2, blackPaint);
                }else{
                    canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 2, whitePaint);
                }



                if(controller.stoneAtPositionIsLastPlayedStone(line, column)){
                    if(controller.getPieceAt(line, column).equals(GoBoard.StoneColor.BLACK)){
                        canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 4, whitePaint);
                    }else{
                        canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 4, blackPaint);
                    }

                }
            }
        }
    }

    private void drawGrid(int onBoardX, int onBoardY, Canvas canvas, int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        for(int i = 0; i < boardSize ; i++){
            int start = i * blockSize;

            int lineLeft = onBoardX;
            int lineTop = onBoardY + start;
            int lineRight = onBoardX + rowSize;
            int lineBottom = lineTop + blockSize;

            int columnLeft = onBoardX + start;
            int columnTop = onBoardY ;
            int columnRight = columnLeft + blockSize;
            int columnBottom = onBoardY + rowSize;

            canvas.drawRect(lineLeft, lineTop, lineRight, lineBottom, paint);
            canvas.drawRect(columnLeft, columnTop, columnRight, columnBottom, paint);
        }
    }

    public void setController(GoGameController controller) {
        this.controller = controller;
        boardSize = controller.getSize();
    }
}