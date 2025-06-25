package com.example.demosrobotsciente

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.demosrobotsciente.VariableGlobal.miDominioDelaWeb
import com.example.demosrobotsciente.VariableGlobal.miMensajeDeErrorGeneral
import com.example.demosrobotsciente.VariableGlobal.miOkHttpClientQucContieneLaCoockie
import com.example.demosrobotsciente.VariableGlobal.miRespuestaJSON
import com.example.demosrobotsciente.VariableGlobal.miStringDeLaFoto
import com.example.demosrobotsciente.VariableGlobal.pasarDeStringAbase64
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity3 : AppCompatActivity() {
    lateinit var miTextView13controlRobot : TextView;
    lateinit var miButton1arriba : Button;
    lateinit var miButton2derecha : Button;
    lateinit var miButton3abajo : Button;
    lateinit var miButton4izquierda : Button;
    lateinit var miButton5stop : Button;
    lateinit var miTextView2nombreUsuarioActivity3 : TextView;
    lateinit var miTextView3nombreSalaActivity3 : TextView;
    lateinit var miTextView9estado : TextView;
    lateinit var miTextView10idDelRobotCampoActivity3 : TextView;
    lateinit var miTextView11macCampoActivity3 : TextView;
    lateinit var miImageView2 : ImageView;
    lateinit var miTextView14finDelControl : TextView;
    lateinit var miTextView12esAdministradorCampoActivity3 : TextView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main3)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        miButton1arriba = findViewById(R.id.button1arriba);
        miButton2derecha = findViewById(R.id.button2derecha);
        miButton3abajo = findViewById(R.id.button3abajo);
        miButton4izquierda = findViewById(R.id.button4izquierda);
        miButton5stop = findViewById(R.id.button5stop)
        miTextView9estado = findViewById(R.id.textView9estadoDelRobotCampoActivity);
        miTextView10idDelRobotCampoActivity3 = findViewById(R.id.textView10idDelRobotCampoActivity3);
        miTextView2nombreUsuarioActivity3 = findViewById(R.id.textView2nombreUsuarioActivity3);
        miTextView3nombreSalaActivity3 = findViewById(R.id.textView3nombreSalaActivity3);
        miTextView11macCampoActivity3 = findViewById(R.id.textView11macCampoActivity3);
        miTextView12esAdministradorCampoActivity3 = findViewById(R.id.textView12esAdministradorCampoActivity3);
        miTextView13controlRobot = findViewById(R.id.textView13controlRobot);
        miImageView2 = findViewById(R.id.imageView2);
        miTextView14finDelControl = findViewById(R.id.textView14finDelControl);
        miTextView14finDelControl.isVisible = false;

        miTextView2nombreUsuarioActivity3.setText(miRespuestaJSON?.apodoUsuario.toString());
        miTextView3nombreSalaActivity3.setText(miRespuestaJSON?.nombreEvento.toString());
        miTextView9estado.setText(miRespuestaJSON?.estado);
        miTextView10idDelRobotCampoActivity3.setText(miRespuestaJSON?.idRobot.toString());
        miTextView11macCampoActivity3.setText(miRespuestaJSON?.mac.toString());
        miTextView12esAdministradorCampoActivity3.setText(miRespuestaJSON?.correoAdministrador.toString());

        var miBitmapDeLaImagen : Bitmap = pasarDeStringAbase64 (miStringDeLaFoto);
        miImageView2.setImageBitmap(miBitmapDeLaImagen);

        miButton1arriba.setOnClickListener {
            miTextView13controlRobot.setText("Simulando robot avanzando.");
        }
        miButton2derecha.setOnClickListener {
            miTextView13controlRobot.setText("Simulando robot girando a la derecha. ");
        }
        miButton3abajo.setOnClickListener {
            miTextView13controlRobot.setText("Simulando robot retrocediento.");
        }
        miButton4izquierda.setOnClickListener {
            miTextView13controlRobot.setText("Simulando robot girando a la izquierda.");
        }
        miButton5stop.setOnClickListener {
            miTextView13controlRobot.setText("Simulando paranda del robot. ");
        }

        val retrofit = Retrofit.Builder().baseUrl(miDominioDelaWeb + "/aceptarrobot/").addConverterFactory(GsonConverterFactory.create()).client(miOkHttpClientQucContieneLaCoockie).build();
        val miPeticion = PeticionBody (miParametroCodigoQR = miRespuestaJSON?.codigoQR.toString(),  miParametroCorreoElectronicoDelAdministrador = miRespuestaJSON?.correoAdministrador.toString(), miParametroIdRobot = miRespuestaJSON?.idRobot.toString().toInt());


        // esta es la ejecución del hilo secundario.
        lifecycleScope.launch (Dispatchers.IO){
            var miVerdad = false;
            while (miVerdad == false) {
                delay(1000);
                try {
                    println("mainActivity3, lifecycleScope ()--- soy el hilo secundario. ");

                    val apiService = retrofit.create(APIservicio::class.java);
                    val response = apiService.funcion_aceptarRobot(parametroAPIservicioCsrfToken = miRespuestaJSON?.csrfToken.toString(), parametroAPIservicioUrl = miDominioDelaWeb + "/aceptarrobot/".substringBeforeLast("/"), parametroAPIservicioPeticionBody = miPeticion);
                    if (response.isSuccessful) {
                        miRespuestaJSON = response.body();
                    } else {
                        val errorBody = response.errorBody()?.string();
                        val miObjetoGson  = Gson();
                        miRespuestaJSON = miObjetoGson.fromJson(errorBody, FlaskResponse::class.java);
                    }
                } catch (e: Exception) {
                    miMensajeDeErrorGeneral = ("mainActivity3, lifecycleScope ()--- soy el hilo secundario. Error en la ejecución del hilo secundario: " + e +".   Las demostraciones robóticas no están disponibles en este momento.");
                    withContext(Dispatchers.Main) {
                        miVerdad = true;
                        Toast.makeText(this@MainActivity3, miMensajeDeErrorGeneral, Toast.LENGTH_SHORT).show();
                        val miIntent = Intent(this@MainActivity3, MainActivity::class.java);
                        startActivity(miIntent);
                        finish();
                    }
                }
                // esto lo hago porque en el caso de que la bandera "miVerdad" esté en true, eso significa que toda la ejecución de este hilo debe terminar, por lo tanto en el caso de que sea true, no quiero que ejecute más código.
                if (miVerdad == false){
                    if (miRespuestaJSON != null){
                        // este if no tiene else, ya que en caso de que sea else, lo que quiero es que no se ejecuta  ninguna línea de código.
                        if (miRespuestaJSON!!.estado.toString() != "Robot manejando") {
                            miVerdad = true;
                            withContext(Dispatchers.Main){
                                miButton1arriba.isVisible = false;
                                miButton2derecha.isVisible = false;
                                miButton3abajo.isVisible = false;
                                miButton4izquierda.isVisible = false;
                                miButton5stop.isVisible = false;
                                miTextView14finDelControl.isVisible = true;
                                delay(4000);
                            }
                            if (miRespuestaJSON!!.estado.toString() == "Robot Listo"){
                                val miIntent = Intent(this@MainActivity3, MainActivity2::class.java);
                                startActivity(miIntent);
                                finish();
                            }else if (miRespuestaJSON!!.estado.toString() == "error404, evento no encontrado"){
                                miMensajeDeErrorGeneral = "mainActivity3, lifecycleScope ()--- " +miRespuestaJSON!!.estado.toString() + " Escanee el código QR otra vez. ";
                                val miIntent = Intent(this@MainActivity3, MainActivity::class.java);
                                startActivity(miIntent);
                                finish();
                            }else if (miRespuestaJSON!!.estado.toString() == "error500, error en la bae de datos"){
                                miMensajeDeErrorGeneral = "mainActivity3, lifecycleScope ()--- " + miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                                val miIntent = Intent(this@MainActivity3, MainActivity::class.java);
                                startActivity(miIntent);
                                finish();
                            }else if (miRespuestaJSON!!.estado.toString() == "Las demostraciones robóticas no están disponibles, no hay robots para manejar"){
                                miMensajeDeErrorGeneral = "mainActivity3, lifecycleScope ()--- "  +miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                                val miIntent = Intent(this@MainActivity3, MainActivity::class.java);
                                startActivity(miIntent);
                                finish();
                            }else if (miRespuestaJSON!!.estado.toString().startsWith("Esperando por un robot, tiempo de espera para el proximo")){
                                miMensajeDeErrorGeneral = "mainActivity3, lifecycleScope ()--- " + miRespuestaJSON!!.estado.toString();
                                val miIntent = Intent(this@MainActivity3, MainActivity::class.java);
                                startActivity(miIntent);
                                finish();
                            }
                            else{
                                println ("mainActivity3, lifecycleScope ()--- " +  miRespuestaJSON!!.estado.toString());
                                miMensajeDeErrorGeneral = "mainActivity3, lifecycleScope ()---" +"No se reconoce la respuesta del mensaje del servidor. (Aunque el JSON sí ha sido recivido). Las demostraciones robóticas no están disponibles en este momento.";
                                val miIntent = Intent(this@MainActivity3, MainActivity::class.java);
                                startActivity(miIntent);
                                finish();
                            }
                        }
                    }
                }
            }
        }
    }
}