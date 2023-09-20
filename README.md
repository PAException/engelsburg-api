# Engelsburg-API

A REST API that evaluates and clearly presents information about the [Engelsburg-Gymnasium Kassel](https://engelsburg.smmp.de), mostly used by the Engelsburg-Planer Flutter app, which the API can also send notifications via [FCM](https://firebase.google.com/docs/cloud-messaging) to.

## Run
 - Clone the repository
 - Check for the correct image tag
 - Fill out missing enviromental variables
 - Uncomment Watchtower for automatic updates
 - Run the following command

```Shell
docker compose up -d
```



Unofficial API of the Engelsburg-Gymnasium-Kassel.

## Structure

The Engelsburg-API can be divided into two sections.

### Public

The first section ist completely public. It includes unprotected resources of
the [Engelsburg-Website] like events and articles.

### Private

The last section is private. That means it can only be accessed with a created account on the Engelsburg-API and
specific endpoints can't be accessed if the account lacks of the required scope.

### Authorization flow

Every section has a different authorization flow.

#### Public

*There is no authorization flow for this section.*

#### Private

SignUp

First of you have to register with a `POST` request at `/auth/signup`. Parameter needed in the request body are `email`
, `password` and `schoolToken`. The two first ones should be clear. `schoolToken` is a token given by the
**Engelsburg-Gymnasium-Kassel** to identify students. (At the moment it's the password for the substitutes)
An email will be sent to verify the created account. If the account doesn't get verified, there can't be sent any
requests to the private part of the api.

---

Login

To login send a `GET` request with `email` and `password` parameter in the request body to `/auth/login`. If email and
password are correct, and the account is verified, the request will include a refresh token. To obtain the Access-Token
(*Json-Web-Token*) send the refresh token as request parameter to `/auth/refresh`. The response body will contain the
JWT as token and a new refresh as refreshToken. The refresh token is needed to obtain a new JWT if the current expires.

---

Resource requests

To send a request to a resource of the Private-API-Section you have to include the `token` from the login request in
the `Authorization` header field of the request. The API will check if you have the permission to access that resource.
The token will expire after 30 minutes. To obtain a new token send another login request.

---

## Environment

This project needs several environment variables:

| Environment Variable| Description                            |
|---------------------|----------------------------------------|
| SERVER_PORT         | The port the server is running on      |
| SCHOOL_TOKEN        | Token given by the school for the substitutes|
| GOOGLE_ACCOUNT_CREDENTIALS| Google account credentials for firebase cloud messaging|
| PRODUCTION          | Production mode to actually send notifications and more|
| SSL_ENABLED         | Enable https and ssl                   |

If you want to enable https and ssl (use.ssl=true) which is highly recommended you also need following environment
variables:

| Environment Variable   | Description                            |
|------------------------|----------------------------------------|
| ssl.key.password       | SSL-Key password                       |
| ssl.key.store          | SSL-Keystore                           | 
| ssl.key.store.password | SSL-Keystore password                  |

## Endpoint Documentation

We use Swagger to document our endpoints. You can visit [Swagger.io](https://editor.swagger.io/) and
import [endpoints.yaml](.docs/endpoints.yaml) on any branch, but you can also
click [here](https://editor.swagger.io/?url=https://raw.githubusercontent.com/engelsburg/engelsburg-api/master/.docs/endpoints.yaml)
to view the endpoint documentation of the current version.

## Contributing

You are welcome to contribute this project. Found a bug? Do you have a feature request? You can create an issue, we'll
have a look at it.

Do you want it to implement by yourself? Fix a bug by yourself? Just fork this repository and start coding. Afterwards
open a pull request which will be reviewed.

*Please orient on the code style used on the project. The code of the project has to stay clean and organized, so we
won't accept pull requests until this is the case.*

## Code of Conduct

You can find our code of conduct [here](.docs/CODE_OF_CONDUCT.md)

## Copyright notice

Copyright (c) 2022 Paul Huerkamp. All rights reserved.
