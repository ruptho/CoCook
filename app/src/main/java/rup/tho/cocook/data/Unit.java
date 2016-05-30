package rup.tho.cocook.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by thorsten on 5/29/16.
 */
@DatabaseTable(tableName = "unit")
public class Unit {

    public static final String UNIT_ID_FIELD_NAME = "unit_id";
    public static final String UNIT_NAME_FIELD_NAME = "unit_name";
    public static final String UNIT_SHORTNAME_FIELD_NAME = "unit_shortname";

    @DatabaseField(generatedId = true, columnName = UNIT_ID_FIELD_NAME)
    private long id;

    @DatabaseField(canBeNull = false, columnName = UNIT_NAME_FIELD_NAME, unique = true)
    private String name;

    @DatabaseField(canBeNull = false, columnName = UNIT_SHORTNAME_FIELD_NAME, unique = true)
    private String shortName;

    public Unit() {
    }

    public Unit(String shortName, String name) {
        this.shortName = shortName;
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return shortName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unit unit = (Unit) o;

        if (id != unit.id) return false;
        if (!name.equals(unit.name)) return false;
        return shortName.equals(unit.shortName);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + name.hashCode();
        result = 31 * result + shortName.hashCode();
        return result;
    }
}
