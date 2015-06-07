package beothorn.github.com.toroidalgo.painters;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class TextPainter {

    private String text = "White passed";
    private Paint textPaint;
    private Paint textStrokePaint;
    private Paint backPaint;
    private int yPos = 1000;

    public TextPainter(){
        textPaint = new Paint();
        textPaint.setARGB(200, 250, 250, 250);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setTextSize(130);

        backPaint = new Paint();
        backPaint.setARGB(200, 50, 50, 50);

        textStrokePaint = new Paint();
        textStrokePaint.setARGB(200, 0, 0, 0);
        textStrokePaint.setStyle(Paint.Style.STROKE);
        textStrokePaint.setTextAlign(Paint.Align.CENTER);
        textStrokePaint.setTypeface(Typeface.MONOSPACE);
        textStrokePaint.setTextSize(130);
    }

    public void paintText(Canvas canvas){
        int xPos = (canvas.getWidth() / 2);
        float fontHeight = (textPaint.descent() + textPaint.ascent()) / 2;

        canvas.drawRect(0, yPos + textPaint.ascent(), canvas.getWidth(),canvas.getHeight(), backPaint);
        canvas.drawText(text, xPos, yPos, textPaint);
        canvas.drawText(text, xPos, yPos, textStrokePaint);
    }
}
