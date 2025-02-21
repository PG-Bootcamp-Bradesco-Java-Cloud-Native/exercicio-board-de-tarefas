package model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Card extends BaseModel {
    private Boolean isBlocked;
    private String description;
    private Column column;
    private List<String> blockChangeLog = new ArrayList<>();

    public void addBlockChangeLog(String string) {
        blockChangeLog.add(string);
    }

    public String getBlockChangeLogString() {
        return String.join("<endcl/>", blockChangeLog);
    }

    private List<String> parseBlockChangeLogString(String s) {
        return new ArrayList<String>(Arrays.asList(s.split("<endcl/>")));
    }

    public Card(String title, String description, Column column) {
        super(null, title, new Date());
        this.isBlocked = false;
        this.description = description;
        this.column = column;
    }

    public Card(Integer id, String title, Date createdAt, Boolean isBlocked, String description, String blockChangeLogString, Column column) {
        super(id, title, createdAt);
        this.isBlocked = isBlocked;
        this.description = description;
        this.column = column;
        this.blockChangeLog = parseBlockChangeLogString(blockChangeLogString);
    }
}
