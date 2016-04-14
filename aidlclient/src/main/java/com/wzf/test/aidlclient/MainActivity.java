package com.wzf.test.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wzf.test.aidldemo.IIotekAidl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText et_num1;
    private EditText et_num2;
    private TextView tv_sum;
    private IIotekAidl aidl;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 获取最重要的 服务端 aidl 对象
            aidl = IIotekAidl.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // 施放资源
            aidl = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent();
        // 注意类名必须是全类名
        ComponentName componentName = new ComponentName("com.wzf.test.aidldemo","com.wzf.test.aidldemo.NumAddService");
        intent.setComponent(componentName);
        boolean success = bindService(intent,conn, Context.BIND_AUTO_CREATE);
        Toast.makeText(getApplicationContext(), "服务绑定" + success, Toast.LENGTH_LONG).show();
    }

    private void initView() {
        et_num1 = (EditText) findViewById(R.id.et_num1);
        et_num2 = (EditText) findViewById(R.id.et_num2);
        tv_sum = (TextView) findViewById(R.id.tv_sum);
        findViewById(R.id.btn_add).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public void onClick(View v) {
        if(aidl != null){
            try {
                int num1 = Integer.valueOf(et_num1.getText().toString());
                int num2 = Integer.valueOf(et_num2.getText().toString());
                int sum = aidl.add(num1,num2);
                tv_sum.setText("结果：" + sum);
            } catch (RemoteException e) {
                e.printStackTrace();
                tv_sum.setText("远程计算错误");
            } catch (Exception e) {
                e.printStackTrace();
                tv_sum.setText("输入错误");
            }finally {
            }
        }else {
            tv_sum.setText("绑定服务失败");
        }
    }
}
