# octopusdownloader

[![Build Status](https://travis-ci.com/octopusdownloader/octopusdownloader.svg?branch=master)](https://travis-ci.com/octopusdownloader/octopusdownloader) [![Build status](https://ci.appveyor.com/api/projects/status/ebu18r8r4rmtcj7k?svg=true)](https://ci.appveyor.com/project/kasvith/octopusdownloader)
 [![codecov](https://codecov.io/gh/octopusdownloader/octopusdownloader/branch/master/graph/badge.svg)](https://codecov.io/gh/octopusdownloader/octopusdownloader)

<p align="center"><img src="https://user-images.githubusercontent.com/13379595/47604595-9b56e180-da19-11e8-93cf-a4174fa0ad38.png" height="400" /></p>


A simple crossplatform downloader powered by Java

# Development

## Requirements
- JDK 8+
- Gradle

## Building from source
We use gradle as the build tool. As we ship our gradle wrapper you only need java to build the project.
Run `./gradlew assemble` in your project root and wait until things get done by gradle

### To build JAR file
- `./gradlew jfxJar`

### To run the application
- `./gradlew jfxRun`

### To build native installers(refer Oracle guide for more info)
- `./gradlew jfxNative`

### To run unit tests
- `./gradlew check`
