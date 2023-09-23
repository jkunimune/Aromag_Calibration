# Aromagtic alibration

Before running the script, you'll probably want to unplug all output wires from the Arcogam.

To run this script, you'll first need to make sure it's compiled by navigating to the Aromag_Calibration directory and calling
~~~bash
javac --class-path=lib/jamod-1.2.jar --source-path=src src/Program.java
~~~

Then, from the same directory, you can run it with
~~~bash
java --class-path=lib/jamod-1.2.jar:src Program
~~~

It will print out fourteen lists of numbers in columns, along with an index column, over the course of ten minutes or so.  Each column represents the response of one output channel.  If the aroma works prefectly, then each list will equal the index.  in practice, there will be some deviation from these ideal outputs.  the outputs are automaticly saved to the file calibration.dat.  you can load them into python or something to plot them and decide whether the deviations from ideal are acceptable or not.
