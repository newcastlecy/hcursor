package hcursor.download;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import hcursor.tool.FileUtils;
/**
 * 
 *会出现存储空间满，下载表面成功,需要解决
 *
 */

public class FontHttpDownloader
{
	private URL url=null;
	/**
	 * 
	 *下载文本文件,但是不下载到本地
	 */

	public String downloadtext(String urlStr)
	{
		StringBuffer sb=new StringBuffer();
		String line=null;
		BufferedReader buffer=null;
		
		try 
		{
			url=new URL(urlStr);
			HttpURLConnection urlconn=(HttpURLConnection) url.openConnection();
			buffer=new BufferedReader(new InputStreamReader(urlconn.getInputStream()));
			while((line=buffer.readLine())!=null)
			{
				sb.append(line);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				buffer.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		return sb.toString();		
	} 
	
	/**
	 * 
	 * 可下载任意形式的文件
	 * 该函数返回int类型
	 * 返回-1：下载出错
	 * 返回0：下载成功
	 * 返回1：代表文件已存在
	 * 参数：第一个为网络地址链接，第二个为存放SD卡目录(/xx)，第三个为存放的文件名称
	 */

	public int downloadmp3(String urlStr,String path,String fileName)
	{
		InputStream inputstream=null;
		try 
		{
			FileUtils fileutils=new FileUtils();
			if(fileutils.isFileExist(path+fileName))
			{
				return 1;
			}
			else
			{
			
				inputstream = getInputStreamFromUrl(urlStr);
				File resultFile=fileutils.write2SDFromInput(path, fileName, inputstream);
				if(resultFile==null)
					return -1;
				
				//关闭InputStream
				inputstream.close();
			}			
		} 
		catch (IOException e) 
			{
				e.printStackTrace();
				return -1;
			}
/*		finally
		{
			try 
			{
				inputstream.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}*/
		return 0;		
	}
	
	//根据URL获得输入流
	public InputStream getInputStreamFromUrl(String urlStr) throws MalformedURLException,IOException
	{
		url=new URL(urlStr);
		HttpURLConnection urlconn=(HttpURLConnection) url.openConnection();
		InputStream inputstream=urlconn.getInputStream();
		return inputstream;
		
	}
}
