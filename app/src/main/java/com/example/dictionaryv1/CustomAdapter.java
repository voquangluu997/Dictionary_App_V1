package com.example.dictionaryv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<Word> {
    private Context context;
    private int resource;
    private List<Word> Words;

    public CustomAdapter(Context context, int resource, ArrayList<Word> arrWord) {
        super(context, resource, arrWord);
        this.context = context;
        this.resource = resource;
        this.Words = arrWord;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.row_listview,parent,false);
            viewHolder= new ViewHolder();
            viewHolder.tvWord=convertView.findViewById(R.id.tvWord);
            viewHolder.tvContent=convertView.findViewById(R.id.tvContent);

            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Word word= Words.get(position);
        viewHolder.tvWord.setText(word.getWord());
        viewHolder.tvContent.setText(android.text.Html.fromHtml(word.getContent()));

        return convertView;

    }

    public class ViewHolder{
        TextView tvWord,tvContent;
    }
}
