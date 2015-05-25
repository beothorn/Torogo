package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;

public class GoView extends View{

    public static final int MIN_BLOCK_SIZE = 40;
    public static final int MAX_BLOCK_SIZE = 200;
    private ScaleGestureDetector scaleGestureDetector;

    private final Paint paint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint whitePaint = new Paint();
    private final Paint shadowPaint = new Paint();

    private float mLastTouchX;
    private float mLastTouchY;

    private float mLastBoardX;
    private float mLastBoardY;

    private Bitmap img;
    private BitmapDrawable tileImg;

    private int blockSize;
    private int moveTolerance = 10;
    private int boardSize;
    private int boardX;
    private int boardY;
    private int rowSize = blockSize * boardSize;
    private GoGameController controller;

    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        img = BitmapFactory.decodeResource(getResources(), R.drawable.wood);//http://tiled-bg.blogspot.com.br/
        tileImg = new BitmapDrawable(getResources(), img);

        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setStrokeWidth(1);
        blackPaint.setAntiAlias(true);
        //blackPaint.setColor(Color.BLACK);

        whitePaint.setStyle(Paint.Style.FILL);
        whitePaint.setStrokeWidth(1);
        whitePaint.setAntiAlias(true);
        whitePaint.setColor(Color.WHITE);

        shadowPaint.setMaskFilter(new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL));
        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setStyle(Paint.Style.FILL);

        paint.setAntiAlias(true);

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
        this.controller = controller;
        boardSize = controller.getSize();
    }

    public void play(int column, int line) {
        controller.play(line, column);
    }

    private void updateBlockSize(int newSize) {
        if(newSize < MIN_BLOCK_SIZE)
            newSize = MIN_BLOCK_SIZE;
        if(newSize > MAX_BLOCK_SIZE)
            newSize = MAX_BLOCK_SIZE;
        blockSize = newSize;
        rowSize = blockSize * boardSize;
        invalidate();
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
                }


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

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(blockSize == 0) updateBlockSize(getMeasuredWidth() / boardSize);

        if(controller == null) return;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(240, 182, 98));
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), paint);

        int saveCount = canvas.save();
        try {
            int width = img.getWidth();
            int height = img.getHeight();
            if(boardX != 0 && boardY != 0 && blockSize != 0) {
                int dx = (boardX % width) - width;
                int dy = (boardY % height) - height;
                canvas.translate(dx, dy);
            }

            tileImg.setBounds(0, 0, getMeasuredWidth()+width, getMeasuredHeight()+height);
            tileImg.setTileModeXY(Shader.TileMode.REPEAT.REPEAT, Shader.TileMode.REPEAT.REPEAT);
            tileImg.draw(canvas);
        } finally {
            canvas.restoreToCount(saveCount);
        }

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
                drawGrid(currentBoardX, currentBoardY, canvas);
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
                    blackPaint.setShader(new RadialGradient((column * blockSize) + onBoardX, (line * blockSize) + onBoardY,blockSize * 2,Color.BLACK,Color.DKGRAY, Shader.TileMode.MIRROR));
                    canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 2, blackPaint);
                    whitePaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 2, whitePaint);
                }else{
                    whitePaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 2, whitePaint);
                    blackPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 2, blackPaint);
                }



                blackPaint.setShader(null);
                if(controller.stoneAtPositionIsLastPlayedStone(line, column)){
                    if(controller.getPieceAt(line, column).equals(GoBoard.StoneColor.BLACK)){
                        whitePaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 4, whitePaint);
                    }else{
                        blackPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle((column * blockSize) + onBoardX, (line * blockSize) + onBoardY, blockSize / 4, blackPaint);
                    }

                }
            }
        }
    }

    private void drawGrid(int onBoardX, int onBoardY, Canvas canvas) {
        drawGrid(onBoardX, onBoardY, canvas, Color.DKGRAY, 4);
        drawGrid(onBoardX, onBoardY, canvas, Color.BLACK, 2);
    }

    private void drawGrid(int onBoardX, int onBoardY, Canvas canvas, int color, int size) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(size);
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
}