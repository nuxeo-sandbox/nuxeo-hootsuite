## Description
This plugin provides an integration between Hootsuite and the Nuxeo Platform.
Hootsuite users can search and use content from the Nuxeo Platform.

## Important Note

**These features are not part of the Nuxeo Production platform.**

These solutions are provided for inspiration and we encourage customers to use them as code samples and learning resources.

This is a moving project (no API maintenance, no deprecation process, etc.) If any of these solutions are found to be useful for the Nuxeo Platform in general, they will be integrated directly into platform, not maintained here.

## Requirements
Building requires the following software:
- git
- maven
- npm

## How to build
```
git clone https://github.com/nuxeo-sandbox/nuxeo-hootsuite
cd nuxeo-hootsuite
mvn clean install
```

## Deploying
* Install the marketplace package from the admin center or using nuxeoctl
* Edit nuxeo.conf

```
hootsuite.secret=my_secret
nuxeo.frame.options=ALLOW-FROM https://hootsuite.com/
```

* This plugins requires the AWS S3 storage plugin with direct download activated

## Hootsuite Configuration
You need a hootsuite developer account in order to use this plugin

* Create a content source application [documentation](https://developer.hootsuite.com/docs/add-a-content-source-component)
* Set the content source application Service URL to https://nuxeo_host/nuxeo/hootsuite

## Known limitations
This plugin is a work in progress.

## About Nuxeo
Nuxeo dramatically improves how content-based applications are built, managed and deployed, making customers more agile, innovative and successful. Nuxeo provides a next generation, enterprise ready platform for building traditional and cutting-edge content oriented applications. Combining a powerful application development environment with SaaS-based tools and a modular architecture, the Nuxeo Platform and Products provide clear business value to some of the most recognizable brands including Verizon, Electronic Arts, Netflix, Sharp, FICO, the U.S. Navy, and Boeing. Nuxeo is headquartered in New York and Paris. More information is available at [www.nuxeo.com](http://www.nuxeo.com).
