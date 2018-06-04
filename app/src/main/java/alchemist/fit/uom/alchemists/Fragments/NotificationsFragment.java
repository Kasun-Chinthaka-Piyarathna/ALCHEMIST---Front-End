package alchemist.fit.uom.alchemists.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import alchemist.fit.uom.alchemists.R;


public class NotificationsFragment extends Fragment {

	public static NotificationsFragment newInstance() {
		NotificationsFragment fragment = new NotificationsFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT >= 21) {
			Window window = getActivity().getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View fragment_notifications = inflater.inflate(R.layout.fragment_notifications, container, false);
		return fragment_notifications;
	}
}