package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import beothorn.github.com.toroidalgo.painters.BoardPainter;

public class GoView extends View{

    public static final int MIN_BLOCK_SIZE = 40;
    public static final int MAX_BLOCK_SIZE = 200;
    private ScaleGestureDetector scaleGestureDetector;

    private BoardPainter boardPainter;

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


    private Bitmap boardBitmap;
    private Canvas boardCanvas;
    private Paint boardPaint = new Paint();

    private int blockSize;
    private int moveTolerance = 10;
    private int boardSlotsCount;
    private int boardX;
    private int boardY;
    private int rowSize = blockSize * boardSlotsCount;
    private GoGameController controller;


    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        boardPainter = new BoardPainter(this);

        blackPaint.setStyle(Paint.Style.FILL);
        blackPaint.setStrokeWidth(1);
        blackPaint.setAntiAlias(true);

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

    private void redrawBoard() {
        drawGrid(boardCanvas);
        drawPieces(boardCanvas);
    }

    public void setController(GoGameController controller) {
        this.controller = controller;
        boardSlotsCount = controller.getSize();
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
        rowSize = blockSize * boardSlotsCount;
        boardBitmap = Bitmap.createBitmap(rowSize, rowSize, Bitmap.Config.ARGB_8888);
        boardCanvas = new Canvas(boardBitmap);
        redrawBoard();
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
                    redrawBoard();
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

        if(blockSize == 0) updateBlockSize(getMeasuredWidth() / boardSlotsCount);

        if(controller == null) return;

        boardPainter.onDraw(canvas, boardX, boardY, blockSize);

        int onBoardX = boardX;
        int onBoardY = boardY;

        int boardSize = blockSize * boardSlotsCount;

        int startDrawingX = onBoardX-((onBoardX / boardSize)+1) * boardSize;
        int startDrawingY = onBoardY-((onBoardY / boardSize)+1) * boardSize;

        int currentBoardX = startDrawingX;
        int currentBoardY = startDrawingY;

        while(currentBoardX < getMeasuredWidth() && currentBoardY < getMeasuredHeight() ){
            while(currentBoardX < getMeasuredWidth()){
                canvas.drawBitmap(boardBitmap, currentBoardX, currentBoardY, paint);

                currentBoardX += (blockSize*(boardSlotsCount));
            }

            currentBoardX = startDrawingX;
            currentBoardY += (blockSize*(boardSlotsCount));
        }
    }

    private void drawPieces(Canvas canvas) {
        for(int line = 0; line <= boardSlotsCount; line++){
            for(int column = 0; column <= boardSlotsCount; column++){
                if(controller.getPieceAt(line%boardSlotsCount, column%boardSlotsCount) == null) continue;
                int shadowDistance = 4;
                canvas.drawCircle(column * blockSize +shadowDistance, line * blockSize +shadowDistance, blockSize / 2, shadowPaint);
            }
        }

        for(int line = 0; line <= boardSlotsCount; line++){
            for(int column = 0; column <= boardSlotsCount; column++){
                GoBoard.StoneColor pieceAt = controller.getPieceAt(line%boardSlotsCount, column%boardSlotsCount);
                if(pieceAt == null) continue;

                int cx = column * blockSize;
                int cy = line * blockSize;
                int radius = blockSize / 2;
                if(pieceAt.equals(GoBoard.StoneColor.BLACK)){
                    blackPaint.setShader(new RadialGradient(cx, cy,blockSize * 2,Color.BLACK,Color.DKGRAY, Shader.TileMode.MIRROR));
                    canvas.drawCircle(cx, cy, radius, blackPaint);
                    whitePaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(cx, cy, radius, whitePaint);
                }else{
                    whitePaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(cx, cy, radius, whitePaint);
                    blackPaint.setStyle(Paint.Style.STROKE);
                    canvas.drawCircle(cx, cy, radius, blackPaint);
                }

                blackPaint.setShader(null);
                if(controller.stoneAtPositionIsLastPlayedStone(line%boardSlotsCount, column%boardSlotsCount)){
                    if(pieceAt.equals(GoBoard.StoneColor.BLACK)){
                        whitePaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(cx, cy, blockSize / 4, whitePaint);
                    }else{
                        blackPaint.setStyle(Paint.Style.FILL);
                        canvas.drawCircle(cx, cy, blockSize / 4, blackPaint);
                    }

                }
            }
        }
    }

    private void drawGrid(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        drawGrid(canvas, Color.DKGRAY, 4);
        drawGrid(canvas, Color.BLACK, 2);
    }

    private void drawGrid(Canvas canvas, int color, int size) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        for(int i = 0; i < boardSlotsCount; i++){
            int start = i * blockSize;

            int lineLeft = 0;
            int lineTop = start;
            int lineRight =  rowSize;
            int lineBottom = lineTop + blockSize;

            int columnLeft =  start;
            int columnTop = 0 ;
            int columnRight = columnLeft + blockSize;
            int columnBottom = rowSize;

            canvas.drawRect(lineLeft, lineTop, lineRight, lineBottom, paint);
            canvas.drawRect(columnLeft, columnTop, columnRight, columnBottom, paint);
        }
    }
}