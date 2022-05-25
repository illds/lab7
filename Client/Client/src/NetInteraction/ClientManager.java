package NetInteraction;

import Dependency.Command;
import Dependency.Utils;

import java.io.IOException;
import java.net.PortUnreachableException;
import java.util.NoSuchElementException;

public class ClientManager {
    private Ask ask;
    private Answer answer;
    String login;
    String password;

    public ClientManager(Ask ask, Answer answer) {
        this.ask = ask;
        this.answer = answer;
    }

    public void printCollection(Command command) throws IOException, ClassNotFoundException {
        command.getStrings().stream().forEach(x -> System.out.println(x));
    }

    public Command receiveCommand() throws IOException, ClassNotFoundException {
        return ask.askForServer();
    }

    public void send(Command command) throws IOException {
        answer.answerForServer(command);
    }

    public void printString(Command command) throws IOException, ClassNotFoundException {
        System.out.println(command.getUserCommand());
    }

    private Command commandOut;
    private boolean isFirstCommand = false;

    public Command getCommandOut() {
        return commandOut;
    }

    public void setCommandOut(Command commandOut) {
        this.commandOut = commandOut;
        isFirstCommand = true;
    }

    public void setFirstCommand(boolean firstCommand) {
        isFirstCommand = firstCommand;
    }

    public Command interact() throws IOException, ClassNotFoundException {
        String userCommand = "";
        try {
            if (!isFirstCommand) {
                userCommand = Utils.scanner().nextLine();
                if (userCommand.equals("connect")) {
                    userCommand = "";
                }
            } else {
                userCommand = commandOut.getUserCommand();
                if (userCommand.equals("connect")) {
                    userCommand = "";
                }
                isFirstCommand = false;
            }
        } catch (NoSuchElementException e) {
            System.out.println("Вы принудительно завершили работу клиентского приложения.");
            System.exit(0);
        }
        commandOut = validate(userCommand);
        send(commandOut);
        Command commandIn;
        commandIn = receiveCommand();
        if(commandIn.getUserCommand().equals("not reg")){
            login = null;
            password =null;
            commandIn.setUserCommand("Пользователь с таким логином уже создан! Для регистрации введите reg, для авторизации auth:");
        }
        if(commandIn.getUserCommand().equals("not auth")){
            commandIn.setUserCommand("Вы не авторизованы! Для регистрации введите reg, для авторизации auth:");
        }
        return commandIn;
    }

    public Command validate(String userCommand) {
        Command command = new Command("",login,password);
        String[] finalUserCommand = userCommand.trim().split(" +", 3);
        switch (finalUserCommand[0]) {
            case "auth":
            case "reg":
                try {
                    if (finalUserCommand.length !=1)
                        throw new IndexOutOfBoundsException();
                    System.out.println("Введите логин:");
                    login = Utils.scannerForUser();
                    System.out.println("Введите пароль:");
                    password = Utils.encryptThisString(Utils.scannerForUser());
                    command = new Command(finalUserCommand[0],login,password);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Вы ввели команду неправильно.");
                }
                break;
            case "insert":
                try {
                    if (finalUserCommand.length < 2 | finalUserCommand.length > 2)
                        throw new IndexOutOfBoundsException();
                    command = new Command(finalUserCommand[0], Utils.integerConverter(finalUserCommand[1]),login,password);
                    setupPassport(command);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Вы ввели команду неправильно.");
                } catch (NullPointerException e) {
                    System.err.println("Ключ должен быть целым числом.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case "update":
                try {
                    if (finalUserCommand.length < 3 | finalUserCommand.length > 3)
                        throw new IndexOutOfBoundsException();
                    command = new Command(userCommand, Utils.integerConverter(finalUserCommand[1]), finalUserCommand[2],login,password);
                    if (command.getElement().equals("person")) {
                        setupPassport(command);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Вы ввели команду неправильно.");
                } catch (NullPointerException e) {
                    System.err.println("ID должно быть целым числом.");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case "remove_key":
                try {
                    if (finalUserCommand.length != 2) throw new IndexOutOfBoundsException();
                    command = new Command(userCommand,login,password);
                    break;
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Вы ввели команду неправильно.");
                } catch (NullPointerException e) {
                    System.err.println("Ключ должен быть целым числом.");
                }
            case "remove_greater":
                try {
                    if (finalUserCommand.length != 2)
                        throw new IndexOutOfBoundsException();
                    command = new Command(userCommand,login,password);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Вы ввели команду неправильно.");
                } catch (NullPointerException e) {
                    System.err.println("Зарплата должна быть целым числом.");
                }
                break;
            case "replace_if_greater":
                try {
                    if (finalUserCommand.length < 3 | finalUserCommand.length > 3)
                        throw new IndexOutOfBoundsException();
                    command = new Command(userCommand,login,password);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Вы ввели команду неправильно.");
                } catch (NullPointerException e) {
                    System.err.println("Проверьте ключ или зарплату, они должны быть целыми числами.");
                }
                break;
            case "remove_greater_key":
                try {
                    if (finalUserCommand.length < 2 | finalUserCommand.length > 2)
                        throw new IndexOutOfBoundsException();
                    command = new Command(userCommand,login,password);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Вы ввели команду неправильно.");
                } catch (NullPointerException e) {
                    System.err.println("Ключ должен быть целым числом.");
                }
                break;
            case "execute_script":
                try {
                    if (finalUserCommand.length < 2 | finalUserCommand.length > 2)
                        throw new IndexOutOfBoundsException();
                    command = new Command(userCommand,login,password);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Команда введена неверно.");
                }
                break;
            case "exit":
                System.out.println("Вы завершили работу клиентского приложения.");
                System.exit(0);
                break;
            default:
                command = new Command(finalUserCommand[0],login,password);
                break;

        }
        return command;
    }

    public void setupPassport(Command command) throws IOException, ClassNotFoundException {
        boolean isCorrectPassport = false;
        Command passport = new Command("check_passport",login,password);
        while (!isCorrectPassport) {
            System.out.println("Введите паспортные данные:");
            String passportID = Utils.scanner().nextLine();
            passport.setPassportID(passportID);
            send(passport);
            if (receiveCommand().getUserCommand().equals("correct")) {
                isCorrectPassport = true;
                command.setPassportID(passportID);
            } else {
                System.err.println("Повторите ввод.\nДлина строки не должна быть больше 44, Строка не может быть пустой, Значение этого поля должно быть уникальным");
            }
        }
    }
}
