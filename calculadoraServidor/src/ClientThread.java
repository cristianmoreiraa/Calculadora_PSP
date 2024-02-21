import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

/**
 *
 * @author postgres
 */
public class ClientThread extends Thread{

    private Socket newSocket;

    public ClientThread(Socket socket){
        this.newSocket=socket;
    }

    public void run(){
        try{
            InputStream is=newSocket.getInputStream();
            OutputStream os=newSocket.getOutputStream();

            // Leer y escribir mensajes
            String mensajeCliente;
            int operacionCliente,decisionCliente=-1;
            double n1=0,n2,resultado=0;
            String mensajeServidor="";

            do {
                // Leer la operacion escogida por el cliente
                byte[] lengthBuffer = new byte[4]; // Usamos un buffer de 4 bytes para almacenar la longitud del mensaje
                is.read(lengthBuffer);
                int messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                byte[] messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeCliente=new String(messageBuffer);

                //Controlando Error: Boton Cancelar por el cliente
                try{
                    operacionCliente=Integer.parseInt(mensajeCliente);
                }catch(Exception e){
                    break;
                }

                //Leer el primer valor
                if(decisionCliente!=0){
                    lengthBuffer = new byte[4]; // Usamos un buffer de 4 bytes para almacenar la longitud del mensaje
                    is.read(lengthBuffer);
                    messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                    messageBuffer = new byte[messageLength];
                    is.read(messageBuffer);
                    mensajeCliente=new String(messageBuffer);

                    n1=Double.parseDouble(mensajeCliente);
                }else{
                    n1=resultado;
                }

                //Leer el segundo valor
                lengthBuffer = new byte[4]; // Usamos un buffer de 4 bytes para almacenar la longitud del mensaje
                is.read(lengthBuffer);
                messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeCliente=new String(messageBuffer);

                n2=Double.parseDouble(mensajeCliente);

                //COMPROBACION DE DATOS RECIBIDOS:
                System.out.println("Nro. Operacion: "+operacionCliente+", n1: "+n1+", n2:"+n2);

                //Resultado a enviar
                switch (operacionCliente){
                    case 1: //Suma
                        resultado=n1+n2;
                        mensajeServidor="Suma resultante: "+resultado;
                        break;
                    case 2: //Resta
                        resultado=n1-n2;
                        mensajeServidor="Resta resultante: "+resultado;
                        break;
                    case 3: //Multiplicacion
                        resultado=n1*n2;
                        mensajeServidor="Multiplicacion resultante: "+resultado;
                        break;
                    case 4: //Division
                        if(n2==0){
                            mensajeServidor="Division resultante: Infinito";
                        }else{
                            resultado=n1/n2;
                            mensajeServidor="Division resultante: "+resultado;
                        }
                        break;
                }

                os.write(ByteBuffer.allocate(4).putInt(mensajeServidor.getBytes().length).array());
                os.write(mensajeServidor.getBytes());

                //Leemos la decision del usuario:
                lengthBuffer = new byte[4]; // Usamos un buffer de 4 bytes para almacenar la longitud del mensaje
                is.read(lengthBuffer);
                messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeCliente=new String(messageBuffer);

                decisionCliente=Integer.parseInt(mensajeCliente);

                if(decisionCliente==0){
                    //COMPROBACION DE RESULTADO PREVIO A USAR EN LA PROXIMA OPERACION:
                    System.out.println("Seguimos operando con: "+resultado);
                }else{
                    System.out.println("Reiniciamos calculadora...");
                }

            } while (decisionCliente!=2);


            System.out.println("Socket cerrado");

            newSocket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}