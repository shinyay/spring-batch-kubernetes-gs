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
