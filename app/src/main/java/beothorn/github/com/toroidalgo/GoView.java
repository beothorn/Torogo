package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.View;

public class GoView extends View{

    private final Paint paint = new Paint();
    private int blockSize = 30;
    private int boardSize = 9;
    private int boardX = 150;
    private int boardY = 300;
    private int rowSize = blockSize * boardSize;

    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

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

    }
}
