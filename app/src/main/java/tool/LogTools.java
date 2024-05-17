package tool;

import android.util.Log;

/**
 * @Description 调试Log.d
 * @author
 *
 */
public class LogTools {
	public static void println(String hint, String msg) {
		if(msg.length()>300){
			msg=msg.substring(0,300);
		}
		if (hint == null) {
			Log.d("test", msg+"");
		} else {
			Log.d(hint, msg+"");
		}
	}
	public static void println(String msg) {
		Log.d("test", msg+"");
	}


}
