package alchemist.fit.uom.alchemists.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import alchemist.fit.uom.alchemists.adapters.CustomListAdapter;
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
        ListView list;
        final String[] web = {
                "Flinker Matee added a video",
                "Robert peo added a video",
                "Ramith Singh added a video",
                "Kasun Chinthaka added a video",
                "Manee Chopra added a video",
                "John kehn added a video",
                "Maree Nipon added a video"
        };
        final String[] time = {
                "1 hour ago",
                "2 hour ago",
                "3 days ago",
                "4 days ago",
                "1 week ago",
                "1 week ago",
                "2 week ago"
        };
        Integer[] imageId = {
                R.drawable.test_image_new,
                R.drawable.s1,
                R.drawable.s2,
                R.drawable.s5,
                R.drawable.s3,
                R.drawable.s4,
                R.drawable.s6
        };
        CustomListAdapter adapter = new CustomListAdapter(getActivity(), time, web, imageId);
        list = fragment_notifications.findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getActivity(), "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
            }
        });
        return fragment_notifications;
    }
}