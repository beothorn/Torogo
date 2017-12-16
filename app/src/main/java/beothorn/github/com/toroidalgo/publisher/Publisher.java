package beothorn.github.com.toroidalgo.publisher;

import java.util.LinkedHashMap;
import java.util.Map;

import beothorn.github.com.toroidalgo.GoGameController;
import beothorn.github.com.toroidalgo.ToroidalGoActivity;
import beothorn.github.com.toroidalgo.ToroidalGoListener;
import beothorn.github.com.toroidalgo.go.impl.logic.StoneColor;
import sneer.android.ui.SneerInstallation;

public class Publisher {

    private ToroidalGoListener listener;

    public static final String MOVE_TYPE = "TYPE";
    public static final int TOGGLE_DEAD_STONE = 0;
    public static final int PLAY = 1;
    public static final int PASS = 2;
    public static final int RESIGN = 3;
    public static final int CONTINUE = 4;
    public static final int WHITE_TURN = 5;
    public static final int BLACK_TURN = 6;
    public static final int END_GAME = 7;

    public static GoGameController createController(ToroidalGoActivity activity){

        if(SneerInstallation.wasCalledFromConversation(activity)) {
            return new SneerSetup().setupController(activity);
        }
        else {
            return new LocalPlaySetup().setupController(activity);
        }

    }

    public void setPublishListener(ToroidalGoListener listener){
        this.listener = listener;
    }

    public void toggleDeadStone(int line, int column, StoneColor currentPlaying) {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(MOVE_TYPE, TOGGLE_DEAD_STONE);
        play.put("line", line);
        play.put("column", column);
        listener.doPlay(play, currentPlaying);
    }

    public void playStone(int line, int column, StoneColor currentPlaying) {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(MOVE_TYPE, PLAY);
        play.put("line", line);
        play.put("column", column);
        listener.doPlay(play, currentPlaying);
    }

    public void pass(StoneColor currentPlaying) {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(MOVE_TYPE, PASS);
        listener.doPlay(play, currentPlaying);
    }

    public void resign(StoneColor currentPlaying) {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(MOVE_TYPE, RESIGN);
        listener.doPlay(play, currentPlaying);
    }

    public void endMarkingStones() {
        Map<String, Integer> play = new LinkedHashMap<String, Integer>();
        play.put(MOVE_TYPE, END_GAME);
        listener.doPlay(play, StoneColor.ANY);
    }
}
