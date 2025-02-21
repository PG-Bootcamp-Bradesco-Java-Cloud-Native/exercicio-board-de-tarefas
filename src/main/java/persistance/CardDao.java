package persistance;

import model.Card;
import model.Column;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CardDao {
    private CardDao() {
    }

    public static Card reload(Card card) {
        var column = card.getColumn();
        var newCard = findById(card.getId());
        newCard.setColumn(column);
        return newCard;
    }

    public static Card create(String title, String description, Column column) {
        var newCard = new Card(title, description, column);

        try {
            var queryString = String.format(
                    "INSERT INTO cards (title, created_at, is_blocked, description, block_change_log, column_id) " +
                            "VALUES (\"%s\", \"%s\", %d, \"%s\", \"%s\", %d)",
                    newCard.getTitle(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").format(newCard.getCreatedAt()),
                    newCard.getIsBlocked() ? 1 : 0,
                    newCard.getDescription(),
                    newCard.getBlockChangeLogString(),
                    column.getId()
            );
            var query = DbContext.connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            query.execute();
            newCard.setId(query.getGeneratedKeys().getInt(1));
            System.out.println();
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }

        return newCard;
    }

    public static Card findById(Integer id) {
        try {
            var queryString = String.format("SELECT * FROM cards WHERE id = %d", id);
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
            var resultSet = query.getResultSet();
            var newCard = new Card(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .parse(resultSet.getString("created_at")),
                    resultSet.getBoolean("is_blocked"),
                    resultSet.getString("description"),
                    resultSet.getString("block_change_log"),
                    null
            );
            return newCard;
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
            return null;
        }
    }

    public static List<Card> findAllByColumn(Column column) {
        try {
            var queryString = String.format("SELECT * FROM cards WHERE column_id = %d", column.getId());
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
            var resultSet = query.getResultSet();
            var cards = new ArrayList<Card>();
            while (resultSet.next()) {
                var newCard = new Card(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm")
                                .parse(resultSet.getString("created_at")),
                        resultSet.getBoolean("is_blocked"),
                        resultSet.getString("description"),
                        resultSet.getString("block_change_log"),
                        column
                );
                cards.add(newCard);
            }
            return cards;
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
            return null;
        }
    }

    public static void update(Card card) {
        try {
            var queryString = String.format("UPDATE cards SET " +
                            "title = \"%s\", " +
                            "is_blocked = %d, " +
                            "description = \"%s\", " +
                            "block_change_log = \"%s\", " +
                            "column_id = %d " +
                            "WHERE id = %d",
                    card.getTitle(),
                    card.getIsBlocked() ? 1 : 0,
                    card.getDescription(),
                    card.getBlockChangeLogString(),
                    card.getColumn().getId(),
                    card.getId()
            );
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }
    }

    public static void delete(Card card) {
        deleteById(card.getId());
    }

    private static void deleteById(Integer id) {
        try {
            var queryString = String.format("DELETE FROM cards WHERE id = %d", id);
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }
    }
}
