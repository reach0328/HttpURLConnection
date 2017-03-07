package com.android.jh.httpurlconnection;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView txt,txtTitle;
    EditText edit;
    RelativeLayout progress_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.button);
        txt = (TextView) findViewById(R.id.textView);
        txtTitle = (TextView) findViewById(R.id.textTitle);
        edit = (EditText) findViewById(R.id.editText);
        progress_layout = (RelativeLayout) findViewById(R.id.progress_Layout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlString = edit.getText().toString();
                getUrl(urlString);
            }
        });
    }
    public void getUrl(String urlString) {
        if(!urlString.startsWith("http")){
            urlString = "http://"+urlString;
        }
        new AsyncTask<String,Void,String>() {
            ProgressDialog dialog = new ProgressDialog(MainActivity.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setMessage("불러오는중.....");
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];

                try {
                    //String을 url 객체로 변환
                    URL url = new URL(urlString);
                    //url 로 네트워크 연결 시작
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // url 연결에 대한 옵션 설정
                    connection.setRequestMethod("GET"); // GET : 데이터 요청시 사용하는 방식
                    // POST : 데이터 입력시
                    // PUT : 데이터 수정시
                    // DELETE : 데이터 삭제시
                    // 서버로부터 응답 코드 회신
                    int responseCode = connection.getResponseCode();
                    if(responseCode ==HttpURLConnection.HTTP_OK){
                        // 연결로 부터 스트림을 얻고, 버퍼래퍼로 감싼다.
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder result = new StringBuilder();
                        String lineOfdata = "";
                        String title = "";
                        // 번복문을 돌며넛 버퍼의 데이터를 읽어온다.
                        while((lineOfdata = br.readLine()) != null) {
                            result.append(lineOfdata);
                        }
                        return result.toString();
                    } else {
                        Log.e("HTTPConnection","Error Code "+ responseCode);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                txt.setText(result);
                String title = result.substring(result.indexOf("<title>")+7,result.indexOf("</title>"));
                txtTitle.setText(title);
                dialog.dismiss();
            }
        }.execute(urlString);

    }
}
