package alves.thiago.agenda;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;

public class MainActivity extends AppCompatActivity {

   // mCalendarView calendar;
    ExpCalendarView calendar;
    String Username = "";
    Map<String, String > days = new HashMap<>();
    Thread StartReceiverData ;
    List cods = new ArrayList<String>();
    InterstitialAd mInterstitialAd;
    Spinner selectAno;
    TextView textView;
    String[] mes;
    CallbackManager callbackManager;
    String[] arraySpinner;
    Date currentTime;
    int color = 0;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_compose:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar =  findViewById(R.id.calendar);
        textView = findViewById(R.id.textView);
        selectAno = findViewById(R.id.select);

        callbackManager = CallbackManager.Factory.create();

        ViewGroup root = findViewById(R.id.root);

        CardsAdapter Cardsadapter;
        Cardsadapter = new CardsAdapter(this);
        List<CardModel> cardsAux = new ArrayList<>();
        cardsAux.add(new CardModel("IMAGEM", "Churrasco na Casa de Rafa no dia 09/09", "Criador do evento: Lucas","Thiago confirmou!! \n Addison confirmou !!",""));


       Cardsadapter.add(cardsAux.get(0));

        ListView lvCards = findViewById(R.id.list);
        lvCards.setAdapter(Cardsadapter);



        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        //loginButton.callOnClick();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });



        mes = new String[] {"Janeiro","Fevereiro","Março","Abril","Maio","Junho","Julho","Agosto","Setembro","Outubro","Novembro","Dezembro"};

        currentTime = Calendar.getInstance().getTime();
        textView.setText(mes[currentTime.getMonth()]);


        arraySpinner = new String[90];

        for (int i = 10; i != arraySpinner.length; i++){
            arraySpinner[i-10] = "20"+ (String.valueOf(i));

        }

        selectAno.post(new Runnable() {
            @Override
            public void run() {
                selectAno.setSelection(8);
            }
        });


        selectAno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
               // Toast.makeText(getBaseContext(), list.get(position), Toast.LENGTH_SHORT).show();
                calendar.travelTo(new DateData(Integer.valueOf(arraySpinner[position]), currentTime.getMonth()+1,1));
                calendar.unMarkDate(Integer.valueOf(arraySpinner[position]), currentTime.getMonth()+1,1);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        calendar.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                textView.setText(mes[month-1]);

            }
        });


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectAno.setAdapter(adapter);



        Typeface face = Typeface.createFromAsset(getAssets(),
                "fonts/font.ttf");
        textView.setTypeface(face);


        MobileAds.initialize(this, "ca-app-pub-4653575622321119~7566354167");

        AdView adView = findViewById(R.id.adView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4653575622321119/2718294116");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });



        StartReceiverData = new Thread(new Runnable() {
            public void run() {
                while(true){
                    try {
                        RequestForTime();
                        try{ StartReceiverData.sleep(5000);}catch (Exception e){}
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }});
        StartReceiverData.start();
        //RequestForTime();






        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        try{
            Username = readNoteOnSD("Username.txt");
        } catch (Exception e){
            e.printStackTrace();
            Username = null;
        }


        if (Username == null){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Seu nome");
            alertDialog.setMessage("PS: Não tem como mudar depois, bote seu nome mesmo KK");
            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);
            Random rnd = new Random();
            final int color2 = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));


            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                        generateNoteOnSD("Username.txt",input.getText().toString()+"\n"+color2);
                    Username = input.getText().toString();
                    color = color2;
                }
            });
            alertDialog.show();


        }

        calendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                CreateDialog(date);

            }
        });

    }
    public void generateNoteOnSD(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Salvo !", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readNoteOnSD(String sFileName) {
        String line = null;
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileReader reader = new FileReader(gpxfile);
            BufferedReader br = new BufferedReader(reader);
            try {
                line = br.readLine();
                color = Integer.valueOf(br.readLine());
                Toast.makeText(this, "Bem vindo de volta " + line, Toast.LENGTH_SHORT).show();

            } catch (Exception e){
                e.printStackTrace();
            }
                finally {
                br.close();
            }



        } catch (IOException e) {
            e.printStackTrace();
        }

        return line;
    }

    public void CreateDialog(final DateData date){

        AlertDialog alerta;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eventos neste dia");
        String day = date.getMonth() + Integer.toString(date.getDay());
        Log.d("Carai", day);
        builder.setMessage(days.get(day));
        Log.d("Carai", "A MESSAGEM: " + days.toString());
//        Log.d("Carai", days.get(day));


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        builder.setNeutralButton("Criar novo Evento", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                CreateNovoEvento(Integer.toString(date.getYear()), (Integer.toString(date.getMonth())), (Integer.toString(date.getDay())));


            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        alerta = builder.create();
        alerta.show();
    }

    public void CreateNovoEvento(final String year,final String month, final String dayOfMonth){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Novo Evento");
        alertDialog.setMessage("Descreva o novo evento");
        final EditText input = new EditText(MainActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);


        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int which) {

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        // Create a new user with a first and last name
                        Map<String, Object> user = new HashMap<>();
                        user.put("ano", year);
                        user.put("mes", month);
                        user.put("dia", dayOfMonth);
                        user.put("message", input.getText().toString());
                        user.put("user",Username);
                        Date currentTime = Calendar.getInstance().getTime();
                        user.put("cod",currentTime);
                        user.put("color",color);



                        db.collection("users")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d("Carai", "DocumentSnapshot added with ID: " + documentReference.getId());
                                        Toast.makeText(getApplication(), "Evento criado !", Toast.LENGTH_SHORT).show();

                                        if (mInterstitialAd.isLoaded()) {
                                            mInterstitialAd.show();
                                        } else {
                                            Log.d("TAG2", "The interstitial wasn't loaded yet.");
                                        }


                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Carai", "Error adding document", e);
                                    }
                                });


                    }
                });
        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        dialog.cancel();
                    }
                });

        alertDialog.show();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void RequestForTime(){
        days.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Carai", document.getId() + " => " + document.getData());
                                Log.d("Carai", document.getId() + " => " + document.getData().get("mes"));
                                Log.d("Carai", document.getId() + " => " + document.getData().get("dia"));
                                Log.d("Carai", document.getId() + " => " + document.getData().get("user"));
                                Log.d("Carai", document.getId() + " => " + document.getData().get("cod"));
                                Log.d("Carai", document.getId() + " => " + document.getData().get("color"));


                                String day = document.getData().get("mes").toString()+document.getData().get("dia").toString();
                                String message = null;
                                try {
                                    message = days.get(day);
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                if (message == null){
                                    message = document.getData().get("user") +": "+ document.getData().get("message").toString();
                                } else{
                                    message += "\n" + document.getData().get("user") +": "+ document.getData().get("message").toString() ;
                                }
                                Log.d("Carai", day);


                                days.put(day, message);




                                Log.d("Carai", days.toString());


                                calendar.markDate(
                                        new DateData(Integer.valueOf(document.getData().get("ano").toString()),  Integer.valueOf(document.getData().get("mes").toString()),  Integer.valueOf(document.getData().get("dia").toString())).
                                                setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Integer.valueOf(document.getData().get("color").toString()))));


                            }
                        } else {
                            Log.w("Carai", "Error getting documents.", task.getException());
                        }
                    }
                });



    }

}
