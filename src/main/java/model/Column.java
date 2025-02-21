package model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Column extends BaseModel {
    private ColumnType type;
    private Short order;
    private Board board;
    private List<Card> cards = new ArrayList<>();

    public Column(String title, ColumnType type, Short order, Board board) {
        super(null, title, new Date());
        this.type = type;
        this.order = order;
        this.board = board;
    }

    public Column(Integer id, String title, Date createdAt, ColumnType type, Short order, Board board) {
        super(id, title, createdAt);
        this.type = type;
        this.order = order;
        this.board = board;
    }
}
