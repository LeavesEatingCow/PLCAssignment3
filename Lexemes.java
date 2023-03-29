import javax.swing.plaf.nimbus.State;
import java.io.*;
import java.util.*;

public class Lexemes {

    int c;
    char currChar;
    String lexeme = "";
    List<String> lex = new ArrayList<>();
    HashMap<String, String> map = new HashMap<>();


    public static void main(String[] args) {
        Lexemes lexemes = new Lexemes();
        Scanner console = new Scanner(System.in);

        //Mapping acceptable operations
        lexemes.map.put("+", "Addition Operation");
        lexemes.map.put("-", "Subtraction Operation");
        lexemes.map.put("*", "Multiplication Operation");
        lexemes.map.put("/", "Division Operation");
        lexemes.map.put("%", "Modulo Operation");
        lexemes.map.put("(", "Left Parenthesis");
        lexemes.map.put(")", "Right Parenthesis");
        lexemes.map.put("=", "Assignment operation");
        lexemes.map.put("==", "Equals Operation");
        lexemes.map.put("<", "Less than operation");
        lexemes.map.put("<=", "Less than or equal to operation");
        lexemes.map.put(">", "Greater than operation");
        lexemes.map.put(">=", "Greater than or equal to operation");
        lexemes.map.put("&&", "Logical and operation");
        lexemes.map.put("||", "Logical or operation");

        //Locate file and read file
        try {
            String filePath;
            System.out.print("Enter the path of your file >> ");
            filePath = console.nextLine();
            File f = new File(filePath);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            do {
                lexemes.getChar(br);
            }while(lexemes.c != -1);
            System.out.println(lexemes.lexeme);

        }catch(IOException e){
            e.printStackTrace();
        }

        //Print to screen all the tokens found
        int i = 1;
        for(String lexeme: lexemes.lex){
            System.out.print(i + ". ");
            if(lexemes.map.containsKey(lexeme)){
                System.out.println("Lexeme: " + lexeme + "\tToken: " + lexemes.map.get(lexeme));
            }else{
                System.out.println("Error: " + lexeme + "\tToken: Unidentifiable");
            }
            i++;
        }


    }

    //Stores characters that aren't letters or numbers to see if they can be found in the "map"
    public void lookup(BufferedReader br) throws IOException{
        switch(currChar){
            case ' ':
                getChar(br);
                break;
            case '+':
                lex.add("+");
                getChar(br);
                break;
            case '-':
                lex.add("-");
                getChar(br);
                break;
            case '*':
                lex.add("*");
                getChar(br);
                break;
            case '/':
                lex.add("/");
                getChar(br);
                break;
            case '%':
                lex.add("%");
                getChar(br);
                break;
            case '(':
                lex.add("(");
                getChar(br);
                break;
            case ')':
                lex.add(")");
                getChar(br);
                break;
            case '=':
                if(lexeme.equals("<")){
                    lex.remove("<");
                    lex.add("<=");
                }else if(lexeme.equals(">")){
                    lex.remove(">");
                    lex.add(">=");
                }else if(lexeme.equals("=")){
                    lex.remove("=");
                    lex.add("==");
                }else {
                    addChar();
                    lex.add("=");
                }
                break;
            case '<':
                addChar();
                lex.add("<");
                break;
            case '>':
                addChar();
                lex.add(">");
                break;
            case '&':
                if(lexeme.equals("&")) {
                    lex.add("&&");
                }
                addChar();
                break;
            case '|':
                if(lexeme.equals("|")) {
                    lex.add("||");
                }
                addChar();
                break;
        }
    }

    //Basic method to concatenate characters to a lexeme and reading next character
    public void getc(BufferedReader br) throws IOException {
        addChar();
        c = br.read();
        currChar = (char) c;
    }

    //A DFA approach to identifying an identifier regex: ([a-zA-Z_]([a-zA-Z0-9_])*)
    public void tokenIden(BufferedReader br) throws IOException{
        Identifiers currentId = Identifiers.start;
        lexeme = "";
        while(c != -1 && currentId != Identifiers.t){
            switch (currentId){
                case start:
                    if(Character.isLetter(currChar) || currChar == '_'){
                        currentId = Identifiers.a;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        currentId = Identifiers.t;
                    }
                    break;
                case a:
                    if(Character.isDigit(currChar) || Character.isLetter(currChar) || currChar =='_'){
                        currentId = Identifiers.a;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        map.put(lexeme, "Variable identifiers");
                        currentId = Identifiers.t;
                    }
                    break;
                case t:

            }
        }

        if(c == -1){
            lex.add(lexeme);
            if(currentId != Identifiers.start){
                if(currentId == Identifiers.a){
                    map.put(lexeme, "Variable identifiers");
                }
            }
        }
        lexeme = "";
    }

    //Skips whitespace
    public void getWhiteSpace(BufferedReader br) throws IOException{
        while(Character.isWhitespace(currChar)){
            c = br.read();
            currChar = (char) c;
        }
    }

    //A DFA approach to identifying integers and floating point numbers. If acceptable number found it'll be mapped, otherwise it'll not.
    public void tokenNum(BufferedReader br) throws IOException{
        Numbers currentNum = Numbers.start;
        lexeme = "";
        while(c != -1 && currentNum != Numbers.h && currentNum != Numbers.t){
            switch (currentNum){
                case start:
                    if(currChar == '+' || currChar == '-'){
                        currentNum = Numbers.sign;
                        getc(br);
                    }else if(Character.isDigit(currChar)){
                        currentNum = Numbers.c;
                        getc(br);
                    }else if(currChar == '.'){
                        currentNum = Numbers.b;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        currentNum = Numbers.t;
                    }
                    break;
                case sign:
                    if(Character.isDigit(currChar)){
                        currentNum = Numbers.c;
                        getc(br);
                    }else if(currChar == '.'){
                        currentNum = Numbers.b;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        currentNum = Numbers.t;
                    }
                    break;
                case b:
                    if(Character.isDigit(currChar)){
                        currentNum = Numbers.d;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        currentNum = Numbers.t;
                    }
                    break;
                case c:
                    if(Character.isDigit(currChar)){
                        currentNum = Numbers.c;
                        getc(br);
                    }else if(currChar == '.'){
                        currentNum = Numbers.d;
                        getc(br);
                    }else if(currChar == 'e' || currChar == 'E'){
                        currentNum = Numbers.e;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        map.put(lexeme, "integer literals");
                        currentNum = Numbers.t;
                    }
                    break;
                case d:
                    if(Character.isDigit(currChar)){
                        currentNum = Numbers.d;
                        getc(br);
                    }else if(currChar == 'e' || currChar == 'E'){
                        currentNum = Numbers.e;
                        getc(br);
                    }else if(currChar == 'L' || currChar == 'l' || currChar == 'F' || currChar == 'f'){
                        currentNum = Numbers.h;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        map.put(lexeme, "floating point literals");
                        currentNum = Numbers.t;
                    }
                    break;
                case e:
                    if(currChar == '+' || currChar == '-'){
                        currentNum = Numbers.f;
                        getc(br);
                    }else if(Character.isDigit(currChar)){
                        currentNum = Numbers.g;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        currentNum = Numbers.t;
                    }
                    break;
                case f:
                    if(Character.isDigit(currChar)){
                        currentNum = Numbers.g;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        currentNum = Numbers.t;
                    }
                    break;
                case g:
                    if(Character.isDigit(currChar)){
                        currentNum = Numbers.g;
                        getc(br);
                    }else if(currChar == 'L' || currChar == 'l' || currChar == 'F' || currChar == 'f'){
                        currentNum = Numbers.h;
                        getc(br);
                    }else{
                        lex.add(lexeme);
                        map.put(lexeme, "floating point literals");
                        currentNum = Numbers.t;
                    }
                    break;
                case h:
                    lex.add(lexeme);
                    map.put(lexeme, "floating point literals");
                    currentNum = Numbers.t;
                    break;
                case t:

            }
        }

        if(c == -1){
            lex.add(lexeme);
            if(currentNum != Numbers.start){
                if(currentNum == Numbers.c){
                    map.put(lexeme, "integer literals");
                }else if(currentNum == Numbers.d || currentNum == Numbers.g || currentNum == Numbers.h){
                    map.put(lexeme, "floating point literals");
                }
            }
        }
        lexeme = "";
    }

    //Basic method to concatenate characters to lexeme
    public void addChar(){
        this.lexeme += currChar;
    }

    //Method decides what the next lexeme is based on the current character read in
    public void getChar(BufferedReader br) throws IOException{
        if ((c = br.read()) != -1) {
            currChar = (char) c;
            getWhiteSpace(br);
            if (Character.isLetter(currChar) || currChar == '_') {
                tokenIden(br);
                lookup(br);
            }
            else if (Character.isDigit(currChar) || currChar == '+' || currChar =='-') {
                tokenNum(br);
                lookup(br);
            }
            else {
                lookup(br);
            }
        } else {


        }


    }



}

//DFA for numbers
enum Numbers{
    start,
    sign,
    b,
    c,
    d,
    e,
    f,
    g,
    h,
    t,
}

//DFA for identifiers
enum Identifiers{
    start,
    a,
    t,
}