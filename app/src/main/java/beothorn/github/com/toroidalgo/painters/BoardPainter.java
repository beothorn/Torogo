package beothorn.github.com.toroidalgo.painters;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import beothorn.github.com.toroidalgo.GoGameController;
import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;

public class BoardPainter {

    private final Paint paint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint whitePaint = new Paint();
    private final Paint shadowPaint = new Paint();

    private Bitmap boardBitmap;
    private Canvas boardCanvas;

    public BoardPainter(){
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
    }

    public void updateBoard(GoGameController controller, int boardSlotsCount, int blockSize) {
        int rowSize = blockSize * boardSlotsCount;
        if(boardBitmap == null || boardBitmap.getWidth() != rowSize){
            boardBitmap = Bitmap.createBitmap(rowSize, rowSize, Bitmap.Config.ARGB_8888);
            boardCanvas = new Canvas(boardBitmap);
        }else{
            boardCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        drawGrid(boardCanvas, boardSlotsCount, blockSize, rowSize);
        drawPieces(boardCanvas, boardSlotsCount, controller, blockSize);
    }

    private void drawPieces(Canvas canvas, int boardSlotsCount, GoGameController controller, int blockSize) {
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

    private void drawGrid(Canvas canvas, int boardSlotsCount, int blockSize, int rowSize) {
        drawGrid(canvas, Color.DKGRAY, 4, boardSlotsCount, blockSize, rowSize);
        drawGrid(canvas, Color.BLACK, 2, boardSlotsCount, blockSize, rowSize);
    }

    private void drawGrid(Canvas canvas, int color, int size, int boardSlotsCount, int blockSize, int rowSize) {
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

    public void paint(Canvas canvas,int blockSize, int boardSlotsCount,int boardX, int boardY, int width, int height) {
        int boardSize = blockSize * boardSlotsCount;

        int startDrawingX = boardX -((boardX / boardSize)+1) * boardSize;
        int startDrawingY = boardY -((boardY / boardSize)+1) * boardSize;

        int currentBoardX = startDrawingX;
        int currentBoardY = startDrawingY;

        while(currentBoardX < width && currentBoardY < height ){
            while(currentBoardX < width){
                canvas.drawBitmap(boardBitmap, currentBoardX, currentBoardY, paint);
                currentBoardX += (blockSize*(boardSlotsCount));
            }

            currentBoardX = startDrawingX;
            currentBoardY += (blockSize*(boardSlotsCount));
        }
    }
}
