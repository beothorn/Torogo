package beothorn.github.com.toroidalgo.publisher;

import java.util.Map;

import beothorn.github.com.toroidalgo.ControllerSetup;
import beothorn.github.com.toroidalgo.GoGameController;
import beothorn.github.com.toroidalgo.ToroidalGoActivity;
import beothorn.github.com.toroidalgo.ToroidalGoListener;
import beothorn.github.com.toroidalgo.go.impl.logic.StoneColor;

public class LocalPlaySetup implements ControllerSetup {
    @Override
    public GoGameController setupController(final ToroidalGoActivity toroidalGoActivity) {

        Publisher publisher = new Publisher();
        publisher.setPublishListener(new ToroidalGoListener() {
            @Override
            public void doPlay(Map<String, Integer> play, StoneColor playingColor) {
                toroidalGoActivity.playLocally(play);
                toroidalGoActivity.goView.invalidate();
            }
        });

        return new GoGameController(publisher);
    }
}
