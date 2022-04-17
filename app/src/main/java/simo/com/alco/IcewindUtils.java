package simo.com.alco;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 *
 * Created by icewind on 23.03.17.
 */

public class IcewindUtils {

    /**
     * Get a diff between two dates
     * @param date1 the oldest date
     * @param date2 the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static long getMillisDiff(long date1, long date2, TimeUnit timeUnit) {
        long diffInMillies = date2 - date1;
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhoneNumber(String phone)
    {
        if (!phone.trim().equals("") || phone.length() > 10)
        {
            return android.util.Patterns.PHONE.matcher(phone).matches();
        }

        return false;
    }


}
