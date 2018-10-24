package com.arahasya.sap.forwardkey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.onegravity.contactpicker.contact.Contact;
import com.onegravity.contactpicker.contact.ContactDescription;
import com.onegravity.contactpicker.contact.ContactSortOrder;
import com.onegravity.contactpicker.core.ContactPickerActivity;
import com.onegravity.contactpicker.picture.ContactPictureType;

import java.util.List;
import java.util.Objects;

public class RecordActivity extends AppCompatActivity {

    TextView title, ok;
    String keyword;
    SQLiteAdapter sqLiteAdapter;
    SharedPreferences sharedPreferences;
    String string;
    Gson gson;

    TextInputEditText editText;


    private static final int REQUEST_CONTACT = 1618;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        sqLiteAdapter = new SQLiteAdapter(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editText = new TextInputEditText(this);
        ok = findViewById(R.id.ok_keyword);
        title = findViewById(R.id.title);

        ok = findViewById(R.id.ok_keyword);
        editText = findViewById(R.id.enter_keyword);
        gson = new Gson();
        insertKeyword();


    }

    public void insertKeyword() {


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = Objects.requireNonNull(editText.getText()).toString();
                if (keyword.isEmpty()) {
                    keyword = "Forward=all+";
                } else {

                    keyword = keyword.trim();
                }
                contactPicker();
            }
        });


    }


    public void contactPicker() {
        Intent intent = new Intent(this, ContactPickerActivity.class)
                .putExtra(ContactPickerActivity.EXTRA_THEME, R.style.ContactPicker_Theme_Light)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_BADGE_TYPE, ContactPictureType.ROUND.name())
                .putExtra(ContactPickerActivity.EXTRA_SHOW_CHECK_ALL, false)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION, ContactDescription.PHONE.name())
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_DESCRIPTION_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactPickerActivity.EXTRA_CONTACT_SORT_ORDER, ContactSortOrder.AUTOMATIC.name())
                .putExtra(ContactPickerActivity.EXTRA_SELECT_CONTACTS_LIMIT, 6)
                .putExtra(ContactPickerActivity.EXTRA_LIMIT_REACHED_MESSAGE, "You can select up to 6 contacts only!")
                .putExtra(ContactPickerActivity.EXTRA_ONLY_CONTACTS_WITH_PHONE, true);


        startActivityForResult(intent, REQUEST_CONTACT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CONTACT && resultCode == Activity.RESULT_OK &&
                data != null && data.hasExtra(ContactPickerActivity.RESULT_CONTACT_DATA)) {

            // we got a result from the contact picker
            List<Contact> contacts = (List<Contact>) data.getSerializableExtra(ContactPickerActivity.RESULT_CONTACT_DATA);
            // process contacts
            int count = contacts.size();

            String names[] = new String[count];
            String numbers[] = new String[count];

            for (int i = 0; i < count; i++) {
                names[i] = contacts.get(i).getFirstName();

                numbers[i] = contacts.get(i).getPhone(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            }
            String input_names = gson.toJson(names);
            String removerNames = input_names.replaceAll("[\\[\\]\"]", "").replaceAll(",", ", ");

            String input_numbers = gson.toJson(numbers);
            String removerNumbers = input_numbers.replaceAll("[\\[\\]\"]", "");

            boolean ins = sqLiteAdapter.insert(sqLiteAdapter, keyword, removerNames, removerNumbers, count);
            if (ins) {
                Toast.makeText(this, "Record created!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to insert please try again!", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(RecordActivity.this, MainActivity.class);
            startActivity(intent);

        }
    }

}
