# README #

Simple web application that demonstrates the usage of the Credhub API via Spring Credhub to generate and rotate passwords and certificates. Interaction with Credhub takes place via a UAA client instead of the default mTLS authentication. This is more portable across different environments because it does not rely on application guids.

## Initial setup

### Create a UAA client using the UAAC CLI
```bash
uaac token client get admin -s <youradminclientsecret>

uaac client add --name credhubtest4 --scope uaa.none --authorized_grant_types client_credentials --authorities "credhub.write,credhub.read"

```

### Add permissions for this UAA client using Credhub CLI
Give this client the permissions on the paths you intend to use in your application. In our case we will add permissions on /passwords/* and /myroot/mycacertificate

```bash
credhub api --server <hostname-or-ip-of-pas-credhub>

credhub login --client-name credhub_admin_client --client-secret <yourcredhubadminclientsecret>

credhub curl -p /api/v2/permissions -X POST -d '{"path": "/mypasswords/*","actor": "uaa-client:credhubtest4", "operations": ["read","write"]}'

credhub curl -p /api/v2/permissions -X POST -d '{"path": "/myroot/mycacertificate","actor": "uaa-client:credhubtest4", "operations": ["read","write"]}'
```

### Add the UAA client credentials in the app config

Add the client-id and client-secret you created in the first step in your [application configuration](https://github.com/NLxAROSA/pcf-credhub/blob/master/src/main/resources/application.yml)

### Build and run the app and push it to CF
```bash
./mvn clean package && cf push
```