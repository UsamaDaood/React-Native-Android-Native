import React from 'react';
import {StyleSheet, View, Button, NativeModules} from 'react-native';
import {check, request, PERMISSIONS, RESULTS} from 'react-native-permissions';

const {VideoCaptureModule} = NativeModules;

const App = () => {
  const handleCapture = async () => {
    const cameraPermission = await request(PERMISSIONS.ANDROID.CAMERA);
    const audioPermission = await request(PERMISSIONS.ANDROID.RECORD_AUDIO);

    if (
      cameraPermission === RESULTS.GRANTED &&
      audioPermission === RESULTS.GRANTED
    ) {
      VideoCaptureModule.captureVideo();
    } else {
      console.log('Permissions not granted');
    }
  };

  return (
    <View style={styles.container}>
      <Button title="Capture" onPress={handleCapture} />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#F5FCFF',
  },
});

export default App;
