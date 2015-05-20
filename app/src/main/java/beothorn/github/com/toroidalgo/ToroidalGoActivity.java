package beothorn.github.com.toroidalgo;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import beothorn.github.com.toroidalgo.graphs.MyGLSurfaceView;
import sneer.android.Message;
import sneer.android.PartnerSession;

public class ToroidalGoActivity extends Activity { // implements Listener {

    private GoGameController controller;

    private GoView goView;
    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);

        /**
        setContentView(R.layout.activity_toroidal_go);

        goView = (GoView) findViewById(R.id.goView);
        Publisher publisher = new Publisher();

        final PartnerSession session = PartnerSession.join(this, new PartnerSession.Listener() {
            @Override
            public void onUpToDate() {      /////////////Sneer API
                goView.invalidate();
            }

            @Override
            public void onMessage(Message message) {
                if(message.wasSentByMe()) return;
                Object payload = message.payload();
                Map<String, Integer> torogoMove = convertFromSneerToTorogoMove((Map<String, Long>) payload);
                playLocally(torogoMove);
                goView.invalidate();
            }

            private Map<String, Integer> convertFromSneerToTorogoMove(Map<String, Long> payload) {
                Map<String, Integer> casted = new LinkedHashMap<String, Integer>();
                for (Map.Entry<String, Long> stringLongEntry : payload.entrySet()) {
                    casted.put(stringLongEntry.getKey(), stringLongEntry.getValue().intValue());
                }
                return casted;
            }
        });

        final GoBoard.StoneColor myColor;
        if(session.wasStartedByMe()){
            myColor = GoBoard.StoneColor.WHITE;
        }else{
            myColor = GoBoard.StoneColor.BLACK;
        }

        controller = new GoGameController(publisher, myColor);
        goView.setController(controller);

        TextView gameStateLabel = (TextView) findViewById(R.id.gameState);
        controller.setStateLabel(gameStateLabel);

        Button continueButton =  (Button) findViewById(R.id.continueButton);
        controller.setContinueButton(continueButton);

        Button passButton =  (Button) findViewById(R.id.passButton);
        controller.setPassButton(passButton);

        Button resignButton =  (Button) findViewById(R.id.resignButton);
        controller.setResignButton(resignButton);

        TextView blackScore =  (TextView) findViewById(R.id.blackScore);
        controller.setBlackScore(blackScore);

        TextView whiteScore =  (TextView) findViewById(R.id.whiteScore);
        controller.setWhiteScore(whiteScore);

        publisher.setPublishListener(new ToroidalGoListener(){
            @Override
            public void doPlay(Map<String, Integer> play, GoBoard.StoneColor playingColor) {
                Map<String, Long> casted = new LinkedHashMap<String, Long>();
                for (Map.Entry<String, Integer> stringLongEntry : play.entrySet()) {
                    casted.put(stringLongEntry.getKey(), stringLongEntry.getValue().longValue());
                }

                if(playingColor == myColor) {
                    playLocally(play);
                    goView.invalidate();
                }

                session.send(casted);
            }
        });
         **/
    }

    public void playLocally(Map<String, Integer> play){
        switch (play.get("TYPE")){
            case Publisher.TOGGLE_DEAD_STONE:
                controller.toggleDeadStone(play.get("line"), play.get("column"));
                break;
            case Publisher.PLAY:
                controller.playStone(play.get("line"), play.get("column"));
                break;
            case Publisher.PASS:
                controller.pass();
                break;
            case Publisher.RESIGN:
                controller.resign();
            case Publisher.CONTINUE:
                controller.continueGame(play.get("turn"));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toroidal_go, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
