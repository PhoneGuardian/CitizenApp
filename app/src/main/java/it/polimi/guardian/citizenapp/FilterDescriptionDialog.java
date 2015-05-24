package it.polimi.guardian.citizenapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Mirjam on 19/04/2015.
 */

public class FilterDescriptionDialog {
    String description = "";
    TextView filterResult;
    EditText input;
Context ctx;
    AlertDialog.Builder builder;

    public FilterDescriptionDialog(Context ctx, final TextView filterResult) {
        this.ctx = ctx;
        this.filterResult = filterResult;
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Filter by Description:");
        input = new EditText(ctx);
        input.setHint("description");
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                description = input.getText().toString();
                filterResult.setText(description.length() == 0 ? "Any" : description);
            }
        });
        builder.create();

    }

    public void show(){
        input = new EditText(ctx);
        input.setHint("description");
        builder.setView(input);
        builder.show();
    }

    public String getDescription (){
        return description ;
    }
}