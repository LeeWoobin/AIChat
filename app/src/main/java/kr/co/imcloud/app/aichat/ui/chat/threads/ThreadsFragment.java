package kr.co.imcloud.app.aichat.ui.chat.threads;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import kr.co.imcloud.app.aichat.R;
import kr.co.imcloud.app.aichat.models.ThreadItem;
import kr.co.imcloud.app.aichat.stores.ChatStore;
import kr.co.imcloud.app.aichat.ui.chat.ChatActivity;

public class ThreadsFragment extends Fragment {

    public static final String TAG = "ThreadsFragment";
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    public ThreadsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            this.recyclerView = (RecyclerView) view;
//            recyclerView.setHasFixedSize(true);
                this.mLinearLayoutManager = new WrapContentLinearLayoutManager(context);
//            this.mLinearLayoutManager = new LinearLayoutManager(context);
//                this.mLinearLayoutManager.setReverseLayout(true);
                this.mLinearLayoutManager.setStackFromEnd(true);
                this.mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                this.recyclerView.setLayoutManager(this.mLinearLayoutManager);
            ThreadItemRecyclerViewAdapter adapter = new ThreadItemRecyclerViewAdapter(this.getStore().getChatModel().getItems(), mListener,(ChatActivity)getActivity());
            this.recyclerView.setAdapter(adapter);

            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ChatStore.inst().reqHideKeyboard();
                    return false;
                }
            });

//            this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                @Override
//                public void onScrollStateChanged(RecyclerView recyclerView, int newState)
//                {
//                    super.onScrollStateChanged(recyclerView, newState);
//                    Log.d(TAG, "onScrollStateChanged()");
//                }
//
//                @Override
//                public void onScrolled(RecyclerView recyclerView, int dx, int dy)
//                {
//                    super.onScrolled(recyclerView, dx, dy);
//                    // Do my logic
//                    Log.d(TAG, "onScrolled(), dy=" + dy);
//                }
//            });

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.getStore().addMessageHandler(messageHandler);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

        if (this.recyclerView != null) {
            this.recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        this.getStore().removeMessageHandler(messageHandler);
    }


    private ChatStore getStore() {
        return ChatStore.inst();
    }

    private Handler messageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ChatStore.MSG_CHAT_RECV:
                case ChatStore.MSG_CHAT_SEND: {
                    onChat(msg);
                    break;
                }
            }
        }
    };

    private void onChat(Message msg) {
        int itemCount = getStore().getThreadCount();
        this.recyclerView.getAdapter().notifyItemInserted(itemCount-1);
//        this.recyclerView.getAdapter().notifyDataSetChanged();

        int ic = recyclerView.getAdapter().getItemCount();
        Log.d(TAG, "onChat(), ic=" + ic);

        this.recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
    }

//    public void scrollToBottom() {
////        mLinearLayoutManager.scrollToPosition(getStore().getThreadCount());
//        this.recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
//
////        new Handler().postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                int itemCount = getStore().getThreadCount();
////                Log.d(TAG, "scrollToBottom(), itemCount=" + itemCount);
////                int ic = recyclerView.getAdapter().getItemCount();
////                Log.d(TAG, "scrollToBottom(), ic=" + ic);
////            }
////        }, 2000);
//
//    }


    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ThreadItem item);
    }


}
