package rup.tho.cocook.data;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by thorsten on 5/30/16.
 */
@DatabaseTable(tableName = "cookstep")
public class CookStep {
    public static final String CS_ID_FIELD_NAME = "_id";
    public static final String CS_NUMBER_FIELD_NAME = "step_number";
    public final static String CS_RECIPE_FIELD_NAME = "step_recipe";
    public final static String CS_TEXT_FIELD_NAME = "step_text";

    @DatabaseField(generatedId = true, columnName = CS_ID_FIELD_NAME)
    private long id;

    @DatabaseField(columnName = CS_NUMBER_FIELD_NAME, canBeNull = false)
    private int number;

    @DatabaseField(columnName = CS_TEXT_FIELD_NAME)
    private String text;

    @DatabaseField(foreign = true, foreignAutoRefresh = true,  canBeNull = false, columnName = CS_RECIPE_FIELD_NAME)
    private Recipe recipe;

    public CookStep() {
    }

    public CookStep(Recipe recipe, int number, String text) {
        this.number = number;
        this.text = text;
        this.recipe = recipe;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
