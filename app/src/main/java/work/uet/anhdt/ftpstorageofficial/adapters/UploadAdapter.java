package work.uet.anhdt.ftpstorageofficial.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import work.uet.anhdt.ftpstorageofficial.R;
import work.uet.anhdt.ftpstorageofficial.models.FileInfo;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadStatus;

/**
 * Created by anansaj on 11/20/2017.
 */

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ListUploadItemViewHolder> {

    private final String TAG = UploadAdapter.class.getSimpleName();

    private List<UploadMetadata> uploadMetadatas;
    private Context context;

    private ManagerTaskItemListener managerTaskItemListener;

    public UploadAdapter(List<UploadMetadata> uploadMetadata, Context context) {
        this.context = context;
        this.uploadMetadatas = uploadMetadata;
    }

    public void setManagerTaskItemListener(ManagerTaskItemListener managerTaskItemListener) {
        this.managerTaskItemListener = managerTaskItemListener;
    }

    @Override
    public ListUploadItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemview = inflater.inflate(R.layout.file_upload_item, parent, false);
        return new ListUploadItemViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final ListUploadItemViewHolder holder, int position) {
        final UploadMetadata singleItem = uploadMetadatas.get(position);
        holder.title.setText(singleItem.getFileName());
        Log.d(TAG, "file pos = " + position + ": " + singleItem.getFileName());
        holder.status.setText(singleItem.getStatus().name());
        Log.d(TAG, "file pos = " + position + ": " + singleItem.getStatus().name());

        switch (singleItem.getStatus()) {
            case ERROR:
                holder.imv_pause_upload.setVisibility(View.GONE);
                holder.imv_play_upload.setVisibility(View.GONE);
                holder.imv_stop_upload.setVisibility(View.GONE);
                break;
            case COMPLETED:
                holder.imv_pause_upload.setVisibility(View.GONE);
                holder.imv_play_upload.setVisibility(View.GONE);
                holder.imv_stop_upload.setVisibility(View.GONE);
                break;
            case PAUSED:
                holder.imv_pause_upload.setVisibility(View.GONE);
                holder.imv_play_upload.setVisibility(View.VISIBLE);
                holder.imv_stop_upload.setVisibility(View.VISIBLE);
                break;
            case UPLOADING:
                holder.imv_pause_upload.setVisibility(View.VISIBLE);
                holder.imv_play_upload.setVisibility(View.GONE);
                holder.imv_stop_upload.setVisibility(View.VISIBLE);
                break;
            case READY:
                holder.imv_pause_upload.setVisibility(View.VISIBLE);
                holder.imv_play_upload.setVisibility(View.GONE);
                holder.imv_stop_upload.setVisibility(View.VISIBLE);
                break;
            case NEW:
                holder.imv_pause_upload.setVisibility(View.GONE);
                holder.imv_play_upload.setVisibility(View.GONE);
                holder.imv_stop_upload.setVisibility(View.GONE);
                break;
        }
        setIcon(singleItem, holder);

        holder.imv_play_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imv_pause_upload.setVisibility(View.VISIBLE);
                holder.imv_play_upload.setVisibility(View.GONE);
                holder.imv_stop_upload.setVisibility(View.VISIBLE);
                holder.status.setText("PAUSED");
                managerTaskItemListener.onStartPressed(singleItem.getId());
            }
        });

        holder.imv_pause_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imv_pause_upload.setVisibility(View.GONE);
                holder.imv_play_upload.setVisibility(View.VISIBLE);
                holder.imv_stop_upload.setVisibility(View.VISIBLE);
                managerTaskItemListener.onPausePressed(singleItem.getId());
            }
        });

        holder.imv_stop_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.status.setText("STOPPED");
                holder.imv_pause_upload.setVisibility(View.GONE);
                holder.imv_play_upload.setVisibility(View.GONE);
                holder.imv_stop_upload.setVisibility(View.GONE);
                managerTaskItemListener.onStopPressed(singleItem.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return uploadMetadatas.size();
    }

    public void setIcon(UploadMetadata file, UploadAdapter.ListUploadItemViewHolder holder) {

        String extension = "";
        Drawable drawable = null;

        try {
            String filepath = file.getFilePath();
            Log.d(TAG, "file : " + file.getFilePath());
            extension = filepath.substring(filepath.lastIndexOf("."));
            Log.d(TAG, "file : " + extension);
            switch (extension) {

                case ".c":
                case ".cpp":
                case ".doc":
                case ".docx":
                case ".exe":
                case ".h":
                case ".html":
                case ".java":
                case ".log":
                case ".txt":
                case ".pdf":
                case ".ppt":
                case ".xls":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_file_undefined);
                    break;

                case ".3ga":
                case ".aac":
                case ".mp3":
                case ".m4a":
                case ".ogg":
                case ".wav":
                case ".wma":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_file_audio);
                    break;

                case ".3gp":
                case ".avi":
                case ".mpg":
                case ".mpeg":
                case ".mp4":
                case ".mkv":
                case ".webm":
                case ".wmv":
                case ".vob":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_file_video);
                    break;

                case ".ai":
                case ".bmp":
                case ".exif":
                case ".gif":
                case ".jpg":
                case ".jpeg":
                case ".png":
                case ".svg":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_file_image);
                    break;

                case ".rar":
                case ".zip":
                case ".ZIP":
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_file_compressed);
                    break;

                default:
                    drawable = ContextCompat.getDrawable(context, R.drawable.ic_file_error);
                    break;
            }

        }   catch (Exception e) {
            drawable = ContextCompat.getDrawable(context,R.drawable.ic_file_error);
        }

        drawable = DrawableCompat.wrap(drawable);
        holder.icon.setImageDrawable(drawable);

    }

    static class ListUploadItemViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView status;
        ImageView icon;
        LinearLayout linearLayout;
        ImageView imv_play_upload;
        ImageView imv_stop_upload;
        ImageView imv_pause_upload;

        public ListUploadItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_upload);
            title = (TextView) itemView.findViewById(R.id.tv_upload_title);
            icon = (ImageView) itemView.findViewById(R.id.icon_upload);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            status = (TextView) itemView.findViewById(R.id.tv_upload_status);

            imv_play_upload = (ImageView) itemView.findViewById(R.id.imv_play_upload);
            imv_stop_upload = (ImageView) itemView.findViewById(R.id.imv_stop_upload);
            imv_pause_upload = (ImageView) itemView.findViewById(R.id.imv_pause_upload);
        }
    }

    public interface ManagerTaskItemListener {
        void onStartPressed(long id);
        void onPausePressed(long id);
        void onStopPressed(long id);
    }
}
