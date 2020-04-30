package com.example.manage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Slide;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.manage.activity.BigScreenActivity;
import com.example.manage.activity.EquipmentListActivity;
import com.example.manage.activity.LightingActivity;
import com.example.manage.activity.LoginActivity;
import com.example.manage.activity.MonitorListActivity;
import com.example.manage.activity.NetActivity;
import com.example.manage.activity.SearchActivity;
import com.example.manage.activity.SenFogActivity;
import com.example.manage.activity.SettingActivity;
import com.example.manage.adapter.MainEquAdapter;
import com.example.manage.adapter.MainSecneAdapter;
import com.example.manage.app.AppUrl;
import com.example.manage.bean.ApStatusBean;
import com.example.manage.bean.ClientNumsBean;
import com.example.manage.bean.EnvironmentBean;
import com.example.manage.bean.MainEquBean;
import com.example.manage.bean.MainSceneBean;
import com.example.manage.bean.StatisticBean;
import com.example.manage.bean.TrushLastestBean;
import com.example.manage.utils.BarChartUtils;
import com.example.manage.utils.PieChartUtil;
import com.example.manage.utils.ToastUtils;
import com.example.manage.view.CircularProgressView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.zhouyou.http.EasyHttp;
import com.zhouyou.http.callback.SimpleCallBack;
import com.zhouyou.http.exception.ApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页，包括里面的两个抽屉
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout draw_layout;
    private LinearLayout ll_visible, ll_cut, ll_setting;//隐藏，切换，设置
    private SlidingDrawer slidingdrawer;
    private LinearLayout ll_into,ll_title,ll_search;
    private LinearLayout headerMesBottom, headerMesTop;
    private CircularProgressView progress_rub_one, progress_rub_two; //第一个垃圾桶， 第二个
    private TextView tv_progress_two, tv_progress_one; //第一个垃圾桶数值， 第二个
    private TextView btn_ele, btn_war; //电能，水能
    private BarChart barChart; // 柱状图
    private PieChart pieChart;// 饼状图
    private RecyclerView recycle_scene, recycle_equ;
    private LinearLayout ll_net; //进入网络界面
    private ArrayList count = new ArrayList();
    private FlexboxLayoutManager flexboxLayoutManager;
    private FlexboxLayoutManager flexboxLayoutManagerEqu;
    private Button btn_login;
    private RelativeLayout rl_monitor;
    private MapView mapView = null;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;
    private TextView tvTotalOne,tvTotalTwo,tvTotalThr,tvTotalFour,tvTotalFive,tvTotalSix;
    private TextView tv_rainvalue_bot,tv_pm_bot,tv_humidity_bot,tv_windirection_bot,tv_tem_bot,tv_tem_title_bot;
    private TextView tv_rainvalue_top,tv_pm_top,tv_humidity_top,tv_windirection_top,tv_tem_top,tv_tem_title_top;
    private TextView tv_flow_total,tv_flow_total_bot,tv_online,tv_stack,tv_wifi_total,tv_wifi_online,tv_wifi_unline;

    private boolean isFirstLoc = true; //第一次定位
    private int IsVisible = 1; //1为显示 2 为隐藏

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListen();
        initMap();
    }


    private void initView() {
        draw_layout = findViewById(R.id.draw_layout);
        ll_visible = findViewById(R.id.ll_visible);
        ll_cut = findViewById(R.id.ll_cut);
        ll_setting = findViewById(R.id.ll_setting);
        slidingdrawer = findViewById(R.id.slidingdrawer);
        headerMesBottom = findViewById(R.id.headerMesBottom);
        headerMesTop = findViewById(R.id.headerMesTop);
        ll_into = findViewById(R.id.ll_into);
        progress_rub_one = findViewById(R.id.progress_rub_one);
        progress_rub_two = findViewById(R.id.progress_rub_two);
        tv_progress_one = findViewById(R.id.tv_progress_one);
        tv_progress_two = findViewById(R.id.tv_progress_two);
        btn_ele = findViewById(R.id.btn_ele);
        btn_war = findViewById(R.id.btn_war);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        recycle_scene = findViewById(R.id.recycle_scene);
        recycle_equ = findViewById(R.id.recycle_equ);
        ll_net = findViewById(R.id.ll_net);
        btn_login = findViewById(R.id.btn_login);
        rl_monitor = findViewById(R.id.rl_monitor);
        mapView = findViewById(R.id.mapView);
        ll_title = findViewById(R.id.ll_title);
        ll_search = findViewById(R.id.ll_search);

        tvTotalOne = findViewById(R.id.tvTotalOne);
        tvTotalTwo = findViewById(R.id.tvTotalTwo);
        tvTotalThr = findViewById(R.id.tvTotalThr);
        tvTotalFour = findViewById(R.id.tvTotalFour);
        tvTotalFive = findViewById(R.id.tvTotalFive);
        tvTotalSix = findViewById(R.id.tvTotalSix);

        tv_rainvalue_bot = findViewById(R.id.tv_rainvalue_bot);
        tv_pm_bot = findViewById(R.id.tv_pm_bot);
        tv_humidity_bot = findViewById(R.id.tv_humidity_bot);
        tv_windirection_bot = findViewById(R.id.tv_windirection_bot);
        tv_tem_bot = findViewById(R.id.tv_tem_bot);
        tv_rainvalue_top = findViewById(R.id.tv_rainvalue_top);
        tv_pm_top = findViewById(R.id.tv_pm_top);
        tv_humidity_top = findViewById(R.id.tv_humidity_top);
        tv_windirection_top = findViewById(R.id.tv_windirection_top);
        tv_tem_top = findViewById(R.id.tv_tem_top);
        tv_tem_title_top = findViewById(R.id.tv_tem_title_top);
        tv_tem_title_bot = findViewById(R.id.tv_tem_title_bot);

        tv_flow_total = findViewById(R.id.tv_flow_total);
        tv_flow_total_bot = findViewById(R.id.tv_flow_total_bot);
        tv_stack = findViewById(R.id.tv_stack);
        tv_online = findViewById(R.id.tv_online);

        tv_wifi_total = findViewById(R.id.tv_wifi_total);
        tv_wifi_online = findViewById(R.id.tv_wifi_online);
        tv_wifi_unline = findViewById(R.id.tv_wifi_unline);

        ll_visible.setOnClickListener(this);
        btn_ele.setOnClickListener(this);
        btn_war.setOnClickListener(this);
        ll_cut.setOnClickListener(this);
        ll_setting.setOnClickListener(this);
        ll_net.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        rl_monitor.setOnClickListener(this);
        ll_search.setOnClickListener(this);
    }


    private void initListen() {

        //设置drawLayout的宽度
        WindowManager wm = getWindowManager();//获取屏幕宽高
        int width = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams layoutParams = ll_into.getLayoutParams();
        layoutParams.width = width * 3 / 4;
        ll_into.setLayoutParams(layoutParams);


        draw_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);


        slidingdrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                headerMesTop.setVisibility(View.GONE);
                headerMesBottom.setVisibility(View.VISIBLE);
            }
        });
        slidingdrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                headerMesTop.setVisibility(View.VISIBLE);
                headerMesBottom.setVisibility(View.GONE);
            }
        });



        initBarChartData(2);
        initPieChartData(); //饼状图数据获取
        getRubash();//获取垃圾桶信息
        Environment();//获取环境信息
        ClientNums();//获取wifi累计人数
        ApStatus();//获取wifi设备
        sceneRecycle();
    }




    private void initMap() {
        //设置定位图标
//        MyLocationConfiguration myLocationConfiguration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null, 00000000, 00000000);
        // 不显示百度地图Logo
        mapView.removeViewAt(1);
        mBaiduMap = mapView.getMap();

        mBaiduMap.setMyLocationEnabled(true);
//        mBaiduMap.setMyLocationConfiguration(myLocationConfiguration);
        //定位初始化
        mLocationClient = new LocationClient(this);


        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);


        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        mLocationClient.registerLocationListener(mListener);
        //开启地图定位图层
        mLocationClient.start();

        setMaker();
    }


    private List<LatLng> latLngList = new ArrayList<>();
    private List<OverlayOptions> optionsList = new ArrayList<>();

    private void setMaker() {
        mBaiduMap.clear();
        //定义Maker坐标点
        LatLng point = new LatLng(34.8972, 113.9117);
        latLngList.add(new LatLng(34, 113));
        latLngList.add(new LatLng(35, 113));
        latLngList.add(new LatLng(36, 113));
        latLngList.add(new LatLng(37, 113));
        latLngList.add(new LatLng(38, 113));
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_safe_type_one);
        //构建MarkerOption，用于在地图上添加Marker
        for (int i = 0; i < 5; i++) {
            Bundle mBundle = new Bundle();
            mBundle.putString("title", "第" + i + "个marker");
            mBundle.putDouble("lat", latLngList.get(i).latitude);
            mBundle.putDouble("lng", latLngList.get(i).longitude);

            OverlayOptions option = new MarkerOptions()
                    .extraInfo(mBundle)
        //                    .title("lat="+latLngList.get(i).latitude+"---lng="+latLngList.get(i).longitude)
                    .position(latLngList.get(i))
                    .icon(bitmap);
            optionsList.add(option);
        }

        //在地图上添加Marker，并显示
        //        mBaiduMap.addOverlay(option);
        mBaiduMap.addOverlays(optionsList);
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Bundle extraInfo = marker.getExtraInfo();
                String title = extraInfo.getString("title");
                double lat = extraInfo.getDouble("lat");
                double lng = extraInfo.getDouble("lng");
                Toast.makeText(MainActivity.this, title + " ---- " + lat + "----" + lng, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_cut:
                draw_layout.openDrawer(GravityCompat.END);
                break;
            case R.id.btn_war: //水量
                btn_war.setBackgroundColor(getResources().getColor(R.color.light_blue));
                btn_war.setTextColor(getResources().getColor(R.color.white));
                btn_ele.setBackgroundColor(getResources().getColor(R.color.white));
                btn_ele.setTextColor(getResources().getColor(R.color.black));
                initBarChartData(1);
                break;
            case R.id.btn_ele: //电量
                btn_ele.setBackgroundColor(getResources().getColor(R.color.light_blue));
                btn_ele.setTextColor(getResources().getColor(R.color.white));
                btn_war.setBackgroundColor(getResources().getColor(R.color.white));
                btn_war.setTextColor(getResources().getColor(R.color.black));
                initBarChartData(2);
                break;
            case R.id.ll_visible: //隐藏
                if (IsVisible == 1) {
                    slidingdrawer.setVisibility(View.GONE);
                    ll_cut.setVisibility(View.GONE);
                    ll_setting.setVisibility(View.GONE);
                    ll_title.setVisibility(View.GONE);
                    ll_search.setVisibility(View.GONE);
                    IsVisible = 2;
                } else if (IsVisible == 2) {
                    slidingdrawer.setVisibility(View.VISIBLE);
                    ll_cut.setVisibility(View.VISIBLE);
                    ll_setting.setVisibility(View.VISIBLE);
                    ll_title.setVisibility(View.VISIBLE);
                    ll_search.setVisibility(View.VISIBLE);

                    IsVisible = 1;
                }
                break;
            case R.id.ll_net: //进入网络界面
                Intent intent = new Intent(MainActivity.this, NetActivity.class);
                Log.e("fhxx","发送前--->"+data1.toString());
                intent.putExtra("apStatusBean", apStatusBean);
                startActivity(intent);
                break;
            case R.id.btn_login:
//                startActivity(new Intent(MainActivity.this, BigScreenActivity.class)); //大屏控制
                startActivity(new Intent(MainActivity.this, LightingActivity.class));//灯光控制
//                startActivity(new Intent(MainActivity.this, SenFogActivity.class));//森雾控制
//                startActivity(new Intent(MainActivity.this, EquipmentListActivity.class));//设备列表
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            case R.id.rl_monitor:
                startActivity(new Intent(MainActivity.this, MonitorListActivity.class));
                break;
            case R.id.ll_setting:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.ll_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
//                Toast.makeText(this, "搜素", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /**
     * 柱状图数据添加
     *
     * @param type
     */
    private void initBarChartData(int type) {

        ArrayList<BarEntry> yValues = new ArrayList<>();
        if (type == 1) {
            for (int x = 0; x < 30; x++) {
                // 2.0 ----xValues.add(String.valueOf(i));
                float y = (float) (Math.random() * 500);
                yValues.add(new BarEntry(x, y));
            }
        } else if (type == 2) {
            for (int x = 0; x < 15; x++) {
                // 2.0 ----xValues.add(String.valueOf(i));
                float y = (float) (Math.random() * 500);
                yValues.add(new BarEntry(x, y));
            }
        }
        BarChartUtils.getBarChart().setBarChart(barChart, type, yValues, this);
    }

    /**
     * 饼状图数据添加
     */
    int total;
    StatisticBean.DataBean data;
    private void initPieChartData() {
        EasyHttp.get(AppUrl.PoiStatistic)
                .syncRequest(false)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }
                    @Override
                    public void onSuccess(String s) {
                        StatisticBean statisticBean = JSON.parseObject(s, StatisticBean.class);

                        if (statisticBean.isStatus()){ //判断是否成功
                            data = statisticBean.getData();
                            count.clear();
                            total=0;
                            for (int i = 0; i < data.getStatis().size(); i++) {
                                count.add(data.getStatis().get(i).getNum());
                                total +=data.getStatis().get(i).getNum();
                            }
                            tvTotalOne.setText(data.getStatis().get(0).getNum()+"");
                            tvTotalTwo.setText(data.getStatis().get(1).getNum()+"");
                            tvTotalThr.setText(data.getStatis().get(2).getNum()+"");
                            tvTotalFour.setText(data.getStatis().get(3).getNum()+"");
                            tvTotalFive.setText(data.getStatis().get(4).getNum()+"");
                            tvTotalSix.setText(data.getStatis().get(5).getNum()+"");
                            PieChartUtil.getPitChart().setPieChart(pieChart, (ArrayList) count, "周边概况", false, MainActivity.this, total);
                        }

                    }
                });

    }

    /**
     * 垃圾桶信息获取
     */
    private void getRubash(){
        EasyHttp.get(AppUrl.TrushLatestData)
                .syncRequest(false)
                .timeStamp(true)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }
                    @Override
                    public void onSuccess(String s) {
                        TrushLastestBean trushLastestBean = JSON.parseObject(s, TrushLastestBean.class);
                        if (trushLastestBean.isStatus()){
                            List<TrushLastestBean.DataBean.ListBean> list = trushLastestBean.getData().getList();
                            progress_rub_one.setProgress(list.get(0).getNowrecovery()*100);
                            tv_progress_one.setText(list.get(0).getNowrecovery()*100 + "%");
                            progress_rub_two.setProgress(list.get(1).getNowrecovery()*100);
                            tv_progress_two.setText(list.get(1).getNowrecovery()*100 + "%");
                        }
                    }
                });
    }

    /**
     * 获取实时环境信息
     */
    private void Environment(){
        EasyHttp.get(AppUrl.RealEnvironment)
                .syncRequest(false)
                .timeStamp(true)
                .execute(new SimpleCallBack<String >() {
                    @Override
                    public void onError(ApiException e) {
                    }
                    @Override
                    public void onSuccess(String s) {
                        EnvironmentBean environmentBean = JSON.parseObject(s, EnvironmentBean.class);

                        if (environmentBean.isStatus()){
                            EnvironmentBean.DataBean.MonitorBean monitor = environmentBean.getData().getMonitor();
                            EnvironmentBean.DataBean.WeatherBean weather = environmentBean.getData().getWeather();
                            tv_tem_top.setText(weather.getTem()+"℃");
                            tv_tem_bot.setText(weather.getTem()+"℃");
                            tv_windirection_bot.setText(weather.getWin());
                            tv_windirection_top.setText(weather.getWin());
                            tv_humidity_top.setText(weather.getHumidity());
                            tv_humidity_bot.setText(weather.getHumidity());
                            tv_pm_top.setText(weather.getAir_pm25());
                            tv_pm_bot.setText(weather.getAir_pm25());
                            tv_rainvalue_top.setText(monitor.getRainvalue());
                            tv_rainvalue_bot.setText(monitor.getRainvalue());

                            tv_tem_title_top.setText(weather.getWea());
                            tv_tem_title_bot.setText(weather.getTem()+"℃");
                        }
                    }
                });
    }

    /**
     * 获取wifi在线人数和累计人数
     */
    private void ClientNums(){
        EasyHttp.get(AppUrl.Clientnums)
                .timeStamp(true)
                .syncRequest(false)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }
                    @Override
                    public void onSuccess(String s) {
                        ClientNumsBean clientNumsBean = JSON.parseObject(s, ClientNumsBean.class);
                        if (clientNumsBean.isStatus()){
                            tv_online.setText("在线人数:"+clientNumsBean.getData().getOnlineusernum()+"");
                            tv_stack.setText("累计人数:"+clientNumsBean.getData().getStackusernum()+"");
                        }
                    }
                });
    }

    /**
     * 获取无线网ap列表和状态
     */
    ApStatusBean apStatusBean;
    List<ApStatusBean.DataBeanX.DataBean> data1=new ArrayList<>();
    private void ApStatus(){
        EasyHttp.get(AppUrl.ApStatus)
                .timeStamp(true)
                .syncRequest(false)
                .execute(new SimpleCallBack<String>() {
                    @Override
                    public void onError(ApiException e) {
                    }

                    @Override
                    public void onSuccess(String s) {
                        apStatusBean= JSON.parseObject(s, ApStatusBean.class);

                        if (apStatusBean.isStatus()){
                            data1= apStatusBean.getData().getData();
                            ApStatusBean.DataBeanX data = apStatusBean.getData();
                            tv_wifi_total.setText(data.getTotal()+"");
                            tv_wifi_online.setText(data.getOnlineCount()+"");
                            tv_wifi_unline.setText(data.getOfflineCount()+"");
                        }
                    }
                });
    }


    private MainSecneAdapter secneAdapter;
    private List<MainSceneBean> mainSceneBeanList = new ArrayList<>();
    private MainEquAdapter equAdapter;
    private List<MainEquBean> equBeansList = new ArrayList<>();

    private void sceneRecycle() {
        mainSceneBeanList.add(new MainSceneBean("两个", 2));
//        for (int i = 0; i < 8; i++) {
//            mainSceneBeanList.add(new MainSceneBean("英协花" + i, 2));
//        }
        mainSceneBeanList.add(new MainSceneBean("三个字", 2));
        mainSceneBeanList.add(new MainSceneBean("我猜就是看了觉得", 2));
        mainSceneBeanList.add(new MainSceneBean("你得四字", 2));
        mainSceneBeanList.add(new MainSceneBean("两个2", 2));
        mainSceneBeanList.add(new MainSceneBean("你得五个字", 2));


        equBeansList.add(new MainEquBean("设备", 2));
        equBeansList.add(new MainEquBean("设备四字", 2));
        for (int i = 0; i < 8; i++) {
            equBeansList.add(new MainEquBean("设备" + i, 2));
        }
        equBeansList.add(new MainEquBean("设备五个字", 2));
        equBeansList.add(new MainEquBean("设备五个字", 2));
        equBeansList.add(new MainEquBean("设备六六个字", 2));

        mainSceneBeanList.get(0).setIsTrue(1);
        equBeansList.get(0).setIsChoose(1);

        secneAdapter = new MainSecneAdapter(R.layout.item_scene, mainSceneBeanList, this);
        flexboxLayoutManager = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManager.setAlignItems(AlignItems.STRETCH);

        recycle_scene.setLayoutManager(flexboxLayoutManager);

        recycle_scene.setAdapter(secneAdapter);
        secneAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tvMessage:
                        Toast.makeText(MainActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();

                        for (int i = 0; i < mainSceneBeanList.size(); i++) {
                            mainSceneBeanList.get(i).setIsTrue(2);
                        }
                        mainSceneBeanList.get(position).setIsTrue(1);
                        secneAdapter.notifyDataSetChanged();
                        draw_layout.closeDrawer(GravityCompat.END);
                        break;
                }

            }
        });
        flexboxLayoutManagerEqu = new FlexboxLayoutManager(this);
        //设置主轴排列方式
        flexboxLayoutManagerEqu.setFlexDirection(FlexDirection.ROW);
        //设置是否换行
        flexboxLayoutManagerEqu.setFlexWrap(FlexWrap.WRAP);
        flexboxLayoutManagerEqu.setAlignItems(AlignItems.STRETCH);
        equAdapter = new MainEquAdapter(R.layout.item_scene, equBeansList, this);
        recycle_equ.setLayoutManager(flexboxLayoutManagerEqu);
        recycle_equ.setAdapter(equAdapter);
        equAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tvMessage:
                        Toast.makeText(MainActivity.this, "点击了" + position, Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < equBeansList.size(); i++) {
                            equBeansList.get(i).setIsChoose(2);
                        }
                        equBeansList.get(position).setIsChoose(1);
                        equAdapter.notifyDataSetChanged();
                        draw_layout.closeDrawer(GravityCompat.END);
                        break;
                }
            }
        });

    }

    private static final int TIME_EXIT = 2000;
    private long mBackPressed;

    @Override
    public void onBackPressed() {
        if (slidingdrawer.isOpened()) {
            slidingdrawer.animateClose();
        } else if (draw_layout.isDrawerOpen(GravityCompat.END)) {
            draw_layout.closeDrawer(GravityCompat.END);
        } else {
            if (mBackPressed + TIME_EXIT > System.currentTimeMillis()) {
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(this, "再点击一次返回退出程序", Toast.LENGTH_SHORT).show();
                mBackPressed = System.currentTimeMillis();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }


    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(19.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mapView = null;
    }
}
