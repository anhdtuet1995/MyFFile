package work.uet.anhdt.ftpstorageofficial.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import work.uet.anhdt.ftpstorageofficial.R;
import work.uet.anhdt.ftpstorageofficial.activities.MainActivity;
import work.uet.anhdt.ftpstorageofficial.adapters.DownloadAdapter;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DBDownloadFactory;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadManager;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadStatus;
import work.uet.anhdt.ftpstorageofficial.util.Constant;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DownloadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DownloadFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, Observer,
        DownloadAdapter.ManagerTaskItemListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = DownloadFragment.class.getSimpleName();

    private OnDownloadPoolChanged onDownloadPoolChanged;
    private ArrayList<DownloadMetadata> downloadMetadataArrayList;
    private DownloadAdapter downloadAdapter;
    private SwipeRefreshLayout swipeRefreshDownload;
    private RecyclerView recyclerViewDownload;
    private Activity mActivity;

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Update from broadcast");
            if (intent.getAction().equals(Constant.BROADCAST_UPDATE_STATUS_DOWNLOAD)) {
                long download_id = intent.getLongExtra("download_id", -1);
                DownloadStatus download_status = DownloadStatus.values()[intent.getIntExtra("status", -1)];
                int pos = findPositionHaveDownloadId(download_id);
                if (pos > -1) {
                    if ((download_status == DownloadStatus.DOWNLOADING && downloadMetadataArrayList.get(pos).getStatus() != DownloadStatus.DOWNLOADING)
                            || download_status != DownloadStatus.DOWNLOADING)  {
                        downloadMetadataArrayList.get(pos).setStatus(download_status);
                        downloadAdapter.notifyDataSetChanged();
                    }

                }
                else {
                    if (downloadMetadataArrayList != null && downloadAdapter != null) {
                        downloadMetadataArrayList.clear();
                        downloadMetadataArrayList.addAll(DBDownloadFactory.getInstance().getDownloadList());
                        downloadAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    public DownloadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DownloadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        downloadMetadataArrayList = new ArrayList<>();
        downloadMetadataArrayList.addAll(DBDownloadFactory.getInstance().getDownloadList());
        downloadAdapter = new DownloadAdapter(downloadMetadataArrayList, mActivity);
        downloadAdapter.setManagerTaskItemListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_download, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        initView(view);
    }

    private void initView(View view) {
        swipeRefreshDownload = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshDownload);
        swipeRefreshDownload.setOnRefreshListener(this);

        recyclerViewDownload = (RecyclerView) view.findViewById(R.id.recyclerViewDownload);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerViewDownload.setLayoutManager(linearLayoutManager);

        recyclerViewDownload.setAdapter(downloadAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (Activity) context;
        }
        if (context instanceof OnDownloadPoolChanged) {
            onDownloadPoolChanged = (OnDownloadPoolChanged) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        onDownloadPoolChanged = null;
        super.onDetach();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        downloadMetadataArrayList.clear();
        downloadMetadataArrayList.addAll(DBDownloadFactory.getInstance().getDownloadList());
        downloadAdapter.notifyDataSetChanged();
        swipeRefreshDownload.setRefreshing(false);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof DownloadManager) {
        }
    }

    private int findPositionHaveDownloadId(long id) {
        for (int i = 0; i < downloadMetadataArrayList.size(); i++) {
            if (downloadMetadataArrayList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        mActivity.registerReceiver(mNotificationReceiver, new IntentFilter(Constant.BROADCAST_DOWNLOAD));
        mActivity.registerReceiver(mNotificationReceiver, new IntentFilter(Constant.BROADCAST_UPDATE_STATUS_DOWNLOAD));
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        mActivity.unregisterReceiver(mNotificationReceiver);
    }

    @Override
    public void onStartDownloadPressed(long id) {
        int pos = findPositionHaveDownloadId(id);
        if (pos > -1) {
            downloadMetadataArrayList.get(pos).setStatus(DownloadStatus.DOWNLOADING);
        }

        downloadAdapter.notifyDataSetChanged();
        onDownloadPoolChanged.onStartDownloadId(id);
    }

    @Override
    public void onPauseDownloadPressed(long id) {
        int pos = findPositionHaveDownloadId(id);
        if (pos > -1) {
            downloadMetadataArrayList.get(pos).setStatus(DownloadStatus.PAUSED);
        }

        downloadAdapter.notifyDataSetChanged();
        onDownloadPoolChanged.onPauseDownloadId(id);
    }

    @Override
    public void onStopDownloadPressed(long id) {
        onDownloadPoolChanged.onStopDownloadId(id);
    }

    public interface OnDownloadPoolChanged {
        public void onPauseDownloadId(long id);

        public void onStartDownloadId(long id);

        public void onStopDownloadId(long id);
    }
}
