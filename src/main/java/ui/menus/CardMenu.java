package ui.menus;

import helpers.Console;
import helpers.Parsers;
import model.Card;
import persistance.CardDao;

public class CardMenu extends BaseMenu {

    private Card card;

    private CardMenu(Card card) {
        this.card = card;
    }

    public static void run(Card card) {
        new CardMenu(card)._run();
    }

    protected void _run() {
        while (!menuClosed) {
            Console.clear();

            this.card = CardDao.reload(card);

            var menuStringBuilder = new StringBuilder("Card: ").append(card.getTitle())
                    .append("\nBloqueado: ").append(card.getIsBlocked())
                    .append("\nDescrição: ").append(card.getDescription())
                    .append("\n");

            consumeMessages(menuStringBuilder);

            menuStringBuilder.append("\n\t[0] Voltar")
                    .append("\n\t[1] Renomear")
                    .append("\n\t[2] Alterar Descrição")
                    .append(card.getIsBlocked() ? "\n\t[3] Desbloquear" : "\n\t[3] Bloquear")
                    .append("\n\t[4] Mostrar logs de bloqueio / desbloqueio")
                    .append("\n> ");
            System.out.print(menuStringBuilder.toString());

            var input = Parsers.tryParseInteger(scan.nextLine());
            if (input == null) {
                messages.push("Opção inválida.");
                continue;
            }
            switch (input) {
                case 0 -> closeMenu();
                case 1 -> renameCard();
                case 2 -> editCardDescription();
                case 3 -> setCardBlockedState(!card.getIsBlocked());
                case 4 -> showBlockChangeLog();
                default -> messages.push("Opção inválida.");
            }
        }
    }

    private void renameCard() {
        Console.clear();

        System.out.println("Renomear Card");

        System.out.print("\n\tRenomear para: ");

        card.setTitle(scan.nextLine());

        CardDao.update(card);

        messages.push("Card renomeado com sucesso.");
    }

    private void editCardDescription() {
        Console.clear();

        System.out.println("Editar Descrição");

        System.out.print("\n\tSubstituir por: ");

        card.setDescription(scan.nextLine());

        CardDao.update(card);

        messages.push("Descrição do card alterado com sucesso.");
    }

    private void setCardBlockedState(boolean newState) {
        Console.clear();

        System.out.println(newState ? "Bloquear Card" : "Desbloquear Card");

        System.out.print("\n\tJustificativa: ");

        card.setIsBlocked(newState);
        card.addBlockChangeLog(scan.nextLine());

        CardDao.update(card);
    }

    private void showBlockChangeLog() {
        for (String changeLog : card.getBlockChangeLog()) {
            messages.push(changeLog);
        }
    }
}
