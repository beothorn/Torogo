package torogo.model;

public interface Match {

    interface Listener {
        void onChange(Situation situation);
    }

    class Situation {
    }

    boolean isToroidal();
    boolean isParallel(); //For the future. ;)
}
