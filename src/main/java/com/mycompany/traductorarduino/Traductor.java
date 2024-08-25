package com.mycompany.traductorarduino;
import java.util.ArrayList;
import java.util.Objects;

public class Traductor {
    /*String que le va a indicar al usuario que clase de error cometio; si no
    cometio ninguno, se quedara vacio y, por lo tanto, no habra ningun error sintactico*/
    private String error;

    /*Lista que contendra todos los tokens utiles como:
    numeros (para indicar cuanto debe avanzar) y palabras clave (direcciones).
    Los punto y coma y parantesis solo sirven para verificar la sintaxis*/
    private final ArrayList<String> tokensUtiles;

    /*Este string contendra un "mensaje" mostrando TODOS los tokens del lenguaje que han
    sido ingresados por el usuario (incluyendo parentesis y punto y coma.). Los presentara
    de una forma estructurada indicando que son cada token*/
    private String tokens;

    // CONSTRUCTOR
    public Traductor() {
        tokensUtiles = new ArrayList<>();
    }

    public void analizarCodigoFuente(String codigoPilot) {
        /*Siempre se vacian estas variables para asegurarse que no si el usuario cometio un error,
        el programa lo olvide, asi como tambien, actualizar los tokens del nuevo codigo*/
        error = "";
        tokens = "";
        tokensUtiles.clear();

        /*Se convierte el codigo fuente en array de caracteres para que el programa tenga un mejor
        control al evaluar si el codigo no tiene errores sintacticos*/
        char[] codigoPilotCharArray = codigoPilot.toCharArray();

        /*Este string almacenara todos los caracteres que no sean espacios en blanco, tabs y
        saltos de linea*/
        String token = "";

        /*Todas las lineas aumentaran cuando el caracter que se este evaluando sea un
        salto de linea*/
        int linea = 1;

        /*try catch por si se llega al punto en el que el programa necesite ver si a un caracter
        le sigue otro pero el codigo ingresado acaba en ese punto y por lo tanto el programa estaria
        tratando de acceder a un elemento que no existe.*/
        try {
            /*For loop para analizar letra por letra el codigo ingresado*/
            for (int i = 0; i < codigoPilotCharArray.length; i++) {

                /*Esta variable contendra todos los caracteres del codigo ingresado segun avance
                el programa.*/
                char l = codigoPilotCharArray[i];

                // EL ANALIZAR SINTACTICO EMPIEZA AQUI

                /*Los comentarios del lenguaje pilot son asi:

                    -Comentario-

                    -
                        Comentario multilinea
                    -

                */

                /*Solo le interesa analizar letras mayusculas, tabs, espacios en blanco, comentarios
                y saltos de linea, lo demas no interesa; de no ingresar nada lo que al programa le interesa,
                saltara un mensaje de error. EL lenguaje es case sensitive*/
                // '\t' es tab. '\n' es salto de linea. ' ' es espacio en blanco.
                if (Character.isUpperCase(l) || l == '\t' || l == ' ' || l == '\n' || l == '-') {

                    /*La implementacion de los do se justifica mas adelante...*/
                    do {
                        /*FASE DE OMITIR TABS, ESPACIOS EN BLANCO Y SALTOS DE LINEA*/
                        /*Aqui basicamente omite los tabs, espacio en blanco y saltos de linea, ademas, tambien
                        verifica que no sean validos tokens asi: N ORTE, N O R T E, NO R TE*/
                        if (l == '\t' || l == ' ' || l == '\n') {

                            if (l == '\n') {
                                linea++;
                            }
                            /*Se adelanta para ver si encuentra una letra mayuscula. Si hay, sale a la siguiente
                            fase*/
                            i++;
                            l = codigoPilotCharArray[i];

                            if (Character.isUpperCase(l)) {
                                /*Se asegura que no sea valido:
                                    N   ORTE,
                                    N ORTE,
                                    N
                                    ORTE
                                 Si lo hay, reemplaza el tab, espacio en blanco o salto de linea con 'x' para
                                 forzarle un error*/
                                if (codigoPilotCharArray[i + 1] != '\t' && codigoPilotCharArray[i + 1] != ' ' && codigoPilotCharArray[i + 1] != '\n') {
                                    /*Caso asi: N;ORTE lo va a pasar a la 3 fase en la que se indican errores. Este if basicamente
                                    es para saber si es la primera letra de la linea*/

                                    /*Todos los breaks son para que deje de recorrer letra por letra el codigo pilot. Ya que si
                                    ocurre un error, se detiene el programa*/
                                    if (token.isBlank()) {
                                        break;
                                    } else if (codigoPilotCharArray[i - 1] != '\t' && codigoPilotCharArray[i - 1] != ' ' && codigoPilotCharArray[i - 1] != '\n') {
                                        /*Si entra aqui indica que no es la primera letra de la linea porque token esta vacio; por lo
                                        tanto, se asegura que no sea valido:
                                            NO   RTE ,
                                            NO RTE,
                                            NO
                                            RTE
                                        de lo contraria lo fuerza a tener error concatenando 'd'*/
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

                        do {
                            /*FASE DE COMENTARIOS*/
                            if (l == '-') {
                                i++;
                                l = codigoPilotCharArray[i];
                                /*Omite caracteres hasta encontrar otro '-' indicando el fin del comentario*/
                                while (l != '-') {
                                    if (l == '\n') {
                                        linea++;
                                    }
                                    /*Se asegura que haya excepcion de ArrayOutofBonds*/
                                    if (i + 1 < codigoPilotCharArray.length) {
                                        i++;
                                        l = codigoPilotCharArray[i];
                                    } else {
                                        /*Si llego al tope del codigo fuente, lo posiciona en el ultimo
                                        caracter*/
                                        i = i - 2;
                                        break;
                                    }
                                }
                                /*Se pasa al caracter despues de cerrar el comentario ej. "-NORTE" se
                                pasaria a 'N'*/
                                i++;
                                l = codigoPilotCharArray[i];
                                /*Se prepara para estas situaciones:
                                    -comentario- -otro comentario-,
                                    -comentario-    -otro comentario-,
                                    -comentario-

                                    -otro comentario-
                                */
                                /*Si es asi, va a ver si encuentra un otro comentario o una letra para pasarla a
                                la siguiente fase, de encontrar otro comentario, repite, por eso el do while*/
                                if (l == ' ' || l == '\t' || l == '\n' && !Character.isUpperCase(codigoPilotCharArray[i + 1])) {
                                    /*Crea a un subindice para 'ver' si hay otro comentario. Si lo hay, omite los tabs,
                                    espacios en blanco y saltos de linea asignandole a i el index donde se encontro el otro
                                    comentario. Se hace un subindice solo para asegurarse de que haya otro comentario*/
                                    int j = i;
                                    j++;
                                    /*Misma logica*/
                                    while (codigoPilotCharArray[j] != '-') {
                                        if (codigoPilotCharArray[j] == '\n') {
                                            linea++;
                                        }
                                        if (j + 1 < codigoPilotCharArray.length) {
                                            j++;
                                            if (codigoPilotCharArray[j] == '-') {
                                                i = j;
                                                l = codigoPilotCharArray[i];
                                                break;
                                            }
                                            if (Character.isUpperCase(codigoPilotCharArray[j])) {
                                                i = j;
                                                l = codigoPilotCharArray[i];
                                                break;

                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                    if (codigoPilotCharArray[j] == '-') {
                                        i = j;
                                        l = codigoPilotCharArray[i];
                                    }
                                }
                            }
                            /*Do while por si encuentra otro comentario*/
                        } while (l == '-');
                        /*Do while por si le siguen espacios en blanco, tabs o saltos de linea
                        para volver a omitirlos*/
                    } while (l == '\t' || l == ' ' || l == '\n');

                    /*ULTIMA FASE: SINTAXIS*/
                    /*Concatena SOLO LETRAS MAYUSCULAS ya que por cada letra en el codigo fuente,
                    chequea si es una direccion valida. Las direcciones son NORTE, ESTE, OESTE; y para
                    ser validas le tiene que seguir un parentesis abierto, para que no sea valido: NORTE3,
                    NORTEFDKLSJF, NORTE;, etc*/
                    token = token + l;
                    char siguiente = codigoPilotCharArray[i + 1];
                    if (esDireccionValida(token, siguiente)) {
                        tokensUtiles.add(token);
                        tokens = tokens + "+------------------------------+\nPALABRA RESERVADA => `" + token + "`\n";
                        // Pasa al parentesis
                        i++;
                        // Se limpia y se le asigna el parentesis
                        token = "" + codigoPilotCharArray[i];
                        tokens = tokens + "PARENTESIS ABIERTO => `" + token + "`\n";
                        // Se va a los pasos
                        i++;
                        token = "";
                        while (Character.isDigit(codigoPilotCharArray[i])) {
                            token = token + codigoPilotCharArray[i];
                            i++;
                        }
                        tokensUtiles.add(token);
                        tokens = tokens + "PASOS => `" + token + "`\n";

                        if (!(Character.toString(codigoPilotCharArray[i]).equals(")")) || token.isEmpty()) {
                            token = token + codigoPilotCharArray[i];
                            error = "'" + token + "'" + " No es un numero o le falta cerrar parentesis... " +
                                    "Error en linea: " + linea;
                            break;
                        }
                        token = "" + codigoPilotCharArray[i];
                        tokens = tokens + "PARENTESIS CERRADO `" + token + "`\n";
                        i++;
                        /*En caso de no llegar a tener ";" ocurre exepcion porque se esta pasando del limite
                        del array*/
                        if (i < codigoPilotCharArray.length) {
                            token = "" + codigoPilotCharArray[i];
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
                                "en linea: " + linea;
                    }
                } else {
                    error = "'" + token + "...'" + " No es un token valido... despues de una direccion valida, van direcciones" +
                            ". Las direcciones deben estar" +
                            " en mayusculas ej. NORTE, ESTE, OESTE... Error en linea: " + linea;
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            /*Se asegura que no se cuente una linea extra*/
            linea--;
        }

        if (!error.isBlank()) {
            tokensUtiles.clear();
            tokens = "";
        }
    }

    private boolean esDireccionValida(String token, char siguiente) {
        if(Objects.equals(token, "NORTE") || Objects.equals(token, "OESTE") || Objects.equals(token, "ESTE")) {
            return Character.toString(siguiente).equals("(");
        }
        return false;
    }

    public String getCodigoArduino() {
        StringBuilder arduino = new StringBuilder();

        /*Constantes*/
        final int TIEMPO_MOVIMIENTO = 500;
        final int VELOCIDAD = 255;
        final int DELAY_GIRO = 1000;

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
                        if (i == 0 || Objects.equals(tokensUtiles.get(i - 2), "NORTE")) {
                            arduino.append(
                                    "   // NORTE\n" +
                                            parar + avanzar +
                                            "   delay(" + pasos + ");\n\n");
                        }
                        // Ajuste desde este
                        else if (Objects.equals(tokensUtiles.get(i - 2), "ESTE")) {
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
                                    "  delay(" + DELAY_GIRO + ");\n" +
                                    parar +
                                    avanzar +
                                    "   delay(" + pasos + ");\n\n"
                            );
                        } // Ajuste desde oeste
                        else if (Objects.equals(tokensUtiles.get(i - 2), "OESTE")) {
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
                                    "  delay(" + DELAY_GIRO + ");\n" +
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
                                        "  delay(" + DELAY_GIRO + ");\n" +
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
                                        "  delay(" + DELAY_GIRO + ");\n" +
                                        parar +
                                        avanzar +
                                        "   delay(" + pasos + ");\n\n");
                        i++;
                        break;
                }
            }
            arduino.append(
                    """
                            // Final
                              motor1.run(RELEASE);
                              motor2.run(RELEASE);
                              motor3.run(RELEASE);
                              motor4.run(RELEASE);\
                            }

                            void loop() {
                             \s
                            }"""
            );
        } else {
            arduino.append(error);
        }
        tokensUtiles.clear();
        return arduino.toString();
    }

    public String getTokens() {
        if (!tokens.isBlank()) {
            return tokens;
        }
        return "Vacio...";
    }
}
