import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import javax.swing.JOptionPane;

/**
 *
 * @author Popi
 */
public class calculadoraCliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here

        try{
            System.out.println("Creando socket cliente");
            Socket clientSocket=new Socket();
            System.out.println("Estableciendo la conexion");

            InetSocketAddress addr=new InetSocketAddress("192.168.0.2",6666);
            clientSocket.connect(addr);

            InputStream is=clientSocket.getInputStream();
            OutputStream os=clientSocket.getOutputStream();

            // Leer y escribir mensajes
            String mensajeServidor="";
            String mensajeCliente="";
            int mensajeClienteInt=0,comprobacion=-1;

            do {

                // Responder al servidor con la operación a realizar

                do{

                    try{ //Controlando Error: Boton Cancelar
                        mensajeCliente=JOptionPane.showInputDialog(null,"¡BIENVENIDO A CALCUSERVER! :D"
                                + "\n\n Introduce el numero de la operacion a realizar:"
                                + "\n1. Suma"
                                + "\n2.Resta"
                                + "\n3.Multiplicacion"
                                + "\n4.Division");
                        mensajeClienteInt=Integer.parseInt(mensajeCliente);

                        if (mensajeClienteInt<1 | mensajeClienteInt>4){
                            JOptionPane.showMessageDialog(null, "ERROR: Operacion no encontrada");
                        }
                    }catch(Exception e){
                        JOptionPane.showMessageDialog(null, "Hasta el proximo calculo! :D");
                        break;
                    }

                }while(mensajeClienteInt<1 | mensajeClienteInt>4);

                //Controlando Error: NullPointer
                try{
                    os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                    os.write(mensajeCliente.getBytes());
                }catch(Exception e){
                    break;
                }

                boolean validInput=false;




                // Responder al servidor con el primer valor

                if (comprobacion!=0){
                    do{
                        mensajeCliente=JOptionPane.showInputDialog(null,"Introduce el primer valor:");

                        //Controlando Error: Caracteres no numéricos
                        try{
                            Double.parseDouble(mensajeCliente);
                            validInput=true;
                        }catch(Exception e){
                            JOptionPane.showMessageDialog(null, "ERROR: Caracter no valido, asegurate de que sea un numero");
                        }

                    }while(!validInput);

                    os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                    os.write(mensajeCliente.getBytes());
                }

                validInput=false;




                // Responder al servidor con el segundo valor

                do{
                    mensajeCliente=JOptionPane.showInputDialog(null,"Introduce el segundo valor:");

                    //Controlando Error: Caracteres no numéricos
                    try{
                        Double.parseDouble(mensajeCliente);
                        validInput=true;
                    }catch(NumberFormatException e){
                        JOptionPane.showMessageDialog(null, "ERROR: Caracter no valido, asegurate de que sea un numero");
                    }
                }while(!validInput);

                os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                os.write(mensajeCliente.getBytes());





                // Leer mensaje del servidor
                byte[] lengthBuffer = new byte[4]; // Usamos un buffer de 4 bytes para almacenar la longitud del mensaje
                is.read(lengthBuffer);
                int messageLength = ByteBuffer.wrap(lengthBuffer).getInt();

                byte[] messageBuffer = new byte[messageLength];
                is.read(messageBuffer);
                mensajeServidor=new String(messageBuffer);

                JOptionPane.showMessageDialog(null,mensajeServidor);

                //Uso del calculo previo del servidor
                comprobacion=JOptionPane.showConfirmDialog(null, "Deseas seguir operando con este resultado?");
                mensajeCliente=String.valueOf(comprobacion);

                os.write(ByteBuffer.allocate(4).putInt(mensajeCliente.getBytes().length).array());
                os.write(mensajeCliente.getBytes());

                if (comprobacion==2){
                    JOptionPane.showMessageDialog(null, "Hasta el proximo calculo! :D");
                }

            } while (comprobacion!=2);

            System.out.println("Cerrando el socket cliente...");

            clientSocket.close();

            System.out.println("Terminado");


        }catch (IOException e){
            e.printStackTrace();
        }
    }

}