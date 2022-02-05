import 'package:flutter/material.dart';
import 'package:plugin_dev_example/BasicMessageChannelPage.dart';
import 'package:plugin_dev_example/EventChannelPage.dart';
import 'package:plugin_dev_example/MethodChannelPage.dart';

main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: HomePage(),
    );
  }
}

class HomePage extends StatelessWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Flutter与原生通信'),
      ),
      body: Center(
        child: Column(
          children: [
            TextButton(
              onPressed: ()=>Navigator.of(context).push(
                  MaterialPageRoute(builder: (_)=>const MethodChannelPage())
              ),
              child:const Text('MethodChannel'),
            ),
            TextButton(
              onPressed: ()=>Navigator.of(context).push(
                  MaterialPageRoute(builder: (_)=>const EventChannelPage())
              ),
              child:const Text('EventChannel'),
            ),
            TextButton(
              onPressed: ()=>Navigator.of(context).push(
                  MaterialPageRoute(builder: (_)=>const BasicMessageChannelPage())
              ),
              child:const Text('BasicMessageChannel'),
            )
          ],
        ),
      ),

    );
  }
}

