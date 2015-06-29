package hackwu.wuhack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.Authenticator;


public class MainActivity extends ActionBarActivity
{
    Spinner spinnerOptions, spinnerAll;
    Button btSearch;
    WebView wvLessons;
    String uname = "";
    char[] password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerOptions = (Spinner) findViewById(R.id.spinnerOption);
        spinnerAll = (Spinner) findViewById(R.id.spinnerAll);
        btSearch = (Button) findViewById(R.id.btSearch);
        wvLessons = (WebView) findViewById(R.id.wvLessons);

        ArrayAdapter<CharSequence> adapterOptions = ArrayAdapter.createFromResource(this,
                R.array.spinnerOptionsArray, android.R.layout.simple_spinner_dropdown_item);
        adapterOptions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapterOptions);

        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                if (id == 0)
                {
                    ArrayAdapter<CharSequence> adapterKlassen = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.spinnerKlassenArray, R.layout.spinner_item);
                    adapterKlassen.setDropDownViewResource(R.layout.spinner_item);
                    spinnerAll.setAdapter(adapterKlassen);

                } else if (id == 1)
                {
                    ArrayAdapter<CharSequence> adapterLehrer = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.spinnerTeacherArray, R.layout.spinner_item);
                    adapterLehrer.setDropDownViewResource(R.layout.spinner_item);
                    spinnerAll.setAdapter(adapterLehrer);
                } else if (id == 2)
                {
                    ArrayAdapter<CharSequence> adapterRaum = ArrayAdapter.createFromResource(getApplicationContext(),
                            R.array.spinnerRaumArray, R.layout.spinner_item);
                    adapterRaum.setDropDownViewResource(R.layout.spinner_item);
                    spinnerAll.setAdapter(adapterRaum);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });



        btSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LayoutInflater li = LayoutInflater.from(getApplicationContext());
                View alertDialogView = li.inflate(R.layout.alertdialog, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getApplicationContext());

                alertDialogBuilder.setView(alertDialogView);

                final EditText username = (EditText) findViewById(R.id.username);
                final EditText passwd = (EditText) findViewById(R.id.password);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        uname = username.getText().toString();
                                        password = passwd.getText().toString().toCharArray();
                                        System.out.println(uname);
                                        System.out.println(password);
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                if (!uname.equals("") && !(password.length == 0))
                {
                    Authenticator.setDefault(new AuthenticatorTest(uname, password));

                } else
                {
                    Toast.makeText(getApplicationContext(), "Please login", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }
}
