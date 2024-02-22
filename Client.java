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

      // TODO :
      /*
       * 1) Check for unexpected End of files
       * 2) Make it loop until it reaches the end of file so that the client can
       * recieve multiple messages
       * 3) Test, Test, Test
       * 4) Javadocs
       */

      // Get the head
      byte head;
      try {
         head = (byte) in.read();
         // TODO : Check for end of file
      } catch (IOException e) {
         System.out.println(e);
         head = 0;
         System.exit(1);
      }

      // decide if the input is numeric or
      int charOrNumeric = (head & 0xff) >> 7; // get most significant bit
      if (charOrNumeric == 0) {
         numericLoop(in, head);
      } else {
         printChars(in, head);
      }

      // printInt(in);

      // int byteCounter = 0;
      // System.out.printf("\nRead %d bytes from standard input.\n", byteCounter);
   }

   private static void printChars(BufferedInputStream in, byte head) {
      System.out.println("Character");

      int numChars = (head & 0x7f); // get the last 7 bits of the head
      System.out.println("numChars: " + numChars);
      try {
         for (int i = 0; i < numChars; i++) {
            char inputCharacter = (char) in.read();
            System.out.print(inputCharacter);
         }
         // TODO : Check for eof()

      } catch (IOException e) {
         System.out.println("\nUnexpected end of file while reading charcater : \n" + e);
         e.printStackTrace();

      }

      return;
   }

   private static void numericLoop(BufferedInputStream in, byte head) {
      // 00111001 = Most Significant -> Least significant
      // most significant bit = 0 = numeric
      // 0 = int
      // 1 = double
      // Loop through the next 7 numbers from the input stream

      // check the bit
      int lsb = head & 1;
      for (int i = 8; i > 1; i--) {
         byte intOrDouble = (byte) (head & 1);
         System.out.println("\nBit #" + i + " = " + intOrDouble);

         // check if int or double
         if ((int) intOrDouble == 0) {
            printInt(in);
         } else {
            printDouble(in);
         }

         head = (byte) (head >> 1);
      }

   }

   private static void printInt(BufferedInputStream in) {
      // created buffer
      ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.BYTES);
      try {
         // read bytes in weird endian = {3rd, 2nd, 1st, 4th} most significant bit
         intByteBuffer.put(2, (byte) in.read());
         intByteBuffer.put(1, (byte) in.read());
         intByteBuffer.put(0, (byte) in.read());
         intByteBuffer.put(3, (byte) in.read());
         // TODO: look for eof char

         // convert to int and print result
         System.out.println("intByteBuffer: " + intByteBuffer.getInt());

      } catch (IOException e) {
         System.out.println("Unexpected error whilte reading bytes into intByteBuffer:\n" + e);
         e.printStackTrace();
      }
   }

   private static void printDouble(BufferedInputStream in) {
      // Read Double
      // create bytebuffer
      ByteBuffer doubleByteBuffer = ByteBuffer.allocate(Double.BYTES);
      // read bytes backwards
      try {
         for (int i = 7; i >= 0; i--) {
            doubleByteBuffer.put(i, (byte) in.read());
            // TODO: look for eof char
         }
         // convert to double and print result
         System.out.printf("doubleByteBuffer : %.12f \n", doubleByteBuffer.getDouble());

      } catch (IOException e) {
         System.out.println("Unexpected error whilte reading bytes into doubleByteBuffer:\n" + e);
         e.printStackTrace();
      }

   }

}
