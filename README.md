# Poem of the Day (PoD) Server

A simple Java TCP server that delivers poems to connected clients. The server loads poems from a text file, presents a numbered menu, and sends the selected poem based on client input.

## Features
- Java socket-based server
- Loads poems from a text file
- Menu-driven client interaction
- Input validation and error handling
- Finite-State Machine (FSM) based protocol design

## Requirements
- Java JDK 8 or higher

## How to Compile
```bash
javac PoDServer.java
How to Run
bash
Copy code
java PoDServer poems.txt
The server runs on port 9000.

Connecting as a Client
You can connect using telnet:

bash
Copy code
telnet localhost 9000
Protocol Summary
Client connects

Server sends welcome message and poem list

Client sends a poem number

Server returns poem or error message

Connection closes

Example Poem File Format
Poems should be separated by a blank line.

Author
Dylan Phillips
