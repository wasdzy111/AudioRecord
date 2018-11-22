# AudioRecord

#### 项目介绍
自定义一个Button,实现仿微信发语音界面！
生成文件格式.amr(ios 也可以使用貌似)

#### 安装教程

Step 1. Add it in your root build.gradle at the end of repositories:

~~~
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
~~~
Step 2. Add the dependency

~~~
	dependencies {
	        implementation 'com.gitee.wasdzy:AudioRecord:1.0.0'
	}
~~~
Step 3.在布局文件中直接用于自定义button
~~~
	<cn.mrlong.audiorecord.recorder.AudioRecordButton
	android:id="@+id/arb"
	android:background="@drawable/button_recordnormal"
	android:layout_width="match_parent"
	android:layout_height="50dp"
	android:text="按住 说话" />
~~~
Step 4 一建调用
~~~
	//第一个参数是回调，第二个是正常下的背景样式，第三是录制中的背景样式
	audioRecordButton.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(final float seconds, final String filePath) {
				//final float seconds 时间, final String filePath 文件存储位置
            }
        }, R.drawable.button_recordnormal, R.drawable.button_recording);
	//或者 使用默认的样式
	 audioRecordButton.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
            @Override
            public void onFinished(final float seconds, final String filePath) {
            }
        });
~~~


#### 参与贡献

1. 感谢强大的网友提供源码

