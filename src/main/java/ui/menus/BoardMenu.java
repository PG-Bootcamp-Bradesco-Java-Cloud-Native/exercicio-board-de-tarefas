package ui.menus;

import helpers.Console;
import helpers.Parsers;
import model.Board;
import model.Column;
import model.ColumnType;
import persistance.BoardDao;
import persistance.ColumnDao;

public class BoardMenu extends BaseMenu {

    private Board board;

    private BoardMenu(Board board) {
        this.board = board;
    }

    public static void run(Board board) {
        new BoardMenu(board)._run();
    }

    protected void _run() {
        while (!menuClosed) {
            Console.clear();

            this.board = BoardDao.getInstance().reload(board);

            var menuStringBuilder = new StringBuilder(String.format("Board: %s\n", board.getTitle()));

            consumeMessages(menuStringBuilder);

            addDisplayList(menuStringBuilder, board.getColumns());

            menuStringBuilder.append("\n\t[0] Voltar")
                    .append("\n\t[1] Renomear")
                    .append("\n\t[2] Selecionar Coluna")
                    .append("\n\t[3] Adicionar Coluna")
                    .append("\n\t[4] Reposicionar Coluna")
                    .append("\n\t[5] Remover Coluna")
                    .append("\n> ");
            System.out.print(menuStringBuilder.toString());

            var input = Parsers.tryParseInteger(scan.nextLine());
            if (input == null) {
                messages.push("Opção inválida.");
                continue;
            }
            switch (input) {
                case 0 -> closeMenu();
                case 1 -> renameBoard();
                case 2 -> selectColumn();
                case 3 -> addColumn();
                case 4 -> reorderColumn();
                case 5 -> removeColumn();
                default -> messages.push("Opção inválida.");
            }
        }
    }

    private void renameBoard() {
        Console.clear();
        System.out.println("Renomear Board");

        System.out.print("\n\tRenomear para: ");

        board.setTitle(scan.nextLine());

        BoardDao.getInstance().update(board);

        messages.push("Board renomeado com sucesso.");
    }

    private void selectColumn() {
        Console.clear();

        Column column = promptChoiceFromList(
                board.getColumns(),
                "Selecionar Coluna",
                "Não há colunas registradas.",
                "Coluna não encontrada."
        );

        if (column == null) {
            return;
        }

        ColumnMenu.run(column);
    }

    private void addColumn() {
        Console.clear();

        System.out.println("Adicionar Coluna");
        System.out.print("\n\tTítulo da Coluna: ");

        var newColumn = ColumnDao.getInstance().create(
                scan.nextLine(),
                ColumnType.Pendente,
                (short) (board.getColumns().size() - 2),
                board
        );

        messages.push(String.format("Coluna '%s' criada com sucesso.", newColumn.getTitle()));
    }

    private void removeColumn() {
        Console.clear();

        Column column = promptChoiceFromList(
                board.getColumns(),
                "Remover Coluna",
                "Não há colunas registradas.",
                "Coluna não encontrada."
        );

        if (column == null) {
            return;
        }

        ColumnDao.getInstance().delete(column);

        messages.push(String.format("Coluna '%s' removido com sucesso.", column.getTitle()));
    }

    private void reorderColumn() {
        messages.push("Não implementado...");
    }

}
