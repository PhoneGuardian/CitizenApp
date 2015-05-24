package it.polimi.guardian.citizenapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Mirjam on 19/04/2015.
 */

public class FilterDateDialog {
    String description = "";

    Date dt_begin;
    Date dt_end;
    Button btn_fromDate;
    Button btn_toDate;

    boolean filterByFromDate = false;
    boolean filterByToDate = false;

    TextView filterResult;
    LayoutInflater inflater;
    View filterByDateLayout;

    DatePickerDialog fromDatePickerDialog;
    DatePickerDialog toDatePickerDialog;

    Context ctx;
    AlertDialog.Builder builder;

    public FilterDateDialog(Context ctx, final TextView filterResult) {
        this.ctx = ctx;
        this.filterResult = filterResult;

        inflater = (LayoutInflater) ctx.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        filterByDateLayout = inflater.inflate(R.layout.filter_by_date, null);

        builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Filter by Date:");
        builder.setView(filterByDateLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 filterResult.setText(getDescription().length() == 0 ? "Any" : getDescription() );
            }
        });
        builder.create();

    }

    public void show(){
        filterByFromDate = false;
        filterByToDate = false;

        filterByDateLayout = inflater.inflate(R.layout.filter_by_date, null);
        builder.setView(filterByDateLayout);

        initializeLayoutViews();

        builder.show();
    }

    private void initializeLayoutViews() {
        btn_fromDate = (Button) filterByDateLayout.findViewById(R.id.btn_filter_from_date1);
        btn_fromDate.setOnClickListener(mOnClickListener);
        btn_toDate = (Button) filterByDateLayout.findViewById(R.id.btn_filter_to_date1);
        btn_toDate.setOnClickListener(mOnClickListener);
        filterByDateLayout.findViewById(R.id.btn_filter_clear_from_date1).setOnClickListener(mOnClickListener);
        filterByDateLayout.findViewById(R.id.btn_filter_clear_to_date1).setOnClickListener(mOnClickListener);

        Calendar c = Calendar.getInstance();
        int cYear = c.get(Calendar.YEAR);
        int cMonth = c.get(Calendar.MONTH);
        int cDay = c.get(Calendar.DAY_OF_MONTH);

        fromDatePickerDialog = new DatePickerDialog(builder.getContext(), fromDateSetListener, cYear, cMonth, cDay);
        toDatePickerDialog = new DatePickerDialog(builder.getContext(), toDateSetListener, cYear, cMonth, cDay);
    }

    public String getDescription (){
        description = "";
        description += filterByFromDate ? btn_fromDate.getText() : "Any" ;
        description +=  " - ";
        description += filterByToDate ? btn_toDate.getText() : "Any" ;

        return description ;
    }

    private DatePickerDialog.OnDateSetListener  fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dt_begin = new Date(view.getCalendarView().getDate());
            filterByFromDate = true;
            btn_fromDate.setText(dateFormat.format(view.getCalendarView().getDate()));

        }
    };
    private DatePickerDialog.OnDateSetListener  toDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dt_end = new Date(view.getCalendarView().getDate());
            filterByToDate = true;
            btn_toDate.setText(dateFormat.format(view.getCalendarView().getDate()));
        }
    };


    private View.OnClickListener  mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_filter_from_date1:
                    fromDatePickerDialog.show();
                    break;
                case R.id.btn_filter_to_date1:
                    toDatePickerDialog.show();
                    break;
                case R.id.btn_filter_clear_from_date1:
                    filterByFromDate = false;
                    btn_fromDate.setText("");
                    break;
                case R.id.btn_filter_clear_to_date1:
                    filterByToDate = false;
                    btn_toDate.setText("");
                    break;
            }
        }
    };

    public boolean isFilteringByFromDate() {
        return filterByFromDate;
    }

    public boolean isFilteringByToDate() {
        return filterByToDate;
    }

    public Date getToDate() {
        return dt_end;
    }

    public Date getFromDate() {
        return dt_begin;
    }

}