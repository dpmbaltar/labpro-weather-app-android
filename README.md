# labpro-weather-app-android

## Empezar

Antes de iniciar la app, se debe iniciar el servidor de la API:
[labpro-weather-api](https://github.com/dpmbaltar/labpro-weather-api)

Luego:

1. Abrir el proyecto `labpro-weather-app-android` con Android Studio
2. Abrir el archivo `Constants.kt` del paquete `com.example.weatherapp.util`,
   ubicado en `app/src/main/java/com/example/weatherapp/util`
3. Cambiar el valor de la constante API_BASE_URL según el ip del servidor de la
   API. Por ejemplo:

```
const val API_BASE_URL = "http://192.168.0.239:9000/"
```

4. Incluir la ip del servidor en la configuración de red de la app en el archivo
   `network_security_config.xml` ubicado en `app/src/main/res/xml` agregando
   `<domain includeSubdomains="true">192.168.0.239</domain>` en `domain-config`.
   Ejemplo del archivo completo:

```
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <base-config>
        <trust-anchors>
            <certificates src="system"/>
        </trust-anchors>
    </base-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">192.168.0.239</domain>
    </domain-config>
</network-security-config>
```

5. Al ejecutar la app la primera vez se solicita permisos para obtener la
   ubicación; deben aceptarse para que funcione, de lo contrario se cierra.


Observaciones: para que funcione en el emulador de Android Studio, se debe establecer la ubicación
desde las opciones del dispositivo (botón con 3 puntos) una vez iniciado, y se deben activar los
servicios de Google Play (probado en API 30+).