package com.example.plugin_dev;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.StandardMessageCodec;

public class PluginDevPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {
    private MethodChannel channel;
    private EventChannel.EventSink eventSink;
    private Activity mActivity;
    private final int seconds = 10*1000;
    private BasicMessageChannel<Object> basicMessageChannel;

    private  CountDownTimer cdTimer;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
        //实例化MethodChannel,并且实现MethodCallHandler.注意:通道名method_channel必须与flutter端一致
        channel = new MethodChannel(binding.getBinaryMessenger(), "method_channel");
        channel.setMethodCallHandler(this); //与MethodCallHandler绑定

        //实例化eventChannel,并且实现StreamHandler.注意:通道名event_channel必须与flutter端一致.
        EventChannel eventChannel = new EventChannel(binding.getBinaryMessenger(), "event_channel");
        eventChannel.setStreamHandler(streamHandler);

        //实例化basicMessageChannel,并且实现MessageHandler
        basicMessageChannel = new BasicMessageChannel<Object>(binding.getBinaryMessenger(),"basic_message_channel", StandardMessageCodec.INSTANCE);
        basicMessageChannel.setMessageHandler(new BasicMessageChannel.MessageHandler<Object>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onMessage(@Nullable Object message, @NonNull BasicMessageChannel.Reply<Object> reply) {
                if(message == null) return;
                if(Objects.equals(((Map<?, ?>) message).get("name"), "test1")){
                    Toast.makeText(mActivity, "原生收到Flutter发送的消息:"+message, Toast.LENGTH_SHORT).show();
                    reply.reply("Android reply!");
                }else if(Objects.equals(((Map<?, ?>) message).get("name"), "test2")){
                    Toast.makeText(mActivity, "原生收到Flutter发送的消息:"+message, Toast.LENGTH_SHORT).show();
                }else if(Objects.equals(((Map<?, ?>) message).get("name"), "test3")){
                    basicMessageChannel.send("Android send to Flutter!");
                }
            }
        });
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null); //与MethodCallHandler解绑
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity(); //在这里获得activity
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivity() {
        mActivity = null;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        //在这里获取从flutter端传过来的参数,并把android端的结果返回给flutter
        if (call.method.equals("method_channel_test")) { //与flutter端定义的方法名必须一致
            result.success(""+getBatteryLevel());
        } else {
            result.notImplemented();
        }
    }

    /**
     * 获取电源电量
     * @return int
     */
    private int getBatteryLevel() {
        int batteryLevel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager batteryManager = (BatteryManager) mActivity.getSystemService(Context.BATTERY_SERVICE);
            batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        } else {
            Intent intent = new ContextWrapper(mActivity.getApplicationContext()).registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 / intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        }
        return batteryLevel;
    }

    /**
     * 设置StreamHandler.注意:它需要重写2个方法:onListen,onCancel.
     */
    private final EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler() {
        @Override
        public void onListen(Object arguments, EventChannel.EventSink events) {
            eventSink = events;
            cdTimer = new CountDownTimer(seconds,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    eventSink.success(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    eventSink.endOfStream();
                }
            } ;
            cdTimer.start();
        }

        @Override
        public void onCancel(Object arguments) {
            eventSink = null;
            cdTimer.cancel();
        }

    };
}
