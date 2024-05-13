package com.example.mydemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.example.mydemo.databinding.ActivityMainBinding;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * 几种子线程更新ui的方法
 * 参考：https://blog.csdn.net/ykmeory/article/details/133523643
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding.btnUpdate1.setOnClickListener(this);
        binding.btnUpdate2.setOnClickListener(this);
        binding.btnUpdate3.setOnClickListener(this);

        binding.btnUpdate1.setOnClickListener(view ->
        {
            new Thread(() -> {
                // 执行耗时操作
                final String result1 = "异步更新视图一";

                runOnUiThread(() -> {
                    // 更新UI
                    binding.tvContent.setText(result1);
                });
            }).start();
            //这个先执行，然后是runOnUiThread里面的
            binding.tvContent.setText("666666");
        });


        binding.tvContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler mainHandler = new Handler(Looper.getMainLooper());
                //new Handler()也可以，但是new Handler()已经被弃用；
//                final Handler mainHandler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 执行耗时操作
                        final String result2 = "异步更新二";

                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                // 更新UI
                                binding.tvContent.setText(result2);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_update1:
                btn_update1();
                break;
            case R.id.btn_update2:
                btn_update2();
                break;
            case R.id.btn_update3:
                btn_update3();
                break;
            default:
                break;
        }
    }

    private void btn_update1() {
        Looper.prepare();
        binding.tvContent.setText("异步更新视图一");
        Looper.loop();
    }

    private void btn_update2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvContent.setText("异步更新视图二");
                    }
                });
            }
        }).start();
    }

    private void btn_update3() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvContent.setText("异步更新视图三");
                    }
                });
            }
        }).start();
    }
}