package tool;

import android.widget.Toast;


/**
 * 全局Toast显示
 * 避免Toast长时间显示问题
 *
 * @author
 */
public class OwerToastShow {
      private static Toast toast = null;
//    private static TextView tv_mr_toastHint;
//
//
//    /**
//     * 自定义布局的toast提醒
//     * @param msg 提示信息
//     */
//    public static void show(String msg) {
//        if (msg == null || msg.length() < 1) {
//            return;
//        }
//        if (toast == null) {
//            toast = Toast.makeText(AppApplication.context, msg, Toast.LENGTH_SHORT);
//            View  mrToastView = LayoutInflater.from(AppApplication.context).inflate(R.layout.toastview_mr_black, null);
//            tv_mr_toastHint = (TextView) mrToastView.findViewById(R.id.tv_toastHint);
//            toast.setDuration(Toast.LENGTH_SHORT);
//            toast.setGravity(Gravity.CENTER, 0, 0);//居中显示
//        }
//        tv_mr_toastHint.setText(msg);
//        toast.show();
//    }

    public static void show(String msg) {
        if (msg == null || msg.length() < 1) {
            return;
        }
        if (toast == null) {
//            toast = Toast.makeText(AppApplication.context, msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

}
