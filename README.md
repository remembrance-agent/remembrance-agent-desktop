# Remembrance Agent Desktop GUI

\> Desktop graphical user interface (GUI) for Remembrance Agents.  
\> Based on research by Bradley Rhodes and MIT Media Lab.  
\> Written in pure Java.  
\> Available on Windows, macOS, and Linux.

[![Build Status](https://github.com/remembrance-agent/remembrance-agent-desktop/workflows/Java%20CI/badge.svg)](https://github.com/remembrance-agent/remembrance-agent-desktop/actions?workflow=Java+CI)

## Background

 From [[Rhodes 1996]](./docs/papers/rhodes1996.pdf)'s Abstract:

> The Remembrance Agent (RA) is a program which augments human memory by displaying a list of documents which might be relevant to the user’s current context. Unlike most information retrieval systems, the RA runs continuously without user intervention. Its unobtrusive interface allows a user to pursue or ignore the RA’s suggestions as desired.

RAs were originally implemented as an extension for the Emacs text editor by [[Rhodes 1996]](./docs/papers/rhodes1996.pdf). [[Rhodes 1997]](./docs/papers/rhodes1997.pdf) took this one step further by integrating the RA with head-worn displays. However, as of 2020, the use of Emacs or head-worn displays is not widespread. (Only 4.5% of developers use Emacs according to a [2019 survey of Stack Overflow users](https://insights.stackoverflow.com/survey/2019#technology-_-most-popular-development-environments).)

This `remembrance-agent-desktop` project recognizes that 1) RAs are powerful tools and 2) to enable widespread use of RAs, they must be available in a form accessible to a wide range of users outside of just tech-savvy folks. A desktop GUI fills this gap.

This package uses the [`remembrance-agent` Java package](https://github.com/remembrance-agent/remembrance-agent) to provide a desktop-based interface to a remembrance agent. A remembrance agent (RA) is a memory augmentation tool that matches documents from your digital fingerprint to your current context.

## Example use cases

For example, when typing an email to a colleague about "artificial intelligence," a remembrance agent can pull up lecture notes from years ago on the topic.

Another useful example is in the context of industrial tasks. Think of a factory worker learning a new role on an assembly line. Your company provides you with a lot of documentation as to how to work this role. Your colleagues also help get you up to speed. However, this is a lot of information to keep in mind and pull up at the right moment, say in the middle of a particular task. A remembrance agent can help the worker remember contextually-relevant advice thereby augmenting his memory.

Beyond a straightforward keyword match based on the content of your documents, other contextual factors of documents can be considered as well including:
- the date a document is written,
- the people involved in with the document (useful for meeting notes), and
- where the document was written (useful for location-aware suggestions).

Of course, other factors can be included as needed. The "magic formula" used to determine a useful contextual suggestion can be easily altered as well. ([Future changes](https://github.com/remembrance-agent/remembrance-agent/issues/13) will make this "formula" easy to edit.)

Special features for this desktop implementation include:
* Support for indexing emails from Gmail
* Support for indexing documents from Google Drive
* Support for indexing HTML documents from the internet:
    * macOS users can have the RA automatically detect the top-most Google Chrome tab
    * Windows users will need to paste in the URL they wish to index
* Speech as input using a Google Cloud Speech API
* Automatic logging of all keystrokes while the RA GUI is running, both for use in the RA and for logging to the `RA` data directory

## Installation instructions

This project offers macOS and Windows distributions in the form of `dmg` and `exe` files, respectively.

Please follow these steps:

1. Go to the [project's releases](https://github.com/remembrance-agent/remembrance-agent-desktop/releases).
2. Click on the latest published release.
3. Download the `zip` file for your operating system.
4. Install the application:
    - On macOS:
        1. The `zip` file will be automatically unzipped for you by macOS
        2. Double-click on the `dmg` file
        3. Drag-and-drop the application into your `Applications` folder (or anywhere you'd prefer)
        4. Double-click on the RA application icon in your `Applications` folder
        5. You will likely see a security warning since this package is not signed. Go to `System Preferences > Security & Privacy > General` and click on the `Open` button that should appear for this application.
        6. The RA app window will appear.
        7. You will likely see a request for access to accessibility features. These are required for the RA's keylogger to work; this is the primary means of input to the RA. Follow the instructions provided by macOS to enable access to accessibility features.
        8. Restart the RA: Within the RA app window, click on `RA Settings > Reinitialize remembrance agent`
        9. You should be good to go!
    - Windows:
        1. Unzip the `zip` file
        2. Double-click on the `exe`. This will launch the installer.
        3. You will likely see a SmartScreen warning from Windows because this package is not signed. Click on `More Info` and select `Run anyway`.
        4. Follow the instructions to install the application to your system.
        5. Open the Start menu and click on the new brain icon labeled "remembrance-agent-desktop".
        6. You should be good to go!
5. Read more on the usage of this project below.

## GUI usage

This implementation uses your keystrokes as input to the RA's suggestion engine. This input alongside contextual factors, such as the current date, allows the RA to suggest you contextually relevant suggestions. Doing so, your memory is effectively augmented to include all written documents that are indexed and available to the RA.

Upon starting the application for the first time, you will see a consent dialog. Click Ok to continue. You will be presented with the following GUI in a fixed-size window that will stay on top of other windows you have open. (You can always minimize the window to have suggestions be computed in the background.)

![](./docs/img/ra-client-menu-open.png)

There are four main components of the GUI to be familiar with:

1. The title displays the version of the RA desktop client.
2. The menu bar presents you with options to configure the client's various settings.
3. The Suggestions panel presents you with contextually relevant suggestions based on your most recent keystrokes. By clicking on the suggestion, your computer will open the document or web page associated with the document so you can find contextually-relevant information.
4. The Input panel presents you with what string of characters forms the query to the RA.
    1. By default, your keystrokes are used as input. You should see `Keyboard Input Mechanism` indicating this. Your previous `75` keystrokes are used. (This can be changed at `RemembranceAgentClient::KEYBOARD_BUFFER_SIZE` although you will have to ensure the GUI presents the buffer approrpiately.)
    2. You can also use speech as an input via Google Cloud's Speech APIs. This option is not user-friendly yet as it requires an API key from Google Cloud Platform.
    3. (You can easily add new input mechanisms such as from custom peripherals. See `GoogleCloudSpeechInputMechanism` for a comprehensive example.)

As you type during normal use of your computer, the RA will log those keystrokes and calculate contextually-relevant suggestions. Those suggestions will be displayed in the Suggestions panel. Click on a suggestion to open the associated document. The keyboard buffer is read and a query is sent to the RA every `2.5` seconds (see `RemembranceAgentClient::RA_CLIENT_UPDATE_PERIOD_MS`). Please note that the strings listed after the document name and `:` are [stemmed](https://en.wikipedia.org/wiki/Stemming). 

![](./docs/img/ra-client-with-suggestion.png)

However, to get suggestions to appear, you need to tell the RA what documents you would like indexed. A few different "document databases" are supported out-of-the-box:

1. Local documents:  
    Navigate to your user accounts's `Documents > RA > local-documents` folder. Here, you can paste in any `.txt` or `.md` files that will be indexed by the RA. (You will need to click on the `RA Settings > Invalid/reload cache` option to pick up these changes). This is the most robust document database option. Sample documents can be downloaded from this link and unzipped into the `local-documents` directory.

2. Further integrations include support for indexing specifically-labelled emails from Gmail and documents inside a specified folder from Google Drive. These integrations will be documented and made more user-friendly soon. 


## Developing and contributing

### Contributing

Contributors are always welcome! Please open an issue or pull-request with concerns or suggestions; you will get a response within a few business days. You can also contact me at `pramod.kotipalli@gmail.com`. (Please mention remembrance agents in the subject line.)

### Versioning

Increment the version number in the `VERSION` file. Version number changes will be propagated through the package during the build process where the number is needed. 

---

Pramod Kotipalli  
@p13i  
http://p13i.io
