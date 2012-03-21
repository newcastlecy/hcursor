package hcursor.huanziti;

import hcursor.widget.PagerHeader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.admogo.AdMogoLayout;
import com.admogo.AdMogoListener;
import com.admogo.AdMogoManager;
import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;
import com.mobclick.android.MobclickAgent;
import com.tencent.mobwin.AdListener;
import com.tencent.mobwin.AdView;

public class ActivityMain extends FragmentActivity implements AdMogoListener
	{
		/** Called when the activity is first created. */
		public boolean mDualPane = false;
		private boolean mLoggingEnabled = true;

		private ViewPager mPager;
		private TransitionDrawable mTitleLogo;

		private ImageView btn_clearad;
		private RelativeLayout layout_ad;

		private PagerAdapter pagerAdapter;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			if (true == checksign()) {
				// Toast.makeText(getApplicationContext(), "签名一致。",
				// Toast.LENGTH_LONG).show();
			} else {
				int pid = android.os.Process.myPid();
				android.os.Process.killProcess(pid); // 杀死当前进程
				// Toast.makeText(getApplicationContext(), "签名不一致。",
				// Toast.LENGTH_LONG).show();
			}

			MobclickAgent.update(this);
			UMFeedbackService.enableNewReplyNotification(this,
					NotificationType.AlertDialog);

			setContentView(R.layout.activity_home);
			btn_clearad = (ImageView) findViewById(R.id.btn_clearad);
			layout_ad = (RelativeLayout) findViewById(R.id.layout_ad);
			AdMogoLayout adView = (AdMogoLayout) findViewById(R.id.admogo_layout);
			adView.setAdMogoListener(this);

			if (findViewById(R.id.fragment_container) != null) {
				mDualPane = true;
				if (savedInstanceState == null) {
					if (mLoggingEnabled) {
						// showLog();
					} else {
						// Fragment detailsFragment =
						// Fragment.instantiate(this,
						// AppDetailsFragment.class.getName());
						// FragmentTransaction transaction =
						// getSupportFragmentManager().beginTransaction();
						// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
						// transaction.replace(R.id.fragment_container,
						// detailsFragment);
						// transaction.commit();
					}
				}
			} else {
				mPager = (ViewPager) findViewById(R.id.pager);
				PagerHeader pagerHeader = (PagerHeader) findViewById(R.id.pager_header);
				pagerAdapter = new PagerAdapter(this, mPager, pagerHeader);

				pagerAdapter.addPage(MondayFragment.class, "本地字库");
				pagerAdapter.addPage(TuesdayFragment.class, "网络字库");

				try {
					CreateText();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// mPager.setCurrentItem(1);
				// if (mLoggingEnabled) {
				// pagerAdapter.addPage(TuesdayFragment.class, "星期二");
				// } else {
				// pagerHeader.setVisibility(View.GONE);
				// }

				// DEBUG
				// pagerAdapter.addPage(AppListFragment.class, null,
				// "APPS");
				// pagerAdapter.addPage(LogFragment.class, null,
				// "LOGS");
				// pagerAdapter.addPage(AppListFragment.class, null,
				// "more apps");
				// pagerAdapter.addPage(LogFragment.class, null,
				// "more logs");
				// END DEBUG
			}
			btn_clearad.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					layout_ad.setVisibility(8);
				}
			});
			btn_clearad.setClickable(false);
		}

		// public void showLog() {
		// if (mDualPane) {
		// Fragment logFragment = Fragment.instantiate(this,
		// LogFragment.class.getName());
		// FragmentTransaction transaction =
		// getSupportFragmentManager().beginTransaction();
		// transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		// transaction.replace(R.id.fragment_container, logFragment);
		// transaction.commit();
		// }
		// }
		public Boolean checksign() {
			android.content.pm.Signature[] sigs;
			try {
				sigs = getBaseContext().getPackageManager().getPackageInfo(
						"hcursor.huanziti", 64).signatures;
				// Log.d("ANDROID_LAB", "sigs.len=" + sigs.length);
				// Log.d("ANDROID_LAB",sigs[0].toCharsString());
				String sigs1 = toMd5((sigs[0].toCharsString() + " ").getBytes());
				if (sigs1.equals("c965d753d0d5862ccdd8ece955853629")) {
					return true;
				} else {
					return false;
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		private static String toMd5(byte[] bytes) {
			try {
				MessageDigest algorithm = MessageDigest.getInstance("MD5");
				algorithm.reset();
				algorithm.update(bytes);
				return toHexString(algorithm.digest(), "");
			} catch (NoSuchAlgorithmException e) {
				// Log.i("sheepkx", "toMd5(): " + e);
				throw new RuntimeException(e);
				// 05-20 09:42:13.697: ERROR/hjhjh(256):
				// 5d5c87e61211ab7a4847f7408f48ac
			}
		}

		private static String toHexString(byte[] bytes, String separator) {
			StringBuilder hexString = new StringBuilder();
			for (byte b : bytes) {
				hexString.append(Integer.toHexString(0xFF & b)).append(
						separator);
			}
			return hexString.toString();
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// TODO Auto-generated method stub
			menu.add(0, 1, 1, "恢复系统默认字体");
			menu.add(0, 2, 2, "反馈");
			menu.add(0, 3, 2, "关于");
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			if (item.getItemId() == 1) {
				File file = new File(
						"/sdcard/kdfly/huanziti/DroidSansFallback.ttf");
				if (file.exists()) {
					runRootCommand("mount -o remount,rw rootfs /system");
					runRootCommand("cp -f /sdcard/kdfly/huanziti/DroidSansFallback.ttf"
							+ " /system/fonts/DroidSansFallback.ttf");
					runRootCommand("chmod 644  /system/fonts/DroidSansFallback.ttf");
					runRootCommand("mount -o remount,ro rootfs /system");

					Toast.makeText(getApplicationContext(), "恢复默认字体成功！感谢您的支持！",
							Toast.LENGTH_LONG).show();
					new AlertDialog.Builder(ActivityMain.this)
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
				} else {
					Toast.makeText(getApplicationContext(),
							"找不到备份字体，如果碰到无法显示的字符，请更换雅黑字体试试，感谢您的支持！",
							Toast.LENGTH_LONG).show();
				}
			} else if (item.getItemId() == 2) {
				UMFeedbackService.openUmengFeedbackSDK(this);
			} else if (item.getItemId() == 3) {
				// 关于按钮
				String verName = "";
				try {
					verName = this.getPackageManager().getPackageInfo(
							"hcursor.huanziti", 0).versionName;
				} catch (NameNotFoundException e) {
					Log.e("log", e.getMessage());
				}

				new AlertDialog.Builder(this)
						.setTitle("关于")
						.setMessage(
								"版本:"
										+ verName
										+ "\n"
										+ "作者QQ:110964755\n作者邮箱:hcursor@gmail.com\n\n如果您有任何问题及bug反馈请联系我们，感谢您对我们的支持！")
						.setPositiveButton("确定", null).show();
			}
			return super.onOptionsItemSelected(item);
		}

		public static class PagerAdapter extends FragmentPagerAdapter implements
				ViewPager.OnPageChangeListener,
				PagerHeader.OnHeaderClickListener
			{

				private final Context mContext;
				private final ViewPager mPager;
				private final PagerHeader mHeader;
				private final ArrayList<PageInfo> mPages = new ArrayList<PageInfo>();

				static final class PageInfo
					{
						private final Class<?> clss;
						private final Bundle args;

						PageInfo(Class<?> _clss, Bundle _args) {
							clss = _clss;
							args = _args;
						}
					}

				public PagerAdapter(FragmentActivity activity, ViewPager pager,
						PagerHeader header) {
					super(activity.getSupportFragmentManager());
					mContext = activity;
					mPager = pager;
					mHeader = header;
					mHeader.setOnHeaderClickListener(this);
					mPager.setAdapter(this);
					mPager.setOnPageChangeListener(this);
				}

				public void addPage(Class<?> clss, int res) {
					addPage(clss, null, res);
				}

				public void addPage(Class<?> clss, String title) {
					addPage(clss, null, title);
				}

				public void addPage(Class<?> clss, Bundle args, int res) {
					addPage(clss, null, mContext.getResources().getString(res));
				}

				public void addPage(Class<?> clss, Bundle args, String title) {
					PageInfo info = new PageInfo(clss, args);
					mPages.add(info);
					mHeader.add(0, title);
					notifyDataSetChanged();
				}

				@Override
				public int getCount() {
					return mPages.size();
				}

				@Override
				public Fragment getItem(int position) {
					PageInfo info = mPages.get(position);
					return Fragment.instantiate(mContext, info.clss.getName(),
							info.args);
				}

				@Override
				public void onPageScrolled(int position, float positionOffset,
						int positionOffsetPixels) {
					mHeader.setPosition(position, positionOffset,
							positionOffsetPixels);
				}

				@Override
				public void onPageSelected(int position) {
					mHeader.setDisplayedPage(position);
				}

				@Override
				public void onPageScrollStateChanged(int state) {
				}

				@Override
				public void onHeaderClicked(int position) {

				}

				@Override
				public void onHeaderSelected(int position) {
					mPager.setCurrentItem(position);
				}
			}

		// 创建文件夹及文件
		public void CreateText() throws IOException {
			File file = new File("/sdcard/kdfly/huanziti");
			// File filefont = new File("/sdcard/kdfly/huanziti/font");
			// filefont.mkdirs();
			File file1 = new File(
					"/sdcard/kdfly/huanziti/DroidSansFallback.ttf");
			if (!file.exists()) {
				try {
					// 按照指定的路径创建文件夹
					file.mkdirs();
					runRootCommand("cat /system/fonts/DroidSansFallback.ttf>/sdcard/kdfly/huanziti/DroidSansFallback.ttf");
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (!file1.exists()) {
				runRootCommand("cat /system/fonts/DroidSansFallback.ttf>/sdcard/kdfly/huanziti/DroidSansFallback.ttf");
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

		public void onResume() {
			super.onResume();
			MobclickAgent.onResume(this);
		}

		public void onPause() {
			super.onPause();
			MobclickAgent.onPause(this);
		}

		@Override
		protected void onDestroy() {
			AdMogoManager.clear();
			super.onDestroy();
		}

		@Override
		public void onClickAd() {
			// TODO Auto-generated method stub
			btn_clearad.setClickable(true);
		}

		@Override
		public void onCloseAd() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCloseMogoDialog() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onFailedReceiveAd() {
			// TODO Auto-generated method stub

		}

		@Override
		public void onReceiveAd() {
			// TODO Auto-generated method stub

		}
	}