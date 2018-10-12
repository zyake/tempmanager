#!/bin/sh

keytool -genseckey -alias dbpass -storetype PKCS12 -keystore keystore.ks -storepass storepass -keyalg AES -keysize 256
