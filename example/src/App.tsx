import { View, StyleSheet, Text } from 'react-native';
//import { FaceAuth } from 'rn-face-auth';

export default function App() {
  return (
    <View style={styles.container}>
      <Text style={styles.text}>Hello world</Text>
      {/* <RnFaceAuthView color="#32a852" style={styles.box} /> */}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    //backgroundColor: 'black',
  },
  text: {
    fontSize: 20,
    color: '#0a7ea4',
    fontWeight: 'bold',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
