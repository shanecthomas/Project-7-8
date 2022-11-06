import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
  private BufferedReader file;
  private String currentLine = null;
  private String nextLine;

  //constructor - opens input file / stream & gets ready to parse it 
  public Parser (File f) throws IOException {
    file = new BufferedReader(new FileReader(f));
    nextLine = this.getNextLine();
  }

  public void close() throws IOException {
    file.close();
  }

  //grabs next line, skips white space/comments
  //assumes hasMoreLines == true
  public String getNextLine() throws IOException {
    String line;

    do {
      line = file.readLine();
      if (line == null)
        return null;
    }
      while (line.trim().isEmpty() || line.trim().startsWith("//"));

    int comment = line.indexOf("//");
    if (comment != -1)
      line = line.substring(0, comment - 1);
    
    return line;
  }

  //is file empty?
  public boolean hasMoreLines() {
    return (nextLine != null);
  }

  //read next line
  public void advance() throws IOException {
    currentLine = nextLine;
    nextLine = this.getNextLine();
  }

  //return current line
  public String getCurrentLine() {
    return currentLine;
  }

  //Returns command type
  public CommandType commandType() {
    String line = currentLine.trim();
    String commands[] = line.split(" ");

    switch (commands[0]) {
      case "add":
      case "sub":
      case "neg":
      case "eq":
      case "gt":
      case "lt":
      case "and":
      case "or":
      case "not": {
        return CommandType.C_ARITHMETIC;
        }
      case "push": {
        return CommandType.C_PUSH;
        }
      case "pop": {
        return CommandType.C_POP;
        }
      case "label": {
        return CommandType.C_LABEL;
        }
      case "goto": {
        return CommandType.C_GOTO;
        }
      case "if-goto": {
        return CommandType.C_IF;
        }
      case "function": {
        return CommandType.C_FUNCTION;
        }
      case "return": {
        return CommandType.C_RETURN;
        }  
      case "call": {
        return CommandType.C_CALL;
        }
      default: {
        return null;
        }
    }
  }

  //returns 1st argument - NOT FOR return
  public String arg1() {
    String line = currentLine.trim();
    String commands[] = line.split(" ");

    switch (commands[0]) {
      case "add":
      case "sub":
      case "neg":
      case "eq":
      case "gt":
      case "lt":
      case "and":
      case "or":
      case "not": {
        return commands[0];
      }
      case "push":
      case "pop":
      case "label":
      case "goto":
      case "if-goto":
      case "function":
      case "call": {
        return commands[1];
        }
    }

    return null;
  }

  //returns 2nd argument - ONLY FOR Push/Pop/Function/Call
  public int arg2() {
    String line = currentLine.trim();
    String commands[] = line.split(" ");

    return Integer.parseInt(commands[2]);
  }
}
