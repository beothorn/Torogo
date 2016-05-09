package beothorn.github.com.toroidalgo.painters;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.widget.Button;

public class TextPainter {

    private String text = "";
    private Paint textPaint;
    private Paint textStrokePaint;
    private Paint backPaint;
    private Button passButton;

    public TextPainter(){
        textPaint = new Paint();
        textPaint.setARGB(255, 250, 250, 250);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.MONOSPACE);
        textPaint.setTextSize(100);

        backPaint = new Paint();
        backPaint.setARGB(255, 50, 50, 50);

        textStrokePaint = new Paint();
        textStrokePaint.setARGB(255, 100, 100, 100);
        textStrokePaint.setStyle(Paint.Style.STROKE);
        textStrokePaint.setTextAlign(Paint.Align.CENTER);
        textStrokePaint.setTypeface(Typeface.MONOSPACE);
        textStrokePaint.setTextSize(100);

    }

    public void clearText(){
        this.text = "";
    }

    public void setText(String text){
        this.text = text;
    }

    public void paintText(Canvas canvas){
        if(text.isEmpty()) return;

        String[] textLines = text.split("\n");

        if(textLines.length == 1) {
            paintOneLine(canvas);
        }
        if(textLines.length == 2) {
            paintTwoLines(canvas);
        }
    }

    private void paintTwoLines(Canvas canvas) {
        String[] textLines = text.split("\n");

        int width = canvas.getWidth();
        int xPos = (width / 2);
        float fontHeight = textPaint.descent() - textPaint.ascent();
        float halfFontHeight = fontHeight / 2;

        int height = canvas.getHeight();
        int margin = 50;
        float charHeight = fontHeight + margin;
        float top = height - charHeight * 2;
        canvas.drawRect(0, top - (margin*2), width, height, backPaint);
        float y = height - charHeight - halfFontHeight;
        canvas.drawText(textLines[0], xPos, y, textPaint);
        canvas.drawText(textLines[0], xPos, y, textStrokePaint);
        y = height - halfFontHeight;
        canvas.drawText(textLines[1], xPos, y, textPaint);
        canvas.drawText(textLines[1], xPos, y, textStrokePaint);
    }

    private void paintOneLine(Canvas canvas) {
        int width = canvas.getWidth();
        int xPos = (width / 2);
        float fontHeight = textPaint.descent() - textPaint.ascent();
        float halfFontHeight = fontHeight / 2;

        int height = canvas.getHeight();
        float top = height - fontHeight;
        canvas.drawRect(0, top - 50, width, height, backPaint);
        float y = height - halfFontHeight;
        canvas.drawText(text, xPos, y, textPaint);
        canvas.drawText(text, xPos, y, textStrokePaint);
    }

    public String getText() {
        return text;
    }
}
