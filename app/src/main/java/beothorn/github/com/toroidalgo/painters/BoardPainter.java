package beothorn.github.com.toroidalgo.painters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;

import beothorn.github.com.toroidalgo.GoView;
import beothorn.github.com.toroidalgo.R;

public class BoardPainter {

    private Bitmap img;
    private BitmapDrawable tileImg;
    private GoView goView;

    public BoardPainter(GoView goView){
        this.goView = goView;
        img = BitmapFactory.decodeResource(goView.getResources(), R.drawable.wood);//http://tiled-bg.blogspot.com.br/
        tileImg = new BitmapDrawable(goView.getResources(), img);
    }

    public void onDraw(Canvas canvas, int boardX, int boardY, int blockSize) {
        drawBackground(canvas, boardX, boardY, blockSize);
    }

    private void drawBackground(Canvas canvas, int boardX, int boardY, int blockSize) {
        int saveCount = canvas.save();
        try {
            int width = img.getWidth();
            int height = img.getHeight();
            if(boardX != 0 && boardY != 0 && blockSize != 0) {
                int dx = (boardX % width) - width;
                int dy = (boardY % height) - height;
                canvas.translate(dx, dy);
            }

            tileImg.setBounds(0, 0, goView.getMeasuredWidth()+width, goView.getMeasuredHeight()+height);
            tileImg.setTileModeXY(Shader.TileMode.REPEAT.REPEAT, Shader.TileMode.REPEAT.REPEAT);
            tileImg.draw(canvas);
        } finally {
            canvas.restoreToCount(saveCount);
        }
    }
}
