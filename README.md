# Engelsburg-API

Unofficial API of the Engelsburg-Gymnasium-Kassel.

## Structure

The Engelsburg-API can be divided into two sections.

### Public

The first section ist completely public. It includes unprotected resources of
the [Engelsburg-Website](https://engelsburg.smmp.de) like events and articles.

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
password are correct, the account is verified, the request will include an *Json-Web-Token* in the response body with
the key `token`.

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
| DATABASE_HOSTNAME   | Hostname of the database               | 
| DATABASE_PORT       | Port of the database                   |
| DATABASE_DATABASE   | Database-name of the database          |
| DATABASE_USERNAME   | Username to login into database        |
| DATABASE_PASSWORD   | Password to login into database        |
| SERVICE_TOKEN       | Service-Token to secure access         |
| SCHOOL_TOKEN        | Token given by the school for the substitutes|
| JWT_SECRET          | Secret for JWT                         |
| GOOGLE_ACCOUNT_CREDENTIALS| Google account credentials for firebase cloud messaging|
| PRODUCTION          | Production mode to actually send notifications and more|
| use.ssl             | Enable https and ssl                   |

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

## Apache License 2.0

Copyright 2020-2021 Paul Huerkamp

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
License. You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
language governing permissions and limitations under the License.
