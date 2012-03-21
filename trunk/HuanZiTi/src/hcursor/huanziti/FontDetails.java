package hcursor.huanziti;

import hcursor.service.DownloadService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.mobclick.android.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class FontDetails extends Activity
	{
		private String font_name;
		private String fontdetail_dl;
		private String zipFile;
		private String fontsrc = "/sdcard/kdfly/huanziti/font/";

		private ImageView iv_fontdetail_img;
		private Drawable drawable;

		@Override
		protected void onCreate(Bundle savedInstanceState) {

			final Button btn_fontdetail_dl;
			Button btn_fontdetail_use;
			Button btn_fontdetail_del;
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_fontdetails);

			Bundle extras = getIntent().getExtras();
			font_name = extras.getString("font_name");
			String font_size = extras.getString("font_size");
			String fontdetail_img = extras.getString("fontdetail_img");
			fontdetail_dl = extras.getString("fontdetail_dl");

			zipFile = fontsrc
					+ fontdetail_dl
							.replace(
									"http://update.kdfly.com/mysoft/huanziti/font/",
									"");

			setTitle(font_name + "  大小：" + font_size);

			iv_fontdetail_img = (ImageView) findViewById(R.id.iv_fontdetail_img);
			new ImageDownloadTask().execute(fontdetail_img);

			btn_fontdetail_use = (Button) findViewById(R.id.btn_fontdetail_use);
			btn_fontdetail_dl = (Button) findViewById(R.id.btn_fontdetail_dl);
			btn_fontdetail_dl.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(FontDetails.this,
							DownloadService.class);
					intent.putExtra("font_name", font_name);
					intent.putExtra("font_dlurl", fontdetail_dl);

					startService(intent);
					btn_fontdetail_dl.setClickable(false);
				}
			});
			btn_fontdetail_use.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Unzip(zipFile, fontsrc);
					runRootCommand("mount -o remount,rw rootfs /system");
					runRootCommand("cp -f " + zipFile.replace("zip", "ttf")
							+ " /system/fonts/DroidSansFallback.ttf");
					runRootCommand("chmod 644  /system/fonts/DroidSansFallback.ttf");
					System.out.println(zipFile.replace("zip", "ttf"));
					runRootCommand("mount -o remount,ro rootfs /system");

					new AlertDialog.Builder(FontDetails.this)
							.setTitle("提示")
							.setMessage(
									"字体更换完成，重启后就可以看到新字体了，是否立即重启？(如果不能自动重启，请手动重启手机即可生效。)")
							.setPositiveButton(
									"快速重启",
									new android.content.DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO
											// Auto-generated
											// method stub
											runRootCommand("killall  system_server");
										}
									})
							.setNeutralButton(
									"重启",
									new android.content.DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO
											// Auto-generated
											// method stub
											runRootCommand("reboot");
										}
									}).setNegativeButton("取消", null).show();
				}
			});
			btn_fontdetail_del = (Button) findViewById(R.id.btn_fontdetail_del);
			btn_fontdetail_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					File delzipFile = new File(zipFile);
					File delttfFile = new File(zipFile.replace("zip", "ttf"));
					if (delzipFile.exists()) {
						delzipFile.delete();
					} else if (delttfFile.exists()) {
						delttfFile.delete();
					} else {

					}
					new AlertDialog.Builder(FontDetails.this)
							.setTitle("提示")
							.setMessage("字体删除成功。")
							.setPositiveButton(
									"确定",
									new android.content.DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO
											// Auto-generated
											// method stub
											finish();
										}
									}).show();
				}
			});
			btn_fontdetail_use.setClickable(false);
			// 检测字体是否已经下载。
			Boolean checkisdown = CheckIsDown();
			if (checkisdown == true) {
				btn_fontdetail_use.setClickable(true);
				btn_fontdetail_use.setText("使用");
				btn_fontdetail_dl.setText("重新下载");
				btn_fontdetail_del.setVisibility(0);
			}
		}

		private Boolean CheckIsDown() {
			String[] getDataBasesPath;
			getDataBasesPath = (new File(fontsrc)).list();

			for (int i = 0; i < getDataBasesPath.length; i++) {
				String fileName = getDataBasesPath[i];
				if (fileName.compareTo(fontdetail_dl.replace(
						"http://update.kdfly.com/mysoft/huanziti/font/", "")) == 0) {
					return true;
				}
			}
			return false;
		}

		private static void Unzip(String zipFile, String targetDir) {
			int BUFFER = 4096; // 这里缓冲区我们使用4KB，
			String strEntry; // 保存每个zip的条目名称

			try {
				BufferedOutputStream dest = null; // 缓冲输出流
				FileInputStream fis = new FileInputStream(zipFile);
				ZipInputStream zis = new ZipInputStream(
						new BufferedInputStream(fis));
				ZipEntry entry; // 每个zip条目的实例

				while ((entry = zis.getNextEntry()) != null) {
					try {
						// Log.i("Unzip: ","="+ entry);
						int count;
						byte data[] = new byte[BUFFER];
						strEntry = entry.getName();

						File entryFile = new File(targetDir + strEntry);
						File entryDir = new File(entryFile.getParent());
						if (!entryDir.exists()) {
							entryDir.mkdirs();
						}

						FileOutputStream fos = new FileOutputStream(entryFile);
						dest = new BufferedOutputStream(fos, BUFFER);
						while ((count = zis.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						dest.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				zis.close();
			} catch (Exception cwj) {
				cwj.printStackTrace();
			}
		}

		public static boolean runRootCommand(String command) {
			Process process = null;
			DataOutputStream os = null;
			try {
				process = Runtime.getRuntime().exec("su");
				os = new DataOutputStream(process.getOutputStream());
				os.writeBytes(command + "\n");
				os.writeBytes("exit\n");
				os.flush();
				process.waitFor();
			} catch (Exception e) {
				Log.d("hcursor", "the device is not rooted, error message: "
						+ e.getMessage());
				return false;
			} finally {
				try {
					if (os != null) {
						os.close();
					}
					if (process != null) {
						process.destroy();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return true;
		}

		private class ImageDownloadTask extends
				AsyncTask<Object, Object, Drawable>
			{

				@Override
				protected Drawable doInBackground(Object... params) {
					// TODO Auto-generated method stub
					drawable = WebImageBuilder
							.loadImageFromNetwork((String) params[0]);
					return null;
				}

				@Override
				protected void onPostExecute(Drawable result) {
					// TODO Auto-generated method stub
					iv_fontdetail_img.setImageDrawable(drawable);
					super.onPostExecute(result);
				}

			}

		public void onResume() {
			super.onResume();
			MobclickAgent.onResume(this);
		}

		public void onPause() {
			super.onPause();
			MobclickAgent.onPause(this);
		}
	}
