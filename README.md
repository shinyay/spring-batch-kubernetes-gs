# Spring Batch on GKE

Overview

## Description
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
```shell script
$ ./gradlew clean bootBuildImage --imageName shinyay/spring-batch:0.0.1
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
$ gcloud beta sql connect mysql-instance
```

#### GKE
Deploy Secret
```shell script
$ kubectl apply -f kubernetes/secret.yml
```

#### Workload Identity for CloudSQL
##### Create Service Account for Cloud SQL
Create Service Account
```shell script
$ gcloud iam service-accounts create spring-cloud-gcp --display-name "Spring Cloud GCP"
```

##### Enable Workload Identity
Confirm Workload Identity enabled
```shell script
$ gcloud container clusters describe shinyay-cluster-auto --region us-central1

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
