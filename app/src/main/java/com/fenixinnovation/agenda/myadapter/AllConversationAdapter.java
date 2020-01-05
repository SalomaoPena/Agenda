package com.fenixinnovation.agenda.myadapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.fenixinnovation.agenda.R;
import com.fenixinnovation.agenda.interfaces.ItemCLickListener;
import com.fenixinnovation.agenda.objects.SMS;
import com.fenixinnovation.agenda.utils.ColorGeneratorModified;
import com.fenixinnovation.agenda.utils.Helpers;

import java.util.List;

public class AllConversationAdapter extends RecyclerView.Adapter<AllConversationAdapter.MyHolder> {

    private Context context;
    private List<SMS> data;
    private ItemCLickListener itemClickListener;
    private ColorGeneratorModified generator = ColorGeneratorModified.MATERIAL;


    public AllConversationAdapter(List<SMS> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setItemClickListener(ItemCLickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup convertView, int viewType) {
        context = convertView.getContext();
        LayoutInflater mInflater = LayoutInflater.from(context);
        View view = mInflater.inflate(R.layout.single_sms_small_layout, convertView, false);
        return new AllConversationAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        SMS SMS = data.get(position);

        holder.senderContact.setText(SMS.getAddress());
        holder.message.setText(SMS.getMsg());

        int color = generator.getColor(SMS.getAddress());
        String firstChar = String.valueOf(SMS.getAddress().charAt(0));
        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, color);
        holder.senderImage.setImageDrawable(drawable);

        SMS.setColor(color);


        if (SMS.getReadState().equals("0")) {
            holder.senderContact.setTypeface(holder.senderContact.getTypeface(), Typeface.BOLD);
            holder.message.setTypeface(holder.message.getTypeface(), Typeface.BOLD);
            holder.message.setTextColor(ContextCompat.getColor(context, R.color.colorBlack));
            holder.time.setTypeface(holder.time.getTypeface(), Typeface.BOLD);
            holder.time.setTextColor(ContextCompat.getColor(context,R.color.colorBlack));
        } else {
            holder.senderContact.setTypeface(null, Typeface.NORMAL);
            holder.message.setTypeface(null, Typeface.NORMAL);
            holder.time.setTypeface(null, Typeface.NORMAL);

        }

        holder.time.setText(Helpers.getDate(SMS.getTime()));
    }

    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView senderImage;
        private TextView senderContact;
        private TextView message;
        private TextView time;
        private RelativeLayout mainLayout;

        MyHolder(View itemView) {
            super(itemView);
            senderImage = itemView.findViewById(R.id.smsImage);
            senderContact = itemView.findViewById(R.id.smsSender);
            message = itemView.findViewById(R.id.smsContent);
            time = itemView.findViewById(R.id.time);
            mainLayout = itemView.findViewById(R.id.small_layout_main);

            mainLayout.setOnClickListener(this);
            mainLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {
                data.get(getAdapterPosition()).setReadState("1");
                notifyItemChanged(getAdapterPosition());
                itemClickListener.itemClicked(data.get(getAdapterPosition()).getColor(),
                        senderContact.getText().toString(),
                        data.get(getAdapterPosition()).getId(),
                        data.get(getAdapterPosition()).getReadState());
            }

        }

        @Override
        public boolean onLongClick(View view) {

            String[] items = {"Delete"};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context
                    , android.R.layout.simple_list_item_1, android.R.id.text1, items);

            new AlertDialog.Builder(context)
                    .setAdapter(adapter,(dialogInterface, i) ->  {
                        dialogInterface.dismiss();
                        deleteDialog();
                    })
                    .show();

            return true;
        }

        private void deleteDialog() {

            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setMessage("Are you sure you want to delete this message?");
            alert.setPositiveButton("Yes",(dialogInterface, i) ->  {
                deleteSMS(data.get(getAdapterPosition()).getId(), getAdapterPosition());
            });
            alert.setNegativeButton("No", (dialog, i) ->  {
                dialog.dismiss();
            });
            alert.create();
            alert.show();
        }
    }

    private void deleteSMS(long messageId, int position) {

        long affected = context.getContentResolver().delete(
                Uri.parse("content://sms/" + messageId), null, null);

        if (affected != 0) {
            data.remove(position);
            notifyItemRemoved(position);
        }

    }
}
