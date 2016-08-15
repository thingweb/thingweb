# thingweb #

Thingweb is an open source implementation of the WoT servient model of W3C's interest group on the web of things (W3C WoT IG).

For information about what it does, see also the [WoT Current Practices document] (http://w3c.github.io/wot/current-practices/wot-practices.htm).

### Using ###

Hosted on Bintray:

* client: [ ![Download](https://api.bintray.com/packages/thingweb/maven/de.thingweb.thingweb-client/images/download.svg) ](https://bintray.com/thingweb/maven/de.thingweb.thingweb-client/_latestVersion)
* common: [ ![Download](https://api.bintray.com/packages/thingweb/maven/de.thingweb.thingweb-common/images/download.svg) ](https://bintray.com/thingweb/maven/de.thingweb.thingweb-common/_latestVersion)
* discovery: [ ![Download](https://api.bintray.com/packages/thingweb/maven/de.thingweb.thingweb-discovery/images/download.svg) ](https://bintray.com/thingweb/maven/de.thingweb.thingweb-discovery/_latestVersion)
* javascript-runtime: [ ![Download](https://api.bintray.com/packages/thingweb/maven/de.thingweb.thingweb-javascript/images/download.svg) ](https://bintray.com/thingweb/maven/de.thingweb.thingweb-javascript/_latestVersion)
* td-parser: [ ![Download](https://api.bintray.com/packages/thingweb/maven/de.thingweb.thingweb-parser/images/download.svg) ](https://bintray.com/thingweb/maven/de.thingweb.thingweb-parser/_latestVersion)
* server: [ ![Download](https://api.bintray.com/packages/thingweb/maven/de.thingweb.thingweb-server/images/download.svg) ](https://bintray.com/thingweb/maven/de.thingweb.thingweb-server/_latestVersion)

Jcenter and MavenCentral will follow soon

Resolving using gradle:

```groovy
repositories {
    maven {
        url  "http://dl.bintray.com/thingweb/maven" 
    }
}
```

### Building ###

* we are using [Gradle](https://gradle.org/) as build tool

There is a also build setup on [drone.io](https://drone.io/github.com/thingweb/thingweb/latest).
[![Build Status](https://drone.io/github.com/thingweb/thingweb/status.png)](https://drone.io/github.com/thingweb/thingweb/latest)

**The latest artifacts can be downloaded [here](https://drone.io/github.com/thingweb/thingweb/files)**

### Contribution guidelines ###

* we are happy for contributions
* just open a PR
* Writing tests will be mandatory soon
* Peer code review will take place

### TODO List (adopt a topic by PR-ing your name) ###

- [ ] interface for token checking
- [ ] API documentation
- [ ] further protocol bindings for XMPP and WS
- [ ] test framework
- [ ] tests

### License ###

MIT License

### Who do I talk to? ###

* W3C WoT IG Members from Siemens

