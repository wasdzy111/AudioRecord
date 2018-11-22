package cn.mrlong.audiorecord.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import java.util.ArrayList;

/**
 * Created by greatpresident on 2014/8/5. 
 */
public class AudioRecordData {

    public interface IDataCallBack{
        public void callBack(float datas) ;
    }

    private static final String TAG = "AudioRecord";
    static final int SAMPLE_RATE_IN_HZ = 8000;
    private int size = 0 ;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    private AudioRecord mAudioRecord;
    private boolean isGetVoiceRun;
    private Object mLock;
    private ArrayList<Float> datas ;
    private ArrayList<Float> tempDatas ;
    private IDataCallBack _callBack ;

    public AudioRecordData(IDataCallBack callBack, int size) {
        this._callBack = callBack ;
        this.size = size ;
        mLock = new Object();
        datas = new ArrayList<>() ;
        tempDatas = new ArrayList<>() ;
    }

    public void setGetVoiceRun(boolean getVoiceRun) {
        isGetVoiceRun = getVoiceRun;
    }

    public void getNoiseLevel() {
        if (isGetVoiceRun) {
            Log.e(TAG, "还在录着呢");
            return;
        }
            mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (mAudioRecord == null) {
            Log.e("sound", "mAudioRecord初始化失败");
        }
        isGetVoiceRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                while (isGetVoiceRun) {
                    //r是实际读取的数据长度，一般而言r会小于buffersize
                    int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                    long v = 0;
                    // 将 buffer 内容取出，进行平方和运算
                    for (int i = 0; i < buffer.length; i++) {
                        v += buffer[i] * buffer[i];
                    }
                    // 平方和除以数据总长度，得到音量大小。
                    double mean = v / (double) r;
                    if(!Double.isInfinite(mean) && !Double.isNaN(mean)){
                        double volume = 10 * Math.log10(mean);
                        _callBack.callBack((float) volume);
                        Log.d(TAG, "分贝值:" + volume);
                    }
                    synchronized (mLock) {
                        try {
                            mLock.wait(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }).start();
    }
}  