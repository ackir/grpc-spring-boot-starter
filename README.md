# GRpc Spring Boot Starter

[![Circle CI](https://circleci.com/gh/lavcraft/grpc-spring-boot-starter/tree/master.svg?style=shield)](https://circleci.com/gh/lavcraft/grpc-spring-boot-starter/tree/master)
[![codecov.io](https://codecov.io/github/lavcraft/grpc-spring-boot-starter/coverage.svg?branch=master)](https://codecov.io/github/lavcraft/grpc-spring-boot-starter?branch=master)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3ae2b8efd3124618928e3008d8e90afd)](https://www.codacy.com/app/lavcraft/grpc-spring-boot-starter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lavcraft/grpc-spring-boot-starter&amp;utm_campaign=Badge_Grade)
[![Join the chat at https://gitter.im/lavcraft/grpc-spring-boot-starter](https://badges.gitter.im/lavcraft/grpc-spring-boot-starter.svg)](https://gitter.im/lavcraft/grpc-spring-boot-starter?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Download](https://api.bintray.com/packages/lavcraft/maven/grpc-spring-boot-starter/images/download.svg) ](https://bintray.com/lavcraft/maven/grpc-spring-boot-starter/_latestVersion)

## Usage

* Add dependencies (see examples)

```
ru.alfalab.grpc.spring:starter
io.grpc:grpc-stub
io.grpc:grpc-protobuf
io.grpc:grpc-netty
```
    
example:
```groovy
repositories {
  jcenter()
}

apply plugin: 'java'
apply plugin: 'com.google.protobuf'
apply plugin: 'org.springframework.boot'

dependencies {
  compile "ru.alfalab.grpc.spring:starter:$starterVersion"
  compile "io.grpc:grpc-stub:$grpcVersion"
  compile "io.grpc:grpc-protobuf:$grpcVersion"
  compile "io.grpc:grpc-netty:$grpcVersion"  
}
// see examples/example-isolated/build.gradle for details  
```

* Add `@EnableGRpcServer` anotation to your configuration for enable grpc server
* Customize configuration - application.yml/application.property. Example:

```YAML
grpc:
 enabled: true
 servers:
   -
     address: 127.0.0.1
     port: 6565
   -
     address: 127.0.0.1
     port: 0
```

Use `port: 0` for auto assign random port

or

```ini  
    grpc.enabled: true
    grpc.servers[0].address=127.0.0.1
    grpc.servers[0].port=6565
    grpc.servers[0].address=127.0.0.1
    grpc.servers[0].port=0    
```    

## Default values

* port 6565
* host/ip localhost/127.0.0.1

## Advanced features

### Discover random port

If you want to run on random port (`grpc.servers[0].port=0`), you will need inject resulted port. 
Use `@GRPCLocalPort` please for solve this

```groovy
@GRPCLocalPort
int port
```

## Tested on

| name                      | version       |
| ---                       | ---           |
| io.grpc:grpc-stub         | 1.7.0         |
| io.grpc:grpc-protobuf     | 1.7.0         |
| io.grpc:grpc-netty        | 1.7.0         |
| com.google.protobufprotoc | 3.2.0         |
| Spring Boot               | 1.5.8.RELEASE |
