package com.example.lab3_1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Volley — это HTTP-библиотека, которая упрощает и ускоряет работу в сети для приложений Android.
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;


public class MainActivity extends AppCompatActivity {

    public Context mainActivity = this;
    private ImageView picture;
    private RadioGroup radioGroup;
    private Button btn1;
    boolean firstPress = true;//первое ли нажатие на кнопку
    private String link = "https://www.dorogavrim.ru/articles/flagi_stran_mira/";
    private String domain = "https://www.dorogavrim.ru";
    private ArrayList<String> names = new ArrayList<>();//названия для угадывания
    private ArrayList<String> urls = new ArrayList<>();//картинки для угадывания
    private int numberOfTrueImage = 0; // номер картинки, которую угадываем
    private String context = "";
    private int round = 1; //номер раунда
    private int rightAnswers = 0; // количество правильных ответов
    private boolean firstLaunch = true;
    private int numberOfAnswers = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        picture = findViewById(R.id.image);
        radioGroup = findViewById(R.id.choises);
        btn1 = findViewById(R.id.check);

        if (getIntent().getExtras() != null) {
            this.round = getIntent().getExtras().getInt("rounds");
            this.rightAnswers = getIntent().getExtras().getInt("right");
            if (this.round > 1) {
                TextView textView2 = findViewById(R.id.textView2);
                String result = "Правильных ответов: " + this.rightAnswers + '/' + this.round;
                textView2.setText(result);
            }
            this.round = 1;
            this.rightAnswers = 0;
        }

        //Скачиваем и парсим данные с сайта
        getContent(link);
        int a =0;
    }

    private void randomOptions(int numberOfAnswers) {
        //номер кнопки с правильным ответом
        int numberOfRightAnswer = (int) (Math.random() * numberOfAnswers);//3);
        int i = 0;
        while (i < numberOfAnswers) {
            int numberOfRandomImage = (int) (Math.random() * urls.size());
            while (numberOfRandomImage == this.numberOfTrueImage /*|| numberOfRandomImage == numberOfRightAnswer*/) {
                numberOfRandomImage = (int) (Math.random() * urls.size());
            }
            RadioButton radioButton;
            radioGroup.clearCheck();
            if (firstPress) {
                radioButton = new RadioButton(this);
                radioButton.setId(View.generateViewId());
                radioGroup.addView(radioButton);
            } else {
                radioButton = (RadioButton) radioGroup.getChildAt(i);
            }
            if (numberOfRightAnswer != i) {
                radioButton.setText(names.get(numberOfRandomImage));
            } else {
                radioButton.setText(names.get(this.numberOfTrueImage));
            }
            radioButton.setTextSize(20);
            i++;
        }
    }



    //Классы для загрузки изображений по ссылке и кода HTML переданного адреса сайта
    private static class DownloadContentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }
    }


    //Метод, который загружает в  массив список изображений:
    private  void getContent(String url)
    {
        DownloadContentTask task = new DownloadContentTask();
        try {
            String content = task.execute(url).get();
            String  start = "<table style=\"width: 100%;\" align=\"center\">";
            String finish = "</table>";
            Pattern pattern = Pattern.compile(start+ "(.*?)"+ finish);
            Matcher matcher = pattern.matcher(content);
            String splitContent = "";
            while (matcher.find())
                splitContent = matcher.group(1);

            Pattern paternImg = Pattern.compile(" src=\"(.*?)\" style=");
            Pattern patternName = Pattern.compile("<br>\t\t (.*?)</");
            Matcher matcherImg = paternImg.matcher(splitContent);
            Matcher matcherName = patternName.matcher(splitContent);
            while (matcherImg.find())
                urls.add(domain+ matcherImg.group(1));
            while (matcherName.find())
                names.add(matcherName.group(1));


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    //Метод который отображает на экране фото:
    private void playGame()
    {
        numberOfTrueImage = (int) (Math.random()*urls.size());

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        // Bitmap listener
        // Error listener
        ImageRequest imageRequest = new ImageRequest(
                urls.get(numberOfTrueImage), // Image URL
                response -> {
                    // Do something with response
                    picture.setImageBitmap(response);
                },
                0, // Image width
                0, // Image height
                ImageView.ScaleType.CENTER_CROP, // Image scale type
                Bitmap.Config.RGB_565, //Image decode configuration
                error -> {
                    // Do something with error response
                    Log.i("JSON error:", error.getMessage());
                    error.printStackTrace();

                }
        );
        // Add ImageRequest to the RequestQueue
        requestQueue.add(imageRequest);

    }

    public void check(View view){
        try {   //первое нажатие на кнопку: выводит картинку и поле для ввода фамилии
            if (firstPress) {
                playGame();
                randomOptions(numberOfAnswers);
                btn1.setText("Ответ");
                radioGroup.setVisibility(View.VISIBLE);
                firstPress = false;
            }
            //второе и последющие нажатия на кнопку проверяют
            // введенную фамилию и выводят новую картинку
            else {
                int id = radioGroup.getCheckedRadioButtonId();
                if (id != -1) {
                    String answer = ((RadioButton) findViewById(id)).getText().toString();
                    if (!(answer.equals(""))) {
                        if (answer.equals(names.get(numberOfTrueImage))) {
                            Toast toast = Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT);
                            toast.show();
                            this.rightAnswers++;
                        } else {
                            Toast toast = Toast.makeText(this, "Неправильно! (Ответ - "+names.get(numberOfTrueImage)+")", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        playGame();
                        randomOptions(numberOfAnswers);
                    }
                    this.round++;
                } else {
                    Toast toast = Toast.makeText(this, "Ответ не выбран", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            TextView textView1 = findViewById(R.id.textView1);
            String questionNo = "Вопрос № " + this.round;
            textView1.setText(questionNo);
            TextView textView2 = findViewById(R.id.textView2);
            String count = this.rightAnswers + "/" + (this.round - 1);
            textView2.setText(count);
            if (this.round == 2) {
                Button finishButton = findViewById(R.id.exit);
                finishButton.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void finish(View view) {
        Intent intent = new Intent(this.mainActivity, MainActivity.class);
        intent.putExtra("rounds", this.round - 1);
        intent.putExtra("right", this.rightAnswers);
        startActivity(intent);
        Toast toast = Toast.makeText(this, "Тест завершен", Toast.LENGTH_SHORT);
        toast.show();
    }


}


