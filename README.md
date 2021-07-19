# OpenAVT-Android

[![License](https://img.shields.io/github/license/asllop/OpenAVT-Android)](https://github.com/asllop/OpenAVT-Android)

1. [ Introduction ](#intro)
2. [ Installation ](#install)
3. [ Usage ](#usage)
4. [ Examples ](#examp)
5. [ Documentation ](#doc)
6. [ Author ](#auth)
7. [ License ](#lice)

<a name="intro"></a>
## 1. Introduction

The Open Audio-Video Telemetry is a set of tools for performance monitoring in multimedia applications. The objectives are similar to those of the OpenTelemetry project, but specifically for sensing data from audio and video players. OpenAVT can be configured to generate Events, Metrics, or a combination of both.

<a name="install"></a>
## 2. Installation

To install OpenAVT-Android using JitPack, add the following lines to your root build.gradle:

```
allprojects {
    repositories {
        ...
        
        // Add this line at the end of your repositories
        maven { url 'https://jitpack.io' }
    }
}
```

And then add in your app build.gradle one line per each module:

#### 2.1 Core

This one is mandatory, needed by the rest of modules.

```
dependencies {
    ...
    implementation 'com.github.asllop.OpenAVT-Android:OpenAVT-Core:master-SNAPSHOT'
}
```

#### 2.2 ExoPlayer Tracker

```
dependencies {
    ...
    implementation 'com.github.asllop.OpenAVT-Android:OpenAVT-ExoPlayer:master-SNAPSHOT'
    
    // ExoPlayer is a dependency of OpenAVT-ExoPlayer
    implementation 'com.google.android.exoplayer:exoplayer:+'
}
```

#### 2.3 Google IMA Tracker

```
dependencies {
    ...
    implementation 'com.github.asllop.OpenAVT-Android:OpenAVT-IMA:master-SNAPSHOT'
    
    // ExoPlayer IMA extension is a dependency of OpenAVT-IMA
    implementation 'com.google.android.exoplayer:extension-ima:+'
}
```

#### 2.4 InfluxDB Backend

```
dependencies {
    ...
    implementation 'com.github.asllop.OpenAVT-Android:OpenAVT-InfluxDB:master-SNAPSHOT'
}
```

#### 2.5 Graphite Backend

```
dependencies {
    ...
    implementation 'com.github.asllop.OpenAVT-Android:OpenAVT-Graphite:master-SNAPSHOT'
}
```

<a name="usage"></a>
## 3. Usage

There are many ways to use the OpenAVT library, depending on the use case, here we will cover the most common combinations. We won't explain all the possible arguments passed to the constructors, only the essential ones. For the rest check out the [documentation](#doc).

### 3.1 Choosing a Backend

The first step is choosing the backend where the data will be sent.

#### 3.1.1 Init the InfluxDB Backend

```kotlin
val backend = OAVTBackendInfluxdb(url = URL("http://192.168.99.100:8086/write?db=test"))
```

`url` is the URL of the InfluxDB server used to write data to a particular database (in this case named `test`).

#### 3.1.1 Init the Graphite Backend

```kotlin
val backend = OAVTBackendGraphite(host = "192.168.99.100"))
```

`host` is the address of the Graphite server.

### 3.2 Choosing a Hub

Next, we will choose a Hub. This element is used to obtain the data coming from the trackers and process it to pass the proper events to the backend. Users can implement their logic for this and use their custom hubs, but OpenAVT provides a default implementation that works for most cases.

For instruments with video tracker only, we will choose:

```kotlin
val hub = OAVTHubCore()
```

And for instruments with video and ads tracker:

```kotlin
val hub = OAVTHubCoreAds()
```

### 3.3 Choosing a Metricalc

This step is optional and only necessary if we want to generate metrics, if we only need events this section can be omitted. A Metricalc is something like a Hub but for metrics, it gets events and processes them to generate metrics. Again, users can provide custom implementation, but the OpenAVT library provides a default one:

```kotlin
val metricalc = OAVTMetricalcCore()
```

### 3.4 Choosing Trackers

And finally, the trackers, the piece that generates the data. Currently, OpenAVT provides two trackers: ExoPlayer and Google IMA Ads. We won't cover how to set up the ExoPlayer and IMA libraries, for this check out the corresponding documentation or the [examples](#examp).

#### 3.4.1 Init the ExoPlayer Tracker

```kotlin
val tracker = OAVTTrackerExoPlayer(player)
```

Where `player` is an instance of the SimpleExoPlayer.

#### 3.4.2 Init the IMA Tracker

```kotlin
val adTracker = OAVTTrackerIMA()
```

### 3.5 Creating the Instrument

Once we have all the elements, the only step left is putting everything together:

```kotlin
val instrument = OAVTInstrument(hub = hub, metricalc = metricalc, backend = backend)
val trackerId = instrument.addTracker(tracker)
val adTrackerId = instrument.addTracker(adTracker)
instrument.ready()
```

Here we have created a new instrument that contains all the elements, and once all are present, we called `ready()` to initialize everything, This will cause the execution of the method `OAVTComponentInterface.instrumentReady(...)` in all trackers, hub, metricalc and backend. Now the instrument is ready to start generating data.

<!--
NOTE: Should we put all this in a separate .md file?

## Advanced Topics

#### Custom Instrument Elements

OpenAVT provides a set of trackers, hubs, metricalcs, and backends, that cover a wide range of possibilities, but not all. For this reason, the most interesting capability it offers is its flexibility to accept custom implementations of these elements.

TODO: explain how to create custom stuff.

#### Custom Events

#### Custom Attributes

#### Custom Metrics

#### Custom Trackers

#### Custom Hubs

#### Custom Metricalcs

#### Custom Backends

#### Custom Buffers

## Use Cases

TODO: how to modify the OpenAVT compoonents, creating custom stuff or subclassing, to support certain use cases.
-->

<a name="examp"></a>
## 4. Examples

Checkout the `app` folder for usage examples.

<a name="doc"></a>
## 5. Documentation

**Check out the [Documentation Repository](https://github.com/asllop/OpenAVT-Docs) for general and platform-independent documentation.**

All classes and methods are documented with annotations. To generate the docs you can use [Dokka](https://github.com/Kotlin/dokka). Just open the project in Android Studio, go to the Gradle menu, and double-click on `OpenAVT-Android > Tasks > documentation > dokka`.

<a name="auth"></a>
## 6. Author

Andreu Santarén Llop (asllop)<br>
andreu.santaren at gmail .com

<a name="lice"></a>
## 7. License

OpenAVT-Android is available under the MIT license. See the LICENSE file for more info.
