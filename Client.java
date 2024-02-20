/*

*/

import java.io.InputStream;
import java.lang.reflect.Array;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

class Client {
   public static void main(String[] args) {
      final BufferedInputStream in = new BufferedInputStream(System.in);

      // constantly check for end of file
      // if eof comes before you expect, print an error and exit

      // Get the head
      byte head;
      try {
         head = (byte) in.read();
      } catch (IOException e) {
         System.out.println(e);
         head = 0;
      }

      // printIntOrDouble(in);

      // int byteCounter = 0;
      // System.out.printf("\nRead %d bytes from standard input.\n", byteCounter);
   }

   boolean isNumeric(byte head) {
      // decide if the input is numeric or
      int charOrNumeric = (head & 0xff) >> 7;
      System.out.println(charOrNumeric);

      if (charOrNumeric == 0) {
         System.out.println("Numeric");
         return true;
      } else {
         System.out.println("Character");
         return false;
      }
   }

   static void printIntOrDouble(BufferedInputStream in) {
      // read the message header for and print out if we're expecting an int or a
      // double
      // 00111001 = Most Significant -> Least significant
      // most significant bit = 0 = numeric
      // 0 = int
      // 1 = double

      // read the first byte
      byte[] b;
      byte m;
      try {
         // b = in.readNBytes(1);
         // m = b[0];
         byte head = (byte) in.read();
         m = head;
      } catch (IOException e) {
         System.out.println(e);
         m = (byte) 0;
      }

      int lsb = m & 1;
      for (int i = 8; i > 0; i--) {
         byte intOrDouble = (byte) (m & 1);
         System.out.println("Bit #" + i + " = " + intOrDouble);
         m = (byte) (m >> 1);
      }
      // System.out.println("Least Significant bit from first byte: " + lsb + "\n"
      // + "Most Significant bit from fist byte: " + msb);

      return;
   }

}
