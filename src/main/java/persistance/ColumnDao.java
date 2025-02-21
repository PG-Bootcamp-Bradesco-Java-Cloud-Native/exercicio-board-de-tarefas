package persistance;

import model.Board;
import model.Card;
import model.Column;
import model.ColumnType;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ColumnDao {
    private ColumnDao() {
    }

    public static Column reload(Column column) {
        var board = column.getBoard();
        var newColumn = findById(column.getId());
        newColumn.setBoard(board);
        return newColumn;
    }

    public static Column create(String title, ColumnType type, Short order, Board board) {
        var newColumn = new Column(title, type, order, board);

        try {
            var queryString = String.format(
                    "INSERT INTO columns (title, type, order_in_board, created_at, board_id) " +
                            "VALUES (\"%s\", %d, %d, \"%s\", %d)",
                    newColumn.getTitle(),
                    newColumn.getType().ordinal(),
                    newColumn.getOrder(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").format(newColumn.getCreatedAt()),
                    board.getId()
            );
            var query = DbContext.connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            query.execute();
            newColumn.setId(query.getGeneratedKeys().getInt(1));
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }

        return newColumn;
    }

    public static Column findById(Integer id) {
        try {
            var queryString = String.format("SELECT * FROM columns WHERE id = %d", id);
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
            var resultSet = query.getResultSet();
            var newColumn = new Column(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .parse(resultSet.getString("created_at")),
                    ColumnType.values()[resultSet.getInt("type")],
                    (short) resultSet.getInt("order_in_board"),
                    null
            );
            newColumn.setCards(CardDao.findAllByColumn(newColumn));
            return newColumn;
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
            return null;
        }
    }

    public static List<Column> findAllByBoard(Board board) {
        try {
            var queryString = String.format("SELECT * FROM columns WHERE board_id = %d ORDER BY order_in_board ASC", board.getId());
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
            var resultSet = query.getResultSet();
            var columns = new ArrayList<Column>();
            while (resultSet.next()) {
                var newColumn = new Column(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm")
                                .parse(resultSet.getString("created_at")),
                        ColumnType.values()[resultSet.getInt("type")],
                        (short) resultSet.getInt("order_in_board"),
                        board
                );
                columns.add(newColumn);
            }
            return columns;
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
            return null;
        }
    }

    public static void update(Column column) {
        try {
            var queryString = String.format("UPDATE columns SET " +
                            "title = \"%s\", " +
                            "type = %d, " +
                            "order_in_board = %d " +
                            "WHERE id = %d",
                    column.getTitle(),
                    column.getType().ordinal(),
                    column.getOrder(),
                    column.getId()
            );
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }
    }

    public static void delete(Column column) {
        var cards = CardDao.findAllByColumn(column);
        for (Card card : cards) {
            CardDao.delete(card);
        }
        deleteById(column.getId());
    }

    private static void deleteById(Integer id) {
        try {
            var queryString = String.format("DELETE FROM columns WHERE id = %d", id);
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }
    }
}
