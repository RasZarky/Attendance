package com.example.attendancev1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

 public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {

    private List<ModelClass> userList;
    private Context context;
    DBHelper db;

    public Adapter (List<ModelClass>userList, Context context) {
        this.db = new DBHelper(context);
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_design,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.viewHolder holder, int position) {

        String Programme = userList.get(position).getTxtProgramme();
        String Course = userList.get(position).getTxtCourse();
        String Level = userList.get(position).getTxtLevel();
        String Progress = userList.get(position).getTxtProgress();

        holder.setData(Programme, Course, Level, Progress);
        holder.relativeLayout.setOnClickListener(v -> {

            Cursor getStatus = db.viewStatus();
            StringBuilder buffer = new StringBuilder();

            while (getStatus.moveToNext()){
                buffer.append(getStatus.getString(0));
            }
            String Status = buffer.toString().toUpperCase().replace(" ","");

            Intent intent;
            if (Status.equals("TUTOR") && isClickable){
                intent = new Intent(context.getApplicationContext(), ProgressActivity.class);

                intent.putExtra("Programme",userList.get(position).getTxtProgramme());
                intent.putExtra("Course",userList.get(position).getTxtCourse());
                intent.putExtra("Level",userList.get(position).getTxtLevel());
                context.startActivity(intent);
            }  else if (Status.equals("")){
                intent = new Intent(context.getApplicationContext(), StudentProgressActivity.class);

                intent.putExtra("Programme",userList.get(position).getTxtProgramme());
                intent.putExtra("Course",userList.get(position).getTxtCourse());
                intent.putExtra("Level",userList.get(position).getTxtLevel());
                context.startActivity(intent);
            }


        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        private TextView txtProgramme;
        private TextView txtCourse;
        private TextView txtLevel;
        private TextView txtProgress;
        private RelativeLayout relativeLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            txtProgramme = itemView.findViewById(R.id.txtProgramme);
            txtCourse = itemView.findViewById(R.id.txtCourse);
            txtLevel = itemView.findViewById(R.id.txtLevel);
            txtProgress = itemView.findViewById(R.id.txtProgress);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }

        public void setData(String Programme, String Course, String Level, String Progress){
            txtProgramme.setText(Programme);
            txtCourse.setText(Course);
            txtLevel.setText(Level);
            txtProgress.setText(Progress);
        }
    }

    public boolean isClickable = true;
}
