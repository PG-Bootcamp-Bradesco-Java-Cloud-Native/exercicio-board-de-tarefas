package ui.menus;

import helpers.Console;
import helpers.Parsers;
import model.Card;
import model.Column;
import persistance.CardDao;
import persistance.ColumnDao;

public class ColumnMenu extends BaseMenu {

    private Column column;

    private ColumnMenu(Column column) {
        this.column = column;
    }

    public static void run(Column column) {
        new ColumnMenu(column)._run();
    }

    protected void _run() {
        while (!menuClosed) {
            Console.clear();

            this.column = ColumnDao.getInstance().reload(column);

            var menuStringBuilder = new StringBuilder(String.format("Coluna: %s\n", column.getTitle()));

            consumeMessages(menuStringBuilder);

            addDisplayList(menuStringBuilder, column.getCards());

            menuStringBuilder.append("\n\t[0] Voltar")
                    .append("\n\t[1] Renomear")
                    .append("\n\t[2] Selecionar Card")
                    .append("\n\t[3] Adicionar Card")
                    .append("\n\t[4] Mover Card")
                    .append("\n\t[5] Remover Card")
                    .append("\n> ");
            System.out.print(menuStringBuilder.toString());

            var input = Parsers.tryParseInteger(scan.nextLine());
            if (input == null) {
                messages.push("Opção inválida.");
                continue;
            }
            switch (input) {
                case 0 -> closeMenu();
                case 1 -> renameColumn();
                case 2 -> selectCard();
                case 3 -> addCard();
                case 4 -> repositionCard();
                case 5 -> removeCard();
                default -> messages.push("Opção inválida.");
            }
        }
    }

    private void renameColumn() {
        Console.clear();

        System.out.println("Renomear Coluna");

        System.out.print("\n\tRenomear para: ");

        column.setTitle(scan.nextLine());

        ColumnDao.getInstance().update(column);

        messages.push("Coluna renomeada com sucesso.");
    }

    private void selectCard() {
        Console.clear();

        Card card = promptChoiceFromList(
                column.getCards(),
                "Selecionar Card",
                "Não há Cards registrados.",
                "Card não encontrado."
        );

        if (card == null) {
            return;
        }

        CardMenu.run(card);
    }

    private void addCard() {
        Console.clear();

        System.out.println("Adicionar Card");
        System.out.print("\n\tTítulo do Card: ");
        var title = scan.nextLine();

        System.out.print("\n\tDescrição do Card: ");
        var description = scan.nextLine();

        var newCard = CardDao.getInstance().create(
                title,
                description,
                column
        );

        messages.push(String.format("Card '%s' adicionado com sucesso.", newCard.getTitle()));
    }

    private void repositionCard() {
        Console.clear();

        System.out.println("Mover Card\n");

        Card card = promptChoiceFromList(
                column.getCards(),
                "\tCard",
                "Não há colunas registradas.",
                "Coluna não encontrada."
        );

        if (card == null) return;
        if (card.getIsBlocked()) {
            messages.add(String.format("O card '%s' está bloqueado e não pode ser movido.", card.getTitle()));
            return;
        }

        Column col = promptChoiceFromList(
                column.getBoard().getColumns(),
                "\tColuna",
                "Não há colunas registradas.",
                "Coluna não encontrada."
        );

        if (col == null) return;

        card.setColumn(col);

        CardDao.getInstance().update(card);

        messages.push(String.format("Card '%s' movido com sucesso para a coluna '%s'.", card.getTitle(), col.getTitle()));
    }

    private void removeCard() {
        Console.clear();

        Card card = promptChoiceFromList(
                column.getCards(),
                "Remover Card",
                "Não há cards registrados.",
                "Card não encontrado."
        );

        if (card == null) {
            return;
        }

        CardDao.getInstance().delete(card);

        messages.push(String.format("Card '%s' removido com sucesso.", card.getTitle()));
    }
}
