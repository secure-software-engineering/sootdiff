import java.io.*;

// This program shows a stack track that occurs when java
// encounters a terminal error when running a program.

public class DivBy0
{

 public static void funct1 ()
 {
  System.out.println ("Inside funct1()");

  funct2();
 }

 public static void main (String[] args)
 {
  int val;

  System.out.println ("Inside main()");

  funct1();

 }

 public static void funct2 ()
 {
  System.out.println ("Inside funct2()");
  int i, j, k;

  i = 10;
  j = 0;

  k = i/j;
 }

}
