package hcursor.huanziti;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class WebImageBuilder
	{

		/**
		 * ͨ��ͼƬurl����ͼƬBitmap
		 * 
		 * @param url
		 * @return
		 */
		// public static Bitmap returnBitMap(String url) {
		// URL myFileUrl = null;
		// Bitmap bitmap = null;
		// try {
		// myFileUrl = new URL(url);
		// } catch (MalformedURLException e) {
		// e.printStackTrace();
		// }
		// try {
		// HttpURLConnection conn = (HttpURLConnection) myFileUrl
		// .openConnection();
		// conn.setDoInput(true);
		// conn.connect();
		// InputStream is = conn.getInputStream();
		// bitmap = BitmapFactory.decodeStream(is);
		// is.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// return bitmap;
		// }

		public static Bitmap returnBitMap(String url) {
			Bitmap bm = drawableToBitmap(loadImageFromNetwork(url));
			return bm;
		}

		public static Bitmap drawableToBitmap(Drawable drawable) {
			// ȡ drawable �ĳ���
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();

			// ȡ drawable ����ɫ��ʽ
			Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565;
			// ������Ӧ bitmap
			Bitmap bitmap = Bitmap.createBitmap(w, h, config);
			// ������Ӧ bitmap �Ļ���
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			// �� drawable ���ݻ���������
			drawable.draw(canvas);
			return bitmap;
		}

		public static Drawable loadImageFromNetwork(String imageUrl) {
			String imgcache = "/sdcard/kdfly/huanziti/imgcache";
			// �����ļ��С�
			File mWorkingPath = new File(imgcache);
			if (!mWorkingPath.exists()) {
				if (!mWorkingPath.mkdirs()) {
					Log.i("sheepkx", "�޷�����ͼƬ�����ļ��У�");
				}
			}

			Drawable drawable = null;
			if (imageUrl == null)
				return null;
			String imagePath = "";
			String fileName = "";

			// ��ȡurl��ͼƬ���ļ������׺
			if (imageUrl != null && imageUrl.length() != 0) {
				fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
			}

			// ͼƬ���ֻ����صĴ��·��,ע�⣺fileNameΪ�յ����
			imagePath = imgcache + "/" + fileName;

			// Log.i("test","imagePath = " + imagePath);
			File file = new File(imgcache, fileName);// �����ļ�
			// Log.i("test","file.toString()=" + file.toString());
			if (!file.exists() && !file.isDirectory()) {
				try {
					// ����������ͨ���ļ������жϣ��Ƿ񱾵��д�ͼƬ

					FileOutputStream fos = new FileOutputStream(file);
					InputStream is = new URL(imageUrl).openStream();
					int data = is.read();
					while (data != -1) {
						fos.write(data);
						data = is.read();
						;
					}
					fos.close();
					is.close();
					// drawable = Drawable.createFromStream(
					// new URL(imageUrl).openStream(),
					// file.toString() ); // (InputStream) new
					// URL(imageUrl).getContent();
					drawable = Drawable.createFromPath(file.toString());
					// Log.i("test", "file.exists()���ļ����ڣ���������:" +
					// drawable.toString());
				} catch (IOException e) {
					Log.d("test", e.getMessage());
				}
			} else if (file.length() == 0) {
				file.delete();
				try {
					// ����������ͨ���ļ������жϣ��Ƿ񱾵��д�ͼƬ

					FileOutputStream fos = new FileOutputStream(file);
					InputStream is = new URL(imageUrl).openStream();
					int data = is.read();
					while (data != -1) {
						fos.write(data);
						data = is.read();
						;
					}
					fos.close();
					is.close();
					// drawable = Drawable.createFromStream(
					// new URL(imageUrl).openStream(),
					// file.toString() ); // (InputStream) new
					// URL(imageUrl).getContent();
					drawable = Drawable.createFromPath(file.toString());
					// Log.i("test", "file.exists()���ļ����ڣ���������:" +
					// drawable.toString());
				} catch (IOException e) {
					Log.d("test", e.getMessage());
				}
			} else {
				drawable = Drawable.createFromPath(file.toString());
				// Log.i("test", "file.exists()�ļ����ڣ����ػ�ȡ");
			}

			if (drawable == null) {
				// Log.d("test", "null drawable");
			} else {
				// Log.d("test", "not null drawable");
			}

			return drawable;
		}
	}
