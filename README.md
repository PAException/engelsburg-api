# Engelsburg-API
Unofficial API of the Engelsburg-Gymnasium-Kassel.

# Environment
This project needs several environment-variables:

| Environment Variable| Description                            |
|---------------------|----------------------------------------|
| SERVER_PORT         | The port the server is running on      |
| DATABASE_HOSTNAME   | Hostname of the database               | 
| DATABASE_PORT       | Port of the database                   |
| DATABASE_DATABASE   | Database-name of the database          |
| DATABASE_USERNAME   | Username to login into database        |
| DATABASE_PASSWORD   | Password to login into database        |
| SERVICE_TOKEN       | Service-Token to secure access         |

# Endpoint Documentation

We use Swagger to document our endpoints. 
You can visit [Swagger.io](https://editor.swagger.io/) and import [endpoints.yaml](.docs/endpoints.yaml) on any branch, 
but you can also click [here](https://editor.swagger.io/?url=https://raw.githubusercontent.com/engelsburg/engelsburg-api/master/.docs/endpoints.yaml)
to view the endpoint documentation of the current version.

# License
Copyright 2020-2021 Paul Huerkamp
