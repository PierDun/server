import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import org.json.simple.JSONObject;

/**
 * Класс коллекций сундуков
 * @author Земнухов Владимир
 */
class ChestCollection {
    private Comparator<Chest> chestComparator = new ChestNameComparator().thenComparing(new ChestSumComparator());
    ConcurrentSkipListSet<Chest> types = new ConcurrentSkipListSet<>();

    Scanner input = null;
    private String path = "";

    void setPath(String path) throws FileNotFoundException, NullPointerException {
        this.path = path;
        File file = new File(path);
        file.setExecutable(true);
        file.setReadable(true);
        file.setWritable(true);
        input = new Scanner(file);
    }

    /**
     * Считать элементы с указанного файла
     * @param a - нужный файл
     */
    void readElements(Scanner a){
        int index = 0;
        while(a.hasNextLine()){
            Scanner dataScanner = new Scanner(a.nextLine());
            dataScanner.useDelimiter(";");
            Chest curChest = new Chest();
            while(dataScanner.hasNext()){
                String data = dataScanner.next();
                if (index == 0){
                    curChest.setName(data);
                }
                else if (index == 1) {
                    curChest.setSum(data);
                }
                else if (index == 2) {
                    curChest.state[0] = Float.valueOf(data);
                }
                else if (index == 3) {
                    curChest.state[1] = Float.valueOf(data);
                }
                else if (index == 4) {
                    curChest.state[2] = Float.valueOf(data);
                }
                else if (index == 5) {
                    curChest.setCreate_date(data);
                }
                index++;
            }
            index = 0;
            types.add(curChest);
        }
        input.close();
    }

    /**
     * Вывести элементы коллекции
     */
    void writeElements(){
        System.out.println("----------------------");
        for(Chest type: types){
            type.printInfo();
            System.out.println("----------------------");
        }
    }

    /**
     * сохранить коллекцию в файл
     * @throws IOException - бросает исключение при ошибке ввода/вывода
     */
    void save() throws IOException {
        FileWriter output = new FileWriter(path);
        StringBuilder curStr = new StringBuilder();
        int i = 0;
        for(Chest type: types)
        {
            if(i!=types.size()) {
                curStr.append(type.getName()).append(";").append(type.getSum()).append(";").append(type.state[0]).append(";").append(type.state[1]).append(";").append(type.state[2]).append(";").append(type.getCreate_date()).append("\n");
            } else {
                curStr.append(type.getName()).append(";").append(type.getSum()).append(";").append(type.state[0]).append(";").append(type.state[1]).append(";").append(type.state[2]).append(";").append(type.getCreate_date());
            }
            i++;
        }
        output.write(curStr.toString());
        output.close();
    }

    /**
     * добавить новый элемент в коллекцию, если его значение меньше, чем у наименьшего элемента этой коллекции
     * @param jsonCommand - добавляемый в колекцию элемент
     */
    void add_if_min(JSONObject jsonCommand) {
        System.out.println(jsonCommand.get("name") + " " + jsonCommand.get("sum"));
        Chest curElement = new Chest();
        curElement.setName(jsonCommand.get("name").toString());
        curElement.setSum(String.valueOf(jsonCommand.get("sum")));
        if (types.lower(curElement)== null){
            types.add(curElement);
            System.out.println("Элементы успешно добавлены...");
        }
        else
            System.out.println("Элемент не минимален...");
    }

    /**
     * добавить новый элемент в коллекцию
     * @param jsonCommand - добавляемый в колекцию элемент
     */
    void add_element(JSONObject jsonCommand) {
      try {
            types.add(new Chest(jsonCommand.get("name").toString(), String.valueOf(jsonCommand.get("sum")), String.valueOf(jsonCommand.get("state[0]")), String.valueOf(jsonCommand.get("state[1]")), String.valueOf(jsonCommand.get("state[2]"))));
            System.out.println("Элементы успешно добавлены...");
        }catch (ClassCastException e){
            System.out.println("Неизвестные типы элементов...");
        }catch (NullPointerException e){
            System.out.println("Ошибка...");
        }
    }

    /**
     * удалить из коллекции все элементы, меньшие, чем заданный
     * @param jsonCommand - удаляемый элемент
     */
    void remove_lower(JSONObject jsonCommand) {
        System.out.println(jsonCommand.get("name") + " " + jsonCommand.get("sum"));
        Chest curElement = new Chest(jsonCommand.get("name").toString(), String.valueOf(jsonCommand.get("sum")), String.valueOf(jsonCommand.get("state[0]")), String.valueOf(jsonCommand.get("state[1]")), String.valueOf(jsonCommand.get("state[2]")));

        types.removeIf(curChest -> chestComparator.compare(curChest, curElement) < 0);

        System.out.println("Элементы успешно удалены...");
    }

    /**
     * добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
     * @param jsonCommand - добавляемый в колекцию элемент
     */
    void add_if_max(JSONObject jsonCommand) {
        System.out.println(jsonCommand.get("name") + " " + jsonCommand.get("sum"));
        Chest curElement = new Chest(jsonCommand.get("name").toString(), String.valueOf(jsonCommand.get("sum")), String.valueOf(jsonCommand.get("state[0]")), String.valueOf(jsonCommand.get("state[1]")), String.valueOf(jsonCommand.get("state[2]")));

        if (types.higher(curElement)== null){
            types.add(curElement);
            System.out.println("Элементы успешно добавлены...");
        }
        else
            System.out.println("Элемент не максимален...");
    }

    /**
     * добавить в коллекцию все данные из файла
     * @param path - путь к файлу
     */
    void import_All(String path) {
        Scanner input_file = null;
        {
            try {
                input_file = new Scanner(new File(path));
            } catch (FileNotFoundException | NullPointerException e) {
                System.out.println("Файл не найден.");
            }
        }
        readElements(input_file);
        try {
            save();
        } catch (IOException e) {
            System.out.println("Файл не найден.");
        }
        input.close();
    }

    ArrayList<Chest> returnObjects(){
        return new ArrayList<>(types);
    }
}