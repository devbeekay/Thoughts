package com.beekay.thoughts.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.beekay.thoughts.AddActivity;
import com.beekay.thoughts.R;
import com.beekay.thoughts.ViewActivity;
import com.beekay.thoughts.db.DataOpener;
import com.beekay.thoughts.model.Thought;
import com.beekay.thoughts.util.GlideApp;
import com.beekay.thoughts.util.Utilities;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThoughtsAdapter extends RecyclerView.Adapter<ThoughtsAdapter.ThoughtViewHolder>{

    List<Thought> thoughts = new ArrayList<>();
    Context context;
    RequestOptions glideOptions;
    Utilities utilities;
    boolean nightMode;

    public ThoughtsAdapter(List<Thought> thoughts, Context context, boolean nightMode) {
        this.thoughts.addAll(thoughts);
        this.context = context;
        glideOptions = new RequestOptions();
        glideOptions.fitCenter();
//        CircularProgressDrawable placeHolder = new CircularProgressDrawable(context);
//        placeHolder.setStrokeWidth(5f);
//        placeHolder.setCenterRadius(30f);
//        placeHolder.start();
//        glideOptions.placeholder(placeHolder);
        utilities = new Utilities(context);
        this.nightMode = nightMode;
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
        holder.starView.setImageDrawable(thought.isStarred() ?
                context.getResources().getDrawable(R.drawable.ic_star):
                nightMode ? context.getResources().getDrawable(R.drawable.ic_star_border) :
                context.getResources().getDrawable(R.drawable.ic_star_border_black));
        if(thought.getImg() != null && thought.getImg().length > 1) {
//            Glide.with(context)
//                    .load(thought.getImg())
//                    .apply(glideOptions)
//                    .into(holder.imgView);
//            System.out.println(thought.getImg());
//            System.out.println(thought.getImgSource());
            GlideApp.with(context)
                    .load(thought.getImg())
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
        } else if (thought.getImgSource() != null) {
            File f = new File(thought.getImgSource());
            if (f.exists()) {
                GlideApp.with(context)
                        .load(f)
                        .into(holder.imgView);
            } else {
                holder.imgView.setVisibility(View.GONE);
            }

        } else{
            holder.imgView.setVisibility(View.GONE);
//            ShareLinkContent content = new ShareLinkContent.Builder()
//                    .setContentUrl(Uri.parse("www.google.com"))
//                    .setQuote(thought.getThoughtText())
//                    .build();
//            holder.shareButton.setShareContent(content);
        }


    }

    @Override
    public int getItemCount() {
        return thoughts.size();
    }

    class ThoughtViewHolder extends RecyclerView.ViewHolder{

        TextView thoughtView;
        TextView dateView;
        ImageView imgView;
        TextView idView;
        ImageButton starView;

        public ThoughtViewHolder(View itemView) {
            super(itemView);

            thoughtView = itemView.findViewById(R.id.thought);
            dateView = itemView.findViewById(R.id.timestamp);
            imgView = itemView.findViewById(R.id.img);
            idView = itemView.findViewById(R.id.idField);
            starView = itemView.findViewById(R.id.star);
            starView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = idView.getText().toString();
                    boolean starred = false;
                    for (Thought t : thoughts) {
                        if (t.getId().equals(Long.valueOf(id))) {
                            starred = t.isStarred();
                            break;
                        }
                    }
                    starred = !starred;
                    DataOpener db = new DataOpener(context);
                    db.open();
                    db.updateStar(id, starred);
                    db.close();
                    starView.setImageDrawable(starred ?
                            context.getResources().getDrawable(R.drawable.ic_star):
                            nightMode ? context.getResources().getDrawable(R.drawable.ic_star_border) :
                            context.getResources().getDrawable(R.drawable.ic_star_border_black));
//                    starView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star));
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long id = Long.valueOf(idView.getText().toString());

                    Intent intent = new Intent(context, ViewActivity.class);
                    intent.putExtra("Mode", nightMode);
                    intent.putExtra("thoughtSelected", id);
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
                intent.putExtra("Mode", nightMode);
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
