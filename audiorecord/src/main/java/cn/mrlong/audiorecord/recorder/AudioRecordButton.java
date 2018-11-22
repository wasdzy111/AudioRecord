package cn.mrlong.audiorecord.recorder;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import cn.mrlong.audiorecord.R;


/**
 * 自定义按钮，实现按住调用系统的录音，并发送等功能,修复了卡顿问题
 */

public class AudioRecordButton extends AppCompatButton implements AudioManager.AudioStageListener {

    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private String dir;
    private static final int DISTANCE_Y_CANCEL = 50;
    private int mCurrentState = STATE_NORMAL;
    // 已经开始录音
    private boolean isRecording = false;
    private DialogManager mDialogManager;
    private AudioManager mAudioManager;
    private float mTime = 0;
    // 是否触发了onlongclick，准备好了
    private boolean mReady;
    //默认的存储文件目录
    private String saveFilePath = "/AudioCache";
    private AudioFinishRecorderListener mListener;

    public AudioRecordButton(Context context) {
        this(context, null);
    }

    public AudioRecordButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
        //获得存储文件位置
        getSaveDir(context);
        mAudioManager = AudioManager.getInstance(dir);
        mAudioManager.setOnAudioStageListener(this);
        //只有触发了onLongClick才会触发 开始储备录音；避免了误触发 点击事件
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 获取存储位置
     *
     * @param context
     */
    private void getSaveDir(Context context) {
        try {
            if (existSDCard()) {
                dir = Environment.getExternalStorageDirectory().getAbsolutePath() + saveFilePath;
            } else {
                dir = context.getFilesDir().getAbsolutePath() + "/AudioCache";
            }
        } catch (Exception e) {
            e.printStackTrace();
            dir = context.getFilesDir().getAbsolutePath() + "/AudioCache";
        }
    }

    /**
     * 判断sd卡是否存在
     *
     * @return
     */
    private boolean existSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 回调获得存储文件 及 文件时间长短
     */
    public interface AudioFinishRecorderListener {
        void onFinished(float seconds, String filePath);
    }

    //按钮的背景设置
    private int normalBackground = R.drawable.button_recordnormal;
    private int recordingbackground = R.drawable.button_recording;

    public void setAudioFinishRecorderListener(AudioFinishRecorderListener listener, int normalBackgroundDrawable, int recordingBackgroundDrawable) {
        this.mListener = listener;
        this.normalBackground = normalBackgroundDrawable;
        this.recordingbackground = recordingBackgroundDrawable;
    }

    // 获取音量大小的runnable
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1f;
                    mhandler.sendEmptyMessage(MSG_VOICE_CHANGE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // 准备三个常量
    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGE = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED:
                    // 显示应该是在audio end prepare之后回调
                    if (null != mDialogManager)
                        mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    // 需要开启一个线程来变换音量
                    break;
                case MSG_VOICE_CHANGE:
                    if (null != mDialogManager)
                        mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS:
                    break;
            }
        }
    };

    /**
     * AudioManager 准备ok
     */
    @Override
    public void wellPrepared() {
        mhandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }


    /**
     * 根据按下的时间 触发不同的提示
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                changeState(STATE_RECORDING);
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 根据x，y来判断用户是否想要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                // 首先判断是否有触发onlongclick事件，没有的话直接返回reset
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                // 如果按的时间太短，还没准备好或者时间录制太短，就离开了，则显示这个dialog
                if (!isRecording || mTime < 0.6f) {
                    if (null != mDialogManager)
                        mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mhandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);// 持续1.3s
                } else if (mCurrentState == STATE_RECORDING) {//正常录制结束
                    if (null != mDialogManager)
                        mDialogManager.dimissDialog();
                    mAudioManager.release();// release释放一个mediarecorder
                    if (mListener != null) {// 并且callbackActivity，保存录音
                        mListener.onFinished(mTime, mAudioManager.getCurrentFilePath());
                    }
                } else if (mCurrentState == STATE_WANT_TO_CANCEL) {
                    // cancel
                    mAudioManager.cancel();
                    if (null != mDialogManager)
                        mDialogManager.dimissDialog();
                }
                reset();// 恢复标志位
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 重置状态
     */
    private void reset() {
        isRecording = false;
        changeState(STATE_NORMAL);
        mReady = false;
        mTime = 0;
    }

    /**
     * 判断用户取消操作
     *
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {// 判断是否在左边，右边，上边，下边
            return true;
        }
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    /**
     * 根据不同的状态设置文字等
     *
     * @param state
     */
    private void changeState(int state) {
        if (mCurrentState != state) {
            mCurrentState = state;
            switch (mCurrentState) {
                case STATE_NORMAL:
                    //setBackgroundResource(R.drawable.button_recordnormal);
                    setBackgroundResource(normalBackground);
                    setTextStr("按住 说话");
                    break;
                case STATE_RECORDING:
                    //setBackgroundResource(R.drawable.button_recording);
                    setBackgroundResource(recordingbackground);
                    setTextStr("松开 结束");
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    //setBackgroundResource(R.drawable.button_recording);
                    setBackgroundResource(recordingbackground);
                    setTextStr("松开手指，取消发送");
                    if (null != mDialogManager)
                        mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    /**
     * 设置文字方法，最开始是解决部分手机卡顿的源点
     *
     * @param s
     */
    public void setTextStr(String s) {
        try {
            this.setText(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onPreDraw() {
        return true;
    }


}
