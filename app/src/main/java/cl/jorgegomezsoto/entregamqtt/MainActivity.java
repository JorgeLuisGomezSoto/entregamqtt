package cl.jorgegomezsoto.entregamqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {
    private MqttAndroidClient mqttclienteandroid;
    private final MemoryPersistence persistenciadememoria = new MemoryPersistence();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MqttAndroidClient cliente2 = new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.emqx.io:1883", "Jorgeg", persistenciadememoria);
        cliente2.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection was lost!");
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Message Arrived!: " + topic + ": " + new String(message.getPayload()));
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Delivery Complete!");
            }
        });
        MqttConnectOptions conexion1 = new MqttConnectOptions();
        conexion1.setCleanSession(true);
        try {
            cliente2.connect(conexion1, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Connection Success!");
                    try {
                        System.out.println("Subscribing to Central/Ventas/Ropa");
                        cliente2.subscribe("Central/Ventas/Ropa", 0);
                        System.out.println("Subscribed to Central/Ventas/Ropa");
                        System.out.println("Se esta realizando una venta");
                        cliente2.publish("Central/Ventas/Ropa", new MqttMessage("Has vendido una prenda".getBytes()));
                    }catch (MqttException ex) {
                        System.out.println(ex.toString());
                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Error en la central, por favor vuelva a intentarlo más tarde");
                    System.out.println("throwable: " + exception.toString());
                }
            });
        } catch (MqttException ex) {
            System.out.println(ex.toString());
        }
    }
}
