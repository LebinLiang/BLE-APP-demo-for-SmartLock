package activitytest.example.com.smartlock;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by pc on 2018/2/20.
 */

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverAdapter.ViewHolder> {

   private ArrayList<String> mmessageslist=new ArrayList<>();


    private LayoutInflater mLayoutInflater;
    private Context mContext;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView messages;

        public ViewHolder(View view){
            super(view);

            messages = (TextView)view.findViewById(R.id.mess_item);
        }

    }
    public ReceiverAdapter(Context context, ArrayList<String>messageslist){
        mContext = context;
        mmessageslist = messageslist;
        mLayoutInflater = LayoutInflater.from(context);

    }

    public void remove(int position){
        notifyItemRemoved(position);

    }

    public void add(String text,String time ,int position) {
        mmessageslist.add(position, text);
        notifyItemInserted(position);

    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ReceiverAdapter.ViewHolder holder, int position) {
        holder.messages.setText(mmessageslist.get(position));

    }

    @Override
    public int getItemCount() {

        return mmessageslist==null ? 0 : mmessageslist.size();
    }

    }



