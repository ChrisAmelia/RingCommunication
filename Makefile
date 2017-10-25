EXEC=Link.java Connect.java Read.java Write.java Options.java Message.java Main.java

all: $(EXEC)
	@javac $^

run: Main.class
	@java Main $(tcp) $(udp)

Main.class: Main.java
	@javac Main.java

clean:
	rm -rf *.class
