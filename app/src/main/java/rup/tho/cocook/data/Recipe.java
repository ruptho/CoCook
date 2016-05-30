package rup.tho.cocook.data;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by thorsten on 5/29/16.
 */
@DatabaseTable(tableName = "recipe")
public class Recipe {
    public static final String RECIPE_ID_FIELD_NAME = "_id";
    public static final String RECIPE_NAME_FIELD_NAME = "recipe_name";
    public static final String RECIPE_DESCRIPTION_FIELD_NAME = "recipe_description";
    public static final String RECIPE_HOURS_FIELD_NAME = "recipe_hours";
    public static final String RECIPE_MINUTES_FIELD_NAME = "recipe_minutes";
    public static final String RECIPE_DIFFICULTY_FIELD_NAME = "recipe_diff";

    @DatabaseField(generatedId = true, columnName = RECIPE_ID_FIELD_NAME)
    private long id;

    @DatabaseField(columnName = RECIPE_NAME_FIELD_NAME)
    private String name;

    @DatabaseField(columnName = RECIPE_DESCRIPTION_FIELD_NAME)
    private String description;

    @DatabaseField(columnName = RECIPE_HOURS_FIELD_NAME)
    private int hours;

    @DatabaseField(columnName = RECIPE_MINUTES_FIELD_NAME)
    private int minutes;

    @DatabaseField(columnName = RECIPE_DIFFICULTY_FIELD_NAME, dataType = DataType.ENUM_INTEGER)
    private Difficulty difficulty;

    @ForeignCollectionField(orderColumnName = CookStep.CS_NUMBER_FIELD_NAME)
    ForeignCollection<CookStep> steps;

    public Recipe() {
    }

    public Recipe(String name, int hours, int minutes, Difficulty difficulty, String description) {
        this.name = name;
        this.hours = hours;
        this.minutes = minutes;
        this.difficulty = difficulty;
        this.description = description;
    }

    public enum Difficulty {
        EASY(0), MEDIUM(1), HARD(2);

        public final int value;
        Difficulty(int value) {
            this.value = value;
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public ForeignCollection<CookStep> getSteps() {
        return steps;
    }
}
