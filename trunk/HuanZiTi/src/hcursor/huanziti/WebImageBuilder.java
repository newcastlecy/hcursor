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
		 * 通过图片url返回图片Bitmap
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
			// 取 drawable 的长宽
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();

			// 取 drawable 的颜色格式
			Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565;
			// 建立对应 bitmap
			Bitmap bitmap = Bitmap.createBitmap(w, h, config);
			// 建立对应 bitmap 的画布
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			// 把 drawable 内容画到画布中
			drawable.draw(canvas);
			return bitmap;
		}

		public static Drawable loadImageFromNetwork(String imageUrl) {
			String imgcache = "/sdcard/kdfly/huanziti/imgcache";
			// 创建文件夹。
			File mWorkingPath = new File(imgcache);
			if (!mWorkingPath.exists()) {
				if (!mWorkingPath.mkdirs()) {
					Log.i("sheepkx", "无法创建图片缓存文件夹！");
				}
			}

			Drawable drawable = null;
			if (imageUrl == null)
				return null;
			String imagePath = "";
			String fileName = "";

			// 获取url中图片的文件名与后缀
			if (imageUrl != null && imageUrl.length() != 0) {
				fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
			}

			// 图片在手机本地的存放路径,注意：fileName为空的情况
			imagePath = imgcache + "/" + fileName;

			// Log.i("test","imagePath = " + imagePath);
			File file = new File(imgcache, fileName);// 保存文件
			// Log.i("test","file.toString()=" + file.toString());
			if (!file.exists() && !file.isDirectory()) {
				try {
					// 可以在这里通过文件名来判断，是否本地有此图片

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
					// Log.i("test", "file.exists()不文件存在，网上下载:" +
					// drawable.toString());
				} catch (IOException e) {
					Log.d("test", e.getMessage());
				}
			} else if (file.length() == 0) {
				file.delete();
				try {
					// 可以在这里通过文件名来判断，是否本地有此图片

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
					// Log.i("test", "file.exists()不文件存在，网上下载:" +
					// drawable.toString());
				} catch (IOException e) {
					Log.d("test", e.getMessage());
				}
			} else {
				drawable = Drawable.createFromPath(file.toString());
				// Log.i("test", "file.exists()文件存在，本地获取");
			}

			if (drawable == null) {
				// Log.d("test", "null drawable");
			} else {
				// Log.d("test", "not null drawable");
			}

			return drawable;
		}
	}
