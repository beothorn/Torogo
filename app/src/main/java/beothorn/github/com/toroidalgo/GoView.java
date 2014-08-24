package beothorn.github.com.toroidalgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GoView extends View{

    private final Paint paint = new Paint();
    private int blockSize = 30;
    private int boardSize = 9;
    private int boardX = 0;
    private int boardY = 0;

    public GoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int line = 0; line < boardSize; line++){
            for(int column = 0; column < boardSize; column++){
                int left = boardX + column * blockSize;
                int top = boardY + line * blockSize;
                int right = boardX + top + blockSize;
                int bottom = boardY + left + blockSize;
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }
}
