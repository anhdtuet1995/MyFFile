package work.uet.anhdt.ftpstorageofficial.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nbsp.materialfilepicker.MaterialFilePicker;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import work.uet.anhdt.ftpstorageofficial.R;
import work.uet.anhdt.ftpstorageofficial.adapters.FileFTPAdapter;
import work.uet.anhdt.ftpstorageofficial.models.FileInfo;
import work.uet.anhdt.ftpstorageofficial.models.GetFiles;
import work.uet.anhdt.ftpstorageofficial.services.GetFilesAPI;
import work.uet.anhdt.ftpstorageofficial.services.InitServiceRetrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FileFragment.OnFileFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FileFragment#} factory method to
 * create an instance of this fragment.
 */
public class FileFragment extends Fragment implements FileFTPAdapter.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener{
    private static final String TAG = FileFragment.class.getSimpleName();

    private OnFileFragmentInteractionListener mListener;
    private RecyclerView recyclerViewFileFragment;
    private ArrayList<FileInfo> allFiles;
    private FileFTPAdapter fileFTPAdapter;
    private Activity mActivity;
    private SwipeRefreshLayout swipeRefreshFile;
    private FloatingActionButton fabUploadFile;

    public FileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        initComponentRecycleView();
    }

    private void initComponentRecycleView() {
        allFiles = new ArrayList<>();
        getAllFiles();
        fileFTPAdapter = new FileFTPAdapter(allFiles, this, mActivity);
    }

    private void getAllFiles() {
        Log.d(TAG, "get all files from server");
        GetFilesAPI getFilesAPI = InitServiceRetrofit.getInstance().createService(GetFilesAPI.class);
        getFilesAPI.getAllFiles().enqueue(new Callback<GetFiles>() {
            @Override
            public void onResponse(Call<GetFiles> call, Response<GetFiles> response) {
                if (response.isSuccessful()) {

                    allFiles.clear();
                    GetFiles getFiles = response.body();
                    ArrayList<FileInfo> fileInfos = getFiles.getFiles();
                    allFiles.addAll(fileInfos);
                    Log.d(TAG, "get all files success: " + allFiles.size());
                }
                else {
                    Log.d(TAG, "get all files failed");
                    Toast.makeText(mActivity, R.string.error, Toast.LENGTH_LONG).show();
                    try {
                        Log.d(TAG, response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Log(t.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GetFiles> call, Throwable t) {
                Toast.makeText(mActivity, R.string.error, Toast.LENGTH_LONG).show();
                Log.d(TAG, "get all files failed");
                Log.d(TAG, t.getMessage());
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_file, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");

        initViewForFragment(view);
    }
    
    private void initViewForFragment(View view) {
        swipeRefreshFile = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshFile);
        swipeRefreshFile.setOnRefreshListener(this);

        fabUploadFile = (FloatingActionButton) view.findViewById(R.id.fabUploadFile);
        fabUploadFile.setOnClickListener(this);

        recyclerViewFileFragment = (RecyclerView) view.findViewById(R.id.recyclerViewFileFragment);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2);
        gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerViewFileFragment.setLayoutManager(gridLayoutManager);
        recyclerViewFileFragment.setAdapter(fileFTPAdapter);
        fileFTPAdapter.notifyDataSetChanged();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFileFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFileFragmentInteractionListener) {
            mListener = (OnFileFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onIconClick(View view, int position) {

    }

    //swipe refresh
    @Override
    public void onRefresh() {
        getAllFiles();
        fileFTPAdapter.notifyDataSetChanged();
        swipeRefreshFile.setRefreshing(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabUploadFile:
                //fab upload
                new MaterialFilePicker()
                        .withActivity(mActivity)
                        .withRequestCode(10)
                        .start();
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFileFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFileFragmentInteraction(Uri uri);
    }
}
