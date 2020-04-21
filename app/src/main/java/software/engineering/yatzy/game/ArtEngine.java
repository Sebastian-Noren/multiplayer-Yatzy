package software.engineering.yatzy.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import software.engineering.yatzy.R;

public class ArtEngine {

    private Bitmap[] dice;
    Drawable highlight;

    public ArtEngine(Resources res) {
        dice = new Bitmap[6];
        dice[0] = BitmapFactory.decodeResource(res, R.drawable.dice_one);
        dice[1] = BitmapFactory.decodeResource(res, R.drawable.dice_two);
        dice[2] = BitmapFactory.decodeResource(res, R.drawable.dice_three);
        dice[3] = BitmapFactory.decodeResource(res, R.drawable.dice_four);
        dice[4] = BitmapFactory.decodeResource(res, R.drawable.dice_five);
        dice[5] = BitmapFactory.decodeResource(res, R.drawable.dice_six);

        highlight = res.getDrawable( R.drawable.select_highlight);
    }

    public Drawable getHighlight() {
        return highlight;
    }

    // Return goblinHead bitmap of frame
    public Bitmap getDiceSide(int frame) {
        return dice[frame];
    }
}
