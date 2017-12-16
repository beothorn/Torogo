package beothorn.github.com.toroidalgo.publisher;

import java.util.LinkedHashMap;
import java.util.Map;

import beothorn.github.com.toroidalgo.ControllerSetup;
import beothorn.github.com.toroidalgo.GoGameController;
import beothorn.github.com.toroidalgo.ToroidalGoActivity;
import beothorn.github.com.toroidalgo.ToroidalGoListener;
import beothorn.github.com.toroidalgo.go.impl.logging.GoLogger;
import beothorn.github.com.toroidalgo.go.impl.logic.StoneColor;
import sneer.android.Message;
import sneer.android.PartnerSession;
import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.WHITE;
import static beothorn.github.com.toroidalgo.go.impl.logic.StoneColor.BLACK;

public class SneerSetup implements ControllerSetup {

    public GoGameController setupController(final ToroidalGoActivity toroidalGoActivity) {
        StoneColor myColor = BLACK;

        Publisher publisher = new Publisher();

        final PartnerSession session = PartnerSession.join(toroidalGoActivity, new PartnerSession.Listener() {
            @Override
            public void onUpToDate() {
                toroidalGoActivity.goView.invalidate();
            }

            @Override
            public void onMessage(Message message) {
                GoLogger.log("Received Sneer Message");
                Object payload = message.payload();
                Map<String, Integer> torogoMove = convertValuesToInteger((Map<String, Long>) payload);
                GoLogger.log("Playing from Sneer Message");
                toroidalGoActivity.playLocally(torogoMove);
                toroidalGoActivity.goView.invalidate(); // is this line necessary?
            }

            private Map<String, Integer> convertValuesToInteger(Map<String, Long> payload) {
                    Map<String, Integer> casted = new LinkedHashMap<String, Integer>();
                    for (Map.Entry<String, Long> entry : payload.entrySet()) {
                        casted.put(entry.getKey(), entry.getValue().intValue());
                    }
                    return casted;
            }
        });

        if(session.wasStartedByMe()){
            myColor = WHITE;
        }

        final StoneColor finalMyColor = myColor;
        publisher.setPublishListener(new ToroidalGoListener() {
            @Override
            public void doPlay(Map<String, Integer> play, StoneColor playingColor) {
                Map<String, Long> casted = new LinkedHashMap<String, Long>();
                for (Map.Entry<String, Integer> stringLongEntry : play.entrySet())
                    casted.put(stringLongEntry.getKey(), stringLongEntry.getValue().longValue());
                session.send(casted);
            }
        });

        return new GoGameController(publisher, myColor);
    }
}
