# Ring Communication

Ring network implementation. This is a student project.

## Getting Started

TODO

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

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for more details.
