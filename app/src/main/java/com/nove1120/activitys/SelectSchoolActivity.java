package com.nove1120.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.nove1120.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SelectSchoolActivity extends Activity {
    private ImageView returnButton;

    private Spinner spinner_sheng;//省
    private Spinner spinner_shi;//市
    private Spinner spinner_school;//学校


    private Button btn_confirm;//确认按钮
    private int resultCode = 103;//返回码

    //全局的jsonObject
    private JSONObject jsonObject;//把全国的省市学校的信息以json的格式保存，解析完成后赋值为null
    private String[] allProv;//所有的省

    private ArrayAdapter<String> provinceAdapter;//省份数据适配器
    private ArrayAdapter<String> cityAdapter;//城市数据适配器
    private ArrayAdapter<String> schoolAdapter;//区县数据适配器

    private String[] allSpinList;//在spinner中选出来的地址，后面需要用空格隔开省市区

    private String address;//用来接收intent的参数



    private String provinceName;//省的名字
    private String schoolName;//区的名字
    private Boolean isFirstLoad = true;//判断是不是最近进入对话框

    //省市区的集合
    private Map<String, String[]> cityMap = new HashMap<String, String[]>();//key:省p---value:市n  value是一个集合
    private Map<String, String[]> areaMap = new HashMap<String, String[]>();//key:市n---value:区s    区也是一个集合



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_school);
        initJsonData();//初始化json数据
        initDatas();//初始化省市区数据
        initView();//初始化控件
        initClick();//初始化点击
        setSpinnerData();//为spinner设置值
    }

    /**
     * spinner设置值（默认设置值）
     */
    private void setSpinnerData() {
        int selectPosition = 0;//有数据传入时
        address = getIntent().getStringExtra("school");
        if (address != null && !address.equals("") ) {
            allSpinList = address.split(" ");//用空格隔开allSpinList地址
        }
        /**
         * 设置省市区的适配器，进行动态设置
         */
        provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);//系统默认的
        for (int i = 0; i < allProv.length; i++) {
            //给spinner省赋值,设置默认值
            if (address != null && !address.equals("") &&  allSpinList.length > 0 && allSpinList[0].equals(allProv[i])) {
                selectPosition = i;
            }
            provinceAdapter.add(allProv[i]);//添加每一个省
        }
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//按下的效果
        spinner_sheng.setAdapter(provinceAdapter);
        spinner_sheng.setSelection(selectPosition);//设置选中的省，默认

        //市
        cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_shi.setAdapter(cityAdapter);

        //学校
        schoolAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        schoolAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_school.setAdapter(schoolAdapter);

        setListener();//设置spinner的点击监听

    }

    //设置spinner的点击监听
    private void setListener() {
        //省
        spinner_sheng.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                provinceName = parent.getSelectedItem() + "";//获取点击列表spinner item的省名字
                if (isFirstLoad) {
                    // 判断是否省市区都存在
                    if (address != null && !address.equals("") && allSpinList.length > 1 && allSpinList.length < 3) {
                        updateCityAndArea(provinceName, allSpinList[1], null);//更新市和区
                    } else if (address != null && !address.equals("")  && allSpinList.length >= 3) {
                        //存在省市区
                        //去更新
                        updateCityAndArea(provinceName, allSpinList[1], allSpinList[2]);
                    } else {
                        updateCityAndArea(provinceName, null, null);
                    }
                } else {
                    updateCityAndArea(provinceName, null, null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //市
        spinner_shi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //更新区
                updateArea(parent.getSelectedItem() + "", null);
                isFirstLoad = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //区
        spinner_school.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //直接获取学校的名字
                schoolName = parent.getSelectedItem() + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * 根据当前的省，更新市和学校的信息
     */
    private void updateCityAndArea(Object object, Object city, Object area) {
        int selectPosition = 0;//有数据时，进行匹配城市，默认选中
        String[] cities = cityMap.get(object);
        cityAdapter.clear();//清空adapter的数据
        for (int i = 0; i < cities.length; i++) {
            if (city != null && city.toString().equals(cities[i])) {//判断传入的市在集合中匹配
                selectPosition = i;
            }
            cityAdapter.add(cities[i]);//将这个列表“市”添加到adapter中
        }
        cityAdapter.notifyDataSetChanged();//刷新
        if (city == null) {
            updateArea(cities[0], null);//更新区,没有市则默认第一个给它
        } else {
            spinner_shi.setSelection(selectPosition);
            updateArea(city, area);//穿入的区去集合中匹配
        }

    }

    //根据当前的市，更新学校的信息
    private void updateArea(Object object, Object myArea) {
        boolean isArea = false;//判断第三个地址是地区还是详细地址
        int selectPosition = 0;//当有数据时，进行匹配地区，默认选中
        String[] area = areaMap.get(object);
        schoolAdapter.clear();//清空
        if (area != null) {
            for (int i = 0; i < area.length; i++) {
                if (myArea != null && myArea.toString().equals(area[i])) {//去集合中匹配
                    selectPosition = i;
                    isArea = true;//地区
                }
                schoolAdapter.add(area[i]);//填入到这个列表

            }
            schoolAdapter.notifyDataSetChanged();//刷新
            spinner_school.setSelection(selectPosition);//默认选中
        }
    }

    //初始化省市学校数据
    private void initDatas() {
        try {
            JSONArray jsonArray = jsonObject.getJSONArray("schoolList");//获取整个json数据
            allProv = new String[jsonArray.length()];//封装数据
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonP = jsonArray.getJSONObject(i);//jsonArray转jsonObject
                String provStr = jsonP.getString("p");//获取所有的省
                allProv[i] = provStr;//封装所有的省
                JSONArray jsonCity = null;

                try {
                    jsonCity = jsonP.getJSONArray("c");//在所有的省中取出所有的市，转jsonArray
                } catch (Exception e) {
                    continue;
                }
                //所有的市
                String[] allCity = new String[jsonCity.length()];//所有市的长度
                for (int c = 0; c < jsonCity.length(); c++) {
                    JSONObject jsonCy = jsonCity.getJSONObject(c);//转jsonObject
                    String cityStr = jsonCy.getString("n");//取出所有的市
                    allCity[c] = cityStr;//封装市集合

                    JSONArray jsonArea = null;
                    try {
                        jsonArea = jsonCy.getJSONArray("u");//在从所有的市里面取出所有的学习校,转jsonArray
                    } catch (Exception e) {
                        continue;
                    }
                    String[] allArea = new String[jsonArea.length()];//所有的学校
                    for (int a = 0; a < jsonArea.length(); a++) {
                        JSONObject jsonAa = jsonArea.getJSONObject(a);
                        String areaStr = jsonAa.getString("s");//获取所有的学习
                        allArea[a] = areaStr;//封装起来
                    }

                    areaMap.put(cityStr, allArea);//某个市取出所有的区集合


                }
                cityMap.put(provStr, allCity);//某个省取出所有的市,
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jsonObject = null;//清空所有的数据
    }

    /**
     * 从assert文件夹中获取json数据
     */
    private void initJsonData() {
        try {
            StringBuffer sb = new StringBuffer();
            InputStream is = getAssets().open("school.json");//打开json数据
            byte[] by = new byte[is.available()];//转字节
            int len = -1;
            while ((len = is.read(by)) != -1) {
                sb.append(new String(by, 0, len, "gb2312"));//根据字节长度设置编码
            }
            is.close();//关闭流
            jsonObject = new JSONObject(sb.toString());//为json赋值
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化点击
    private void initClick() {
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把值返回给MainActivity
                Intent intent = new Intent();
                schoolName = spinner_school.getSelectedItem() == null ? "" : spinner_school.getSelectedItem().toString();
                Toast.makeText(SelectSchoolActivity.this,spinner_shi.getSelectedItem().toString(),Toast.LENGTH_SHORT).show();
                intent.putExtra("school", spinner_sheng.getSelectedItem() + " " + spinner_shi.getSelectedItem() + " " + schoolName);
                setResult(resultCode, intent);
                finish();
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //初始化控件
    private void initView() {
        spinner_sheng = (Spinner) findViewById(R.id.spinner_sheng);
        spinner_shi = (Spinner) findViewById(R.id.spinner_shi);
        spinner_school = (Spinner) findViewById(R.id.spinner_school);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);
        returnButton = (ImageView) findViewById(R.id.returnButton);
    }
}
