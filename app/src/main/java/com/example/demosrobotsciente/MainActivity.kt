package com.example.demosrobotsciente

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.demosrobotsciente.VariableGlobal.miDominioDelaWeb
import com.example.demosrobotsciente.VariableGlobal.miEndpointAlQueApuntoEnActivity
import com.example.demosrobotsciente.VariableGlobal.miMensajeDeErrorGeneral
import com.example.demosrobotsciente.VariableGlobal.miRespuestaJSON
import com.example.demosrobotsciente.VariableGlobal.miOkHttpClientQucContieneLaCoockie
import com.google.gson.Gson
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

//jhonjames: Este mensaje de aviso está en las dos aplicaciones, la cliente (Kotlin) y la servidor (Python). Tener cuidado porque en el caso de que no se envíe desde el cliente al servidor en los métodos POST
// e CSRF token  en la cabezera, entonces el servidor no va a aceptar ni si quiera la petición, es decir, que la terminal que imprime los PRINT de Python, no va sacar el mensaje de que la petición ha sido POST,
// sino que lo único que va a pasar es que va a salir un PRINT en Kotlin que dice que  <!doctype html> <html lang=en> <title>400 Bad Request</title> <p>The CSRF token is missing.</p> y no habra manera
// en el servidor que yo me de cuenta de esto, porque recordar que en este caso los PRINT del servidor ¡¡NO salen!!


object VariableGlobal{
    var miRespuestaJSON : FlaskResponse? = null;
    lateinit var miDominioDelaWeb : String;
    // esta variable la declaro global porque necesito que se almacene cada vez que accedo a esta activity, es decir, cuando se abra este activity desde Activity2 o Activity3, necesito que esta variable no esté vacia.
    // lo cual me hace pensar que lo que podria hacer es dejarla incicalizad en las varibales locales de este activity para que de esta forma no haga falta almacenarla en una variable Global la cual va a ocupar espacio
    // en memoria durante la ejecución de toda la aplicación. Pero lo que pasa es que realmente esta variable no puedo dejarla inicializada porque nunca va a tener el mismo valor, es decir, depende del nombre del evento
    // esta variable va a almacenar una cadena u otra, de tal forma que como los nombres de evento son infinitos, nunca la voy a poder dejar fija. Esto contrata con los otros dos endpoint que son POST, los cules sus
    // URN sí ue son estáticas, es decir siempre se van a llamar /aceptarrobot y /rechazarrobot
    lateinit var miEndpointAlQueApuntoEnActivity :  String;
    var miOkHttpClientQucContieneLaCoockie : OkHttpClient? = null;
    var miStringDeLaFoto : String? = null;
    var miMensajeDeErrorGeneral : String = "/";

    fun pasarDeStringAbase64 (parametroString : String?) : Bitmap {
        //println ("MainActivity2, pasarDeStringAbase64()---este es el string: ");
        //println (parametroString);
        // en esta primera parte convierto el String a una lista de bits.
        var misBytesDeLaFoto = Base64.decode(parametroString, Base64.DEFAULT);
        // En este segunda parte paso la lista de String a Bitmap.
        var miBitMap = BitmapFactory.decodeByteArray(misBytesDeLaFoto, 0, misBytesDeLaFoto.size);
        return  miBitMap;
    }
}

class MainActivity : AppCompatActivity() {
    lateinit var miButton1leercodigoqr : Button;
    lateinit var miButton2solicirarrobot : Button;
    lateinit var miTextView2direccionip : TextView;
    lateinit  var direccionURLleidaDesdeElQR : String;
    lateinit var miTextView3MensajeAviso : TextView;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        miButton1leercodigoqr = findViewById(R.id.button1leercodigoqr);
        miButton2solicirarrobot = findViewById(R.id.button2solicirarrobot);
        miTextView2direccionip = findViewById(R.id.textView2direccionip);
        miTextView3MensajeAviso = findViewById(R.id.textView3mensajeAviso);

        miTextView3MensajeAviso.setText(miMensajeDeErrorGeneral);

        if (miMensajeDeErrorGeneral == "/"){
            miButton2solicirarrobot.isEnabled = false;
        }

        miButton1leercodigoqr.setOnClickListener {
            println ("MainActivity, miButton1leercodigoqr.setOnClickListener()--- se ha pulsado. ");
            //miTextView2direccionip.setText("http://192.168.1.129:5000/demostracionesroboticas/miQR1/");
            //direccionURLleidaDesdeElQR = "http://192.168.1.129:5000/demostracionesroboticas/miQR1/";
            //miTextView2direccionip.setText("http://192.168.1.129:5000/demostracionesroboticas/miQR1/jhon@gmail.com/");
            //direccionURLleidaDesdeElQR = "http://192.168.1.129:5000/demostracionesroboticas/miQR1/jhon@gmail.com/";

            scanearCodigo ();
            println ("MainActivity, miButton1leercodigoqr.setOnClickListener()--- fin del escaneo. ");
        }

        miButton2solicirarrobot.setOnClickListener {
            println ("MainActivity, miButton2solicirarrobot.setOnClickListener()--- se ha pulsado. ");
            buscarInternet (miDominioDelaWeb + miEndpointAlQueApuntoEnActivity);
        }
    }

    // con esta función lo que hago es conseguir solamente el dominio, para almacenarlo en una variable global, para que lo puedan utilizar las demás activities en sus peticiones.
    fun conseguirDominio (url: String): String{
        var miObjetoURL = URL (url);
        return miObjetoURL.protocol +"://"  +miObjetoURL.host +":" +miObjetoURL.port;
    }

    fun conseguirEndpoint (url: String): String{
        var miObjetURL = URL (url);
        return miObjetURL.path;
    }

    fun buscarInternet (url : String){
        println("buscarInternet()--- se ejecuta con la URL: $url");
        val okHttpClient = OkHttpClient.Builder().cookieJar(PersistentCookieJar()).build();
        val retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient).build();
        //val retrofit = Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        runBlocking {
            try{
                val apiService = retrofit.create(APIservicio::class.java);
                // en este momento que es cuando se hace el GET, es cuando se almacena la coockie, de tal forma que el código se va a ejecutar la clase PersistentCookieJar.
                val response = apiService.funcion_registrarse(url.substringBeforeLast("/"));

                //  en el caso de que la respuesta halla fallado, imprimo el código de respuesta para saber que ha fallado.
                if (response.isSuccessful) {
                    miRespuestaJSON = response.body();
                }else{
                    val errorBody = response.errorBody()?.string();
                    // este objeto lo uso para pasar de la clase String a la clase FlaskResponse (creada por mi) para que de esta forma yo pueda obtener el JSON que envía el servidor de Flask. Fijarse que
                    // en lineas más arriba, este en concreto, miRespuestaJSON = response.body();  no hace falta invocar a esta clase, porque el body es un JSON por defecto, lo que no entiendo es porque
                    // en las librerías de retrofit (que es lo que estoy usando para comunicarme con Flask) no se haya hecho de le misma forma  body que errorBody.
                    val miObjetoGson  = Gson();
                    miRespuestaJSON = miObjetoGson.fromJson(errorBody, FlaskResponse::class.java);
                }
            }catch (e : Exception){
                miMensajeDeErrorGeneral = "MainActivity.buscarInternet()---error al recibir el mensaje: " + e;
                miTextView3MensajeAviso.setText(miMensajeDeErrorGeneral);
            }
        }
        // este if no tiene else porque en el caso de que la conexión falle, miRespuestaJSON será entonces null y no hay que ejecutar más lógica ya que en ese caso lo único que quiero es que se me muestre el mensaje
        // de error por pantalla.
        if (miRespuestaJSON != null) {
            //println ("MainActivity, buscarInternet()--- aquí voy a imprimir la coockie. ");
            //println (okHttpClient.cookieJar.toString());
            miOkHttpClientQucContieneLaCoockie = okHttpClient;
            if (miRespuestaJSON!!.estado.toString() == "Robot Listo"){
                miMensajeDeErrorGeneral = "//";
                val miIntent = Intent(this, MainActivity2::class.java);
                startActivity(miIntent);
            }else if (miRespuestaJSON!!.estado.toString() == "error404, evento no encontrado"){
                miMensajeDeErrorGeneral ="MainActivity.buscarInternet()---" +miRespuestaJSON!!.estado.toString() + " Escanee el código QR otra vez. ";
                miTextView3MensajeAviso.setText(miMensajeDeErrorGeneral);
            }else if (miRespuestaJSON!!.estado.toString() == "error500, error en la bae de datos"){
                miMensajeDeErrorGeneral = "MainActivity.buscarInternet()---" + miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                miTextView3MensajeAviso.setText(miMensajeDeErrorGeneral);
            }else if (miRespuestaJSON!!.estado.toString() == "Las demostraciones robóticas no están disponibles, no hay robots para manejar"){
                miMensajeDeErrorGeneral = "MainActivity.buscarInternet()---" + miRespuestaJSON!!.estado.toString() + " Las demostraciones robóticas no están disponibles en este momento.";
                miTextView3MensajeAviso.setText(miMensajeDeErrorGeneral);
            }else if (miRespuestaJSON!!.estado.toString().startsWith("Esperando por un robot, tiempo de espera para el proximo")){
                miMensajeDeErrorGeneral = "MainActivity.buscarInternet()---" + miRespuestaJSON!!.estado.toString()
                miTextView3MensajeAviso.setText(miMensajeDeErrorGeneral);
            }else{
                miMensajeDeErrorGeneral = "MainActivity.buscarInternet()---" + "No se reconoce la respuesta del mensaje del servidor. (Aunque el JSON sí ha sido recivido). Las demostraciones robóticas no están disponibles en este momento.";
                miTextView3MensajeAviso.setText (miMensajeDeErrorGeneral);
            }
        }
    }
    fun scanearCodigo (){
        var miOptions = ScanOptions();
        miOptions.setBeepEnabled(true);
        miOptions.setOrientationLocked(true);
        miOptions.setCaptureActivity(Capturar::class.java);
        barraLanzadora.launch(miOptions);
    }

    private val barraLanzadora = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            direccionURLleidaDesdeElQR = result.contents;
            direccionURLleidaDesdeElQR = direccionURLleidaDesdeElQR + "/";
            if (direccionURLleidaDesdeElQR.length < 40){
                miMensajeDeErrorGeneral = "Error al leer el código QR. ";
            }else{
                miButton2solicirarrobot.isEnabled = true;
                miTextView2direccionip.setText(direccionURLleidaDesdeElQR);
                // con esto sólo consigo http://localhost:5000, es decir el dominio donde estámontado el servidor. Pero cuidado porque no me quedo con la barra final /.
                miDominioDelaWeb = conseguirDominio (direccionURLleidaDesdeElQR);
                miEndpointAlQueApuntoEnActivity = conseguirEndpoint(direccionURLleidaDesdeElQR);
            }
        }
    }
}