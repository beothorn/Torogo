package beothorn.github.com.toroidalgo;

import java.util.LinkedHashMap;
import java.util.Map;

import sneer.android.PartnerSession;

public class Publisher {

    private ToroidalGoListener listener;

    public static final String TYPE = "TYPE";
    public static final int TOGGLE_DEAD_STONE = 0;
    public static final int PLAY = 1;
    public static final int PASS = 2;
    public static final int RESIGN = 3;
    public static final int CONTINUE = 4;
    public static final int WHITE_TURN = 5;
    public static final int BLACK_TURN = 6;

    public void setPublishListener(ToroidalGoListener listener){
        this.listener = listener;
    }

    public void toggleDeadStone(int line, int column) {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(TYPE, TOGGLE_DEAD_STONE);
        play.put("line", line);
        play.put("column", column);
        listener.doPlay(play);
    }

    public void playStone(int line, int column) {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(TYPE, PLAY);
        play.put("line", line);
        play.put("column", column);
        listener.doPlay(play);
    }

    public void pass() {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(TYPE, PASS);
        listener.doPlay(play);
    }

    public void continueGame(int turn) {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(TYPE, CONTINUE);
        play.put("turn", turn);
        listener.doPlay(play);
    }

    public void resign() {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(TYPE, RESIGN);
        listener.doPlay(play);
    }
}
