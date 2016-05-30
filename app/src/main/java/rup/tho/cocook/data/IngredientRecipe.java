package rup.tho.cocook.data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by thorsten on 5/29/16.
 */
@DatabaseTable(tableName = "ingredient_recipe")
public class IngredientRecipe {

    public static final String INGRECIPE_ID_FIELD_NAME = "_id";
    public static final String INGRECIPE_ING_FIELD_NAME = "ir_ing";
    public static final String INGRECIPE_RECIPE_FIELD_NAME = "ir_rec";
    public static final String INGRECIPE_AMOUNT_FIELD_NAME = "ir_amount";
    public static final String INGRECIPE_UNIT_FIELD_NAME = "ir_unit";

    @DatabaseField(generatedId = true, columnName = INGRECIPE_ID_FIELD_NAME)
    private long id;

    @DatabaseField(foreign = true, columnName = INGRECIPE_ING_FIELD_NAME)
    Ingredient ingredient;

    @DatabaseField(foreign = true, columnName = INGRECIPE_RECIPE_FIELD_NAME)
    Recipe recipe;

    @DatabaseField(columnName = INGRECIPE_AMOUNT_FIELD_NAME)
    private int amount;

    @DatabaseField(foreign = true, columnName = INGRECIPE_UNIT_FIELD_NAME)
    private Unit unit;

    public IngredientRecipe() {
    }

    public IngredientRecipe(Ingredient ingredient, Recipe recipe, int amount, Unit unit) {
        this.ingredient = ingredient;
        this.recipe = recipe;
        this.amount = amount;
        this.unit = unit;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
