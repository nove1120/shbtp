package com.nove1120.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nove1120.R;
import com.nove1120.pojo.UserAddress;

import java.util.List;

public class AddressAdapter extends ArrayAdapter {
    private final int resourceId;

    public AddressAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UserAddress userAddress = (UserAddress) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        TextView consigneeText = (TextView) view.findViewById(R.id.consigneeText);
        TextView phoneNumberText = (TextView) view.findViewById(R.id.phoneNumberText);
        TextView addressText = (TextView) view.findViewById(R.id.addressText);
        ImageView defaultAddressImage = (ImageView) view.findViewById(R.id.defaultAddressImage);

        consigneeText.setText("收货人:" +userAddress.getConsignee());
        phoneNumberText.setText(userAddress.getPhone_number());
        addressText.setText("收货地址:"+userAddress.getLocation()+" "+userAddress.getAddress());
        if(userAddress.getDefault_flag()==1){
            defaultAddressImage.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
