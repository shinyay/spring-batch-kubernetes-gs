# Spring Batch on GKE

Spring Batch Job runs on GKE

## Description
### Spring Batch Model
![spring-batch-model](https://user-images.githubusercontent.com/3072734/110638927-7500fd80-81f2-11eb-90a1-cafaab3b2ec7.png)

- Job
  - It is an entity that encapsulates an entire batch process
  - It is simply a container for Step instances
  - It combines multiple steps that belong logically together in a flow and allows for configuration of properties global to all steps
- Step
  - It  is a domain object that encapsulates an independent, sequential phase of a batch job
- JobRepository
  - It is the persistence mechanism for all the `Jobs` and `Steps`
- JobLauncher
  - It represents a simple interface for launching a `Job`
- Item Reader
  - It is an abstraction that represents the retrieval of input for a `Step`
- Item Writer
  - It is an abstraction that represents the output of a `Step`
- Item Processor
  - It is an abstraction that represents the business processing of an item

### BatchConfiguration
#### JobParameter
- `@Value("#{jobParameters['xxx']}")`

You can refer JobParameter with `@Value("#{jobParameters['xxx']}")` by Command Line Arguments.
This expressions called by Spring Expression Language (**SpEL**)
```shell script
$ java -jar springbatch.jar foo.param001=bar
```

```kotlin
@Value("#{jobParameters['foo.param001']}")
val param001: String
```

## Demo
### Local
#### Start Database as a Container
Start MySQL as a Container
```shell script
$ docker-compose -f docker/mysql/docker-compose.yml up -d
```

Access inside the Container
```shell script
$ docker exec -it my_db bash
```

```shell script
# mysql -u root mydb -p
Enter password: root

mysql> show tables;
+------------------------------+
| Tables_in_guest              |
+------------------------------+
| BATCH_JOB_EXECUTION          |
| BATCH_JOB_EXECUTION_CONTEXT  |
| BATCH_JOB_EXECUTION_PARAMS   |
| BATCH_JOB_EXECUTION_SEQ      |
| BATCH_JOB_INSTANCE           |
| BATCH_JOB_SEQ                |
| BATCH_STEP_EXECUTION         |
| BATCH_STEP_EXECUTION_CONTEXT |
| BATCH_STEP_EXECUTION_SEQ     |
| PEOPLE                       |
+------------------------------+
10 rows in set (0.01 sec)

mysql> show full columns from PEOPLE;
+------------+-------------+--------------------+------+-----+---------+----------------+---------------------------------+---------+
| Field      | Type        | Collation          | Null | Key | Default | Extra          | Privileges                      | Comment |
+------------+-------------+--------------------+------+-----+---------+----------------+---------------------------------+---------+
| ID         | bigint(20)  | NULL               | NO   | PRI | NULL    | auto_increment | select,insert,update,references |         |
| FIRST_NAME | varchar(30) | utf8mb4_general_ci | YES  |     | NULL    |                | select,insert,update,references |         |
| LAST_NAME  | varchar(30) | utf8mb4_general_ci | YES  |     | NULL    |                | select,insert,update,references |         |
| EMAIL      | varchar(40) | utf8mb4_general_ci | YES  |     | NULL    |                | select,insert,update,references |         |
| LOCATION   | varchar(40) | utf8mb4_general_ci | YES  |     | NULL    |                | select,insert,update,references |         |
+------------+-------------+--------------------+------+-----+---------+----------------+---------------------------------+---------+
5 rows in set (0.00 sec)
```

#### Build Container
Build Image for Docker Hub
```shell script
$ ./gradlew clean bootBuildImage --imageName shinyay/spring-batch:0.0.1
$ docker push shinyay/spring-batch:0.0.1
```

Build Image for GCR
```shell script
$ ./gradlew clean bootBuildImage --imageName gcr.io/(gcloud config get-value project)/spring-batch:0.0.1
$ docker push gcr.io/(gcloud config get-value project)/spring-batch:0.0.1
```

Build Image for Artifact Registry
```shell script
$ ./gradlew clean bootBuildImage --imageName us-central1-docker.pkg.dev/(gcloud config get-value project)/shinyay-docker-repo/spring-batch:0.0.1
$ docker push us-central1-docker.pkg.dev/(gcloud config get-value project)/shinyay-docker-repo/spring-batch:0.0.1
```

Build Image with Cloud Build for Artifact Registry
- `gcloud builds submit --pack image=<LOCATION>-docker.pkg.dev/(gcloud config get-value project)/<REPOSITORY>/<NAME>:<TAG>`
```shell script
$ gcloud builds submit --pack image=us-central1-docker.pkg.dev/(gcloud config get-value project)/shinyay-docker-repo/spring-batch:0.0.1
```

#### Execute Spring Batch
```shell script
$ docker run \
     --net mysql_default \
     -e SPRING_DATASOURCE_URL=jdbc:mysql://my_db:3306/mydb \
     -e SPRING_DATASOURCE_USERNAME=root \
     -e SPRING_DATASOURCE_PASSWORD=root \
     -e SPRING_DATASOURCE_DRIVER-CLASS-NAME=com.mysql.cj.jdbc.Driver \
     shinyay/spring-batch:0.0.1 \
     fileName=https://raw.githubusercontent.com/shinyay/spring-batch-kubernetes-gs/main/src/main/resources/person.csv
```
or
```shell script
$ docker run \
     --net mysql_default \
     shinyay/spring-batch:0.0.1 \
     fileName=https://raw.githubusercontent.com/shinyay/spring-batch-kubernetes-gs/main/src/main/resources/person.csv
```

### Cloud
#### Create Cloud SQL for MySQL
Create Instance
```shell script
$ gcloud sql instances create mysql-instance \
    --database-version MYSQL_5_7 \
    --region us-central1 \
    --cpu 2 \
    --memory 4G \
    --root-password root
```

List Instance
```shell script
$ gcloud sql instances list
```

Create Database
```shell script
$ gcloud sql databases create mydb --instance mysql-instance
```

List Databases
```shell script
$ gcloud sql databases list --instance mysql-instance
```

Add User
```shell script
$ gcloud sql users create batch --instance=mysql-instance --host=% --password=batch
```

List Users
```shell script
$ gcloud sql users list --instance mysql-instance

NAME   HOST  TYPE
batch  %     BUILT_IN
root   %     BUILT_IN
```

Connect to MySQL
```shell script
$ gcloud beta sql connect mysql-instance -u batch
```

Create Schema
```shell script
mysql> use mydb;
mysql> create ...
mysql> show full columns from PEOPLE;
```

#### GKE Autopilot Cluster
##### Create Autopilot Cluster
```shell script
$ gcloud container clusters create-auto shinyay-cluster-auto \
    --region us-central1 \
    --project (gcloud config get-value project)
```

##### Verify Nodes
```shell script
$ kubectl get nodes -L beta.kubernetes.io/instance-type
```

#### Workload Identity for CloudSQL
##### Create Service Account for Cloud SQL
Create Service Account
```shell script
$ gcloud iam service-accounts create spring-cloud-gcp --display-name "Spring Cloud GCP"
```

Grant Service Account to Role
```shell script
$ gcloud projects add-iam-policy-binding (gcloud config get-value project) \
    --member serviceAccount:spring-cloud-gcp@(gcloud config get-value project).iam.gserviceaccount.com \
    --roles/cloudsql.client
```

Create Service Account Key
```shell script
$ gcloud iam service-accounts keys create key.json \
  --iam-account spring-cloud-gcp@(gcloud config get-value project).iam.gserviceaccount.com
```
```shell script
$ base64 key.json

ewogICJ0eXBlIjo......bnQuY29tIgp9Cg==
```
##### Enable Workload Identity
Confirm Workload Identity enabled
```shell script
$ gcloud container clusters describe shinyay-cluster-auto --region us-central1|grep -C 2 workloadIdentity

workloadIdentityConfig:
  workloadPool: <PROJECT_ID>.svc.id.goog
```

If Workload Identity is not enabled, you can configure as the following:

```shell script
$ gcloud container clusters create <CLUSTER_NAME> \
    --workload-pool=<PROJECT_ID>.svc.id.goog
```
or
```shell script
$ gcloud container clusters update <CLUSTER_NAME> \
    --workload-pool=<PROJECT_ID>.svc.id.goog
```

##### Create Kubernetes Service Account
- [service_account.yml](kubernetes/service_account.yml)

```shell script
$ kubectl apply -f kubernetes/service_account.yml
```

```shell script
$ kubectl get sa

NAME      SECRETS
batch     1      
default   1      
```

##### Bind KSA and GSA
Bind KSA and GSA
```shell script
$ gcloud iam service-accounts add-iam-policy-binding \
    --role roles/iam.workloadIdentityUser \
    --member serviceAccount:(gcloud config get-value project).svc.id.goog[default/batch] \
    spring-cloud-gcp@(gcloud config get-value project).iam.gserviceaccount.com
```

Annotate Service Account
```shell script
$ kubectl annotate serviceaccount \
    batch \
    iam.gke.io/gcp-service-account=spring-cloud-gcp@(gcloud config get-value project).iam.gserviceaccount.com
```

Specify **Kubernetes Service Account** in Job.yml
```yaml
apiVersion: batch/v1
kind: Job
spec:
  template:
    spec:
      serviceAccountName: batch
      containers:
        - name: spring-job
  :
  :
```

Verify Workload Identity Configuration
```shell script
$ kubectl run -it \
    --image google/cloud-sdk:slim \
    --serviceaccount batch \
    workload-identity-test

root@workload-identity-test:/# gcloud auth list
ACTIVE  ACCOUNT
*       spring-cloud-gcp@<PROJECT_ID>.iam.gserviceaccount.com
```

#### GKE
##### Prepare ConfigMap
- [configmap.yml](kubernetes/configmap.yml)

Put the following values:
- Cloud SQL Instance Connection Name
  - `gcloud sql instances describe mysql-instance --format='value(connectionName)'`

##### Prepare Secret
- [secret.yml](kubernetes/secret.yml)

Put the following values:
- Cloud SQL User
- Cloud SQL Password
- Service Account Key for Cloud SQL

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: mydb-secret
type: Opaque
data:
  db.username: YmF0Y2g=  #batch
  db.password: YmF0Y2g=  #batch
  db.accountkey: ewogICJ0e......
```
Deploy Secret
```shell script
$ kubectl apply -f kubernetes/secret.yml
```

#### Execute Batch as Job
```shell script
$ JOB_NAME=create-user \
  PROJECT_NAME=(gcloud config get-value project) \
  envsubst < kubernetes/job.yml | kubectl apply -f -
```

## Features

- feature:1
- feature:2

## Requirement

## Usage

## Installation

## References

## Licence

Released under the [MIT license](https://gist.githubusercontent.com/shinyay/56e54ee4c0e22db8211e05e70a63247e/raw/34c6fdd50d54aa8e23560c296424aeb61599aa71/LICENSE)

## Author

[shinyay](https://github.com/shinyay)
