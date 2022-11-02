# SimBionic
SimBionic software makes it possible to specify real-time intelligent software agents quickly, visually, and intuitively by drawing and configuring behavioral transition networks (**BTNs**). Each BTN is a network of nodes connected by connectors (links), similar to a flow chart or finite state machine. Visual logic also makes it easy to show, discuss, and verify the behaviors with members of the development team, subject matter experts, and other stakeholders. BTNs are especially effective for developing tactical decision-making modules, real-time reactive planners, and adaptive execution systems which detect, track, and classify complex sequences of events and state conditions and then take appropriate actions.

SimBionic supports the feature set needed to build robust, intelligent, real-time systems. For example, BTNs can access local and global variables and can call JavaScript functions and Java methods, using the JavaScript engine embedded within the Java run-time system. BTNs are hierarchical: a node within a BTN can invoke other BTNs. Hierarchical BTNs simplify the logic of higher-level BTNs by encapsulating details within lower-level BTNs. BTNs can read and write from/to message queues and blackboards to enable agents to cooperate and share information. Support for exception handlers make it possible to develop agent behaviors which handle unexpected events and situations cleanly and gracefully. 

SimBionic software provides three components. The **SimBionic Visual IDE** (or Editor) application enables developers to specify intelligent agent behaviors by creating and saving BTNs that are read and executed by the **SimBionic Run-time System**.  This run-time software library can be embedded within a Java® software application to query for state information and execute actions, as specified by the BTNs. **The SimBionic Debugger** application helps developers test and debug behavior logic by stepping through the execution of the BTNs and inspecting the values of local and global variables.

![Hello World Image](https://github.com/StottlerHenkeAssociates/SimBionic/blob/master/samples/HelloWorld/HelloWorld.png)

## How to build this software
From within the SimBionic directory, issue the following command to create a distribution:

`gradlew dist`

To create an eclipse project, use:

`gradlew eclipse`

For more information, see BUILD README.txt
