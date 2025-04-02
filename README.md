<img align="left" alt="logo" width="128" src=".idea/icon.svg">

# &nbsp;&nbsp;&nbsp;Ktrabbitmq

###### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Additional functionality of [RabbitMQ Java Client](https://github.com/rabbitmq/rabbitmq-java-client).

[![Kotlin](https://img.shields.io/badge/kotlin-2.1.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.kodepix/ktrabbitmq)](https://central.sonatype.com/artifact/io.github.kodepix/ktrabbitmq)
![GitHub](https://img.shields.io/github/license/kodepix/ktrabbitmq)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

---
<br>

* [Installation](#installation)
* [Development](#development)
    * [Getting started](#getting-started)
    * [Gradle wrapper generation](#gradle-wrapper-generation)
    * [Versions update](#versions-update)

## Installation

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.kodepix:ktrabbitmq:1.0")
}
```

## Development

### Getting started

- JDK 21

### Gradle wrapper generation

```shell
./gradlew wrapper --gradle-version 8.13 --distribution-type bin
```

### Versions update

```shell
./gradlew versionCatalogUpdate
```

---
