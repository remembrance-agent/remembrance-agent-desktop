# remembrance-agent
Java package for Remembrance Agents! Based on Rhodes

**STRICTLY NON-COMMERCIAL USE**  
**STRICTLY NON-COMMERCIAL USE**  
**STRICTLY NON-COMMERCIAL USE**  

[![Build Status](https://travis-ci.org/remembrance-agent/remembrance-agent-desktop.svg?branch=master)](https://travis-ci.org/remembrance-agent/remembrance-agent-desktop) [![Build Status](https://github.com/remembrance-agent/remembrance-agent-desktop/workflows/Java%20CI/badge.svg)](https://github.com/remembrance-agent/remembrance-agent-desktop/actions?workflow=Java+CI)

This package uses the [`remembrance-agent` engine package](https://github.com/remembrance-agent/remembrance-agent) to implement at desktop GUI-based RA. Special features include:
* Support for indexing emails from Gmail
* Support for indexing documents from Google Drive
* Support for indexing HTML documents from Google Chrome (only supports macOS because this feature uses AppleScript)
* Speech as input using a Google Cloud Speech API
* Automatic logging of all keystrokes while the RA GUI is running, both for use in the RA and for logging to the `RA` data directory

## Installation instructions

This project offers macOS and Windows distributions in the form of `dmg` and `exe` files, respectively.

Please see the Actions tab above, click the latest build with a green check mark, and download the artifact for your operating system.


## Screenshots

### RA client with menu open

![](./docs/img/ra-client-menu-open.png)

### RA client with suggestion

![](./docs/img/ra-client-with-suggestion.png)

### Chrome opened suggestion

![](./docs/img/chrome-opened-suggestion.png)

## Developing

### Versioning

Increment the version number in the `VERSION` file and in the `RemembranceAgentClient`.

---

Pramod Kotipalli  
@p13i  
http://p13i.io
