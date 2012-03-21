package hcursor.huanziti;

import hcursor.download.HttpDownloader;
import hcursor.download.Mp3Info;
import hcursor.download.Mp3ListContentHandler;
import hcursor.tool.AppConstant;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class MondayFragment extends ListFragment
	{
		@Override
		public void onDestroy()
			{
				// TODO Auto-generated method stub
				if (dialog != null)
					{
						dialog.dismiss();
					}
				super.onDestroy();
			}

		@Override
		public void onResume()
			{
				// TODO Auto-generated method stub
				updateListView();
				super.onResume();
			}

		ListView lvmain;
		TextView font_name;
		TextView font_size;
		TextView fontdetail_img;
		TextView fontdetail_dl;
		ProgressDialog dialog;

		private Mp3Info mp3Info;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
			{
				View view = inflater.inflate(R.layout.fragment_monday,
						container, false);
				lvmain = (ListView) view.findViewById(R.id.lvmain);

				DownLoadTask dTask = new DownLoadTask();
				dTask.execute(100);

				// String
				// xml=downloadXML("http://update.kdfly.com/mysoft/huanziti/resources.xml");
				// //System.out.println("xml--->"+xml);
				// List<Mp3Info> mp3Infos=parseSAX(xml);
				// MySimpleAdapter SA=buildSimpleAdapter(mp3Infos);
				// lvmain.setAdapter(SA);

				lvmain.setOnItemClickListener(new OnItemClickListener()
					{

						@Override
						public void onItemClick(AdapterView<?> arg0, View view,
								int arg2, long arg3)
							{
								// TODO Auto-generated method stub
								font_name = (TextView) view
										.findViewById(R.id.mp3_name);
								font_size = (TextView) view
										.findViewById(R.id.mp3_size);
								fontdetail_img = (TextView) view
										.findViewById(R.id.fontdetail_img);
								fontdetail_dl = (TextView) view
										.findViewById(R.id.fontdetail_dl);

								Intent intent = new Intent(getActivity(),
										FontDetails.class);
								intent.putExtra("font_name",
										font_name.getText());
								intent.putExtra("font_size",
										font_size.getText());
								intent.putExtra("fontdetail_img",
										fontdetail_img.getText());
								intent.putExtra("fontdetail_dl",
										fontdetail_dl.getText());
								startActivity(intent);
							}
					});
				return view;
			}

		@Override
		public void onActivityCreated(Bundle savedInstanceState)
			{
				super.onActivityCreated(savedInstanceState);
			}

		public MySimpleAdapter buildSimpleAdapter(List<Mp3Info> mp3Infos)
			{
				List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				for (Iterator iter = mp3Infos.iterator(); iter.hasNext();)
					{
						mp3Info = (Mp3Info) iter.next();
						HashMap<String, String> map = new HashMap<String, String>();
						map.put("mp3_name", mp3Info.getMp3Name());
						map.put("mp3_size", mp3Info.getMp3Size());
						map.put("font_img", mp3Info.getfontimg());
						map.put("fontdetail_img", mp3Info.getfontdetail_img());
						map.put("font_dl", mp3Info.getfontdl());

						Boolean checkisdown = CheckIsDown();
						if (checkisdown == true)
							{
								list.add(map);
							}
					}
				MySimpleAdapter simpleAdapter = new MySimpleAdapter(
						getActivity(), list, R.layout.mp3_info_item,
						new String[] { "mp3_name", "mp3_size", "font_img",
								"fontdetail_img", "font_dl" }, new int[] {
								R.id.mp3_name, R.id.mp3_size, R.id.font_img,
								R.id.fontdetail_img, R.id.fontdetail_dl });
				return simpleAdapter;
			}

		private Boolean CheckIsDown()
			{
				String[] getDataBasesPath;
				String fontsrc = "/sdcard/kdfly/huanziti/font/";
				File file = new File(fontsrc);
				if (!file.exists())
					{
						file.mkdir();
						getDataBasesPath = (new File(fontsrc)).list();

						for (int i = 0; i < getDataBasesPath.length; i++)
							{
								String fileName = getDataBasesPath[i];
								if (fileName
										.compareTo(mp3Info
												.getfontdl()
												.replace(
														"http://update.kdfly.com/mysoft/huanziti/font/",
														"")) == 0)
									{
										return true;
									}
							}
					}
				else {
					getDataBasesPath = (new File(fontsrc)).list();

					for (int i = 0; i < getDataBasesPath.length; i++)
						{
							String fileName = getDataBasesPath[i];
							if (fileName
									.compareTo(mp3Info
											.getfontdl()
											.replace(
													"http://update.kdfly.com/mysoft/huanziti/font/",
													"")) == 0)
								{
									return true;
								}
						}
				}
				return false;
			}

		// 列表更新按钮方法
		public void updateListView()
			{
				// String xml =
				// downloadXML("http://www.kdfly.com/mysoft/huanziti/resources.xml");
				// System.out.println("xml--->"+xml);
				String xml = AppConstant.xml;
				List<Mp3Info> mp3Infos = parseSAX(xml);
				MySimpleAdapter SA = buildSimpleAdapter(mp3Infos);
				lvmain.setAdapter(SA);
			}

		public String downloadXML(String urlStr)
			{
				HttpDownloader httpDownloader = new HttpDownloader();
				String xmlStr = httpDownloader.download(urlStr);
				return xmlStr;
			}

		// SAX解析过程
		public List<Mp3Info> parseSAX(String xmlStr)
			{
				SAXParserFactory factory = SAXParserFactory.newInstance();
				SAXParser parser;
				List<Mp3Info> infos = new ArrayList<Mp3Info>();
				try
					{
						parser = factory.newSAXParser();
						XMLReader xmlreader = parser.getXMLReader();
						Mp3ListContentHandler mp3listContentHandler = new Mp3ListContentHandler(
								infos);
						xmlreader.setContentHandler(mp3listContentHandler);
						xmlreader.parse(new InputSource(
								new StringReader(xmlStr)));
						for (Iterator iterator = infos.iterator(); iterator
								.hasNext();)
							{
								// System.out.println("*************");
								Mp3Info mp3Info = (Mp3Info) iterator.next();
								// System.out.println(mp3Info);
							}
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				return infos;
			}

		class DownLoadTask extends AsyncTask<Integer, Integer, String>
			{
				MySimpleAdapter SA;

				@Override
				protected void onPreExecute()
					{
						// TODO Auto-generated method stub
						// 定义对话框进度条对象
						dialog = new ProgressDialog(getActivity());
						// 设置进度条标题
						dialog.setTitle("请稍等");
						// 设置进度条内容
						dialog.setMessage("本地字库正在加载!");
						// 设置是否显示进度值默认进度条设置true或false都不显示
						dialog.setIndeterminate(false);
						// 设置是否按back键取消
						// dialog.setCancelable(false);
						dialog.show();
						super.onPreExecute();
					}

				@Override
				protected String doInBackground(Integer... params)
					{
						// TODO Auto-generated method stub
						String xml = "";
						xml = downloadXML("http://update.kdfly.com/mysoft/huanziti/resources.xml");
						if (!(xml == null))
							{
								AppConstant.xml = xml;
								// System.out.println("xml--->"+xml);
								List<Mp3Info> mp3Infos = parseSAX(xml);
								SA = buildSimpleAdapter(mp3Infos);
							}
						return null;
					}

				@Override
				protected void onPostExecute(String result)
					{
						// TODO Auto-generated method stub
						lvmain.setAdapter(SA);
						dialog.dismiss();
						super.onPostExecute(result);
					}
			}
	}
