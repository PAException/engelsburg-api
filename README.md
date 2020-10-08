# Engelsburg-API
API of the Engelsburg-Gymnasium-Kassel

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
| DATA_FOLDER         | Relative or absolute path to datafiles |

# Endpoint Documentation

Go to [Swagger.io](https://editor.swagger.io/) and import [endpoints.yaml](.docs/endpoints.yaml)

# Roadmap

## v1

- [x] Substitutes
- [x] SubstituteMessages
- [x] Hashes to see if information was updated
- [x] ServiceToken for selective access

## v1.1

- [x] InformationEndpoint
  - [x] current classes
  - [ ] ~~teachers~~

## v2

- [x] Articles on homepage endpoint
- [x] Events endpoint

# License
Copyright 2020 Paul Huerkamp
