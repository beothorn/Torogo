package beothorn.github.com.toroidalgo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logging.GoLogger;
import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;
import beothorn.github.com.toroidalgo.publisher.Publisher;

public class ToroidalGoActivity extends Activity {

    private GoGameController controller;
    public GoView goView;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_toroidal_go);

        goView = (GoView) findViewById(R.id.goView);

        controller = Publisher.createController(this);

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
                goView.redraw();
            }

            @Override
            public void onBlackTurn() {
                goView.clearText();
                goView.redraw();
            }

            @Override
            public void onResignWhite() {
                String text = "B + R";
                goView.setText(text);
                gameEndedMenu();
            }

            @Override
            public void onResignBlack() {
                goView.setText("W + R");
                gameEndedMenu();
            }

            private void gameEndedMenu() {
                if (menu != null) {
                    menu.clear();
                    getMenuInflater().inflate(R.menu.game_ended, menu);
                }
            }

            @Override
            public void onMarkStone() {
                goView.redraw();
            }

            @Override
            public void onMarkStonesPhaseStart() {
                goView.setText("Mark Dead\nStones");
                menu.clear();
                getMenuInflater().inflate(R.menu.dead_stones, menu);
            }

            @Override
            public void onMarkStonesPhaseEnded() {
                GoBoard.StoneColor s = controller.getWinner();
                String text = "White Wins";
                if (s.equals(GoBoard.StoneColor.BLACK))
                    text = "Black Wins";
                text += "\nW:" + controller.getWhiteScore() + " x B:" + controller.getBlackScore();
                goView.setText(text);

                gameEndedMenu();
            }
        });

        controller.setScoreListener(new ScoreListener() {
            @Override
            public void setScore(int blackScore, float whiteScore) {
                System.out.println(blackScore + " " + whiteScore);
            }
        });

    }

    public void playLocally(Map<String, Integer> play) {
        GoLogger.log("Activity Playing: " + play.get(Publisher.TYPE));
        controller.playLocally(play);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
        if (id == R.id.acceptButton) {
            controller.callEndMarkingStones();
            return true;
        }
        if (id == R.id.restart) {
            return true;
        }
        if (id == R.id.close) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        controller.save(outState);
        goView.save(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        controller.recoverFrom(savedInstanceState);
        goView.recoverFrom(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
