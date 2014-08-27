package beothorn.github.com.toroidalgo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import sneer.android.ui.SessionActivity;

//public class ToroidalGoActivity extends SessionActivity {
public class ToroidalGoActivity extends Activity {

    private GoGameController controller;

    private GoView goView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toroidal_go);

        GoView goView = (GoView) findViewById(R.id.goView);
        Publisher publisher = new Publisher();
        controller = new GoGameController(publisher, GoBoard.StoneColor.BLACK);
        goView.setController(controller);

        publisher.setPublishListener(this);

        TextView gameStateLabel = (TextView) findViewById(R.id.gameState);
        controller.setStateLabel(gameStateLabel);

        Button passButton =  (Button) findViewById(R.id.passButton);
        controller.setPassButton(passButton);

        Button resignButton =  (Button) findViewById(R.id.resignButton);
        controller.setResignButton(resignButton);

        TextView blackScore =  (TextView) findViewById(R.id.blackScore);
        controller.setBlackScore(blackScore);

        TextView whiteScore =  (TextView) findViewById(R.id.whiteScore);
        controller.setWhiteScore(whiteScore);
    }

    public void doPlay(Map<String, Integer> play){
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
                break;
        }
    }

//    @Override
    protected void onPeerName(String oponnent) {

    }

//    @Override
    protected void messageSent(Object newMessage) {
        doPlay((Map<String, Integer>) newMessage);
        goView.invalidate();
    }

//    @Override
    protected void messageReceived(Object newMessage) {
        doPlay((Map<String, Integer>) newMessage);
        goView.invalidate();
    }

//    @Override
    protected void replayMessageSent(Object oldMessage) {
        doPlay((Map<String, Integer>) oldMessage);
    }

//    @Override
    protected void replayMessageReceived(Object oldMessage) {
        doPlay((Map<String, Integer>) oldMessage);
    }

//    @Override
    protected void onReplayCompleted() {

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
