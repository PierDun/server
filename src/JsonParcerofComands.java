import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.IOException;

/**
 * Класс парсинга строки в JSON-объект и выполнения команд с ним
 * @author Земнухов Владимир
 */
class JsonParcerofComands {

    /**
     * основной метод по получению JSON-объекта и выполнения команд с ним
     * @param cmd - введенная команда
     * @param jsonobj - введенный JSON-объект
     * @param tree - используемая для хранения коллекция
     * @throws IOException - метод бросает исключение в случае ошибки ввода-вывода
     */
    void command(String cmd, String jsonobj, ChestCollection tree) throws IOException {
        JSONParser parser = new JSONParser();
        JSONObject jsonCommand = null;

        try {
            jsonCommand = (JSONObject) parser.parse(String.valueOf(jsonobj));
        }catch (ParseException e){
            System.out.println("Неверный JSON формат...");
        }

        switch(cmd){
            case "remove_lower":
                tree.remove_lower(jsonCommand);
                break;
            case "add_if_max":
                tree.add_if_max(jsonCommand);
                break;
            case "add_if_min":
                tree.add_if_min(jsonCommand);
                break;
            case "add":
                tree.add_element(jsonCommand);
                break;
            case "clear":
                tree.types.clear();
                System.out.println("Коллекция очищена.");
                break;
            case "exit":
                System.out.println("Клиент отключился");
                break;
            case "save":
                try {
                    tree.save();
                    System.out.println("Данные сохранены");
                } catch (IOException e2) {
                    System.out.println("Невозможно сохранить файл.");
                }
                break;
            case "import":
                tree.import_All(jsonobj);
                break;
            default:
                System.out.println("Неизвестная команда...");
                break;
        }

        tree.save();
    }
}