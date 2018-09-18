package beothorn.github.com.toroidalgo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logging.GoLogger;
import beothorn.github.com.toroidalgo.go.impl.logic.StoneColor;
import beothorn.github.com.toroidalgo.publisher.DialogHandler;
import beothorn.github.com.toroidalgo.publisher.Publisher;
import torogo.model.Match;
import torogo.model.sim.MatchSimulator;

import static torogo.model.Match.Action.ACCEPT_DEAD_STONES;
import static torogo.model.Match.Action.PASS;
import static torogo.model.Match.Action.RESIGN;

public class ToroidalGoActivity extends Activity {

    private Match match;
    public GoView goView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_toroidal_go);

        goView = (GoView) findViewById(R.id.goView);

        final Button passButton = (Button) findViewById(R.id.passButton);
        passButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new DialogHandler().confirm(ToroidalGoActivity.this, "Confirmation", "Confirm pass?",
                        "Yes", "No", new Runnable() {
                    public void run() {
                        match.handle(PASS);
                        Log.d("pass action", "user passed");
                    }
                },  new Runnable() {
                    public void run() {
                        Log.d("pass action", "user gave up pass");
                    }
                });
            }
        });

        final Button acceptButton = (Button) findViewById(R.id.acceptButton);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                match.handle(ACCEPT_DEAD_STONES);
            }
        });

        final Button resignButton = (Button) findViewById(R.id.resignButton);
        resignButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                match.handle(RESIGN);
            }
        });

        match = new MatchSimulator(getApplicationContext());
        match.initListener(new Runnable() {@Override public void run() {

            // repintar();
        }});

        goView.setMatch(match);
        findViewById(R.id.acceptButton).setVisibility(View.GONE);

//        match.setStateListener(new StateListener() {
//            @Override
//            public void onPassWhite() {
//                goView.setText("White Passed");
//            }
//
//            @Override
//            public void onPassBlack() {
//                goView.setText("Black Passed");
//            }
//
//            @Override public void onWhiteTurn() {
//                if (match.myColor == StoneColor.WHITE && !match.isMyTurn())
//                    throw new IllegalStateException("Should be my turn");
//
//                onSomebodysTurn();
//            }
//            @Override public void onBlackTurn() { onSomebodysTurn(); }
//
//            private void onSomebodysTurn() {
//                goView.clearText();
//                findViewById(R.id.passButton).setEnabled(match.isMyTurn());
//                goView.redrawBoard();
//            }
//
//            @Override
//            public void onResignWhite() {
//                goView.setText("White resigns");
//                gameEnded();
//            }
//
//            @Override
//            public void onResignBlack() {
//                goView.setText("Black resigns");
//                gameEnded();
//            }
//
//            private void gameEnded() {
//                findViewById(R.id.footer).setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onMarkStone() {
//                goView.redrawBoard();
//            }
//
//            @Override
//            public void onMarkStonesPhaseStart() {
//                goView.setText("Mark Dead\nStones");
//                findViewById(R.id.acceptButton).setVisibility(View.VISIBLE);
//                findViewById(R.id.passButton).setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onMarkStonesPhaseEnded() {
//                String text = match.getWinner().name() + " WINS";
//                text += "\nW:" + match.getWhiteScore() + " x B:" + match.getBlackScore();
//                goView.setText(text);
//                gameEnded();
//            }
//        });

//        match.setScoreListener(new ScoreListener() {
//            @Override
//            public void setScore(int blackScore, float whiteScore) {
//                System.out.println(blackScore + " " + whiteScore);
//            }
//        });

    }

    public void playLocally(Map<String, Integer> play) {
        GoLogger.log("Activity Playing: " + play.get(Publisher.MOVE_TYPE));
//        match.playLocally(play);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.passButton) {
//            match.callPass();
            return true;
        }
        if (id == R.id.resignButton) {
//            match.callResign();
            return true;
        }
        if (id == R.id.acceptButton) {
//            match.callEndMarkingStones();
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
//        match.save(outState);
        goView.save(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        match.recoverFrom(savedInstanceState);
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
