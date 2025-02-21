package ui.menus;

import helpers.Console;
import helpers.Parsers;
import model.Board;
import persistance.BoardDao;

public class MainMenu extends BaseMenu {

    private MainMenu() {
    }

    public static void run() {
        new MainMenu()._run();
    }

    protected void _run() {
        while (!menuClosed) {
            Console.clear();

            var menuStringBuilder = new StringBuilder("Boards de Tarefas\n");

            consumeMessages(menuStringBuilder);

            addDisplayList(menuStringBuilder, BoardDao.getInstance().findAll());

            menuStringBuilder.append("\n\t[0] Sair")
                    .append("\n\t[1] Selecionar Board")
                    .append("\n\t[2] Criar Board")
                    .append("\n\t[3] Remover Board")
                    .append("\n> ");
            System.out.print(menuStringBuilder.toString());

            var input = Parsers.tryParseInteger(scan.nextLine());
            if (input == null) {
                messages.push("Opção inválida.");
                continue;
            }
            switch (input) {
                case 0 -> closeMenu();
                case 1 -> selectBoard();
                case 2 -> createBoard();
                case 3 -> removeBoard();
                default -> messages.push("Opção inválida.");
            }
        }
    }

    private void selectBoard() {
        Console.clear();

        Board board = promptChoiceFromList(
                BoardDao.getInstance().findAll(),
                "Selecionar Board",
                "Não há Boards registrados.",
                "Board não encontrado."
        );

        if (board == null) {
            return;
        }

        BoardMenu.run(board);
    }

    private void createBoard() {
        Console.clear();
        System.out.println("Criar Board");
        System.out.print("\n\tTítulo do Board: ");
        var newBoard = BoardDao.getInstance().create(scan.nextLine());
        messages.push(String.format("Board '%s' criado com sucesso.", newBoard.getTitle()));
    }

    private void removeBoard() {
        Console.clear();

        Board board = promptChoiceFromList(
                BoardDao.getInstance().findAll(),
                "Remover Board",
                "Não há Boards Registrados.",
                "Board não encontrado."
        );

        if (board == null) {
            return;
        }

        BoardDao.getInstance().delete(board);

        messages.push(String.format("Board '%s' removido com sucesso.", board.getTitle()));
    }
}
