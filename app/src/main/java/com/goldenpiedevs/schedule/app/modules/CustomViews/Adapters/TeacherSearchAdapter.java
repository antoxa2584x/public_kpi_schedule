package com.goldenpiedevs.schedule.app.modules.CustomViews.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.goldenpiedevs.schedule.app.R;
import com.goldenpiedevs.schedule.app.activitys.MainActivity;
import com.goldenpiedevs.schedule.app.dataloader.TeacherScheduleLoader;
import com.goldenpiedevs.schedule.app.dataloader.io.TeacherIO;
import com.goldenpiedevs.schedule.app.dataloader.listeners.DownloadStatusListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TeacherSearchAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> originalData = null;
    private List<String> filteredData = null;
    private ItemFilter mFilter = new ItemFilter();
    private Activity mContext;

    public TeacherSearchAdapter(Activity context, int resource, List<String> teachers) {
        super(context, resource, teachers);
        this.filteredData = teachers;
        this.originalData = teachers;
        mContext = context;
    }


    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.teacheras_listview_row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.rate = (TextView) convertView.findViewById(R.id.teacher_list_row_rating);
            viewHolder.name = (TextView) convertView.findViewById(R.id.teacher_list_row_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(String.valueOf(filteredData.get(position).charAt(0)));
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(String.valueOf(filteredData.get(position).charAt(0)), color);

        viewHolder.image.setImageDrawable(drawable);
        viewHolder.name.setText(filteredData.get(position));
        viewHolder.rate.setVisibility(View.GONE);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openConfirmDialog(originalData.indexOf(filteredData.get(position)));
            }
        });
        return convertView;
    }

    private void openConfirmDialog(final int position) {
        if (!TeacherIO.isTeacherDownloaded(String.valueOf(position + 1), getContext()))
            new MaterialDialog.Builder(getContext())
                    .title("Завантажити розклад викладача?")
                    .content("Розклад викладача буде збережено в локальну пам\'ять")
                    .autoDismiss(true)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            final MaterialDialog progressDialog = new MaterialDialog.Builder(getContext())
                                    .title("Зачекайте")
                                    .autoDismiss(false)
                                    .cancelable(false)
                                    .content(getContext().getResources().getString(R.string.loading_chedule))
                                    .progress(true, 0)
                                    .show();

                            new TeacherScheduleLoader(mContext)
                                    .setStatusListener(new DownloadStatusListener() {
                                        @Override
                                        public void onComplete(boolean result) {
                                            new Timer().schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    progressDialog.dismiss();
                                                    openTeacherActivity(position);
                                                }
                                            }, 100);
                                        }


                                        @Override
                                        public void onFailed(int status) {
                                        }
                                    })
                                    .execute(String.valueOf(position + 1));


                            super.onPositive(dialog);
                        }

                    })
                    .positiveText(getContext().getString(R.string.yes))
                    .negativeText("Сховати")
                    .build()
                    .show();
        else {
            openTeacherActivity(position);
        }

    }

    private void openTeacherActivity(int position) {
        Intent i = new Intent(getContext(), MainActivity.class);
        i.putExtra("teacherId", position + 1);
        i.putExtra("teacherName", originalData.get(position));
        getContext().startActivity(i);

    }

    public Filter getFilter() {
        return mFilter;
    }

    public int getCount() {
        return filteredData.size();
    }

    public String getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position + 1;
    }

    static class ViewHolder {
        TextView name;
        ImageView image;
        TextView rate;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();
            if (filterString.contains("и"))
                filterString = filterString.replace('и', 'і');

            FilterResults results = new FilterResults();

            final List<String> list = originalData;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<>(count);

            String filterableString;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }
}
