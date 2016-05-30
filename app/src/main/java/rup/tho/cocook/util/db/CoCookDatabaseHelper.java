package rup.tho.cocook.util.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rup.tho.cocook.R;
import rup.tho.cocook.data.CookStep;
import rup.tho.cocook.data.Ingredient;
import rup.tho.cocook.data.IngredientRecipe;
import rup.tho.cocook.data.Recipe;
import rup.tho.cocook.data.Unit;

public class CoCookDatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "cocook.db";
    private static final String TAG = CoCookDatabaseHelper.class.getSimpleName();
    private static String DATABASE_PATH;
    // if changes are made, this has to be increased
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<Ingredient, Long> ingDao;
    private RuntimeExceptionDao<Unit, Long> unitDao;
    private RuntimeExceptionDao<Recipe, Long> recipeDao;
    private RuntimeExceptionDao<IngredientRecipe, Long> ingRecipeDao;
    private RuntimeExceptionDao<CookStep, Long> stepDao;
    private Context context;

    private PreparedQuery<Ingredient> ingredientsForRecipesQuery;

    public CoCookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        if (android.os.Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {

    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
    }

    public RuntimeExceptionDao<Ingredient, Long> getIngDao() {
        if (ingDao == null) {
            ingDao = getRuntimeExceptionDao(Ingredient.class);
        }
        return ingDao;
    }

    public RuntimeExceptionDao<IngredientRecipe, Long> getIngRecipeDao() {
        if (ingRecipeDao == null) {
            ingRecipeDao = getRuntimeExceptionDao(IngredientRecipe.class);
        }
        return ingRecipeDao;
    }


    public RuntimeExceptionDao<Unit, Long> getUnitDao() {
        if (unitDao == null) {
            unitDao = getRuntimeExceptionDao(Unit.class);
        }
        return unitDao;
    }

    public RuntimeExceptionDao<Recipe, Long> getRecipeDao() {
        if (recipeDao == null) {
            recipeDao = getRuntimeExceptionDao(Recipe.class);
        }
        return recipeDao;
    }

    public RuntimeExceptionDao<CookStep, Long> getStepDao() {
        if (stepDao == null) {
            stepDao = getRuntimeExceptionDao(CookStep.class);
        }
        return stepDao;
    }
    public void createNewDatabase() {
        try {
            // if db not existing, this will work (returns false then)
            context.deleteDatabase(this.getDatabaseName());

            createTables(this.getConnectionSource());
            initTables();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void initTables() throws SQLException {
        String shortNameArray[] = context.getResources().getStringArray(R.array.unit_shortname);
        String nameArray[] = context.getResources().getStringArray(R.array.unit_name);
        for (int u = 0; u < shortNameArray.length; u++) {
            Unit unit = new Unit(shortNameArray[u], nameArray[u]);
            getUnitDao().create(unit);
        }

        String ingredientArray[] = context.getResources().getStringArray(R.array.ingredients);
        for (int i = 0; i < ingredientArray.length; i++) {
            Ingredient ing = new Ingredient(ingredientArray[i]);;
            if (i % 3 == 0) {
                ing.setPreference(Ingredient.PrefVal.NEG);
                ing.setAmount(10+i);
                ing.setUnit(getUnitDao().queryForId((long) i % shortNameArray.length + 1));
            } else {
                if (i % 2 == 0) {
                    ing.setPreference(Ingredient.PrefVal.POS);
                    ing.setAmount(Math.abs(10-i));
                    ing.setUnit(getUnitDao().queryForId((long) i % shortNameArray.length + 1));
                }
            }
            addIngredientWithRank(ing);
        }

        Recipe rec = new Recipe("Toast", 0, 30, Recipe.Difficulty.EASY, "The favourite meal of students all over the world.");
        getRecipeDao().create(rec);
        createSteps(rec, context.getResources().getStringArray(R.array.steps_toast));
        createIngRecipe(rec, context.getResources().getStringArray(R.array.ing_toast), context.getResources().getIntArray(R.array.amounts_toast), context.getResources().getStringArray(R.array.units_toast));

        rec = new Recipe("Fish and chips", 0, 50, Recipe.Difficulty.EASY, "A british classic. My thanks to Jamie Oliver for the recipe!");
        getRecipeDao().create(rec);
        createSteps(rec, context.getResources().getStringArray(R.array.steps_fish_chips));
        createIngRecipe(rec, context.getResources().getStringArray(R.array.ing_fish), context.getResources().getIntArray(R.array.amounts_fish), context.getResources().getStringArray(R.array.units_fish));

        rec = new Recipe("Wiener Schnitzel", 1, 30, Recipe.Difficulty.MEDIUM, "The official favourite meal of each and every person from Germany and Austria. (Or so they say :-)");
        recipeDao.create(rec);
        createSteps(rec, context.getResources().getStringArray(R.array.steps_schnitzel));
        createIngRecipe(rec, context.getResources().getStringArray(R.array.ing_schnitzel), context.getResources().getIntArray(R.array.amounts_schnitzel), context.getResources().getStringArray(R.array.units_schnitzel));
        
        rec = new Recipe("Pizza", 1, 0, Recipe.Difficulty.MEDIUM, "Pizza is a regular visitor to our house, only it doesn't usually come in the form of a delivery person. Making homemade pizza from prepared dough is a quick and easy dinner any night of the week. Even making your own dough only really adds a few minutes to the prep time! Pile on your favorite toppings and get ready to chow down.");
        recipeDao.create(rec);
        createSteps(rec, context.getResources().getStringArray(R.array.steps_pizza));
        createIngRecipe(rec, context.getResources().getStringArray(R.array.ing_pizza), context.getResources().getIntArray(R.array.amounts_pizza), context.getResources().getStringArray(R.array.units_pizza));
        
        rec = new Recipe("Filet Mignon", 2, 36, Recipe.Difficulty.HARD, "Bite-sized cuts of filet mignon are stuffed with jalapeno-spiked cream cheese, wrapped in bacon, and grilled. This dish is very popular with our tailgating crowd at football games. Its very easy to prepare and only takes a few minutes to grill.");
        recipeDao.create(rec);
        createSteps(rec, context.getResources().getStringArray(R.array.steps_steak));
        createIngRecipe(rec, context.getResources().getStringArray(R.array.ing_steak), context.getResources().getIntArray(R.array.amounts_steak), context.getResources().getStringArray(R.array.units_steak));

    }

    private void createIngRecipe(Recipe rec, String[] ingNames, int[] amounts, String[] unitShortNames) {

        for(int i = 0; i < ingNames.length; i++) {
            IngredientRecipe ingRec = new IngredientRecipe(getIngDao().queryForEq(Ingredient.INGREDIENT_NAME_FIELD_NAME, ingNames[i]).get(0),
                    rec, amounts[i], getUnitDao().queryForEq(Unit.UNIT_SHORTNAME_FIELD_NAME, unitShortNames[i]).get(0));
            getIngRecipeDao().create(ingRec);
        }
    }

    private void createSteps(Recipe rec, String[] stringArray) {
        for(int i = 1; i<=stringArray.length; i++)
        {
            CookStep step = new CookStep(rec, i, stringArray[i-1]);
            getStepDao().create(step);
        }
    }

    private void createTables(ConnectionSource connectionSource) throws SQLException {
        TableUtils.createTable(connectionSource, Unit.class);
        TableUtils.createTable(connectionSource, Ingredient.class);
        TableUtils.createTable(connectionSource, Recipe.class);
        TableUtils.createTable(connectionSource, IngredientRecipe.class);
        TableUtils.createTable(connectionSource, CookStep.class);
    }


    public Cursor getNotPreferencedIngredientsSorted() {
        // when you are done, prepare your query and build an iterator
        CloseableIterator<Ingredient> iterator = null;
        Cursor cursor = null;
        try {
            QueryBuilder<Ingredient, Long> qb = getIngDao().queryBuilder();
            qb.orderBy(Ingredient.INGREDIENT_RANK_FIELD_NAME, true);
            qb.where().not().eq(Ingredient.INGREDIENT_PREFERENCED_FIELD_NAME, Ingredient.PrefVal.NOT);
            iterator = ingDao.iterator(qb.prepare());

            // get the raw results which can be cast under Android
            AndroidDatabaseResults results =
                    (AndroidDatabaseResults) iterator.getRawResults();
            cursor = results.getRawCursor();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return cursor;
    }

    public Cursor getIngredientsInFridgeSorted() {
        CloseableIterator<Ingredient> iterator = null;
        Cursor cursor = null;
        try {
            QueryBuilder<Ingredient, Long> qb = getIngDao().queryBuilder();
            qb.orderBy(Ingredient.INGREDIENT_NAME_FIELD_NAME, true);
            qb.where().not().eq(Ingredient.INGREDIENT_AMOUNT_FIELD_NAME, Ingredient.NO_AMOUNT);
            Log.d(TAG, qb.prepareStatementString());
            iterator = ingDao.iterator(qb.prepare());

            // get the raw results which can be cast under Android
            AndroidDatabaseResults results =
                    (AndroidDatabaseResults) iterator.getRawResults();
            cursor = results.getRawCursor();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

        return cursor;
    }

    public Unit getUnitForId(long id) {
        return getUnitDao().queryForId(id);
    }

    public List<Ingredient> getIngredientsNotInFridgeSorted() throws SQLException {
        return getIngDao().queryBuilder().where().eq(Ingredient.INGREDIENT_AMOUNT_FIELD_NAME, Ingredient.NO_AMOUNT).query();
    }

    public void addIngredientWithRank(Ingredient ing) throws SQLException {
        RuntimeExceptionDao<Ingredient, Long> dao = getIngDao();
        ing.setRank(dao.queryBuilder().where().not().eq(Ingredient.INGREDIENT_PREFERENCED_FIELD_NAME, Ingredient.PrefVal.NOT).countOf() + 1);
        dao.create(ing);
    }

    public Ingredient addIngredient(Ingredient ing) throws SQLException {
        if (getIngDao().queryBuilder().where().like(Ingredient.INGREDIENT_NAME_FIELD_NAME, ing.getName()).query().size() != 0) {
            return null;
        }
        ingDao.create(ing);
        return ing;
    }

    public void updateIngredient(Ingredient ing) throws SQLException {
        getIngDao().update(ing);
    }

    public void addIngredientToPref(Ingredient ing) throws SQLException {
        ing.setRank(getIngDao().queryBuilder().where().not().eq(Ingredient.INGREDIENT_PREFERENCED_FIELD_NAME, Ingredient.PrefVal.NOT).countOf() + 1);
        getIngDao().update(ing);
    }

    public void rankDownIngredientById(long id) {
        Ingredient ing = getIngDao().queryForId(id);
        long rank = ing.getRank();

        try {
            if (rank < ingDao.queryBuilder().where().not().eq(Ingredient.INGREDIENT_PREFERENCED_FIELD_NAME, Ingredient.PrefVal.NOT).countOf()) {
                ing.setRank(ing.getRank() + 1);
                ingDao.updateRaw("UPDATE ingredient SET ing_rank = ing_rank - 1 WHERE ing_rank == " + (rank + 1));
                ingDao.update(ing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void rankUpIngredientById(long id) {
        Ingredient ing = getIngDao().queryForId(id);
        long rank = ing.getRank();
        if (rank > 1) {
            ing.setRank(ing.getRank() - 1);
            ingDao.updateRaw("UPDATE ingredient SET ing_rank = ing_rank + 1 WHERE ing_rank == " + (rank - 1));
            ingDao.update(ing);
        }
    }

    public void rankFirstIngredientById(long id) {
        Ingredient ing = getIngDao().queryForId(id);
        ingDao.updateRaw("UPDATE ingredient SET ing_rank = ing_rank + 1 WHERE ing_rank < " + ing.getRank());

        ing.setRank(1);
        ingDao.update(ing);
    }

    public void rankLastIngredientById(long id) {
        Ingredient ing = getIngDao().queryForId(id);
        try {
            ingDao.updateRaw("UPDATE ingredient SET ing_rank = ing_rank - 1 WHERE ing_rank > " + ing.getRank());
            ing.setRank(ingDao.queryBuilder().where().not().eq(Ingredient.INGREDIENT_PREFERENCED_FIELD_NAME, Ingredient.PrefVal.NOT).countOf());
            ingDao.update(ing);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteIngredientFromPref(long id) {
        getIngDao().updateRaw("UPDATE ingredient SET ing_rank = ing_rank - 1 WHERE ing_rank > " + ingDao.queryForId(id).getRank());
        Ingredient ing = ingDao.queryForId(id);
        ing.setPreference(Ingredient.PrefVal.NOT);
        ingDao.update(ing);
    }

    public void deleteIngredientFromFridge(long id) {
        Ingredient ing = getIngDao().queryForId(id);
        ing.setAmount(Ingredient.NO_AMOUNT);
        ingDao.update(ing);
    }

    public List<Ingredient> getNotPreferencedIngredients() {
        return ingDao.queryForEq(Ingredient.INGREDIENT_PREFERENCED_FIELD_NAME, Ingredient.PrefVal.NOT);
    }

    public List<Unit> getUnits() {
        return getUnitDao().queryForAll();
    }

    public Ingredient getIngredientForName(String ingName) throws SQLException {
        return getIngDao().queryBuilder().where()
                .like(Ingredient.INGREDIENT_NAME_FIELD_NAME, ingName).queryForFirst();
    }

    public Cursor getAllRecipes() {
        CloseableIterator<Recipe> iterator = null;
        Cursor cursor = null;
        try {
            QueryBuilder<Recipe, Long> qb = getRecipeDao().queryBuilder();
            qb.orderBy(Recipe.RECIPE_NAME_FIELD_NAME, true);
            iterator = recipeDao.iterator(qb.prepare());

            // get the raw results which can be cast under Android
            AndroidDatabaseResults results =
                    (AndroidDatabaseResults) iterator.getRawResults();
            cursor = results.getRawCursor();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

        return cursor;
    }

    public Cursor getRecipesSorted(String column, boolean ascending) {
        CloseableIterator<Recipe> iterator = null;
        Cursor cursor = null;
        try {
            QueryBuilder<Recipe, Long> qb = getRecipeDao().queryBuilder();
            qb.orderBy(column, ascending);
            if (column.equals(Recipe.RECIPE_HOURS_FIELD_NAME)) {
                qb.orderBy(Recipe.RECIPE_MINUTES_FIELD_NAME, ascending);
            }

            iterator = recipeDao.iterator(qb.prepare());

            // get the raw results which can be cast under Android
            AndroidDatabaseResults results =
                    (AndroidDatabaseResults) iterator.getRawResults();
            cursor = results.getRawCursor();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }

        return cursor;
    }

    public Cursor getIngredientsForRecipe(Recipe recipe) {
        Cursor cursor = null;
        CloseableIterator<IngredientRecipe> iterator = null;
    /*    try {
            if (ingredientsForRecipesQuery == null) {

                ingredientsForRecipesQuery = makeIngredientsForRecipeQuery();
            }
            ingredientsForRecipesQuery.setArgumentHolderValue(0, recipe);

            Log.d(TAG, ingredientsForRecipesQuery.getStatement());

            // get the raw results which can be cast under Android
            AndroidDatabaseResults results =
                    (AndroidDatabaseResults) iterator.getRawResults();

            cursor = results.getRawCursor();
        } catch (SQLException e) {
            e.printStackTrace();
        }*/

        try {
            QueryBuilder<Recipe, Long> qb = getRecipeDao().queryBuilder();
            QueryBuilder<IngredientRecipe, Long> qa = getIngRecipeDao().queryBuilder();
            qa.orderBy(IngredientRecipe.INGRECIPE_ID_FIELD_NAME, true);
            qb.where().idEq(recipe.getId());

            iterator = ingRecipeDao.iterator(qa.join(qb).prepare());

            // get the raw results which can be cast under Android
            AndroidDatabaseResults results =
                    (AndroidDatabaseResults) iterator.getRawResults();
            cursor = results.getRawCursor();
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
        }
        return cursor;
    }

    private PreparedQuery<Ingredient> makeIngredientsForRecipeQuery() throws SQLException {
        // build our inner query for IngredientRecipe objects
        QueryBuilder<IngredientRecipe, Long> userPostQb = ingRecipeDao.queryBuilder();
        // just select the ingredient-id field
        userPostQb.selectColumns(IngredientRecipe.INGRECIPE_ID_FIELD_NAME);
        SelectArg userSelectArg = new SelectArg();
        userPostQb.where().eq(IngredientRecipe.INGRECIPE_RECIPE_FIELD_NAME, userSelectArg);

        // build our outer query for Post objects
        QueryBuilder<Ingredient, Long> postQb = ingDao.queryBuilder();
        // where the id matches in the post-id from the inner query
        postQb.where().in(Ingredient.INGREDIENT_ID_FIELD_NAME, userPostQb);
        return postQb.prepare();
    }


    public void removeIngredientsFromFridge(Recipe mRecipe) {
        // TODO: not yet implemented, since we have no algorithm which really suggests us
        //       possible alternatives
    }

    public Ingredient getIngredientById(long id) {
        return getIngDao().queryForId(id);
    }
}
