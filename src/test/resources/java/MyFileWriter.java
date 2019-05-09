import java.io.*;
import java.util.*;

public class MyFileWriter
{

 public static void main (String[] args) throws java.io.IOException
 {

  String s1;
  String s2;
  boolean cont = true;

  // set up the buffered reader to read from the keyboard
  BufferedReader br = new BufferedReader (new InputStreamReader (
			System.in));

  // Set up the output file
  FileWriter fw = new FileWriter ("MyFileWriter.txt");
  BufferedWriter bw = new BufferedWriter (fw);
  PrintWriter pw = new PrintWriter (bw);

  while ( cont )
     {
      System.out.println ("Enter a line of input");

      s1 = br.readLine();

      System.out.println ("The line has " + s1.length() + " characters");

      if (s1.length() == 0)
	{
	 cont = false;
	}
      else
	{
	 pw.println (s1);
        }
     }
  pw.close();
 }
}
