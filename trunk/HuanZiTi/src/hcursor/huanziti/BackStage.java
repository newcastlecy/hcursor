package hcursor.huanziti;

import hcursor.tool.AppConstant;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
/**
 * 
 * service 与activity 之间的消息通信 既是activity向service发消息 需要实现ServiceConnection接口 绑定服务
 *
 */
public class BackStage extends Activity
{
	private IntentFilter filter=null;
	private TextView progressTextView=null;
	private ProgressBar downloadProgress=null;
	private TextView mp3_nameTextView=null;
	private String mp3_name=null;
	private int downloadSize=0;
	private Handler handler=null;
	private Runnable progressRunnable=null;
	private DownloadBroadCastReceiver receiver=null;
	private int fileSize=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.progressbar);
		
		progressTextView=(TextView) findViewById(R.id.progress);
		downloadProgress=(ProgressBar) findViewById(R.id.downloadProgress);
		mp3_nameTextView=(TextView) findViewById(R.id.mp3Name);
		
		/**
		 * 第一次启动BackStage的Activity时启动接收广播
		 */
		IntentFilter filter=new IntentFilter();
		filter.addAction(AppConstant.DOWNLOAD_MESSAGE_ACTION);
		receiver=new DownloadBroadCastReceiver();
		registerReceiver(receiver,filter);
						
			
			//定义一个Handler，用于处理下载线程与UI间通讯
			handler=new Handler()
			{				
				@Override
				public void handleMessage(Message msg) 
				{
					downloadProgress.setVisibility(View.VISIBLE);
					mp3_nameTextView.setVisibility(View.VISIBLE);
					progressTextView.setVisibility(View.VISIBLE);
					
					downloadProgress.setMax(msg.arg2);
					mp3_nameTextView.setText((CharSequence) msg.obj);
					
					downloadProgress.setProgress(msg.arg1);
					handler.post(progressRunnable);
					super.handleMessage(msg);
				}
				
			};
			
			progressRunnable=new Runnable() 
			{
				public void run() 
				{
					int result = downloadSize * 100 / fileSize;				
					progressTextView.setText(result + "%");	
					if(downloadSize==fileSize)
					{
						handler.removeCallbacks(progressRunnable);
						System.out.println("BackStage---->stop");
					}
				}
			};

		
	}
	
	class DownloadBroadCastReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context arg0, Intent intent) 
		{
			downloadSize=intent.getIntExtra("downloadSize",0);
			fileSize=intent.getIntExtra("fileSize",0);
			mp3_name=intent.getStringExtra("mp3_name");
					
			Message msg=handler.obtainMessage();
			msg.arg1=downloadSize;
			msg.arg2=fileSize;
			msg.obj=mp3_name;
			handler.sendMessage(msg);
			
		}		
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
//		if(receiver!=null)
//		{
//			unregisterReceiver(receiver);
//		}
//		else
//		{
//			System.out.println("onPause---->"+"null");
//		}

	}

	@Override
	protected void onResume()
	{
		super.onResume();	
		if(getFilter()!=null)
		{
			
			receiver=new DownloadBroadCastReceiver();
			registerReceiver(receiver,getFilter());
		}
		else
		{
			System.out.println("onResume---->"+"null");
		}

	}

	private IntentFilter getFilter()
	{
		if(filter==null)
		{
			IntentFilter filter=new IntentFilter();
			filter.addAction(AppConstant.DOWNLOAD_MESSAGE_ACTION);
		}
		return filter;
	}
}
