import paho.mqtt.client as paho
from paho import mqtt
import json
import requests


## Installation Seriennummer
installation = "123456789"
password = "testing321"
token = ""
backendurl = "http://localhost:8080/api"


# Environment variables
broker = "localhost"
port = 1883

def getConfig():
    print("Getting configuration from backend")
    r = requests.get(backendurl + "/installations/" + installation + "/configuration")
    if r.status_code == 200:
        return r.text
    else:
        print("Error while getting configuration from backend")
        return None





def on_connect(client, userdata, flags, rc, properties=None):
    client.subscribe("installations/" + installation, qos=1)

def on_subscribe(client, userdata, mid, granted_qos, properties=None):
    print("Subscribed to topic! installations/" + installation )

# print message, useful for checking if it was successful
def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.qos) + " " + str(msg.payload))

    # parse payload as json
    eventMsg = json.loads(msg.payload)
    if eventMsg["event"] == "newConfiguration":
        print("Should update configuration")
        config = getConfig()
        if config is not None:
            print("Got configuration from backend")
            print(config)


def connect():
    # using MQTT version 5 here, for 3.1.1: MQTTv311, 3.1: MQTTv31
    # userdata is user defined data of any type, updated by user_data_set()
    # client_id is the given name of the client
    client = paho.Client(client_id="pseudoInstallation")
    client.on_connect = on_connect

    # enable TLS for secure connection => for local development disable
    # client.tls_set(tls_version=mqtt.client.ssl.PROTOCOL_TLS)
    # set username and password
    client.username_pw_set(installation, password)
    
    # Connect read only
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