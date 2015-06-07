package beothorn.github.com.toroidalgo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.LinkedHashMap;
import java.util.Map;

public class ToroidalGoActivity extends Activity {

    private GoGameController controller;
    public GoView goView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_toroidal_go);

        goView = (GoView) findViewById(R.id.goView);

        if(true)
                controller = new LocalPlaySetup().setupController(this);
            else
                controller = new SneerSetup().setupController(this);

        goView.setController(controller);

        controller.setStateListener(new StateListener() {
            @Override
            public void onPassWhite() {
                goView.setText("White Passed");
            }

            @Override
            public void onPassBlack() {
                goView.setText("Black Passed");
            }

            @Override
            public void onWhiteTurn() {
                goView.clearText();
            }

            @Override
            public void onBlackTurn() {
                goView.clearText();
            }

            @Override
            public void onResignWhite() {

            }

            @Override
            public void onResignBlack() {

            }

            @Override
            public void onMarkStonesPhaseStart() {
                goView.setText("mark dead");
            }
        });

        controller.setScoreListener(new ScoreListener() {
            @Override
            public void setScore(int blackScore, int whiteScore) {
                System.out.println(blackScore + " " + whiteScore);
            }
        });

        LinkedHashMap<String, Integer> stringIntegerLinkedHashMap = new LinkedHashMap<String, Integer>();

//        stringIntegerLinkedHashMap.put("TYPE", Publisher.PLAY);
//        stringIntegerLinkedHashMap.put("line", 1);
//        stringIntegerLinkedHashMap.put("column", 1);
//        playLocally(stringIntegerLinkedHashMap);

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
                break;
            case Publisher.CONTINUE:
                controller.continueGame(play.get("turn"));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toroidal_go, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.passButton) {
            controller.callPass();
            return true;
        }
        if (id == R.id.resignButton) {
            controller.callResign();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("gameState", controller.asString());
        outState.putString("viewState", goView.asString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        controller.recoverFromString(savedInstanceState.getString("gameState"));
        goView.recoverFromString(savedInstanceState.getString("viewState"));
        goView.redraw();
        super.onRestoreInstanceState(savedInstanceState);
    }
}
