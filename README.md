<img align="left" alt="logo" width="128" src=".idea/icon.svg">

# &nbsp;&nbsp;&nbsp;Library template

###### &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Library template.

[![Kotlin](https://img.shields.io/badge/kotlin-2.1.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
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
    implementation("io.github.kodepix:lib-template:1.0")
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
