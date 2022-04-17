package simo.com.alco.fragments.scrolltable;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import simo.com.alco.R;

public class ScrollableTableLayout extends RelativeLayout {
    int CELL_HEIGHT_DP = 50; // cell height in DP for scrollable table
    int CELL_WIDTH_DP = 50;  // cell width in DP for scrollable table
    // int TEXT_SIZE_SP = 20;

    String intersections[][] = {
            {"95","64","133","209","295","391","501","629","779","957","1174","1443","1785","2239"},
            {"90","","65","138","218","310","414","435","677","847","1052","1306","1630","2061"},
            {"85","","","68","144","231","329","443","578","738","932","1172","1478","1884"},
            {"80","","","","72","153","246","353","480","630","812","1039","1327","1709"},
            {"75","","","","","76","163","264","382","523","694","906","1177","1535"},
            {"70","","","","","","81","175","285","417","577","774","1027","1360"},
            {"65","","","","","","","88","190","311","460","644","878","1189"},
            {"60","","","","","","","","95","207","344","514","730","1017"},
            {"55","","","","","","","","","103","229","384","583","845"},
            {"50","","","","","","","","","", "114","255","436","674"},
            {"45","","","","","","","","","","","127","290","505"},
            {"40","","","","","","","","","","","","144","335"},
            {"35","","","","","","","","","","","","","167"},

    };

    // 14
    String headers[] = {"           ","90","85","80","75","70","65","60","55","50","45","40","35","30"};

    TableLayout tableA;
    TableLayout tableB;
    TableLayout tableC;
    TableLayout tableD;

    HorizontalScrollView horizontalScrollViewB;
    HorizontalScrollView horizontalScrollViewD;

    ScrollView scrollViewC;
    ScrollView scrollViewD;

    Context context;

    List<TableDataObject> sampleObjects = this.getDataObjects();

    protected int headerCellsWidth[] = new int[headers.length];

    int PX_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_WIDTH_DP, getResources().getDisplayMetrics());
    int PX_HEIGHT = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_HEIGHT_DP, getResources().getDisplayMetrics());


    public ScrollableTableLayout(Context context) {
        super(context);
        this.context = context;

        // initialize the main components (TableLayouts, HorizontalScrollView, ScrollView)
        this.initComponents();
        this.setComponentsId();
        this.setScrollViewAndHorizontalScrollViewTag();

        // no need to assemble component A, since it is just a table
        this.horizontalScrollViewB.addView(this.tableB);
        this.scrollViewC.addView(this.tableC);


        this.scrollViewD.addView(this.horizontalScrollViewD);
        this.horizontalScrollViewD.addView(this.tableD);

        // add the components to be part of the main layout
        this.addComponentToMainLayout();
        //this.setBackgroundColor(Color.RED);

        // add some table rows
        this.addTableRowToTableA();
        this.addTableRowToTableB();

        this.resizeHeaderHeight();
        this.getTableRowHeaderCellWidth();
        this.generateTableC_AndTable_B();
        this.resizeBodyTableRowHeight();

        this.scrollViewC.setVerticalScrollBarEnabled(false);
        this.scrollViewD.setVerticalScrollBarEnabled(false);
        this.scrollViewC.setHorizontalScrollBarEnabled(false);
        this.scrollViewD.setHorizontalScrollBarEnabled(false);

        this.horizontalScrollViewB.setVerticalScrollBarEnabled(false);
        this.horizontalScrollViewD.setVerticalScrollBarEnabled(false);
        this.horizontalScrollViewB.setHorizontalScrollBarEnabled(false);
        this.horizontalScrollViewD.setHorizontalScrollBarEnabled(false);


    }

    public int[] getCellWidths() {
        return headerCellsWidth;
    }

    /**
     *
     * @return
     */
    List<TableDataObject> getDataObjects(){
        List<TableDataObject> tableDataObjects = new ArrayList<>();
        for (String[] intersection : intersections) {
            TableDataObject sampleObject = new TableDataObject(intersection);
            tableDataObjects.add(sampleObject);
        }
        return tableDataObjects;
    }

    /**
     * Initializing main components and layout
     */
    private void initComponents(){

        this.tableA = new TableLayout(this.context);
        this.tableB = new TableLayout(this.context);
        this.tableC = new TableLayout(this.context);
        this.tableD = new TableLayout(this.context);

        tableA.setBackgroundColor(getResources().getColor(R.color.fertman2TableLayoutBg));
        tableB.setBackgroundColor(getResources().getColor(R.color.fertman2TableLayoutBg));
        tableC.setBackgroundColor(getResources().getColor(R.color.fertman2TableLayoutBg));
        tableD.setBackgroundColor(getResources().getColor(R.color.fertman2TableLayoutBg));

        this.horizontalScrollViewB = new MyHorizontalScrollView(this.context);
        this.horizontalScrollViewD = new MyHorizontalScrollView(this.context);

        this.scrollViewC = new MyScrollView(this.context);
        this.scrollViewD = new MyScrollView(this.context);
        this.horizontalScrollViewB.setBackgroundColor(Color.LTGRAY);

    }

    /**
     *
     */
    private void setComponentsId(){
        this.tableA.setId(R.id.aId);
        this.horizontalScrollViewB.setId(R.id.bId);
        this.scrollViewC.setId(R.id.cId);
        this.scrollViewD.setId(R.id.dId);
    }

    /**
     *
     */
    private void setScrollViewAndHorizontalScrollViewTag(){
        this.horizontalScrollViewB.setTag("horizontal scroll view b");
        this.horizontalScrollViewD.setTag("horizontal scroll view d");
        this.scrollViewC.setTag("scroll view c");
        this.scrollViewD.setTag("scroll view d");
    }

    /**
     *
     */
    private void addComponentToMainLayout(){
        RelativeLayout.LayoutParams componentB_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentB_Params.addRule(RelativeLayout.RIGHT_OF, this.tableA.getId());

        RelativeLayout.LayoutParams componentC_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentC_Params.addRule(RelativeLayout.BELOW, this.tableA.getId());

        RelativeLayout.LayoutParams componentD_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.scrollViewC.getId());
        componentD_Params.addRule(RelativeLayout.BELOW, this.horizontalScrollViewB.getId());

        this.addView(this.tableA);
        this.addView(this.horizontalScrollViewB, componentB_Params);
        this.addView(this.scrollViewC, componentC_Params);
        this.addView(this.scrollViewD, componentD_Params);
    }


    /**
     *
     */
    private void addTableRowToTableA(){
        this.tableA.addView(this.componentATableRow());
    }

    /**
     *
     */
    private void addTableRowToTableB(){
        this.tableB.addView(this.componentTopHeaderTableRow());
    }

    /**
     *
     * @return
     */
    TableRow componentATableRow() {
        TableRow componentATableRow = new TableRow(this.context);
        TextView textView = this.headerTextView(this.headers[0]);
        textView.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));
        componentATableRow.addView(textView);
        return componentATableRow;
    }

    /**
     *
     * @return
     */
    TableRow componentTopHeaderTableRow(){
        TableRow componentBTableRow = new TableRow(this.context);
        TableRow.LayoutParams params = new TableRow.LayoutParams(PX_WIDTH, PX_HEIGHT);
        params.setMargins(1, 1, 1, 1);

        for(int x = 1; x < this.headers.length; x++){
            TextView textView = this.headerTextView(this.headers[x]);
            textView.setLayoutParams(params);
            textView.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));
            componentBTableRow.addView(textView);
        }

        return componentBTableRow;
    }

    /**
     *
     */
    private void generateTableC_AndTable_B(){
        for(TableDataObject sampleObject : this.sampleObjects){
            TableRow tableRowForTableC = this.tableRowForTableC(sampleObject);
            TableRow taleRowForTableD = this.tableRowForTableD(sampleObject);
            this.tableC.addView(tableRowForTableC);
            this.tableD.addView(taleRowForTableD);
        }
    }

    /**
     * Returns row for left column header
     * @param sampleObject - data table
     * @return
     */
    TableRow tableRowForTableC(TableDataObject sampleObject){
        TableRow.LayoutParams params = new TableRow.LayoutParams( this.headerCellsWidth[0],LayoutParams.MATCH_PARENT);
        TableRow tableRowForTableC = new TableRow(this.context);
        TextView textView = this.bodyTextView(sampleObject.header1);
        params.setMargins(1, 1, 1, 1);
        textView.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));
        tableRowForTableC.addView(textView,params);
        return tableRowForTableC;
    }

    /**
     * Returns row for the central table
     * @param sampleObject - data object
     * @return
     */
    TableRow tableRowForTableD(TableDataObject sampleObject){
        TableRow taleRowForTableD = new TableRow(this.context);
        String info[] = sampleObject.getRaw();

        for(int x = 1 ; x < headers.length; x++) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(PX_WIDTH, PX_HEIGHT);
            params.setMargins(1, 1, 1, 1);
            TextView textViewB = this.bodyTextView(info[x]);
            textViewB.setTextColor(getResources().getColor(R.color.fertman2TextViewBg));
            textViewB.setBackgroundColor(getResources().getColor(R.color.fertman2TextView));
            taleRowForTableD.addView(textViewB,params);
        }

        return taleRowForTableD;

    }

    // table cell standard TextView
    TextView bodyTextView(String label){

        TextView bodyTextView = new TextView(this.context);
        //bodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        bodyTextView.setText(label);
        bodyTextView.setGravity(Gravity.CENTER);

        return bodyTextView;
    }

    // header standard TextView
    TextView headerTextView(String label){
        TextView headerTextView = new TextView(this.context);
        //headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        headerTextView.setText(label);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setPadding(5, 5, 5, 5);

        return headerTextView;
    }

    // resizing TableRow height starts here
    void resizeHeaderHeight() {

        TableRow productNameHeaderTableRow = (TableRow) this.tableA.getChildAt(0);
        TableRow productInfoTableRow = (TableRow)  this.tableB.getChildAt(0);

        int rowAHeight = this.viewHeight(productNameHeaderTableRow);
        int rowBHeight = this.viewHeight(productInfoTableRow);

        TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
        int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

        this.matchLayoutHeight(tableRow, finalHeight);
    }

    void getTableRowHeaderCellWidth(){

        int tableAChildCount = ((TableRow)this.tableA.getChildAt(0)).getChildCount();
        int tableBChildCount = ((TableRow)this.tableB.getChildAt(0)).getChildCount();

        for(int x=0; x<(tableAChildCount+tableBChildCount); x++){

            if(x == 0){
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableA.getChildAt(0)).getChildAt(x));
            }else{
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableB.getChildAt(0)).getChildAt(x-1));
            }

        }
    }

    // resize body table row height
    void resizeBodyTableRowHeight(){

        int tableC_ChildCount = this.tableC.getChildCount();

        for(int x=0; x<tableC_ChildCount; x++){

            TableRow productNameHeaderTableRow = (TableRow) this.tableC.getChildAt(x);
            TableRow productInfoTableRow = (TableRow)  this.tableD.getChildAt(x);

            int rowAHeight = this.viewHeight(productNameHeaderTableRow);
            int rowBHeight = this.viewHeight(productInfoTableRow);

            TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
            int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

            this.matchLayoutHeight(tableRow, finalHeight);
        }

    }

    // match all height in a table row
    // to make a standard TableRow height
    private void matchLayoutHeight(TableRow tableRow, int height) {

        int tableRowChildCount = tableRow.getChildCount();

        // if a TableRow has only 1 child
        if(tableRow.getChildCount()==1){

            View view = tableRow.getChildAt(0);
            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
            params.height = height - (params.bottomMargin + params.topMargin);

            return ;
        }

        // if a TableRow has more than 1 child
        for (int x = 0; x < tableRowChildCount; x++) {

            View view = tableRow.getChildAt(x);

            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();

            if (!isTheHeighestLayout(tableRow, x)) {
                params.height = height - (params.bottomMargin + params.topMargin);
                return;
            }
        }

    }

    // check if the view has the highest height in a TableRow
    private boolean isTheHeighestLayout(TableRow tableRow, int layoutPosition) {

        int tableRowChildCount = tableRow.getChildCount();
        int heighestViewPosition = -1;
        int viewHeight = 0;

        for (int x = 0; x < tableRowChildCount; x++) {
            View view = tableRow.getChildAt(x);
            int height = this.viewHeight(view);

            if (viewHeight < height) {
                heighestViewPosition = x;
                viewHeight = height;
            }
        }

        return heighestViewPosition == layoutPosition;
    }

    // read a view's height
    private int viewHeight(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    // read a view's width
    private int viewWidth(View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();
    }

    // horizontal scroll view custom class
    class MyHorizontalScrollView extends HorizontalScrollView{

        public MyHorizontalScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("horizontal scroll view b")){
                horizontalScrollViewD.scrollTo(l, 0);
            }else{
                horizontalScrollViewB.scrollTo(l, 0);
            }
        }

    }

    // scroll view custom class
    class MyScrollView extends ScrollView{

        public MyScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {

            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("scroll view c")){
                scrollViewD.scrollTo(0, t);
            }else{
                scrollViewC.scrollTo(0,t);
            }
        }
    }


}