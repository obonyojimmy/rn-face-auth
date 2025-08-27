import { useEffect, useState } from 'react';
import { Platform, PermissionsAndroid, View, StyleSheet } from 'react-native';
import { FaceAuth } from 'rn-face-auth';

export default function App() {
  const [granted, setGranted] = useState(false);

  useEffect(() => {
    if (Platform.OS === 'android') {
      PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.CAMERA).then(
        (res) => {
          if (res === PermissionsAndroid.RESULTS.GRANTED) {
            setGranted(true);
          }
        }
      );
    } else {
      setGranted(true);
    }
  }, []);
  return (
    <View style={styles.container}>
      {granted && <FaceAuth style={styles.box} />}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 400,
    height: 600,
    marginVertical: 20,
  },
});
