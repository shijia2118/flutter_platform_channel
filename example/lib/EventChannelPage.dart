import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class EventChannelPage extends StatefulWidget {
  const EventChannelPage({Key? key}) : super(key: key);

  @override
  _EventChannelPageState createState() => _EventChannelPageState();
}

class _EventChannelPageState extends State<EventChannelPage> {

  StreamSubscription? _streamSubscription;
  String _platformMessage = '未收到消息';
  final EventChannel _channel = const EventChannel('event_channel');

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _enableEventReceiver();
  }


  void _enableEventReceiver() {
    _streamSubscription = _channel.receiveBroadcastStream().listen(
            (dynamic event) {
              num s = 0;
              if(event is num) s = event;
          setState(() {
            _platformMessage = (s/1000).ceil().toString()+'s';
          });
        },
        onError: (dynamic error) {
          setState(() {
            _platformMessage = error.message;
          });
        },
        cancelOnError: true,
      onDone: (){
              setState(() {
                _platformMessage = '完成了';
              });
      }
    );
  }

  @override
  void dispose() {
    // TODO: implement dispose
    super.dispose();
    _streamSubscription?.cancel();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('EventChannel')),
      body: Container(
        margin:const EdgeInsets.only(top: 30,left: 15),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('原生返回的字符串:'),
            Text(_platformMessage),
          ],
        ),
      ),
    );
  }
}
