import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class VMTranslator {
  public VMTranslator () {
    System.out.println("Enter a file/directory to convert: ");
    Scanner keyboard = new Scanner(System.in);
    String input = keyboard.nextLine();
    File srcFile = new File(input);
    File destFile = null;

    
    if (!srcFile.exists()) {
      System.out.println("File doesn't exist. Exiting program.");
      System.exit(0);
    }

    if (!srcFile.isDirectory()) {
      String fileFullName = srcFile.getName();
      int index = fileFullName.lastIndexOf(".");
      String fileName = fileFullName.substring(0, index);
      String outFileName = fileName + ".asm";
      destFile = new File(outFileName);
    }

    else {
      String outFileName = srcFile.getName() + ".asm";
      destFile = new File(outFileName);
    }

    try {
      if (destFile.exists()) {
        destFile.delete();
      }
      destFile.createNewFile();

      if (!srcFile.isDirectory()) {
        CodeWriter codeWriter = new CodeWriter(srcFile, destFile);
        codeWriter.isFile();
        codeWriter.parse();
        codeWriter.close();
      }
        
      else {
        File[] directory = srcFile.listFiles();
        CodeWriter codeWriter = null;
        boolean firstPass = true;
        for ( File f : directory) {
          String fileFullName = f.getName();
          int index = fileFullName.lastIndexOf(".");
          String fileName = fileFullName.substring(index);
          if (fileName.equals(".vm")) {
            if (firstPass) {
              codeWriter = new CodeWriter(f, destFile);
              codeWriter.parse();
              firstPass = false;
            }
            else {
              codeWriter.setFileName(f);
              codeWriter.parse();
            }
          }
        }
        codeWriter.close();
      }
      
      System.out.println(destFile + " has been created!");
      keyboard.close();
    }

    catch (IOException e) {
      if (destFile.exists())
        System.out.println("IOExcpetion error caught.\n" + destFile + " has been created!");
      else
        System.out.println("An unknown error occurred.");
      System.exit(0);
    }
  }
}
