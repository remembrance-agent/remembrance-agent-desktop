# remembrance-agent
Java package for Remembrance Agents! Based on Rhodes/Starner

[![Build Status](https://travis-ci.org/remembrance-agent/remembrance-agent-desktop.svg?branch=master)](https://travis-ci.org/remembrance-agent/remembrance-agent-desktop) [![Build Status](https://github.com/remembrance-agent/remembrance-agent-desktop/workflows/Java%20CI/badge.svg)](https://github.com/remembrance-agent/remembrance-agent-desktop/actions?workflow=Java+CI)

This package uses the [`remembrance-agent` engine package](https://github.com/remembrance-agent/remembrance-agent) to implement at desktop GUI-based RA. Special features include:
* Support for indexing emails from Gmail
* Support for indexing documents from Google Drive
* Support for indexing HTML documents from Google Chrome (only supports macOS because this feature uses AppleScript)
* Speech as input using a Google Cloud Speech API
* Automatic logging of all keystrokes while the RA GUI is running, both for use in the RA and for logging to the `RA` data directory

![Logo](./docs/img/logo.png)

## Commands

### Building

```bash
VERSION="2.0.0" ./gradlew build
```

### Installing as Launch Daemon (macOS)

```bash
sudo VERSION="2.0.0" bash ./bin/install
```

Now you can run the following from anywhere:
```bash
VERSION="2.0.0" ra --home $HOME
```

### Running (cross platform)

Adapt the file `./bin/ra` for your platform.

## Screenshots

### RA client with menu open

![](./docs/img/ra-client-menu-open.png)

### RA client with suggestion

![](./docs/img/ra-client-with-suggestion.png)

### Chrome opened suggestion

![](./docs/img/chrome-opened-suggestion.png)

## Developing

### Versioning

Increment the version numbers in this README.

## OS-specific setup instructions

### Windows

To make use of the "Save text of active Chrome tab" feature on Windows, you'll need to install Python and the pip package `pywinauto`.

You will also need `bash` installed and available on your `PATH`.

---

Pramod Kotipalli  
@p13i  
http://p13i.io
