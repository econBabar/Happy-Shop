package happyshop.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import happyshop.models.Product;

public class SqliteUtils extends SQLiteOpenHelper
{
    private final static String DB_NAME = "happyshop.db";

    private final static int DB_VERSION = 1;

    private final static String COLUMN_ID = "_id";

    private final static String COLUMN_NAME = "name";

    private final static String COLUMN_CATEGORY = "category";

    private final static String COLUMN_PRICE = "price";

    private final static String COLUMN_DESCRIPTION = "description";

    private final static String COLUMN_IMAGE_URL = "image_url";

    private final static String COLUMN_UNDER_SALE = "under_sale";

    private final static String TABLE_CART = "cart";

    private final static String CREATE_TABLE_CART = "CREATE TABLE "
            + TABLE_CART
            + " (" + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " TEXT NOT NULL, "
            + COLUMN_CATEGORY + " TEXT NOT NULL, "
            + COLUMN_PRICE + " INTEGER, "
            + COLUMN_DESCRIPTION + " TEXT NOT NULL, "
            + COLUMN_IMAGE_URL + " TEXT, "
            + COLUMN_UNDER_SALE + " INTEGER)";

    public SqliteUtils(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);

        onCreate(sqLiteDatabase);
    }

    public void addToCart(Product product)
    {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, product.getId());
        values.put(COLUMN_NAME, product.getName());
        values.put(COLUMN_CATEGORY, product.getCategory());
        values.put(COLUMN_DESCRIPTION, product.getDescription());
        values.put(COLUMN_PRICE, product.getPrice());
        values.put(COLUMN_IMAGE_URL, product.getImgUrl());
        values.put(COLUMN_UNDER_SALE, product.isUnderSale());

        long rowId = db.insert(TABLE_CART, null, values);

        System.out.println("Inserted row id: " + rowId);

        db.close();
    }

    public long getCartCount()
    {
        SQLiteDatabase db = getReadableDatabase();

        long count = DatabaseUtils.queryNumEntries(db, TABLE_CART);

        System.out.println("Cart count: " + count);

        db.close();

        return count;
    }

    public void removeFromCart(int productId)
    {
        SQLiteDatabase db = getWritableDatabase();

        long rowsDeleted = db.delete(TABLE_CART, COLUMN_ID + "=?", new String[]{String.valueOf(productId)});

        System.out.println("Rows Deleted" + rowsDeleted);

        db.close();
    }

    public boolean isAlreadyInCart(int productId)
    {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_CART, new String[]{COLUMN_ID},
                COLUMN_ID + "=?", new String[]{String.valueOf(productId)}, null, null, null);

        return cursor.getCount() > 0 ? true : false;
    }


}
