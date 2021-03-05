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
### Local - Start Database as a Container
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
