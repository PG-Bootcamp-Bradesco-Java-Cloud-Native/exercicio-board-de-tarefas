package persistance;

import model.Board;
import model.Column;
import model.ColumnType;

import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoardDao {
    private BoardDao() {
    }

    public static Board reload(Board board) {
        var newBoard = findById(board.getId());
        return newBoard;
    }

    public static Board create(String title) {
        var newBoard = new Board(title);

        try {
            var queryString = String.format(
                    "INSERT INTO boards (title, created_at) VALUES (\"%s\", \"%s\")",
                    newBoard.getTitle(),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm").format(newBoard.getCreatedAt())
            );
            var query = DbContext.connection.prepareStatement(queryString, Statement.RETURN_GENERATED_KEYS);
            query.execute();
            newBoard.setId(query.getGeneratedKeys().getInt(1));
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }

        List<Column> defaultColumns = Arrays.asList(
                ColumnDao.create("TBD", ColumnType.Inicial, (short) 0, newBoard),
                ColumnDao.create("Done", ColumnType.Final, (short) 1, newBoard),
                ColumnDao.create("Cancelled", ColumnType.Cancelamento, (short) 2, newBoard)
        );

        newBoard.setColumns(defaultColumns);

        return newBoard;
    }

    public static Board findById(Integer id) {
        try {
            var queryString = String.format("SELECT * FROM boards WHERE id = %d", id);
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
            var resultSet = query.getResultSet();
            var newBoard = new Board(
                    resultSet.getInt("id"),
                    resultSet.getString("title"),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm")
                            .parse(resultSet.getString("created_at"))
            );
            newBoard.setColumns(ColumnDao.findAllByBoard(newBoard));
            return newBoard;
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
            return null;
        }
    }

    public static List<Board> findAll() {
        try {
            var queryString = "SELECT * FROM boards";
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
            var resultSet = query.getResultSet();
            var boards = new ArrayList<Board>();
            while (resultSet.next()) {
                var newBoard = new Board(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm")
                                .parse(resultSet.getString("created_at"))
                );
                newBoard.setColumns((ArrayList<Column>) ColumnDao.findAllByBoard(newBoard));
                boards.add(newBoard);
            }
            return boards;
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
            return null;
        }
    }

    public static void update(Board board) {
        try {
            var queryString = String.format("UPDATE boards SET " +
                            "title = \"%s\" " +
                            "WHERE id = %d",
                    board.getTitle(), board.getId()
            );
            var query = DbContext.connection.prepareStatement(queryString);
            var result = query.execute();
            System.out.println();
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }
    }

    public static void delete(Board board) {
        var columns = ColumnDao.findAllByBoard(board);
        for (Column column : columns) {
            ColumnDao.delete(column);
        }
        deleteById(board.getId());
    }

    private static void deleteById(Integer id) {
        try {
            var queryString = String.format("DELETE FROM boards WHERE id = %d", id);
            var query = DbContext.connection.prepareStatement(queryString);
            query.execute();
        } catch (Exception e) {
            System.err.println(e);
            e.getCause().printStackTrace();
        }
    }
}
