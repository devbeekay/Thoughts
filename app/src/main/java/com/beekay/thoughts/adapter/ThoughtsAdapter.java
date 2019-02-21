package com.beekay.thoughts.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.beekay.thoughts.AddActivity;
import com.beekay.thoughts.R;
import com.beekay.thoughts.ViewActivity;
import com.beekay.thoughts.db.DataOpener;
import com.beekay.thoughts.model.Thought;
import com.beekay.thoughts.util.Utilities;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.share.model.ShareLinkContent;

import java.util.ArrayList;
import java.util.List;

public class ThoughtsAdapter extends RecyclerView.Adapter<ThoughtsAdapter.ThoughtViewHolder>
    implements Filterable {

    List<Thought> thoughts;
    Context context;
    RequestOptions fitCenter;
    List<Thought> filteredThoughts = new ArrayList<>();
    List<Thought> oldThoughts = new ArrayList<>();
    Utilities utilities;

    public ThoughtsAdapter(List<Thought> thoughts, Context context){
        this.thoughts = thoughts;
        this.context = context;
        this.filteredThoughts.addAll(thoughts);
        this.oldThoughts.addAll(thoughts);
        fitCenter = new RequestOptions().fitCenter();
        utilities = new Utilities(context);
    }

    public void swap(List<Thought> thoughts){
        this.thoughts.clear();
        this.thoughts.addAll(thoughts);
        notifyDataSetChanged();
    }

    @Override
    public ThoughtViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item,parent,false);
        return new ThoughtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ThoughtViewHolder holder, int position) {
        final Thought thought = thoughts.get(position);
        holder.thoughtView.setText(thought.getThoughtText());
        holder.dateView.setText(thought.getTimestamp());
        holder.idView.setText(""+thought.getId());
        if(thought.getImg() != null){
            Glide.with(context)
                    .load(thought.getImg())
                    .apply(fitCenter)
                    .into(holder.imgView);
//            SharePhoto photo = new SharePhoto.Builder()
//                    .setBitmap(myBitMap)
//                    .setCaption("Batman")
//                    .build();
//            SharePhotoContent content = new SharePhotoContent.Builder()
//                    .addPhoto(photo)
//                    .build();
//            holder.shareButton.setShareContent(content);
//            }
        }else{
            holder.imgView.setVisibility(View.GONE);
            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("www.google.com"))
                    .setQuote(thought.getThoughtText())
                    .build();
//            holder.shareButton.setShareContent(content);
        }


    }

    @Override
    public int getItemCount() {
        return thoughts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString();
                if (query.isEmpty()){
                    filteredThoughts.clear();
                    filteredThoughts.addAll(oldThoughts);
                } else {
                    List<Thought> fList = new ArrayList<>();
                    for ( Thought t : oldThoughts ) {
                        if (t.getThoughtText().toLowerCase().contains(query.toLowerCase())) {
                            fList.add(t);
                        }
                    }
                    filteredThoughts.clear();
                    filteredThoughts.addAll(fList);
                }

                FilterResults fResults = new FilterResults();
                fResults.values = filteredThoughts;
                return  fResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredThoughts = (List<Thought>) results.values;
                swap(filteredThoughts);
            }
        };
    }


    class ThoughtViewHolder extends RecyclerView.ViewHolder{

        TextView thoughtView;
        TextView dateView;
        ImageView imgView;
        TextView idView;
//        ShareButton shareButton;

        public ThoughtViewHolder(View itemView) {
            super(itemView);

            thoughtView = itemView.findViewById(R.id.thought);
//            thoughtView.setMovementMethod(LinkMovementMethod.getInstance());
            dateView = itemView.findViewById(R.id.timestamp);
            imgView = itemView.findViewById(R.id.img);
            idView = itemView.findViewById(R.id.idField);
//            shareButton = itemView.findViewById(R.id.share);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long id = Long.valueOf(idView.getText().toString());
                    Thought selectedThought = null;
                    for ( Thought t : thoughts) {
                        if ( t.getId().equals(id)) {
                            selectedThought = t;
                            break;
                        }
                    }
                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra("thoughtSelected", selectedThought);
                    context.startActivity(intent);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((AppCompatActivity)v.getContext() ).startSupportActionMode(new ContextualCallBack(ThoughtViewHolder.this));
                    return true;
                }
            });
        }
    }

    class ContextualCallBack implements ActionMode.Callback {

        private ThoughtViewHolder viewHolder;

        public ContextualCallBack(ThoughtViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("Delete");
            menu.add("Edit");
            menu.add("Copy");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if ( item.getTitle().equals("Copy")) {
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Thought", viewHolder.thoughtView.getText().toString() +
                                                        " Created at " + viewHolder.dateView.getText().toString());
                clipboardManager.setPrimaryClip(clip);
                mode.finish();
            }
            if (item.getTitle().equals("Delete")) {
                DataOpener db = new DataOpener(context);
                db.open();
                db.delete(viewHolder.idView.getText().toString());
                db.close();
                thoughts = utilities.getThoughts();
                notifyDataSetChanged();
                mode.finish();
            }
            if (item.getTitle().equals("Edit")) {
                Intent intent = new Intent(context, AddActivity.class);
                intent.putExtra("Edit", true);
                intent.putExtra("id", viewHolder.idView.getText().toString());
                ((Activity)context).startActivityForResult(intent,100);
                mode.finish();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }

}
