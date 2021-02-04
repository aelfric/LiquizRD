# LiquizRD
This is a partial attemp to convert the LiquiZ  parser (https://github.com/StevensDeptECE/LiquiZ9) into Java and a partial attempt to 
upload the parsed quiz into Canvas LMS.

To run this, copy `src/main/resources/config.properties.template` to `src/main/resources/config.properties` and 
edit it accordingly.

Then run the main method in `src/main/java/org/liquiz/canvas/CanvasClient`.  It currenty only reads from `src/main/resources/data-types.lq`.

The parser can read much of the Liquiz syntax but can currently only covert predefined choice questions into something Canvas can interpret.
