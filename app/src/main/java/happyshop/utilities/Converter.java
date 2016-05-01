package happyshop.utilities;

import android.content.res.Resources;
import android.util.TypedValue;

public class Converter
{
    public static float convertDpToPixels(float dp) {
        Resources resources = Resources.getSystem();

        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
        return px;
    }

}