package com.example.android.project10;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.N;

/**
 * Created by Manik on 27-04-2017.
 */

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView item_image = (ImageView) view.findViewById(R.id.item_image);
        TextView Sale = (TextView) view.findViewById(R.id.sale);

        int nameColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_NAME);
        final int quantityColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(ItemContract.ItemEntry.COLUMN_ITEM_PRICE);
        final int idColumnIndex = cursor.getInt(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry._ID));
        String imageUriString = cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_ITEM_IMAGE));

        if (imageUriString.startsWith(EditorActivity.ImageLink)) {
            item_image.setImageURI(Uri.parse(imageUriString));
        } else {
            item_image.setImageResource(R.drawable.add_image);
        }

        Sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity;
                quantity = quantityColumnIndex - 1;
                ContentValues values = new ContentValues();
                values.put(ItemContract.ItemEntry.COLUMN_ITEM_QUANTITY, quantity);
                Uri currentUri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, idColumnIndex);
                context.getContentResolver().update(currentUri, values, null, null);
            }
        });

        String itemName = cursor.getString(nameColumnIndex);
        String itemQuantity = cursor.getString(quantityColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        nameTextView.setText(itemName);
        quantityTextView.setText(itemQuantity);
        priceTextView.setText(itemPrice);
    }

}
