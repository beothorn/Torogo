package beothorn.github.com.toroidalgo.painters;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.List;
import java.util.Map;

import beothorn.github.com.toroidalgo.GoGameController;
import beothorn.github.com.toroidalgo.go.impl.logic.BoardPosition;
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
        drawTerritories(boardCanvas, controller, blockSize, boardSlotsCount);
    }

    private void drawPieces(Canvas canvas, int boardSlotsCount, GoGameController controller, int blockSize) {
        paintShadows(canvas, boardSlotsCount, controller, blockSize);

        for(int line = 0; line <= boardSlotsCount; line++){
            for(int column = 0; column <= boardSlotsCount; column++){
                GoBoard.StoneColor pieceAt = controller.getPieceAt(line%boardSlotsCount, column%boardSlotsCount);
                if(pieceAt == null) continue;

                int cx = column * blockSize;
                int cy = line * blockSize;
                int radius = blockSize / 2;
                paintSolidPieces(canvas, blockSize, pieceAt, cx, cy, radius);
                paintLastPlayedMark(canvas, boardSlotsCount, controller, blockSize, line, column, pieceAt, cx, cy);
                paintDeadPieces(canvas, pieceAt, cx, cy, radius);
            }
        }
    }

    private void paintLastPlayedMark(Canvas canvas, int boardSlotsCount, GoGameController controller, int blockSize, int line, int column, GoBoard.StoneColor pieceAt, int cx, int cy) {
        if(controller.stoneAtPositionIsLastPlayedStone(line % boardSlotsCount, column % boardSlotsCount)){
            if(pieceAt.equals(GoBoard.StoneColor.BLACK)){
                whitePaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, blockSize / 4, whitePaint);
            }

            if(pieceAt.equals(GoBoard.StoneColor.WHITE)){
                blackPaint.setShader(null);
                blackPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, blockSize / 4, blackPaint);
            }
        }
    }

    private void paintShadows(Canvas canvas, int boardSlotsCount, GoGameController controller, int blockSize) {
        for(int line = 0; line <= boardSlotsCount; line++){
            for(int column = 0; column <= boardSlotsCount; column++){
                GoBoard.StoneColor pieceAt = controller.getPieceAt(line % boardSlotsCount, column % boardSlotsCount);
                if(pieceAt == null || pieceAt.equals(GoBoard.StoneColor.WHITEDEAD) || pieceAt.equals(GoBoard.StoneColor.BLACKDEAD)) continue;
                int shadowDistance = 4;
                canvas.drawCircle(column * blockSize + shadowDistance, line * blockSize + shadowDistance, blockSize / 2, shadowPaint);
            }
        }
    }

    private void paintSolidPieces(Canvas canvas, int blockSize, GoBoard.StoneColor pieceAt, int cx, int cy, int radius) {
        if(pieceAt.equals(GoBoard.StoneColor.BLACK)){
            blackPaint.setShader(new RadialGradient(cx, cy, blockSize * 2, Color.BLACK, Color.DKGRAY, Shader.TileMode.MIRROR));
            blackPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius, blackPaint);
            whitePaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(cx, cy, radius, whitePaint);
        }
        if(pieceAt.equals(GoBoard.StoneColor.WHITE)){
            whitePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius, whitePaint);
            blackPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(cx, cy, radius, blackPaint);
        }
    }

    private void paintTerritory(Canvas canvas, int blockSize, GoBoard.StoneColor pieceAt, int cx, int cy, int radius) {
        if(pieceAt.equals(GoBoard.StoneColor.BLACK)){
            blackPaint.setShader(new RadialGradient(cx, cy, blockSize * 2, Color.BLACK, Color.DKGRAY, Shader.TileMode.MIRROR));
            blackPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(cx - radius, cy - radius,cx + radius,cy + radius, blackPaint);
            whitePaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(cx - radius, cy - radius, cx + radius, cy + radius, whitePaint);
        }
        if(pieceAt.equals(GoBoard.StoneColor.WHITE)){
            whitePaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(cx - radius, cy - radius, cx + radius, cy + radius, whitePaint);
            blackPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(cx - radius, cy - radius, cx + radius, cy + radius, blackPaint);
        }
    }

    private void drawTerritories(Canvas canvas, GoGameController controller,int blockSize, int boardSlotsCount) {

        Map<GoBoard.StoneColor, List<List<BoardPosition>>> territoriesOwnership = controller.getTerritoriesOwnership();

        List<List<BoardPosition>> blackTerritories = territoriesOwnership.get(GoBoard.StoneColor.BLACK);
        for(List<BoardPosition> blackTerritory : blackTerritories){
            for(BoardPosition territoryPosition : blackTerritory){
                int column = territoryPosition.getColumn();
                int line = territoryPosition.getLine();

                int cx = column * blockSize;
                int cy = line * blockSize;
                int radius = blockSize / 4;

                paintTerritory(canvas, blockSize, GoBoard.StoneColor.BLACK, cx, cy, radius);
                if(column == 0){
                    paintTerritory(canvas, blockSize, GoBoard.StoneColor.BLACK, boardSlotsCount * blockSize, cy, radius);
                }
                if(line == 0){
                    paintTerritory(canvas, blockSize, GoBoard.StoneColor.BLACK, cx, boardSlotsCount * blockSize, radius);
                }
                if(line == 0 && column == 0){
                    paintTerritory(canvas, blockSize, GoBoard.StoneColor.BLACK, boardSlotsCount * blockSize, boardSlotsCount * blockSize, radius);
                }
            }
        }

        List<List<BoardPosition>> whiteTerritories = territoriesOwnership.get(GoBoard.StoneColor.WHITE);
        for(List<BoardPosition> whiteTerritory : whiteTerritories){
            for(BoardPosition territoryPosition : whiteTerritory){
                int column = territoryPosition.getColumn();
                int line = territoryPosition.getLine();

                int cx = column * blockSize;
                int cy = line * blockSize;
                int radius = blockSize / 4;

                paintTerritory(canvas, blockSize, GoBoard.StoneColor.WHITE, cx, cy, radius);
                if(column == 0){
                    paintTerritory(canvas, blockSize, GoBoard.StoneColor.WHITE, boardSlotsCount * blockSize, cy, radius);
                }
                if(line == 0){
                    paintTerritory(canvas, blockSize, GoBoard.StoneColor.WHITE, cx, boardSlotsCount * blockSize, radius);
                }
                if(line == 0 && column == 0){
                    paintTerritory(canvas, blockSize, GoBoard.StoneColor.WHITE, boardSlotsCount * blockSize, boardSlotsCount * blockSize, radius);
                }
            }
        }
    }

    private void paintDeadPieces(Canvas canvas, GoBoard.StoneColor pieceAt, int cx, int cy, int radiusBig) {
        int radius = radiusBig;
        if(pieceAt.equals(GoBoard.StoneColor.BLACKDEAD)){
            blackPaint.setShader(null);
            blackPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius, blackPaint);
            whitePaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(cx, cy, radius, whitePaint);
        }
        if(pieceAt.equals(GoBoard.StoneColor.WHITEDEAD)){
            whitePaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, radius, whitePaint);
            blackPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(cx, cy, radius, blackPaint);
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
