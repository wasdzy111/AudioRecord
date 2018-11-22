package cn.mrlong.audiorecord.recorder;


import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

public class MediaManager {


	private static MediaPlayer mPlayer;

	public static MediaPlayer getmPlayer() {
		return mPlayer;
	}

	private static boolean isPause;


//	public static  void playSound(Context context, String filePathString,
//								  OnCompletionListener onCompletionListener) {
//		// TODO Auto-generated method stub
//		if (mPlayer==null) {
////			mPlayer=new MediaPlayer();
//
//			mPlayer = MediaPlayer.create(context, Uri.fromFile(new File(filePathString)));
//
//			//保险起见，设置报错监听
//			mPlayer.setOnErrorListener(new OnErrorListener() {
//
//				@Override
//				public boolean onError(MediaPlayer mp, int what, int extra) {
//					// TODO Auto-generated method stub
//					mPlayer.resetLocation();
//					return false;
//				}
//			});
//		}else {
//			mPlayer.resetLocation();//就回复
//		}
//
//		try {
//			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//			mPlayer.setOnCompletionListener(onCompletionListener);
//			mPlayer.setDataSource(filePathString);
//			mPlayer.prepare();
//			mPlayer.start();
//		} catch (IllegalArgumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IllegalStateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


	public static  void playSound(String filePathString,
			OnCompletionListener onCompletionListener) {
		// TODO Auto-generated method stub
		if (mPlayer==null) {
			mPlayer=new MediaPlayer();

			System.out.println("==="+filePathString);

			//保险起见，设置报错监听
			mPlayer.setOnErrorListener(new OnErrorListener() {

				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					// TODO Auto-generated method stub
					mPlayer.reset();
					return false;
				}
			});
		}else {
			mPlayer.reset();//就回复
		}

		try {
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setOnCompletionListener(onCompletionListener);
			mPlayer.setDataSource(filePathString);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//停止函数
	public static void pause(){
		if (mPlayer!=null&&mPlayer.isPlaying()) {
			mPlayer.pause();
			isPause=true;
		}
	}
	
	//继续
	public static void resume()
	{
		if (mPlayer!=null&&isPause) {
			mPlayer.start();
			isPause=false;
		}
	}
	

	public  static void release()
	{
		if (mPlayer!=null) {
			mPlayer.release();
			mPlayer=null;
		}
	}
}
