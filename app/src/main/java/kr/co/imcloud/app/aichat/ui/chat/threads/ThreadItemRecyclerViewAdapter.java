package kr.co.imcloud.app.aichat.ui.chat.threads;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import kr.co.imcloud.app.aichat.R;
import kr.co.imcloud.app.aichat.models.ThreadItem;
import kr.co.imcloud.app.aichat.stores.ChatStore;
import kr.co.imcloud.app.aichat.ui.chat.ChatActivity;
import kr.co.imcloud.app.aichat.ui.chat.threads.ThreadsFragment.OnListFragmentInteractionListener;
import kr.co.imcloud.app.aichat.ui.common.dialog.TableDialog;
//import kr.co.dominos.app.aichat.ui.chat.threads.dummy.DummyContent.DummyItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ThreadItemRecyclerViewAdapter extends RecyclerView.Adapter<ThreadItemRecyclerViewAdapter.ViewHolder> {

    private final List<ThreadItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    public ChatActivity chatActivity;
    public ImageLoader imageLoader;
    public DisplayImageOptions options;

    public ThreadItemRecyclerViewAdapter(List<ThreadItem> items, OnListFragmentInteractionListener listener , ChatActivity chatActivity) {
        mValues = items;
        mListener = listener;
        this.chatActivity = chatActivity;
    }

    @Override
    public int getItemViewType(int position) {
        ThreadItem item = mValues.get(position);
        if (item.isSend) {
            return 0;
        }
        return 1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.thread_item_send, parent, false);
            return new ViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thread_item_receive, parent, false);

        imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(chatActivity)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.no_img)
                .showImageOnFail(R.drawable.no_img)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, 180, 180, false);
                    }
                })
                .build();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getTime());
        if (mValues.get(position).content != null) {
            holder.mContentView.setText(Html.fromHtml(mValues.get(position).content));
        }

//        itemType(holder, position);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                    ChatStore.inst().reqHideKeyboard();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public ThreadItem mItem;
        public FrameLayout mContentLayout;
//        public LinearLayout tableLayout;
//        public TableLayout table1 ,table2;
        public ImageView imageView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mContentLayout = (FrameLayout)view.findViewById(R.id.content_detail_layout);
//            tableLayout = (LinearLayout) view.findViewById(R.id.table_layout);
//            table1 = (TableLayout)view.findViewById(R.id.table1);
//            table2 = (TableLayout)view.findViewById(R.id.table2);
            imageView = (ImageView)view.findViewById(R.id.image);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

        public void itemType(ViewHolder holder , int position){
        if(mValues.get(position).type != null && !mValues.get(position).type.equals("")){

            if(mValues.get(position).content != null && !mValues.get(position).content.equals("")){
                if(mValues.get(position).content.contains("image")){
                    holder.mContentLayout.setVisibility(View.VISIBLE);
                    holder.imageView.setVisibility(View.VISIBLE);
//                    holder.tableLayout.setVisibility(View.GONE);
                }
                else{
                    if(holder.mContentLayout != null && holder.imageView !=null) {
                        holder.mContentLayout.setVisibility(View.GONE);
//                        holder.tableLayout.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }
}
