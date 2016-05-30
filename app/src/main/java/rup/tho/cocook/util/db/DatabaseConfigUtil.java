package rup.tho.cocook.util.db;


import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;


import rup.tho.cocook.data.CookStep;
import rup.tho.cocook.data.Ingredient;
import rup.tho.cocook.data.IngredientRecipe;
import rup.tho.cocook.data.Recipe;
import rup.tho.cocook.data.Unit;

// this file is needed for optimization according to ormlite
// for details see:
// http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Config-Optimization
public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    private static final Class<?>[] classes = new Class[]{
            Unit.class, Ingredient.class, Recipe.class, IngredientRecipe.class, CookStep.class
    };

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
