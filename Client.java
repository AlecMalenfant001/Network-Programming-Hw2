/*
      Course: CS 33600
      Name: Alec Malenfant
      Email: amalenf@pnw.edu
      Assignment: 2
   */

import java.io.InputStream;
import java.lang.reflect.Array;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

class Client {
   private static int byteCounter = 0;

   public static void main(String[] args) {
      final BufferedInputStream in = new BufferedInputStream(System.in);

      while (true) {
         int input;
         byte head;
         try {
            // Get the head of the next message
            input = in.read();
            byteCounter++;
            checkForEndOfFile(input);
            head = (byte) input; // convert from int to byte
            checkForEndOfTransmission(head);

         } catch (IOException e) {
            head = 0;
            System.out.printf("\nRead %d bytes from standard input.", byteCounter);
            System.out.println("Unexpected IO error while reading the next head\n" + e);
            System.exit(1);
         }

         // decide if the input is made of characters or numbers
         int charOrNumeric = (head & 0xff) >> 7; // get most significant bit
         if (charOrNumeric == 0) {
            numericLoop(in, head);
         } else {
            printChars(in, head);
         }

      }

   }

   /**
    * This method reads the last 7 bits of the head to determine how many
    * characters the client should expect from the server. Then this method reads
    * that many characters from the inputstream and prints them to standard out.
    * 
    * @param in   the input stream to read from
    * @param head the byte to be analyzed
    */
   private static void printChars(BufferedInputStream in, byte head) {
      int numChars = (head & 0x7f); // get the last 7 bits of the head

      try {
         // read the number of characters from standard input and print them out
         for (int i = 0; i < numChars; i++) {
            int inputCharacter = in.read();
            byteCounter++;
            checkForEndOfFile(inputCharacter);
            System.out.print((char) inputCharacter);
         }
         System.out.print("\n");

      } catch (IOException e) {
         System.out.printf("\nRead %d bytes from standard input.", byteCounter);
         System.out.println("\nUnexpected end of file while reading charcater : \n" + e);
         e.printStackTrace();

      }

      return;
   }

   /**
    * The main loop for numeric inputs to the client.
    * The method reads the least significant bit of the head to determine if the
    * next number from the server is an integer or a
    * double. The method repeats this for the last 7 bits in the head.
    * 0 = Int,
    * 1 = double.
    * The method then calls either the {@link #printInt() printInt} method or the
    * {@link #printDouble() printDouble} method
    * 
    * @param in   the input stream to read from
    * @param head the byte to be analyzed
    */
   private static void numericLoop(BufferedInputStream in, byte head) {
      // 00111001 = Most Significant -> Least significant
      // most significant bit = 0 = numeric
      // 0 = int
      // 1 = double
      // least significatn bit = head & 1;

      for (int i = 8; i > 1; i--) {
         byte intOrDouble = (byte) (head & 1); // get least significant bit

         // check if the next number from the input stream is an int or a double
         if ((int) intOrDouble == 0) {
            printInt(in);
         } else {
            printDouble(in);
         }
         head = (byte) (head >> 1); // go to next bit
      }

      return;

   }

   /**
    * Reads 4 bytes in weird endian order as defined in the assginment page.
    * Weird Endian = 3rd, 2nd, 1st, 4th most significant bit.
    * Those bytes are then converted into an int and then printed to standard out.
    * 
    * @param in the input stream to read from
    */
   private static void printInt(BufferedInputStream in) {
      ByteBuffer intByteBuffer = ByteBuffer.allocate(Integer.BYTES);

      try {
         // read bytes in weird endian order = {3rd, 2nd, 1st, 4th} most significant bit
         int intByte;

         // read 3rd byte of ByteBuffer
         intByte = in.read();
         byteCounter++;
         checkForEndOfFile(intByte);
         intByteBuffer.put(2, (byte) intByte);

         // read 2nd byte of ByteBuffer
         intByte = in.read();
         byteCounter++;
         checkForEndOfFile(intByte);
         intByteBuffer.put(1, (byte) intByte);

         // read 1st byte of ByteBuffer
         intByte = in.read();
         byteCounter++;
         checkForEndOfFile(intByte);
         intByteBuffer.put(0, (byte) intByte);

         // read 4th byte of ByteBuffer
         intByte = in.read();
         byteCounter++;
         checkForEndOfFile(intByte);
         intByteBuffer.put(3, (byte) intByte);

         // convert to int and print result
         System.out.println(intByteBuffer.getInt());

      } catch (IOException e) {
         System.out.printf("\nRead %d bytes from standard input.", byteCounter);
         System.out.println("Unexpected error while reading bytes into intByteBuffer:\n" + e);
         e.printStackTrace();
      }

      return;
   }

   /**
    * Reads 8 bytes from an input stream in little-endian order.
    * Those bytes are then converted into a double that is printed to standard out.
    * 
    * @param in the input stream to read from
    */
   private static void printDouble(BufferedInputStream in) {
      ByteBuffer doubleByteBuffer = ByteBuffer.allocate(Double.BYTES);

      try {
         // read bytes backwards
         for (int i = 7; i >= 0; i--) {
            int doubleByte = in.read();
            byteCounter++;
            checkForEndOfFile(doubleByte);
            doubleByteBuffer.put(i, (byte) doubleByte); // put byte into corresponding location in the buffer
         }
         System.out.printf("%.12f \n", doubleByteBuffer.getDouble()); // convert to double and print result

      } catch (IOException e) {
         System.out.printf("\nRead %d bytes from standard input.", byteCounter);
         System.out.println("Unexpected error while reading bytes into doubleByteBuffer:\n" + e);
         e.printStackTrace();
         System.exit(1);
      }

      return;

   }

   /**
    * Tests a byte for the end of transmission character that is defined for the
    * assignment's
    * protocol.
    * If true, then the method will print the number of bytes
    * read from standard input and then exit.
    * 
    * @param head is the byte that you want to test
    */
   private static void checkForEndOfTransmission(Byte head) {
      if (head == -128) {
         System.out.printf("\nRead %d bytes from standard input.\n", byteCounter);
         System.exit(0);
      }
      return;
   }

   /**
    * Checks for an unexpected end of file from a given integer.
    * If the given int = -1, then the method will print an error message the number
    * of bytes
    * read from standard input.
    * This works becaus The read() method of an InputStream returns -1 when the end
    * of the file is reached. However, it returns this as an int, not a byte. This
    * is because a byte in Java is signed and has a range from -128 to 127, so -1
    * can be a valid byte value.
    * 
    * 
    * @param head the byte that you want to test
    */
   private static void checkForEndOfFile(int head) {
      if (head == -1) {
         System.out.println("Head: " + (byte) head);
         System.out.println("Test Condition: " + (head == -1));
         System.out.printf("\nUnexpected end of file \nRead %d bytes from standard input.\n", byteCounter);
         System.exit(1);
      }
      return;
   }

}
