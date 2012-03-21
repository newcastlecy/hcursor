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
 *����ִ洢�ռ��������ر���ɹ�,��Ҫ���
 *
 */

public class FontHttpDownloader
{
	private URL url=null;
	/**
	 * 
	 *�����ı��ļ�,���ǲ����ص�����
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
	 * ������������ʽ���ļ�
	 * �ú�������int����
	 * ����-1�����س���
	 * ����0�����سɹ�
	 * ����1�������ļ��Ѵ���
	 * ��������һ��Ϊ�����ַ���ӣ��ڶ���Ϊ���SD��Ŀ¼(/xx)��������Ϊ��ŵ��ļ�����
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
				
				//�ر�InputStream
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
	
	//����URL���������
	public InputStream getInputStreamFromUrl(String urlStr) throws MalformedURLException,IOException
	{
		url=new URL(urlStr);
		HttpURLConnection urlconn=(HttpURLConnection) url.openConnection();
		InputStream inputstream=urlconn.getInputStream();
		return inputstream;
		
	}
}
