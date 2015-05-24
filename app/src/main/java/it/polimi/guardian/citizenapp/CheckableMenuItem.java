package it.polimi.guardian.citizenapp;

import android.view.MenuItem;

/**
 * Created by Mirjam on 15/04/2015.
 */
public class CheckableMenuItem {
    MenuItem item;
    boolean isChecked;
    int checkedDrawable;
    int uncheckedDrawable;

    public CheckableMenuItem(MenuItem item, boolean isChecked, int checkedDrawable, int uncheckedDrawable) {
        this.item = item;
        this.isChecked = isChecked;
        this.checkedDrawable = checkedDrawable;
        this.uncheckedDrawable = uncheckedDrawable;
    }
    public void toggle() {
        isChecked = !isChecked;
        updateMenuItem();
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        updateMenuItem();
    }

    private void updateMenuItem() {
        item.setChecked(isChecked);
        item.setIcon( isChecked ? checkedDrawable : uncheckedDrawable);
    }
    public MenuItem getItem() {
        return item;
    }

    public boolean isChecked() {
        return isChecked;
    }

}
