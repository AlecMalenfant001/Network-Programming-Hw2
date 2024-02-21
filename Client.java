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
         System.out.println(isNumeric(head)); // remove this later
         // TODO : Check for end of file
      } catch (IOException e) {
         System.out.println(e);
         head = 0;
         System.exit(1);
      }

      // printDouble(in);

      // printIntOrDouble(in, head);

      // int byteCounter = 0;
      // System.out.printf("\nRead %d bytes from standard input.\n", byteCounter);
   }

   private static boolean isNumeric(byte head) {
      // decide if the input is numeric or
      int charOrNumeric = (head & 0xff) >> 7;
      if (charOrNumeric == 0) {
         System.out.println("Numeric");
         return true;
      } else {
         System.out.println("Character");
         return false;
      }
   }

   private static void printIntOrDouble(BufferedInputStream in, byte head) {
      // read the message header for and print out if we're expecting an int or a
      // double
      // 00111001 = Most Significant -> Least significant
      // most significant bit = 0 = numeric
      // 0 = int
      // 1 = double

      int lsb = head & 1;
      for (int i = 8; i > 0; i--) {
         byte intOrDouble = (byte) (head & 1);
         System.out.println("Bit #" + i + " = " + intOrDouble);
         head = (byte) (head >> 1);
      }
      // System.out.println("Least Significant bit from first byte: " + lsb + "\n"
      // + "Most Significant bit from fist byte: " + msb);

      return;
   }

   static void printInt(BufferedInputStream in) {
      // created buffer
      ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.BYTES);
      try {
         // read bytes in weird endian = {3rd, 2nd, 1st, 4th} most significant bit
         intByteBuffer.put(2, (byte) in.read());
         intByteBuffer.put(1, (byte) in.read());
         intByteBuffer.put(0, (byte) in.read());
         intByteBuffer.put(3, (byte) in.read());

         // convert to int and print result
         System.out.println("intByteBuffer: " + intByteBuffer.getInt() + "\n");

      } catch (IOException e) {
         System.out.println("Unexpected error whilte reading bytes into intByteBuffer:\n" + e);
         e.printStackTrace();
      }
   }

   static void printDouble(BufferedInputStream in) {
      // Read Double
      // create bytebuffer
      ByteBuffer doubleByteBuffer = ByteBuffer.allocate(Double.BYTES);
      // read bytes backwards
      try {
         for (int i = 7; i >= 0; i--) {
            doubleByteBuffer.put(i, (byte) in.read());
         }
         // convert to double and print result
         System.out.printf("\ndoubleByteBuffer : %.12f \n", doubleByteBuffer.getDouble());

      } catch (IOException e) {
         System.out.println("Unexpected error whilte reading bytes into doubleByteBuffer:\n" + e);
         e.printStackTrace();
      }

   }

}
