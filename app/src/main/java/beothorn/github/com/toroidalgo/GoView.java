package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;

public class GoView extends View{

    private final Paint paint = new Paint();
    private int blockSize = 30;
    private int boardSize = 9;
    private int rowSize = blockSize * boardSize;
    private GoGameController controller;

    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);

        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int column = Math.round(motionEvent.getX()/ blockSize)-1;
                int line = Math.round(motionEvent.getY()/ blockSize)-1;
                if(column < 0 || column >= boardSize || line < 0 || line >= boardSize) return false;
                controller.play(GoBoard.StoneColor.BLACK, line, column);
                invalidate();
                return false;
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(controller == null) return;

        int min = Math.min(getMeasuredWidth(),getMeasuredHeight());

        blockSize = min / (boardSize + 2);
        rowSize = blockSize * boardSize;
        int boardX = blockSize;
        int boardY = blockSize;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(240, 182, 98));
        canvas.drawRect(boardX-blockSize, boardY-blockSize, boardX + (blockSize * boardSize)+blockSize, boardY + (blockSize * boardSize)+blockSize, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        for(int i = 0; i < boardSize; i++){
            int start = i * blockSize;

            int lineLeft = boardX;
            int lineTop = boardY + start;
            int lineRight = boardX + rowSize;
            int lineBottom = lineTop + blockSize;


            int columnLeft = boardX + start;
            int columnTop = boardY ;
            int columnRight = columnLeft + blockSize;
            int columnBottom = boardY + rowSize;

            canvas.drawRect(lineLeft, lineTop, lineRight, lineBottom, paint);
            canvas.drawRect(columnLeft, columnTop, columnRight, columnBottom, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        for(int line = 0; line < boardSize; line++){
            for(int column = 0; column < boardSize; column++){
                if(controller.getPieceAt(line, column) == null) continue;
                if(controller.getPieceAt(line, column).equals(GoBoard.StoneColor.BLACK)){
                    paint.setColor(Color.BLACK);
                }else{
                    paint.setColor(Color.WHITE);
                }
                canvas.drawCircle((column*blockSize)+boardX,(line*blockSize)+boardY, blockSize/2, paint);
            }
        }

    }

    public void setController(GoGameController controller) {
        this.controller = controller;
        boardSize = controller.getSize();
    }
}
