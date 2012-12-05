package cn.mimail.sdk.util;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import cn.mimail.sdk.exception.PermissionException;

public class Utils {

	private static final String TAG = "Utils.java";

	/**
	 * 私有构造函数,禁止创建实例.
	 */
	private Utils() {
	}

	public static boolean hasFroyo() {
		// Can use static final constants like FROYO, declared in later versions
		// of the OS since they are inlined at compile time. This is guaranteed
		// behavior.
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
	}

	public static boolean hasGingerbread() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
	}

	public static boolean hasHoneycomb() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
	}

	public static boolean hasHoneycombMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
	}

	public static boolean hasJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context The context to use
	 * @param uniqueName A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() : context.getCacheDir()
				.getPath();
		final File dir = new File(cachePath + File.separator + uniqueName);
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				dir.delete();
			} else {
				return dir;
			}
		} else {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context The context to use
	 * @param uniqueName A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() : context.getCacheDir()
				.getPath();

		return new File(cachePath);
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context The context to use
	 * @param uniqueName A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskFilesDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !isExternalStorageRemovable() ? getExternalFilesDir(context).getPath() : context.getFilesDir()
				.getPath();

		final File dir = new File(cachePath + File.separator + uniqueName);
		if (dir.exists()) {
			if (!dir.isDirectory()) {
				dir.delete();
			} else {
				return dir;
			}
		} else {
			dir.mkdirs();
		}
		return dir;
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context The context to use
	 * @param uniqueName A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskFilesDir(Context context) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
				|| !isExternalStorageRemovable() ? getExternalFilesDir(context).getPath() : context.getFilesDir()
				.getPath();
		return new File(cachePath);
	}

	/**
	 * A hashing method that changes a string (like a URL) into a hash suitable for using as a disk filename.
	 */
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	public static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * Get the size in bytes of a bitmap.
	 * 
	 * @param bitmap
	 * @return size in bytes
	 */
	@TargetApi(12)
	public static int getBitmapSize(Bitmap bitmap) {
		if (Utils.hasHoneycombMR1()) {
			return bitmap.getByteCount();
		}
		// Pre HC-MR1
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * Check if external storage is built-in or removable.
	 * 
	 * @return True if external storage is removable (like an SD card), false otherwise.
	 */
	@TargetApi(9)
	public static boolean isExternalStorageRemovable() {
		if (Utils.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	private static File getExternalCacheDir(Context context) {
		if (Utils.hasFroyo()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	private static File getExternalFilesDir(Context context) {
		if (Utils.hasFroyo()) {
			return context.getExternalFilesDir(null);
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/files/";
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}

	/**
	 * Check how much usable space is available at a given path.
	 * 
	 * @param path The path to check
	 * @return The space available in bytes
	 */
	@TargetApi(9)
	public static long getUsableSpace(File path) {
		if (Utils.hasGingerbread()) {
			return path.getUsableSpace();
		}
		final StatFs stats = new StatFs(path.getPath());
		return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
	}

	/**
	 * 判断content给定的程序是否处于前台<br />
	 * 注意:运行此方法需要<br />
	 * &lt;uses-permission android:name="android.permission.GET_TASKS" /&gt;权限
	 * 
	 * @param context
	 * @return true:程序处于前台运行,false:Activity未处于前台状态
	 */
	public static boolean isApplicationForeground(final Context context) throws PermissionException {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (!tasks.isEmpty()) {
				ComponentName topActivity = tasks.get(0).topActivity;
				if (topActivity.getPackageName().equals(context.getPackageName())) {
					return true;
				}
			}
		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage() + " Try to complete without permission");
			try {
				return isApplicationForegroundWithoutPermissions(context);
			} catch (UnsupportedOperationException e2) {
				throw new PermissionException(e);
			}
		}
		return false;
	}

	/**
	 * 判断cls给定Activity的是否处于前台<br />
	 * 注意:运行此方法需要<br />
	 * &lt;uses-permission android:name="android.permission.GET_TASKS" /&gt;权限
	 * 
	 * @param context
	 * @return true:Activity处于前台运行,false:Activity未处于前台状态
	 */
	public static boolean isActivityForeground(final Context context, Class<?> cls) throws PermissionException{
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasks = am.getRunningTasks(1);
			if (!tasks.isEmpty()) {
				ComponentName topActivity = tasks.get(0).topActivity;
				if (topActivity.getClassName().equals(cls.getName())) {
					return true;
				}
			}
		} catch (SecurityException e) {
			Log.e(TAG, e.getMessage() + " Try to complete without permission");
			throw new PermissionException(e);
		}
		return false;
	}

	/**
	 * 判断content给定的程序是否处于前台<br />
	 * 注意:运行此方法不需要权限，但是比{@code isApplicationForeground}耗费更多时间
	 * 
	 * @return true 在前台; false 在后台
	 */
	public static boolean isApplicationForegroundWithoutPermissions(final Context context) throws UnsupportedOperationException {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		boolean isspecial = true;
		String packageName = context.getPackageName();
		List<RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName.equals(packageName)) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
						|| appProcess.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					return true;
				}
				if (km.inKeyguardRestrictedInputMode())
					return true;
			}
			if (isspecial) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					isspecial = false;
				}
			}
		}
		if (isspecial) {
			Log.e(TAG,
					"Utils.isApplicationForegroundWithoutPermissions():The system is unable to obtain accurate information and return false");
			throw new UnsupportedOperationException("The system is unable to obtain accurate information");
		}
		return false;
	}

	/**
	 * Workaround for bug pre-Froyo, see here for more info: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 */
	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (hasHttpConnectionBug()) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	/**
	 * Check if OS version has a http URLConnection bug. See here for more information:
	 * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
	 * 
	 * @return
	 */
	public static boolean hasHttpConnectionBug() {
		return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
	}
}
