package app.co.francisco.co_app.utils.Controller;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.MediaController;

/**
 * Created by ASUS on 22/03/2019.
 */

public class MusicController  extends MediaController {
    public MusicController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MusicController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MusicController(Context context) {
        super(context);
    }

    public void hide(){}
}
