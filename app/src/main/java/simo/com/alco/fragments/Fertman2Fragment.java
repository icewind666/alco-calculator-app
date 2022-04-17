package simo.com.alco.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import simo.com.alco.R;
import simo.com.alco.fragments.scrolltable.ScrollableTableLayout;

/**
 * Fertman table fragment.
 * Simple fragment without logic.
 * Created by icewind on 29.09.2017.
 */

public class Fertman2Fragment extends FragmentWithInit {

    /**
     * Need this constructor to conform with android fragment api
     * to create empty fragment
     */


    public Fertman2Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView tx = new TextView(getContext());

        View v = inflater.inflate(R.layout.fertman2_fragment, container, false);

        int px_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 380, getResources().getDisplayMetrics());
        int px_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());

        ViewGroup.LayoutParams lParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, px_height);
        ScrollableTableLayout scrollableTableLayout = new ScrollableTableLayout(getContext());

        v.setLayoutParams(lParams);
        linearLayout.addView(v);

        TableLayout tableBeforeMain = new TableLayout(getContext());
        TableRow row = new TableRow(getContext());
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        row.setBackgroundColor(getResources().getColor(R.color.fertman2TextView));

        TextView firstTx = new TextView(getContext());
        firstTx.setBackgroundColor(getResources().getColor(R.color.fertman2TextView));
        firstTx.setTextColor(getResources().getColor(R.color.fertman2TextViewBg));
        firstTx.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
        firstTx.setGravity(Gravity.CENTER);

        TextView secondTx = new TextView(getContext());
        secondTx.setBackgroundColor(getResources().getColor(R.color.fertman2TextView));
        secondTx.setTextColor(getResources().getColor(R.color.fertman2TextViewBg));
        secondTx.setTextSize(TypedValue.COMPLEX_UNIT_SP, 7);
        secondTx.setGravity(Gravity.CENTER );

        firstTx.setText(R.string.tableHeaderFertman2);
        secondTx.setText(R.string.desiredProofTextFertman2);

        firstTx.setWidth(scrollableTableLayout.getCellWidths()[0]);
        secondTx.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        secondTx.setWidth(getResources().getDisplayMetrics().widthPixels - scrollableTableLayout.getCellWidths()[0]);
        row.addView(firstTx);
        row.addView(secondTx);

        tableBeforeMain.addView(row);

        linearLayout.addView(tableBeforeMain);
        linearLayout.addView(scrollableTableLayout);

        return linearLayout;
    }

    @Override
    public void onClick(View view) {

    }
}
