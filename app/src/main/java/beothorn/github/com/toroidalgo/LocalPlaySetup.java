package beothorn.github.com.toroidalgo;

import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logic.GoBoard;

public class LocalPlaySetup implements ControllerSetup{
    @Override
    public GoGameController setupController(final ToroidalGoActivity toroidalGoActivity) {

        Publisher publisher = new Publisher();
        publisher.setPublishListener(new ToroidalGoListener() {
            @Override
            public void doPlay(Map<String, Integer> play, GoBoard.StoneColor playingColor) {
                toroidalGoActivity.playLocally(play);
                toroidalGoActivity.goView.invalidate();
            }
        });

        return new GoGameController(publisher);
    }
}
