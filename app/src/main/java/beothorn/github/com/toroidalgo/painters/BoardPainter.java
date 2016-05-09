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

import beothorn.github.com.toroidalgo.go.impl.logic.GoMatch.StoneColor;
import static beothorn.github.com.toroidalgo.go.impl.logic.GoMatch.StoneColor.BLACK;
import static beothorn.github.com.toroidalgo.go.impl.logic.GoMatch.StoneColor.WHITE;

public class BoardPainter {

    private final Paint paint = new Paint();
    private final Paint blackPaint = new Paint();
    private final Paint whitePaint = new Paint();
    private final Paint shadowPaint = new Paint();

    private Bitmap boardBitmap;
    private Canvas boardCanvas;

    private Bitmap whitePieceBitmap;
    private Canvas whitePieceCanvas;

    private Bitmap blackPieceBitmap;
    private Canvas blackPieceCanvas;

    private Bitmap pieceShadowBitmap;
    private Canvas pieceShadowCanvas;

    private Bitmap whiteTerritoryBitmap;
    private Canvas whiteTerritoryCanvas;

    private Bitmap blackTerritoryBitmap;
    private Canvas blackTerritoryCanvas;

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
        if(boardBitmap == null || boardBitmap.getWidth() < rowSize){
            boardBitmap = Bitmap.createBitmap(rowSize, rowSize, Bitmap.Config.ARGB_8888);
            boardCanvas = new Canvas(boardBitmap);
        }else{
            boardCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }
        if(whitePieceBitmap == null || whitePieceBitmap.getWidth() < blockSize){
            whitePieceBitmap = Bitmap.createBitmap(blockSize, blockSize, Bitmap.Config.ARGB_8888);
            whitePieceCanvas = new Canvas(whitePieceBitmap);

            blackPieceBitmap = Bitmap.createBitmap(blockSize, blockSize, Bitmap.Config.ARGB_8888);
            blackPieceCanvas = new Canvas(blackPieceBitmap);

            whiteTerritoryBitmap = Bitmap.createBitmap(blockSize, blockSize, Bitmap.Config.ARGB_8888);
            whiteTerritoryCanvas = new Canvas(whiteTerritoryBitmap);

            blackTerritoryBitmap = Bitmap.createBitmap(blockSize, blockSize, Bitmap.Config.ARGB_8888);
            blackTerritoryCanvas = new Canvas(blackTerritoryBitmap);

            pieceShadowBitmap = Bitmap.createBitmap(blockSize+8, blockSize+8, Bitmap.Config.ARGB_8888);
            pieceShadowCanvas = new Canvas(pieceShadowBitmap);
        }else{
            whitePieceCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            blackPieceCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            whiteTerritoryCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            blackTerritoryCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            pieceShadowCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        }

        int radius = blockSize / 2;
        int quarter = blockSize / 4;

        drawWhitePiece(radius);
        drawBlackPiece(blockSize, radius);

        blackPaint.setShader(null);
        blackPaint.setStyle(Paint.Style.FILL);
        blackTerritoryCanvas.drawRect(radius - quarter, radius - quarter, radius + quarter, radius + quarter, blackPaint);
        whitePaint.setStyle(Paint.Style.STROKE);
        blackTerritoryCanvas.drawRect(radius - quarter, radius - quarter, radius + quarter, radius + quarter, whitePaint);

        whitePaint.setStyle(Paint.Style.FILL);
        whiteTerritoryCanvas.drawRect(radius - quarter, radius - quarter, radius + quarter, radius + quarter, whitePaint);
        blackPaint.setStyle(Paint.Style.STROKE);
        whiteTerritoryCanvas.drawRect(radius - quarter, radius - quarter, radius + quarter, radius + quarter, blackPaint);


        pieceShadowCanvas.drawCircle(radius, radius, radius, shadowPaint);

        drawGrid(boardCanvas, boardSlotsCount, blockSize, rowSize);
        drawPieces(boardCanvas, boardSlotsCount, controller, blockSize);
        if(controller.gameHasEnded())
            drawTerritories(boardCanvas, controller, blockSize, boardSlotsCount);
    }

    private void drawBlackPiece(int blockSize, int radius) {
        blackPaint.setShader(new RadialGradient(radius, radius, blockSize * 2, Color.BLACK, Color.DKGRAY, Shader.TileMode.MIRROR));
        blackPaint.setStyle(Paint.Style.FILL);
        blackPieceCanvas.drawCircle(radius, radius, radius, blackPaint);
        whitePaint.setStyle(Paint.Style.STROKE);
        blackPieceCanvas.drawCircle(radius, radius, radius, whitePaint);
    }

    private void drawWhitePiece(int radius) {
        whitePaint.setStyle(Paint.Style.FILL);
        whitePieceCanvas.drawCircle(radius, radius, radius, whitePaint);
        blackPaint.setStyle(Paint.Style.STROKE);
        whitePieceCanvas.drawCircle(radius, radius, radius, blackPaint);
    }

    private void drawPieces(Canvas canvas, int boardSlotsCount, GoGameController controller, int blockSize) {
        paintShadows(canvas, boardSlotsCount, controller, blockSize);

        for(int line = 0; line <= boardSlotsCount; line++){
            for(int column = 0; column <= boardSlotsCount; column++){
                StoneColor pieceAt = controller.getPieceAt(line%boardSlotsCount, column%boardSlotsCount);
                if(pieceAt == null) continue;

                int cx = column * blockSize;
                int cy = line * blockSize;
                int radius = blockSize / 2;
                paintSolidPieces(canvas, pieceAt, cx, cy, radius);
                if(!controller.gameHasEnded())
                    paintLastPlayedMark(canvas, boardSlotsCount, controller, blockSize, line, column, pieceAt, cx, cy);
                paintDeadPieces(canvas, pieceAt, cx, cy, radius);
            }
        }
    }

    private void paintLastPlayedMark(Canvas canvas, int boardSlotsCount, GoGameController controller, int blockSize, int line, int column, StoneColor pieceAt, int cx, int cy) {
        if(controller.stoneAtPositionIsLastPlayedStone(line % boardSlotsCount, column % boardSlotsCount)){
            if(pieceAt.equals(BLACK)){
                whitePaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, blockSize / 4, whitePaint);
            }

            if(pieceAt.equals(WHITE)){
                blackPaint.setShader(null);
                blackPaint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy, blockSize / 4, blackPaint);
            }
        }
    }

    private void paintShadows(Canvas canvas, int boardSlotsCount, GoGameController controller, int blockSize) {
        for(int line = 0; line <= boardSlotsCount; line++){
            for(int column = 0; column <= boardSlotsCount; column++){
                StoneColor pieceAt = controller.getPieceAt(line % boardSlotsCount, column % boardSlotsCount);
                if(pieceAt == null || pieceAt.equals(StoneColor.WHITEDEAD) || pieceAt.equals(StoneColor.BLACKDEAD)) continue;

                int radius = blockSize / 2;
                int shadowDistance = 4;
                int cx = column * blockSize + shadowDistance;
                int cy = line * blockSize + shadowDistance;
                canvas.drawBitmap(pieceShadowBitmap, cx - radius, cy - radius, paint);
            }
        }
    }

    private void paintSolidPieces(Canvas canvas, StoneColor pieceAt, int cx, int cy, int radius) {
        if(pieceAt.equals(BLACK)){
            canvas.drawBitmap(blackPieceBitmap, cx - radius, cy - radius, paint);
        }
        if(pieceAt.equals(WHITE)){
            canvas.drawBitmap(whitePieceBitmap, cx - radius, cy - radius,paint);
        }
    }

    private void paintTerritory(Canvas canvas, StoneColor pieceAt, int cx, int cy, int radius) {
        if(pieceAt.equals(BLACK)){
            canvas.drawBitmap(blackTerritoryBitmap, cx - radius, cy - radius, paint);
        }
        if(pieceAt.equals(WHITE)){
            canvas.drawBitmap(whiteTerritoryBitmap, cx - radius, cy - radius, paint);
        }
    }

    private void drawTerritories(Canvas canvas, GoGameController controller,int blockSize, int boardSlotsCount) {

        Map<StoneColor, List<List<BoardPosition>>> territoriesOwnership = controller.getTerritoriesOwnership();

        int radius = blockSize / 2;

        List<List<BoardPosition>> blackTerritories = territoriesOwnership.get(BLACK);
        for(List<BoardPosition> blackTerritory : blackTerritories){
            for(BoardPosition territoryPosition : blackTerritory){
                int column = territoryPosition.getColumn();
                int line = territoryPosition.getLine();

                int cx = column * blockSize;
                int cy = line * blockSize;

                paintTerritory(canvas, BLACK, cx, cy, radius);
                if(column == 0){
                    paintTerritory(canvas, BLACK, boardSlotsCount * blockSize, cy, radius);
                }
                if(line == 0){
                    paintTerritory(canvas, BLACK, cx, boardSlotsCount * blockSize, radius);
                }
                if(line == 0 && column == 0){
                    paintTerritory(canvas, BLACK, boardSlotsCount * blockSize, boardSlotsCount * blockSize, radius);
                }
            }
        }

        List<List<BoardPosition>> whiteTerritories = territoriesOwnership.get(WHITE);
        for(List<BoardPosition> whiteTerritory : whiteTerritories){
            for(BoardPosition territoryPosition : whiteTerritory){
                int column = territoryPosition.getColumn();
                int line = territoryPosition.getLine();

                int cx = column * blockSize;
                int cy = line * blockSize;

                paintTerritory(canvas,  WHITE, cx, cy, radius);
                if(column == 0){
                    paintTerritory(canvas,  WHITE, boardSlotsCount * blockSize, cy, radius);
                }
                if(line == 0){
                    paintTerritory(canvas,  WHITE, cx, boardSlotsCount * blockSize, radius);
                }
                if(line == 0 && column == 0){
                    paintTerritory(canvas,  WHITE, boardSlotsCount * blockSize, boardSlotsCount * blockSize, radius);
                }
            }
        }
    }


    private void paintDeadPieces(Canvas canvas, StoneColor pieceAt, int cx, int cy, int radiusBig) {
        int radius = radiusBig;
        if(pieceAt.equals(StoneColor.BLACKDEAD)){
            canvas.drawBitmap(blackPieceBitmap, cx - radius, cy - radius, paint);
        }
        if(pieceAt.equals(StoneColor.WHITEDEAD)){
            canvas.drawBitmap(whitePieceBitmap, cx - radius, cy - radius, paint);
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
