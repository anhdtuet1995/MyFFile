package work.uet.anhdt.ftpstorageofficial.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activeandroid.util.Log;

import java.util.List;

import work.uet.anhdt.ftpstorageofficial.R;
import work.uet.anhdt.ftpstorageofficial.tasks.download.DownloadMetadata;
import work.uet.anhdt.ftpstorageofficial.tasks.upload.UploadMetadata;

/**
 * Created by anansaj on 11/21/2017.
 */

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.ListDownloadItemViewHolder> {

    private final String TAG = DownloadAdapter.class.getSimpleName();

    private List<DownloadMetadata> downloadMetadatas;
    private Context context;

    private ManagerTaskItemListener managerTaskItemListener;

    public DownloadAdapter(List<DownloadMetadata> downloadMetadatas, Context context) {
        this.context = context;
        this.downloadMetadatas = downloadMetadatas;
    }

    public void setManagerTaskItemListener(ManagerTaskItemListener managerTaskItemListener) {
        this.managerTaskItemListener = managerTaskItemListener;
    }

    @Override
    public ListDownloadItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemview = inflater.inflate(R.layout.file_download_item, parent, false);
        return new ListDownloadItemViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(final ListDownloadItemViewHolder holder, int position) {
        final DownloadMetadata singleItem = downloadMetadatas.get(position);
        holder.title.setText(singleItem.getFileName());
        Log.d(TAG, "file pos = " + position + ": " + singleItem.getFileName());
        holder.status.setText(singleItem.getStatus().name());
        Log.d(TAG, "file pos = " + position + ": " + singleItem.getStatus().name());

        switch (singleItem.getStatus()) {
            case ERROR:
                holder.imv_pause_download.setVisibility(View.GONE);
                holder.imv_play_download.setVisibility(View.GONE);
                holder.imv_stop_download.setVisibility(View.GONE);
                break;
            case COMPLETED:
                holder.imv_pause_download.setVisibility(View.GONE);
                holder.imv_play_download.setVisibility(View.GONE);
                holder.imv_stop_download.setVisibility(View.GONE);
                break;
            case PAUSED:
                holder.imv_pause_download.setVisibility(View.GONE);
                holder.imv_play_download.setVisibility(View.VISIBLE);
                holder.imv_stop_download.setVisibility(View.VISIBLE);
                break;
            case DOWNLOADING:
                holder.imv_pause_download.setVisibility(View.VISIBLE);
                holder.imv_play_download.setVisibility(View.GONE);
                holder.imv_stop_download.setVisibility(View.VISIBLE);
                break;
            case READY:
                holder.imv_pause_download.setVisibility(View.VISIBLE);
                holder.imv_play_download.setVisibility(View.GONE);
                holder.imv_stop_download.setVisibility(View.VISIBLE);
                break;
            case NEW:
                holder.imv_pause_download.setVisibility(View.GONE);
                holder.imv_play_download.setVisibility(View.GONE);
                holder.imv_stop_download.setVisibility(View.GONE);
                break;
        }
        setIcon(singleItem, holder);

        holder.imv_play_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imv_pause_download.setVisibility(View.VISIBLE);
                holder.imv_play_download.setVisibility(View.GONE);
                holder.imv_stop_download.setVisibility(View.VISIBLE);
                holder.status.setText("PAUSED");
                managerTaskItemListener.onStartDownloadPressed(singleItem.getId());
            }
        });

        holder.imv_pause_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.imv_pause_download.setVisibility(View.GONE);
                holder.imv_play_download.setVisibility(View.VISIBLE);
                holder.imv_stop_download.setVisibility(View.VISIBLE);
                managerTaskItemListener.onPauseDownloadPressed(singleItem.getId());
            }
        });

        holder.imv_stop_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.status.setText("STOPPED");
                holder.imv_pause_download.setVisibility(View.GONE);
                holder.imv_play_download.setVisibility(View.GONE);
                holder.imv_stop_download.setVisibility(View.GONE);
                managerTaskItemListener.onStopDownloadPressed(singleItem.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return downloadMetadatas.size();
    }

    public void setIcon(DownloadMetadata file, DownloadAdapter.ListDownloadItemViewHolder holder) {

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
            drawable = ContextCompat.getDrawable(context, R.drawable.ic_file_error);
        }

        drawable = DrawableCompat.wrap(drawable);
        holder.icon.setImageDrawable(drawable);

    }

    static class ListDownloadItemViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView status;
        ImageView icon;
        LinearLayout linearLayout;
        ImageView imv_play_download;
        ImageView imv_stop_download;
        ImageView imv_pause_download;

        public ListDownloadItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_download);
            title = (TextView) itemView.findViewById(R.id.tv_download_title);
            icon = (ImageView) itemView.findViewById(R.id.icon_download);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            status = (TextView) itemView.findViewById(R.id.tv_download_status);

            imv_play_download = (ImageView) itemView.findViewById(R.id.imv_play_download);
            imv_stop_download = (ImageView) itemView.findViewById(R.id.imv_stop_download);
            imv_pause_download = (ImageView) itemView.findViewById(R.id.imv_pause_download);
        }
    }

    public interface ManagerTaskItemListener {
        void onStartDownloadPressed(long id);
        void onPauseDownloadPressed(long id);
        void onStopDownloadPressed(long id);
    }

}
