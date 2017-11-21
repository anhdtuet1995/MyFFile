package work.uet.anhdt.ftpstorageofficial.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
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
import work.uet.anhdt.ftpstorageofficial.adapters.UploadAdapter;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.DBUploadFactory;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadManager;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadStatus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UploadFragment.OnUploadPoolChanged} interface
 * to handle interaction events.
 * Use the {@link UploadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UploadFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, Observer,
                                                            UploadAdapter.ManagerTaskItemListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = UploadFragment.class.getSimpleName();

    private OnUploadPoolChanged onUploadPoolChanged;
    private ArrayList<UploadMetadata> uploadMetadataArrayList;
    private UploadAdapter uploadAdapter;
    private SwipeRefreshLayout swipeRefreshFile;
    private RecyclerView recyclerViewUpload;
    private Activity mActivity;

    public UploadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UploadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UploadFragment newInstance() {
        UploadFragment fragment = new UploadFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        uploadMetadataArrayList = new ArrayList<>();
        uploadMetadataArrayList.addAll(DBUploadFactory.getInstance().getUploadList());
        uploadAdapter = new UploadAdapter(uploadMetadataArrayList, mActivity);
        uploadAdapter.setManagerTaskItemListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        initView(view);
    }

    private void initView(View view) {
        swipeRefreshFile = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshFile);
        swipeRefreshFile.setOnRefreshListener(this);

        recyclerViewUpload = (RecyclerView) view.findViewById(R.id.recyclerViewUpload);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(mActivity);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewUpload.setLayoutManager(gridLayoutManager);

        recyclerViewUpload.setAdapter(uploadAdapter);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mActivity = (Activity) context;
        }
        if (context instanceof OnUploadPoolChanged) {
            onUploadPoolChanged = (OnUploadPoolChanged) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        onUploadPoolChanged = null;
        super.onDetach();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onRefresh() {
        uploadMetadataArrayList.clear();
        uploadMetadataArrayList.addAll(DBUploadFactory.getInstance().getUploadList());
        uploadAdapter.notifyDataSetChanged();
        swipeRefreshFile.setRefreshing(false);
    }

    @Override
    public void update(Observable observable, Object o) {
        if (observable instanceof UploadManager) {
            UploadManager manager = (UploadManager) observable;
            final long id = manager.getUploadId();
            final UploadStatus status = manager.getStatus();
            Log.d(TAG, manager.getUploadCompleted() + "");

            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Update list");
                        if (uploadMetadataArrayList != null && uploadAdapter != null) {
                            uploadMetadataArrayList.clear();
                            uploadMetadataArrayList.addAll(DBUploadFactory.getInstance().getUploadList());
                            uploadAdapter.notifyDataSetChanged();
                            int pos = findPositionHaveUploadId(id);
                            if (pos > -1) {
                                uploadMetadataArrayList.get(pos).setStatus(status);
                            }

                            uploadAdapter.notifyDataSetChanged();
                        }

                    }
                });

            }
        }
    }

    private int findPositionHaveUploadId(long id) {
        for (int i = 0; i < uploadMetadataArrayList.size(); i++) {
            if (uploadMetadataArrayList.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onStartPressed(long id) {
        int pos = findPositionHaveUploadId(id);
        if (pos > -1) {
            uploadMetadataArrayList.get(pos).setStatus(UploadStatus.UPLOADING);
        }

        uploadAdapter.notifyDataSetChanged();
        onUploadPoolChanged.onStartUploadId(id);
    }

    @Override
    public void onPausePressed(long id) {
        int pos = findPositionHaveUploadId(id);
        if (pos > -1) {
            uploadMetadataArrayList.get(pos).setStatus(UploadStatus.PAUSED);
        }

        uploadAdapter.notifyDataSetChanged();
        onUploadPoolChanged.onPauseUploadId(id);
    }

    @Override
    public void onStopPressed(long id) {
        onUploadPoolChanged.onStopUploadId(id);
    }

    public interface OnUploadPoolChanged {
        public void onPauseUploadId(long id);
        public void onStartUploadId(long id);
        public void onStopUploadId(long id);
    }

}
