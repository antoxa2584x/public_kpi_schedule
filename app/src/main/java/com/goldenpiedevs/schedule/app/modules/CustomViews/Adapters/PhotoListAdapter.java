package com.goldenpiedevs.schedule.app.modules.CustomViews.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.FullInfoActivity;
import com.goldenpiedevs.schedule.app.activitys.NotePhotoActvivty;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class PhotoListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> arrayList;
    private final boolean isEditable;
    private final Animation show;
    private AdapterChangeListener adapterChangeListener;

    public PhotoListAdapter(Activity context, ArrayList<String> arrayList, boolean isEditable, AdapterChangeListener adapterChangeListener) {
        super(context, R.layout.list_single, arrayList);
        this.context = context;
        this.arrayList = arrayList;
        this.isEditable = isEditable;
        this.adapterChangeListener = adapterChangeListener;
        show = AnimationUtils.loadAnimation(context, R.anim.appear_animation);
    }


    public static Bitmap decodeSampledBitmapFromResource(File file, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        adapterChangeListener.onDataChanged(arrayList.size());
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            view = inflater.inflate(R.layout.list_single, null);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) view.findViewById(R.id.img);
            viewHolder.progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
            viewHolder.removeImage = (ImageView) view.findViewById(R.id.image_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final Timer myTimer = new Timer(); // Создаем таймер
        myTimer.schedule(new TimerTask() { // Определяем задачу
            @Override
            public void run() {
                File f = new File(arrayList.get(position));
                if (f.exists()) {
                    final Bitmap myBitmap = decodeSampledBitmapFromResource(f, 200, 200);
                    if (myBitmap != null) {
                        myTimer.cancel();
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.progressBar.setVisibility(View.GONE);
                                viewHolder.image.setImageBitmap(Bitmap.createScaledBitmap(myBitmap, 120, 120, false));
                                viewHolder.image.setVisibility(View.VISIBLE);
                                viewHolder.image.startAnimation(show);
                            }
                        });
                    }
                }
            }
        }, 100, 100);


        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, NotePhotoActvivty.class);
                i.putExtra("photo_link", arrayList.get(position));
                context.startActivity(i);
            }
        });
        viewHolder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File f = new File(arrayList.get(position));
                f.delete();
                arrayList.remove(position);
                FullInfoActivity.photoList = arrayList;
                notifyDataSetChanged();
            }
        });

        if (!isEditable)
            viewHolder.removeImage.setVisibility(View.INVISIBLE);

        return view;

    }

    public interface AdapterChangeListener {
        void onDataChanged(int arraySize);
    }

    static class ViewHolder {
        ImageView image;
        ProgressBar progressBar;
        ImageView removeImage;
    }
}