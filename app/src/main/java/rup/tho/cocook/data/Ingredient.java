package rup.tho.cocook.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * The ingredient in relation to the user
 * holds user preferences and if it is existent in the fridge.
 */
@DatabaseTable(tableName = "ingredient")
public class Ingredient {
    public static final String INGREDIENT_ID_FIELD_NAME = "_id";
    public static final String INGREDIENT_NAME_FIELD_NAME = "ing_name";
    public final static String INGREDIENT_RANK_FIELD_NAME = "ing_rank";
    public final static String INGREDIENT_PREFERENCED_FIELD_NAME = "ing_pref";
    public static final String INGREDIENT_UNIT_FIELD_NAME = "ing_unit";
    public static final String INGREDIENT_AMOUNT_FIELD_NAME = "ing_amount";
    public static final int NO_AMOUNT = -1;

    @DatabaseField(generatedId = true, columnName = INGREDIENT_ID_FIELD_NAME)
    private long id;

    @DatabaseField(canBeNull = false, columnName = INGREDIENT_NAME_FIELD_NAME, unique = true)
    private String name;

    @DatabaseField(columnName = INGREDIENT_PREFERENCED_FIELD_NAME, dataType = DataType.ENUM_INTEGER)
    private PrefVal preference;

    @DatabaseField(columnName = INGREDIENT_RANK_FIELD_NAME)
    private long rank;

    @DatabaseField(columnName = INGREDIENT_AMOUNT_FIELD_NAME)
    private int amount;

    @DatabaseField(foreign = true, columnName = INGREDIENT_UNIT_FIELD_NAME)
    private Unit unit;

    public Ingredient() {
    }

    public Ingredient(String name) {
        this.name = name;
        this.rank = 0;
        this.preference = PrefVal.NOT;
        this.amount = NO_AMOUNT;
    }

    public Ingredient(String name, int amount, Unit unit) {
        this.name = name;
        this.amount = amount;
        this.unit = unit;
        this.preference = PrefVal.NOT;
        this.rank = 0;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PrefVal getPreference() {
        return preference;
    }

    public void setPreference(PrefVal preference) {
        this.preference = preference;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public enum PrefVal {
        NOT(0), POS(1), NEG(2);

        public final int value;
        PrefVal(int value) {
            this.value = value;
        }
    };

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return name;
    }

    public Unit getUnit() {
        return unit;
    }
}
