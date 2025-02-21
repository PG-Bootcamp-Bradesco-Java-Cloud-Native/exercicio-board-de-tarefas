package model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Board extends BaseModel {
    private List<Column> columns = new ArrayList<>();

    public Board(String name) {
        super(null, name, new Date());
    }

    public Board(Integer id, String title, Date createdAt) {
        super(id, title, createdAt);
    }
}
