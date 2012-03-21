package hcursor.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.os.Environment;

public class FileUtils 
{
	/**
	 * ��Ҫ֪����ǰSD����Ŀ¼��Environment.getExternalStorageDierctory()
	 */

	private String SDPATH;

	public String getSDPATH() 
	{
		return SDPATH;
	}
	public FileUtils()
	{          //  Ŀ¼��/sdcard
		SDPATH=Environment.getExternalStorageDirectory()+"/kdfly/huanziti/font/";
	}
	
	//��sdcard���ϴ����ļ�
	public File createSDFile(String fileName) throws IOException
	{
		File file=new File(SDPATH+fileName);
		file.createNewFile();
		return file;
	}
	
	//��sd���ϴ���Ŀ¼
	public File createSDDir(String dirName)
	{
		File dir=new File(SDPATH+dirName);
		//mkdirֻ�ܴ���һ��Ŀ¼ ,mkdirs���Դ����༶Ŀ¼
		dir.mkdir();
		return dir;
	}
	
	//�ж�sd���ϵ��ļ����Ƿ����
	public boolean isFileExist(String fileName)
	{
		File file=new File(SDPATH+fileName);
		return file.exists();
	}
	
	/**
	 *��һ��inputstream���������д��SD����
	 *��һ������ΪĿ¼��
	 *�ڶ�������Ϊ�ļ���
	 */
	public File write2SDFromInput(String path,String fileName,InputStream inputstream)
	{
		File file=null;
		OutputStream output=null;
		try 
		{
			createSDDir(path);
			//System.out.println(createSDDir(path).getParentFile());
			file=createSDFile(path+fileName);
			output=new FileOutputStream(file);
			//4kΪ��λ��ÿ4Kдһ��
			byte buffer[]=new byte[4*1024];
			int temp=0;
			while((temp=inputstream.read(buffer))!=-1)
			{
				//��ȡָ����,��ֹд��û�õ���Ϣ
				output.write(buffer,0,temp);
			}
			output.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				output.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		return file;		
	}
}
