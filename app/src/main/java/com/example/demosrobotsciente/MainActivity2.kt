package com.example.demosrobotsciente

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demosrobotsciente.MainActivity3
import com.example.demosrobotsciente.VariableGlobal.miDominioDelaWeb
import com.example.demosrobotsciente.VariableGlobal.miMensajeDeErrorGeneral
import com.example.demosrobotsciente.VariableGlobal.miOkHttpClientQucContieneLaCoockie
import com.example.demosrobotsciente.VariableGlobal.miRespuestaJSON
import com.example.demosrobotsciente.VariableGlobal.miStringDeLaFoto
import com.example.demosrobotsciente.VariableGlobal.pasarDeStringAbase64
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity2 : AppCompatActivity() {
    lateinit var miButton1aceptarRobot : Button;
    lateinit var miButton2rechazarRobot : Button;
    lateinit var miTextView2nombreUsuario : TextView;
    lateinit var miTextView3nombreSala : TextView;
    lateinit var miTextView9estadoDelRobotCampo : TextView;
    lateinit var miTextView10idDelRobotCampo : TextView;
    lateinit var miTextView11macCampo : TextView;
    lateinit var miTextView12esAdministradorCampo : TextView;

    lateinit var miImageViewFotoDelRobot : ImageView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        miTextView2nombreUsuario = findViewById(R.id.textView2nombreUsuario);
        miTextView3nombreSala = findViewById(R.id.textView3nombreSala);
        miTextView9estadoDelRobotCampo = findViewById(R.id.textView9estadoDelRobotCampo);
        miTextView10idDelRobotCampo = findViewById(R.id.textView10idDelRobotCampo);
        miTextView11macCampo = findViewById(R.id.textView11macCampo);
        miTextView12esAdministradorCampo = findViewById(R.id.textView12esAdministradorCampo);
        miImageViewFotoDelRobot = findViewById(R.id.imageView);
        miButton1aceptarRobot = findViewById(R.id.button1aceptarRobot);
        miButton2rechazarRobot = findViewById(R.id.button2rechazarRobot);

        miTextView2nombreUsuario.setText(miRespuestaJSON?.apodoUsuario.toString());
        miTextView3nombreSala.setText(miRespuestaJSON?.nombreEvento.toString());
        miTextView9estadoDelRobotCampo.setText(miRespuestaJSON?.estado.toString());
        miTextView10idDelRobotCampo.setText(miRespuestaJSON?.idRobot.toString());
        miTextView11macCampo.setText(miRespuestaJSON?.mac.toString());
        miTextView12esAdministradorCampo.setText(miRespuestaJSON?.correoAdministrador.toString());
        miStringDeLaFoto = miRespuestaJSON?.fotoDelRobot.toString();
        var miBitmapDeLaImagen : Bitmap = pasarDeStringAbase64 (miStringDeLaFoto);
        miImageViewFotoDelRobot.setImageBitmap(miBitmapDeLaImagen);

        miButton1aceptarRobot.setOnClickListener {
            println ("miButton1aceptarRobot()---");
            buscarInternetAceptarRobot (miDominioDelaWeb + "/aceptarrobot/");
        }
        miButton2rechazarRobot.setOnClickListener {
            println ("miButton2rechazarRobot()---");
            buscarInternetRechazarRobot (miDominioDelaWeb + "/rechazarrobot/");
        }
    }

    fun buscarInternetAceptarRobot (url : String){
        val retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).client(miOkHttpClientQucContieneLaCoockie).build();
        //val retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        val miPeticion = PeticionBody (miParametroCodigoQR = miRespuestaJSON?.codigoQR.toString(),  miParametroCorreoElectronicoDelAdministrador = miRespuestaJSON?.correoAdministrador.toString(), miParametroIdRobot = miRespuestaJSON?.idRobot.toString().toInt());

        runBlocking {
            try{
                val apiService = retrofit.create(APIservicio::class.java)
                val response = apiService.funcion_aceptarRobot(parametroAPIservicioCsrfToken = miRespuestaJSON?.csrfToken.toString(), parametroAPIservicioUrl = url.substringBeforeLast("/"), parametroAPIservicioPeticionBody =  miPeticion);
                if (response.isSuccessful){
                    miRespuestaJSON = response.body();
                }else{
                    val errorBody = response.errorBody()?.string();
                    /// jhonjames: este es el print con el que descubrí que en cada petición POST, se necesitaba la cookie, ya que si no se tiene la coockie, el servidor de flask ni si quiera reacciona, para probar que ese era el
                    // problema, hice la misma petición POST desde postman, sólo que eliminando la coockie, y el error que se obtiene en POSTMAN, es el mismo:  <!doctype html> <html lang=en> <title>400 Bad Request</title>
                    //  <h1>Bad Request</h1>  <p>The CSRF session token is missing.</p>
                    println  ("MainActivity2, buscarInternetAceptarRobot()--- este es el error boty: " + errorBody);
                    println  ("MainActivity2, buscarInternetAceptarRobot()--- error codigo:  " + response.code());
                    val miObjetoGson  = Gson();
                    miRespuestaJSON = miObjetoGson.fromJson(errorBody, FlaskResponse::class.java);
                }
            } catch (e: Exception){
                miMensajeDeErrorGeneral = ("MainActivity2, buscarInternetAceptarRobot()--- error al recibir el mensaje: "  + e);
                val miIntent = Intent(this@MainActivity2, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }
        }
        if (miRespuestaJSON != null){
            println("buscarInternetAceptarRobot()--- sí que se ha obtenido respuesta del servidor. ");
            if (miRespuestaJSON!!.estado.toString() == "Robot manejando") {
                val miIntent = Intent(this, MainActivity3::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString() == "error404, evento no encontrado"){
                miMensajeDeErrorGeneral ="MainActivity2, buscarInternetAceptarRobot()---  " +miRespuestaJSON!!.estado.toString() + " Escanee el código QR otra vez. ";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString() == "error500, error en la bae de datos"){
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetAceptarRobot()---  " + miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString() =="MainActivity2, buscarInternetAceptarRobot()---  " + "Las demostraciones robóticas no están disponibles, no hay robots para manejar"){
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetAceptarRobot()---  " + miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString().startsWith("Esperando por un robot, tiempo de espera para el proximo")){
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetAceptarRobot()---  " + miRespuestaJSON!!.estado.toString();
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString() == "Robot Listo"){
                miTextView2nombreUsuario.setText(miRespuestaJSON?.apodoUsuario.toString());
                miTextView3nombreSala.setText(miRespuestaJSON?.nombreEvento.toString());
                miTextView9estadoDelRobotCampo.setText(miRespuestaJSON?.estado.toString());
                miTextView10idDelRobotCampo.setText(miRespuestaJSON?.idRobot.toString());
                miTextView11macCampo.setText(miRespuestaJSON?.mac.toString());
                miTextView12esAdministradorCampo.setText(miRespuestaJSON?.correoAdministrador.toString());
                miStringDeLaFoto = miRespuestaJSON?.fotoDelRobot.toString();
                var miBitmapDeLaImagen : Bitmap = pasarDeStringAbase64 (miStringDeLaFoto);
                miImageViewFotoDelRobot.setImageBitmap(miBitmapDeLaImagen);
            }else{
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetAceptarRobot()---  " +"No se reconoce la respuesta del mensaje del servidor. (Aunque el JSON sí ha sido recivido). Las demostraciones robóticas no están disponibles en este momento.";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }
        }
    }

    fun buscarInternetRechazarRobot (url: String){
        println("buscarInternetRechazarRobot()--- se ejecuta con la URL: $url");
        val retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).client(miOkHttpClientQucContieneLaCoockie).build();
        val miPeticion = PeticionBody (miParametroCodigoQR = miRespuestaJSON?.codigoQR.toString(),  miParametroCorreoElectronicoDelAdministrador = miRespuestaJSON?.correoAdministrador.toString(), miParametroIdRobot = miRespuestaJSON?.idRobot.toString().toInt());
        runBlocking {
            try{
                val apiService = retrofit.create(APIservicio::class.java);
                val response = apiService.funcion_rechazarRobot(parametroAPIservicioCsrfToken = miRespuestaJSON?.csrfToken.toString(), parametroAPIservicioUrl = url.substringBeforeLast("/"), parametroAPIservicioPeticionBody =  miPeticion);
                if (response.isSuccessful){
                    miRespuestaJSON = response.body();
                    println("buscarInternetRechazarRobot()--- mensaje recibido");
                    println(miRespuestaJSON);
                }else{
                    val errorBody = response.errorBody()?.string();
                    val miObjetoGson  = Gson();
                    miRespuestaJSON = miObjetoGson.fromJson(errorBody, FlaskResponse::class.java);
                }
            }catch (e: Exception){
                miMensajeDeErrorGeneral = ("MainActivity2, buscarInternetRechazarRobot()--- error al recibir el mensaje: "  + e);
                val miIntent = Intent(this@MainActivity2, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }
        }

        // recordar que en el caso de que le petición falle (el anterior try-catch) y el código se haya ido al catch, entonces miRespuestaJSON va a ser null y estas lineas de abajo no se van a ejecutar.
        if (miRespuestaJSON != null){
            if (miRespuestaJSON!!.estado.toString() == "Robot Listo"){
                miTextView2nombreUsuario.setText(miRespuestaJSON?.apodoUsuario.toString());
                miTextView3nombreSala.setText(miRespuestaJSON?.nombreEvento.toString());
                miTextView9estadoDelRobotCampo.setText(miRespuestaJSON?.estado.toString());
                miTextView10idDelRobotCampo.setText(miRespuestaJSON?.idRobot.toString());
                miTextView11macCampo.setText(miRespuestaJSON?.mac.toString());
                miTextView12esAdministradorCampo.setText(miRespuestaJSON?.correoAdministrador.toString());
                miStringDeLaFoto = miRespuestaJSON?.fotoDelRobot.toString();
                var miBitmapDeLaImagen : Bitmap = pasarDeStringAbase64 (miStringDeLaFoto);
                miImageViewFotoDelRobot.setImageBitmap(miBitmapDeLaImagen);
                // este else if:  miRespuestaJSON!!.estado.toString() == "Robot manejando"  me parecería muy raro que se llegara a ejecuar, pero lo voy a dejar por si acaso para las veces en las que por lo que sea Kotlin caiga en un error, de que
                // por ejemplo se pulse el botón atras (Recordar que tengo una funcionalidad para que esto no esté permitido, ya que la activity se destruye).  y que se le muestre las opciones de rechazar o de aceptar estando el robot controlándose.
            }else if (miRespuestaJSON!!.estado.toString() == "Robot manejando"){
                val miIntent = Intent(this, MainActivity3::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString() == "error404, evento no encontrado"){
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetRechazarRobot()--- " +miRespuestaJSON!!.estado.toString() + " Escanee el código QR otra vez. ";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString() == "error500, error en la bae de datos"){
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetRechazarRobot()--- " + miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString() == "Las demostraciones robóticas no están disponibles, no hay robots para manejar"){
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetRechazarRobot()--- "  +miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }else if (miRespuestaJSON!!.estado.toString().startsWith("Esperando por un robot, tiempo de espera para el proximo")){
                miMensajeDeErrorGeneral = "MainActivity2, buscarInternetRechazarRobot()--- " + miRespuestaJSON!!.estado.toString();
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }
            else{
                miMensajeDeErrorGeneral ="MainActivity2, buscarInternetRechazarRobot()--- " + "No se reconoce la respuesta del mensaje del servidor. (Aunque el JSON sí ha sido recivido). Las demostraciones robóticas no están disponibles en este momento.";
                val miIntent = Intent(this, MainActivity::class.java);
                startActivity(miIntent);
                finish();
            }
        }
    }
}