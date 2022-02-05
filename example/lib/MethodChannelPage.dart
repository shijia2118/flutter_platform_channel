import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MethodChannelPage extends StatefulWidget {
  const MethodChannelPage({Key? key}) : super(key: key);

  @override
  _MethodChannelPageState createState() => _MethodChannelPageState();
}

class _MethodChannelPageState extends State<MethodChannelPage> {
  //定义一个MethodChannel通道,同时约定通道名称method_channel.
  static const MethodChannel _channel = MethodChannel('method_channel');

  String str = '未知';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('MethodChannel'),
      ),
      body: Container(
        padding: const EdgeInsets.symmetric(horizontal: 15,vertical: 30),
        child: Column(
          children: [
            OutlinedButton(onPressed: getTextFromPlatform, child:const Text('MethodChannel通信')),
            const Text('点击按钮,调用MethodChannel方法,获得从原生端返回的字符串'),
            const SizedBox(height: 20),
            Row(children: [
              const Text('当前电池电量:'),
              Text(str),
            ],)
          ],
        ),
      ),
    );
  }

  Future<void> getTextFromPlatform()async{
    String? result = await _channel.invokeMethod('method_channel_test');
    if(result != null){
      setState(() {
        str = result + '%';
      });
    }
  }
}
