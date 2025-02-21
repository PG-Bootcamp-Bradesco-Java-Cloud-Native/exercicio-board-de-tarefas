package ui.menus;

import helpers.Parsers;
import model.BaseModel;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Scanner;

public abstract class BaseMenu {
    protected static final Scanner scan = new Scanner(System.in);
    protected static final ArrayDeque<String> messages = new ArrayDeque<>();
    protected Boolean menuClosed = false;

    protected static void consumeMessages(StringBuilder stringBuilder) {
        if (!messages.isEmpty()) {
            while (!messages.isEmpty()) {
                stringBuilder.append("\n\t- ").append(messages.pop());
            }
            stringBuilder.append("\n");
        }
    }

    protected void closeMenu() {
        menuClosed = true;
    }

    protected <T extends BaseModel> T promptChoiceFromList(List<T> list, String promptTitle, String messageOnListEmpty, String messageOnNotFound) {
        if (list.isEmpty()) {
            messages.push(messageOnListEmpty);
            return null;
        }

        var promptStringBUilder = new StringBuilder(promptTitle).append("\n");
        addDisplayOptions(promptStringBUilder, list);
        promptStringBUilder.append("> ");

        System.out.print(promptStringBUilder);

        var input = Parsers.tryParseInteger(scan.nextLine());

        if (input == null || input < 0 || input >= list.size()) {
            messages.push(messageOnNotFound);
            return null;
        }

        return list.get(input);
    }

    protected <T extends BaseModel> void addDisplayList(StringBuilder stringBuilder, List<T> list) {
        if (list.isEmpty()) {
            return;
        }
        for (T object : list) {
            stringBuilder.append(String.format("\n\t- %s", object.getTitle()));
        }
        stringBuilder.append("\n");
    }

    protected <T extends BaseModel> void addDisplayOptions(StringBuilder stringBuilder, List<T> list) {
        if (list.isEmpty()) {
            return;
        }
        int i = 0;
        for (T object : list) {
            stringBuilder.append(String.format("\n\t[%d] %s", i++, object.getTitle()));
        }
        stringBuilder.append("\n");
    }
}