package cn.mimessage.and.sdk.net;

import java.util.Observable;

import cn.mimessage.and.sdk.profile.Config;
import cn.mimessage.and.sdk.sdcard.IPersistentStore;
import cn.mimessage.and.sdk.util.log.LogAdapter;

public abstract class BaseConnectionFactory implements IConnectionFactory {
	private boolean hasPreviousNetworkSuccessBeenRead;
	private final String netAvailablePrefName;
	private boolean networkWorked;
	private boolean networkWorkedThisSession;
	protected IPersistentStore store;
	private Config mConfig;

	protected BaseConnectionFactory(Config config, String prefName) {
		netAvailablePrefName = prefName;
		mConfig = config;
	}

	private void setNetworkWorked(boolean isWorked) {
		hasPreviousNetworkSuccessBeenRead = true;
		store = mConfig.getPersistentStore();
		networkWorked = isWorked;
	}

	protected void checkPreviousNetworkSuccess() {
		if (store.readPreference(netAvailablePrefName) != null) {
			setNetworkWorked(true);
		} else {
			setNetworkWorked(false);
		}
	}

	protected byte getNetworkPreferenceValue() {
		return 0;
	}

	@Override
	public boolean getNetworkWorked() {
		if (!hasPreviousNetworkSuccessBeenRead) {
			checkPreviousNetworkSuccess();
		}
		return networkWorked;
	}

	@Override
	public boolean getNetworkWorkedThisSession() {
		return networkWorkedThisSession;
	}

	@Override
	public void notifyFailure() {
	}

	@Override
	public synchronized boolean registerNetworkSuccess(boolean isSucess) {
		try {
			networkWorkedThisSession = true;
			if (!getNetworkWorked() || isSucess) {
				networkWorked = true;
				store.setPreference(netAvailablePrefName, new byte[] { getNetworkPreferenceValue() });
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void resetNetworkSettings() {
		final IPersistentStore localPersistentStore = store;
		localPersistentStore.setPreference(netAvailablePrefName, null);
		store.savePreferences();
		setNetworkWorked(false);
		networkWorkedThisSession = false;
	}

	@Override
	public void setLogAdapter(LogAdapter logAdapter) {
	}

	@Override
	public void setSettingObservable(Observable ob) {
	}
}
