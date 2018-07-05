/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import static java.util.regex.Pattern.matches;

/**
 *
 * @author C
 */
public class Client {
    
    // примеры:
    // http://cs.lmu.edu/~ray/notes/javanetexamples/
    
    
    private BufferedReader in;  //входной поток
    private PrintWriter out;    //выходной поток

    //значения по умолчанию
    private String SERVER = "127.0.0.1";
    private int PORT = 9001;
    //для замены значения по умолчанию
    String IP_PATTERN = "(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)";
    
    private JFrame frame = new JFrame("Клиент (не подключен)");
    private JTextField dataField = new JTextField(40);      //ввод
    private JTextArea messageArea = new JTextArea(8, 60);   //вывод
    
    
public static void main(String[] args) throws Exception {
        Client client = new Client();            //новый экземпляр клиента
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    //Метод setDefaultCloseOperation определяет действие, которое необходимо выполнить при "выходе из программы".
        client.frame.pack();        //метод pack() устанавливает такой минимальный размер контейнера, который достаточен для отображения всех компонентов.
        client.frame.setVisible(true);      //отображение
        client.connectToServer();           //коннект
    }


//Клиент с интерфейсом, который создается в начале запуска
    public Client() {

        messageArea.setEditable(false);         //поле вывода не редактируемое
        frame.getContentPane().add(dataField, "North");     //вверху поле воода
        frame.getContentPane().add(new JScrollPane(messageArea), "Center"); //по центру в поле прокрутки поле вывода

        // Слушатель на поле воода
        dataField.addActionListener(
         
                new ActionListener() {
            //вызываем, если событие настало
            public void actionPerformed(ActionEvent e) {
                //отправляем в вывод
                out.println(dataField.getText());
                
                //объявляем переменную для ответа
                String response;
                   
                try {
                    //получаем ответ
                    response = in.readLine();
                    //если ответа нет или он пустой - закрываемся
                    if (response == null || response.equals("")) {
                          System.exit(0);
                      }
                } catch (IOException ex) {
                    //обрабатываем исключение
                       response = "Error: " + ex;
                }
                
                //выводим переменную ответа
                messageArea.append(response + "\n");
                dataField.selectAll();
            }
        });
    }
//END

//Установка соединения
    public void connectToServer() throws IOException {
        
        String serverAddress = "Тут будет адрес сервера";
        
        //просим ввести корректный адрес и порт
        while(serverAddress != null || !serverAddress.equals("") || !matches(IP_PATTERN,serverAddress))
        {
        // Получение IP:порта
        serverAddress = JOptionPane.showInputDialog(
            frame,
            "Введите IP и порт для подключения, или оставьте поле пустым (127.0.0.1:9001):",
            "Подключение...",
            JOptionPane.QUESTION_MESSAGE).trim();
        if (serverAddress.equals("") || matches(IP_PATTERN,serverAddress)) break;
        }
        
        if (!serverAddress.equals(""))
        {
          //берем новый адрес сервера
          SERVER = serverAddress.replaceAll(IP_PATTERN, "$1");
          //берем новый порт сервера
          PORT = Integer.parseInt(serverAddress.replaceAll(IP_PATTERN, "$2"));
        }
        // Устанавливаем соединение
        Socket socket = new Socket(SERVER, PORT);
        
        //входной поток это...
        //https://docs.oracle.com/javase/7/docs/api/java/io/InputStreamReader.html
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        //выходной поток это...
        //https://docs.oracle.com/javase/7/docs/api/java/io/PrintWriter.html
        out = new PrintWriter(socket.getOutputStream(), true);

        //устанавливаем заголовок
        //https://docs.oracle.com/javase/7/docs/api/java/awt/Frame.html#setTitle(java.lang.String)
        frame.setTitle("Клиент #"+ in.readLine() + " ("+ SERVER +":"+PORT+")");
        
        // Получаем сообщения сервера (2 приветственных)
        for (int i = 0; i < 3; i++) {
            messageArea.append(in.readLine() + "\n");
        }
        
    }
    //END
}
