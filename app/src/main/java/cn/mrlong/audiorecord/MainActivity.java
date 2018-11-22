package cn.mrlong.audiorecord;

import android.Manifest;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.mrlong.audiorecord.recorder.AudioRecordButton;
import cn.mrlong.audiorecord.recorder.MediaManager;
import cn.mrlong.audiorecord.recorder.Recorder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "===>";
    private RecyclerView recyclerView;
    private AudioAdapter audioAdapter;
    private AudioRecordButton audioRecordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.ry);
        initRy();

        //动态权限申请
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new io.reactivex.functions.Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {

                        } else {

                        }
                    }
                });

        audioRecordButton = findViewById(R.id.arb);
        audioRecordButton.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(final float seconds, final String filePath) {
                Recorder recorder = new Recorder(seconds, filePath);
                audioAdapter.addData(recorder);
            }
        }, R.drawable.button_recordnormal, R.drawable.button_recording);
    }

    private void initRy() {
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(manager);
        audioAdapter = new AudioAdapter(MainActivity.this, new ItemOnClickListener() {
            @Override
            public void itemOnClickListener(int i) {
                Recorder recorder = audioAdapter.getData(i);
                //播放
                MediaManager.playSound(recorder.getFilePathString(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                    }
                });
            }
        });
        recyclerView.setAdapter(audioAdapter);
    }


}
