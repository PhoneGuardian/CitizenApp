package edu.elfak.mosis.phoneguardian;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Mirjam on 19/04/2015.
 */

public class FilterTypeDialog {
    final CharSequence[] items = {"Fire","Police","Emergency"};

    final int TAG_FIRE = 0;
    final int TAG_POLICE = 1;
    final int TAG_EMERGENCY = 2;
    boolean[] checkedValues = new boolean[3];
    TextView filterResult;

    AlertDialog.Builder builder;

    public FilterTypeDialog(Context ctx, final TextView filterResult) {
        this.filterResult = filterResult;
        builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Filter by type:");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterResult.setText(getChoices());
            }
        });
    }

    private DialogInterface.OnMultiChoiceClickListener mDialogInterfaceListener =  new DialogInterface.OnMultiChoiceClickListener(){
        @Override
        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
            checkedValues[indexSelected] = isChecked;
        }
    };

    public void show(){
        builder.setMultiChoiceItems(items, checkedValues, mDialogInterfaceListener);
        builder.show();
    }
    public boolean isFireChecked (){ return checkedValues[TAG_FIRE]; }
    public boolean isPoliceChecked (){ return checkedValues[TAG_POLICE]; }
    public boolean isEmergencyChecked (){ return checkedValues[TAG_EMERGENCY]; }


    public String getChoices (){
        String filterBy = "Filter by: ";
        if (isFireChecked()) filterBy += "F ";
        if (isPoliceChecked()) filterBy += "P ";
        if (isEmergencyChecked()) filterBy += "E ";
        if(!isFireChecked() && !isPoliceChecked() && !isEmergencyChecked())  filterBy = "Any";
        return filterBy ;
    }
}