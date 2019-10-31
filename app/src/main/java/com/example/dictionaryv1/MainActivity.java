package com.example.dictionaryv1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity /*implements AdapterView.OnItemClickListener*/ {
    private final int REQ_CODE_SPEECH_INPUT = 100;

    private ListView listView;
    private DatabaseAccess databaseAccess;
    private AutoCompleteTextView auto;
    private ImageButton btnSpeak;
    private List<Word> listWord;
    private List<String> allWord;
    private CustomAdapter customAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mToggle;
    private ProgressBar progressBar;
    private AsyncTaskWait asyncTaskWait;
    private WebView wvSimpleWebview;
    private int loadMore=0;
  //  private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getWords();
        listWord=new ArrayList<>();
        setCustomAdapter();
        setListViewFooter();
        geSuggestion();
        createMenu();
        //listView.setOnItemClickListener(this);
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if(listView.getLastVisiblePosition() == listWord.size()){
                    asyncTaskWait = new AsyncTaskWait(new WeakReference<Context>(MainActivity.this));
                    if(asyncTaskWait == null || asyncTaskWait.getStatus() != AsyncTask.Status.RUNNING){
                        progressBar.setVisibility(View.VISIBLE);
                        asyncTaskWait.execute();
                    }
                }
            }
        });
    }

    public void init(){
        listView = (ListView) findViewById(R.id.listView);
        drawerLayout= findViewById(R.id.drawer);
        auto=findViewById(R.id.auto_complete_textview);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
      //  webView = findViewById(R.id.wvSimpleWebview);
    }

    public void createMenu(){
        mToggle= new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getWords() {
        databaseAccess = DatabaseAccess.getInstance(this);
        allWord=new ArrayList<>();
        databaseAccess.open();
        allWord = databaseAccess.getWords();
        databaseAccess.close();
    }

    private void addMoreItems(){

        List<Word> lw=new ArrayList<>();
        databaseAccess.open();
        lw=databaseAccess.getAllWordEV(loadMore);
        listWord.addAll(lw);
        Toast.makeText(this, listWord.size()+"", Toast.LENGTH_SHORT).show();
        databaseAccess.close();
        customAdapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        loadMore+=20;
    }

    public void setCustomAdapter(){
        customAdapter=new CustomAdapter(this,R.layout.row_listview, (ArrayList<Word>) listWord);
        listView.setAdapter(customAdapter);
    }

    private void geSuggestion(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,allWord);
        auto.setAdapter(arrayAdapter);
        auto.setThreshold(1);
        auto.setDropDownHeight(800);
    }

    private void setListViewFooter(){
        View view = LayoutInflater.from(this).inflate(R.layout.footer_view, null);
        progressBar = view.findViewById(R.id.progressBar);
        listView.addFooterView(progressBar);
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Trả lại dữ liệu sau khi nhập giọng nói vào
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    auto.setText(result.get(0));
                }
                break;
            }
        }
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //the one second is over, load more data
            addMoreItems();
        }
    };

    @Override
    protected void onResume(){
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("result");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

 //   @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        webView.setWebViewClient(new WebViewClient());
//        String url = "https://dict.laban.vn/find?type=1&query=";
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.loadUrl(url); // load a web page in a web view
//           Intent intent= new Intent(MainActivity.this,FrameEngVi.class);
//            startActivity(intent);
//    }

    // custom web view client class who extends WebViewClient
//    private class MyWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url); // load the url
//            return true;
//      }  }


}