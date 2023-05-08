import paho.mqtt.client as paho
from paho import mqtt

## Installation Seriennummer
installation = "123456789"
username = ""
password = ""


# Environment variables
broker = "6285dd2901794a3ba0a4a03d1823cf50.s2.eu.hivemq.cloud"
port = 8883




def on_connect(client, userdata, flags, rc, properties=None):
    client.subscribe("installation/" + installation, qos=1)

def on_subscribe(client, userdata, mid, granted_qos, properties=None):
    print("Subscribed to topic! installation/" + installation )

# print message, useful for checking if it was successful
def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.qos) + " " + str(msg.payload))

def connect():
    # using MQTT version 5 here, for 3.1.1: MQTTv311, 3.1: MQTTv31
    # userdata is user defined data of any type, updated by user_data_set()
    # client_id is the given name of the client
    client = paho.Client(client_id="pseudoInstallation", userdata=None, protocol=paho.MQTTv311)
    client.on_connect = on_connect

    # enable TLS for secure connection
    client.tls_set(tls_version=mqtt.client.ssl.PROTOCOL_TLS)
    # set username and password
    client.username_pw_set(username, password)
    # connect to HiveMQ Cloud on port 8883 (default for MQTT)
    client.connect(broker, port)

    # setting callbacks, use separate functions like above for better visibility
    client.on_subscribe = on_subscribe
    client.on_message = on_message


    client.loop_forever()

def main():
    print('Starting the client')
    connect()

if __name__ == '__main__':
    main()