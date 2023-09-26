# Engelsburg-API

A REST API that evaluates and clearly presents information about the [Engelsburg-Gymnasium Kassel](https://engelsburg.smmp.de), mostly used by the Engelsburg-Planer Flutter app, which the API can also send notifications via [FCM](https://firebase.google.com/docs/cloud-messaging) to.

## Run
 - Clone the repository
 - Check for the correct image tag
 - Fill out missing environment variables
 - Uncomment Watchtower for automatic updates
 - Run the following command

```Shell
docker compose up -d
```

## Environment


| Environment Variable       | Description                                                                                                        |
|----------------------------|--------------------------------------------------------------------------------------------------------------------|
| SCHOOL_TOKEN               | Token given by the school for the substitutes                                                                      |
| GOOGLE_ACCOUNT_CREDENTIALS | Google account credentials to use FCM                                                                              |
| PRODUCTION                 | If set to false no notifications will be send and no images will be loaded to create the blurhash for each article | 
| BLURHASH                   | If true generate blurhash for articles                                                                             |

### SSL Settings

It is highly recommended to use *HTTPS* via *SSL*:

| Environment Variable      | Description                          |
|---------------------------|--------------------------------------|
| SSL_ENABLED               | Enable https and ssl                 |
| SSL_KEYSTORE              | SSL-Keystore file                    |
| SSL_KEYSTORE_PASSWORD     | SSL-Keystore password                | 
| SSL_KEYSTORE_KEY_PASSWORD | Password to access Keystore password |

### Automatic issue SSL-Certificate

To enable automatic SSL-Certificate-Issuing you need to set following environment variables:

| Environment Variable        | Description                        |
|-----------------------------|------------------------------------|
| lets-encrypt-helper.domain  | Domain(s) to issue certificate for |
| lets-encrypt-helper.contact | Contact email to issue certificate |

## Endpoint Documentation

We use Swagger to document our endpoints. You can visit [Swagger.io](https://editor.swagger.io/) and
import [endpoints.yaml](.docs/endpoints.yaml) on any branch, but you can also
click [here](https://editor.swagger.io/?url=https://raw.githubusercontent.com/engelsburg/engelsburg-api/main/.docs/endpoints.yaml)
to view the endpoint documentation of the current version.

## Copyright notice

Copyright (c) 2019-2023 Paul Huerkamp. All rights reserved.
