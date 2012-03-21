package hcursor.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import hcursor.tool.AppConstant;
import hcursor.huanziti.BackStage;
import hcursor.huanziti.R;
import hcursor.tool.FileUtils;


public class DownloadService extends Service
{
	private URL url=null;
	private int downloadSize;
	private String mp3_name=null;
	private String font_name=null;
	private String font_dlurl=null;
	private int fileSize=0;
	
	//����֪ͨ��
	private static final int NOTIFY_ID = 0;  
    private boolean cancelled;  
      
    private Context mContext = this;  
    private NotificationManager mNotificationManager;  
    private Notification mNotification; 
    //����֪ͨ������
    
    private Handler handler = new Handler() {  
        public void handleMessage(android.os.Message msg) {  
            switch (msg.what) {  
            case 1:  
                int rate = msg.arg1;  
                if (rate < 100) {  
                    //���½���   
                    RemoteViews contentView = mNotification.contentView;  
                    contentView.setTextViewText(R.id.rate, rate + "%");  
                    
                    contentView.setProgressBar(R.id.notificationprogress, 100, rate, false);  
                } else {  
                    //������Ϻ�任֪ͨ��ʽ   
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;  
                    mNotification.contentView = null;  
                    Intent intent = new Intent(mContext, BackStage.class);  
                    // ��֪�����   
                    intent.putExtra("completed", "yes");  
                    //���²���,ע��flagsҪʹ��FLAG_UPDATE_CURRENT   
                    PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
                    mNotification.setLatestEventInfo(mContext, "�������", "�ļ����������", contentIntent);  
                }  
  
                // ��������֪ͨһ��,���򲻻����   
                mNotificationManager.notify(NOTIFY_ID, mNotification);  
                  
                if (rate >= 100) {  
                    stopSelf(); //ֹͣ����   
                }  
                  
                break;  
            case 0:  
                // ȡ��֪ͨ   
                mNotificationManager.cancel(NOTIFY_ID);  
                break;  
            }  
        };  
    };  
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{	
		
		//֪ͨ������
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);  
		
        int icon = R.drawable.icon;  
        CharSequence tickerText = "��ʼ����";  
        long when = System.currentTimeMillis();  
        mNotification = new Notification(icon, tickerText, when);  
  
        // ������"��������"��Ŀ��   
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;  
        
        font_name=intent.getStringExtra("font_name");
        
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(), R.layout.download_notification_layout);  
        contentView.setTextViewText(R.id.fileName, font_name);  
        // ָ�����Ի���ͼ   
        mNotification.contentView = contentView;  
  
        Intent intnt = new Intent(this, BackStage.class);  
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intnt, PendingIntent.FLAG_UPDATE_CURRENT);  
        // ָ��������ͼ   
        mNotification.contentIntent = contentIntent;  
  
        mNotificationManager.notify(NOTIFY_ID, mNotification);  
		//֪ͨ����������
		
		font_dlurl=intent.getStringExtra("font_dlurl");
		mp3_name=font_dlurl.replace("http://update.kdfly.com/mysoft/huanziti/font/", "");
		
		DownloadThread downloadThread=new DownloadThread(
				font_dlurl,"/",mp3_name);
		Thread t=new Thread(downloadThread);
		t.start();
				
		return super.onStartCommand(intent, flags, startId);
	}
	
	class DownloadThread implements Runnable
	{
		String urlStr=null;
		String path=null;
		String fileName=null;
		public DownloadThread(String urlStr,String path,String fileName)
		{
			this.urlStr=urlStr;
			this.fileName=fileName;
			this.path=path;
		}
		public void run() 
		{
			try 
			{
				int result=downloadFile(urlStr,path,fileName);
				if(result==1)
				{
					System.out.println("downloadFile Existed");
				}
				else
				if(result==-1)
				{
					System.out.println("downloadFile Failed");
				}
				else
				if(result==0)
				{
					System.out.println("downloadFile Success");
				}
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
	
	public int downloadFile(String urlStr,String path,String fileName) throws IOException
	{
		int downloadCount = 0;
		url=new URL(urlStr);
		HttpURLConnection urlconn=(HttpURLConnection) url.openConnection();
		InputStream inputstream=urlconn.getInputStream();
		OutputStream outputStream=null;
		try 
		{
			FileUtils fileutils=new FileUtils();
			File f = new File("/sdcard/kdfly/huanziti/font",fileName);
//			if(fileutils.isFileExist(path+fileName))
//			{
//				f.delete();
//				return 1;
//			}
//			else
			{
				f.delete();
				if(!fileutils.isFileExist(path))
				{
					fileutils.createSDDir(path);
				}
				File file=fileutils.createSDFile(path+fileName);
				outputStream=new FileOutputStream(file);
				this.fileSize=urlconn.getContentLength();
				byte buffer[]=new byte[4*1024];
				int temp=0;
				downloadSize=0;
				while((temp=inputstream.read(buffer))!=-1)
				{
					outputStream.write(buffer,0,temp);
					downloadSize+=temp;
					
                    //Ϊ�˷�ֹƵ����֪ͨ����Ӧ�óԽ����ٷֱ�����10��֪ͨһ��
                    if((downloadCount == 0)||(int) (downloadSize*100/this.fileSize)>downloadCount){ 
                        downloadCount += 10;
    		            Message msg = handler.obtainMessage();  
    		            msg.what = 1;  
    		            msg.arg1 = (int)downloadSize*100/this.fileSize+9;  
    		            handler.sendMessage(msg);  
                    }
					
					Intent intent=new Intent();
					intent.setAction(AppConstant.DOWNLOAD_MESSAGE_ACTION);
					intent.putExtra("downloadSize",downloadSize);
					intent.putExtra("fileSize",fileSize);
					intent.putExtra("mp3_name",mp3_name);
					sendBroadcast(intent);
				}
/*				do
				{
					//ѭ����ȡ
					temp = inputstream.read(buffer);	
					if (temp == -1)
					{
						break;
					}
					outputStream.write(buffer, 0,temp);
					downloadSize += temp;
				} while (true);*/
				
				outputStream.flush();
				if(file==null)
					return -1;
				inputstream.close();
				outputStream.close();
			}
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return -1;
		}
		return 0;
	}
	@Override
	public IBinder onBind(Intent intent)
	{   
	        return null;
	}
}
