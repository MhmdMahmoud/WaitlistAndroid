package com.example.waitlist;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.waitlist.Data.WaitlistContract.WaitlistEntry;
import com.example.waitlist.Data.WaitlistDBHelper;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton addGuestBtn;
    ImageView placeholderList;
    RecyclerView recyclerView;

    WaitlistDBHelper waitlistDBHelper;
    SQLiteDatabase sqLiteDatabase;
    GuestAdapter guestAdapter;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize views
        addGuestBtn = findViewById(R.id.main_floating_btn);
        placeholderList = findViewById(R.id.home_img_placeholder);
        recyclerView = findViewById(R.id.main_recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.hasFixedSize();
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));

        //Open Database and write to
        waitlistDBHelper = new WaitlistDBHelper(getApplicationContext());
        sqLiteDatabase = waitlistDBHelper.getWritableDatabase();

        //Set data to recyclerView
        cursor = getAllGuests();
        guestAdapter = new GuestAdapter(getApplicationContext(), cursor);
        recyclerView.setAdapter(guestAdapter);

        setListImageVisabilty();

        //Show dialog when click on floating button
        addGuestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomDialog();
            }
        });

        createItemTouchHelper();
    }

    private void showCustomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.newguuest_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText dialog_guestName = dialog.findViewById(R.id.dialog_gName);
        final EditText dialog_guestNumber = dialog.findViewById(R.id.dialog_gNumber);

        final Button dialog_addButton = dialog.findViewById(R.id.dialog_add_btn);
        final Button dialog_backButton = dialog.findViewById(R.id.dialog_back_btn);

        dialog_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = dialog_guestName.getText().toString();
                String number = dialog_guestNumber.getText().toString();

                if (name.isEmpty() || number.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter Valid data", Toast.LENGTH_LONG).show();
                } else {
                    addNewGuest(name, number);
                    guestAdapter.swapCursor(getAllGuests());

                    placeholderList.setVisibility(View.GONE);

                    dialog.dismiss();
                }
            }
        });

        dialog_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void addNewGuest(String name, String number) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(WaitlistEntry.COLUMN_GUEST_NAME, name);
        contentValues.put(WaitlistEntry.COLUMN_PARTY_SIZE, number);

        sqLiteDatabase.insert(WaitlistEntry.TABLE_NAME,
                null,
                contentValues);
    }

    private Cursor getAllGuests() {
        return sqLiteDatabase.query(
                WaitlistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistEntry.COLUMN_TIMESTAMP
        );
    }

    private void setListImageVisabilty() {
        if (cursor.getCount() != 0) {
            placeholderList.setVisibility(View.GONE);
        } else {
            placeholderList.setVisibility(View.VISIBLE);
        }
    }

    public void createItemTouchHelper(){
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                long id = (long) viewHolder.itemView.getTag();

                removeGuest(id);

                guestAdapter.swapCursor(getAllGuests());

                if (getAllGuests().getCount() == 0){
                    placeholderList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                if (viewHolder != null) {
                    final View foregroundView = ((GuestAdapter.GuestViewHolder) viewHolder).viewForeground;
                    getDefaultUIUtil().onSelected(foregroundView);
                }

            }
            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                final View foregroundView = ((GuestAdapter.GuestViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().clearView(foregroundView);
            }
            @Override
            public int convertToAbsoluteDirection(int flags, int layoutDirection) {
                return super.convertToAbsoluteDirection(flags, layoutDirection);
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((GuestAdapter.GuestViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }
            @Override
            public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                final View foregroundView = ((GuestAdapter.GuestViewHolder) viewHolder).viewForeground;
                getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

        }).attachToRecyclerView(recyclerView);
    }

    private boolean removeGuest(long id) {
        return sqLiteDatabase.delete(WaitlistEntry.TABLE_NAME,
                WaitlistEntry._ID+" = "+id,
                null) > 0;
    }
}
