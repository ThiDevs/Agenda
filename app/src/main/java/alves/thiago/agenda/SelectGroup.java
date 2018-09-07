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
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
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
import java.util.Set;

import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.views.ExpCalendarView;
import sun.bob.mcalendarview.vo.DateData;

public class SelectGroup extends AppCompatActivity {

    // mCalendarView calendar;
    ExpCalendarView calendar;
    String Username = "";
    Map<String, String > days = new HashMap<>();
    Map<String, Integer > situacaoDays = new HashMap<>();

    /*
    Uma lista que tenha um map Map<String,Integer >
     */
    Thread StartReceiverData ;
    List cods = new ArrayList<String>();
    InterstitialAd mInterstitialAd;
    Spinner selectAno;
    TextView textView;
    CardView cardView;
    String[] mes;
    DateData DataMarcada;
    CallbackManager callbackManager;
    String[] arraySpinner;
    Date currentTime;
    int color = 0;
    ListView lvCards;
    CardsAdapter Cardsadapter;

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
            case R.id.CriarumGrupo:





                return true;
            case R.id.EntrarEmUmGrupo:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    CheckBox confirmar;
    CheckBox talvez;
    CheckBox nao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        calendar =  findViewById(R.id.calendar);
        textView = findViewById(R.id.textView);
        selectAno = findViewById(R.id.select);
        cardView = findViewById(R.id.card_view);
        Button FecharCard = findViewById(R.id.button3);
        confirmar = findViewById(R.id.checkBox);
        talvez = findViewById(R.id.checkBox2);
        nao = findViewById(R.id.checkBox3);
        lvCards = findViewById(R.id.list);
        Cardsadapter = new CardsAdapter(this);



        calendar.getMarkedDates().getAll().clear();



        final String collectionPathUsers = "users"+MainActivity.getCode();
        final String collectionPathUsers2 = "datas"+MainActivity.getCode();



        FecharCard.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardView.setVisibility(View.INVISIBLE);
                        lvCards.setVisibility(View.INVISIBLE);

                    }
                }
        );
        Button Ok = findViewById(R.id.button2);
        Ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cardView.setVisibility(View.INVISIBLE);
                        lvCards.setVisibility(View.INVISIBLE);
                        DateData date = getDataMarcada();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final Map<String, Object> user = new HashMap<>();
                        String data = String.valueOf(date.getYear())+date.getMonthString()+date.getDayString();
                        user.put("data", data);
                        user.put("user",Username);
                        int number;
                        if (confirmar.isChecked()){
                            number = 2;
                        } else if (talvez.isChecked()){
                            number = 1;
                        } else {
                            number = 0;
                        }
                        user.put("situacao",number);

                        int ano = Calendar.getInstance().getTime().getYear();
                        int mes = Calendar.getInstance().getTime().getMonth();
                        int dia = Calendar.getInstance().getTime().getDay();
                        int hora = Calendar.getInstance().getTime().getHours();
                        int minuto = Calendar.getInstance().getTime().getMinutes();
                        int segundo = Calendar.getInstance().getTime().getSeconds();
                        String currentTime = String.valueOf(ano)+String.valueOf(mes)+String.valueOf(dia)+String.valueOf(hora)+String.valueOf(minuto)+String.valueOf(segundo);
                        Log.d("CARAI5", currentTime);
                        user.put("time",currentTime);
                        if (Username != null){

                            db.collection(collectionPathUsers2).document(data+Username)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplication(), "Resposta Enviada !", Toast.LENGTH_SHORT).show();

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
                                        }
                                    });

                        } else {
                            Toast.makeText(getApplication(), "Deu merda, Desistala e Instala novamento o APP !", Toast.LENGTH_LONG).show();

                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                Log.d("TAG2", "The interstitial wasn't loaded yet.");
                            }

                        }
                    }
                }
        );



        callbackManager = CallbackManager.Factory.create();

        ViewGroup root = findViewById(R.id.root);



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
        getSupportActionBar().setTitle(mes[currentTime.getMonth()] + " de 2018");


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
                getSupportActionBar().setTitle(mes[month-1] + " de " + year);

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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectGroup.this);
            alertDialog.setTitle("Seu nome");
            alertDialog.setMessage("PS: Não tem como mudar depois, bote seu nome mesmo KK");
            final EditText input = new EditText(SelectGroup.this);
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
                            generateNoteOnSD("Username.txt",input.getText().toString()+"\n"+color2,0);
                            Username = input.getText().toString();
                            color = color2;
                        }
                    });
            alertDialog.show();


        }

        calendar.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                lvCards.setAdapter(null);
                Cardsadapter.clear();
                Boolean CriaDialog = CheckEvent(date);
                setDateMark(date);


                if (CriaDialog){
                    cardView.setVisibility(View.VISIBLE);
                    lvCards.setVisibility(View.VISIBLE);

                    List<CardModel> cardsAux = new ArrayList<>();
                    String day = date.getMonth() + Integer.toString(date.getDay());

                    String name = "" ,situacao = "";
                    try {
                        for (String key : situacaoDays.keySet()) {
                            System.out.println();
                            if (key.substring(0,8).equals(String.valueOf(date.getYear())+date.getMonthString()+date.getDayString())){

                                int number = situacaoDays.get(key);
                                if (number == 2) {
                                    situacao = " Confirmou!!";
                                } else if (number == 1) {
                                    situacao = " diz que Talvez..";
                                } else {
                                    situacao = " não confirmou";
                                }

                                name += key.split(";")[1] + situacao + "\n";

                            }

                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        name = "Ninguém confirmou ainda :(";
                    }


                    cardsAux.add(new CardModel("IMAGEM", days.get(day), "Criador do evento: " + days.get(day+"2"),name,""));

                    Cardsadapter.add(cardsAux.get(0));
                    lvCards.setAdapter(Cardsadapter);


                } else {
                    cardView.setVisibility(View.INVISIBLE);
                    lvCards.setVisibility(View.INVISIBLE);
                    CreateDialog(date);
                }


            }
        });

    }
    public void setDateMark(DateData date){
        DataMarcada = date;
    }

    public DateData getDataMarcada() {
        return DataMarcada;
    }

    public void generateNoteOnSD(String sFileName, String sBody, int cod) {
        try {
            File root;
            if (cod == 1) {
                root = new File(Environment.getExternalStorageDirectory(), "Gruops");
            } else {
                root = new File(Environment.getExternalStorageDirectory(), "Notes");
            }
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
    public boolean CheckEvent(DateData date){
        for ( DateData dates : calendar.getMarkedDates().getAll()){
            if ((dates.getMonthString()+"/"+dates.getDayString()).equals(date.getMonthString()+"/"+date.getDayString())){
                Log.d("Carai2",dates.getDayString()+"/"+dates.getMonthString());
                return true;
            }
        }
        return false;
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectGroup.this);
        alertDialog.setTitle("Novo Evento");
        alertDialog.setMessage("Descreva o novo evento");
        final EditText input = new EditText(SelectGroup.this);
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
                        int ano = Calendar.getInstance().getTime().getYear();
                        int mes = Calendar.getInstance().getTime().getMonth();
                        int dia = Calendar.getInstance().getTime().getDay();
                        int hora = Calendar.getInstance().getTime().getHours();
                        int minuto = Calendar.getInstance().getTime().getMinutes();
                        int segundo = Calendar.getInstance().getTime().getSeconds();
                        String currentTime = String.valueOf(ano)+String.valueOf(mes)+String.valueOf(dia)+String.valueOf(hora)+String.valueOf(minuto)+String.valueOf(segundo);
                        Log.d("CARAI5", currentTime);
                        user.put("cod",currentTime);
                        user.put("color",color);



                        db.collection("users"+MainActivity.getCode())
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
        db.collection("users"+MainActivity.getCode())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("Carai", document.getId() + " => " + document.getData());
                                // Log.d("Carai", document.getId() + " => " + document.getData().get("mes"));
                                // Log.d("Carai", document.getId() + " => " + document.getData().get("dia"));
                                // Log.d("Carai", document.getId() + " => " + document.getData().get("user"));
                                //Log.d("Carai", document.getId() + " => " + document.getData().get("cod"));
                                // Log.d("Carai", document.getId() + " => " + document.getData().get("color"));


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



                                days.put(day, message);
                                days.put(day+"2", document.getData().get("user").toString());
                                calendar.markDate(
                                        new DateData(Integer.valueOf(document.getData().get("ano").toString()),  Integer.valueOf(document.getData().get("mes").toString()),  Integer.valueOf(document.getData().get("dia").toString())).
                                                setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Integer.valueOf(document.getData().get("color").toString()))));


                            }
                        } else {
                            Log.w("Carai", "Error getting documents.", task.getException());
                        }
                    }
                });
        Log.d("Carai3", situacaoDays.toString());
        situacaoDays.clear();


        db.collection("datas"+MainActivity.getCode())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("Carai", document.getId() + " => " + document.getData());
                                // Log.d("Carai3", document.getId() + " => " + document.getData().get("data"));
                                // Log.d("Carai", document.getId() + " => " + document.getData().get("user"));
                                // Log.d("Carai", document.getId() + " => " + document.getData().get("situacao"));
//


                                String data = document.getData().get("data").toString();
                                String user = document.getData().get("user").toString();
                                String situacao = document.getData().get("situacao").toString();

                                situacaoDays.put(data+";"+user,Integer.valueOf(situacao));
                                Log.d("Carai4", situacaoDays.toString());


                            }
                        } else {
                            Log.w("Carai", "Error getting documents.", task.getException());
                        }
                    }
                });



    }

}
