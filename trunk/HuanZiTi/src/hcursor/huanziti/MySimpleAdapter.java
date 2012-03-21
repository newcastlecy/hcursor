package hcursor.huanziti;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MySimpleAdapter extends SimpleAdapter
	{

		private int[] mTo;
		private String[] mFrom;
		private ViewBinder mViewBinder;

		private List<? extends Map<String, ?>> mData;

		private int mResource;
		private int mDropDownResource;
		private LayoutInflater mInflater;

		private Drawable qj_drawable;

		public MySimpleAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to)
			{
				super(context, data, resource, from, to);
				mData = data;
				mResource = mDropDownResource = resource;
				mFrom = from;
				mTo = to;
				mInflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

		/**
		 * @see android.widget.Adapter#getView(int, View, ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent)
			{
				return createViewFromResource(position, convertView, parent,
						mResource);
			}

		private View createViewFromResource(int position, View convertView,
				ViewGroup parent, int resource)
			{
				View v;
				if (convertView == null)
					{
						v = mInflater.inflate(resource, parent, false);

						final int[] to = mTo;
						final int count = to.length;
						final View[] holder = new View[count];

						for (int i = 0; i < count; i++)
							{
								holder[i] = v.findViewById(to[i]);
							}

						v.setTag(holder);
					} else
					{
						v = convertView;
					}

				bindView(position, v);

				return v;
			}

		private void bindView(int position, View view)
			{
				final Map dataSet = mData.get(position);
				if (dataSet == null)
					{
						return;
					}

				final ViewBinder binder = mViewBinder;
				final View[] holder = (View[]) view.getTag();
				final String[] from = mFrom;
				final int[] to = mTo;
				final int count = to.length;

				for (int i = 0; i < count; i++)
					{
						final View v = holder[i];
						if (v != null)
							{
								final Object data = dataSet.get(from[i]);
								String text = data == null ? "" : data
										.toString();
								if (text == null)
									{
										text = "";
									}

								boolean bound = false;
								if (binder != null)
									{
										bound = binder.setViewValue(v, data,
												text);
									}

								if (!bound)
									{
										if (v instanceof Checkable)
											{
												if (data instanceof Boolean)
													{
														((Checkable) v)
																.setChecked((Boolean) data);
													} else
													{
														throw new IllegalStateException(
																v.getClass()
																		.getName()
																		+ " should be bound to a Boolean, not a "
																		+ data.getClass());
													}
											} else if (v instanceof TextView)
											{
												// Note: keep the instanceof
												// TextView check at the bottom
												// of these
												// ifs since a lot of views are
												// TextViews (e.g. CheckBoxes).
												setViewText((TextView) v, text);
											} else if (v instanceof ImageView)
											{

												// Bitmap bitmap =
												// WebImageBuilder.returnBitMap("http://timg3.ddmapimg.com/city/images/citynew/2696c2126e903cf8d-7f23.jpg");
												// ((ImageView)
												// v).setImageBitmap(bitmap);
												// setViewImage((ImageView)
												// v,"http://timg3.ddmapimg.com/city/images/citynew/2696c2126e903cf8d-7f23.jpg");
												if (data instanceof Integer)
													{
														setViewImage(
																(ImageView) v,
																(Integer) data);
													} else
													{
														setViewImage(
																(ImageView) v,
																text);
													}
											} else
											{
												throw new IllegalStateException(
														v.getClass().getName()
																+ " is not a "
																+ " view that can be bounds by this SimpleAdapter");
											}
									}
							}
					}
			}

		/**
		 * Called by bindView() to set the image for an ImageView but only if
		 * there is no existing ViewBinder or if the existing ViewBinder cannot
		 * handle binding to an ImageView.
		 * 
		 * This method is called instead of
		 * {@link #setViewImage(ImageView, String)} if the supplied data is an
		 * int or Integer.
		 * 
		 * @param v
		 *            ImageView to receive an image
		 * @param value
		 *            the value retrieved from the data set
		 * 
		 * @see #setViewImage(ImageView, String)
		 */
		public void setViewImage(ImageView v, int value)
			{
				v.setImageResource(value);
			}

		/**
		 * Called by bindView() to set the image for an ImageView but only if
		 * there is no existing ViewBinder or if the existing ViewBinder cannot
		 * handle binding to an ImageView.
		 * 
		 * By default, the value will be treated as an image resource. If the
		 * value cannot be used as an image resource, the value is used as an
		 * image Uri.
		 * 
		 * This method is called instead of
		 * {@link #setViewImage(ImageView, int)} if the supplied data is not an
		 * int or Integer.
		 * 
		 * @param v
		 *            ImageView to receive an image
		 * @param value
		 *            the value retrieved from the data set
		 * 
		 * @see #setViewImage(ImageView, int)
		 */
		public void setViewImage(ImageView v, String value)
			{
				// Bitmap bitmap = WebImageBuilder.returnBitMap(value);
				// ((ImageView) v).setImageBitmap(bitmap);
				// Drawable drawable = WebImageBuilder.loadImageFromNetwork(value);
				// ((ImageView) v).setImageDrawable(drawable);
				// ((ImageView) v).setAlpha(80);
				new ImageDownloadTask().execute(value, v);
			}

		private class ImageDownloadTask extends AsyncTask<Object, Object, Drawable>
			{
				private ImageView imageView = null;

				@Override
				protected Drawable doInBackground(Object... params)
					{
						// TODO Auto-generated method stub
						Drawable drawable = null;
						imageView = (ImageView) params[1];
						drawable = WebImageBuilder.loadImageFromNetwork((String)params[0]);
						return drawable;
					}

				protected void onPostExecute(Drawable result)
					{
						imageView.setImageDrawable(result);
					}
			}

	}
