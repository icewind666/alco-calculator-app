package simo.com.alco.fragments.scrolltable;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import simo.com.alco.R;

/**
 *
 * Created by icewind on 03.10.2017.
 */


public class TableMainLayout extends RelativeLayout {
    public final String TAG = "TableMainLayout";
    int CELL_HEIGHT_DP = 50; // cell height in DP for scrollable table
    int CELL_WIDTH_DP = 50;  // cell width in DP for scrollable table
    int TEXT_SIZE_SP = 25;   // default text size

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

    // set the header titles
    String headers[] = {"     xx xx xx      ","90","85","80","75","70","65","60","55","50","45","40","35","30"};

    TableLayout tableUpperLeftCorner;
    TableLayout tableTopHeaderLine;
    TableLayout tableLeftColumn;
    TableLayout tableMainContent;

    HorizontalScrollView horizontalScrollViewHeaderLine;
    HorizontalScrollView horizontalScrollViewMainContent;

    ScrollView scrollViewLeftColumn;
    ScrollView scrollViewMainContent;

    Context context;

    List<TableDataObject> tableDataObjects = this.generateTableObjects();
    int headerCellsWidth[] = new int[headers.length];


    /**
     *
     * @param context
     */
    public TableMainLayout(Context context) {
        super(context);
        this.context = context;

        // initialize the main components (TableLayouts, HorizontalScrollView, ScrollView)
        initComponents();
        setComponentsId();
        setScrollViewAndHorizontalScrollViewTag();

        // no need to assemble component A, since it is just a table
        horizontalScrollViewHeaderLine.addView(this.tableTopHeaderLine);
        scrollViewLeftColumn.addView(this.tableLeftColumn);

        scrollViewMainContent.addView(this.horizontalScrollViewMainContent);
        horizontalScrollViewMainContent.addView(this.tableMainContent);

        // add the components to be part of the main layout
        addComponentToMainLayout();

        // add some table rows
        addTableRowToTableA();
        addTableRowToTableB();
        resizeHeaderHeight();
        getTableRowHeaderCellWidth();
        generateTableC_AndTable_B();
    }


    /**
     * Returns list of filled table data objects ready for inserting to table
     *
     * @return
     */
    List<TableDataObject> generateTableObjects(){
        List<TableDataObject> sampleObjects = new ArrayList<>();

        for (String[] intersection : intersections) {
            TableDataObject sampleObject = new TableDataObject(intersection);
            sampleObjects.add(sampleObject);
        }

        return sampleObjects;

    }

    /**
     *
     */
    private void initComponents(){

        this.tableUpperLeftCorner = new TableLayout(this.context);
        this.tableTopHeaderLine = new TableLayout(this.context);

        this.tableLeftColumn = new TableLayout(this.context);
        this.tableMainContent = new TableLayout(this.context);

        this.horizontalScrollViewHeaderLine = new ExtendedHorizontalScrollView(this.context);
        this.horizontalScrollViewMainContent = new ExtendedHorizontalScrollView(this.context);

        this.scrollViewLeftColumn = new ExtendedScrollView(this.context);
        this.scrollViewMainContent = new ExtendedScrollView(this.context);

        //this.tableUpperLeftCorner.setBackgroundColor(Color.GREEN);
        //this.horizontalScrollViewHeaderLine.setBackgroundColor(Color.LTGRAY);

    }



    /**
     * set essential component IDs
     */
    private void setComponentsId(){
        this.tableUpperLeftCorner.setId(R.id.aId);
        this.horizontalScrollViewHeaderLine.setId(R.id.bId);
        this.scrollViewLeftColumn.setId(R.id.cId);
        this.scrollViewMainContent.setId(R.id.dId);
    }


    /**
     * set tags for some horizontal and vertical scroll view
     */
    private void setScrollViewAndHorizontalScrollViewTag(){

        this.horizontalScrollViewHeaderLine.setTag("horizontal scroll view b");
        this.horizontalScrollViewMainContent.setTag("horizontal scroll view d");

        this.scrollViewLeftColumn.setTag("scroll view c");
        this.scrollViewMainContent.setTag("scroll view d");
    }

    /**
     * we add the components here in our TableMainLayout
     */
    private void addComponentToMainLayout(){

        // RelativeLayout params were very useful here
        // the addRule method is the key to arrange the components properly
        RelativeLayout.LayoutParams componentB_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentB_Params.addRule(RelativeLayout.RIGHT_OF, this.tableUpperLeftCorner.getId());

        RelativeLayout.LayoutParams componentC_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentC_Params.addRule(RelativeLayout.BELOW, this.tableUpperLeftCorner.getId());

        RelativeLayout.LayoutParams componentD_Params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.scrollViewLeftColumn.getId());
        componentD_Params.addRule(RelativeLayout.BELOW, this.horizontalScrollViewHeaderLine.getId());

        // 'this' is a relative layout,
        // we extend this table layout as relative layout as seen during the creation of this class
        this.addView(this.tableUpperLeftCorner);
        this.addView(this.horizontalScrollViewHeaderLine, componentB_Params);
        this.addView(this.scrollViewLeftColumn, componentC_Params);
        this.addView(this.scrollViewMainContent, componentD_Params);

    }


    /**
     *
     */
    private void addTableRowToTableA(){
        this.tableUpperLeftCorner.addView(this.componentATableRow());
    }

    /**
     *
     */
    private void addTableRowToTableB(){
        this.tableTopHeaderLine.addView(this.componentBTableRow());
    }

    // generate table row of table A
    TableRow componentATableRow(){
        int px_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_WIDTH_DP, getResources().getDisplayMetrics());
        int px_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_HEIGHT_DP, getResources().getDisplayMetrics());

        TableRow componentATableRow = new TableRow(this.context);
        ViewGroup.LayoutParams lParams = new ViewGroup.LayoutParams(px_width, px_height);

        TextView textView = this.getHeaderTextView(this.headers[0]);
        textView.setLayoutParams(lParams);
        //textView.setText("xxx");
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        componentATableRow.addView(textView);

        return componentATableRow;
    }

    /**
     * generate table row of table B
     * @return
     */
    TableRow componentBTableRow() {
        TableRow componentBTableRow = new TableRow(this.context);
        int headerFieldCount = this.headers.length;

        int px_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_WIDTH_DP, getResources().getDisplayMetrics());
        int px_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_HEIGHT_DP, getResources().getDisplayMetrics());
        TableRow.LayoutParams params = new TableRow.LayoutParams(px_width, px_height);

        componentBTableRow.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));

        for(int x = 1; x < (headerFieldCount - 1); x++){
            TextView textView = this.getHeaderTextView(this.headers[x]);
            textView.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));
            textView.setLayoutParams(params);
            componentBTableRow.addView(textView);
        }

        return componentBTableRow;
    }

    /**
     * generate table row of table C and table D
     */
    private void generateTableC_AndTable_B(){

        for(TableDataObject sampleObject : this.tableDataObjects){
            TableRow tableRowForTableC = this.getLeftColumnTableRow(sampleObject);
            TableRow taleRowForTableD = this.getMainContentTableRow(sampleObject);

            tableRowForTableC.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));

            taleRowForTableD.setBackgroundColor(Color.BLACK);

            this.tableLeftColumn.addView(tableRowForTableC);
            this.tableMainContent.addView(taleRowForTableD);

        }
    }

    // a TableRow for table C
    TableRow getLeftColumnTableRow(TableDataObject dataObject){
        TableRow leftColumnTableRow = new TableRow(this.context);
        leftColumnTableRow.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));
        int px_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_WIDTH_DP, getResources().getDisplayMetrics());
        int px_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_HEIGHT_DP, getResources().getDisplayMetrics());

        TableRow.LayoutParams params = new TableRow.LayoutParams(px_width, px_height);

        TextView textView = this.getBodyTextView(dataObject.header1);
        //textView.setLayoutParams(params);
        textView.setBackgroundColor(getResources().getColor(R.color.fertman2LightCyanBg));

        leftColumnTableRow.addView(textView, params);

        return leftColumnTableRow;
    }

    TableRow getMainContentTableRow(TableDataObject sampleObject) {
        TableRow mainTableRow = new TableRow(this.context);
        int loopCount = ((TableRow)this.tableTopHeaderLine.getChildAt(0)).getChildCount();

        String info[] = {
                sampleObject.header2,
                sampleObject.header3,
                sampleObject.header4,
                sampleObject.header5,
                sampleObject.header6,
                sampleObject.header7,
                sampleObject.header8,
                sampleObject.header9,
                sampleObject.header10,
                sampleObject.header11,
                sampleObject.header12,
                sampleObject.header13,
                sampleObject.header14
        };

        int px_width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_WIDTH_DP, getResources().getDisplayMetrics());
        int px_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CELL_HEIGHT_DP, getResources().getDisplayMetrics());

        for(int x = 0 ; x < loopCount; x++) {
            TableRow.LayoutParams params = new TableRow.LayoutParams(px_width, px_height);
            TextView textViewB = getBodyTextView(info[x]);
            mainTableRow.addView(textViewB,params);
        }
        return mainTableRow;

    }

    // table cell standard TextView
    TextView getBodyTextView(String label) {
        TextView bodyTextView = new TextView(this.context);
        bodyTextView.setBackgroundColor(Color.WHITE);
        bodyTextView.setText(label);
        //bodyTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        bodyTextView.setGravity(Gravity.CENTER);
        bodyTextView.setPadding(0, 0, 0, 0);

        return bodyTextView;
    }

    // header standard TextView
    TextView getHeaderTextView(String label){

        TextView headerTextView = new TextView(this.context);
        headerTextView.setBackgroundColor(Color.WHITE);
        headerTextView.setText(label);
        //headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setPadding(0, 0, 0, 0);

        return headerTextView;
    }


    void resizeHeaderHeight() {

        TableRow productNameHeaderTableRow = (TableRow) this.tableUpperLeftCorner.getChildAt(0);
        TableRow productInfoTableRow = (TableRow)  this.tableTopHeaderLine.getChildAt(0);

        int rowAHeight = this.viewHeight(productNameHeaderTableRow);
        int rowBHeight = this.viewHeight(productInfoTableRow);

        TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
        int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

        this.matchLayoutHeight(tableRow, finalHeight);
    }

    void getTableRowHeaderCellWidth(){

        int tableAChildCount = ((TableRow)this.tableUpperLeftCorner.getChildAt(0)).getChildCount();
        int tableBChildCount = ((TableRow)this.tableTopHeaderLine.getChildAt(0)).getChildCount();;

        for(int x=0; x<(tableAChildCount+tableBChildCount); x++){

            if(x==0){
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableUpperLeftCorner.getChildAt(0)).getChildAt(x));
            }else{
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableTopHeaderLine.getChildAt(0)).getChildAt(x-1));
            }

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


    /**
     * Custom horizontal scroll view
     */
    class ExtendedHorizontalScrollView extends HorizontalScrollView{

        public ExtendedHorizontalScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {
            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("horizontal scroll view b")){
                horizontalScrollViewMainContent.scrollTo(l, 0);
            }
            else {
                horizontalScrollViewHeaderLine.scrollTo(l, 0);
            }
        }

    }

    /**
     * Custom vertical scroll view
     */
    class ExtendedScrollView extends ScrollView {

        public ExtendedScrollView(Context context) {
            super(context);
        }

        @Override
        protected void onScrollChanged(int l, int t, int oldl, int oldt) {

            String tag = (String) this.getTag();

            if(tag.equalsIgnoreCase("scroll view c")) {
                scrollViewMainContent.scrollTo(0, t);
            } else {
                scrollViewLeftColumn.scrollTo(0,t);
            }
        }
    }


}