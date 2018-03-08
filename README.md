# Ring Communication

<h1 align="center">
    <img src="https://upload.wikimedia.org/wikipedia/commons/d/db/NetworkTopology-Ring.png" alt="network ring topology">
</h1>

Ring network implementation.

## Getting Started

### Prequisites

Java 8 and Python3.

## Running

First move to the source folder:

    $ cd src

### Python

Run the script:

    $ ./run.py

Then the ring running with default parameters. You may also give the TCP and UDP ports:

    $ ./run.py PORT_TCP PORT_UDP

### If Python3 is not suported

Then run `make`:

    $ make

And finally run the compiled project with default parameters:

    $ make run

Or you can precise the UDP and TCP ports:

    $ make run tcp=4242 udp=4545

## Basic commands

Once the ring is running, it is recommended to run `help` command:

    $ help

Here's a few available commands :

    $ TCP ip port

    establish a TCP connection with the given IP and port to a running ring,
    if the ring accepts the request then a message "WELC" will be receivd, else "NOTC".

    $ WHOS

    send the message "WHOS" on the current ring to know who are currently present on it.
    Each node sends back a message "MEMB" with additionnal informations.

    $ GBYE

    request to exit the current ring. The current node insures to carry out its part
    until it leaves the ring.

    $ TEST

    send the message "TEST" on the current ring. If after a certain amount of time (default 10 sec),
    this message is not received back then the message "DOWN" is sent on the whole ring.
    Every node then disconnects.

    $ EXIT

    close the program.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for more details.
