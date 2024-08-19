package com.mycompany.traductorarduino;

import java.util.ArrayList;
import java.util.Objects;


public class Traductor {
    private String error;
    public ArrayList<String> tokensUtiles;
    // Lista de todos los tokens incluyendo parentesis
    public ArrayList<String> tokens;

    public Traductor() {
        tokensUtiles = new ArrayList<>();
        tokens = new ArrayList<>();
        error = "";
    }

    public void getTokensValidos(String codigo) {
        /*Otra vez vacio el error para asegurarse que si el usuario ha cometido un error y lo
        arreglo, la funcion limpie tambien el error anterior, lo mismo con token*/
        error = "";
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
                // Es case sensitive "normal" != "NORMAL"
                if (Character.isUpperCase(l)) {
                    /*La funcion esTokenValido chequea si encuentra una palabra reservada
                    * y se asegura que le siga un parentesis*/
                    token = token + l;
                    char siguiente = letraCodigo[i + 1];
                    if (esTokenValido(token, siguiente)) {
                        tokensUtiles.add(token);
                        tokens.add(token);
                        // Pasa al parentesis
                        i++;
                        // Se limpia y se le asigna el parentesis
                        token = "" + letraCodigo[i];
                        tokens.add(token);
                        // Se va a los pasos
                        i++;
                        token = "";
                        while (Character.isDigit(letraCodigo[i])) {
                            token = token + letraCodigo[i];
                            i++;
                        }
                        tokensUtiles.add(token);
                        tokens.add(token);

                        if (!(Character.toString(letraCodigo[i]).equals(")")) || token.isEmpty()) {
                            token = token + letraCodigo[i];
                            error = "'" + token +"'" + " No es un numero o le falta cerrar parentesis... " +
                                    "Error en linea: " + linea;
                            break;
                        }
                        token = "" + letraCodigo[i];
                        tokens.add(token);
                        i++;
                        token = "" + letraCodigo[i];
                        if (!token.equals(";")) {
                            error = "';' Es esperado al cerrar parentesis en la linea: " + (linea);
                            break;
                        }
                        tokens.add(token);
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
            tokensUtiles.clear();
        }
        if (!error.isEmpty()) {
            tokensUtiles.clear();
            tokens.clear();
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
            System.out.println("Tokens: " + tokens);
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
                        arduino.append(
                                "   // NORTE\n" +
                                        parar + avanzar +
                                        "   delay(" + pasos + ");\n\n");
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
        tokens.clear();
        return arduino.toString();
    }
}
