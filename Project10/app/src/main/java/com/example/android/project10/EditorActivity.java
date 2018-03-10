package com.example.android.project10;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.*;
import static java.lang.Integer.parseInt;

import android.view.View.OnClickListener;

/**
 * Created by Manik on 27-04-2017.
 */

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_ITEM_LOADER = 0;
    public static final String ImageLink = "content://com.android.providers.media.documents/document/image";
    public int quantityShow;
    static final int TAKE_IMAGE = 100;
    Uri imageUri;
    private ImageView mItemImage;
    private Uri mCurrentItemUri;
    private EditText mNameEditText;
    public TextView mQuantityEditText;
    private EditText mPriceEditText;
    private boolean mItemHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Button saved = (Button) findViewById(R.id.save);
        saved.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkFields())
                    saveItem();
                finish();
            }
        });

        Button Increment = (Button) findViewById(R.id.increment);
        Increment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                quantityShow = quantityShow + 1;
                displayQuantity(quantityShow);
            }
        });

        Button Decrement = (Button) findViewById(R.id.decrement);
        Decrement.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantityShow <= 0) {
                    toast();
                    return;
                }
                quantityShow = quantityShow - 1;
                displayQuantity(quantityShow);
            }
        });

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        if (mCurrentItemUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_item));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_item));
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }
        mNameEditText = (EditText) findViewById(R.id.edit_item_name);
        Button mQuantityIncEditText = (Button) findViewById(R.id.increment);
        Button mQuantityDecEditText = (Button) findViewById(R.id.decrement);
        mQuantityEditText = (TextView) findViewById(R.id.quantity_text_view);
        mPriceEditText = (EditText) findViewById(R.id.edit_price);
        mNameEditText.setOnTouchListener(mTouchListener);
        mQuantityIncEditText.setOnTouchListener(mTouchListener);
        mQuantityDecEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mItemImage = (ImageView) findViewById(R.id.image);
        mItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE);
                }
            }
        });
    }

    private String mailItems() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        return "NAME: " + nameString + "\nQUANTITY: " + quantityString + "\nPRICE: " + priceString;
    }

    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String imageUriString = "";
        if (imageUri != null) {
            imageUriString = imageUri.toString();
        }

        if (mCurrentItemUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(quantityString) &&
                TextUtils.isEmpty(priceString)) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, quantityString);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE, priceString);
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE, imageUriString);
        int price =parseInt(priceString) ;
        values.put(ItemContract.ItemEntry.COLUMN_ITEM_PRICE, price);
        if (mCurrentItemUri == null) {
            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

            if (newUri == null) {
                makeText(this, getString(R.string.editor_insert_item_failed),
                        LENGTH_SHORT).show();
            } else {
                makeText(this, getString(R.string.editor_insert_item_successful),
                        LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            if (rowsAffected == 0) {
                makeText(this, getString(R.string.editor_update_item_failed),
                        LENGTH_SHORT).show();
            } else {
                makeText(this, getString(R.string.editor_update_item_successful),
                        LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == TAKE_IMAGE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            mItemImage.setImageURI(Uri.parse(String.valueOf(imageUri)));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void order(View view) {
        final String items = mailItems();
        Button Order = (Button) findViewById(R.id.order);
        EditText text = (EditText) findViewById(R.id.edit_item_name);
        final String value = text.getText().toString();
        Order.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Item Order for " + value);
                intent.putExtra(Intent.EXTRA_TEXT, items);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkFields() {
        String pr_name = mNameEditText.getText().toString().trim();
        String pr_price = mPriceEditText.getText().toString().trim();
        String pr_quantity = mQuantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(pr_name) || TextUtils.isEmpty(pr_price) || TextUtils.isEmpty(pr_quantity)) {
            mNameEditText.setError("Enter name");
            mPriceEditText.setError("Enter price");
            mQuantityEditText.setError("Enter quantity");
            return false;
        }
        return true;
    }

    public void toast() {
        Toast.makeText(this, "cannot enter quantity less than 0", LENGTH_SHORT).show();
    }

    public void delete(View view) {
        showDeleteConfirmationDialog();
    }


    private void displayQuantity(int n) {
        TextView quantityTextView = (TextView) findViewById(
                R.id.quantity_text_view);
        quantityTextView.setText("" + n);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ItemContract.ItemEntry._ID,
                ItemContract.ItemEntry.COLUMN_ITEM_NAME,
                ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY,
                ItemContract.ItemEntry.COLUMN_ITEM_PRICE,
                ItemContract.ItemEntry.COLUMN_ITEM_IMAGE,};
        return new CursorLoader(this,
                mCurrentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
            int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
            int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);

            String name = cursor.getString(nameColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            quantityShow = cursor.getInt(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            mQuantityEditText.setText(quantity);
            mNameEditText.setText(name);
            mPriceEditText.setText(Integer.toString(price));
            String imageUriString = cursor.getString(cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE));
            if (imageUriString.startsWith(ImageLink)) {
                mItemImage.setImageURI(Uri.parse(imageUriString));
            } else {
                mItemImage.setImageResource(R.drawable.add_image);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");
        mItemImage.setImageURI(null);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        if (mCurrentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);
            if (rowsDeleted == 0) {
                makeText(this, getString(R.string.editor_delete_item_failed),
                        LENGTH_SHORT).show();
            } else {
                makeText(this, getString(R.string.editor_delete_item_successful),
                        LENGTH_SHORT).show();
            }
        }
        finish();
    }

}