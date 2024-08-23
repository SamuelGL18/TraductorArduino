package com.mycompany.traductorarduino;
import java.util.ArrayList;
import java.util.Objects;

public class Traductor {
    private String error;
    public ArrayList<String> tokensUtiles;
    // Lista de todos los tokens incluyendo parentesis
    public String tokens;

    public Traductor() {
        tokensUtiles = new ArrayList<>();
        tokens = "";
        error = "";
    }

    public void getTokensValidos(String codigo) {
        /*Otra vez vacio el error para asegurarse que si el usuario ha cometido un error y lo
        arreglo, la funcion limpie tambien el error anterior, lo mismo con token*/
        error = "";
        tokens = "";
        tokensUtiles.clear();
        char[] letraCodigo = codigo.toCharArray();
        String token = "";
        int linea = 1;

        /*Obteniendo solo los tokens validos,
        l de letra*/
        /*try catch por si el string no es tan grande al entrar en la funcion
        que checkea si un token es valido, ya que ve si le sigue un parentesis*/
        try {
            for (int i = 0; i < letraCodigo.length; i++) {
                char l = letraCodigo[i];
                // Es case sensitive "normal" != "NORMAL", no importan tabs o espacios
                if (Character.isUpperCase(l) || l == '\t' || l == ' ' || l == '\n' || l == '-') {
                    /*La funcion esTokenValido chequea si encuentra una palabra reservada
                    * y se asegura que le siga un parentesis*/
                    while(l == '\t' || l == ' ' || l == '\n') {
                        if(l == '\n') {
                        linea++;
                        }
                        i++;
                        l = letraCodigo[i];
                        if(Character.isUpperCase(l)) {
                            if(letraCodigo[i + 1] != '\t' && letraCodigo[i + 1] != ' ' && letraCodigo[i + 1] != '\n') {
                                if (token.isEmpty()) {
                                    break;
                                } else if(!token.isEmpty() && letraCodigo[i - 1] != '\t' && letraCodigo[i - 1] != ' ' && letraCodigo[i - 1] != '\n') {
                                    break;
                                } else {
                                    l = 'd';
                                    break;
                                }
                            } else {
                                l = 'x';
                                break;
                            }
                        } 
                    }
                    
                    do{
                    if(l == '-') {
                        i++;
                        l = letraCodigo[i];
                        while(l != '-') {
                            if(l == '\n') {
                                linea++;
                            }
                            if (i + 1 < letraCodigo.length) {
                                i++;
                                l = letraCodigo[i];
                            } else {
                                i = i - 2;
                                break;
                            }
                        }
                        i++;
                        l = letraCodigo[i];
                        // Va a ver si encuentra un comentario...
                        if (l == ' ' || l == '\t' || l == '\n' && !Character.isUpperCase(letraCodigo[i + 1])) {
                            int j = i;
                            j++;
                            while(letraCodigo[j] != '-') {
                                if(letraCodigo[j] == '\n') {
                                    linea++;
                                }
                                if (j + 1 < letraCodigo.length) {
                                    j++;
                                    if (letraCodigo[j] == '-') {
                                        i = j;
                                        l = letraCodigo[i];
                                        break;
                                    }
                                    if (Character.isUpperCase(letraCodigo[j])) {
                                        i = j; l = letraCodigo[i];
                                        break;
                                        
                                    }
                                } else {
                                    break;
                                }
                            } 
                            if (letraCodigo[j] == '-') {
                                i = j;
                                l = letraCodigo[i];
                            }
                        }
                    }} while(l == '-');
                    
                    while(l == '\t' || l == ' ' || l == '\n') {
                        if(l == '\n') {
                        linea++;
                        }
                        i++;
                        l = letraCodigo[i];
                        if(Character.isUpperCase(l)) {
                            if(letraCodigo[i + 1] != '\t' && letraCodigo[i + 1] != ' ' && letraCodigo[i + 1] != '\n') {
                                if (token.isEmpty()) {
                                    break;
                                } else if(!token.isEmpty() && letraCodigo[i - 1] != '\t' && letraCodigo[i - 1] != ' ' && letraCodigo[i - 1] != '\n') {
                                    break;
                                } else {
                                    l = 'd';
                                    break;
                                }
                            } else {
                                l = 'x';
                                break;
                            }
                        } 
                    }
                    token = token + l;
                    char siguiente = letraCodigo[i + 1];
                    if (esTokenValido(token, siguiente)) {
                        tokensUtiles.add(token);
                        tokens = tokens + "+------------------------------+\nPALABRA RESERVADA => `" + token + "`\n";
                        // Pasa al parentesis
                        i++;
                        // Se limpia y se le asigna el parentesis
                        token = "" + letraCodigo[i];
                        tokens = tokens + "PARENTESIS ABIERTO => `" + token + "`\n";
                        // Se va a los pasos
                        i++;
                        token = "";
                        while (Character.isDigit(letraCodigo[i])) {
                            token = token + letraCodigo[i];
                            i++;
                        }
                        tokensUtiles.add(token);
                        tokens = tokens + "PASOS => `" + token + "`\n";

                        if (!(Character.toString(letraCodigo[i]).equals(")")) || token.isEmpty()) {
                            token = token + letraCodigo[i];
                            error = "'" + token +"'" + " No es un numero o le falta cerrar parentesis... " +
                                    "Error en linea: " + linea;
                            break;
                        }
                        token = "" + letraCodigo[i];
                        tokens = tokens + "PARENTESIS CERRADO `" + token + "`\n";
                        i++;
                        /*En caso de no llegar a tener ";" ocurre exepcion porque se esta pasando del limite
                        del array*/
                        if (i < letraCodigo.length) {
                            token = "" + letraCodigo[i];
                        }
                        if (!token.equals(";")) {
                            error = "';' Es esperado al cerrar parentesis en la linea: " + (linea);
                            break;
                        }
                        tokens = tokens + "PUNTO Y COMA => `" + token + "`\n+------------------------------+\n\n";
                        token = "";
                        error = "";
                        linea++;
                        i++;
                    } else {
                        error = "'" + token + "...'" + " no es una direccion valida o necesita parentesis..." +
                                "\n Direcciones permitidas: NORTE, ESTE, OESTE. Error " +
                                "en linea: " + linea ;
                    }
                } else {
                    error = "'" + token + "...'" + " Invalido... antes del  parentesis, van direcciones" +
                            ". Las direcciones deben estar" +
                            " en mayusculas ej. NORTE, ESTE, OESTE... Error en linea: " + linea;
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            
        }
        
        if (!error.isEmpty()) {
            tokensUtiles.clear();
            tokens = "";
        }
    }

    private boolean esTokenValido(String token, char siguiente) {
        if(Objects.equals(token, "NORTE") || Objects.equals(token, "OESTE") || Objects.equals(token, "ESTE")) {
            if(Character.toString(siguiente).equals("(")) {
                return true;
            }
        }
        return false;
    }

    public String getArduino() {
        StringBuilder arduino = new StringBuilder();
        int TIEMPO_MOVIMIENTO = 500;
        int VELOCIDAD = 255;
        
        if (error.isEmpty() && !tokensUtiles.isEmpty()) {
            String parar =
                    """
                              motor1.run(RELEASE);
                              motor2.run(RELEASE);
                              motor3.run(RELEASE);
                              motor4.run(RELEASE);
                              delay(250);
                            """;
            String avanzar =
                    "  motor1.setSpeed(" + VELOCIDAD + ");\n" +
                    "  motor1.run(FORWARD);\n" +
                    "  motor2.setSpeed(" + VELOCIDAD + ");\n" +
                    "  motor2.run(FORWARD);\n" +
                    "  motor3.setSpeed(" + VELOCIDAD + ");\n" +
                    "  motor3.run(FORWARD);\n" +
                    "  motor4.setSpeed(" + VELOCIDAD + ");\n" +
                    "  motor4.run(FORWARD);\n";


            arduino.append(
                    """
                            #include <AFMotor.h>

                            AF_DCMotor motor1(1, MOTOR12_1KHZ);
                            AF_DCMotor motor2(2, MOTOR12_1KHZ);
                            AF_DCMotor motor3(3, MOTOR34_1KHZ);
                            AF_DCMotor motor4(4, MOTOR34_1KHZ);


                            void setup() {
                            """
            );

            for (int i = 0; i < tokensUtiles.size(); i++) {
                String token = tokensUtiles.get(i);
                int pasos;
                switch (token) {
                    case "NORTE":
                        pasos = Integer.parseInt(tokensUtiles.get(i + 1)) * TIEMPO_MOVIMIENTO;
                        //No necesita ajuste
                        if(i == 0 || Objects.equals(tokensUtiles.get(i - 2), "NORTE")) {
                        arduino.append(
                                "   // NORTE\n" +
                                        parar + avanzar +
                                        "   delay(" + pasos + ");\n\n");
                        }
                        // Ajuste desde este
                        else if(Objects.equals(tokensUtiles.get(i - 2), "ESTE")) {
                            arduino.append(" //NORTE  (de ESTE apunta a NORTE)\n" +
                                            " // ESTE => OESTE (ahora gira en direccion contraria para apuntar al norte)\n" +
                                            parar +
                                            "  motor1.setSpeed(" + VELOCIDAD + ");\n" +
                                            "  motor1.run(FORWARD);\n" +
                                            "  motor2.setSpeed(" + VELOCIDAD + ");\n" +
                                            "  motor2.run(BACKWARD);\n" +
                                            "  motor3.setSpeed(" + VELOCIDAD + ");\n" +
                                            "  motor3.run(BACKWARD);\n" +
                                            "  motor4.setSpeed(" + VELOCIDAD + ");\n" +
                                            "  motor4.run(FORWARD);\n" +
                                            "  delay(1000);\n" +
                                            parar +
                                            avanzar +
                                            "   delay(" + pasos + ");\n\n"
                                    );
                        } // Ajuste desde oeste
                        else if(Objects.equals(tokensUtiles.get(i - 2), "OESTE")) {
                            arduino.append(" //NORTE  (de OESTE apunta a NORTE)\n" +
                                    " // OESTE => ESTE (ahora gira en direccion contraria para apuntar al norte)\n" +
                                    parar +
                                    "  motor1.setSpeed(" + VELOCIDAD + ");\n" +
                                    "  motor1.run(BACKWARD);\n" +
                                    "  motor2.setSpeed(" + VELOCIDAD + ");\n" +
                                    "  motor2.run(FORWARD);\n" +
                                    "  motor3.setSpeed(" + VELOCIDAD + ");\n" +
                                    "  motor3.run(FORWARD);\n" +
                                    "  motor4.setSpeed(" + VELOCIDAD + ");\n" +
                                    "  motor4.run(BACKWARD);\n" +
                                    "  delay(1000);\n" +
                                    parar +
                                    avanzar +
                                    "   delay(" + pasos + ");\n\n"
                            );
                        }
                        i++;
                        break;
                    case "ESTE":
                        pasos = Integer.parseInt(tokensUtiles.get(i + 1)) * TIEMPO_MOVIMIENTO;
                        arduino.append(
                                " // ESTE\n" +
                                        parar +
                                        "  motor1.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor1.run(BACKWARD);\n" +
                                        "  motor2.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor2.run(FORWARD);\n" +
                                        "  motor3.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor3.run(FORWARD);\n" +
                                        "  motor4.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor4.run(BACKWARD);\n" +
                                        "  delay(1000);\n" +
                                        parar +
                                        avanzar +
                                        "   delay(" + pasos + ");\n\n");
                        i++;
                        break;
                    case "OESTE":
                        pasos = Integer.parseInt(tokensUtiles.get(i + 1)) * TIEMPO_MOVIMIENTO;
                        arduino.append(
                                " // OESTE\n" +
                                        parar +
                                        "  motor1.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor1.run(FORWARD);\n" +
                                        "  motor2.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor2.run(BACKWARD);\n" +
                                        "  motor3.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor3.run(BACKWARD);\n" +
                                        "  motor4.setSpeed(" + VELOCIDAD + ");\n" +
                                        "  motor4.run(FORWARD);\n" +
                                        "  delay(1000);\n" +
                                        parar +
                                        avanzar +
                                        "   delay(" + pasos + ");\n\n");
                        i++;
                        break;
                }
            }
            arduino.append(
                    "// Final\n" +
                            "  motor1.run(RELEASE);\n" +
                            "  motor2.run(RELEASE);\n" +
                            "  motor3.run(RELEASE);\n" +
                            "  motor4.run(RELEASE);" +
                            "}\n" +
                            "\n" +
                            "void loop() {\n" +
                            "  \n" +
                            "}"
            );
        } else {
            arduino.append(error);
        }
        tokensUtiles.clear();
        return arduino.toString();
    }

    public String getTokens() {
        if(!tokens.isEmpty()) {
            return tokens;
        }
        return "Vacio...";
    }
}
