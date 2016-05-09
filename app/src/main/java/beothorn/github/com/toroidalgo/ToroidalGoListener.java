package beothorn.github.com.toroidalgo;

import java.util.Map;

import beothorn.github.com.toroidalgo.go.impl.logic.StoneColor;

public interface ToroidalGoListener {
    void doPlay(Map<String, Integer> play, StoneColor playingColor);
}
