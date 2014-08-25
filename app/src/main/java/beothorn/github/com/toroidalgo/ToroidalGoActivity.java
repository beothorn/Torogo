package beothorn.github.com.toroidalgo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ToroidalGoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toroidal_go);

        final GoView goView = (GoView) findViewById(R.id.goView);
        GoGameController controller = new GoGameController();
        goView.setController(controller);

        TextView gameStateLabel = (TextView) findViewById(R.id.gameState);
        controller.setStateLabel(gameStateLabel);

        Button passButton =  (Button) findViewById(R.id.passButton);
        controller.setPassButton(passButton);

        Button resignButton =  (Button) findViewById(R.id.resignButton);
        controller.setResignButton(resignButton);

        TextView blackScore =  (TextView) findViewById(R.id.blackScore);
        controller.setBlackScore(blackScore);

        TextView whiteScore =  (TextView) findViewById(R.id.whiteScore);
        controller.setWhiteScore(whiteScore);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toroidal_go, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
