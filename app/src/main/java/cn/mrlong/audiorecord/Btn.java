package cn.mrlong.audiorecord;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class Btn extends android.support.v7.widget.AppCompatButton {
    public Btn(Context context) {
        super(context);
    }

    public Btn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setText("按下");
                break;
            case MotionEvent.ACTION_MOVE:
                setText("移动");
                break;
            case MotionEvent.ACTION_UP:
                setText("抬起");
                break;
        }
        return super.onTouchEvent(event);
    }
}
