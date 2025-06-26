package com.example.demosrobotsciente

// este código se usa para almcenar la cookie en una variable global para que despues en cada petición esta variable sea utilizada.

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

class PersistentCookieJar : CookieJar{
    private val cookieStore = ConcurrentHashMap<String, List<Cookie>>();


    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        println ("PersistentCookieJar, saveFromResponse() --- ");
        cookieStore[url.host] = cookies;
        //println("PersistentCookieJar(), saveFromResponse()---  === Cookies guardadas para ${url.host} ===");
        //cookies.forEach { cookie ->
        //    println("Nombre: ${cookie.name}, Valor: ${cookie.value}")
        //    println("Dominio: ${cookie.domain}, Path: ${cookie.path}")
        //    println("Expira: ${cookie.expiresAt}, Seguro: ${cookie.secure}")
         //   println("----------------------------------")
        //}
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        println ("PersistentCookieJar, loadForRequest() --- ");
        // Recupera cookies para el dominio de la petición
        val cookies = cookieStore[url.host] ?: emptyList();
        //println("PersistentCookieJar(), loadForRequest()--- === Cookies enviadas a ${url.host} ===");
        //cookies.forEach { cookie ->
        //    println("Nombre: ${cookie.name}, Valor: ${cookie.value}")
        //}
        //println("PersistentCookieJar(), loadForRequest()--- fin de la impresión de las coockies. ");
        return cookies;
    }
}