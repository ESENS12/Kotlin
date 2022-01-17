import 'dart:io';

import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

Future<String> callPermissions() async {
  Map<Permission, PermissionStatus> statuses = await [
    Permission.location,
    Permission.storage,
  ].request();

  if (statuses.values.every((element) => element.isGranted)) {
    return 'success';
  }

  return 'failed';
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: Text('Permission'),
        ),
        body: Center(
          child: FutureBuilder(
            future: callPermissions(),
            builder:
                (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
              if (snapshot.hasData) {
                return Text(snapshot.data);
              }

              return CircularProgressIndicator();
            },
          ),
        ),
      ),
    );
  }
}