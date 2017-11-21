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

import java.util.ArrayList;

import work.uet.anhdt.ftpstorageofficial.R;
import work.uet.anhdt.ftpstorageofficial.activities.MainActivity;
import work.uet.anhdt.ftpstorageofficial.models.FileInfo;

/**
 * Created by anansaj on 11/18/2017.
 */

public class FileFTPAdapter extends RecyclerView.Adapter<FileFTPAdapter.ListItemViewHolder> {
    private static final String TAG = FileFTPAdapter.class.getSimpleName();

    public interface OnItemClickListener  {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view,int position);
        public void onIconClick(View view,int position);
    }

    private ArrayList<FileInfo> allFiles;
    private OnItemClickListener onItemClickListener;
    private Context context;

    public FileFTPAdapter(ArrayList<FileInfo> allFiles, Context context) {
        Log.d(TAG, "init");
        this.allFiles = allFiles;
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    public void setListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setAllFiles(ArrayList<FileInfo> allFiles) {
        this.allFiles.clear();
        this.allFiles.addAll(allFiles);
        this.notifyDataSetChanged();
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.file_item, parent, false);
        return new ListItemViewHolder(view);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public void onBindViewHolder(final ListItemViewHolder holder, final int position) {

        final FileInfo singleItem = allFiles.get(position);

        holder.title.setText(singleItem.getFileName());
        Log.d(TAG, "file pos = " + position + ": " + singleItem.getFileName());
        holder.lastModified.setText(singleItem.getCreatedAt());
        Log.d(TAG, "file pos = " + position + ": " + singleItem.getCreatedAt());
        setIcon(singleItem, holder);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onItemClickListener.onItemLongClick(v, position);
                return true;
            }
        });

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onIconClick(holder.cardView, position);
            }
        });

    }

    public void setIcon(FileInfo file, ListItemViewHolder holder) {

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



    @Override
    public int getItemCount() {
        return allFiles.size();
    }


    static class ListItemViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        TextView lastModified;
        ImageView icon;
        LinearLayout linearLayout;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardView);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            lastModified = (TextView) itemView.findViewById(R.id.lastModified);
        }
    }

}
