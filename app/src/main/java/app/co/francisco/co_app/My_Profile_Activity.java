package app.co.francisco.co_app;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class My_Profile_Activity extends AppCompatActivity {

    TextView about_my_profil;

    private String titre;
    private String image;
    private String desciption;
    private String conclusion;

    private  String my_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__profile);

        this.init();
    }

    private class ImageGetter implements android.text.Html.ImageGetter {
        private ImageGetter() {
        }

        public Drawable getDrawable(String source) {
            int id;
            if (source.equals("home.jpg")) {
                id = R.drawable.ic_bluetooth_connected;
            } else if (source.equals("buttons1.jpg")) {
                id = R.drawable.ic_camera_take_photo;
            } else if (!source.equals("buttons2.jpg")) {
                return null;
            } else {
                id = R.drawable.ic_msb_icone;
            }
            Drawable d = My_Profile_Activity.this.getResources().getDrawable(id);
            d.setBounds(0, 0, 600, 337);
            return d;
        }
    }

    private  void init(){
        about_my_profil = (TextView)findViewById(R.id.about_my_ptofile);

        this.titre = "<body><h2>HAJANIAINA Mahatia Francisco<br> 22 ans </h2><hr/> </body>";
        this.image = "<img src=\"home.jpg\"><br><br>";
        this.desciption = "<p>J'habite  Mananara. J'ai deux souers et deux frères. </p>";
        this.conclusion ="Merci de votre visite";

        this.my_text = titre + "  " + image +"  "+desciption+"  "+conclusion;

        about_my_profil.setText(Html.fromHtml(this.my_text,new ImageGetter(),null));
    }
}
