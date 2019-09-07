1.Request a Remote OpenShift Lab Environment

2.Open terminal use command Login

```
$ oc login --insecure-skip-tls-verify  --server=https://master00-XXXX.generic.opentlc.com:443
```

3.Create the OpenShift projects

```
$ export COOLSTORE_PRJ=simple-project

$ oc new-project ${COOLSTORE_PRJ}

$ oc project $COOLSTORE_PRJ

```

4.Deploy an instance of MongoDB

```
$ cd ~/lab/project-service

$ oc process -f src/ocp/coolstore-project-mongodb-persistent.yaml \
-p PROJECT_DB_USERNAME=mongo \
-p PROJECT_DB_PASSWORD=mongo | oc create -f - -n $COOLSTORE_PRJ
```

5.Create a ConfigMap

```
$ oc create configmap catalog-service --from-file=etc/app-config.yml -n $COOLSTORE_PRJ

$ oc policy add-role-to-user view -z default -n $COOLSTORE_PRJ
```


6.Deploy the project-service application on OpenShift using the Fabric8 Maven plug-in

```
$ mvn clean fabric8:deploy -Popenshift -Dfabric8.namespace=$COOLSTORE_PRJ
```

7.Monitor the deployment of the project-service

```
$ oc get pods -n $COOLSTORE_PRJ -w
```

8.Test Service

```
$ export PROJECT_URL=http://$(oc get route project-service -n $COOLSTORE_PRJ -o template --template='{{.spec.host}}')

$ curl -X GET "$PROJECT_URL/projects"
$ curl -X GET "$PROJECT_URL/projects/{projectId}"
$ curl -X GET "$PROJECT_URL/projects/status/{theStatus}"

```