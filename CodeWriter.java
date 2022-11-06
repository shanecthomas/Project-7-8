import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
  private File vmCode;
  private BufferedWriter assemblyCode;
  private static int count = 0;
  private String nameFile = null;
  private boolean firstPass = true;
  private String funcName = null;

  // constructor - intialize assembler
  public CodeWriter(File src, File dest) throws IOException {
    vmCode = src;
    assemblyCode = new BufferedWriter(new FileWriter(dest));
    String fileFullName = src.getName();
    int index = fileFullName.lastIndexOf(".");
    nameFile = fileFullName.substring(0, index);
  }

  public void setFileName(File src) {
    String fileFullName = src.getName();
    int index = fileFullName.lastIndexOf(".");
    nameFile = fileFullName.substring(0, index);

    vmCode = src;
  }

  public void isFile() {
    firstPass = false;
  }

  public void writeSysInit() throws IOException {
    assemblyCode.write
      ("@256\n" +
      "D=A\n" +
      "@SP\n" +
      "M=D\n\n" +
      "// call Sys.init 0\n");
      writeCall("Sys.init", 0);
      assemblyCode.write("0;JMP\n");
    firstPass = false;
  }

  public void writeArithmetic(String command) throws IOException {
    switch (command) {
      case "add": {
        assemblyCode.write
            ("@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=D+M\n");
        break;
      }
      case "sub": {
        assemblyCode.write
            ("@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=M-D\n");
        break;
      }
      case "neg": {
        assemblyCode.write
            ("@SP\n" +
            "A=M-1\n" +
            "M=-M\n");
        break;
      }
      case "eq": {
        String c = Integer.toString(count);
        count++;
        assemblyCode.write
          ("@SP\n" +
          "AM=M-1\n" +
          "D=M\n" +
          "A=A-1\n" +
          "D=M-D\n" +
          "@EQ.true." + c + "\n" +
          "D;JEQ\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=0\n" +
          "@EQ.after." + c + "\n" +
          "0;JMP\n" +
          "(EQ.true." + c + ")\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=-1\n" +
          "(EQ.after." + c + ")\n");
        break;
      }
      case "gt": {
        String c = Integer.toString(count);
        count++;
        assemblyCode.write
          ("@SP\n" +
          "AM=M-1\n" +
          "D=M\n" +
          "A=A-1\n" +
          "D=M-D\n" +
          "@GT.true." + c + "\n" +
          "D;JGT\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=0\n" +
          "@GT.after." + c + "\n" +
          "0;JMP\n" +
          "(GT.true." + c + ")\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=-1\n" +
          "(GT.after." + c + ")\n");
        break;
      }
      case "lt": {
        String c = Integer.toString(count);
        count++;
        assemblyCode.write
          ("@SP\n" +
          "AM=M-1\n" +
          "D=M\n" +
          "A=A-1\n" +
          "D=M-D\n" +
          "@LT.true." + c + "\n" +
          "D;JLT\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=0\n" +
          "@LT.after." + c + "\n" +
          "0;JMP\n" +
          "(LT.true." + c + ")\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=-1\n" +
          "(LT.after." + c + ")\n");
        break;
      }
      case "and": {
        assemblyCode.write
            ("@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=D&M\n");
        break;
      }
      case "or": {
        assemblyCode.write
            ("@SP\n" +
            "AM=M-1\n" +
            "D=M\n" +
            "A=A-1\n" +
            "M=D|M\n");
        break;
      }
      case "not": {
        assemblyCode.write
            ("@SP\n" +
            "A=M-1\n" +
            "M=!M\n");
        break;
      }
    }
  }

  public void writePushPop(CommandType command, String segment, int index) throws IOException {
    if (command.equals(CommandType.C_PUSH)) {
      switch (segment) {
        case "local": {
          assemblyCode.write
            ("@LCL\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "A=D+A\n" +
            "D=M\n");
          break;
        }
        case "argument": {
          assemblyCode.write
            ("@ARG\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "A=D+A\n" +
            "D=M\n");
          break;
        }
        case "this": {
          assemblyCode.write
            ("@THIS\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "A=D+A\n" +
            "D=M\n");
          break;
        }
        case "that": {
          assemblyCode.write
            ("@THAT\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "A=D+A\n" +
            "D=M\n");
          break;
        }
        case "pointer": {
          if (index == 0) {
            assemblyCode.write
              ("@THIS\n" +
              "D=M\n");
            break;
          }
          else {
            assemblyCode.write
              ("@THAT\n" +
              "D=M\n");
            break;
          }
        }
        case "static": {
          assemblyCode.write
            ("@" + nameFile + "." + index + "\n" +
            "D=M\n");
          break;
        }
        case "constant": {
          assemblyCode.write
            ("@" + index + "\n" +
            "D=A\n");
          break;
        }
        case "temp": {
          assemblyCode.write
            ("@R5\n" +
            "D=A\n" +
            "@" + index + "\n" +
            "A=D+A\n" +
            "D=M\n");
          break;
        }
        default: {
          break;
        }
      }
      //push
      assemblyCode.write
        ("@SP\n" +
        "A=M\n" +
        "M=D\n" +
        "@SP\n" +
        "M=M+1\n");
    }

    else {
      switch (segment) {
        case "local": {
          assemblyCode.write
            ("@LCL\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "D=D+A\n");
          break;
        }
        case "argument": {
          assemblyCode.write
            ("@ARG\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "D=D+A\n");
          break;
        }
        case "this": {
          assemblyCode.write
            ("@THIS\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "D=D+A\n");
          break;
        }
        case "that": {
          assemblyCode.write
            ("@THAT\n" +
            "D=M\n" +
            "@" + index + "\n" +
            "D=D+A\n");
          break;
        }
        case "pointer": {
          if (index == 0) {
            assemblyCode.write
              ("@THIS\n" +
              "D=A\n");
            break;
          }
          else {
            assemblyCode.write
              ("@THAT\n" +
              "D=A\n");
            break;
          }
        }
        case "static": {
          assemblyCode.write
            ("@" + nameFile + "." + index + "\n" +
            "D=A\n");
          break;
        }
        case "temp": {
          assemblyCode.write
            ("@R5\n" +
            "D=A\n" +
            "@" + index + "\n" +
            "D=D+A\n");
          break;
        }
        default: {
          break;
        }
      }
      //pop
      assemblyCode.write
        ("@R13\n" +
        "M=D\n" +
        "@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "@R13\n" +
        "A=M\n" +
        "M=D\n");
    }
  }

  public void writeLabel(String label) throws IOException {
    if (funcName == null)
      assemblyCode.write
        ("(" + label + ")\n");
    else
      assemblyCode.write
        ("(" + funcName + "$" + label + ")\n");
  }

  public void writeGoto(String label) throws IOException {
    if (funcName == null)
      assemblyCode.write
        ("@" + label + "\n" +
        "0;JMP\n");
    else
      assemblyCode.write
        ("@" + funcName + "$" + label + "\n" +
        "0;JMP\n");
  }

  public void writeIf(String label) throws IOException {
    if (funcName == null)
      assemblyCode.write
        ("@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "@" + label + "\n" +
        "D;JNE\n");
    else
      assemblyCode.write
        ("@SP\n" +
        "AM=M-1\n" +
        "D=M\n" +
        "@" + funcName + "$" + label + "\n" +
        "D;JNE\n");
  }

  public void writeFunction(String functionName, int nVars) throws IOException {
    assemblyCode.write
      ("(" + functionName + ")\n" +
      "@SP\n" +
      "A=M\n");
    for (int i = 0; i < nVars; i++) {
      assemblyCode.write
        ("M=0\n" +
        "A=A+1\n");
    }
    assemblyCode.write
      ("D=A\n" +
      "@SP\n" +
      "M=D\n");
  }

  public void writeCall(String functionName, int nArgs) throws IOException {
    String c = Integer.toString(count);
    count++;
    assemblyCode.write
      ("@SP\n" +    // SP = R13
      "D=M\n" +
      "@R13\n" +
      "M=D\n" +

      "@RET." + c + "\n" +      // SP = returnAddress
      "D=A\n" +
      "@SP\n" +
      "A=M\n" +
      "M=D\n" +

      "@SP\n" +      // SP++
      "M=M+1\n" +

      "@LCL\n" +      // SP = LCL
      "D=M\n" +
      "@SP\n" +
      "A=M\n" +
      "M=D\n" +

      "@SP\n" +      // SP++
      "M=M+1\n" +

      "@ARG\n" +      // SP = ARG
      "D=M\n" +
      "@SP\n" +
      "A=M\n" +
      "M=D\n" +

      "@SP\n" +      // SP++
      "M=M+1\n" +

      "@THIS\n" +      // SP = THIS
      "D=M\n" +
      "@SP\n" +
      "A=M\n" +
      "M=D\n" +

      "@SP\n" +      // SP++
      "M=M+1\n" +

      "@THAT\n" +      // SP = THAT
      "D=M\n" +
      "@SP\n" +
      "A=M\n" +
      "M=D\n" +

      "@SP\n" +      // SP++
      "M=M+1\n" +

      "@R13\n" +      // ARG = SP - 5 - nArgs
      "D=M\n" +
      "@" + nArgs + "\n" +
      "D=D-A\n" +
      "@ARG\n" +
      "M=D\n" +

      "@SP\n" +      // LCL = SP
      "D=M\n" +
      "@LCL\n" +
      "M=D\n" +
      "@" + functionName + "\n" + // goto functionName
      "0;JMP\n" +
      "(RET." + c + ")\n");   // (returnAddress)
  }

  public void writeReturn() throws IOException {
    assemblyCode.write
      ("@LCL\n" + // frame = LCL
      "D=M\n" +
       
      "@5\n" +  // retAddr = *(frame - 5)
      "A=D-A\n" +
      "D=M\n" +
      "@R13\n" +
      "M=D\n" +

      "@SP\n" +      // *ARG = pop()
      "A=M-1\n" +
      "D=M\n" +
      "@ARG\n" +
      "A=M\n" +
      "M=D \n" +

      "D=A+1\n" +      // SP = ARG++
      "@SP\n" +
      "M=D\n" +

      "@LCL\n" +      // THAT = *(frame - 1)
      "AM=M-1\n" +
      "D=M\n" +
      "@THAT\n" +
      "M=D\n" +

      "@LCL\n" +      // THIS = *(frame - 2)
      "AM=M-1\n" +
      "D=M\n" +
      "@THIS\n" +
      "M=D\n" +

      "@LCL\n" +      // ARG = *(frame - 3)
      "AM=M-1\n" +
      "D=M\n" +
      "@ARG\n" +
      "M=D\n" +

      "@LCL\n" +      // LCL = *(frame - 4)
      "A=M-1\n" +
      "D=M\n" +
      "@LCL\n" +
      "M=D\n" +

      "@R13\n" +      // goto retAddr
      "A=M\n" +
      "0;JMP\n");
  }

  public void close() throws IOException {
    assemblyCode.flush();
    assemblyCode.close();
  }

  public void parse() throws IOException {
    Parser parser = new Parser(vmCode);
    if (parser.hasMoreLines() && firstPass)
      writeSysInit();
    while (parser.hasMoreLines()) {
      parser.advance();
      assemblyCode.write("\n// " + parser.getCurrentLine() + "\n");

      CommandType commandType = parser.commandType();
      String arg1 = null;
      int arg2 = 0;

      if (!commandType.equals(CommandType.C_RETURN)) {
        arg1 = parser.arg1();
        switch (commandType.toString()) {
          case "C_ARITHMETIC": {
            writeArithmetic(arg1);
            break;
          }
          case "C_LABEL": {
            writeLabel(arg1);
            break;
          }
          case "C_GOTO": {
            writeGoto(arg1);
            break;
          }
          case "C_IF": {
            writeIf(arg1);
            break;
          }
          case "C_PUSH":
          case "C_POP": {
            arg2 = parser.arg2();
            writePushPop(commandType, arg1, arg2);
            break;
          }
          case "C_CALL": {
            arg2 = parser.arg2();
            writeCall(arg1, arg2);
            break;
          }
          case "C_FUNCTION": {
            funcName = arg1;
            arg2 = parser.arg2();
            writeFunction(arg1, arg2);
            break;
          }
        }
      }

      else {
        writeReturn();
      }
    }
    parser.close();
    funcName = null;
  }
  
}