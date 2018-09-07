package alves.thiago.agenda;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {


    CardsAdapter cardsAdapter;
    String Username;
    ListView lvCards;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvCards = findViewById(R.id.lvCards);
        final Intent intent = new Intent(this, SelectGroup.class);
        cardsAdapter = new CardsAdapter(this);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        try{
            Username = readNoteOnSD("Username.txt");
        } catch (Exception e){
            e.printStackTrace();
            Username = null;
        }

        if (Username == null){
            File[] files = null;
            try {
                files = readFiles();
            } catch (Exception e){

            }
            if (files != null) {
                for (File file : files) {
                    try {
                        file.delete();
                    } catch (Exception e) {

                    }

                }
            }


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Seu nome");
            alertDialog.setMessage("PS: Não tem como mudar depois, bote seu nome mesmo KK");
            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);


            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            generateNoteOnSD("Username.txt",input.getText().toString()+"\n"+"0",0);
                            Username = input.getText().toString();
                        }
                    });
            alertDialog.show();


        }

        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                startActivity(intent);

            }});


        RequestNewGroup();

    }
    public void RequestNewGroup(){
        cardsAdapter.clear();
        lvCards.setAdapter(null);
        File[] file = null;
        try {
            file = readFiles();
        } catch (Exception e) {

        }
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Log.d("Files",file[0].getName());

            for (File name : file){
                db.collection("Groups").document(name.getName()).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Log.d("Files",documentSnapshot.getData().toString());
                                List<String> Users = (List<String>) documentSnapshot.get("user");
                                cardsAdapter.add(new CardModel("Imagem",documentSnapshot.get("nome").toString(),"Criador do grupo " + Users.get(0).toString()," "," "));


                            }
                        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        lvCards.setAdapter(cardsAdapter);
                    }
                });

            }

        } catch (Exception e) {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Add) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Nome do Grupo");
            alertDialog.setMessage("");
            final EditText input = new EditText(MainActivity.this);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {

                            int ano = Calendar.getInstance().getTime().getYear();
                            int mes = Calendar.getInstance().getTime().getMonth();
                            int dia = Calendar.getInstance().getTime().getDay();
                            int hora = Calendar.getInstance().getTime().getHours();
                            int minuto = Calendar.getInstance().getTime().getMinutes();
                            int segundo = Calendar.getInstance().getTime().getSeconds();
                            final String currentTime = String.valueOf(ano) + String.valueOf(mes) + String.valueOf(dia) + String.valueOf(hora) + String.valueOf(minuto) + String.valueOf(segundo);


                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            final Map<String, Object> user = new HashMap<>();
                            List<String> usersnames = new ArrayList<>();
                            usersnames.add(Username);
                            user.put("user",usersnames);
                            user.put("nome", input.getText().toString());

                            Log.d("CARAI5", currentTime);
                            user.put("time", currentTime);
                            if (Username != null) {

                                db.collection("Groups").document(currentTime)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getApplication(), "Grupo Criado !", Toast.LENGTH_SHORT).show();
                                                generateNoteOnSD(currentTime, input.getText().toString() + ".txt", 1);


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        RequestNewGroup();
                                    }
                                });
                            }
                        } });


            alertDialog.show();
        } else if (id == R.id.Entry) {


            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Código do Grupo");
            alertDialog.setMessage("");
            final EditText input = new EditText(MainActivity.this);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {


                            final Map<String, Object> user = new HashMap<>();


                            if (Username != null) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                Task<DocumentSnapshot> rst2 = db.collection("Groups").document(input.getText().toString()).get();
                                rst2.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        List<String> usersnames = (List<String>) task.getResult().get("user");

                                        usersnames.add(Username);
                                        user.put("user",usersnames);
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        Task<Void> rst = db.collection("Groups").document(input.getText().toString()).update(user);
                                        rst.addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                generateNoteOnSD(input.getText().toString(), input.getText().toString() + ".txt", 1);
                                                RequestNewGroup();

                                            }
                                        });



                                    }
                                });



                            }
                        } });


            alertDialog.show();




        }
        return super.onOptionsItemSelected(item);
    }



    public File[] readFiles(){
            String path = Environment.getExternalStorageDirectory().toString() + "/Gruops";
            File directory = new File(path);
            File[] files = directory.listFiles();
            return files;
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





    }



